package cn.Power.mod.mods.COMBAT;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRespawn;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.NoFall;
import cn.Power.mod.mods.MOVEMENT.Speed;
import cn.Power.mod.mods.WORLD.Dismount;
import cn.Power.notification.Notification.Type;
import cn.Power.util.Colors;
import cn.Power.util.misc.ChatUtil;
import cn.Power.util.misc.STimer;
import cn.Power.util.misc.Timer;
import cn.Power.util.timeUtils.TimeHelper;
import cn.Power.util.timeUtils.TimerUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.ChatComponentText;

public class Velocity extends Mod {
	public Value<Boolean> warnning = new Value("Velocity_Warnning", false);

	private Value<String> mode;
	private Value<Double> xz;
	private Value<Double> y;
	
	public TimeHelper timer = new TimeHelper();
	
	public TimeHelper timerMod = new TimeHelper();
	
	public TimeHelper timerCancel = new TimeHelper();
	
	public boolean damaged;
	
	public boolean hasFlagged;
	
	public boolean Cancel;
	
	public int checked ; 

	public Velocity() {
		super("Velocity", Category.COMBAT);
		this.mode = new Value<String>("Velocity", "Mode", 0);
		this.xz = new Value<Double>("Velocity_X/Z", 0.0, 0.0, 1.0, 0.01);
		this.y = new Value<Double>("Velocity_Y", 0.0, 0.0, 1.0, 0.01);
		this.mode.addValue("Hypixel");
		this.mode.addValue("AAC1");
		this.mode.addValue("AAC2");

	}
	
	@EventTarget
	public void onRespawn (EventRespawn e) {
		damaged = false;
		
		Cancel = false;
		
		hasFlagged = false;
		
		checked = 0;
	}

	@EventTarget
	public void onEvent(final EventPreMotion event) {
		

		this.showValue = null;
		this.setColor(Colors.AQUA.c);
		if (this.mode.isCurrentMode("AAC1")) {
			this.setDisplayName("AAC1");
			if (this.mc.thePlayer.hurtTime == 1 || this.mc.thePlayer.hurtTime == 2 || this.mc.thePlayer.hurtTime == 3
					|| this.mc.thePlayer.hurtTime == 4 || this.mc.thePlayer.hurtTime == 5
					|| this.mc.thePlayer.hurtTime == 6 || this.mc.thePlayer.hurtTime == 7
					|| this.mc.thePlayer.hurtTime == 8) {
				if (this.mc.thePlayer.onGround) {
					return;
				}
				double yaw = this.mc.thePlayer.rotationYawHead;
				yaw = Math.toRadians(yaw);
				double dX = (-Math.sin(yaw)) * 0.08;
				double dZ = Math.cos(yaw) * 0.08;
				if (this.mc.thePlayer.getHealth() >= 6.0f) {
					this.mc.thePlayer.motionX = dX;
					this.mc.thePlayer.motionZ = dZ;
				}
			}
		}

		else if (this.mode.isCurrentMode("AAC2")) {
			this.setDisplayName("AAC2");
			if (this.mc.thePlayer.hurtTime > 0 && this.mc.thePlayer.fallDistance < 3.0f) {
				if (this.mc.thePlayer.moveForward == 0.0f && this.mc.thePlayer.moveStrafing == 0.0f) {
					this.mc.thePlayer.motionY -= this.y.getValueState();
					this.mc.thePlayer.motionX *= this.xz.getValueState();
					this.mc.thePlayer.motionZ *= this.xz.getValueState();
					this.mc.thePlayer.motionY += this.y.getValueState();
				} else {
					this.mc.thePlayer.motionY -= this.y.getValueState();
					this.mc.thePlayer.motionX *= (this.xz.getValueState()) + 0.2;
					this.mc.thePlayer.motionZ *= (this.xz.getValueState()) + 0.2;
					this.mc.thePlayer.motionY += this.y.getValueState();
				}
			}
		}
	}
	

	@EventTarget
	public void onPack(EventPacket event) {
		if (this.mode.isCurrentMode("Hypixel")) {
			this.setDisplayName("Hypixel");
			if (event.getEventType() == EventType.RECEIVE) {
				if (event.getPacket() instanceof S12PacketEntityVelocity) {
					S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
					if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
						event.setCancelled(true);
						if (mc.thePlayer.onGround || packet.getMotionY() / 8000.0D < .2 || packet.getMotionY() / 8000.0D > .41995) {
							mc.thePlayer.motionY = packet.getMotionY() / 8000.0D;
							mc.thePlayer.addChatMessage(new ChatComponentText("§cKnockback tick: " + mc.thePlayer.ticksExisted));
						}
					}
				}

					}
				}
				if (event.getPacket() instanceof S27PacketExplosion) {
					S27PacketExplosion packet = (S27PacketExplosion) event.getPacket();
					
					if(warnning.getValueState())
						ChatUtil.printChat("Explosion received!!!!!!!" + packet.field_149152_f + " " + packet.field_149153_g + " " + packet.field_149159_h + " " + packet.getStrength() + " " + packet.getAffectedBlockPositions().size());
					
					event.setCancelled(true);
				}
			}
}
