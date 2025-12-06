package cn.Power.util.animations.easings;

import cn.Power.util.animations.Easing;

public class Sine extends Easing {

	public static final double easeIn(double progression, double startValue, double endValue, double duration) {
		return -endValue * (double) Math.cos(progression / duration * (Math.PI / 2)) + endValue + startValue;
	}

	public static final double easeOut(double progression, double startValue, double endValue, double duration) {
		return endValue * (double) Math.sin(progression / duration * (Math.PI / 2)) + startValue;
	}

	public static final double easeInOut(double progression, double startValue, double endValue, double duration) {
		return -endValue / 2 * ((double) Math.cos(Math.PI * progression / duration) - 1) + startValue;
	}

}