package cn.Power.mod.mods.MOVEMENT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventJump;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRespawn;
import cn.Power.events.EventStep;
import cn.Power.events.EventTick;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.mod.mods.RENDER.Hud;
import cn.Power.mod.mods.WORLD.Dismount;
import cn.Power.notification.Notification.Type;
import cn.Power.util.BlockUtils;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.InventoryUtils;
import cn.Power.util.MathUtils;
import cn.Power.util.PlayerUtil;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.Timer;
import cn.Power.util.misc.ChatUtil;
import cn.Power.util.timeUtils.NovoTimer;
import cn.Power.util.timeUtils.TimeHelper;
import cn.Power.util.timeUtils.TimerUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

public class Speed extends Mod {
	private double zDist;
	private double xDist;
	TimeHelper ticks = new TimeHelper();
	TimeHelper other = new TimeHelper();

	TimeHelper ticksX = new TimeHelper();

//	public Value<Double> DMGBoostLimit = new Value<Double>("Speed_DMGBoostLimit", 0.1d, 0.0d, 0.3d, 0.01d);

	public Value<Double> Base_Timer_Min = new Value<Double>("Speed_Base Timer Min", 0.98d, 0.4d, 3.0d, 0.01d);
	public Value<Double> Base_Timer_Max = new Value<Double>("Speed_Base Timer Max", 1.17d, 0.4d, 3.0d, 0.01d);
	
	public Value<Double> Aura_Timer_Min = new Value<Double>("Speed_Aura Timer Min", 0.98d, 0.4d, 3.0d, 0.01d);
	public Value<Double> Aura_Timer_Max = new Value<Double>("Speed_Aura Timer Max", 1.17d, 0.4d, 3.0d, 0.01d);
	
	public Value<Double> TimerTick = new Value<Double>("Speed_TimerTick", 77.2d, 0.0d, 200.0d, 1.00d);

	public Value<Double> ICESPEED = new Value<Double>("Speed_Ice Multifier", 1.07d, 1.0d, 2.0d, 0.01d);
	public Value<Double> PotionMultifier = new Value<Double>("Speed_PotionMultifier", 0.15d, 0.0d, 0.2d, 0.01d);

	public Value<Double> AutoDisable = new Value<Double>("Speed_AutoDisable", 477.7d, 50.0d, 750.0d, 50d);
	public Value<Boolean> AutoDisable_S = new Value<Boolean>("Speed_AutoDisable", false);
	public Value<Boolean> Ability = new Value<Boolean>("Speed_AbilitySpeed", false);

	public Value<Boolean> LagBackCheck = new Value<Boolean>("Speed_LagBackCheck", true);
//	public Value<Boolean> dmgboost = new Value<Boolean>("Speed_HurtBoost", false);

	public double moveSpeed;
	public int stage;
	public boolean shouldslow = false;
	private double distance;
	public static Timer timer = new Timer();
	private Timer lastCheck = new Timer();
	public static double waterSpeed;

	public static double multifierX;
	public static double multifierZ;

	public int level = 1;
	double less, stair;
	public double slow;
	boolean collided = false, lessSlow;
	boolean ICE = false;
	public boolean cooldown;
	public boolean groundswitch;
	private boolean shouldSpoof = false;

	public S27PacketExplosion toP = null;
	private double Y;
	private double DMGspeed;
	private double DMGy;
	private boolean DMGboost;

	public static double getRandomInRange(double min, double max) {
		Random random = new Random();
		double range = max - min;
		double scaled = random.nextDouble() * range;
		if (scaled > max) {
			scaled = max;
		}
		double shifted = scaled + min;

		if (shifted > max) {
			shifted = max;
		}
		return shifted;
	}

	public Speed() {
		super("Speed", Category.MOVEMENT);

		Y = ThreadLocalRandom.current().nextDouble(0.39999998688698, 0.402);
	}

	@EventTarget
	public void onJump(EventJump e) {
		e.setCancelled(true);
	}

