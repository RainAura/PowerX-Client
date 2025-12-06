package cn.Power.util;

public class GameTimer {

	/**
	 * The time when the timer was created.
	 */
	private long now = System.currentTimeMillis();

	/**
	 * If the timer has passed the delay.
	 *
	 * @param delay delay to be checked.
	 * @return if the delay has been reached.
	 */
	public boolean reached(double delay) {
		return System.currentTimeMillis() - now >= delay;
	}

	public boolean reached(int delay) {
		return System.currentTimeMillis() - now >= delay;
	}

	/**
	 * Resets the timer.
	 */
	public void reset() {
		now = System.currentTimeMillis();
	}

	/**
	 * Returns the time passed since reset or creation of the timer.
	 *
	 * @return time passed.
	 */
	public long getTimePassed() {
		return System.currentTimeMillis() - now;
	}

}
