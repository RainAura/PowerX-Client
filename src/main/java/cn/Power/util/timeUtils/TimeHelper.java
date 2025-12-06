package cn.Power.util.timeUtils;

public class TimeHelper {
	public long lastMs;

	public TimeHelper() {
		this.lastMs = 0L;
	}

	public boolean isDelayComplete(long delay) {
			return System.currentTimeMillis() - this.lastMs > delay;
	}

	public long getCurrentMS() {
		return System.nanoTime() / 1000000L;
	}

	public void reset() {
		this.lastMs = System.currentTimeMillis();
	}
	
	public void resetMS() {
		this.lastMs = System.nanoTime() / 1000000L;
	}

	public long getLastMs() {
		return this.lastMs;
	}

	public void setLastMs(int i) {
		this.lastMs = System.currentTimeMillis() + (long) i;
	}

	public boolean hasReached(long milliseconds) {
		return this.getCurrentMS() - this.lastMs >= milliseconds;
	}

	public boolean hasReached(float timeLeft) {
		return (float) (this.getCurrentMS() - this.lastMs) >= timeLeft;
	}

	public boolean delay(double nextDelay) {
		return System.currentTimeMillis() - lastMs >= nextDelay;
	}
}
