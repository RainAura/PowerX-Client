package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

public class EventRender implements Event {
	public float partialTicks;

	public EventRender(float a) {
		this.partialTicks = a;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	public void setPartialTicks(float partialTicks) {
		this.partialTicks = partialTicks;
	}
}
