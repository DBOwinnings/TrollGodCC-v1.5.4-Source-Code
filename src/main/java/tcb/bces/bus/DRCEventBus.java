package tcb.bces.bus;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import tcb.bces.BytecodeUtil;
import tcb.bces.InstrumentationClassLoader;
import tcb.bces.bus.compilation.CompilationNode;
import tcb.bces.bus.compilation.Dispatcher;
import tcb.bces.bus.compilation.DispatcherException;
import tcb.bces.event.Event;
import tcb.bces.event.EventCancellable;
import tcb.bces.event.IEvent;
import tcb.bces.event.IEventCancellable;
import tcb.bces.listener.IListener;
import tcb.bces.listener.Subscribe;
import tcb.bces.listener.SubscriptionException;
import tcb.bces.listener.filter.IFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

/**
 * This event bus should only be used for a low amount of listening methods. The 
 * maximum amount of possible listening methods is {@link DRCEventBus#MAX_METHODS}. 
 * Use {@link DRCExpander} for high amounts of listening methods.
 * <p>
 * This event bus supports listener priorities and an event filter.
 * The filter allows the user to filter and cancel any event before 
 * it's passed to its listener. The event itself will not be cancelled. 
 * Filters work on any type of listener or event, even
 * if the event is not a subclass of {@link EventCancellable}.
 * <p>
 * It is recommended to set a custom dispatcher to increase performance (for some reason
 * custom dispatchers seems to work a little bit faster). More information
 * about custom dispatchers at {@link Dispatcher}, {@link DRCEventBus#setDispatcher(Class)} and
 * {@link Dispatcher#dispatch()}
 * 
 * @author TCB
 *
 */
public class DRCEventBus implements IEventBus, ICopyable, ICompilableBus {
	/**
	 * The internal private dispatcher implementation
	 */
	private static final class DispatcherImpl extends Dispatcher {
		@Override
		public final <T extends Event> T dispatchEvent(T event) {
			return event;
		}

		@Override
		public void init(IListener[] listenerArray, IFilter[] filterArray) {
			this.listenerArray = listenerArray;
			this.filterArray = filterArray;
		}
	}

	/**
	 * Internal private data of a verified listening method
	 */
	private static final class InternalMethodContext {
		private final MethodContext context;
		private final IListener instance;
		private final Method method;
		private final String methodName;
		private final boolean forced;
		private final int priority;
		private final IFilter filter;
		private final Subscribe handlerAnnotation;
		private final Class<? extends Event> eventClass;
		public InternalMethodContext(MethodContext context, IListener instance, Method method, Subscribe handlerAnnotation, IFilter filter, Class<? extends Event> eventClass) {
			this.context = context;
			this.instance = instance;
			this.method = method;
			this.methodName = method.getName();
			this.handlerAnnotation = handlerAnnotation;
			this.forced = handlerAnnotation.forced();
			this.priority = handlerAnnotation.priority();
			this.filter = filter;
			this.eventClass = eventClass;
		}
	}

	/**
	 * The maximum amount of registered method contexts
	 */
	public static final int MAX_METHODS = 256;

	/**
	 * The method names used for reflection and bytecode construction
	 */
	private static final String DISPATCHER_DISPATCH_EVENT_INTERNAL = "dispatchEvent";
	private static final String DISPATCHER_LISTENER_ARRAY = "listenerArray";
	private static final String DISPATCHER_FILTER_ARRAY = "filterArray";
	private static final String IFILTER_FILTER = "filter";
	private static final String ILISTENER_IS_ENABLED = "isListening";
	private static final String IEVENTCANCELLABLE_IS_CANCELLED = "isCancelled";
	private static final String DISPATCHER_DISPATCH = "dispatch";

	/**
	 * Contains all registered listeners
	 */
	private final HashMap<Class<? extends Event>, List<InternalMethodContext>> registeredEntries = new HashMap<Class<? extends Event>, List<InternalMethodContext>>();

	/**
	 * Contains all registered listeners that accept subclasses of events (those listeners are also added to {@link DRCEventBus#registeredEntries})
	 */
	private final List<InternalMethodContext> subclassListeners = new ArrayList<InternalMethodContext>();

	/**
	 * Index lookup map, used for creating the bytecode
	 */
	private final HashMap<IListener, Integer> indexLookup = new HashMap<IListener, Integer>();

	/**
	 * Filter index lookup map, used for creating the bytecode
	 */
	private final HashMap<InternalMethodContext, Integer> filterIndexLookup = new HashMap<InternalMethodContext, Integer>();

	/**
	 * Contains all registered listeners
	 */
	private IListener[] listenerArray;

	/**
	 * Contains all filters
	 */
	private IFilter[] filterArray;

