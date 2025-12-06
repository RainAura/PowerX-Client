package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Fly;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;

public class BoatFlyHelper extends Mod {

	public BoatFlyHelper() {
		super("BoatFlyHelper", Category.WORLD);
	}

	@EventTarget
	public void onUpdate(EventUpdate event) {
		if (mc.thePlayer.ridingEntity != null && mc.thePlayer.ridingEntity instanceof EntityBoat) {
			if(ModManager.getModByClass(Fly.class).isEnabled()) {
				mc.getNetHandler().getNetworkManager().sendPacket(new C0CPacketInput(0.0f, 0.0f, true, true));
				
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
						mc.thePlayer.posX, mc.thePlayer.ridingEntity.getCollisionBoundingBox().maxY + 0.06, mc.thePlayer.posZ, false));
			}
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
