package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

public class EventJump implements Event {
	private double motionY;
	private boolean pre;
	protected boolean cancelled;

	public void fire(double motionY, boolean pre) {
		this.motionY = motionY;
		this.pre = pre;
	}

	public double getMotionY() {
		return motionY;
	}

	public void setMotionY(double motiony) {
		this.motionY = motiony;
	}

	public boolean isPre() {
		return pre;
	}

	public boolean isPost() {
		return !pre;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
	}

}
