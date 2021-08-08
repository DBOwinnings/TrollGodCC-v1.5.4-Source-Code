package tcb.bces.bus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tcb.bces.bus.compilation.CompilationNode;
import tcb.bces.event.Event;
import tcb.bces.listener.IListener;
import tcb.bces.listener.SubscriptionException;

/**
 * This bus expander can expand any limited event bus that extends {@link DRCEventBus} 
 * and thus removes the listener limit while still keeping all the functionality of the limited bus.
 * Any properties of the expanded bus have to be set before {@link DRCExpander#bind()} is called.
 * 
 * @author TCB
 *
 */
public class DRCExpander<B extends DRCEventBus> implements IEventBus, ICompilableBus {
	private final ArrayList<B> busCollection = new ArrayList<B>();
	private final HashMap<Class<? extends Event>, List<B>> busMap = new HashMap<Class<? extends Event>, List<B>>();
	private final List<MethodContext> registeredMethodEntries = new ArrayList<MethodContext>();
	private DRCEventBus currentBus = null;
	private final int maxMethodEntriesPerBus;
	private boolean singleBus = true;
	private B busInstance;
	private Class<B> busType;

	/**
	 * Initializes a new {@link DRCExpander} with a maximum method context limit per bus of 50 method contexts (recommended).
	 */
	@SuppressWarnings("unchecked")
	public DRCExpander(B busInstance) {
		this.maxMethodEntriesPerBus = 50;
		this.busInstance = busInstance;
		this.busType = (Class<B>) busInstance.getClass();
	}

	/**
	 * Initializes a new {@link DRCExpander} with a specified maximum method context limit per bus.
	 * Maximum limit of method contexts per bus is {@link DRCEventBus#MAX_METHODS}
	 * @param maxMethodsPerBus Integer
	 */
	@SuppressWarnings("unchecked")
	public DRCExpander(B busInstance, int maxMethodsPerBus) {
		if(maxMethodsPerBus > DRCEventBus.MAX_METHODS) {
			maxMethodsPerBus = DRCEventBus.MAX_METHODS;
		} else if(maxMethodsPerBus < 1) {
			maxMethodsPerBus = 1;
		}
		this.maxMethodEntriesPerBus = maxMethodsPerBus;
		this.busInstance = busInstance;
		this.busType = (Class<B>) busInstance.getClass();
	}

	/**
	 * Returns the bus type this {@link DRCExpander} uses.
	 * @return
	 */
	public Class<B> getBusType() {
		return this.busType;
	}

	/**
	 * Registers a listener to the {@link DRCEventBus}. The event bus has to be updated with {@link DRCEventBus#bind()} for the new listener to take effect.
	 * <p>
	 * A {@link SubscriptionException} is thrown if an invalid method has been found.
	 * @param listener {@link IListener} to register
	 * @throws SubscriptionException
	 * @return {@link List} read-only list of all found valid method contexts
	 */
	public final List<MethodContext> registerAndAnalyze(IListener listener) throws SubscriptionException {
		List<MethodContext> entries = MethodContext.getMethodContexts(listener);
		this.registeredMethodEntries.addAll(entries);
		return Collections.unmodifiableList(entries);
	}

	/**
	 * Registers a listener to the {@link DRCEventBus}. The event bus has to be updated with {@link DRCEventBus#bind()} for the new listener to take effect.
	 * <p>
	 * A {@link SubscriptionException} is thrown if an invalid method has been found.
	 * @param listener {@link IListener} to register
	 * @throws SubscriptionException
	 */
	@Override
	public final void register(IListener listener) throws SubscriptionException {
		this.registerAndAnalyze(listener);
	}

	/**
	 * Unregisters an {@link IListener} from the {@link DRCExpander}. The event bus has to be updated with {@link DRCExpander#bind()} for this to take effect.
	 * Only unregisters the first occurrence of the specified listener.
	 * @param listener {@link IListener} to unregister
	 */
	@Override
	public final void unregister(IListener listener) {
		for(Method method : listener.getClass().getDeclaredMethods()) {
			Iterator<MethodContext> it = this.registeredMethodEntries.iterator();
			while(it.hasNext()) {
				MethodContext context = it.next();
				if(context.getMethod().equals(method) && context.getListener() == listener) {
					it.remove();
					break;
				}
			}
		}
	}

	/**
	 * Registers a single {@link MethodContext} to the {@link DRCExpander}. The event bus has to be updated with {@link DRCExpander#bind()} for the new {@link MethodContext} to take effect.
	 * @param context {@link MethodContext} to register
	 */
	public final void register(MethodContext context) {
		this.registeredMethodEntries.add(context);
	}