	@EventTarget
	public void donative2(EventPreMotion e) {
		double xDist = Minecraft.thePlayer.posX - Minecraft.thePlayer.prevPosX;
		double zDist = Minecraft.thePlayer.posZ - Minecraft.thePlayer.prevPosZ;
		this.distance = Math.sqrt(xDist * xDist + zDist * zDist);

		if (mc.thePlayer.isMovingKeyBindingActive() && !mc.thePlayer.isSneaking()
				&& !ModManager.getModByClass(Scaffold.class).isEnabled()) {
			double pX = Minecraft.getMinecraft().thePlayer.lastReportedPosX;
			double pY = Minecraft.getMinecraft().thePlayer.lastReportedPosY
					+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight();
			double pZ = Minecraft.getMinecraft().thePlayer.lastReportedPosZ;
			double eX = Minecraft.getMinecraft().thePlayer.posX;
			double eY = Minecraft.getMinecraft().thePlayer.posY
					+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight();
			double eZ = Minecraft.getMinecraft().thePlayer.posZ;
			double dX = pX - eX;
			double dY = pY - eY;
			double dZ = pZ - eZ;
			double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
			double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
			double pitch = Math.toDegrees(Math.atan2(dH, dY));

			e.yaw = (float) yaw;

			mc.thePlayer.rotationYawHead = (float) yaw;

		}

	}

	@EventTarget
	public void onPacket(EventPacket e) {
		if (e.getPacket() instanceof S27PacketExplosion) {
			S27PacketExplosion pe = (S27PacketExplosion) e.packet;

			if (pe.getStrength() == 0 && pe.getAffectedBlockPositions().isEmpty()) {
				e.setCancelled(true);

//				if (dmgboost.getValueState()) {
//					this.DMGspeed = Math.hypot(0.153 + pe.getX() / 8500.0f, 0.153 + pe.getZ() / 8500.0f);
//
//					this.DMGboost = true;
//				}

			}

		}
		if (e.getPacket() instanceof S12PacketEntityVelocity) {
			if (((S12PacketEntityVelocity) e.getPacket()).entityID == Minecraft.thePlayer.getEntityId()) {
				e.setCancelled(true);
			}
		}
	}

