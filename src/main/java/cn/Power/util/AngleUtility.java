package cn.Power.util;

import java.util.Random;

import com.darkmagician6.eventapi.events.Event;

import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.util.Vector.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class AngleUtility {

	private static float minYawSmoothing;
	private static float maxYawSmoothing;
	private static float minPitchSmoothing;
	private static float maxPitchSmoothing;
	private static Vector3<Float> delta;
	private static Angle smoothedAngle;
	private static Random random;
	private static float height = 1.5f;

	public AngleUtility(float minYawSmoothing, float maxYawSmoothing, float minPitchSmoothing,
			float maxPitchSmoothing) {
		this.minYawSmoothing = minYawSmoothing;
		this.maxYawSmoothing = maxYawSmoothing;
		this.minPitchSmoothing = minPitchSmoothing;
		this.maxPitchSmoothing = maxPitchSmoothing;
		this.random = new Random();
		this.delta = new Vector3<>(0F, 0F, 0F);
		this.smoothedAngle = new Angle(0F, 0F);
	}

	public static float[] getAngleBlockpos(Entity target) {
		double xDiff = target.posX - Minecraft.getMinecraft().thePlayer.posX;
		double yDiff = target.posY - Minecraft.getMinecraft().thePlayer.posY;
		double zDiff = target.posZ - Minecraft.getMinecraft().thePlayer.posZ;
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
		float pitch = (float) ((-Math.atan2(
				target.posY + (double) -1
						- (Minecraft.getMinecraft().thePlayer.posY
								+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight()),
				Math.hypot(xDiff, zDiff))) * 180.0 / 3.141592653589793);

		if (yDiff > -0.2 && yDiff < 0.2) {
			pitch = (float) ((-Math.atan2(
					target.posY + (double) -1
							- (Minecraft.getMinecraft().thePlayer.posY
									+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight()),
					Math.hypot(xDiff, zDiff))) * 180.0 / 3.141592653589793);
		} else if (yDiff > -0.2) {
			pitch = (float) ((-Math.atan2(
					target.posY + (double) -1
							- (Minecraft.getMinecraft().thePlayer.posY
									+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight()),
					Math.hypot(xDiff, zDiff))) * 180.0 / 3.141592653589793);
		} else if (yDiff < 0.3) {
			pitch = (float) ((-Math.atan2(
					target.posY + (double) -1
							- (Minecraft.getMinecraft().thePlayer.posY
									+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight()),
					Math.hypot(xDiff, zDiff))) * 180.0 / 3.141592653589793);
		}

		return new float[] { yaw, pitch };
	}

	public float randomFloat(float min, float max) {
		return min + (this.random.nextFloat() * (max - min));
	}

	private static int randomNumber(double min, double max) {
		Random random = new Random();
		return (int) (min + (random.nextDouble() * (max - min)));
	}

	public Angle calculateAngle(Vector3<Double> destination, Vector3<Double> source) {
		Angle angles = new Angle(0.0F, 0.025F);
		// Height of where you want to aim at on the entity.
		float height = 2.2F;
		this.delta.setX(destination.getX().floatValue() - source.getX().floatValue())
				.setY((destination.getY().floatValue() + height) - (source.getY().floatValue() + height))
				.setZ(destination.getZ().floatValue() - source.getZ().floatValue());
		double hypotenuse = Math.hypot(this.delta.getX().doubleValue(), this.delta.getZ().doubleValue());
		float yawAtan = ((float) Math.atan2(this.delta.getZ().floatValue(), this.delta.getX().floatValue()));
		float pitchAtan = ((float) Math.atan2(this.delta.getY().floatValue(), hypotenuse));
		float deg = ((float) (180 / Math.PI));
		float yaw = ((yawAtan * deg) - 90F);
		float pitch = -(pitchAtan * deg);
		return angles.setYaw(yaw).setPitch(pitch);
	}
	
	  public static Angle calculateAngleHvH(Vector3<Double> destination, Vector3<Double> source) {

	        Angle angles = new Angle(0F, 0F);

	        //Height of where you want to aim at on the entity.
	        float height = 1.5F;

	        delta
	                .setX(destination.getX().floatValue() - source.getX().floatValue())
	                .setY((destination.getY().floatValue() + height) - (source.getY().floatValue() + height))
	                .setZ(destination.getZ().floatValue() - source.getZ().floatValue());

	        double hypotenuse = Math.hypot(delta.getX().doubleValue(), delta.getZ().doubleValue());

	        float yawAtan = ((float) Math.atan2(delta.getZ().floatValue(), delta.getX().floatValue()));
	        float pitchAtan = ((float) Math.atan2(delta.getY().floatValue(), hypotenuse));

	        float deg = ((float) (180 / Math.PI));

	        float yaw = ((yawAtan * deg) - 90F);
	        float pitch = -(pitchAtan * deg);

	        return angles.setYaw(yaw).setPitch(pitch).constrantAngle();
	    }

	    public Angle smoothAngle(Angle destination, Angle source, float i, float j) {
	        return smoothedAngle
	                .setYaw(source.getYaw() - destination.getYaw())
	                .setPitch(source.getPitch() - destination.getPitch())
	                .constrantAngle()
	                .setYaw(source.getYaw() - smoothedAngle.getYaw() / 100 * randomFloat(minYawSmoothing, maxYawSmoothing))
	                .setPitch(source.getPitch() - smoothedAngle.getPitch() / 100 * randomFloat(minPitchSmoothing, maxPitchSmoothing))
	                .constrantAngle();
	    }
	    

	public Angle smoothAngle(Angle destination, Angle source, Angle yi, Angle er, Angle autoSwitch) {

		switch (randomNumber(1, 4)) {
		case 1:
			autoSwitch = yi;
			break;
		case 2:
			autoSwitch = er;
			break;
		case 3:
			autoSwitch = destination;
			break;
		case 4:
			autoSwitch = source;
			break;
		}

		return this.smoothedAngle.setYaw(autoSwitch.getYaw() - autoSwitch.getYaw())
				.setPitch(autoSwitch.getPitch() - autoSwitch.getPitch())
				.setYaw(autoSwitch.getYaw() - this.smoothedAngle.getYaw()
						/ (KillAura.Target.rotationYaw * randomFloat(minYawSmoothing, maxYawSmoothing) * 0.4F))
				.setPitch(autoSwitch.getPitch() - this.smoothedAngle.getPitch()
						/ (KillAura.Target.rotationPitch * randomFloat(minPitchSmoothing, maxPitchSmoothing) * 0.25F));
	}
}