	/**
	 * The instance of the recompiled {@link DispatcherImpl}
	 */
	private Dispatcher dispatcherImplInstance = null;

	/**
	 * The dispatcher class
	 */
	private Class<? extends Dispatcher> dispatcherClass = DispatcherImpl.class;

	/**
	 * The amount of registered MethodEntries
	 */
	private int methodCount = 0;

	/**
	 * The compilation data
	 */
	private CompilationNode compilationNode = new CompilationNode("Compilation");

	/**
	 * The compiled dispatcher class bytes
	 */
	private byte[] dispatcherClassBytes;

	/**
	 * The default EventBus has a limit of {@link DRCEventBus#MAX_METHODS} listening methods. 
	 * If you want to add more listening methods use {@link DRCExpander} instead.
	 * More information at {@link DRCEventBus}.
	 */
	public DRCEventBus() { }

	/**
	 * Private constructor for copy method
	 */
	private DRCEventBus(Class<? extends Dispatcher> dispatcherClass, CompilationNode compilationNode) { 
		this.dispatcherClass = dispatcherClass;
		this.compilationNode = compilationNode;
	}

	/**
	 * Registers a list of {@link MethodContext} to the {@link DRCEventBus}. The event bus has to be updated with {@link DRCEventBus#bind()} for the new {@link IListener}s to take effect.
	 * <p>
	 * The default event bus has a limit of {@link DRCEventBus#MAX_METHODS} method contexts. If more than {@link DRCEventBus#MAX_METHODS} method contexts are registered an {@link IndexOutOfBoundsException} is thrown.
	 * @param context {@link List} of {@link MethodContext} to register
	 * @throws IndexOutOfBoundsException
	 */
	public final void register(List<MethodContext> methodList) throws IndexOutOfBoundsException {
		//Check for array bounds
		if(this.methodCount > MAX_METHODS) {
			throw new IndexOutOfBoundsException("Too many registered methods. Max: " + MAX_METHODS);
		} else if(this.methodCount + methodList.size() > MAX_METHODS) {
			throw new IndexOutOfBoundsException("Registering this listener exceeds the maximum " +
					"amount of registered methods. " +
					"Current: " + this.methodCount +
					" Max: " + MAX_METHODS);
		}

		//Add all method contexts
		for(MethodContext context : methodList) {
			Class<? extends Event> paramType = (Class<? extends Event>) context.getEventClass();
			List<InternalMethodContext> lle = this.registeredEntries.get(paramType);
			if(lle == null) {
				lle = new ArrayList<InternalMethodContext>();
				this.registeredEntries.put(paramType, lle);
			}
			lle.add(new InternalMethodContext(context, context.getListener(), context.getMethod(), context.getHandlerAnnotation(), context.getFilter(), context.getEventClass()));
			this.methodCount++;
		}

		this.updateArrays();
	}

	/**
	 * Registers a listener to the {@link DRCEventBus}. The event bus has to be updated with {@link DRCEventBus#bind()} for the new listener to take effect.
	 * <p>
	 * The default EventBus has a limit of {@link DRCEventBus#MAX_METHODS} listening methods. If more than {@link DRCEventBus#MAX_METHODS} listening methods 
	 * are registered an IndexOutOfBoundsException is thrown.
	 * <p>
	 * A {@link SubscriptionException} is thrown if an invalid method has been found.
	 * @param listener {@link IListener} to register
	 * @throws SubscriptionException
	 * @throws IndexOutOfBoundsException
	 * @return {@link List} read-only list of all found valid method contexts
	 */
	public final List<MethodContext> registerAndAnalyze(IListener listener) throws SubscriptionException, IndexOutOfBoundsException {
		List<MethodContext> contextList = MethodContext.getMethodContexts(listener);
		this.register(contextList);
		return Collections.unmodifiableList(contextList);
	}

	/**
	 * Registers a single {@link MethodContext} to the {@link DRCEventBus}. The event bus has to be updated with {@link DRCEventBus#bind()} for the new {@link IListener} to take effect.
	 * <p>
	 * The default event bus has a limit of {@link DRCEventBus#MAX_METHODS} method contexts. If more than {@link DRCEventBus#MAX_METHODS} method contexts are registered an {@link IndexOutOfBoundsException} is thrown.
	 * @param context {@link MethodContext} to register
	 * @throws IndexOutOfBoundsException
	 */
	public final void register(MethodContext context) throws IndexOutOfBoundsException {
		List<MethodContext> contextList = new ArrayList<MethodContext>();
		contextList.add(context);
		this.register(contextList);
	}

