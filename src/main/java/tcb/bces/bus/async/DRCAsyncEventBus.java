package tcb.bces.bus.async;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import tcb.bces.bus.DRCEventBus;
import tcb.bces.bus.DRCExpander;
import tcb.bces.bus.IEventBus;
import tcb.bces.bus.MethodContext;
import tcb.bces.bus.async.feedback.IFeedbackHandler;
import tcb.bces.event.Event;
import tcb.bces.event.IEventCancellable;

/**
 * This event bus allows asynchronous event posting. It still has all the features of
 * {@link DRCEventBus}, but the events are dispatched asynchronously. Posting an event adds
 * it to a queue which is dispatched by the specified amount of dispatcher threads.
 * The event post result can be retrieved by setting a custom {@link IFeedbackHandler}.
 * If {@link DRCExpander} is used to wrap this bus, the feedback handler has to be set
 * before this bus is wrapped in order for it to work correctly.
 * This event bus should be used when the listeners take a long time to process the received
 * events.
 * 
 * @author TCB
 *
 */
public class DRCAsyncEventBus extends DRCEventBus {
	protected final BlockingQueue<Event> eventQueue = new LinkedBlockingDeque<Event>();
	protected final BlockingQueue<IEventCancellable> eventQueueCancellable = new LinkedBlockingDeque<IEventCancellable>();
	protected IFeedbackHandler feedbackHandler = null;
	private final ArrayList<DispatcherThread> dispatchers = new ArrayList<DispatcherThread>();
	private final ArrayList<DispatcherThread> sleepers = new ArrayList<DispatcherThread>();
	private final int cthreads;
	private final boolean manualDispatcherManagement;
	protected final Object eventLock = new Object();
	protected final Object eventCancellableLock = new Object();

	/**
	 * The default asynchronous event bus has a limit of {@link DRCEventBus#MAX_METHODS} listening methods. 
	 * If you want to add more listening methods use {@link EventBusManager} instead.
	 * The amount of dispatchers/threads can be specified.
	 * The dispatchers will go to sleep automatically after {@link DispatcherThread#THREAD_SLEEP_DELAY} and
	 * they will be notified if an event is posted.
	 * @param threads Integer
	 */
	public DRCAsyncEventBus(int threads) {
		this.cthreads = threads;
		this.manualDispatcherManagement = false;
		for(int i = 0; i < threads; i++) {
			DispatcherThread dispatcher = new DispatcherThread(this, new DRCEventBus());
			this.dispatchers.add(dispatcher);
		}
	}

	/**
	 * The default asynchronous event bus has a limit of {@link DRCEventBus#MAX_METHODS} listening methods. 
	 * If you want to add more listening methods use {@link EventBusManager} instead.
	 * The amount of dispatchers/threads can be specified. The dispatchers can be managed
	 * manually by setting manualDispatcherManagement to true. Dispatchers don't go to
	 * sleep if management is set to manual and won't be notified if an event is posted.
	 * @param threads Integer
	 * @param manualDispatcherManagement Boolean
	 */
	public DRCAsyncEventBus(int threads, boolean manualDispatcherManagement) {
		this.cthreads = threads;
		this.manualDispatcherManagement = manualDispatcherManagement;
		for(int i = 0; i < threads; i++) {
			DispatcherThread dispatcher = new DispatcherThread(this, new DRCEventBus());
			this.dispatchers.add(dispatcher);
		}
	}

	/**
	 * Only used for {@link IEventBus#copyBus()}.
	 * Shares the threads with the parent bus.
	 * @param threads Integer
	 * @param dispatchers ArrayList<Dispatcher>
	 * @param sleepers ArrayList<Dispatcher>
	 */
	private DRCAsyncEventBus(int threads, boolean manualDispatcherManagement, IFeedbackHandler feedbackHandler) {
		this.cthreads = threads;
		this.manualDispatcherManagement = manualDispatcherManagement;
		this.feedbackHandler = feedbackHandler;
		for(int i = 0; i < threads; i++) {
			DispatcherThread dispatcher = new DispatcherThread(this, new DRCEventBus());
			this.dispatchers.add(dispatcher);
		}
	}

	/**
	 * Returns the feedback handler of this bus.
	 * @return IFeedbackHandler
	 */
	public final IFeedbackHandler getFeedbackHandler() {
		return this.feedbackHandler;
	}

