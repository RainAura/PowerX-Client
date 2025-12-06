package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

public class EventKeyboard implements Event {

	private int i;

	public EventKeyboard(int i) {
		this.i = i;
	}

	public int getKey() {
		return this.i;
	}

}
