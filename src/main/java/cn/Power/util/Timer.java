package cn.Power.util;

public final class Timer {
	public static float timerSpeed;
	private long last;

	private long time;
	private long prevMS = 0L;

	public Timer() {
		this.time = System.nanoTime() / 1000000L;
	}

	public boolean delay(float milliSec) {
		return (float) (this.getTime() - this.prevMS) >= milliSec;
	}

	public long getTime() {
		return System.nanoTime() / 1000000;
	}

	public boolean hasTimeElapsed(final long time, final boolean reset) {
		if (this.time() >= time) {
			if (reset)
				this.reset();
			return true;
		}
		return false;
	}

	public boolean hasTimeElapsed(final long time) {
		if (this.time() >= time)
			return true;
		return false;
	}

	public boolean hasTicksElapsed(int ticks) {
		if (this.time() >= (1000 / ticks) - 50)
			return true;
		return false;
	}

	public boolean hasTicksElapsed(int time, int ticks) {
		if (this.time() >= (time / ticks) - 50)
			return true;
		return false;
	}

	public long time() {
		return System.nanoTime() / 1000000L - this.time;
	}

	public void resetsig() {
		this.prevMS = this.getTime();
	}

	public void reset() {
		this.prevMS = this.getTime();
	}

	public long getDifference() {
		return this.getTime() - this.prevMS;
	}

	public void setDifference(long difference) {
		this.prevMS = this.getTime() - difference;
	}

	public boolean hasPassed(double milli) {
		return (double) (this.getTime() - this.last) >= milli;
	}

	public long getElapsedTime() {
		return this.getTime() - this.last;
	}

	public void updateLastTime() {
		this.last = this.getTime();
	}
}
