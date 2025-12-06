package cn.Power.util;

import cn.Power.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class Helper2 {
	public static Minecraft mc = Minecraft.getMinecraft();
	private static ColorUtils colorUtils;

	public static void sendMessageOLD(String msg) {
		Object[] arrobject = new Object[2];
		Client.instance.getClass();
		arrobject[0] = (Object) EnumChatFormatting.BLUE + "Vacant" + (Object) EnumChatFormatting.GRAY + ": ";
		arrobject[1] = msg;
		Helper.mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(String.format("%s%s", arrobject)));
	}

	public static EntityPlayerSP player() {
		return mc().thePlayer;
	}

	public static Minecraft mc() {
		return Minecraft.getMinecraft();
	}

	public static void sendMessage(String message) {
		// ChatUtils.ChatMessageBuilder(true,
		// true).appendText(message).setColor(EnumChatFormatting.GRAY).build().displayClientSided();
	}

	public static class ColorUtils {
		public int RGBtoHEX(int r, int g, int b, int a) {
			return (a << 24) + (r << 16) + (g << 8) + b;
		}

		public static ColorUtils colorUtils() {
			return colorUtils;
		}

		public static void sendMessageWithoutPrefix(String message) {
			// ChatUtils.ChatMessageBuilder(false,
			// true).appendText(message).setColor(EnumChatFormatting.GRAY).build().displayClientSided();
		}

		public static boolean onServer(String server) {
			if (mc.isSingleplayer())
				return false;
			if (!Helper.mc.getCurrentServerData().serverIP.toLowerCase().contains(server))
				return false;
			return true;
		}
	}
}
