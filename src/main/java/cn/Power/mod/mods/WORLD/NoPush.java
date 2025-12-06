package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventPushBlock;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Fly;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;

public class NoPush extends Mod {

	public NoPush() {
		super("NoPush", Category.WORLD);
	}

	@EventTarget
	public void onUpdate(EventPushBlock event) {
		event.setCancelled(true);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
