package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

public class EventPushBlock implements Event {
	private boolean cancelled;

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
