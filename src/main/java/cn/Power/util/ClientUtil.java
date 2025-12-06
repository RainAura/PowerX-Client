package cn.Power.util;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.ArrayList;

import cn.Power.Client;
import cn.Power.notification.Notification;
import cn.Power.notification.Notification.Type;
import cn.Power.ui.CFont.CFontRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public enum ClientUtil {

	INSTANCE;

	protected static Minecraft mc = Minecraft.getMinecraft();
	private static ArrayList<Notification> notifications = new ArrayList<>();

	public static int addY = 50;

	public static int reAlpha(int color, float alpha) {
		Color c = new Color(color);
		float r = ((float) 1 / 255) * c.getRed();
		float g = ((float) 1 / 255) * c.getGreen();
		float b = ((float) 1 / 255) * c.getBlue();
		return new Color(r, g, b, alpha).getRGB();
	}

	public static String removeColorCode(String text) {
		String finalText = text;
		if (text.contains("\247")) {
			for (int i = 0; i < finalText.length(); ++i) {
				if (Character.toString(finalText.charAt(i)).equals("\247")) {
					try {
						String part1 = finalText.substring(0, i);
						String part2 = finalText.substring(Math.min(i + 2, finalText.length()), finalText.length());
						finalText = part1 + part2;
					} catch (Exception var5) {
						;
					}
				}
			}
		}

		return finalText;
	}

	public static void sendChatMessage(String message, ChatType type) {
		if (type == ChatType.INFO) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(
					new ChatComponentText("\2478[\247c" + Client.CLIENT_name + "\2478]\247r\247r " + message));
		} else if (type == ChatType.WARN) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(
					new ChatComponentText("\2478[\247c" + Client.CLIENT_name + "\2478]\247r\247e " + message));
		} else if (type == ChatType.ERROR) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(
					new ChatComponentText("\2478[\247c" + Client.CLIENT_name + "\2478]\247r\2474 " + message));
		}

	}

	public static boolean isBlockBetween(BlockPos start, BlockPos end) {
		int startX = start.getX();
		int startY = start.getY();
		int startZ = start.getZ();
		int endX = end.getX();
		int endY = end.getY();
		int endZ = end.getZ();
		double diffX = (double) (endX - startX);
		double diffY = (double) (endY - startY);
		double diffZ = (double) (endZ - startZ);
		double x = (double) startX;
		double y = (double) startY;
		double z = (double) startZ;
		double STEP = 0.1D;
		int STEPS = (int) Math.max(Math.abs(diffX), Math.max(Math.abs(diffY), Math.abs(diffZ))) * 4;

		for (int i = 0; i < STEPS - 1; ++i) {
			x += diffX / (double) STEPS;
			y += diffY / (double) STEPS;
			z += diffZ / (double) STEPS;
			if (x != (double) endX || y != (double) endY || z != (double) endZ) {
				BlockPos pos = new BlockPos(x, y, z);
				Block block = mc.theWorld.getBlockState(pos).getBlock();
				if (block.getMaterial() != Material.air && block.getMaterial() != Material.water
						&& !(block instanceof BlockVine) && !(block instanceof BlockLadder)) {
					return true;
				}
			}
		}

		return false;
	}

	public float randFloat(float min, float max) {

		SecureRandom rand = new SecureRandom();

		return rand.nextFloat() * (max - min) + min;
	}
}
