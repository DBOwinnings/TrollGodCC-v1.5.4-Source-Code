package tcb.bces.bus.async.feedback;

import tcb.bces.bus.async.DRCAsyncEventBus;
import tcb.bces.event.Event;

/**
 * This feedback handler makes it possible to retrieve the result of an event post posted
 * by an {@link DRCAsyncEventBus}.
 * 
 * @author TCB
 *
 */
public interface IFeedbackHandler {
	/**
	 * This method is called after an event has been posted asynchronously by
	 * an {@link DRCAsyncEventBus}.
	 * @param event IEvent
	 */
	public void handleFeedback(Event event);
}
