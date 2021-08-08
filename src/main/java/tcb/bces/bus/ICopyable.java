package tcb.bces.bus;

/**
 * Any event bus that can be copied implements this interface
 * 
 * @author TCB
 *
 */
interface ICopyable {
	/**
	 * Returns a new instance of this {@link DRCEventBus} with the same
	 * properties.
	 * Used in {@link DRCExpander} to create copies of
	 * the given bus.
	 * @return {@link IEventBus}
	 */
	public IEventBus copyBus();
}
