package cn.Power.mod.mods.PLAYER;

import java.util.concurrent.CompletableFuture;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventBlockBB;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.AntiBot;
import cn.Power.mod.mods.MOVEMENT.LookTP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

public class Freecam extends Mod {
	public static EntityOtherPlayerMP freecamEntity;
	public Value<Double> Speed = new Value("Freecam_Speed", 1d, 1d, 10d, 1d);
	public Value<Boolean> back = new Value("Freecam_SetBack", true);
	public Value<Boolean> FreecamTP = new Value("Freecam_GetPos", false);
	private double x;
	private double y;
	private double z;

	public Freecam() {
		super("Freecam", Category.PLAYER);
	}

	@EventTarget
	public void onBlockBB(EventBlockBB ebb) {
		ebb.setBoundingBox(null);
	}
	
	@EventTarget
	public void onPre(EventPreMotion e) {
		e.yaw = Freecam.freecamEntity.rotationYaw;
		e.pitch = Freecam.freecamEntity.rotationPitch;
		e.x = Freecam.freecamEntity.posX;
		e.y = Freecam.freecamEntity.posY;
		e.z = Freecam.freecamEntity.posZ;
		e.onGround = Freecam.freecamEntity.onGround;
	}

	@EventTarget
	public void onpacket(EventPacket ep) {
		
		if (ep.getPacket() instanceof C03PacketPlayer) {
			C03PacketPlayer packet = (C03PacketPlayer) ep.getPacket();
			packet.yaw = Freecam.freecamEntity.rotationYaw;
			packet.pitch = Freecam.freecamEntity.rotationPitch;
			packet.x = Freecam.freecamEntity.posX;
			packet.y = Freecam.freecamEntity.posY;
			packet.z = Freecam.freecamEntity.posZ;
			packet.onGround = Freecam.freecamEntity.onGround;
		} else if (ep.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
			C03PacketPlayer.C04PacketPlayerPosition packet = (C03PacketPlayer.C04PacketPlayerPosition) ep.getPacket();
			packet.yaw = Freecam.freecamEntity.rotationYaw;
			packet.pitch = Freecam.freecamEntity.rotationPitch;
			packet.x = Freecam.freecamEntity.posX;
			packet.y = Freecam.freecamEntity.posY;
			packet.z = Freecam.freecamEntity.posZ;
			packet.onGround = Freecam.freecamEntity.onGround;
		} else if (ep.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
			C03PacketPlayer.C05PacketPlayerLook packet = (C03PacketPlayer.C05PacketPlayerLook) ep.getPacket();
			packet.yaw = Freecam.freecamEntity.rotationYaw;
			packet.pitch = Freecam.freecamEntity.rotationPitch;
			packet.x = Freecam.freecamEntity.posX;
			packet.y = Freecam.freecamEntity.posY;
			packet.z = Freecam.freecamEntity.posZ;
			packet.onGround = Freecam.freecamEntity.onGround;
		}
	}

	@EventTarget
	public void onMove(EventMove em) {
		Minecraft.thePlayer.noClip = true;
		float speed = Speed.getValueState().floatValue();
		if (Minecraft.thePlayer.movementInput.jump) {
			em.setY(Minecraft.thePlayer.motionY = (double) speed);
		} else if (Minecraft.thePlayer.movementInput.sneak) {
			em.setY(Minecraft.thePlayer.motionY = (double) (-speed));
		} else {
			em.setY(Minecraft.thePlayer.motionY = 0.0D);
		}

		speed = (float) (speed * 0.6);
		double forward = (double) MovementInput.moveForward;
		double strafe = (double) MovementInput.moveStrafe;
		float yaw = Minecraft.thePlayer.rotationYaw;
		if (forward == 0.0D && strafe == 0.0D) {
			em.setX(0.0D);
			em.setZ(0.0D);
		} else {
			if (forward != 0.0D) {
				if (strafe > 0.0D) {
					strafe = 1.0D;
					yaw += (float) (forward > 0.0D ? -45 : 45);
				} else if (strafe < 0.0D) {
					yaw += (float) (forward > 0.0D ? 45 : -45);
				}

				strafe = 0.0D;
				if (forward > 0.0D) {
					forward = 1.0D;
				} else {
					forward = -1.0D;
				}
			}
			em.setX(forward * (double) speed * Math.cos(Math.toRadians((double) (yaw + 90.0F)))
					+ strafe * (double) speed * Math.sin(Math.toRadians((double) (yaw + 90.0F))));
			em.setZ(forward * (double) speed * Math.sin(Math.toRadians((double) (yaw + 90.0F)))
					- strafe * (double) speed * Math.cos(Math.toRadians((double) (yaw + 90.0F))));
		}
	}

