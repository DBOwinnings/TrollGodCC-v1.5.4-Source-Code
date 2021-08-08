package tcb.bces.listener.filter;

import tcb.bces.bus.MethodContext;
import tcb.bces.event.Event;
import tcb.bces.listener.Subscribe;

/**
 * This interface must be implemented in order to create a functional and valid filter.
 * 
 * @author TCB
 *
 */
public interface IFilter {
	/**
	 * This method is called after the {@link IFilter} no-arg constructor has
	 * been called. Only called if the filter has been set through
	 * {@link Subscribe#filter()}. The {@link MethodContext} parameter can be used
	 * to retrieve the listener object.
	 * @param context {@link MethodContext}
	 */
	public void init(MethodContext context);

	/**
	 * This method filters the incoming {@link Event}.
	 * Return false to cancel the event for the specific
	 * listening method this filter belongs to. 
	 * This does not cancel the event itself, it just prevents
	 * the event from being received by the listening method
	 * this filter belongs to.
	 * @param event {@link Event}
	 * @return boolean
	 */
	public boolean filter(Event event);
}
