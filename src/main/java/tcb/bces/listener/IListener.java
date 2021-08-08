package tcb.bces.listener;

/**
 * This interface must be implemented in order to create a functional and valid listener.
 * 
 * @author TCB
 *
 */
public interface IListener {
	/**
	 * Returns whether this listener should receive events.
	 * @return Boolean
	 */
	boolean isListening();
}