	/**
	 * Sets the feedback handler of this bus.
	 * @param feedbackHandler IFeedbackHandler
	 * @return AsyncEventBus
	 */
	public final DRCAsyncEventBus setFeedbackHandler(IFeedbackHandler feedbackHandler) {
		this.feedbackHandler = feedbackHandler;
		return this;
	}

	/**
	 * Returns true if management is set to manual.
	 * @return Boolean
	 */
	public final boolean hasManualManagement() {
		return this.manualDispatcherManagement;
	}

	/**
	 * Returns a read-only list of the current dispatchers.
	 * @return List<Dispatcher> read-only
	 */
	public final List<DispatcherThread> getDispatchers() {
		return Collections.unmodifiableList(this.dispatchers);
	}

	/**
	 * Used in {@link DispatcherThread}.
	 * Adds a dispatcher to the sleeper list.
	 * @param dispatcher Dispatcher
	 */
	protected final void addToSleepers(DispatcherThread dispatcher) {
		synchronized(this) {
			this.sleepers.add(dispatcher);
		}
	}

	/**
	 * Used in {@link DispatcherThread}.
	 * Removes a dispatcher from the sleeper list.
	 * @param dispatcher Dispatcher
	 */
	protected synchronized final void removeFromSleepers(DispatcherThread dispatcher) {
		synchronized(this) {
			if(this.sleepers.contains(dispatcher)) {
				this.sleepers.remove(dispatcher);
			}
		}
	}

	/**
	 * Used in {@link DispatcherThread}.
	 * Returns the read-only event queue.
	 * @return Collection<IEvent> read-only
	 */
	public final Collection<Event> getEventQueue() {
		return Collections.unmodifiableCollection(this.eventQueue);
	}

	/**
	 * Used in {@link DispatcherThread}.
	 * Returns the read-only cancellable event queue.
	 * @return Collection<IEventCancellable> read-only
	 */
	public final Collection<IEventCancellable> getEventQueueCancellable() {
		return Collections.unmodifiableCollection(this.eventQueueCancellable);
	}

	/**
	 * Clears the event queue.
	 */
	public final void clearEventQueue() {
		this.eventQueue.clear();
	}

	/**
	 * Clears the cancellable event queue.
	 */
	public final void clearEventQueueCancellable() {
		this.eventQueueCancellable.clear();
	}

	@Override
	public <T extends Event> T post(T event) {
		try {
			this.eventQueue.put(event);
			if(!this.manualDispatcherManagement && this.sleepers.size() > 0) {
				for(DispatcherThread dispatcher : this.sleepers) {
					dispatcher.setSleeping(false);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return event;
	}

	/**
	 * Only used internally in {@link DispatcherThread}
	 * @param event
	 * @return
	 */
	protected final <T extends Event> T postEventS(T event) {
		return super.post(event);
	}

	/**
	 * Starts all dispatchers. Required for the events in
	 * the event queue to be dispatched.
	 */
	public final void startDispatchers() {
		try {
			for(DispatcherThread dispatcher : this.dispatchers) {
				if(!dispatcher.isRunning() && dispatcher.getState() != State.TERMINATED) {
					dispatcher.startDispatcher();
				}
			}
			int threadsLeft = this.cthreads - this.dispatchers.size();
			for(int i = 0; i < threadsLeft; i++) {
				DispatcherThread dispatcher = new DispatcherThread(this, new DRCEventBus());
				dispatcher.startDispatcher();
				this.dispatchers.add(dispatcher);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Stops all dispatchers.
	 */
	public final void stopDispatchers() {
		try {
			synchronized(this) {
				for(DispatcherThread dispatcher : this.dispatchers) {
					dispatcher.stopDispatcher();
				}
				this.dispatchers.clear();
				this.sleepers.clear();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void bind() {
		for(DispatcherThread dispatcher : this.dispatchers) {
			dispatcher.getDispatcherBus().clear();
			for(MethodContext me : this.getMethodEntries()) {
				dispatcher.getDispatcherBus().register(me);
			}
			dispatcher.getDispatcherBus().bind();
		}
	}

	@Override
	public IEventBus copyBus() {
		return new DRCAsyncEventBus(this.cthreads, this.manualDispatcherManagement, this.feedbackHandler);
	}
}
