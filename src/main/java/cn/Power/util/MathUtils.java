package cn.Power.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public final class MathUtils {
	private static Minecraft mc = Minecraft.getMinecraft();

	public static int randomNumber(int max, int min) {
		return (int) (Math.random() * (double) (max - min)) + min;
	}

	public static double roundToPlace(double value, int places) {
		if (places < 0) {
			return value;
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double getIncremental(double val, double inc) {
		double one = 1.0 / inc;
		return (double) Math.round(val * one) / one;
	}

	public static boolean isInteger(Double variable) {
		if (variable != Math.floor(variable))
			return false;
		if (Double.isInfinite(variable))
			return false;
		return true;
	}

	public static int getJumpEffect() {
		if (Minecraft.thePlayer.isPotionActive(Potion.jump))
			return Minecraft.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
		else
			return 0;
	}

	public static int getSpeedEffect() {
		if (Minecraft.thePlayer.isPotionActive(Potion.moveSpeed))
			return Minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
		else
			return 0;
	}

	public static boolean isOnGround(double height) {
		if (!Minecraft.theWorld.getCollidingBoundingBoxes(Minecraft.thePlayer,
				Minecraft.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public static void setMotion(double speed) {
		double forward = Minecraft.thePlayer.movementInput.moveForward;
		double strafe = Minecraft.thePlayer.movementInput.moveStrafe;
		float yaw = Minecraft.thePlayer.rotationYaw;
		if ((forward == 0.0D) && (strafe == 0.0D)) {
			Minecraft.thePlayer.motionX = 0;
			Minecraft.thePlayer.motionZ = 0;
		} else {
			if (forward != 0.0D) {
				if (strafe > 0.0D) {
					yaw += (forward > 0.0D ? -45 : 45);
				} else if (strafe < 0.0D) {
					yaw += (forward > 0.0D ? 45 : -45);
				}
				strafe = 0.0D;
				if (forward > 0.0D) {
					forward = 1;
				} else if (forward < 0.0D) {
					forward = -1;
				}
			}
			Minecraft.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F))
					+ strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
			Minecraft.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F))
					- strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
		}
	}

	public static double defaultSpeed() {
		double baseSpeed = 0.2873D;
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
			int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
		}
		return baseSpeed;
	}

	public static boolean isCollidedH(double dist) {
		AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX - 0.3, mc.thePlayer.posY + 2, mc.thePlayer.posZ + 0.3,
				mc.thePlayer.posX + 0.3, mc.thePlayer.posY + 3, mc.thePlayer.posZ - 0.3);
		if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0.3 + dist, 0, 0)).isEmpty()) {
			return true;
		} else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(-0.3 - dist, 0, 0)).isEmpty()) {
			return true;
		} else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0, 0, 0.3 + dist)).isEmpty()) {
			return true;
		} else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0, 0, -0.3 - dist)).isEmpty()) {
			return true;
		}
		return false;
	}

	public static float[] getRotationsBlock(BlockPos block, EnumFacing face) {
		double x = block.getX() + 0.5 - mc.thePlayer.posX + (double) face.getFrontOffsetX() / 2;
		double z = block.getZ() + 0.5 - mc.thePlayer.posZ + (double) face.getFrontOffsetZ() / 2;
		double y = (block.getY() + 0.5);
		double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
		double d3 = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);
		if (yaw < 0.0F) {
			yaw += 360f;
		}
		return new float[] { yaw, pitch };
	}

	public static float[] constrainAngle(float[] vector) {

		vector[0] = (vector[0] % 360F);
		vector[1] = (vector[1] % 360F);

		while (vector[0] <= -180) {
			vector[0] = (vector[0] + 360);
		}

		while (vector[1] <= -180) {
			vector[1] = (vector[1] + 360);
		}

		while (vector[0] > 180) {
			vector[0] = (vector[0] - 360);
		}

		while (vector[1] > 180) {
			vector[1] = (vector[1] - 360);
		}

		return vector;
	}

	public static double getBaseMovementSpeed() {
		double baseSpeed = 0.2873;
		if (Helper.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
			int amplifier = Helper.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (double) (amplifier + 1);
		}
		return baseSpeed;
	}

	public static double getBaseMoveSpeed() {
		double baseSpeed = 0.272;
		if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
			final int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= 1.0 + (0.2 * amplifier);
		}
		return baseSpeed;
	}

	public static double getRandomInRange(double min, double max) {
		Random random = ThreadLocalRandom.current();
		double range = max - min;
		double scaled = random.nextDouble() * range;
		if (scaled > max) {
			scaled = max;
		}
		double shifted = scaled + min;

		if (shifted > max) {
			shifted = max;
		}
		return shifted;
	}

	public static double round(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

    public static double preciseRound(double value, double precision) {
        double scale = Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

	public static float map(float x, float prev_min, float prev_max, float new_min, float new_max) {
		return (x - prev_min) / (prev_max - prev_min) * (new_max - new_min) + new_min;
	}

	public static double map(double x, double prev_min, double prev_max, double new_min, double new_max) {
		return (x - prev_min) / (prev_max - prev_min) * (new_max - new_min) + new_min;
	}

	public static boolean contains(float x, float y, float minX, float minY, float maxX, float maxY) {
		return x > minX && x < maxX && y > minY && y < maxY;
	}
    
}
