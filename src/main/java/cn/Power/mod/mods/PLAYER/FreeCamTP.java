package cn.Power.mod.mods.PLAYER;

import java.util.LinkedList;
import java.util.List;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.command.commands.CommandTP;
import cn.Power.events.EventBlockBB;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventTick;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.COMBAT.AntiBot;
import cn.Power.mod.mods.MOVEMENT.LookTP;
import cn.Power.util.misc.ChatUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.Vec3;

public class FreeCamTP extends Mod {
	public static EntityOtherPlayerMP freecamEntity;
	public Value<Double> Speed = new Value("FreeCamTP_Speed", 1d, 1d, 10d, 1d);
	private double x;
	private double y;
	private double z;
	public boolean succ;
	private int serverYaw;
	private Vec3 lastsuccVec;

	public FreeCamTP() {
		super("FreecamTP", Category.PLAYER);
	}

	@EventTarget
	public void onBlockBB(EventBlockBB ebb) {

		ebb.setBoundingBox(null);
		
	}
	
	@EventTarget
	public void onpacket(EventPacket ep) {
		if (ep.getPacket() instanceof S08PacketPlayerPosLook && mc.theWorld != null && !(mc.currentScreen instanceof GuiDownloadTerrain)) {

			EventManager.unregister(this);
			this.set(false);
		}
		
		if(ep.getPacket() instanceof C03PacketPlayer)
			ep.setCancelled(true);
	}


	@EventTarget
	public void onMove(EventMove em) {
		
		CommandTP.x = mc.thePlayer.posX;
		CommandTP.y = mc.thePlayer.posY;
		CommandTP.z = mc.thePlayer.posZ;
		
		this.mc.thePlayer.noClip = false;
		float speed = Speed.getValueState().floatValue();
		if (mc.thePlayer.movementInput.jump) {
			em.setY(mc.thePlayer.motionY = (double) speed);
		} else if (mc.thePlayer.movementInput.sneak) {
			em.setY(mc.thePlayer.motionY = (double) (-speed));
		} else {
			em.setY(mc.thePlayer.motionY = 0.0D);
		}

		speed = (float) Math.max((double) speed, getBaseMoveSpeed());
		double forward = (double) mc.thePlayer.movementInput.moveForward;
		double strafe = (double) mc.thePlayer.movementInput.moveStrafe;
		float yaw = mc.thePlayer.rotationYaw;
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
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
			int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= 1.0D + 0.2D * (double) (amplifier + 1);
		}

		return baseSpeed;
	}

	@Override
	public void onEnable() {
		
		succ = false;

		ChatUtil.printChat("\247aType \"-tp\" to stop Teleport!");
		this.freecamEntity = new EntityOtherPlayerMP(this.mc.theWorld, this.mc.thePlayer.getGameProfile());
		this.freecamEntity.clonePlayer(this.mc.thePlayer, true);
		this.freecamEntity.setLocationAndAngles(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ,
				this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
		this.freecamEntity.rotationYawHead = this.mc.thePlayer.rotationYawHead;
		this.freecamEntity.setEntityId(-1337);
		this.freecamEntity.setSneaking(this.mc.thePlayer.isSneaking());
		this.mc.theWorld.addEntityToWorld(this.freecamEntity.getEntityId(), this.freecamEntity);
		this.x = this.mc.thePlayer.posX;
		this.y = this.mc.thePlayer.posY;
		this.z = this.mc.thePlayer.posZ;
		
		mc.thePlayer.sendChatMessage("-tp " + x + " " + y + " " + z);
	}

	public void onDisable() {
		
		this.mc.thePlayer.setSpeed(0.0);
		

		
		if(!succ)
		this.mc.thePlayer.setLocationAndAngles(this.freecamEntity.posX, this.freecamEntity.posY,
				this.freecamEntity.posZ, this.freecamEntity.rotationYaw, this.freecamEntity.rotationPitch);
		this.mc.thePlayer.rotationYawHead = this.freecamEntity.rotationYawHead;
		this.mc.theWorld.removeEntityFromWorld(this.freecamEntity.getEntityId());
		this.mc.thePlayer.setSneaking(this.freecamEntity.isSneaking());
		this.freecamEntity = null;
		this.mc.renderGlobal.loadRenderers();
		
		if(!succ)
		this.mc.thePlayer.setPosition(this.x, this.y, this.z);
		this.mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX,
				this.mc.thePlayer.posY + 0.01, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
		this.mc.thePlayer.capabilities.isFlying = false;
		this.mc.thePlayer.noClip = false;
		this.mc.theWorld.removeEntityFromWorld(-1);
		super.onDisable();
	}
}
