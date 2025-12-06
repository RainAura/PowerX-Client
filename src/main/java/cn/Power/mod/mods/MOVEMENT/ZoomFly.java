package cn.Power.mod.mods.MOVEMENT;

import java.util.Random;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.PLAYER.DankBobbing;
import cn.Power.notification.Notification.Type;
import cn.Power.util.ClientUtil;
import cn.Power.util.MathUtils;
import cn.Power.util.PlayerUtil;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.misc.STimer;
import cn.Power.util.timeUtils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Timer;

public class ZoomFly extends Mod {
	public Value<String> mode = new Value<String>("ZoomFly", "Zoom Mode", 0);
	public Value<Double> Boost = new Value<Double>("ZoomFly_Boost ", 2.0, 0.5, 3.0, 0.05);
	public Value<Double> Dely = new Value<Double>("ZoomFly_WaitTicks", 5.0, 1.0, 10.0, 1.0);
	public Value<Double> TIMER = new Value<Double>("ZoomFly_TimerBoost", 1.0, 0.2, 3.0, 0.05);
	public Value<Double> TIMERCheck = new Value<Double>("ZoomFly_TimerCheck", 250.0, 100.0, 1000.0, 10.0);

	public Value<Double> TPBlock = new Value<Double>("ZoomFly_TPBlock", 25.0, 0.0, 50.0, 1.0);
	public Value<Boolean> tp = new Value<Boolean>("ZoomFly_tp", false);
	public Value<Boolean> wait = new Value<Boolean>("ZoomFly_Waiting", false);
	public Value<Boolean> bobbing = new Value<Boolean>("ZoomFly_BobBing", false);
	public Value<Boolean> LagBackCheck = new Value<Boolean>("ZoomFly_LagBackCheck", false);

	private STimer lastCheck = new STimer();
	private STimer damageCheck = new STimer();

	private STimer C13Check = new STimer();
	private TimerUtil timer = new TimerUtil();
	private double movementSpeed;
	int counter, level;
	double moveSpeed, lastDist;
	boolean b2, nigga;
	boolean jump = false;
	boolean OnFly = false;
	private int posYStage;
	int Flydely;

	private double y;

	public ZoomFly() {
		super("ZoomFly", Category.MOVEMENT);
		this.mode.mode.add("Hypixel");
	}

	public static double random(double min, double max) {
		Random random = new Random();
		return min + (random.nextDouble() * (max - min));
	}

	@Override
	public void onEnable() {

		counter = 0;

	}

	@Override
	public void onDisable() {
		if (Minecraft.thePlayer == null || Minecraft.theWorld == null)
			return;

		Minecraft.thePlayer.motionY = 0;
		Minecraft.thePlayer.motionX = 0;
		Minecraft.thePlayer.motionZ = 0;
		Timer.timerSpeed = 1.0f;
	}

	@EventTarget
	public void onPack(EventPacket event) {
		if (event.getEventType() == EventType.RECEIVE) {
			if (event.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
				if (packet.entityID == Minecraft.thePlayer.getEntityId()) {
					event.setCancelled(true);
				}
			}
			
			if (event.getPacket() instanceof S27PacketExplosion) {
				event.setCancelled(true);
			}
		}
	}

	@EventTarget
	private void onUpdate(EventPreMotion e) {
		if (Minecraft.thePlayer.isMovingKeyBindingActive())
			this.counter++;
		
		mc.thePlayer.motionY = 0;
	}

	@native0
	@EventTarget
	private void onMove(EventMove e) {
		double forward = MovementInput.moveForward;
		double strafe = MovementInput.moveStrafe;
		float yaw = Minecraft.thePlayer.rotationYaw;

		if (this.mode.isCurrentMode("Hypixel")) {
			
			
		
			if ((forward == 0.0F && strafe == 0.0F) || this.counter <= 10) {
				e.x = 0.0D;
				e.z = 0.0D;

				return;
			} else if (forward != 0.0F) {
				if (strafe >= 1.0F) {
					yaw += (float) (forward > 0.0F ? -45 : 45);
					strafe = 0.0F;
				} else if (strafe <= -1.0F) {
					yaw += (float) (forward > 0.0F ? 45 : -45);
					strafe = 0.0F;
				}

				if (forward > 0.0F) {
					forward = 1.0F;
				} else if (forward < 0.0F) {
					forward = -1.0F;
				}
			}

			this.counter = 0;

			if (bobbing.getValueState())
				Minecraft.thePlayer.cameraYaw = 0.090909086F * (ModManager.getModByClass(DankBobbing.class).isEnabled()
						? (float) DankBobbing.Multiplier.getValueState().doubleValue()
						: 1);

			moveSpeed = 0.2863020626850092;

			e.setX(forward * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0F)));
			e.setZ(forward * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0F)));
			if (forward == 0.0F && strafe == 0.0F) {
				e.setX(0.0);
				e.setZ(0.0);
			}

		}
	}

	@EventTarget
	public void onEvent(EventPacket ep) {
		Packet<?> p = ep.getPacket();
		if (p instanceof S08PacketPlayerPosLook) {

			Minecraft.thePlayer.motionX *= 0;
			Minecraft.thePlayer.motionZ *= 0;
			Minecraft.thePlayer.jumpMovementFactor = 0;
			Timer.timerSpeed = 1.0f;
//			this.toggle();

			lastCheck.reset();
		}
	}

	double getBaseMoveSpeed() {
		double baseSpeed = 0.2873D;
		if (Minecraft.thePlayer.isPotionActive(Potion.moveSpeed)) {
			int amplifier = Minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (double) (amplifier + 1);
		}
		return baseSpeed;
	}

}