package cn.Power.mod.mods.MOVEMENT;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;

import cn.Power.Value;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.PLAYER.DankBobbing;
import cn.Power.mod.mods.PLAYER.Freecam;
import cn.Power.util.MathUtils;
import cn.Power.util.PlayerUtil;
import cn.Power.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class Fly extends Mod {

	private int zoom;
	private Timer boostDelay = new Timer();
	public int counter2 = 0;
	public Value<String> mode = new Value("Fly", "Mode", 0);
	public Value<Double> Speed = new Value("Fly_Speed", 1d, 1d, 10d, 0.1d);
	public Value<Double> Boost = new Value("Fly_Boost", 2d, 0d, 3d, 0.25d);
	public Value<Boolean> bobbing = new Value("Fly_BobBing", false);
	public Value<Boolean> MWMotion = new Value("Fly_MWMotion", false);
	public Value<Boolean> lobby = new Value("Fly_AutoBack", false);

	public Fly() {
		super("Fly", Category.MOVEMENT);
		this.mode.mode.add("Hypixel");
		this.mode.mode.add("Vanilla");
		this.mode.mode.add("Motion");
	}

	@Override
	public void onEnable() {

//		PlayerCapabilities playerCapabilities = new PlayerCapabilities();
//		playerCapabilities.isFlying = true;
//		mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive());
//		mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(0, (short) (-1), false));
//		mc.getNetHandler().addToSendQueue(new C13PacketPlayerAbilities(playerCapabilities));

		if (this.mode.isCurrentMode("Hypixel")) {
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
		}
		super.onEnable();
	}

	@EventTarget(Priority.LOWEST)
	public void onChat(EventPacket e) {
		if (e.getPacket() instanceof S02PacketChat) {
			S02PacketChat packet = (S02PacketChat) e.getPacket();
			String chat = packet.getChatComponent().getUnformattedText();
			if (lobby.getValueState().booleanValue()) {
				if (chat.contains("Flying or related.")) {
					new Thread(() -> {
						try {
							Thread.sleep(100L);
						} catch (InterruptedException ep) {
							ep.printStackTrace();
						}
						mc.thePlayer.sendChatMessage("/back");
					}).start();
					;

				}
			}
		}
		
		
		if (e.getPacket() instanceof C03PacketPlayer && !e.isCancelled()) {
			C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
			
			if(packet.isMoving()) {
				
				e.setCancelled(true);
				
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.getPositionX(), packet.getPositionY(), packet.getPositionZ(), false));
				
			}
				
			
		}
	}

	@EventTarget
	public void onMove(EventMove event) {
		if (this.mode.isCurrentMode("Hypixel")) {
			this.setDisplayName("Hypixel");
//			 mc.timer.elapsedPartialTicks = 2.637F;
			mc.thePlayer.onGround = false;
			mc.thePlayer.capabilities.isFlying = false;

			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.thePlayer.motionY *= 0.0d;
			} else if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.thePlayer.motionY *= 0.0d;
			}
			this.mc.thePlayer.lastReportedPosY = 0.0D;
			if (PlayerUtil.MovementInput()) {
				this.setSpeed(PlayerUtil.getBaseMovementSpeed() - 0.05d);
			} else {
				this.setSpeed(0.0d);
			}

			this.counter2 += 1;

			if (PlayerUtil.MovementInput()) {
				if (bobbing.getValueState())
					mc.thePlayer.cameraYaw = 0.090909086F * (ModManager.getModByClass(DankBobbing.class).isEnabled()
							? (float) DankBobbing.Multiplier.getValueState().doubleValue()
							: 1);
				this.setSpeed(PlayerUtil.getBaseMovementSpeed());
			} else {
				mc.thePlayer.motionX *= 0.0D;
				mc.thePlayer.motionZ *= 0.0D;
				mc.timer.timerSpeed = 1.0F;
			}
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
			switch (counter2) {
			case 1:
				break;
			case 2:
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ);
				counter2 = 0;
				break;
			case 3:
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-5, mc.thePlayer.posZ);
				counter2 = 0;
				break;
			}
			event.y = mc.thePlayer.motionY = 0f;
		}
	}

	@EventTarget
	public void onPre(EventPreMotion event) {
		int Speed = this.Speed.getValueState().intValue() / 2;
		if (this.mode.isCurrentMode("Vanilla")) {
			this.setDisplayName("Vanilla");
			this.mc.thePlayer.motionY = this.mc.thePlayer.movementInput.jump ? Speed
					: (this.mc.thePlayer.movementInput.sneak ? -Speed : 0.0);
			
			
		}
		if (this.mode.isCurrentMode("Motion")) {
			this.setDisplayName("Motion");
			
			double speed = Math.max((double) this.Speed.getValueState().floatValue(), getBaseMoveSpeed());
			
//			if(mc.thePlayer.ticksExisted % 3 == 0 && mc.thePlayer.capabilities.allowFlying) {
//				
//				mc.thePlayer.capabilities.isFlying = !mc.thePlayer.capabilities.isFlying;
//				
//				mc.playerController.setPlayerCapabilities(mc.thePlayer);
//				
//				mc.thePlayer.capabilities.isFlying = mc.thePlayer.capabilities.isFlying;
//				
//				mc.playerController.setPlayerCapabilities(mc.thePlayer);
//
//			}
			
			
			

			

				mc.thePlayer.setPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + MathUtils.getRandomInRange(0.00000000000001235423532523523532521,
								0.000000123542353252352353252 * 10),
						mc.thePlayer.posZ);
			
			if (bobbing.getValueState())
				mc.thePlayer.cameraYaw = 0.090909086F * (ModManager.getModByClass(DankBobbing.class).isEnabled()
						? (float) DankBobbing.Multiplier.getValueState().doubleValue()
						: 1);
			if (mc.thePlayer.movementInput.jump) {
				mc.thePlayer.motionY = MWMotion.getValueState() ? 0.5 : speed * 0.6D;
			} else if (mc.thePlayer.movementInput.sneak) {
				mc.thePlayer.motionY = MWMotion.getValueState() ? -0.5 : -speed * 0.6D;
			} else {
				mc.thePlayer.motionY = 0.0D;
			}
		}
	}

	@EventTarget
	public void onMove1(EventMove event) {
		int Speed = this.Speed.getValueState().intValue();

		if (this.mode.isCurrentMode("Vanilla")) {
			setSpeed(event, Speed);
		}
		if (this.mode.isCurrentMode("Motion")) {
			double speed = (double) this.Speed.getValueState().floatValue();

			if (this.boostDelay.delay(10000.0F)) {
				this.boostDelay.reset();
			}

			float boost = this.Speed.getValueState().floatValue();
			if (this.zoom > 0 && boost > 0.0F && !this.boostDelay.delay(5000.0F)) {

				mc.timer.timerSpeed = 1.0F + boost;
				if (this.zoom < 10) {
					float percent = (float) (this.zoom / 10);
					if ((double) percent > 1.0D) {
						percent = 1.0F;
					}

					mc.timer.timerSpeed = 1.0F + boost * percent;
				}
			} else {
				mc.timer.timerSpeed = 1.0F;
			}

			--this.zoom;
			double forward = (double) mc.thePlayer.movementInput.moveForward;
			double strafe = (double) mc.thePlayer.movementInput.moveStrafe;
			float yaw = mc.thePlayer.rotationYaw;
			if (forward == 0.0D && strafe == 0.0D) {
				event.setX(0.0D);
				event.setZ(0.0D);
			} else {
				if (forward != 0.0D) {
					if (strafe > 0.0D) {
						yaw += (float) (forward > 0.0D ? -45 : 45);
					} else if (strafe < 0.0D) {
						yaw += (float) (forward > 0.0D ? 45 : -45);
					}

					strafe = 0.0D;
					if (forward > 0.0D) {
						forward = 1.0D;
					} else if (forward < 0.0D) {
						forward = -1.0D;
					}
				}
				double movespeed = Math.max((double) this.Speed.getValueState().floatValue(), getBaseMoveSpeed());
				event.setX(forward * movespeed * Math.cos(Math.toRadians((double) (yaw + 90.0F)))
						+ strafe * movespeed * Math.sin(Math.toRadians((double) (yaw + 90.0F))));
				event.setZ(forward * movespeed * Math.sin(Math.toRadians((double) (yaw + 90.0F)))
						- strafe * movespeed * Math.cos(Math.toRadians((double) (yaw + 90.0F))));
			}

		}
	}

	public void setSpeed(double speed) {
		mc.thePlayer.motionX = (-MathHelper.sin(PlayerUtil.getDirection()) * speed);
		mc.thePlayer.motionZ = (MathHelper.cos(PlayerUtil.getDirection()) * speed);
	}

	public static double getBaseMoveSpeed() {
		double baseSpeed = 0.2873D;
		if (Minecraft.thePlayer.isPotionActive(Potion.moveSpeed)) {
			int amplifier = Minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= 1.0D + 0.2D * (double) (amplifier + 1);
		}

		return baseSpeed;
	}

	public static void setSpeed(EventMove moveEvent, double moveSpeed) {
		setSpeed(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe,
				mc.thePlayer.movementInput.moveForward);
	}

	public static void setSpeed(EventMove moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe,
			double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;

		if (forward != 0.0D) {
			if (strafe > 0.0D) {
				yaw += ((forward > 0.0D) ? -45 : 45);
			} else if (strafe < 0.0D) {
				yaw += ((forward > 0.0D) ? 45 : -45);
			}
			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1.0D;
			} else if (forward < 0.0D) {
				forward = -1.0D;
			}
		}

		if (strafe > 0.0D) {
			strafe = 1.0D;
		} else if (strafe < 0.0D) {
			strafe = -1.0D;
		}
		double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
		double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
		moveEvent.x = forward * moveSpeed * mx + strafe * moveSpeed * mz;
		moveEvent.z = forward * moveSpeed * mz - strafe * moveSpeed * mx;
	}

	@Override
	public void onDisable() {

		

		mc.timer.timerSpeed = 1f;
		mc.thePlayer.speedInAir = 0.02f;
		super.onDisable();
	}

}