	/**
	 * Registers a listener to the {@link DRCEventBus}. The event bus has to be updated with {@link DRCEventBus#bind()} for the new listener to take effect.
	 * <p>
	 * The default EventBus has a limit of {@link DRCEventBus#MAX_METHODS} listening methods. If more than {@link DRCEventBus#MAX_METHODS} listening methods 
	 * are registered an {@link IndexOutOfBoundsException} is thrown.
	 * <p>
	 * A {@link SubscriptionException} is thrown if an invalid method has been found.
	 * @param listener {@link IListener} to register
	 * @throws SubscriptionException
	 * @throws IndexOutOfBoundsException
	 */
	@Override
	public final void register(IListener listener) throws SubscriptionException, IndexOutOfBoundsException {
		this.registerAndAnalyze(listener);
	}

	/**
	 * Returns a read-only list of all registered method contexts.
	 * @return {@link List} read-only list of all registered method contexts
	 */
	public final List<MethodContext> getMethodEntries() {
		List<MethodContext> result = new ArrayList<MethodContext>();
		for(Entry<Class<? extends Event>, List<InternalMethodContext>> e : this.registeredEntries.entrySet()) {
			for(InternalMethodContext lme : e.getValue()) {
				result.add(lme.context);
			}
		}
		return Collections.unmodifiableList(result);
	}


	/**
	 * Unregisters an {@link IListener} from the {@link DRCEventBus}. The event bus has to be updated with {@link DRCEventBus#bind()} for this to take effect.
	 * Only unregisters the first occurrence of the specified listener.
	 * @param listener {@link IListener} to unregister
	 */
	@Override
	public final void unregister(IListener listener) {
		Method[] listenerMethods = listener.getClass().getDeclaredMethods();
		for(Method method : listenerMethods) {
			if(method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) continue;
			Subscribe handlerAnnotation = method.getAnnotation(Subscribe.class);
			if(handlerAnnotation != null) {
				int methodModifiers = method.getModifiers();
				if((methodModifiers & Modifier.STATIC) != 0 ||
						(methodModifiers & Modifier.ABSTRACT) != 0 ||
						(methodModifiers & Modifier.PRIVATE) != 0 ||
						(methodModifiers & Modifier.PROTECTED) != 0) {
					continue;
				}
				if(method.getReturnType() != void.class) {
					continue;
				}
				Iterator<Entry<Class<? extends Event>, List<InternalMethodContext>>> entryIterator = this.registeredEntries.entrySet().iterator();
				while(entryIterator.hasNext()) {
					Entry<Class<? extends Event>, List<InternalMethodContext>> entry = entryIterator.next();
					Class<? extends Event> eventClassGroup = entry.getKey();
					List<InternalMethodContext> imeList = entry.getValue();
					Iterator<InternalMethodContext> imeIterator = imeList.iterator();
					boolean removed = false;
					while(imeIterator.hasNext()) {
						InternalMethodContext ime = imeIterator.next();
						if(ime.method.equals(method) && ime.instance == listener) {
							imeIterator.remove();
							if(ime.method.getParameterTypes()[0].equals(eventClassGroup)) {
								this.methodCount--;
								if(imeList.size() == 0) {
									entryIterator.remove();
									removed = true;
								}
								break;
							}
						}
					}
					if(!removed && imeList.size() == 0) {
						entryIterator.remove();
					}
				}
			}
		}
		this.updateArrays();
	}

	/**
	 * Unregisters a {@link MethodContext} from the {@link DRCEventBus}. The event bus has to be updated with {@link DRCEventBus#bind()} for this to take effect.
	 * Only unregisters the first occurrence of the specified method context.
	 * @param context {@link MethodContext} to unregister
	 */
	public final void unregister(MethodContext context) {
		Method method = context.getMethod();
		Iterator<Entry<Class<? extends Event>, List<InternalMethodContext>>> entryIterator = this.registeredEntries.entrySet().iterator();
		while(entryIterator.hasNext()) {
			Entry<Class<? extends Event>, List<InternalMethodContext>> entry = entryIterator.next();
			Class<? extends Event> eventClassGroup = entry.getKey();
			List<InternalMethodContext> imeList = entry.getValue();
			Iterator<InternalMethodContext> imeIterator = imeList.iterator();
			while(imeIterator.hasNext()) {
				InternalMethodContext ime = imeIterator.next();
				if(ime.method.equals(method) && ime.instance == context.getListener()) {
					imeIterator.remove();
					if(ime.method.getParameterTypes()[0].equals(eventClassGroup)) {
						this.methodCount--;
						if(imeList.size() == 0) {
							entryIterator.remove();
						}
						break;
					}
				}
			}
			if(imeList.size() == 0) {
				entryIterator.remove();
			}
		}
	}

	/**
	 * Removes all registered listeners from this {@link DRCEventBus}.
	 */
	public final void clear() {
		this.registeredEntries.clear();
		this.indexLookup.clear();
		this.filterIndexLookup.clear();
		this.updateArrays();
	}

