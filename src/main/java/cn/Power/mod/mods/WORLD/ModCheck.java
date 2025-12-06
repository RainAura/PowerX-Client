package cn.Power.mod.mods.WORLD;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventRender2D;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.network.play.server.S02PacketChat;

public class ModCheck extends Mod {

	private String[] modlist = new String[] { "StartOver_", "mxu", "С����", "chen_xixi", "tanker_01", "bingmo",
			"SnowDay", "�컯������", "Owenkill", "chen_duxiu" };
	private String modname;
	private TimeHelper timer = new TimeHelper();
	private List<String> offlinemod = new ArrayList();
	private List<String> onlinemod = new ArrayList();
	private Value<Boolean> showOffline = new Value("ModCheck_ShowOffline", true);
	private Value<Boolean> showOnline = new Value("ModCheck_ShowOnline", true);

	private int counter;
	private boolean isFinished;

	public ModCheck() {
		super("ModCheck", Category.WORLD);
	}

	@EventTarget
	public void onRender(EventRender2D e) {
		FontRenderer font = mc.fontRendererObj;
		List<String> listArray = Arrays.asList(modlist);
		listArray.sort((o1, o2) -> {
			return font.getStringWidth(o2) - font.getStringWidth(o1);
		});
		int counter2 = 0;
		for (String mods : listArray) {
			if (offlinemod.contains(mods) && showOffline.getValueState()) {
				font.drawStringWithShadow(mods, 2, 100 + counter2 * 10, Color.RED.getRGB());
				counter2++;
			}
			if (onlinemod.contains(mods) && showOnline.getValueState()) {
				font.drawStringWithShadow(mods, 2, 100 + counter2 * 10, Color.GREEN.getRGB());
				counter2++;
			}

		}
	}

	/*
	 * @EventTarget public void onPacket(EventPacket ep) { S02PacketChat packet =
	 * (S02PacketChat) ep.getPacket(); String[] list = new
	 * String[]{"������Ҳ����ߣ�"}; for (String str : list) { if
	 * (packet.getChatComponent().getUnformattedText().contains(str)) {
	 * ep.setCancelled(true); break; } } }
	 */

	@EventTarget
	public void onChat(EventPacket e) {
		S02PacketChat packet = (S02PacketChat) e.getPacket();
		String chat = packet.getChatComponent().getUnformattedText();
		if (chat.contains("������Ҳ����ߣ�") || chat.contains("That player is not online!")) {
			e.setCancelled(true);
			if (onlinemod.contains(modname)) {
				ClientUtil.sendChatMessage("��b" + modname + "��c �����ߣ�", ChatType.INFO);
				onlinemod.remove(modname);
				offlinemod.add(modname);
				return;
			}
			if (!offlinemod.contains(modname)) {
				ClientUtil.sendChatMessage("��b" + modname + "��c �����ߣ�", ChatType.INFO);
				offlinemod.add(modname);
			}
		}
		if (chat.contains("You cannot message this player.")) {
			e.setCancelled(true);
			if (offlinemod.contains(modname)) {
				ClientUtil.sendChatMessage("��b" + modname + "��a �����ߣ�", ChatType.WARN);
				offlinemod.remove(modname);
				onlinemod.add(modname);
				return;
			}

			if (!onlinemod.contains(modname)) {
				ClientUtil.sendChatMessage("��b" + modname + "��a ���ߣ�", ChatType.WARN);
				onlinemod.add(modname);
			}
		}
		if (chat.contains("Unknown command. Try /help for a list of commands")) {
			e.setCancelled(true);
			ClientUtil.sendChatMessage("����㲻��hypixel�Ѿ��Զ��ر�mod���", ChatType.ERROR);
			this.set(false);
		}
		if (packet.getChatComponent().getUnformattedText().contains("�Ҳ�����Ϊ \"" + modname + "\" �����")) {
			e.setCancelled(true);
		}

	}

	@EventTarget
	public void onUpdate(EventUpdate e) {
		if (timer.isDelayComplete(isFinished ? 10000L : 2000L)) {
			if (counter >= modlist.length) {
				counter = -1;
				if (!isFinished) {
					isFinished = true;
				}

			}
			counter++;
			modname = modlist[counter];
			mc.thePlayer.sendChatMessage("/message " + modname + " �ͷ�����");
			timer.reset();
		}
	}

}