	public Block getBlock(AxisAlignedBB bb) {
		int y = (int) bb.minY;

		for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
			for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
				Block block = Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (block != null) {
					return block;
				}
			}
		}

		return null;
	}

	static NovoTimer timer_ = new NovoTimer();
	static int lastKey = -1;

	@EventTarget(4)
	public void onUpdate(EventPreMotion e) {
		if (Jesus.Jesuss || ModManager.getModByClass(Scaffold.class).isEnabled()
				|| ModManager.getModByClass(Fly.class).isEnabled() || Minecraft.thePlayer.getAir() < 300
				|| !Minecraft.thePlayer.isMovingKeyBindingActive() || Jesus.isOnLiquid()) {
			return;
		}

		if (this.mc.thePlayer.getSpeed() > 0) {
			if (this.mc.thePlayer.ticksExisted % 3 == 0) {
				if ((double) this.mc.thePlayer.fallDistance < 3) {
					if (mc.gameSettings.keyBindForward.isPressed()
							&& lastKey != mc.gameSettings.keyBindForward.getKeyCode()) {
						timer_.b();
						lastKey = mc.gameSettings.keyBindForward.getKeyCode();
					}
					if (mc.gameSettings.keyBindBack.isPressed()
							&& lastKey != mc.gameSettings.keyBindBack.getKeyCode()) {
						timer_.b();
						lastKey = mc.gameSettings.keyBindBack.getKeyCode();
					}
					if (mc.gameSettings.keyBindLeft.isPressed()
							&& lastKey != mc.gameSettings.keyBindLeft.getKeyCode()) {
						timer_.b();
						lastKey = mc.gameSettings.keyBindLeft.getKeyCode();
					}
					if (mc.gameSettings.keyBindRight.isPressed()
							&& lastKey != mc.gameSettings.keyBindRight.getKeyCode()) {
						timer_.b();
						lastKey = mc.gameSettings.keyBindRight.getKeyCode();
					}
					if (!timer_.a(40.0) || mc.thePlayer.ticksExisted % 3 == 0) {
						e.onGround = true;

					}
				}
			}
		}

	}

	public static boolean isOnIce() {
		final Block blockUnder = getBlockUnder();
		return blockUnder instanceof BlockIce || blockUnder instanceof BlockPackedIce;
	}

	public static Block getBlockUnder() {
		final EntityPlayerSP player = mc.thePlayer;
		return mc.theWorld.getBlockState(
				new BlockPos(player.posX, StrictMath.floor(player.getEntityBoundingBox().minY) - 1.0, player.posZ))
				.getBlock();
	}

	@EventTarget
	public void onMove3(EventMove e) {

		if (Jesus.Jesuss || ModManager.getModByClass(Scaffold.class).isEnabled()
				|| ModManager.getModByClass(Fly.class).isEnabled() || Minecraft.thePlayer.getAir() < 300
				|| Jesus.isOnLiquid()) {
			stage = -1;
			return;
		}

		if (xDist > 200)
			xDist = 0;
		
		float timerMin = Base_Timer_Min.getValueState().floatValue();
		float timerMax = Base_Timer_Max.getValueState().floatValue();

		if(KillAura.Target != null) {
			timerMin = this.Aura_Timer_Min.getValueState().floatValue();
			timerMax = this.Aura_Timer_Max.getValueState().floatValue();
		}

		if (Minecraft.thePlayer.isMovingKeyBindingActive() && (xDist++ > TimerTick.getValueState().intValue())
				&& Minecraft.thePlayer.isMovingKeyBindingActive()
				&& timerMax > timerMin)
			net.minecraft.util.Timer.timerSpeed = (float) (timerMax > Base_Timer_Min
					.getValueState().floatValue()
							? ThreadLocalRandom.current().nextDouble(timerMin,
									timerMax)
							: timerMin - Math.random() / 10);
		else
			net.minecraft.util.Timer.timerSpeed = 0.97f;

		if (Minecraft.thePlayer.fallDistance > 3.0) {
			net.minecraft.util.Timer.timerSpeed = 1.0f;
		}

		moveSpeed = this.getBaseSpeed();

		if (stage < 1) {
			++stage;
			distance = 0.0;
		}

		if (stage < 0) {
			++stage;
			return;
		}
		double xMotionSpeed;

		if (stage == 2 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)
				&& mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {

			xMotionSpeed = Y;
			if (mc.thePlayer.isPotionActive(Potion.jump)) {
				xMotionSpeed += (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f;
			}
			e.setY(mc.thePlayer.motionY = xMotionSpeed);

			if (Ability.getValueState()) {
				moveSpeed += mc.thePlayer.capabilities.getWalkSpeed() - 0.1;
			}

			moveSpeed *= 1.8;

		} else if (stage == 3) {

			xMotionSpeed = (float) (0.72
					* (distance - this.getBaseSpeed() * (isOnIce() ? ICESPEED.getValueState().floatValue() : 1.0)));

			moveSpeed = distance - xMotionSpeed;

		} else {

			if ((mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
					mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0
					|| mc.thePlayer.isCollidedVertically) && stage > 0) {
				stage = ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) ? 1 : 0);
			}
			moveSpeed = distance - distance / (this.isOnIce() ? 91.0 : 99.00000014353486);

		}



