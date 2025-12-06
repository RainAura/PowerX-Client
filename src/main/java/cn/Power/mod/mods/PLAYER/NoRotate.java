package cn.Power.mod.mods.PLAYER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventPacket;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Mod {
	private int packetCounter;

	TimeHelper spamTimer = new TimeHelper();
	TimeHelper deactivationDelay = new TimeHelper();

	public NoRotate() {
		super("NoRotate", Category.PLAYER);
	}

//	@EventTarget
//	public void onEvent(EventPacket e) {
//		if (e.getPacket() instanceof S08PacketPlayerPosLook) {
//			S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
//			if (this.mc.thePlayer.rotationYaw > -90.0f && this.mc.thePlayer.rotationPitch < 90.0f) {
//				packet.yaw = this.mc.thePlayer.rotationYaw;
//				packet.pitch = this.mc.thePlayer.rotationPitch;
//			}
//		}
//	}

	/*
	 * @EventTarget public void Code(EventPacket e) { if (e.isIncoming() &&
	 * e.getPacket() instanceof S08PacketPlayerPosLook) { S08PacketPlayerPosLook
	 * packet = (S08PacketPlayerPosLook)e.getPacket(); packet.yaw =
	 * mc.thePlayer.rotationYaw; packet.pitch = mc.thePlayer.rotationPitch; } }
	 */

	/*
	 * @EventTarget public void onReceivePacket(EventReceivePacket event) { if
	 * (event.getPacket() instanceof S08PacketPlayerPosLook && mc.theWorld != null)
	 * { S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)event.getPacket();
	 * packet.yaw = mc.thePlayer.rotationYaw; packet.pitch =
	 * mc.thePlayer.rotationPitch; } }
	 */

	/*
	 * @EventTarget public void onReceivePacket(EventPacket e) { if (mc.thePlayer !=
	 * null && mc.thePlayer.ticksExisted > 0) { if ( e.getPacket() instanceof
	 * S08PacketPlayerPosLook && this.deactivationDelay.delay(2000.0F)) {
	 * ++this.packetCounter; S08PacketPlayerPosLook pac =
	 * (S08PacketPlayerPosLook)e.getPacket(); pac.yaw = mc.thePlayer.rotationYaw;
	 * pac.pitch = mc.thePlayer.rotationPitch; } } }
	 */
}
