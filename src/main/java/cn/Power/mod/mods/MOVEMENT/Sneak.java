package cn.Power.mod.mods.MOVEMENT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class Sneak extends Mod {
	public static boolean clientSide = true;
	public static boolean isModEnabled = false;
	public static boolean lessPackets = true;

	public Sneak() {
		super("Sneak", Category.MOVEMENT);
	}

	@EventTarget(4)
	public void afterMotionUpdate(EventPostMotion event) {
		if(!mc.thePlayer.onGround)
			mc.getNetHandler().addToSendQueue(
					new C0BPacketEntityAction(3, C0BPacketEntityAction.Action.START_SNEAKING));

	}

	@EventTarget(4)
	public void beforeMotionUpdate(EventPreMotion event) {
			mc.getNetHandler().addToSendQueue(
					new C0BPacketEntityAction(3, C0BPacketEntityAction.Action.STOP_SNEAKING));
	}

	@Override
	public void onDisable() {
		super.onDisable();
			mc.getNetHandler().addToSendQueue(
					new C0BPacketEntityAction(3, C0BPacketEntityAction.Action.STOP_SNEAKING));

	}

	@Override
	public void onEnable() {
		super.onEnable();
		isModEnabled = true;
	}
}
