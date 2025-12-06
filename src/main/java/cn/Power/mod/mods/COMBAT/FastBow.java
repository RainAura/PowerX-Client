package cn.Power.mod.mods.COMBAT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.MathUtils;
import cn.Power.util.misc.STimer;
import net.minecraft.item.ItemBow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class FastBow extends Mod {
	int counter = 0;
	STimer timer = new STimer();
	public Value<String> mode = new Value("FastBow", "Mode", 0);

	public FastBow() {
		super("FastBow", Category.COMBAT);
		this.mode.mode.add("Basic");
		this.mode.mode.add("Guardian");
	}

	@EventTarget
	public void onPre(EventPreMotion em) {
		if (mode.isCurrentMode("Basic")) {
			this.setDisplayName("Basic");
		}
		if (mode.isCurrentMode("Guardian")) {
			this.setDisplayName("Guardian");
		}
		if ((this.mc.thePlayer.isUsingItem())
				&& ((this.mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow))) {
			if (mode.isCurrentMode("Guardian")) {

				if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && MathUtils.isOnGround(0.0001)) {
					mc.rightClickDelayTimer = 0;
					if (timer.delay(500)) {
						double offset = 16;
						em.setY(mc.thePlayer.posY + offset);

						for (int i = 0; i < 11; i++) {
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
									mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, true));
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
									mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
						}
						timer.reset();
					}
				}
			} else if (mode.isCurrentMode("Basic")) {
				mc.rightClickDelayTimer = 0;
				for (int i = 0; i < 20; i++) {
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
				}
			}
			mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
					BlockPos.ORIGIN, EnumFacing.DOWN));
			mc.thePlayer.stopUsingItem();
		}
	}

	@EventTarget
	public void onPacketRecieve(EventPacket event) {
		if (mc.thePlayer.inventory.getCurrentItem() != null)
			if (mode.isCurrentMode("Basic") && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow) {
				EventPacket ep = (EventPacket) event;
				Packet p = ep.getPacket();
				if (p instanceof S08PacketPlayerPosLook) {
					S08PacketPlayerPosLook pac = (S08PacketPlayerPosLook) ep.getPacket();
				}
			}
	}

}
