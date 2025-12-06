package cn.Power.util.misc;

import cn.Power.util.MinecraftUtil;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;

public class ChatUtil implements MinecraftUtil {
	public static void printChat(String text) {
		mc.thePlayer.addChatComponentMessage(new ChatComponentText(text));
	}

	public static void sendChat_NoFilter(String text) {
		mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(text));
	}

	public static void sendChat(String text) {
		mc.thePlayer.sendChatMessage(text);
	}
	public static void sendIRCToPlayer(String content) {
		mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(content));
	}
}
