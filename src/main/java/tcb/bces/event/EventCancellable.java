package tcb.bces.event;


/**
 * An abstract cancellable event class
 * 
 * @author TCB
 *
 */
public abstract class EventCancellable extends Event implements IEventCancellable {
	private boolean isCancelled = false;

	@Override
	public void setCancelled() {
		this.isCancelled = true;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}
}