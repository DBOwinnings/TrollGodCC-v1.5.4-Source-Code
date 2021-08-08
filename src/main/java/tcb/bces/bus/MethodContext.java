package tcb.bces.bus;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import tcb.bces.event.Event;
import tcb.bces.listener.IListener;
import tcb.bces.listener.Subscribe;
import tcb.bces.listener.SubscriptionException;
import tcb.bces.listener.filter.IFilter;

/**
 * The {@link MethodContext} holds information about a registered listener
 * and it's listening {@link Method}.
 */
public final class MethodContext {
	private static final String IFILTER_INIT = "init";

	private final Class<? extends Event> eventClass;
	private final IListener listener;
	private final Method method;
	private final Subscribe handlerAnnotation;
	private IFilter filter;

	/**
	 * The {@link MethodContext} holds information about a registered listener
	 * and it's listening {@link Method}.
	 * @param eventClass {@link Class}
	 * @param listener {@link IListener}
	 * @param method {@link Method}
	 * @param handlerAnnotation {@link Subscribe}
	 */
	private MethodContext(Class<? extends Event> eventClass, IListener listener, Method method, Subscribe handlerAnnotation) {
		this.eventClass = eventClass;
		this.listener = listener;
		this.method = method;
		this.handlerAnnotation = handlerAnnotation;
	}

	/**
	 * Returns the event type of this {@link MethodContext}.
	 * @return {@link Event}
	 */
	public Class<? extends Event> getEventClass() {
		return this.eventClass;
	}

	/**
	 * Returns the {@link IListener} instance.
	 * @return {@link IListener}
	 */
	public IListener getListener() {
		return this.listener;
	}

	/**
	 * Returns the {@link Method} this {@link MethodContext} belongs to.
	 * @return {@link Method}
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * Returns the {@link Subscribe} annotation that belongs to this
	 * {@link MethodContext}.
	 * @return {@link Subscribe}
	 */
	public Subscribe getHandlerAnnotation() {
		return this.handlerAnnotation;
	}

	/**
	 * Returns the {@link IFilter} that has been assigned to this {@link MethodContext}.
	 * @return {@link IFilter}
	 */
	public IFilter getFilter() {
		return this.filter;
	}

	/**
	 * Used to set a custom filter. If this method is used to set
	 * the filter instead of the {@link Subscribe#filter()} annotation member, a custom constructor
	 * can be used in the filter class. 
	 * <p>
	 * The method {@link IFilter#init(IListener)} will not be called if
	 * the filter was set via this method.
	 * @param filter {@link IFilter}
	 * @return {@link MethodContext}
	 */
	public MethodContext setFilter(IFilter filter) {
		this.filter = filter;
		return this;
	}

	/**
	 * Invokes the listening method by reflection if the filter test is passed.
	 * @param event {@link Event}
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void invoke(Event event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(this.filter != null && !this.filter.filter(event)) return;
		this.method.invoke(this.listener, event);
	}

	/**
	 * This method sets the {@link IFilter} of the {@link MethodContext} that was specified
	 * with the {@link Subscribe#filter()} annotation member.
	 * <p>
	 * Throws a {@link SubscriptionException} if the filter class is abstract or interface
	 * or doesn't have a no-arg constructor.
	 * @param context {@link MethodContext} to add the filter to
	 * @throws SubscriptionException
	 * @return {@link MethodContext}
	 */
	public final MethodContext initFilter() throws SubscriptionException {
		if(this.getFilter() != null) {
			return this;
		}
		Class<? extends IFilter> filterClass = this.handlerAnnotation.filter();
		if(filterClass == IFilter.class) {
			return this;
		}
		int classModifiers = filterClass.getModifiers();
		if((classModifiers & Modifier.ABSTRACT) != 0 ||
				(classModifiers & Modifier.INTERFACE) != 0) {
			throw new SubscriptionException("Filter class must not be abstract or interface: " + filterClass.getName());
		}
		if((classModifiers & Modifier.PUBLIC) == 0) {
			throw new SubscriptionException("Filter class must be public: " + filterClass.getName());
		}
		try {
			IFilter instance = null;
			Constructor<? extends IFilter> ctor = filterClass.getDeclaredConstructor();
			boolean accessible = ctor.isAccessible();
			ctor.setAccessible(true);
			instance = ctor.newInstance();
			if(!accessible) ctor.setAccessible(false);
			this.setFilter(instance);
			Method initMethod = filterClass.getDeclaredMethod(IFILTER_INIT, new Class[]{MethodContext.class});
			initMethod.invoke(instance, this);
		} catch(Exception ex) {
			throw new SubscriptionException("Failed to initialize filter: " + filterClass.getName(), ex);
		}
		return this;
	}
	
	/**
	 * Verifies the specified listener and returns a list of all found valid method contexts.
	 * A SubscriptionException is thrown if an invalid method has been found.
	 * @param listener {@link IListener}
	 * @throws SubscriptionException
	 * @return {@link List} of all found valid method contexts
	 */
	public static final List<MethodContext> getMethodContexts(IListener listener) throws SubscriptionException {
		List<MethodContext> entryList = new ArrayList<MethodContext>();
		Method[] listenerMethods = listener.getClass().getDeclaredMethods();
		for(Method method : listenerMethods) {
			if(method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) continue;
			Subscribe handlerAnnotation = method.getAnnotation(Subscribe.class);
			if(handlerAnnotation != null) {
				int methodModifiers = method.getModifiers();
				if((methodModifiers & Modifier.STATIC) != 0 ||
						(methodModifiers & Modifier.ABSTRACT) != 0 ||
						(methodModifiers & Modifier.PRIVATE) != 0 ||
						(methodModifiers & Modifier.PROTECTED) != 0) {
					throw new SubscriptionException("Invalid method modifiers for method " + listener.getClass().getName() + "#" + method.getName());
				}
				if(method.getReturnType() != void.class) {
					throw new SubscriptionException("Return type is not void for method " + listener.getClass().getName() + "#" + method.getName());
				}
				@SuppressWarnings("unchecked")
				Class<? extends Event> paramType = (Class<? extends Event>) method.getParameterTypes()[0];
				if(paramType.isInterface()) {
					throw new SubscriptionException("Parameter for method cannot be an interface: " + listener.getClass().getName() + "#" + method.getName());
				}
				entryList.add(new MethodContext(paramType, listener, method, handlerAnnotation).initFilter());
			}
		}
		return entryList;
	}
}
