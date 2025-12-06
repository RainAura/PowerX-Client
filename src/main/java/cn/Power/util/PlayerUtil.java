package cn.Power.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PlayerUtil {
	private static Minecraft mc = Minecraft.getMinecraft();

	public static float[] getRotations(Entity ent) {
		double x = ent.posX;
		double z = ent.posZ;
		double y = ent.posY + ent.getEyeHeight() / 4.0F;
		return getRotationFromPosition(x, z, y);
	}

	private static float[] getRotationFromPosition(double x, double z, double y) {
		double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
		double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
		double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 0.6D;
		double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / Math.PI);
		return new float[] { yaw, pitch };
	}

	public static double getBaseMovementSpeed() {
		double baseSpeed = 0.2873020626850198;
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
			int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (double) (amplifier + 1);
		}
		return baseSpeed;
	}

	public static float getDirection() {
		float yaw = Minecraft.getMinecraft().thePlayer.rotationYawHead;
		float forward = Minecraft.getMinecraft().thePlayer.moveForward;
		float strafe = Minecraft.getMinecraft().thePlayer.moveStrafing;
		yaw += (forward < 0.0F ? 180 : 0);
		if (strafe < 0.0F) {
			yaw += (forward < 0.0F ? -45 : forward == 0.0F ? 90 : 45);
		}
		if (strafe > 0.0F) {
			yaw -= (forward < 0.0F ? -45 : forward == 0.0F ? 90 : 45);
		}
		return yaw * 0.017453292F;
	}

	public static boolean isInWater() {
		return PlayerUtil.mc.theWorld.getBlockState(
				new BlockPos(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY, PlayerUtil.mc.thePlayer.posZ))
				.getBlock().getMaterial() == Material.water;
	}

	public static void toFwd(double speed) {
		float yaw = PlayerUtil.mc.thePlayer.rotationYaw * 0.017453292f;
		EntityPlayerSP thePlayer = PlayerUtil.mc.thePlayer;
		thePlayer.motionX -= (double) MathHelper.sin((float) yaw) * speed;
		EntityPlayerSP thePlayer2 = PlayerUtil.mc.thePlayer;
		thePlayer2.motionZ += (double) MathHelper.cos((float) yaw) * speed;
	}

	public static double getSpeed() {
		return Math.sqrt(Minecraft.getMinecraft().thePlayer.motionX * Minecraft.getMinecraft().thePlayer.motionX
				+ Minecraft.getMinecraft().thePlayer.motionZ * Minecraft.getMinecraft().thePlayer.motionZ);
	}

	public static Block getBlockUnderPlayer(EntityPlayer inPlayer) {
		return PlayerUtil.getBlock(new BlockPos(inPlayer.posX, inPlayer.posY - 1.0, inPlayer.posZ));
	}

	public static Block getBlock(BlockPos pos) {
		return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
	}

	public static Block getBlockAtPosC(EntityPlayer inPlayer, double x, double y, double z) {
		return PlayerUtil.getBlock(new BlockPos(inPlayer.posX - x, inPlayer.posY - y, inPlayer.posZ - z));
	}

	public static ArrayList<Vector3f> vanillaTeleportPositions(double tpX, double tpY, double tpZ, double speed) {
		double d;
		ArrayList positions = new ArrayList();
		Minecraft mc = Minecraft.getMinecraft();
		double posX = tpX - mc.thePlayer.posX;
		double posY = tpY - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() + 1.1);
		double posZ = tpZ - mc.thePlayer.posZ;
		float yaw = (float) (Math.atan2((double) posZ, (double) posX) * 180.0 / 3.141592653589793 - 90.0);
		float pitch = (float) ((-Math.atan2((double) posY, (double) Math.sqrt((double) (posX * posX + posZ * posZ))))
				* 180.0 / 3.141592653589793);
		double tmpX = mc.thePlayer.posX;
		double tmpY = mc.thePlayer.posY;
		double tmpZ = mc.thePlayer.posZ;
		double steps = 1.0;
		for (d = speed; d < PlayerUtil.getDistance(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, tpX, tpY,
				tpZ); d += speed) {
			steps += 1.0;
		}
		for (d = speed; d < PlayerUtil.getDistance(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, tpX, tpY,
				tpZ); d += speed) {
			tmpX = mc.thePlayer.posX - Math.sin((double) PlayerUtil.getDirection(yaw)) * d;
			tmpZ = mc.thePlayer.posZ + Math.cos((double) PlayerUtil.getDirection(yaw)) * d;
			positions.add((Object) new Vector3f((float) tmpX, (float) (tmpY -= (mc.thePlayer.posY - tpY) / steps),
					(float) tmpZ));
		}
		positions.add((Object) new Vector3f((float) tpX, (float) tpY, (float) tpZ));
		return positions;
	}

	public static float getDirection(float yaw) {
		if (Minecraft.getMinecraft().thePlayer.moveForward < 0.0f) {
			yaw += 180.0f;
		}
		float forward = 1.0f;
		if (Minecraft.getMinecraft().thePlayer.moveForward < 0.0f) {
			forward = -0.5f;
		} else if (Minecraft.getMinecraft().thePlayer.moveForward > 0.0f) {
			forward = 0.5f;
		}
		if (Minecraft.getMinecraft().thePlayer.moveStrafing > 0.0f) {
			yaw -= 90.0f * forward;
		}
		if (Minecraft.getMinecraft().thePlayer.moveStrafing < 0.0f) {
			yaw += 90.0f * forward;
		}
		return yaw *= 0.017453292f;
	}

	public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double d0 = x1 - x2;
		double d2 = y1 - y2;
		double d3 = z1 - z2;
		return MathHelper.sqrt_double((double) (d0 * d0 + d2 * d2 + d3 * d3));
	}

	public static boolean MovementInput() {
		return PlayerUtil.mc.gameSettings.keyBindForward.isKeyDown()
				|| PlayerUtil.mc.gameSettings.keyBindLeft.isKeyDown()
				|| PlayerUtil.mc.gameSettings.keyBindRight.isKeyDown()
				|| PlayerUtil.mc.gameSettings.keyBindBack.isKeyDown();
	}
	
	
    public static void strafe(final float speed) {
        if (!isMoving()) {
            return;
        }
        final double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

	public static void blockHit(Entity en, boolean value) {
		Minecraft mc = Minecraft.getMinecraft();
		ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
		if (mc.thePlayer.getCurrentEquippedItem() != null && en != null && value && stack.getItem() instanceof ItemSword
				&& (double) mc.thePlayer.swingProgress > 0.2) {
			mc.thePlayer.getCurrentEquippedItem().useItemRightClick((World) mc.theWorld, (EntityPlayer) mc.thePlayer);
		}
	}

	public static float getItemAtkDamage(ItemStack itemStack) {
		Iterator iterator;
		Multimap multimap = itemStack.getAttributeModifiers();
		if (!multimap.isEmpty() && (iterator = multimap.entries().iterator()).hasNext()) {
			double damage;
			Map.Entry entry = (Map.Entry) iterator.next();
			AttributeModifier attributeModifier = (AttributeModifier) entry.getValue();
			double d = damage = attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2
					? attributeModifier.getAmount()
					: attributeModifier.getAmount() * 100.0;
			if (attributeModifier.getAmount() > 1.0) {
				return 1.0f + (float) damage;
			}
			return 1.0f;
		}
		return 1.0f;
	}

	public static int bestWeapon(Entity target) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.thePlayer.inventory.currentItem = 0;
		int firstSlot = 0;
		int bestWeapon = -1;
		int j = 1;
		for (int i = 0; i < 9; i = (int) ((byte) (i + 1))) {
			mc.thePlayer.inventory.currentItem = i;
			ItemStack itemStack = mc.thePlayer.getHeldItem();
			if (itemStack == null)
				continue;
			int itemAtkDamage = (int) PlayerUtil.getItemAtkDamage(itemStack);
			if ((itemAtkDamage = (int) ((float) itemAtkDamage + EnchantmentHelper.getModifierForCreature(
					(ItemStack) itemStack, (EnumCreatureAttribute) EnumCreatureAttribute.UNDEFINED))) <= j)
				continue;
			j = itemAtkDamage;
			bestWeapon = i;
		}
		if (bestWeapon != -1) {
			return bestWeapon;
		}
		return firstSlot;
	}

	public static void shiftClick(Item i) {
		for (int i1 = 9; i1 < 37; ++i1) {
			ItemStack itemstack = PlayerUtil.mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
			if (itemstack == null || itemstack.getItem() != i)
				continue;
			PlayerUtil.mc.playerController.windowClick(0, i1, 0, 1, (EntityPlayer) PlayerUtil.mc.thePlayer);
			break;
		}
	}

	public static boolean hotbarIsFull() {
		for (int i = 0; i <= 36; ++i) {
			ItemStack itemstack = PlayerUtil.mc.thePlayer.inventory.getStackInSlot(i);
			if (itemstack != null)
				continue;
			return false;
		}
		return true;
	}

	public static Vec3 getLook(float p_174806_1_, float p_174806_2_) {
		float var3 = MathHelper.cos(-p_174806_2_ * 0.017453292F - 3.1415927F);
		float var4 = MathHelper.sin(-p_174806_2_ * 0.017453292F - 3.1415927F);
		float var5 = -MathHelper.cos(-p_174806_1_ * 0.017453292F);
		float var6 = MathHelper.sin(-p_174806_1_ * 0.017453292F);
		return new Vec3(var4 * var5, var6, var3 * var5);
	}

	public static void tellPlayer(String string) {
		mc.thePlayer.addChatMessage(new ChatComponentText(string));

	}

	public EntityLivingBase getEntity() {

		return null;
	}

	public static void setSpeed(double speed) {
		PlayerUtil.mc.thePlayer.motionX = -Math.sin(PlayerUtil.getDirection()) * speed;
		PlayerUtil.mc.thePlayer.motionZ = Math.cos(PlayerUtil.getDirection()) * speed;
	}

	public static Minecraft getMinecraft() {
		return Minecraft.getMinecraft();
	}

	public static EntityPlayerSP getPlayer() {
		return getMinecraft().thePlayer;
	}

	public static double getIncremental(final double val, final double inc) {
		final double one = 1.0 / inc;
		return Math.round(val * one) / one;
	}

	public static boolean isMoving() {
		if (PlayerUtil.mc.gameSettings.keyBindForward.isPressed())
			return true;
		if (PlayerUtil.mc.gameSettings.keyBindBack.isPressed())
			return true;
		if (PlayerUtil.mc.gameSettings.keyBindLeft.isPressed())
			return true;
		if (PlayerUtil.mc.gameSettings.keyBindRight.isPressed())
			return true;
		if (PlayerUtil.mc.thePlayer.movementInput.moveForward != 0.0f)
			return true;
		if (PlayerUtil.mc.thePlayer.movementInput.moveStrafe != 0.0f)
			return true;
		return false;
	}

	public static boolean isMoving2() {
		return ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F));
	}

	public static boolean isOnLiquid() {
		AxisAlignedBB boundingBox = PlayerUtil.mc.thePlayer.getEntityBoundingBox();
		if (boundingBox == null) {
			return false;
		}
		boundingBox = boundingBox.contract(0.01, 0.0, 0.01).offset(0.0, -0.01, 0.0);
		boolean onLiquid = false;
		int y = (int) boundingBox.minY;
		int x = MathHelper.floor_double(boundingBox.minX);
		while (x < MathHelper.floor_double(boundingBox.maxX + 1.0)) {
			for (int z = MathHelper.floor_double((double) boundingBox.minZ); z < MathHelper
					.floor_double(boundingBox.maxZ + 1.0); ++z) {
				Block block = PlayerUtil.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (block == Blocks.air)
					continue;
				if (!(block instanceof BlockLiquid)) {
					return false;
				}
				onLiquid = true;
			}
			++x;
		}
		return onLiquid;
	}

	public static boolean isInLiquidS() {
		if (mc.thePlayer == null) {
			return false;
		}
		for (int x = MathHelper.floor_double(mc.thePlayer.boundingBox.minX); x < MathHelper
				.floor_double(mc.thePlayer.boundingBox.maxX) + 1; x++) {
			for (int z = MathHelper.floor_double(mc.thePlayer.boundingBox.minZ); z < MathHelper
					.floor_double(mc.thePlayer.boundingBox.maxZ) + 1; z++) {
				BlockPos pos = new BlockPos(x, (int) mc.thePlayer.boundingBox.minY, z);
				Block block = mc.theWorld.getBlockState(pos).getBlock();
				if ((block != null) && (!(block instanceof BlockAir))) {
					return block instanceof BlockLiquid;
				}
			}
		}
		return false;
	}

	public static boolean isInLiquid() {
		if (PlayerUtil.mc.thePlayer == null) {
			return false;
		}
		int x = MathHelper.floor_double(PlayerUtil.mc.thePlayer.boundingBox.minX);
		while (x < MathHelper.floor_double(PlayerUtil.mc.thePlayer.boundingBox.maxX) + 1) {
			for (int z = MathHelper.floor_double((double) PlayerUtil.mc.thePlayer.boundingBox.minZ); z < MathHelper
					.floor_double(PlayerUtil.mc.thePlayer.boundingBox.maxZ) + 1; ++z) {
				BlockPos pos = new BlockPos(x, (int) PlayerUtil.mc.thePlayer.boundingBox.minY, z);
				Block block = PlayerUtil.mc.theWorld.getBlockState(pos).getBlock();
				if (block == null || block instanceof BlockAir)
					continue;
				return block instanceof BlockLiquid;
			}
			++x;
		}
		return false;
	}

	public static double roundToPlace(double value, int places) {
		if (places < 0) {
			return value;
		} else {
			BigDecimal bd = new BigDecimal(value);
			bd = bd.setScale(places, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
	}

	public static BlockPos getHypixelBlockpos(String str) {
		int val = 92;
		if (str != null && str.length() > 1) {
			char[] chs = str.toCharArray();

			int lenght = chs.length;
			for (int i = 0; i < lenght; i++)
				val += (int) chs[i] * str.length() * str.length() + (int) str.charAt(0) + (int) str.charAt(1);
			val /= str.length();
		}
		return new BlockPos(val, -val % 255, val);
	}

	public static BlockPos GetHypixelBlockpos(String str) {
		int val = 20;
		if (str != null && str.length() > 1) {
			char[] chs = str.toCharArray();

			int lenght = chs.length;
			for (int i = 0; i < lenght; i++)
				val += (int) chs[i] * str.length() / str.length() - (int) str.charAt(random(1, 4));
			val /= str.length();
		}
		return new BlockPos(val, -val % 255, val);
	}

	public static int random(double min, double max) {
		Random random = new Random();
		return (int) (min + (random.nextDouble() * (max - min)));
	}

	public static void hypixelTeleport(final double[] startPos, final BlockPos endPos) {

		double distx = startPos[0] - endPos.getX() + 0.5;
		double disty = startPos[1] - endPos.getY();
		double distz = startPos[2] - endPos.getZ() + 0.5;
		double dist = Math.sqrt(mc.thePlayer.getDistanceSq(endPos));
		double distanceEntreLesPackets = 0.31 + MathUtils.getSpeedEffect() / 20;
		double xtp, ytp, ztp = 0;
		if (dist > distanceEntreLesPackets) {

			double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1;

			xtp = mc.thePlayer.posX;
			ytp = mc.thePlayer.posY;
			ztp = mc.thePlayer.posZ;
			double count = 0;
			for (int i = 1; i < nbPackets; i++) {
				double xdi = (endPos.getX() - mc.thePlayer.posX) / (nbPackets);
				xtp += xdi;

				double zdi = (endPos.getZ() - mc.thePlayer.posZ) / (nbPackets);
				ztp += zdi;

				double ydi = (endPos.getY() - mc.thePlayer.posY) / (nbPackets);
				ytp += ydi;
				count++;

				if (!mc.theWorld.getBlockState(new BlockPos(xtp, ytp - 1, ztp)).getBlock().isBlockNormalCube()) {
					if (count <= 2) {
						ytp += 2E-8;
					} else if (count >= 4) {
						count = 0;
					}
				}
				C03PacketPlayer.C04PacketPlayerPosition Packet = new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp,
						ztp, false);
				mc.thePlayer.sendQueue.addToSendQueue(Packet);
			}

			mc.thePlayer.setPosition(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5);

		} else {
			mc.thePlayer.setPosition(endPos.getX(), endPos.getY(), endPos.getZ());

		}
	}

	public static void teleport(final double[] startPos, final BlockPos endPos) {
		double distx = startPos[0] - endPos.getX() + 0.5;
		double disty = startPos[1] - endPos.getY();
		double distz = startPos[2] - endPos.getZ() + 0.5;
		double dist = Math.sqrt(mc.thePlayer.getDistanceSq(endPos));
		double distanceEntreLesPackets = 5;
		double xtp, ytp, ztp = 0;

		if (dist > distanceEntreLesPackets) {
			double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1;
			xtp = mc.thePlayer.posX;
			ytp = mc.thePlayer.posY;
			ztp = mc.thePlayer.posZ;
			double count = 0;
			for (int i = 1; i < nbPackets; i++) {
				double xdi = (endPos.getX() - mc.thePlayer.posX) / (nbPackets);
				xtp += xdi;

				double zdi = (endPos.getZ() - mc.thePlayer.posZ) / (nbPackets);
				ztp += zdi;

				double ydi = (endPos.getY() - mc.thePlayer.posY) / (nbPackets);
				ytp += ydi;
				count++;
				C03PacketPlayer.C04PacketPlayerPosition Packet = new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp,
						ztp, true);

				mc.thePlayer.sendQueue.addToSendQueue(Packet);
			}

			mc.thePlayer.setPosition(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5);
		} else {
			mc.thePlayer.setPosition(endPos.getX(), endPos.getY(), endPos.getZ());
		}
	}

}
