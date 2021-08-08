package tcb.bces.bus.compilation;

import tcb.bces.bus.DRCEventBus;
import tcb.bces.event.Event;
import tcb.bces.listener.IListener;
import tcb.bces.listener.filter.IFilter;

/**
 * This stub is used as a template to compile the dispatcher in {@link DRCEventBus}.
 * Any class that extends this class must have a no-arg or default constructor
 * in order to work.
 * <p>
 * This class can be extended in order to create custom dispatchers. By doing so the event
 * dispatching can be modified or even totally replaced by a custom implementation.
 * <p>
 * More information about custom dispatchers at {@link Dispatcher#dispatch()}
 * 
 * @author TCB
 *
 */
public abstract class Dispatcher {
	protected IListener[] listenerArray;
	protected IFilter[] filterArray;

	/**
	 * Dispatches the event to all registered listeners that use that event.
	 * More information about the default dispatcher can be found at {@link Dispatcher#dispatch()}.
	 * <p>
	 * {@link Dispatcher#listenerArray} and {@link Dispatcher#filterArray} need to be populated
	 * with the arrays from {@link Dispatcher#init(IListener[], IFilter[])} in order for the default dispatching
	 * (see {@link Dispatcher#dispatch()}) to work.
	 * <p>
	 * The arrays {@link Dispatcher#listenerArray} and {@link Dispatcher#filterArray} are ordered. 
	 * These two arrays must stay in the same order if the default dispatching 
	 * (see {@link Dispatcher#dispatch()}) method is used.
	 * @param event {@link Event}
	 * @return {@link Event}
	 */
	public abstract <T extends Event> T dispatchEvent(T event);

	/**
	 * Initializes the dispatcher with all available listeners and filters.
	 * <p>
	 * {@link Dispatcher#listenerArray} and {@link Dispatcher#filterArray} need to be populated
	 * with the arrays from {@link Dispatcher#init(IListener[], IFilter[])} in order for the default dispatching
	 * (see {@link Dispatcher#dispatch()}) to work.
	 * <p>
	 * The arrays {@link Dispatcher#listenerArray} and {@link Dispatcher#filterArray} are ordered. 
	 * These two arrays must stay in the same order if the default dispatching 
	 * (see {@link Dispatcher#dispatch()}) method is used.
	 * @param listenerArray {@link IListener}[]
	 * @param filterArray {@link IFilter}[]
	 */
	public void init(IListener[] listenerArray, IFilter[] filterArray) {
		this.listenerArray = listenerArray;
		this.filterArray = filterArray;
	}

	/**
	 * This method can only be used in {@link Dispatcher#dispatchEvent(Event)} 
	 * or in aforementioned method overridden by a subclass of {@link Dispatcher}.
	 * A {@link DispatcherException} is thrown if used by any other method.
	 * <p>
	 * Implements the default dispatching code when
	 * the dispatcher gets compiled by the {@link DRCEventBus}.
	 * This line of code will be replaced by the dispatching code
	 * during the compilation. The dispatching code can only be implemented
	 * once per method. If this method is used multiple times by a single method 
	 * a {@link DispatcherException} is thrown.
	 * <p>
	 * {@link Dispatcher#listenerArray} and {@link Dispatcher#filterArray} need to be populated
	 * with the arrays from {@link Dispatcher#init(IListener[], IFilter[])} in order for the default dispatching to work.
	 * <p>
	 * The arrays {@link Dispatcher#listenerArray} and {@link Dispatcher#filterArray} are ordered. 
	 * These two arrays must stay in the same order if the default dispatching method is used.
	 */
	protected static final void dispatch() {
		throw new DispatcherException("This method cannot be used outside of Dispatcher#dispatchEvent(IEvent) or aforementioned method overridden by a subclass of Dispatcher");
	}
}