	/**
	 * Returns a read-only list of all registered listeners.
	 * @return {@link List} read-only list of all registered listeners
	 */
	public final List<IListener> getListeners() {
		ArrayList<IListener> listeners = new ArrayList<IListener>();
		for(Entry<Class<? extends Event>, List<InternalMethodContext>> e : this.registeredEntries.entrySet()) {
			for(InternalMethodContext le : e.getValue()) {
				listeners.add(le.instance);
			}
		}
		return Collections.unmodifiableList(listeners);
	}

	/**
	 * Updates the listener array and index lookup map.
	 */
	private final void updateArrays() {
		this.indexLookup.clear();
		this.filterIndexLookup.clear();
		ArrayList<IListener> arrayListenerList = new ArrayList<IListener>();
		ArrayList<IFilter> arrayFilterList = new ArrayList<IFilter>();
		for(Entry<Class<? extends Event>, List<InternalMethodContext>> e : this.registeredEntries.entrySet()) {
			List<InternalMethodContext> listenerEntryList = e.getValue();
			for(InternalMethodContext listenerEntry : listenerEntryList) {
				arrayListenerList.add(listenerEntry.instance);
				this.indexLookup.put(listenerEntry.instance, arrayListenerList.size() - 1);
				if(listenerEntry.filter != null) {
					arrayFilterList.add(listenerEntry.filter);
					this.filterIndexLookup.put(listenerEntry, arrayFilterList.size() - 1);
				}
			}
		}
		this.listenerArray = arrayListenerList.toArray(new IListener[0]);
		this.filterArray = arrayFilterList.toArray(new IFilter[0]);
	}

	/**
	 * Returns the recompiled version of {@link DispatcherImpl}
	 * @return {@link Dispatcher}
	 */
	private final Dispatcher getCompiledDispatcher() {
		if(this.dispatcherImplInstance == null)  {
			this.dispatcherImplInstance = (Dispatcher)this.compileDispatcher();
		}
		return this.dispatcherImplInstance;
	}

	/**
	 * Recompiles {@link DispatcherImpl} with all registered listeners.
	 * @return {@link Dispatcher}
	 */
	private final synchronized Dispatcher compileDispatcher() {
		try {
			//Check for available events for method contexts that accept subclasses and add to the list if available
			this.subclassListeners.clear();
			for(Entry<Class<? extends Event>, List<InternalMethodContext>> mapEntry : this.registeredEntries.entrySet()) {
				List<InternalMethodContext> entryList = mapEntry.getValue();
				for(InternalMethodContext entry : entryList) {
					if(entry.handlerAnnotation.acceptSubclasses()) {
						//Calculate the correct maximum amount of subclass listeners
						int maxContainedEntriesSubclassList = 0;
						for(Entry<Class<? extends Event>, List<InternalMethodContext>> regEntry : this.registeredEntries.entrySet()) {
							for(InternalMethodContext regMethodEntry : regEntry.getValue()) {
								if(regMethodEntry.eventClass.equals(regEntry.getKey()) && regMethodEntry.method.equals(entry.method) && regMethodEntry.instance == entry.instance) {
									maxContainedEntriesSubclassList++;
								}
							}
						}

						//Add to subclass listeners list
						int containedEntries = 0;
						for(InternalMethodContext ime : this.subclassListeners) {
							if(ime.method.equals(entry.method) && ime.instance == entry.instance) {
								containedEntries++;
							}
						}
						if(containedEntries < maxContainedEntriesSubclassList) {
							this.subclassListeners.add(entry);
						}

						//Check if any event subclasses are already available and can be registered in the normal registeredEntries map
						for(Class<? extends Event> eventClass : this.registeredEntries.keySet()) {
							if(entry.eventClass.isAssignableFrom(eventClass) && !entry.eventClass.equals(eventClass)) {
								//Calculate the maximum correct amount of static subclass listeners
								int maxContainedEntries = 0;
								for(Entry<Class<? extends Event>, List<InternalMethodContext>> regEntry : this.registeredEntries.entrySet()) {
									for(InternalMethodContext regMethodEntry : regEntry.getValue()) {
										if(regEntry.getKey().equals(regMethodEntry.eventClass) && regMethodEntry.method.equals(entry.method) && regMethodEntry.instance == entry.instance) {
											maxContainedEntries++;
										}
									}
								}

								//Add to static lists
								List<InternalMethodContext> imeList = this.registeredEntries.get(eventClass);
								containedEntries = 0;
								for(InternalMethodContext ime : imeList) {
									if(!eventClass.equals(ime.eventClass) && ime.method.equals(entry.method) && ime.instance == entry.instance) {
										containedEntries++;
									}
								}
								if(containedEntries < maxContainedEntries) {
									if(imeList != entryList) {
										imeList.add(entry);
									}
								}
							}
						}
					}
				}
			}

			//Sort by priority
			Comparator<InternalMethodContext> prioritySorter = new Comparator<InternalMethodContext>() {
				@Override
				public int compare(InternalMethodContext e1, InternalMethodContext e2) {
					return e2.priority - e1.priority;
				}
			};
			for(List<InternalMethodContext> lle : this.registeredEntries.values()) {
				Collections.sort(lle, prioritySorter);
			}
			Collections.sort(this.subclassListeners, prioritySorter);

			//Instrumentation classloader
			InstrumentationClassLoader<Dispatcher> instrumentationClassLoader = new InstrumentationClassLoader<Dispatcher>(this.getClass().getClassLoader(), DRCEventBus.this.dispatcherClass) {
				@Override
				protected byte[] instrument(byte[] bytecode) {
					ClassReader classReader = new ClassReader(bytecode);
					ClassNode classNode = new ClassNode();
					classReader.accept(classNode, ClassReader.SKIP_FRAMES);
					CompilationNode mainNode = new CompilationNode(DRCEventBus.this.toString());
					DRCEventBus.this.compilationNode.addChild(mainNode);
					for(MethodNode methodNode : (List<MethodNode>) classNode.methods) {
						if(methodNode.name.equals(DISPATCHER_DISPATCH_EVENT_INTERNAL)) {
							instrumentDispatcher(methodNode, true, mainNode);
						}
					}
					DRCEventBus.this.compilationNode = mainNode;
					ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
					classNode.accept(classWriter);
					DRCEventBus.this.dispatcherClassBytes = classWriter.toByteArray();
					return DRCEventBus.this.dispatcherClassBytes;
				}
			};
			Dispatcher dispatcher = instrumentationClassLoader.createInstance(null);
			dispatcher.init(this.listenerArray, this.filterArray);
			return dispatcher;
		} catch(Exception ex) {
			throw new DispatcherException("Could not initialize dispatcher", ex);
		}
	}

