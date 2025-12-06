package cn.Power.util;

import net.minecraft.network.Packet;

public class NetUtil implements MinecraftUtil {
	public static void sendPacketNoEvents(Packet packet) {
		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
	}

	public static void sendPacket(Packet packet) {
		NetUtil.mc.thePlayer.sendQueue.addToSendQueue(packet);
	}
}