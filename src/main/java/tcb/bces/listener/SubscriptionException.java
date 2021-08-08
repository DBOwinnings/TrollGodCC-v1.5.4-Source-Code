package tcb.bces.listener;

/**
 * This exception is thrown if something during the registration of a listener or
 * a subscribed method goes wrong.
 * 
 * @author TCB
 *
 */
public class SubscriptionException extends RuntimeException {
	public SubscriptionException(String msg) {
		super(msg);
	}
	public SubscriptionException(String msg, Exception cause) {
		super(msg, cause);
	}
	private static final long serialVersionUID = 1L;
}
