package tcb.bces.event;

public interface IEventCancellable extends IEvent {
	/**
	 * Sets the cancelled state of this event to true
	 */
	public void setCancelled();

	/**
	 * Sets the cancelled state of this event
	 * @param cancelled the cancelled state
	 */
	public void setCancelled(boolean cancelled);

	/**
	 * Returns whether this event is cancelled
	 * @return cancelled
	 */
	public boolean isCancelled();

}
