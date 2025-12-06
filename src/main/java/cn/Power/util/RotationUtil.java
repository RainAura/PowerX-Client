package cn.Power.util;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtil {
	static Minecraft mc = Minecraft.getMinecraft();

	public static float pitch() {
		return Helper.mc.thePlayer.rotationPitch;
	}

	public static void pitch(float pitch) {
		Helper.mc.thePlayer.rotationPitch = pitch;
	}

	public static float yaw() {
		return Helper.mc.thePlayer.rotationYaw;
	}

	public static void yaw(float yaw) {
		Helper.mc.thePlayer.rotationYaw = yaw;
	}

	public static float[] faceTarget(Entity target, float p_706252, float p_706253, boolean miss) {
		double var6;
		double var4 = target.posX - Helper.mc.thePlayer.posX;
		double var8 = target.posZ - Helper.mc.thePlayer.posZ;
		if (target instanceof EntityLivingBase) {
			EntityLivingBase var10 = (EntityLivingBase) target;
			var6 = var10.posY + (double) var10.getEyeHeight()
					- (Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight());
		} else {
			var6 = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0
					- (Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight());
		}
		Random rnd = new Random();
		double var14 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
		float var12 = (float) (Math.atan2(var8, var4) * 180.0 / 3.141592653589793) - 90.0f;
		float var13 = (float) (-Math.atan2(var6 - (target instanceof EntityPlayer ? 0.25 : 0.0), var14) * 180.0
				/ 3.141592653589793);
		float pitch = RotationUtil.changeRotation(Helper.mc.thePlayer.rotationPitch, var13, p_706253);
		float yaw = RotationUtil.changeRotation(Helper.mc.thePlayer.rotationYaw, var12, p_706252);
		return new float[] { yaw, pitch };
	}

	public static float changeRotation(float p_706631, float p_706632, float p_706633) {
		float var4 = MathHelper.wrapAngleTo180_float(p_706632 - p_706631);
		if (var4 > p_706633) {
			var4 = p_706633;
		}
		if (var4 < -p_706633) {
			var4 = -p_706633;
		}
		return p_706631 + var4;
	}

	public static double[] getRotationToEntity(Entity entity) {
		double pX = Helper.mc.thePlayer.posX;
		double pY = Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight();
		double pZ = Helper.mc.thePlayer.posZ;
		double eX = entity.posX;
		double eY = entity.posY + (double) (entity.height / 2.0f);
		double eZ = entity.posZ;
		double dX = pX - eX;
		double dY = pY - eY;
		double dZ = pZ - eZ;
		double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
		double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
		double pitch = Math.toDegrees(Math.atan2(dH, dY));
		return new double[] { yaw, 90.0 - pitch };
	}

	public static float[] getRotations(Entity entity) {
		if (entity == null) {
			return null;
		}
		double X = entity.posX - mc.thePlayer.posX;
		double Y = entity.posY + (double) entity.getEyeHeight() * 0.9D
				- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
		double Z = entity.posZ - mc.thePlayer.posZ;
		double dist = MathHelper.sqrt_double(X * X + Z * Z);
		float yaw = (float) (Math.atan2(Z, X) * 180.0 / 3.141592653589793) - 90.0f;
		float pitch = (float) (-Math.atan2(Y, dist) * 180.0 / 3.141592653589793);
		float Yaw = mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
		float Pitch = mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch);
		return new float[] { Yaw, Pitch };
	}

	public static float getDistanceBetweenAngles(float angle1, float angle2) {
		float angle3 = Math.abs(angle1 - angle2) % 360.0f;
		if (angle3 > 180.0f) {
			angle3 = 0.0f;
		}
		return angle3;
	}

	public static float[] grabBlockRotations(BlockPos pos) {
		return RotationUtil.getVecRotation(
				Helper.mc.thePlayer.getPositionVector().addVector(0.0, Helper.mc.thePlayer.getEyeHeight(), 0.0),
				new Vec3((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5));
	}

	public static float[] getVecRotation(Vec3 position) {
		return RotationUtil.getVecRotation(
				Helper.mc.thePlayer.getPositionVector().addVector(0.0, Helper.mc.thePlayer.getEyeHeight(), 0.0),
				position);
	}

	public static float[] getVecRotation(Vec3 origin, Vec3 position) {
		Vec3 difference = position.subtract(origin);
		double distance = difference.flat().lengthVector();
		float yaw = (float) Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
		float pitch = (float) (-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
		return new float[] { yaw, pitch };
	}

	public static int wrapAngleToDirection(float yaw, int zones) {
		int angle = (int) ((double) (yaw + (float) (360 / (2 * zones))) + 0.5) % 360;
		if (angle < 0) {
			angle += 360;
		}
		return angle / (360 / zones);
	}

	public static boolean canEntityBeSeen(Entity e) {
		Vec3 vec1 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		AxisAlignedBB box = e.getEntityBoundingBox();
		Vec3 vec2 = new Vec3(e.posX, e.posY + (e.getEyeHeight() / 1.32F), e.posZ);
		double minx = e.posX - 0.25;
		double maxx = e.posX + 0.25;
		double miny = e.posY;
		double maxy = e.posY + Math.abs(e.posY - box.maxY);
		double minz = e.posZ - 0.25;
		double maxz = e.posZ + 0.25;
		boolean see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;
		vec2 = new Vec3(maxx, miny, minz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;
		vec2 = new Vec3(minx, miny, minz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see)
			return true;
		vec2 = new Vec3(minx, miny, maxz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;
		vec2 = new Vec3(maxx, miny, maxz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;

		vec2 = new Vec3(maxx, maxy, minz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see)
			return true;
		vec2 = new Vec3(minx, maxy, minz);

		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;
		vec2 = new Vec3(minx, maxy, maxz - 0.1);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;
		vec2 = new Vec3(maxx, maxy, maxz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;

		return false;
	}
}
