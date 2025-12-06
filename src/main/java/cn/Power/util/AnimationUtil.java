package cn.Power.util;

public class AnimationUtil {
	public static float delta;
	private static float defaultSpeed = 0.125f;

	public static float moveTowards(float current, float end, float minSpeed) {
		return moveTowards(current, end, defaultSpeed, minSpeed);
	}

	public static double getAnimationState(double animation, double finalState, double speed) {
		float add = (float) ((double) delta * speed);
		animation = animation < finalState
				? (animation + (double) add < finalState ? (animation += (double) add) : finalState)
				: (animation - (double) add > finalState ? (animation -= (double) add) : finalState);
		return animation;
	}

	public static float moveTowards(float current, float end, float smoothSpeed, float minSpeed) {
		float movement = (end - current) * smoothSpeed;

		if (movement > 0) {
			movement = Math.max(minSpeed, movement);
			movement = Math.min(end - current, movement);
		} else if (movement < 0) {
			movement = Math.min(-minSpeed, movement);
			movement = Math.max(end - current, movement);
		}

		return current + movement;
	}

	public static float calculateCompensation(float target, float current, long delta, double speed) {
		float diff = current - target;
		if (delta < 1) {
			delta = 1;
		}
		if (delta > 1000) {
			delta = 16;
		}
		if (diff > speed) {
			double xD = (speed * delta / (1000 / 60) < 0.5 ? 0.5 : speed * delta / (1000 / 60));
			current -= xD;
			if (current < target) {
				current = target;
			}
		} else if (diff < -speed) {
			double xD = (speed * delta / (1000 / 60) < 0.5 ? 0.5 : speed * delta / (1000 / 60));
			current += xD;
			if (current > target) {
				current = target;
			}
		} else {
			current = target;
		}
		return current;
	}
}