	/**
	 * Unregisters a {@link MethodContext} from the {@link DRCExpander}. The event bus has to be updated with {@link DRCExpander#bind()} for this to take effect.
	 * Only unregisters the first occurrence of the specified method context.
	 * @param methodEntry {@link MethodContext} to unregister
	 */
	public final void unregister(MethodContext context) {
		this.registeredMethodEntries.remove(context);
	}

	/**
	 * Compiles the internal event dispatcher and binds all registered listeners. Required for new method contexts or dispatcher to take effect.
	 * For optimal performance this method should be called after all listeners have been
	 * registered.
	 */
	@Override
	public final void bind() {
		this.busCollection.clear();
		this.busMap.clear();
		this.singleBus = true;
		for(List<MethodContext> mel : this.getSortedMethodEntries()) {
			@SuppressWarnings("unchecked")
			B bus = (B) this.busInstance.copyBus();
			this.currentBus = bus;
			for(MethodContext me : mel) {
				bus.register(me);
				List<B> busList = this.busMap.get(me.getEventClass());
				if(busList == null) {
					busList = new ArrayList<B>();
					this.busMap.put(me.getEventClass(), busList);
				}
				if(!busList.contains(bus)) busList.add(bus);
			}
			if(!this.busCollection.contains(bus)) this.busCollection.add(bus);
		}
		if(this.busCollection.size() > 0) {
			for(B bus : this.busCollection) {
				bus.bind();
			}
		} else {
			//Create empty bus
			@SuppressWarnings("unchecked")
			B emptyBus = (B) this.busInstance.copyBus();
			emptyBus.bind();
			this.busCollection.add(emptyBus);
			this.currentBus = emptyBus;
		}
	}

	/**
	 * Returns the read-only bus map. 
	 * The bus lists are grouped by event class.
	 * @return read-only bus map
	 */
	public final Map<Class<? extends Event>, List<B>> getBusMap() {
		return Collections.unmodifiableMap(this.busMap);
	}

	/**
	 * Returns the read-only bus list.
	 * @return read-only bus list
	 */
	public final List<DRCEventBus> getBusList() {
		return Collections.<DRCEventBus>unmodifiableList(this.busCollection);
	}

	/**
	 * Returns a list of all registered method contexts grouped by event type.
	 * @return List<List<MethodEntry>>
	 */
	private final List<List<MethodContext>> getSortedMethodEntries() {
		HashMap<Class<? extends Event>, List<MethodContext>> eventListenerMap = new HashMap<Class<? extends Event>, List<MethodContext>>();
		for(MethodContext me : this.registeredMethodEntries) {
			List<MethodContext> mel = eventListenerMap.get(me.getEventClass());
			if(mel == null) {
				mel = new ArrayList<MethodContext>();
				eventListenerMap.put(me.getEventClass(), mel);
			}
			mel.add(me);
		}
		int index = 0;
		Iterator<Entry<Class<? extends Event>, List<MethodContext>>> it = eventListenerMap.entrySet().iterator();
		List<List<MethodContext>> methodEntryList = new ArrayList<List<MethodContext>>();
		List<MethodContext> currentList = new ArrayList<MethodContext>();
		while(it.hasNext()) {
			List<MethodContext> mel = it.next().getValue();
			for(MethodContext me : mel) {
				if(index >= this.maxMethodEntriesPerBus) {
					methodEntryList.add(currentList);
					currentList = new ArrayList<MethodContext>();
					this.singleBus = false;
					index = 0;
				}
				currentList.add(me);
				index++;
			}
		}
		if(index != 0) methodEntryList.add(currentList);
		return methodEntryList;
	}

	/**
	 * The compilation node contains a tree structure that describes the compiled dispatcher methods.
	 * @return {@link CompilationNode}
	 */
	public CompilationNode getCompilationNode() {
		return this.busInstance.getCompilationNode();
	}

	@Override
	public final <T extends Event> T post(T event) {
		if(this.currentBus == null) {
			throw new NullPointerException("Bus has not been compiled");
		}
		if(this.singleBus) {
			event = this.currentBus.post(event);
		} else {
			//TODO: Maybe find a solution that still works with the bus map
			/*List<B> busList = this.busMap.get(event.getClass());
			if(busList == null) {
				return event;
			}
			for(DRCEventBus bus : busList) {
				event = bus.post(event);
			}*/
			for(DRCEventBus bus : this.busCollection) {
				bus.post(event);
			}
		}
		return event;
	}
}
