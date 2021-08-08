package tcb.bces.listener.filter;

import tcb.bces.bus.MethodContext;
import tcb.bces.event.Event;
import tcb.bces.listener.Subscribe;

/**
 * An abstract filter class
 * 
 * @author TCB
 *
 */
public abstract class Filter implements IFilter {
	private MethodContext methodContext;

	/**
	 * A no-arg constructor must be present if this filter is set through
	 * {@link Subscribe#filter()}.
	 */
	private Filter() {}

	/**
	 * Returns the method context that uses this filter.
	 * @return {@link MethodContext}
	 */
	public MethodContext getMethodContext() {
		return this.methodContext;
	}

	/**
	 * This method is called after the IFilter constructor has
	 * been called. Only called if the filter has been set through
	 * {@link Subscribe#filter()}.
	 */
	protected void init() { }

	@Override
	public final void init(MethodContext entry) {
		this.methodContext = entry;
		this.init();
	}

	@Override
	public abstract boolean filter(Event event);
}