	public static double getBaseMoveSpeed() {
		double baseSpeed = 0.2873D;
		Minecraft.getMinecraft();
		if (Minecraft.thePlayer.isPotionActive(Potion.moveSpeed)) {
			Minecraft.getMinecraft();
			int amplifier = Minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= 1.0D + 0.2D * (double) (amplifier + 1);
		}

		return baseSpeed;
	}

	@Override
	public void onEnable() {
		
		Freecam.freecamEntity = new EntityOtherPlayerMP(Minecraft.theWorld, Minecraft.thePlayer.getGameProfile());
		Freecam.freecamEntity.clonePlayer(Minecraft.thePlayer, true);
		Freecam.freecamEntity.setLocationAndAngles(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ,
				Minecraft.thePlayer.rotationYaw, Minecraft.thePlayer.rotationPitch);
		Freecam.freecamEntity.rotationYawHead = Minecraft.thePlayer.rotationYawHead;
		Freecam.freecamEntity.setEntityId(-1332);
		Freecam.freecamEntity.setSneaking(Minecraft.thePlayer.isSneaking());
		Minecraft.theWorld.addEntityToWorld(Freecam.freecamEntity.getEntityId(), Freecam.freecamEntity);
		this.x = Minecraft.thePlayer.posX;
		this.y = Minecraft.thePlayer.posY;
		this.z = Minecraft.thePlayer.posZ;
	}

	public void onDisable() {
		
		Minecraft.thePlayer.setSpeed(0.0);
		
		if(FreecamTP.getValueState())
			LookTP.formattedMsg(
					"§a目标 §r" + "FreeCam TP" 
							+ "§a坐标§BX: §r" + (int) Minecraft.thePlayer.posX  + " §BY: §r" + (int) Minecraft.thePlayer.posY  + " §BZ: §r" + Minecraft.thePlayer.posZ + "  ",
					"&9[§e§l点我TP实时坐标&r&9]", "&aClick to TP~",
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "-tp "+ (double) (Minecraft.thePlayer.posX + Math.random()) + " " + (double) (Minecraft.thePlayer.posY - Math.random())  + " " + (double) (Minecraft.thePlayer.posZ + Math.random())));
		
		
		if(back.getValueState())
		Minecraft.thePlayer.setLocationAndAngles(Freecam.freecamEntity.posX, Freecam.freecamEntity.posY,
				Freecam.freecamEntity.posZ, Freecam.freecamEntity.rotationYaw, Freecam.freecamEntity.rotationPitch);
		Minecraft.thePlayer.rotationYawHead = Freecam.freecamEntity.rotationYawHead;
		Minecraft.theWorld.removeEntityFromWorld(Freecam.freecamEntity.getEntityId());
		Minecraft.thePlayer.setSneaking(Freecam.freecamEntity.isSneaking());
		Freecam.freecamEntity = null;
		Mod.mc.renderGlobal.loadRenderers();
		
		if(back.getValueState())
		Minecraft.thePlayer.setPosition(this.x, this.y, this.z);
		Minecraft.thePlayer.capabilities.isFlying = false;
		Minecraft.thePlayer.noClip = false;
		Minecraft.theWorld.removeEntityFromWorld(-1);
		super.onDisable();
	}
}
