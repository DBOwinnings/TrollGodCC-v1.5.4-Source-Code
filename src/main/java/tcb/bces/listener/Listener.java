package tcb.bces.listener;


/**
 * An abstract listener class that implements {@link IListener}.
 * 
 * @author TCB
 *
 */
public abstract class Listener implements IListener {
	/**
	 * Returns whether this listener should receive events.
	 * True by default.
	 * @return Boolean
	 */
	@Override
	public boolean isListening() {
		return true;
	}
}