package tcb.bces.bus.async.feedback;

import tcb.bces.event.Event;

/**
 * An abstract feedback handler class
 * 
 * @author TCB
 *
 */
public abstract class FeedbackHandler implements IFeedbackHandler {
	@Override
	public abstract void handleFeedback(Event event);
}
