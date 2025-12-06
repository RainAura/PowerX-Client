package cn.Power.mod.mods.PLAYER;

import java.util.Iterator;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.util.PlayerUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

public class AntiDesync extends Mod {
	private int lastSlot = -1;

	public AntiDesync() {
		super("AntiDesync", Category.PLAYER);
	}

	@EventTarget
	public void onEnable() {
		lastSlot = -1;
	}

	@EventTarget
	public void onEventUpdate(EventUpdate eu) {
		if (Client.lastSlot != -1 && Client.lastSlot != mc.thePlayer.inventory.currentItem && !Client.blockActionsForHealing) {
			Minecraft.thePlayer.inventory.currentItem = Client.lastSlot;
		}
	}

}