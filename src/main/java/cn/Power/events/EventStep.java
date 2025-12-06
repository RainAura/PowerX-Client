package cn.Power.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

public class EventStep extends EventCancellable {
	private double stepHeight;
	private double realHeight;
	private boolean active;
	private boolean pre;

	public EventStep(boolean state, double stepHeight) {
		this.pre = state;
		this.realHeight = stepHeight;
		this.stepHeight = stepHeight;
	}

	public boolean isPre() {
		return pre;
	}

	public double getStepHeight() {
		return this.stepHeight;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setStepHeight(double stepHeight) {
		this.stepHeight = stepHeight;
	}

	public void setActive(boolean bypass) {
		this.active = bypass;
	}

	public double getRealHeight() {
		return this.realHeight;
	}

	public void setRealHeight(double realHeight) {
		this.realHeight = realHeight;
	}

}
