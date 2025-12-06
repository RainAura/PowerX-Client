package cn.Power.mod.mods.WORLD;

import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class FakeName extends Mod {

	public FakeName() {
		super("FakeName", Category.WORLD);
	}

	/*
	 * @EventTarget public void onpacke(EventPacket ep) { if ( ep.getPacket()
	 * instanceof S02PacketChat) { S02PacketChat packet = (S02PacketChat)
	 * ep.getPacket(); if
	 * (packet.getChatComponent().getUnformattedText().contains(mc.thePlayer.getName
	 * ())) { String temp = packet.getChatComponent().getFormattedText();
	 * ChatUtil.printChat(temp.replaceAll(mc.thePlayer.getName(),
	 * "\2479[�ճ�]\247r")); ep.setCancelled(true); } } }
	 */
}
