package com.darkmagician6.eventapi;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.events.EventStoppable;
import com.darkmagician6.eventapi.types.Priority;
import com.sun.jna.platform.unix.X11.XClientMessageEvent.Data;

/**
 * 
 * 
 * @author DarkMagician6
 * @since February 2, 2014
 */
public final class EventManager {

	/**
	 * HashMap containing all the registered MethodData sorted on the event
	 * parameters of the methods.
	 */
	private static final HashMap<Class<? extends Event>, List<MethodData>> REGISTRY_MAP = new HashMap<>();

	/**
	 * All methods in this class are static so there would be no reason to create an
	 * object of the EventManager class.
	 */
	private EventManager() {
	}

	/**
	 * Registers all the methods marked with the EventTarget annotation in the class
	 * of the given Object.
	 *
	 * @param object Object that you want to register.
	 * @throws IllegalAccessException
	 */
	public static void register(final Object object) {
		for (final Method method : object.getClass().getDeclaredMethods()) {
			if (!isMethodBad(method)) {
				register(method, object);
			}
		}
	}

	/**
	 * Registers the methods marked with the EventTarget annotation and that require
	 * the specified Event as the parameter in the class of the given Object.
	 *
	 * @param object     Object that contains the Method you want to register.
	 * @param eventClass class for the marked method we are looking for.
	 * @throws IllegalAccessException
	 */
	public static void register(final Object object, Class<? extends Event> eventClass) {
		for (final Method method : object.getClass().getDeclaredMethods()) {
			if (!isMethodBad(method, eventClass)) {
				register(method, object);
			}
		}
	}

	/**
	 * Unregisters all the methods inside the Object that are marked with the
	 * EventTarget annotation.
	 *
	 * @param object Object of which you want to unregister all Methods.
	 */
	public static void unregister(final Object object) {

		try {

			lab: {

				REGISTRY_MAP.values().stream().parallel().forEach(p -> p.removeIf(k -> {
					return k.getSource().equals(object);
				}));
			}

		} catch (Throwable c) {
			c.printStackTrace();
		}
	}

	/**
	 * Unregisters all the methods in the given Object that have the specified class
	 * as a parameter.
	 *
	 * @param object     Object that implements the Listener interface.
	 * @param eventClass class for the method to remove.
	 */
	public static void unregister(final Object object, final Class<? extends Event> eventClass) {
		if (REGISTRY_MAP.containsKey(eventClass)) {

			REGISTRY_MAP.values().stream().parallel().forEach(p -> p.removeIf(k -> {
				return k.getSource().equals(object);
			}));
		}
	}

	/**
	 * Registers a new MethodData to the HashMap. If the HashMap already contains
	 * the key of the Method's first argument it will add a new MethodData to key's
	 * matching list and sorts it based on Priority. @see
	 * com.darkmagician6.eventapi.types.Priority Otherwise it will put a new entry
	 * in the HashMap with a the first argument's class and a new
	 * CopyOnWriteArrayList containing the new MethodData.
	 *
	 * @param method Method to register to the HashMap.
	 * @param object Source object of the method.
	 * @throws IllegalAccessException
	 */
	private static void register(final Method method, final Object object) {
		Class<? extends Event> indexClass = (Class<? extends Event>) method.getParameterTypes()[0];
		// New MethodData from the Method we are registering.

		// Set's the method to accessible so that we can also invoke it if it's
		// protected or private.
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}

		final Lookup lookup = MethodHandles.lookup();

		MethodHandle reflect = null;
		try {
			reflect = lookup.unreflect(method);
		} catch (IllegalAccessException e) {

			e.printStackTrace();

			return;
		}

		final MethodData data = new MethodData(object, reflect, method.getAnnotation(EventTarget.class).value());

