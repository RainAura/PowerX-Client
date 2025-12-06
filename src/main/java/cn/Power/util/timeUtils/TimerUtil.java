package cn.Power.util.timeUtils;

public final class TimerUtil {
	public double time;
	private long lastMS;

	public TimerUtil() {
		this.time = (System.nanoTime() / 1000000l);
	}

	public boolean hasTimeElapsed(double time, boolean reset) {
		if (getTime() >= time) {
			if (reset) {
				reset();
			}

			return true;
		}

		return false;
	}

	public double getTime() {
		return System.nanoTime() / 1000000l - this.time;
	}

	public void setTime(double time) {
		this.time = System.nanoTime() / 1000000l - time;
	}

	
	public void reset() {
		this.time = (System.nanoTime() / 1000000l);
	}

	private long getCurrentMS() {
		return System.nanoTime() / 1000000L;
	}

	public boolean hasReached(double milliseconds) {
		return (double) (this.getCurrentMS() - this.lastMS) >= milliseconds;
	}

}