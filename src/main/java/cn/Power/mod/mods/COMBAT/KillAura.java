package cn.Power.mod.mods.COMBAT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.vecmath.Vector3d;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viamcp.fixes.AttackOrder;
import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender;
import cn.Power.events.EventRender2D;
import cn.Power.events.EventRespawn;
import cn.Power.events.EventTick;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.aura.AngleUtils;
import cn.Power.mod.mods.COMBAT.aura.Vector;
import cn.Power.mod.mods.MOVEMENT.Scaffold;
import cn.Power.mod.mods.MOVEMENT.Speed;
import cn.Power.mod.mods.MOVEMENT.TargetStrafe;
import cn.Power.mod.mods.PLAYER.Freecam;
import cn.Power.mod.mods.RENDER.Hud;
import cn.Power.mod.mods.WORLD.AntiVanish;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.util.Angle;
import cn.Power.util.AngleUtility;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.Colors;
import cn.Power.util.CombatUtil;
import cn.Power.util.Helper;
import cn.Power.util.MathUtils;
import cn.Power.util.PlayerUtil;
import cn.Power.util.RenderUtil;
import cn.Power.util.RotationUtil;
import cn.Power.util.RotationUtils;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.TargetHUD;
import cn.Power.util.TargetHUDOld;
import cn.Power.util.Vector.Vector3;
import cn.Power.util.friendManager.FriendManager;
import cn.Power.util.misc.STimer;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class KillAura extends Mod {
	float hue;
	public CopyOnWriteArrayList<EntityLivingBase> targets = new CopyOnWriteArrayList<EntityLivingBase>();
	public static CopyOnWriteArrayList<String> vips = new CopyOnWriteArrayList<String>();
	public static EntityLivingBase Target = null;
	public Value<String> Mode = new Value<String>("KillAura", "Mode", 0);
	public Value<String> priority = new Value<String>("KillAura", "Priority", 0);
	public Value<String> targetHUD = new Value<String>("KillAura", "targetHUD", 0);
	public Value<Double> reach = new Value<Double>("KillAura_Reach", 4.2, 3.0, 7.0, 0.1);
	public Value<Double> BlockReach = new Value<Double>("KillAura_BlockReach", 4.2, 3.0, 10.0, 0.1);
	public Value<Double> Maxcps = new Value<Double>("KillAura_MaxCPS", 10.0, 1.0, 20.0, 1.0);
	public Value<Double> Mincps = new Value<Double>("KillAura_MinCPS", 10.0, 1.0, 20.0, 1.0);
	public Value<Double> turnspeed = new Value<Double>("KillAura_TurnSpeed", 120.0, 10.0, 300.0, 5.0);
	public Value<Double> switchDelay = new Value<Double>("KillAura_SwitchDelay", 500.0, 100.0, 2000.0, 100.0);
	public Value<Double> hurttime = new Value<Double>("KillAura_HurtTime", 8.0, 1.0, 20.0, 1.0);
	public Value<Double> cracksize = new Value<Double>("KillAura_CrackSize", 1.0, 0.0, 10.0, 1.0);
	public Value<Double> MAXT = new Value<Double>("KillAura_Maxtarget", 5.0, 1.0, 20.0, 1.0);
	public Value<Double> smooth = new Value<Double>("KillAura_Smooth", 20.0, 20.0, 100.0, 1.0);
	public Value<Boolean> autoBlock = new Value<Boolean>("KillAura_AutoBlock", true);
	public Value<Boolean> AiMBot = new Value<Boolean>("KillAura_AiMBot", false);
	public Value<Boolean> targetHP = new Value<Boolean>("KillAura_targetHP", false);
	public Value<Boolean> targetESP = new Value<Boolean>("KillAura_targetESP", true);
	public Value<Boolean> rotations = new Value<Boolean>("KillAura_HeadRotations", true);
	public Value<Boolean> attackPlayers = new Value<Boolean>("KillAura_Players", true);
	public Value<Boolean> invisible = new Value<Boolean>("KillAura_Invisibles", false);
	public Value<Boolean> attackAnimals = new Value<Boolean>("KillAura_Animals", false);
	public Value<Boolean> Villager = new Value<Boolean>("KillAura_Villager", false);
	public Value<Boolean> attackMobs = new Value<Boolean>("KillAura_Mobs", false);
	public Value<Boolean> blockRayTrace = new Value<Boolean>("KillAura_RayTrace", false);
	public Value<Boolean> multi = new Value<Boolean>("KillAura_SemiMulti", true);
	public Value<Boolean> autodisable = new Value<Boolean>("KillAura_AutoDisable", true);

//	public Value<Boolean> BlockSlowDown = new Value<Boolean>("KillAura_BlockSlow", false);

	public static Value<Boolean> Rotary_animation = new Value<Boolean>("KillAura_Rotary animation", false);

	public static Value<Boolean> IgnoreMwAnimals = new Value<Boolean>("KillAura_IgnoreMwAnimals", true);

	private STimer test = new STimer();
	private STimer SwitchDelay = new STimer();
	private boolean unBlock = false;
	public static boolean Blockreach = false;
	private int index;
	public static float[] f;
	public static float b;
	public static float c;
	public static boolean a;
	public static int e;
	public static int d;

	private TimeHelper switchTimer = new TimeHelper();

	private final MouseFilter pitchMouseFilter = new MouseFilter();
	private final MouseFilter yawMouseFilter = new MouseFilter();

	// TODO:Scaffold

	public KillAura() {
		super("KillAura", Category.COMBAT);
		this.showValue = this.reach;

		this.Mode.mode.add("Switch");
		this.Mode.mode.add("Single");
		this.Mode.mode.add("Multi");

		this.targetHUD.mode.add("NONE");
		this.targetHUD.mode.add("New");
		this.targetHUD.mode.add("Old");

		this.priority.mode.add("Health");
		this.priority.mode.add("Reach");
		this.priority.mode.add("Armor");
		this.priority.mode.add("Angle");
		this.priority.mode.add("Fov");
		this.priority.mode.add("HurtTime");

	}

	@native0
	@EventTarget
	public void onRespawn(EventRespawn respawnEvent) {
		if (autodisable.getValueState()) {
			this.toggle();

			ClientUtil.sendChatMessage(String.valueOf(" " + this.getName()) + EnumChatFormatting.RED + " Disabled "
					+ EnumChatFormatting.RESET + " (Auto)", ChatType.INFO);
		}

		this.Blockreach = false;
	}

	private double time;
	private boolean down;
	private myAngle currentRotations;
	public static EntityLivingBase AutoBlockEntity;

	public void nativepro() {
		int color = Hud.getColor();

		int R = (color >> 16 & 0xFF);
		int G = (color >> 8 & 0xFF);
		int B = (color & 0xFF);

		time += .01 * (Client.delta * .12);

		final double height = 0.5 * (1 + Math.sin(2 * Math.PI * (time * .3)));

		if (height > .995) {
			down = true;
		} else if (height < .01) {
			down = false;
		}
		double x = Target.lastTickPosX + (Target.posX - Target.lastTickPosX) * (double) mc.timer.renderPartialTicks
				- mc.getRenderManager().viewerPosX;
		double y = Target.lastTickPosY + (Target.posY - Target.lastTickPosY) * (double) mc.timer.renderPartialTicks
				- mc.getRenderManager().viewerPosY;
		double z = Target.lastTickPosZ + (Target.posZ - Target.lastTickPosZ) * (double) mc.timer.renderPartialTicks
				- mc.getRenderManager().viewerPosZ;
		GlStateManager.enableBlend();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.disableAlpha();
		GL11.glLineWidth(1.5F);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_CULL_FACE);
		final double size = Target.width * 1.1;
		final double yOffset = ((Target.height * (1)) + .2) * height;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		{
			for (int j = 0; j < 361; j++) {
				RenderUtil.color(Colors.getColor(R, G, B, (int) (down ? 255 * height : 255 * (1 - height)) << 2));

				GL11.glVertex3d(x + Math.cos(Math.toRadians(j)) * size, y + yOffset,
						z - Math.sin(Math.toRadians(j)) * size);
				RenderUtil.color(ClientUtil.reAlpha(Colors.getColor(R, G, B), 0F));
				GL11.glVertex3d(x + Math.cos(Math.toRadians(j)) * size,
						y + yOffset + ((down ? .5 * (1 - height) : -.5 * height)),
						z - Math.sin(Math.toRadians(j)) * size);
			}
		}
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			for (int j = 0; j < 361; j++) {
				RenderUtil.color(Colors.getColor(R, G, B));
				GL11.glVertex3d(x + Math.cos(Math.toRadians(j)) * size, y + yOffset,
						z - Math.sin(Math.toRadians(j)) * size);
			}
		}
		GL11.glEnd();
		GlStateManager.enableAlpha();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.resetColor();
	}

	int GroundTick = 0;

	@EventTarget
	public void onTE(EventRender e) {

		if (targetESP.getValueState() && KillAura.Target != null) {
			nativepro();
		}

//		if (BlockSlowDown.getValueState()) {
//			
//			
//			
//			if (mc.thePlayer.onGround) {
//
//				if (GroundTick <= 10)
//					GroundTick++;
//			} else if (GroundTick > 0) {
//				GroundTick--;
//			}
//
//			if (this.Blockreach && GroundTick > 5 && MathUtils.isOnGround(0.1)) {
//
//				mc.thePlayer.movementInput.moveForward *= 0.7;
//				mc.thePlayer.motionX *= 0.689245685;
//				mc.thePlayer.motionZ *= 0.689245685;
//
//			}
//			
//		}
	}

	@EventTarget
	public void onSwitchTarget(EventPostMotion event) {
		// getTrarget
		if (this.Mode.isCurrentMode("Switch") || this.Mode.isCurrentMode("Multi")) {

			if (this.Mode.isCurrentMode("Switch"))
				this.setDisplayName("Switch");
			else
				this.setDisplayName("Multi");

			if (Target != null) {

				int delay = this.switchDelay.getValueState().intValue();

				try {

					if (delay < 0)
						delay = 0;

				} catch (Throwable c) {
					c.printStackTrace();
				}

				if (this.SwitchDelay.delay(delay)) {
					++this.index;

					if (this.index >= this.targets.size()) {
						this.index = 0;
					}
					SwitchDelay.reset();
				}
			}
		}

		if (this.Mode.isCurrentMode("Single")) {
			this.setDisplayName("Single");
			if (Minecraft.thePlayer.getHealth() <= 0.0f && this.targets.size() > 0) {
				++this.index;
			}
		}

		if (!this.targets.isEmpty() && this.index >= this.targets.size()) {
			this.index = 0;
		}

		for (EntityLivingBase ent : this.targets) {
			if (this.isValidEntity(ent, reach.getValueState().doubleValue()))
				continue;

			if (ent == Target)
				Target = null;

			this.targets.remove(ent);
		}
	}

	private float[] getRotations(final Entity entity) {
		final EntityPlayerSP player = Minecraft.thePlayer;
		final double xDist = entity.posX - player.posX;
		final double zDist = entity.posZ - player.posZ;
		double yDist = entity.posY - player.posY;
		final double dist = StrictMath.sqrt(xDist * xDist + zDist * zDist);
		final AxisAlignedBB entityBB = entity.getEntityBoundingBox().expand(0.10000000149011612, 0.10000000149011612,
				0.10000000149011612);
		final double playerEyePos = player.posY + player.getEyeHeight();
		final boolean close = dist < 0.5 && Math.abs(yDist) < 0.5;
		float pitch;
		if (close && playerEyePos > entityBB.minY) {
			pitch = 60.0f;
		} else {
			yDist = ((playerEyePos > entityBB.maxY) ? (entityBB.maxY - playerEyePos)
					: ((playerEyePos < entityBB.minY) ? (entityBB.minY - playerEyePos) : 0.0));
			pitch = (float) (-(StrictMath.atan2(yDist, dist) * 57.29577951308232));
		}
		float yaw = (float) (StrictMath.atan2(zDist, xDist) * 57.29577951308232) - 90.0f;
		if (close) {
			final int inc = (dist < 1.0) ? 180 : 90;
			yaw = (float) (Math.round(yaw / inc) * inc);
		}
		return new float[] { yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f) };
	}

	public Angle R() {

		EntityLivingBase T = Target;

		Angle jsAngle = new Angle(CombatUtil.getRotationsNeeded(T)[0], CombatUtil.getRotationsNeeded(T)[1]);

		return jsAngle;
	}

	@EventTarget(1)
	public void onPre(EventPreMotion event) {

		KillAura.AutoBlockEntity = getOptimalTarget(BlockReach.getValueState().doubleValue());

		this.findTargets();

		if (targets.size() > 0 && this.index < this.targets.size()
				&& this.isValidEntity(this.targets.get(index), reach.getValueState().doubleValue())) {
			Target = this.targets.get(index);
		} else {
			Target = null;
		}

		if (ModManager.getModByClass(Scaffold.class).isEnabled())
			return;

		if (!this.targets.isEmpty() && KillAura.Target != null) {

			try {

//				float pYaw = getRotations(KillAura.Target)[0];
//				float pPitch = getRotations(KillAura.Target)[1];
//
//				float var8 = 0.8F;
//				float var10 = var8 * var8 * var8 * 8.0F;
//
//				pYaw += var10 * 0.15F;
//				pPitch -= var10 * 0.15F;
//
//				if(!ModManager.getModByClass(TargetStrafe.class).isEnabled()
//						&& !ModManager.getModByClass(Speed.class).isEnabled())
//				this.currentRotations.yaw = interpolateRotation(this.currentRotations.yaw, pYaw - 45,
//						(float) (40 - ThreadLocalRandom.current().nextDouble(10)));
//				this.currentRotations.pitch = interpolateRotation(this.currentRotations.pitch, pPitch,
//						(float) (40 - ThreadLocalRandom.current().nextDouble(10)));
//
//				if (!ModManager.getModByClass(Scaffold.class).isEnabled()) {
//					if (TurnHead.getValueState())
//						event.setYaw(this.currentRotations.yaw);
//					event.setPitch(this.currentRotations.pitch);
//				}

				if (Target.getDistanceToEntity(mc.thePlayer) > 0.5) {
					event.yaw = R().getYaw() + 1;
					event.pitch = R().getPitch() + 1;
				}

				if (this.AiMBot.getValueState().booleanValue()) {
					Minecraft.thePlayer.rotationYaw = event.yaw;
					Minecraft.thePlayer.rotationPitch = event.pitch;
				}

			} catch (Throwable c) {
				c.printStackTrace();
			}

		} else {

//			}

			if (this.Blockreach && this.isSword() && AutoBlockEntity == null) {

				if (Minecraft.thePlayer.itemInUseCount == 520) {
					Minecraft.thePlayer.itemInUseCount = 0;
				}

				Minecraft.playerController.updateController();

				Minecraft.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
						C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
				this.Blockreach = false;

			}
		}

		if (Target != null && this.cracksize.getValueState() > 0)
			if ((double) Minecraft.thePlayer.getDistanceToEntity(Target) < reach.getValueState()) {
				int i = 0;
				while ((double) i < this.cracksize.getValueState()) {
					Mod.mc.effectRenderer.emitParticleAtEntity(Target, EnumParticleTypes.CRIT);
					Mod.mc.effectRenderer.emitParticleAtEntity(Target, EnumParticleTypes.CRIT_MAGIC);

					++i;
				}
			}

	}

	@EventTarget
	private void packet(EventPacket e) {
		if (e.getPacket() instanceof C09PacketHeldItemChange) {

			Minecraft.playerController.updateController();

			this.Blockreach = false;
		}
	}

	private static float interpolateRotation(final float prev, final float now, final float maxTurn) {
		float var4 = MathHelper.wrapAngleTo180_float(now - prev);

		if (var4 > maxTurn) {
			var4 = maxTurn;
		}
		if (var4 < -maxTurn) {
			var4 = -maxTurn;
		}

		return new BigDecimal(prev + var4).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public float[] getEntityRotations(EntityLivingBase target, boolean aac, int smooth) {
		myAngleUtility angleUtility = new myAngleUtility(aac, smooth);

		double var3 = target.posX;
		double var5 = target.posY + 2.125;
		double var7 = target.posZ;

		double var9;
		double var11;
		double var10;

		var9 = target.lastTickPosX + (var3 - target.lastTickPosX) * (double) Mod.mc.timer.renderPartialTicks;
		var10 = target.lastTickPosZ + (var7 - target.lastTickPosZ) * (double) Mod.mc.timer.renderPartialTicks;
		var3 += var3 - var9;
		var7 += var7 - var10;

		Vector3d enemyCoords = new Vector3d(var9, var5, var10);
		Vector3d myCoords = new Vector3d(Minecraft.thePlayer.posX,
				Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(), Minecraft.thePlayer.posZ);
		myAngle dstAngle = angleUtility.calculateAngle(enemyCoords, myCoords);
		myAngle srcAngle = new myAngle(currentRotations.yaw, currentRotations.pitch);
		myAngle smoothedAngle = angleUtility.smoothAngle(dstAngle, srcAngle);
		float yaw = smoothedAngle.getYaw();
		float pitch = smoothedAngle.getPitch();

		float var8 = 0.6F + 0.2F;
		float var1011 = var8 * var8 * var8 * 8.0F;

		yaw += var1011 * 0.15F;
		pitch -= var1011 * 0.15F;

		currentRotations = new myAngle(yaw, pitch);

		return new float[] { yaw, pitch };
	}

	class myAngle {
		private float yaw;
		private float pitch;

		public myAngle(float yaw, float pitch) {
			this.yaw = yaw;
			this.pitch = pitch;
		}

		public myAngle() {
			this(0.0f, 0.0f);
		}

		public float getYaw() {
			return yaw;
		}

		public float getPitch() {
			return pitch;
		}

		public void setYaw(float yaw) {
			this.yaw = yaw;
		}

		public void setPitch(float pitch) {
			this.pitch = pitch;
		}

		public myAngle constrantAngle() {
			this.setYaw(this.getYaw());
			this.setPitch(this.getPitch());

			while (this.getYaw() <= -180F) {
				this.setYaw(this.getYaw() + 360F);
			}

			while (this.getPitch() <= -180F) {
				this.setPitch(this.getPitch() + 360F);
			}

			while (this.getYaw() > 180F) {
				this.setYaw(this.getYaw() - 360F);
			}

			while (this.getPitch() > 180F) {
				this.setPitch(this.getPitch() - 360F);
			}

			return this;
		}
	}

	class myAngleUtility {

		private boolean aac;
		private float smooth;
		private Random random;

		public myAngleUtility(boolean aac, float smooth) {
			this.aac = aac;
			this.smooth = smooth;
			this.random = ThreadLocalRandom.current();
		}

		public myAngle calculateAngle(Vector3d destination, Vector3d source) {
			myAngle angles = new myAngle();
			destination.setX(destination.getX() + ((aac ? randomFloat(-0.75F, 0.75F) : 0.0F) - source.getX()));
			destination.setY(destination.getY() + ((aac ? randomFloat(-0.25F, 0.5F) : 0.0F) - source.getY()));
			destination.setZ(destination.getZ() + ((aac ? randomFloat(-0.75F, 0.75F) : 0.0F) - source.getZ()));
			double hypotenuse = Math.hypot(destination.getX(), destination.getZ());
			angles.setYaw((float) (Math.atan2(destination.getZ(), destination.getX()) * 57.29577951308232D) - 90.0F);
			angles.setPitch(-(float) ((Math.atan2(destination.getY(), hypotenuse) * 57.29577951308232D)));
			return angles.constrantAngle();
		}

		public myAngle smoothAngle(myAngle destination, myAngle source) {
			myAngle angles = (new myAngle(source.getYaw() - destination.getYaw(),
					source.getPitch() - destination.getPitch())).constrantAngle();
			angles.setYaw(source.getYaw() - angles.getYaw() / 100.0F * smooth);
			angles.setPitch(source.getPitch() - angles.getPitch() / 100.0F * smooth);
			return angles.constrantAngle();
		}

		public float randomFloat(float min, float max) {
			return min + (this.random.nextFloat() * (max - min));
		}

	}

	@EventTarget
	public void onPost(EventPostMotion event) {

		if (ModManager.getModByClass(Scaffold.class).isEnabled())
			return;

		mc.playerController.updateController();

		if (Target != null) {

			if (!Client.blockActionsForHealing)
				this.doAttack();

		}

		if (AutoBlockEntity != null && isSword() && autoBlock.getValueState().booleanValue()
				&& !Client.blockActionsForHealing
				&& !mc.thePlayer.isEating()) {
			PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
			useItem.write(Type.VAR_INT, 1);
			PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
			Minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));

			this.Blockreach = true;

			

		}
	}

	@EventTarget
	public void onPre1(EventPreMotion event) {

		if (Minecraft.thePlayer.itemInUseCount == 520 && !this.isSword()) {
			Minecraft.thePlayer.itemInUseCount = 0;
		}

		if (ModManager.getModByClass(Scaffold.class).isEnabled())
			return;

		if (AutoBlockEntity != null && Blockreach) {
			if (isSword() && (autoBlock.getValueState().booleanValue() || Minecraft.thePlayer.isBlocking())
					&& !mc.thePlayer.isEating()
					&& !Client.blockActionsForHealing) {

				Minecraft.playerController.syncCurrentPlayItem();

				Minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(
						C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
				Blockreach = false;
			}
		} else if (Minecraft.thePlayer.itemInUseCount == 520 && this.isSword() && (!autoBlock.getValueState().booleanValue() || AutoBlockEntity == null)) {
			Minecraft.thePlayer.itemInUseCount = 0;
		}

		if (AutoBlockEntity != null && Blockreach && isSword() && !mc.thePlayer.isEating()
				&& (autoBlock.getValueState().booleanValue() || Minecraft.thePlayer.isBlocking())
				&& !Client.blockActionsForHealing) {

			Minecraft.playerController.syncCurrentPlayItem();

			Minecraft.getNetHandler().getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(
					C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
			Minecraft.thePlayer.itemInUseCount = 0;
			Blockreach = false;
		}

	}

	private boolean isSword() {
		return Minecraft.thePlayer.inventory.getCurrentItem() != null
				&& Minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
	}

	private void doAttack() {
		int Maxcps = this.Maxcps.getValueState().intValue();
		int Mincps = this.Mincps.getValueState().intValue();
		int delayValue = (int) (1000 / MathUtils.getRandomInRange(Mincps, Maxcps));

		Minecraft.playerController.syncCurrentPlayItem();

		if ((this.test.delay(delayValue))) {

			this.test.reset();

			if ((Blockreach || (this.isSword() && Minecraft.thePlayer.isBlocking() || Minecraft.thePlayer.getHeldItem() != null
					&& !mc.thePlayer.isEating() && Minecraft.thePlayer.getHeldItem().getItem() instanceof ItemSword
					&& autoBlock.getValueState().booleanValue()
					&& !Client.blockActionsForHealing))) {

				Minecraft.playerController.updateController();

//				Minecraft.getNetHandler().getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(
//						C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));

				Blockreach = false;

			}

			Minecraft.playerController.updateController();

//			if (this.isSword() && !Minecraft.thePlayer.isBlocking() && !autoBlock.getValueState().booleanValue()
//					&& (Client.lastSlot == -1 || Client.lastSlot == Minecraft.thePlayer.inventory.currentItem)
//					&& !Client.blockActionsForHealing && Minecraft.thePlayer.itemInUseCount > 0) {
//				Minecraft.thePlayer.itemInUseCount = 0;
//			}

			EntityLivingBase toAttack = Target;

			if (toAttack.hurtTime < hurttime.getValueState().intValue()) {

			} else if (multi.getValueState()) {
				toAttack = this.targets.stream()
						.filter(p -> p != Target && p.hurtResistantTime < hurttime.getValueState().intValue())
						.findFirst().orElse(null);
			}

			if (toAttack != null) {

//				Minecraft.thePlayer.swingItem();
//
//				Minecraft.thePlayer.sendQueue
//						.addToSendQueue(new C02PacketUseEntity(toAttack, C02PacketUseEntity.Action.ATTACK));
				AttackOrder.sendFixedAttack(mc.thePlayer,toAttack);
			}

			final EntityLivingBase toAttack2 = toAttack;

			if (this.Mode.isCurrentMode("Multi")) {
				this.targets.stream().filter(p -> p != toAttack2 && (p.hurtTime < hurttime.getValueState().intValue()))
						.forEach(p -> {

//							Minecraft.thePlayer.swingItem();
//
//							Minecraft.thePlayer.sendQueue
//									.addToSendQueue(new C02PacketUseEntity(p, C02PacketUseEntity.Action.ATTACK));

							AttackOrder.sendFixedAttack(mc.thePlayer,p);
							Minecraft.playerController.updateController();

						});
			}

		}

	}

	private void findTargets() {
		this.findTargets(reach.getValueState().doubleValue());
	}

	private EntityLivingBase getOptimalTarget(double range) {
		List<EntityLivingBase> load = new ArrayList<>();

		Minecraft.theWorld.getLoadedEntityList().stream().filter(o -> o instanceof EntityLivingBase).forEach(o -> {
			EntityLivingBase ent = (EntityLivingBase) o;
			if (isValidEntity(ent, range)) {
				load.add(ent);
			}
		});

		return getTarget(load);
	}

	private EntityLivingBase getTarget(List<EntityLivingBase> list) {
		sortList(list);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	private void sortList(List<EntityLivingBase> list) {
		if (this.priority.isCurrentMode("Reach") && (mc.gameSettings.keyBindSprint.isKeyDown())) {
			list.sort((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
			return;
		}
		if (this.priority.isCurrentMode("Health") && (mc.gameSettings.keyBindSprint.isKeyDown())) {
			list.sort(Comparator.comparingDouble(player -> Minecraft.thePlayer.getDistanceToEntity(player)));
			return;
		}
		if (this.priority.isCurrentMode("Reach")) {
			list.sort(Comparator.comparingDouble(player -> Minecraft.thePlayer.getDistanceToEntity(player)));
		}
		if (this.priority.isCurrentMode("Fov")) {
			list.sort(Comparator.comparingDouble(o -> CombatUtil
					.getDistanceBetweenAngles(Minecraft.thePlayer.rotationPitch, CombatUtil.getRotations(o)[0])));
		}
		if (this.priority.isCurrentMode("Angle")) {
			list.sort((o1, o2) -> {
				float[] rot1 = getRotations(o1);
				float[] rot2 = getRotations(o2);
				return (int) (Minecraft.thePlayer.rotationYaw - rot1[0] - (Minecraft.thePlayer.rotationYaw - rot2[0]));
			});
		}
		if (this.priority.isCurrentMode("Health")) {
			list.sort((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
		}

		if (this.priority.isCurrentMode("Armor")) {
			list.sort(Comparator.comparingDouble(player -> getArmorVal(player)));
		}
	}

	private void findTargets(double reach) {
		int maxtTargets = MAXT.getValueState().intValue();
		int maxSize = Mode.isCurrentMode("Switch") ? 4 : maxtTargets;

		CopyOnWriteArrayList<EntityLivingBase> Vips = new CopyOnWriteArrayList<EntityLivingBase>();
	      
	      for (Entity o3 : mc.theWorld.getLoadedEntityList()) {
	            EntityLivingBase curEnt;
	            if (o3 instanceof EntityLivingBase && this.isValidEntity(curEnt = (EntityLivingBase) o3, reach) && !Vips.contains(curEnt) && vips.contains(curEnt.getName())) {
	                Vips.add(0,curEnt);
	            }
	            targets = Vips;
	        }
	      
	      if(Vips.size() <=0 )
		Minecraft.theWorld.getLoadedEntityList().stream().filter(o -> o instanceof EntityLivingBase).anyMatch(o3 -> {
			EntityLivingBase curEnt;
			if (o3 instanceof EntityLivingBase && this.isValidEntity(curEnt = (EntityLivingBase) o3, reach)&& !this.targets.contains(curEnt)) {
				if (this.targets.size() <= maxSize) {
					this.targets.add((EntityLivingBase) o3);
				}
			}
			return false;
		});

		this.sortList(this.targets);

	}

	private double getArmorVal(EntityLivingBase ent) {
		if (ent instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) ent;
			double armorstrength = 0;
			for (int index = 3; index >= 0; index--) {
				ItemStack stack = player.inventory.armorInventory[index];
				if (stack != null) {
					armorstrength += getArmorStrength(stack);
				}
			}
			return armorstrength;
		}
		return 0;
	}

	private double getArmorStrength(final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemArmor))
			return -1;
		float damageReduction = ((ItemArmor) itemStack.getItem()).damageReduceAmount;
		Map<?, ?> enchantments = EnchantmentHelper.getEnchantments(itemStack);
		if (enchantments.containsKey(Enchantment.protection.effectId)) {
			int level = Integer.parseInt((String) enchantments.get(Enchantment.protection.effectId));
			damageReduction += Enchantment.protection.calcModifierDamage(level, DamageSource.generic);
		}
		return damageReduction;
	}

	private boolean isValidEntity(EntityLivingBase ent, double range) {

		if (ent instanceof EntityArmorStand) {
			return false;
		}
		if (ent == Minecraft.thePlayer.getLowestRidingEntity())
			return false;

		if (ent == null) {
			return false;
		}
		if (ent == Minecraft.thePlayer) {
			return false;
		}
		if (ent instanceof EntitySnowman && SkyBlockUtils.isMWgame() && IgnoreMwAnimals.getValueState())
			return false;

		if ((ent instanceof EntityAnimal || ent instanceof EntityMob) && SkyBlockUtils.isMWgame()
				&& IgnoreMwAnimals.getValueState() && !(ent instanceof EntityWither))
			return false;
		if (ent instanceof EntityPlayer && !this.attackPlayers.getValueState().booleanValue()) {
			return false;
		}
		if ((ent instanceof EntityAnimal || ent instanceof EntitySquid || ent instanceof EntityBat)
				&& !this.attackAnimals.getValueState().booleanValue()) {
			return false;
		}
		if ((ent instanceof EntityVillager) && !this.Villager.getValueState().booleanValue()) {
			return false;
		}
		if ((ent instanceof EntityMob || ent instanceof EntityGhast || ent instanceof EntitySlime
				|| ent instanceof EntityIronGolem || ent instanceof EntitySnowman)
				&& !this.attackMobs.getValueState().booleanValue()) {
			return false;
		}
		if (ent instanceof EntityZombie && ent.getMaxHealth() == 399) {
			return false;
		}
		if ((double) Minecraft.thePlayer.getDistanceToEntity(ent) >= range) {
			return false;
		}
		if (ent.isDead || ent.getHealth() <= 0.0f) {
			return false;
		}
		if (ent.isInvisible() && !this.invisible.getValueState().booleanValue()) {
			return false;
		}
		if (FriendManager.isFriend(ent.getName())) {
			return false;
		}
		if (FriendManager.isIRCMOD(ent)) {
			return false;
		}
		if (ent == Freecam.freecamEntity)
			return false;

		if (Minecraft.thePlayer.isDead) {
			return false;
		}
		if (Minecraft.thePlayer.isDead) {
			return false;
		}
		if (Teams.isOnSameTeam(ent)) {
			return false;
		}
		return !this.blockRayTrace.getValueState() || !ClientUtil.isBlockBetween(new BlockPos(Minecraft.thePlayer.posX,
				Minecraft.thePlayer.posY + (double) Minecraft.thePlayer.getEyeHeight(), Minecraft.thePlayer.posZ),
				new BlockPos(ent.posX, ent.posY + (double) ent.getEyeHeight(), ent.posZ));
	}

	public float randomFloat(float min, float max) {
		return min + (ThreadLocalRandom.current().nextFloat() * (max - min));
	}

	@Override
	public void onEnable() {
		index = 0;

		currentRotations = new myAngle(Minecraft.thePlayer.rotationYaw, Minecraft.thePlayer.rotationPitch);

		super.onEnable();

	}

	@Override
	public void onDisable() {
		TargetHUDOld.TargetHUD = null;
		TargetHUD.TargetHUD = null;

		this.targets.clear();
		Target = null;
		if (isSword() && this.Blockreach) {
			Minecraft.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
					C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
			Minecraft.thePlayer.itemInUseCount = 0;
			this.Blockreach = false;

		}
		if (Minecraft.thePlayer.itemInUseCount == 520) {
			Minecraft.thePlayer.itemInUseCount = 0;
		}
		super.onDisable();
	}

	@EventTarget
	public void targetHud(EventRender2D event) {
		if (this.targetHUD.isCurrentMode("OLD")) {
			ScaledResolution sr = new ScaledResolution(mc);
			TargetHUDOld.onScreenDraw(sr);
		} else if (this.targetHUD.isCurrentMode("New")) {
			ScaledResolution sr = new ScaledResolution(mc);
			TargetHUD.onScreenDraw(sr, Target);
		}

		if (targetHP.getValueState()) {
			if (KillAura.Target != null) {
				ScaledResolution sr = new ScaledResolution(mc);
				GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
				GL11.glEnable(3042);
				Mod.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/icons.png"));
				int i = MathHelper.ceiling_float_int(KillAura.Target.getHealth());
				boolean flag = Mod.mc.ingameGUI.healthUpdateCounter > (long) Mod.mc.ingameGUI.updateCounter
						&& (Mod.mc.ingameGUI.healthUpdateCounter - (long) Mod.mc.ingameGUI.updateCounter) / 3L
								% 2L == 1L;
				int j = Mod.mc.ingameGUI.lastPlayerHealth;
				IAttributeInstance iattributeinstance = KillAura.Target
						.getEntityAttribute(SharedMonsterAttributes.maxHealth);
				int i1 = sr.getScaledWidth() / 2 - 44;
				sr.getScaledWidth();
				int k1 = sr.getScaledHeight() / 2 - 66;
				float f = (float) iattributeinstance.getAttributeValue();
				float f1 = KillAura.Target.getAbsorptionAmount();
				int l1 = MathHelper.ceiling_float_int((f + f1) / 2.0F / 10.0F);
				int i2 = Math.max(10 - (l1 - 2), 3);
				int j2 = k1 - (l1 - 1) * i2 + 40;
				float f2 = f1;
				// int k2 = KillAura.Target.getTotalArmorValue();
				int k2 = 0;
				int l2 = -1;
				if (KillAura.Target.isPotionActive(Potion.regeneration)) {
					l2 = Mod.mc.ingameGUI.updateCounter % MathHelper.ceiling_float_int(f + 5.0F);
				}
				int k5;
				int l7;
				int j6;
				int l3;
				int i4;

				for (k5 = 0; k5 < 10; ++k5) {
					if (k2 > 0) {
						l7 = i1 + k5 * 9;
						if (k5 * 2 + 1 < k2) {
							Mod.mc.ingameGUI.drawTexturedModalRect(l7, j2, 34, 9, 9, 9);
						}

						if (k5 * 2 + 1 == k2) {
							Mod.mc.ingameGUI.drawTexturedModalRect(l7, j2, 25, 9, 9, 9);
						}

						if (k5 * 2 + 1 > k2) {
							Mod.mc.ingameGUI.drawTexturedModalRect(l7, j2, 16, 9, 9, 9);
						}
					}
				}
				for (int j5 = MathHelper.ceiling_float_int((f + f1) / 2.0F) - 1; j5 >= 0; --j5) {
					k5 = 16;
					if (KillAura.Target.isPotionActive(Potion.poison)) {
						k5 += 36;
					} else if (KillAura.Target.isPotionActive(Potion.wither)) {
						k5 += 72;
					}

					if (flag) {
					}

					j6 = MathHelper.ceiling_float_int((float) (j5 + 1) / 10.0F) - 1;
					l3 = i1 + j5 % 10 * 9;
					i4 = k1 - j6 * i2 + 50;
					if (i <= 4) {
						i4 += Mod.mc.ingameGUI.rand.nextInt(2);
					}

					if (j5 == l2) {
						i4 -= 2;
					}

					byte b1 = 0;
					if (KillAura.Target.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
						b1 = 5;
					}

					Mod.mc.ingameGUI.drawTexturedModalRect(l3, i4, 16 + (KillAura.Target.hurtTime > 8 ? 1 : 0) * 9,
							9 * b1, 9, 9);

					if (f2 <= 0.0F) {
						if (j5 * 2 + 1 < i) {
							Mod.mc.ingameGUI.drawTexturedModalRect(l3, i4, k5 + 36, 9 * b1, 9, 9);
						}

						if (j5 * 2 + 1 == i) {
							Mod.mc.ingameGUI.drawTexturedModalRect(l3, i4, k5 + 45, 9 * b1, 9, 9);
						}
					} else {
						if (f2 == f1 && f1 % 2.0F == 1.0F) {
							Mod.mc.ingameGUI.drawTexturedModalRect(l3, i4, k5 + 153, 9 * b1, 9, 9);
						} else {
							Mod.mc.ingameGUI.drawTexturedModalRect(l3, i4, k5 + 144, 9 * b1, 9, 9);
						}

						f2 -= 2.0F;
					}

				}

				String name = KillAura.Target.getDisplayName().getFormattedText();
				mc.fontRendererObj.drawStringWithShadow(name,
						sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(name) / 2, (k2 > 0) ? j2 - 10 : j2,
						16777215);
			}
		}
	}

}