//		moveSpeed += this.DMGboost ? Math.min(this.DMGspeed, DMGBoostLimit.getValueState()) : 0;
//		this.DMGboost = false;



		moveSpeed = Math.max(moveSpeed, this.getBaseSpeed() * 0.98f);
		xMotionSpeed = mc.thePlayer.movementInput.moveForward;
		zDist = mc.thePlayer.movementInput.moveStrafe;
		float rotationYaw = mc.thePlayer.rotationYaw;
		if (xMotionSpeed == 0.0 && zDist == 0.0) {

			e.setX(0.0);
			e.setZ(0.0);
		} else if (xMotionSpeed != 0.0) {
			if (zDist >= 1.0) {
				rotationYaw += ((xMotionSpeed > 0.0) ? -45.0f : 45.0f);
				zDist = 0.0;
			} else if (zDist <= -1.0) {
				rotationYaw += ((xMotionSpeed > 0.0) ? 45.0f : -45.0f);
				zDist = 0.0;
			}
			if (xMotionSpeed > 0.0) {
				xMotionSpeed = (float) 1.0;
			} else if (xMotionSpeed < 0.0) {
				xMotionSpeed = (float) -1.0;
			}
		}
		final double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
		final double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
		final double x = (xMotionSpeed * moveSpeed * cos + zDist * moveSpeed * sin) * 0.987654321;
		final double z = (xMotionSpeed * moveSpeed * sin - zDist * moveSpeed * cos) * 0.987654321;
		if (Math.abs(x) < 1.0 && Math.abs(z) < 1.0) {
			e.setX(x);
			e.setZ(z);
		}

		mc.thePlayer.stepHeight = 0.6f;
		if (xMotionSpeed == 0.0 && zDist == 0.0) {
			e.setX(0.0);
			e.setZ(0.0);

		} else if (xMotionSpeed != 0.0) {
			if (zDist >= 1.0) {
				zDist = 0.0;
			} else if (zDist <= -1.0) {
				zDist = 0.0;
			}
			if (xMotionSpeed > 0.0) {
				xMotionSpeed = (float) 1.0;
			} else if (xMotionSpeed < 0.0) {
				xMotionSpeed = (float) -1.0;
			}
		}
		++stage;

		if (mc.thePlayer.isMoving()) {
			setMotion(e, moveSpeed);
		}

	}

	private double getBaseSpeed() {
		final EntityPlayerSP player = mc.thePlayer;
		double base = player.isSneaking() ? 0.06630000288486482
				: (mc.thePlayer.isSprinting() ? 0.2872999905467033 : 0.22100000083446503);
		final PotionEffect moveSpeed = player.getActivePotionEffect(Potion.moveSpeed);
		final PotionEffect moveSlowness = player.getActivePotionEffect(Potion.moveSlowdown);
		if (moveSpeed != null) {
			base *= 1.0 + 0.8 * PotionMultifier.getValueState().doubleValue() * (moveSpeed.getAmplifier() + 1);
		}
		if (moveSlowness != null) {
			base *= 1.0 + 0.8 * PotionMultifier.getValueState().doubleValue() * (moveSlowness.getAmplifier() + 1);
		}
		if (player.isInWater()) {
			base *= 0.5203619984250619;
			final int depthStriderLevel = EnchantmentHelper.getDepthStriderModifier(mc.thePlayer);
			if (depthStriderLevel > 0) {

				double[] DEPTH_STRIDER_VALUES = new double[] { 1.0, 1.4304347400741908, 1.7347825295420374,
						1.9217391028296074 };

				base *= DEPTH_STRIDER_VALUES[depthStriderLevel];
			}
		} else if (player.isInLava()) {
			base *= 0.5203619984250619;
		}
		return base;
	}

	@EventTarget
	public void onstep(EventStep event) {
		if (Jesus.Jesuss || ModManager.getModByClass(Scaffold.class).isEnabled()
				|| ModManager.getModByClass(Fly.class).isEnabled() || Minecraft.thePlayer.getAir() < 300) {
			return;
		}

		final double Y = Minecraft.thePlayer.getEntityBoundingBox().minY - Minecraft.thePlayer.posY;
		if (Y > 0.7) {
			this.less = 0.0;
		}
		if (Y == 0.5) {
			this.stair = 0.75;
		}
	}

	@EventTarget
	public void onEvent(EventPacket ep) {
		if (Jesus.Jesuss || ModManager.getModByClass(Scaffold.class).isEnabled()
				|| ModManager.getModByClass(Fly.class).isEnabled() || Minecraft.thePlayer.getAir() < 300) {
			return;
		}

		Packet<?> p = ep.getPacket();
		if (ep.getEventType() == EventType.RECEIVE && p instanceof S08PacketPlayerPosLook) {
			S08PacketPlayerPosLook pac = (S08PacketPlayerPosLook) ep.getPacket();

			Client.instance.getNotificationManager().addNotification("Lagback checks!", Type.WARNING);

			net.minecraft.util.Timer.timerSpeed = 1.0f;
			distance = 999;
			moveSpeed = 0;
			this.slow = 1.0E7;
			this.less = 0.0;
			this.stage = -9;

			waterSpeed = 0.1;
			lessSlow = false;
			Minecraft.thePlayer.motionX *= 0.0D;
			Minecraft.thePlayer.motionZ *= 0.0D;

			ticksX.reset();

			cooldown = true;

			shouldSpoof = false;

			if (LagBackCheck.getValueState())
				this.toggle();

			lastCheck.reset();
		}

		if (AutoDisable_S.getValueState() && ticksX.delay(AutoDisable.getValueState().intValue() * 15)) {
			Client.instance.getNotificationManager().addNotification("Auto Disabled !", Type.WARNING);

			this.toggle();

			ticksX.reset();
		}
	}

	public static float getDirection() {
		Minecraft.getMinecraft();
		float yaw = Minecraft.thePlayer.rotationYawHead;
		Minecraft.getMinecraft();
		float forward = Minecraft.thePlayer.moveForward;
		Minecraft.getMinecraft();
		float strafe = Minecraft.thePlayer.moveStrafing;
		float XZ = 0;
		yaw += (forward < 0.0F ? 180 : 0);
		if (strafe < 0.0F) {
			yaw += (forward < 0.0F ? -45 : forward == 0.0F ? 90 : 45);
		}
		if (strafe > 0.0F) {
			yaw -= (forward < 0.0F ? -45 : forward == 0.0F ? 90 : 45);
		}

		if (strafe > 0.0F) {
			XZ = 0.001F;
		} else {
			XZ = 0.0F;
		}

		return yaw * 0.017453292F + XZ;
	}

	private void setMotion(EventMove eventMove, double speed) {
		double forward = MovementInput.moveForward;
		double strafe = MovementInput.moveStrafe;
		float rotationYaw = Minecraft.thePlayer.rotationYaw;

		this.moveSpeed = speed;

		if (forward == 0.0 && strafe == 0.0) {
			eventMove.setX(0.0);
			eventMove.setZ(0.0);
		} else {
			if (forward != 0.0) {
				if (strafe > 0.0) {
					rotationYaw += ((forward > 0.0) ? -45 : 45);
				} else if (strafe < 0.0) {
					rotationYaw += ((forward > 0.0) ? 45 : -45);
				}
				strafe = 0.0;
				if (forward > 0.0) {
					forward = 1.0;
				} else if (forward < 0.0) {
					forward = -1.0;
				}
			}

			eventMove.setX(Minecraft.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(rotationYaw + 88.0))
					+ strafe * speed * Math.sin(Math.toRadians(rotationYaw + 87.9000015258789)));
			eventMove.setZ(Minecraft.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(rotationYaw + 88.0))
					- strafe * speed * Math.cos(Math.toRadians(rotationYaw + 87.9000015258789)));
		}

		Minecraft.thePlayer.FocusYaw = rotationYaw;
	}

	public static double roundToPlace(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public double round(double var1, int var3) {
		if (var3 < 0) {
			throw new IllegalArgumentException();
		} else {
			BigDecimal var4 = new BigDecimal(var1);
			var4 = var4.setScale(var3, RoundingMode.HALF_UP);
			return var4.doubleValue();
		}
	}

	@native0
	@EventTarget
	public void onRespawn(EventRespawn respawnEvent) {
		this.toggle();
		ClientUtil.sendChatMessage(String.valueOf(" " + this.getName()) + EnumChatFormatting.RED + " Disabled "
				+ EnumChatFormatting.RESET + " (Auto)", ChatType.INFO);
	}

	@Override
	public void onDisable() {
		if (Minecraft.thePlayer == null || Minecraft.theWorld == null)
			return;
		ICE = false;
		Minecraft.thePlayer.motionX *= 1.0;
		Minecraft.thePlayer.motionZ *= 1.0;
		net.minecraft.util.Timer.timerSpeed = 1.0f;

		Minecraft.thePlayer.speedInAir = 0.02f;
		super.onDisable();
	}

	public void onEnable() {
		if (Minecraft.thePlayer == null || Minecraft.theWorld == null)
			return;
		net.minecraft.util.Timer.timerSpeed = 1.0f;
		distance = 0;
		moveSpeed = MathUtils.defaultSpeed();
		this.slow = 1.0E7;
		this.less = 0.0;
		this.stage = 2;
		waterSpeed = 0.1;
		lessSlow = false;
		Minecraft.thePlayer.motionX *= 0.0D;
		Minecraft.thePlayer.motionZ *= 0.0D;

		ticksX.reset();

		cooldown = true;

		shouldSpoof = false;

		super.onEnable();
	}

}