		if (REGISTRY_MAP.containsKey(indexClass)) {
			if (!REGISTRY_MAP.get(indexClass).contains(data)) {
				REGISTRY_MAP.get(indexClass).add(data);
				sortListValue(indexClass);
			}
		} else {
			REGISTRY_MAP.put(indexClass, new CopyOnWriteArrayList<MethodData>() {

				private static final long serialVersionUID = 66L;
				{
					add(data);
				}
			});
		}
	}

	/**
	 * Removes an entry based on the key value in the map.
	 *
	 * @param indexClass They index key in the map of which the entry should be
	 *                   removed.
	 */
	public static void removeEntry(final Class<? extends Event> indexClass) {
		Iterator<Map.Entry<Class<? extends Event>, List<MethodData>>> mapIterator = REGISTRY_MAP.entrySet().iterator();

		while (mapIterator.hasNext()) {
			if (mapIterator.next().getKey().equals(indexClass)) {
				mapIterator.remove();
				break;
			}
		}
	}

	/**
	 * Sorts the List that matches the corresponding Event class based on priority
	 * value.
	 *
	 * @param indexClass The Event class index in the HashMap of the List to sort.
	 */
	private static void sortListValue(final Class<? extends Event> indexClass) {
		final List<MethodData> sortedList = new CopyOnWriteArrayList<>();

		for (final byte priority : Priority.VALUE_ARRAY) {
			for (final MethodData data : REGISTRY_MAP.get(indexClass)) {
				if (data.getPriority() == priority) {
					sortedList.add(data);
				}
			}
		}

		// Overwriting the existing entry.
		REGISTRY_MAP.put(indexClass, sortedList);
	}

	/**
	 * Checks if the method does not meet the requirements to be used to receive
	 * event calls from the Dispatcher. Performed checks: Checks if the parameter
	 * length is not 1 and if the EventTarget annotation is not present.
	 *
	 * @param method Method to check.
	 *
	 * @return True if the method should not be used for receiving event calls from
	 *         the Dispatcher.
	 *
	 * @see com.darkmagician6.eventapi.EventTarget
	 */
	private static boolean isMethodBad(final Method method) {
		return method.getParameterTypes().length < 1 || !method.isAnnotationPresent(EventTarget.class);
	}

	/**
	 * Checks if the method does not meet the requirements to be used to receive
	 * event calls from the Dispatcher. Performed checks: Checks if the parameter
	 * class of the method is the same as the event we want to receive.
	 *
	 * @param method     Method to check.
	 * @param eventClass of the Event we want to find a method for receiving it.
	 *
	 * @return True if the method should not be used for receiving event calls from
	 *         the Dispatcher.
	 *
	 * @see com.darkmagician6.eventapi.EventTarget
	 */
	private static boolean isMethodBad(final Method method, Class<? extends Event> eventClass) {
		return isMethodBad(method) || !method.getParameterTypes()[0].equals(eventClass);
	}

	/**
	 * Call's an event and invokes the right methods that are listening to the event
	 * call. First get's the matching list from the registry map based on the class
	 * of the event. Then it checks if the list is not null. After that it will
	 * check if the event is an instance of EventStoppable and if so it will add an
	 * extra check when looping trough the data. If the Event was an instance of
	 * EventStoppable it will check every loop if the EventStoppable is stopped, and
	 * if it is it will break the loop, thus stopping the call. For every MethodData
	 * in the list it will invoke the Data's method with the Event as the argument.
	 * After that is all done it will return the Event.
	 *
	 * @param event Event to dispatch.
	 *
	 * @return Event in the state after dispatching it.
	 */
	public static Event call(final Event event) {
		final List<MethodData> dataList = REGISTRY_MAP.get(event.getClass());

		if (dataList != null) {
			if (event instanceof EventStoppable) {
				EventStoppable stoppable = (EventStoppable) event;


				
				for (Iterator<MethodData> i = dataList.iterator(); i.hasNext(); ) { 
				    MethodData data = i.next(); 
				    
				    invoke(data, event);

					if (stoppable.isStopped()) {
						break;
					}
				}
			} else {

				
				for (Iterator<MethodData> i = dataList.iterator(); i.hasNext(); ) { 
				    MethodData data = i.next(); 
				    
				    try {
						invoke(data, event);

					} catch (Throwable c) {
						c.printStackTrace();
					}
				}
			}
		}

		return event;
	}

	/**
	 * Invokes a MethodData when an Event call is made.
	 *
	 * @param data     The data of which the targeted Method should be invoked.
	 * @param argument The called Event which should be used as an argument for the
	 *                 targeted Method.
	 *
	 */
	private static void invoke(final MethodData data, final Event argument) {
		try {
			data.getTarget().invoke(data.getSource(), argument);
		} catch (Throwable e) {
//			e.printStackTrace();
//			System.out.println(data.getSource().toString() + " | " + data.target.toString() + " | " + e.getCause()
//					+ " | " + e.toString() + " | " + e.getMessage());
		}
	}

	/**
	 * 
	 * @author DarkMagician6
	 * @since January 2, 2014
	 */
	private static final class MethodData {

		private final Object source;

		private final MethodHandle target;

		private final byte priority;

		/**
		 * Sets the values of the data.
		 *
		 * @param source   The source Object of the data. Used by the VM to determine to
		 *                 which object it should send the call to.
		 * @param target   The targeted Method to which the Event should be send to.
		 * @param priority The priority of this Method. Used by the registry to sort the
		 *                 data on.
		 */
		public MethodData(Object source, MethodHandle target, byte priority) {
			this.source = source;
			this.target = target;
			this.priority = priority;
		}

		/**
		 * Gets the source Object of the data.
		 *
		 * @return Source Object of the targeted Method.
		 */
		public Object getSource() {
			return source;
		}

		/**
		 * Gets the targeted Method.
		 *
		 * @return The Method that is listening to certain Event calls.
		 */
		public MethodHandle getTarget() {
			return target;
		}

		/**
		 * Gets the priority value of the targeted Method.
		 *
		 * @return The priority value of the targeted Method.
		 */
		public byte getPriority() {
			return priority;
		}

	}

}
