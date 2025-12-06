package cn.Power.util;

import java.util.List;

import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.KillAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class RotationUtils {
	public static float AnimotaiSpeed;

	public static float[] getRotations(EntityLivingBase ent) {
		double x = ent.posX;
		double z = ent.posZ;
		double y = ent.posY + (double) (ent.getEyeHeight() / 2.0F);
		return getRotationFromPosition(x, z, y);
	}
	
	public static float[] getRotations(EntityLivingBase ent, double x1, double y1, double z1) {
		double x = ent.posX;
		double z = ent.posZ;
		double y = ent.posY + (double) (ent.getEyeHeight() / 2.0F);
		
		
		double xDiff = x - x1;
		double zDiff = z - z1;
		double yDiff = y - y1 - 1.2D;
		double dist = (double) MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
		float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D));
		return new float[] { yaw, pitch };
	}

	public static float[] getAverageRotations(List<EntityLivingBase> targetList) {
		double posX = 0.0D;
		double posY = 0.0D;
		double posZ = 0.0D;
		for (Entity ent : targetList) {
			posX += ent.posX;
			posY += ent.boundingBox.maxY - 2.0D;
			posZ += ent.posZ;
		}
		posX /= targetList.size();
		posY /= targetList.size();
		posZ /= targetList.size();
		return new float[] { getRotationFromPosition(posX, posZ, posY)[0],
				getRotationFromPosition(posX, posZ, posY)[1] };
	}

	public static float[] getBowAngles(final Entity entity) {
		final double xDelta = entity.posX - entity.lastTickPosX;
		final double zDelta = entity.posZ - entity.lastTickPosZ;
		double d = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
		d -= d % 0.8;
		double xMulti = 1.0;
		double zMulti = 1.0;
		final boolean sprint = entity.isSprinting();
		xMulti = d / 0.8 * xDelta * (sprint ? 1.25 : 1.0);
		zMulti = d / 0.8 * zDelta * (sprint ? 1.25 : 1.0);
		final double x = entity.posX + xMulti - Minecraft.getMinecraft().thePlayer.posX;
		final double z = entity.posZ + zMulti - Minecraft.getMinecraft().thePlayer.posZ;
		final double y = Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight()
				- (entity.posY + entity.getEyeHeight());
		final double dist = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
		final float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90.0f;
		final float pitch = (float) Math.toDegrees(Math.atan2(y, dist));
		return new float[] { yaw, pitch };
	}

	public static float[] getRotationFromPosition(double x, double z, double y) {
		double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
		double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
		double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2D;
		double dist = (double) MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
		float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D));
		return new float[] { yaw, pitch };
	}

	public static float getTrajAngleSolutionLow(float d3, float d1, float velocity) {
		float g = 0.006F;
		float sqrt = velocity * velocity * velocity * velocity
				- g * (g * (d3 * d3) + 2.0F * d1 * (velocity * velocity));
		return (float) Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(sqrt)) / (g * d3)));
	}

	public static float getYawChange(double posX, double posZ) {
		double deltaX = posX - Minecraft.getMinecraft().thePlayer.posX;
		double deltaZ = posZ - Minecraft.getMinecraft().thePlayer.posZ;
		double yawToEntity;
		if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
			yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
		} else if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
			yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
		} else {
			yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
		}
		return MathHelper.wrapAngleTo180_float(-(Minecraft.getMinecraft().thePlayer.rotationYaw - (float) yawToEntity));
	}

	public static float getPitchChange(Entity entity, double posY) {
		double deltaX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
		double deltaZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
		double deltaY = posY - 2.2D + entity.getEyeHeight() - Minecraft.getMinecraft().thePlayer.posY;
		double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
		double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
		return -MathHelper
				.wrapAngleTo180_float(Minecraft.getMinecraft().thePlayer.rotationPitch - (float) pitchToEntity) - 2.5F;
	}

	public static float getNewAngle(float angle) {
		angle %= 360.0F;
		if (angle >= 180.0F) {
			angle -= 360.0F;
		}
		if (angle < -180.0F) {
			angle += 360.0F;
		}
		return angle;
	}

	public static float getDistanceBetweenAngles(float angle1, float angle2) {
		float angle = Math.abs(angle1 - angle2) % 360.0F;
		if (angle > 180.0F) {
			angle = 360.0F - angle;
		}
		return angle;
	}

	public static float Yaw(float Yaw) {
		KillAura aura = (KillAura) ModManager.getModByClass(KillAura.class);
		Yaw %= 360.0F;
		AnimotaiSpeed = (float) RenderUtil.getAnimationState(AnimotaiSpeed, 360.0F,
				aura.turnspeed.getValueState().floatValue() / 10);
		if (Yaw >= 180.0F) {
			Yaw -= 360.0F;
		}
		if (Yaw < -180.0F) {
			Yaw += 360.0F;
		}
		float A = getAngleDifference(Yaw, Minecraft.getMinecraft().thePlayer.rotationYaw);
		if (A >= 180.0F) {
			Yaw += AnimotaiSpeed;
		}
		if (A <= -180.0F) {
			Yaw -= AnimotaiSpeed;
		}
		return Yaw;
	}

	public static float getAngleDifference(float direction, float rotationYaw) {
		float phi = Math.abs(rotationYaw - direction) % 360;
		float distance = phi > 180 ? 360 - phi : phi;
		return distance;
	}

	public static float[] getRotationToEntity(EntityLivingBase entity) {
		double xDiff = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
		double zDiff = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
		double yDiff = entity.posY + (double) entity.getEyeHeight() - (Minecraft.getMinecraft().thePlayer.posY
				+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight());
		double distance = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
		float pitch = (float) (-(Math.atan2(yDiff, distance) * 180.0 / 3.141592653589793));
		return new float[] { yaw, pitch };
	}
	
    public static float[] getRotationsToEnt(EntityLivingBase ent) {
    	EntityPlayerSP playerSP = Minecraft.getMinecraft().thePlayer;
        final double differenceX = ent.posX - playerSP.posX;
        final double differenceY = (ent.posY + ent.height) - (playerSP.posY + playerSP.height);
        final double differenceZ = ent.posZ - playerSP.posZ;
        final float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0D / Math.PI) - 90.0f;
        final float rotationPitch = (float) (Math.atan2(differenceY, playerSP.getDistanceToEntity(ent)) * 180.0D / Math.PI);
        final float finishedYaw = playerSP.rotationYaw + MathHelper.wrapAngleTo180_float(rotationYaw - playerSP.rotationYaw);
        final float finishedPitch = playerSP.rotationPitch + MathHelper.wrapAngleTo180_float(rotationPitch - playerSP.rotationPitch);
        return new float[]{finishedYaw, -finishedPitch};
    }
    
	public static float getYawChangeGiven(double posX, double posZ, float yaw) {
		double deltaX = posX - Minecraft.getMinecraft().thePlayer.posX;
		double deltaZ = posZ - Minecraft.getMinecraft().thePlayer.posZ;
		double yawToEntity = deltaZ < 0.0 && deltaX < 0.0 ? 90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX))
				: (deltaZ < 0.0 && deltaX > 0.0 ? -90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX))
						: Math.toDegrees(-Math.atan(deltaX / deltaZ)));
		return MathHelper.wrapAngleTo180_float(-(yaw - (float) yawToEntity));
	}

}