	/**
	 * Modifies the internal event dispatching method.
	 * @param methodNode {@link MethodNode}
	 */
	private final synchronized void instrumentDispatcher(MethodNode methodNode, boolean cancellable, CompilationNode mainNode) {
		InsnList methodInstructionSet = methodNode.instructions;
		ArrayList<AbstractInsnNode> instructionSet = new ArrayList<AbstractInsnNode>();
		Iterator<AbstractInsnNode> it = methodInstructionSet.iterator();
		AbstractInsnNode insn;
		AbstractInsnNode implementationNode = null;
		boolean isNodeDispatcher = false;
		while((insn = it.next()) != null && it.hasNext()) {
			boolean isReturn = this.dispatcherClass == DispatcherImpl.class && 
					(insn.getOpcode() == Opcodes.IRETURN || 
					insn.getOpcode() == Opcodes.LRETURN ||
					insn.getOpcode() == Opcodes.RETURN ||
					insn.getOpcode() == Opcodes.ARETURN ||
					insn.getOpcode() == Opcodes.DRETURN ||
					insn.getOpcode() == Opcodes.FRETURN);
			boolean isDispatcherMethod = this.dispatcherClass != DispatcherImpl.class && insn.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode)insn).name.equals(DISPATCHER_DISPATCH) && ((MethodInsnNode)insn).owner.equals(Dispatcher.class.getName().replace(".", "/"));
			//Only implement first dispatcher, throw an error if there are multiple implementations
			if(isDispatcherMethod && implementationNode != null) {
				throw new DispatcherException("The dispatching implementation Dispatcher#dispatch() can only be used once per method");
			}
			if(isReturn || isDispatcherMethod) {
				isNodeDispatcher = isDispatcherMethod;
				implementationNode = insn;
			}
		}

		//No implementation or return node
		if(implementationNode == null) {
			return;
		}

		LabelNode exitNode = new LabelNode();

		CompilationNode eventNode = new CompilationNode((cancellable ? "Cancellable" : "Non cancellable") + " events");
		mainNode.addChild(eventNode);

		//Scope start for contained variable
		LabelNode containedVarStart = new LabelNode();
		instructionSet.add(containedVarStart);

		//Add the contained variable
		int containedVarID = methodNode.localVariables.size();
		LocalVariableNode containedVarNode = new LocalVariableNode("contained", "Z", null, containedVarStart, exitNode, containedVarID);
		methodNode.localVariables.add(containedVarNode);
		//Set contained variable to false
		instructionSet.add(new InsnNode(Opcodes.ICONST_0));
		instructionSet.add(new VarInsnNode(Opcodes.ISTORE, containedVarID));

		/*
		 * Pseudo code, runs for every listener method:
		 * 
		 * //Optional check, only present if subclasses are also accepted
		 * if(event instanceof listenerArray[n].eventType) {
		 * //Replacement for when subclasses are not accepted
		 * if(event.getClass() == listenerArray[n].eventType) {
		 * 
		 *   //Optional check, only present if filter is not default IFilter class
		 *   if(filterArray[p].filter(listenerArray[n], event) {
		 *   
		 *     //Optional check, only present if the compiled method with the cancellable code is used
		 *     if(event instanceof IEventCancellable == false ||
		 *        !((IEventCancellable)event).isCancelled()) {
		 *        
		 *       //Invokes the listener method
		 *       listenerArray[n].invokeMethod(event);
		 *       
		 *       //Optional check, only present if the compiled method with the cancellable code is used
		 *       if(event instanceof IEventCancellable) {
		 *       
		 *         //Optional check, only present if the compiled method with the cancellable code is used
		 *         if(((IEventCancellable)event).isCancelled()) return;
		 *         
		 *       }
		 *     }
		 *   }
		 *   
		 * }
		 * 
		 * ...
		 */

		for(Entry<Class<? extends Event>, List<InternalMethodContext>> e : this.registeredEntries.entrySet()) {
			String eventClassGroup = BytecodeUtil.getClassType(e.getKey());

			CompilationNode eventClassNode = new CompilationNode(e.getKey().getName());
			eventNode.addChild(eventClassNode);

			CompilationNode staticListenersNode = new CompilationNode("Static Listeners");
			eventClassNode.addChild(staticListenersNode);

			//Fail label, jumped to if class comparison fails
			LabelNode classCompareFailLabelNode = new LabelNode();

			//if(event.getClass() != listenerArray[n].eventType) -> jump to classCompareFailLabelNode
			instructionSet.add(new IntInsnNode(Opcodes.ALOAD, 1));
			instructionSet.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
			String eventClassNameClass = "L" + eventClassGroup + ";";
			instructionSet.add(new LdcInsnNode(Type.getType(eventClassNameClass)));
			instructionSet.add(new JumpInsnNode(Opcodes.IF_ACMPNE, classCompareFailLabelNode));

			//Set contained variable to true
			instructionSet.add(new InsnNode(Opcodes.ICONST_1));
			instructionSet.add(new VarInsnNode(Opcodes.ISTORE, containedVarID));

			for(InternalMethodContext listenerEntry : e.getValue()) {
				//Instrument dispatcher for listener
				this.instrumentSingleDispatcher(instructionSet, listenerEntry, 
						listenerEntry.eventClass, cancellable, exitNode, staticListenersNode);
			}

			//Jumped to if class comparison fails
			instructionSet.add(classCompareFailLabelNode);
		}

		CompilationNode dynamicListenersNode = new CompilationNode("Dynamic Listeners");
		mainNode.addChild(dynamicListenersNode);

		//Jump to exit if contained == true
		instructionSet.add(new VarInsnNode(Opcodes.ILOAD, containedVarID));
		instructionSet.add(new JumpInsnNode(Opcodes.IFNE, exitNode));

		for(InternalMethodContext listenerEntry : this.subclassListeners) {
			//Instrument dispatcher for listener
			this.instrumentSingleDispatcher(instructionSet, listenerEntry,
					listenerEntry.eventClass, cancellable, exitNode, dynamicListenersNode);
		}


		//Jumped to if an event was cancelled or contained == true
		instructionSet.add(exitNode);

		//Instrumentation callback to let the user modify the dispatching bytecode
		if(this.instrumentDispatcher(instructionSet, methodNode)) {
			for(AbstractInsnNode insnNode : instructionSet) {
				methodInstructionSet.insertBefore(implementationNode, insnNode);
			}
			if(isNodeDispatcher) {
				methodInstructionSet.remove(implementationNode);
			}
		}
		methodNode.visitMaxs(0, 0);
	}

	/**
	 * Instruments the dispatcher for a listener entry
	 * @param instructionSet
	 * @param listenerEntry
	 * @param entryList
	 * @param eventClassName
	 * @param cancellable
	 * @param exitLabelNode
	 */
	private final synchronized void instrumentSingleDispatcher(ArrayList<AbstractInsnNode> instructionSet, InternalMethodContext listenerEntry, 
			Class<? extends IEvent> eventClass, boolean cancellable, LabelNode exitLabelNode, CompilationNode compilationNode) {
		String eventClassName = BytecodeUtil.getClassType(eventClass);
		String dispatcherClassName = BytecodeUtil.getClassType(this.dispatcherClass);
		String listenerArrayType = BytecodeUtil.getArrayObjectType(IListener.class);
		String listenerClassName = BytecodeUtil.getClassType(listenerEntry.instance.getClass());
		String listenerMethodName = listenerEntry.methodName;
		String listenerMethodType = BytecodeUtil.getListenerMethodType(eventClassName);
		int listenerIndex = this.indexLookup.get(listenerEntry.instance);

		CompilationNode listenerNode = new CompilationNode(listenerEntry.instance.getClass().getName() + "#" + listenerEntry.methodName);
		compilationNode.addChild(listenerNode);

		CompilationNode indexNode = new CompilationNode("Class index: " + listenerIndex);
		listenerNode.addChild(indexNode);

		CompilationNode priorityNode = new CompilationNode("Priority: " + listenerEntry.priority);
		listenerNode.addChild(priorityNode);

		//Used for filter fail jump or subclass check fail jump (only if subclasses are not accepted)
		LabelNode entryFailLabelNode = new LabelNode();

		//Only implement if filter is not default IFilter class
		//if(!filterArray[n].filter(listenerArray[p], event)) -> jump to entryFailLabelNode
		if(listenerEntry.filter != null) {
			int filterIndex = this.filterIndexLookup.get(listenerEntry);
			String filterClassName = BytecodeUtil.getClassType(listenerEntry.filter.getClass());
			String filterFieldType = BytecodeUtil.getArrayObjectType(IFilter.class);
			String filterMethodType = BytecodeUtil.getFilterMethodType();

			//get filter array
			instructionSet.add(new IntInsnNode(Opcodes.ALOAD, 0));
			instructionSet.add(new FieldInsnNode(Opcodes.GETFIELD, dispatcherClassName, DISPATCHER_FILTER_ARRAY, filterFieldType));
			//push index onto stack
			instructionSet.add(BytecodeUtil.getOptimizedIndex(filterIndex));
			//load filter from array and cast filter type
			instructionSet.add(new InsnNode(Opcodes.AALOAD));
			instructionSet.add(new TypeInsnNode(Opcodes.CHECKCAST, filterClassName));

			//load event and invoke filter, return boolean
			instructionSet.add(new IntInsnNode(Opcodes.ALOAD, 1));
			instructionSet.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, filterClassName, IFILTER_FILTER, filterMethodType, false));

			//jump if false was returned
			instructionSet.add(new JumpInsnNode(Opcodes.IFEQ, entryFailLabelNode));
		}

		if(cancellable && IEventCancellable.class.isAssignableFrom(eventClass)) {
			//load and cast event to EventCancellable
			instructionSet.add(new IntInsnNode(Opcodes.ALOAD, 1));
			instructionSet.add(new TypeInsnNode(Opcodes.CHECKCAST, eventClassName));

			//invoke EventCancellable#isCancelled
			instructionSet.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, IEVENTCANCELLABLE_IS_CANCELLED, "()Z", false));

			//if(!EventCancellable#isCancelled()) -> jump to exitNode
			instructionSet.add(new JumpInsnNode(Opcodes.IFNE, exitLabelNode));
		}

		//Only implement IListener#isEnabled() check if Receiver#forced() is false
		if(!listenerEntry.forced) {
			////////////////////////////// Check if listener is enabled //////////////////////////////
			//get listener array
			instructionSet.add(new IntInsnNode(Opcodes.ALOAD, 0));
			instructionSet.add(new FieldInsnNode(Opcodes.GETFIELD, dispatcherClassName, DISPATCHER_LISTENER_ARRAY, listenerArrayType));
			//push index onto stack
			instructionSet.add(BytecodeUtil.getOptimizedIndex(listenerIndex));
			//load listener from array and cast listener type
			instructionSet.add(new InsnNode(Opcodes.AALOAD));
			instructionSet.add(new TypeInsnNode(Opcodes.CHECKCAST, listenerClassName));

			//invoke isEnabled, return boolean
			instructionSet.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, listenerClassName, ILISTENER_IS_ENABLED, "()Z", false));

			//jump to failLabelNode if returned boolean is false
			instructionSet.add(new JumpInsnNode(Opcodes.IFEQ, entryFailLabelNode));
		}

		///////////////////////////////// Invoke listener method /////////////////////////////////
		//get listener array
		instructionSet.add(new IntInsnNode(Opcodes.ALOAD, 0));
		instructionSet.add(new FieldInsnNode(Opcodes.GETFIELD, dispatcherClassName, DISPATCHER_LISTENER_ARRAY, listenerArrayType));
		//push index onto stack
		instructionSet.add(BytecodeUtil.getOptimizedIndex(listenerIndex));
		//load listener from array and cast listener type
		instructionSet.add(new InsnNode(Opcodes.AALOAD));
		instructionSet.add(new TypeInsnNode(Opcodes.CHECKCAST, listenerClassName));

		//load parameter and cast parameter type (the event to post)
		instructionSet.add(new IntInsnNode(Opcodes.ALOAD, 1));
		instructionSet.add(new TypeInsnNode(Opcodes.CHECKCAST, eventClassName));

		//invoke method
		instructionSet.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, listenerClassName, listenerMethodName, listenerMethodType, false));

		CompilationNode forcedNode = new CompilationNode((!listenerEntry.forced ? "[ ]" : "[x]") + " Forced");
		listenerNode.addChild(forcedNode);

		if(listenerEntry.filter != null) {
			CompilationNode filterNode = new CompilationNode("[x] Filter");
			listenerNode.addChild(filterNode);
			CompilationNode filterNameNode = new CompilationNode(listenerEntry.filter.getClass().getName());
			filterNode.addChild(filterNameNode);
		} else {
			CompilationNode filterNode = new CompilationNode("[ ] Filter");
			listenerNode.addChild(filterNode);
		}

		instructionSet.add(entryFailLabelNode);
	}

	/**
	 * Posts an {@link Event} and returns the posted event.
	 * @param event {@link Event} to dispatch
	 * @return {@link Event} the posted event
	 */
	@Override
	public <T extends Event> T post(T event) {
		return this.getCompiledDispatcher().dispatchEvent(event);
	}

	/**
	 * Compiles the internal event dispatcher and binds all registered listeners. Required for new method contexts or dispatcher to take effect.
	 * For optimal performance this method should be called after all listeners have been
	 * registered.
	 */
	@Override
	public void bind() {
		this.dispatcherImplInstance = (Dispatcher)this.compileDispatcher();
	}

	/**
	 * Sets the event dispatcher class.
	 * Set to null to enable the internal dispatcher of {@link DRCEventBus}.
	 * <p>
	 * More information about custom dispatchers at {@link Dispatcher} and {@link Dispatcher#dispatch()}
	 * <p>
	 * The event bus has to be updated with {@link DRCEventBus#bind()} for the new dispatcher to take effect.
	 * @param dispatcherClass {@link Dispatcher}
	 * @throws DispatcherException
	 */
	public final void setDispatcher(Class<? extends Dispatcher> dispatcherClass) throws DispatcherException {
		try {
			if(dispatcherClass != null) {
				if(dispatcherClass.getDeclaredConstructor() != null) {
					this.dispatcherClass = dispatcherClass;
				}
			} else {
				this.dispatcherClass = DispatcherImpl.class;
			}
		} catch(Exception ex) {
			throw new DispatcherException("Could not set dispatcher", ex);
		}
	}

	/**
	 * Returns the compiled dispatcher object.
	 * <p>
	 * <b>Important</b>: The dispatcher object will not be castable to its own
	 * class as it becomes a different type after the compilation. Casting to a
	 * superclass however still works. If there are any methods or fields specific to that
	 * dispatcher class that have to be accessed from outside, the dispatcher has to be
	 * casted to a superclass or interface (which the dispatcher must extend or implement)
	 * of which those specific methods or fields, that have to be accessed from outside,
	 * can be inherited from or implemented.
	 * @return {@link Dispatcher} the dispatcher
	 */
	public final Dispatcher getDispatcher() {
		//Check by name because it's a different class after compilation
		if(this.dispatcherImplInstance != null && this.dispatcherImplInstance.getClass().getName().equals(DispatcherImpl.class.getName())) {
			return null; 
		}
		return this.dispatcherImplInstance;
	}

	/**
	 * Returns a new instance of this {@link DRCEventBus} with the same
	 * properties.
	 * Used in {@link DRCExpander} to create copies of
	 * the given bus.
	 * @return {@link IEventBus}
	 */
	@Override
	public IEventBus copyBus() {
		return new DRCEventBus(this.dispatcherClass, this.compilationNode);
	}

	/**
	 * The compilation node contains a tree structure that describes the compiled dispatcher method.
	 * @return {@link CompilationNode}
	 */
	public final CompilationNode getCompilationNode() {
		return this.compilationNode;
	}

	/**
	 * Called when the dispatching method is being instrumented. Additional instructions can be added to the baseInstructions or directly
	 * to the methodNode. Return false to cancel the method instrumentation.
	 * @param baseInstructions {@link List}
	 * @param methodNode {@link MethodNode}
	 * @param compilationNode {@link CompilationNode}
	 * @return boolean false to cancel the instrumentation
	 */
	protected boolean instrumentDispatcher(List<AbstractInsnNode> baseInstructions, MethodNode methodNode) {
		return true;
	}

	/**
	 * Dumps the bytecode of the compiled dispatcher
	 * @param stream Stream to write to
	 * @throws IOException 
	 */
	public void dumpBytecode(OutputStream stream) throws IOException {
		if(this.dispatcherClassBytes != null) stream.write(this.dispatcherClassBytes);
	}
}