package cn.Power.util.misc;

public class SigmaTimer {
	private long prevMS;

	public SigmaTimer() {
		this.prevMS = 0L;
	}

	public boolean delay(float milliSec) {
		return (float) (getTime() - this.prevMS) >= milliSec;
	}

	public void reset() {
		this.prevMS = getTime();
	}

	public long getTime() {
		return System.nanoTime() / 1000000L;
	}

	public long getDifference() {
		return getTime() - this.prevMS;
	}

	public void setDifference(long difference) {
		this.prevMS = (getTime() - difference);
	}

	public boolean check(float milliseconds) {
		return getTime() >= milliseconds;
	}

}
