package tcb.bces.bus;

import tcb.bces.event.Event;
import tcb.bces.event.IEventCancellable;
import tcb.bces.listener.IListener;
import tcb.bces.listener.Subscribe;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

/**
 * This event bus implementation uses a {@link HashMap} to map the registered listeners to the event classes.
 * Using listeners that accept subclasses could slow down the event dispatching because the event types 
 * have to be compared during the dispatching.
 * 
 * @author TCB
 *
 */
public class MappedEventBus implements IEventBus {
	private final Map<Class<? extends Event>, List<MethodContext>> eventListenerMap = new HashMap<Class<? extends Event>, List<MethodContext>>();
	private final List<MethodContext> subclassListeners = new ArrayList<MethodContext>();

	@Override
	public void register(IListener listener) {
		List<MethodContext> methodEntries = MethodContext.getMethodContexts(listener);
		List<List<MethodContext>> modifiedLists = new ArrayList<List<MethodContext>>();
		boolean subclassListenersModified = false;
		for(MethodContext me : methodEntries) {
			List<MethodContext> listeners = this.eventListenerMap.get(me.getEventClass());
			if(listeners == null) {
				listeners = new ArrayList<MethodContext>();
				this.eventListenerMap.put(me.getEventClass(), listeners);
			}
			listeners.add(me);
			if(!modifiedLists.contains(listeners)) {
				modifiedLists.add(listeners);
			}
			if(me.getHandlerAnnotation().acceptSubclasses()) {
				//Calculate the correct maximum amount of subclass listeners
				int maxContainedEntriesSubclassList = 0;
				for(Entry<Class<? extends Event>, List<MethodContext>> regEntry : this.eventListenerMap.entrySet()) {
					for(MethodContext regMethodEntry : regEntry.getValue()) {
						if(regMethodEntry.getEventClass().equals(regEntry.getKey()) && regMethodEntry.getMethod().equals(me.getMethod()) && regMethodEntry.getListener() == me.getListener()) {
							maxContainedEntriesSubclassList++;
						}
					}
				}

				//Add to subclass listeners list
				int containedEntries = 0;
				for(MethodContext scl : this.subclassListeners) {
					if(scl.getMethod().equals(me.getMethod()) && scl.getListener() == me.getListener()) {
						containedEntries++;
					}
				}
				if(containedEntries < maxContainedEntriesSubclassList) {
					this.subclassListeners.add(me);
					subclassListenersModified = true;
				}
			}
		}
		if(subclassListenersModified) {
			modifiedLists.add(this.subclassListeners);
		}
		//Check for any registered subclasses of the event types and add listeners to that list
		Iterator<Entry<Class<? extends Event>, List<MethodContext>>> entryIterator = this.eventListenerMap.entrySet().iterator();
		while(entryIterator.hasNext()) {
			Entry<Class<? extends Event>, List<MethodContext>> entry = entryIterator.next();
			Class<? extends Event> eventClass = entry.getKey();
			List<MethodContext> methodEntryList = entry.getValue();
			for(MethodContext me : methodEntries) {
				if(me.getHandlerAnnotation().acceptSubclasses() && me.getEventClass().isAssignableFrom(eventClass) && !me.getEventClass().equals(eventClass)) {
					methodEntryList.add(me);
					if(!modifiedLists.contains(methodEntryList)) {
						modifiedLists.add(methodEntryList);
					}
				}
			}
		}
		//Update priorities
		Comparator<MethodContext> prioritySorter = new Comparator<MethodContext>() {
			@Override
			public int compare(MethodContext e1, MethodContext e2) {
				return e2.getHandlerAnnotation().priority() - e1.getHandlerAnnotation().priority();
			}
		};
		for(List<MethodContext> methodEntryList : modifiedLists) {
			Collections.sort(methodEntryList, prioritySorter);
		}
	}

	@Override
	public void unregister(IListener listener) {
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

				//Remove from regular map
				Iterator<Entry<Class<? extends Event>, List<MethodContext>>> mapEntryIT = this.eventListenerMap.entrySet().iterator();
				while(mapEntryIT.hasNext()) {
					Entry<Class<? extends Event>, List<MethodContext>> mapEntry = mapEntryIT.next();
					List<MethodContext> methodEntries = mapEntry.getValue();
					Iterator<MethodContext> methodEntryIterator = methodEntries.iterator();
					while(methodEntryIterator.hasNext()) {
						MethodContext me = methodEntryIterator.next();
						if(me.getMethod().equals(method) && me.getListener() == listener) {
							methodEntryIterator.remove();
							break;
						}
					}
					if(methodEntries.size() == 0) {
						mapEntryIT.remove();
					}
				}

				//Remove from subclass list
				Iterator<MethodContext> subclassListenersIT = this.subclassListeners.iterator();
				while(subclassListenersIT.hasNext()) {
					MethodContext entry = subclassListenersIT.next();
					if(entry.getMethod().equals(method) && entry.getListener() == listener) {
						subclassListenersIT.remove();
					}
				}
			}
		}
	}

	@Override
	public <T extends Event> T post(T event) {
		Class<?> eventClass = event.getClass();
		List<MethodContext> methodEntries = this.eventListenerMap.get(eventClass);
		IEventCancellable eventCancellable = null;
		if(event instanceof IEventCancellable) {
			eventCancellable = (IEventCancellable) event;
		}
		boolean contained = false;
		if(methodEntries != null) {
			contained = true;
			for(MethodContext me : methodEntries) {
				if((me.getHandlerAnnotation().forced() || me.getListener().isListening()) && (me.getFilter() == null || me.getFilter().filter(event))) {
					try {
						me.invoke(event);
						if(eventCancellable != null && eventCancellable.isCancelled()) {
							return event;
						}
					} catch(Exception ex){
						throw new RuntimeException(ex);
					}
				}
			}
		}
		if(!contained) {
			for(MethodContext me : this.subclassListeners) {
				if((me.getHandlerAnnotation().forced() || me.getListener().isListening()) && (me.getFilter() == null || me.getFilter().filter(event))) {
					if(me.getEventClass() != eventClass) {
						try {
							me.invoke(event);
							if(eventCancellable != null && eventCancellable.isCancelled()) {
								return event;
							}
						} catch(Exception ex){
							throw new RuntimeException(ex);
						}
					}
				}
			}
		}
		return event;
	}
}
