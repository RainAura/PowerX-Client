package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventRespawn;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

public class AutoCraft extends Mod {

	private boolean needSpoofIron;
	private boolean needSpoofGhead;

	public Value<Boolean> iron_pack = new Value<Boolean>("AutoCraft_Iron_Pack", true);
	public Value<Boolean> golden_head = new Value<Boolean>("AutoCraft_Golden_Head", true);

	
	public AutoCraft() {
		super("AutoCraft", Category.WORLD);
	}

	@Override
	public void onEnable() {
		needSpoofGhead = false;
		needSpoofIron = false;
	}

	@EventTarget
	public void onRes(EventRespawn re) {
		needSpoofGhead = false;
		needSpoofIron = false;
	}

	
	public final boolean IsInvFullGPP() {
		for (int i = 9; i < 45; i++) {
			final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			if (stack == null || stack.getItem() == Items.golden_apple) {
				return false;
			}
		}
		return true;
	}
	
	public final boolean IsInvFullIRON() {
		for (int i = 9; i < 45; i++) {
			final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			if (stack == null || stack.getItem() == Items.iron_ingot) {
				return false;
			}
		}
		return true;
	}
	
	// 5 24 0 1

	@EventTarget
	public void onChat(EventPacket e) {

		if (e.getEventType() == EventType.RECEIVE) {

			if (e.getPacket() instanceof S02PacketChat && mc.currentScreen == null) {
				S02PacketChat packet = (S02PacketChat) e.getPacket();
				String chat = packet.getChatComponent().toString();
				if (chat.contains("text='You have all the items to craft a")) {

					if (!this.IsInvFullIRON() && chat.contains("TextComponent{text='Engineering Craft: Iron Pack', siblings=") && iron_pack.getValueState()) {
						Minecraft.thePlayer.sendChatMessage("/internal_autocraftitem IRON_INGOTS");
						needSpoofIron = true;
					} else if (!this.IsInvFullGPP() && chat.contains("TextComponent{text='Bloodcraft Craft: Golden Head', siblings=") && golden_head.getValueState()) {
						Minecraft.thePlayer.sendChatMessage("/internal_autocraftitem GOLDEN_HEAD");
						needSpoofGhead = true;
					}

				}

			} else if (e.getPacket() instanceof S2DPacketOpenWindow) {

				S2DPacketOpenWindow open = (S2DPacketOpenWindow) e.getPacket();

				if (open.getWindowTitle().toString().equals(
						"TranslatableComponent{key='Crafting Table', args=[], siblings=[], style=Style{hasParent=false, color=null, bold=null, italic=null, underlined=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null}}")) {
					
					if (open.getSlotCount() == 45 && this.needSpoofGhead) {

						needSpoofGhead = false;

						Minecraft.getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C0EPacketClickWindow(open.getWindowId(), 24, 0, 1, null, (short) 0));
						
						
						Minecraft.getNetHandler().getNetworkManager()
						.sendPacketNoEvent(new C0DPacketCloseWindow(open.getWindowId()));
				

						e.setCancelled(true);
					}else if (open.getSlotCount() == 45 && needSpoofIron && !this.needSpoofGhead) {

						needSpoofIron = false;

						Minecraft.getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C0EPacketClickWindow(open.getWindowId(), 24, 0, 1, null, (short) 0));
						
						
						Minecraft.getNetHandler().getNetworkManager()
						.sendPacketNoEvent(new C0DPacketCloseWindow(open.getWindowId()));
				

						e.setCancelled(true);
					}
				}
			}

		}

	}

}
