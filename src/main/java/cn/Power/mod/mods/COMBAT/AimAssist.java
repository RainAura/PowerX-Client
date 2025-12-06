package cn.Power.mod.mods.COMBAT;

import java.util.ArrayList;
import java.util.Comparator;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.util.ClientUtil;
import cn.Power.util.CombatUtil;
import cn.Power.util.MathUtils;
import cn.Power.util.RotationUtils;
import cn.Power.util.friendManager.FriendManager;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;

public class AimAssist extends Mod {
	public Value<Double> horizontalSpeed;
	public Value<Double> verticalSpeed;
	public Value<Double> range;
	public Value<Double> fov;
	public Value<Boolean> clickAim;
	public Value<Boolean> weapon;
	public ArrayList<EntityLivingBase> targets = new ArrayList();
	public Value<String> priority;
	public Value<Boolean> blockRayTrace;
	public Value<Boolean> attackPlayers = new Value<Boolean>("AimAssist_Players", true);
	public Value<Boolean> invisible = new Value<Boolean>("AimAssist_Invisibles", false);
	public Value<Boolean> attackAnimals = new Value<Boolean>("AimAssist_Animals", false);
	public Value<Boolean> attackMobs = new Value<Boolean>("AimAssist_Mobs", false);
	EntityLivingBase Target;

	public AimAssist() {
		super("AimAssist", Category.COMBAT);
		this.priority = new Value(AimAssist.class.getSimpleName(), "Priority", 0);
		this.horizontalSpeed = new Value<Double>("AimAssist_H Speed", 60.0, 0.0, 180.0, 10.0);
		this.verticalSpeed = new Value<Double>("AimAssist_V Speed", 60.0, 0.0, 180.0, 10.0);
		this.range = new Value<Double>("AimAssist_Range", 3.8, 3.0, 10.0, 0.1);
		this.fov = new Value<Double>("AimAssist_Fov", 120.0, 1.0, 360.0, 5.0);
		blockRayTrace = new Value<Boolean>("AimAssist_RayTrace", false);
		this.clickAim = new Value<Boolean>("AimAssist_ClickAim", true);
		this.weapon = new Value<Boolean>("AimAssist_Weapon", false);

		this.priority.mode.add("Health");
		this.priority.mode.add("Reach");
		this.priority.mode.add("Angle");
		this.priority.mode.add("Fov");
	}

	@EventTarget
	public void targethud(EventRender2D e) {
		ScaledResolution sr = new ScaledResolution(mc);
		if (this.weapon.getValueState()) {
			if (mc.thePlayer.getCurrentEquippedItem() == null) {
				return;
			}
			if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword)) {
				return;
			}
		}
		if (this.clickAim.getValueState() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
			return;
		}
		if (Target != null) {
			// TargetHUD.onScreenDraw(sr,Target);
		}
	}

	@EventTarget
	public void Onpost(EventPostMotion e) {
		if (mc.theWorld != null) {
			this.clear();
			this.findTargets();
			setTarget();
		}
	}

	@EventTarget
	public void onUpdate(EventPreMotion e) {
		if (mc.theWorld != null) {
			if (this.weapon.getValueState()) {
				if (mc.thePlayer.getCurrentEquippedItem() == null) {
					return;
				}
				if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword)) {
					return;
				}
			}
			if (this.clickAim.getValueState() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
				return;
			}
			if (Target != null
					&& (double) mc.thePlayer.getDistanceToEntity(Target) <= range.getValueState().doubleValue()) {
				boolean b = this.fov(Target) > 0.0;
				double Rotatespeed = (float) (Math.random()
						/ (90 / (horizontalSpeed.getValueState().floatValue() + MathUtils
								.getRandomInRange(0.0001235423532523523532521, 0.000123542353252352353252 * 20)) * 3.0D
								/ 1.2D - 90 * 3.0D / 0.8D)
						+ 90 / (horizontalSpeed.getValueState().floatValue() + MathUtils
								.getRandomInRange(0.0001235423532523523532521, 0.000123542353252352353252 * 20)) * 3.0D
								/ 0.8D);

				if ((this.fov(Target) > 7.0 || this.fov(Target) < -7.0) && horizontalSpeed.getValueState() >= 10) {
					
					 double f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
					 double gcd = f * f * f * 1.2F;
					
					float nextYaw = mc.thePlayer.rotationYaw + (float) (b ? (-(Math.abs(this.fov(Target)) / Rotatespeed))
							: (Math.abs(this.fov(Target)) / Rotatespeed));
					
			        float deltaYaw = nextYaw - mc.thePlayer.rotationYaw;
			        deltaYaw -= deltaYaw % gcd;
			        mc.thePlayer.rotationYaw = mc.thePlayer.rotationYaw + deltaYaw;
				}

				double RotatespeedV = (float) (Math.random()
						/ (90 / (verticalSpeed.getValueState().floatValue() + MathUtils
								.getRandomInRange(0.0001235423532523523532521, 0.000123542353252352353252 * 20)) * 3.0D
								/ 1.2D - 90 * 3.0D / 0.8D)
						+ 90 / (verticalSpeed.getValueState().floatValue() + MathUtils
								.getRandomInRange(0.0001235423532523523532521, 0.000123542353252352353252 * 20)) * 3.0D
								/ 0.8D);

				boolean p = this.fovP(Target) > 0.0;
				if ((this.fovP(Target) > 20.0 || this.fovP(Target) < -1.0) && verticalSpeed.getValueState() >= 10) {
					float nextPitch = mc.thePlayer.rotationPitch + (float) (p ? (-(Math.abs(this.fovP(Target)) / RotatespeedV))
							: (Math.abs(this.fovP(Target)) / RotatespeedV));
					
					 double f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
					 double gcd = f * f * f * 1.2F;
					
			        float deltaYaw = nextPitch - mc.thePlayer.rotationPitch;
			        deltaYaw -= deltaYaw % gcd;
			        mc.thePlayer.rotationPitch = mc.thePlayer.rotationPitch + deltaYaw;
				}
			}
		}
	}

	public double fov(final EntityLivingBase entityLivingBase) {
		return ((mc.thePlayer.rotationYaw - this.faceTarget(entityLivingBase)[0]) % 360.0 + 540.0) % 360.0 - 180.0;
	}

	public double fovP(final EntityLivingBase entityLivingBase) {
		return ((mc.thePlayer.rotationPitch - this.faceTarget(entityLivingBase)[1]) % 360.0 + 540.0) % 360.0 - 180.0;
	}

	private void setTarget() {
		if (targets.size() == 0) {
			Target = null;
			return;
		}
		Target = this.targets.get(0);
	}

	private void clear() {
		// Target = null;
		for (EntityLivingBase ent : this.targets) {
			if (this.isValidEntity(ent, range.getValueState().doubleValue()))
				continue;
			this.targets.remove(ent);
		}
	}

	private void findTargets() {
		// int maxSize = Mode.isCurrentMode("Switch") ? 4 : 4;
		int maxSize = 3;
		for (Entity o3 : mc.theWorld.loadedEntityList) {
			EntityLivingBase curEnt;
			if (o3 instanceof EntityLivingBase
					&& this.isValidEntity(curEnt = (EntityLivingBase) o3, range.getValueState().doubleValue())
					&& !this.targets.contains(curEnt)) {
				this.targets.add(curEnt);
			}
			if (this.targets.size() >= maxSize)
				break;
		}
		if (this.priority.isCurrentMode("Reach") && (mc.gameSettings.keyBindSprint.isKeyDown())) {
			targets.sort((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
			return;
		}
		if (this.priority.isCurrentMode("Health") && (mc.gameSettings.keyBindSprint.isKeyDown())) {
			targets.sort(Comparator.comparingDouble(player -> mc.thePlayer.getDistanceToEntity(player)));
			return;
		}
		if (this.priority.isCurrentMode("Reach")) {
			targets.sort(Comparator.comparingDouble(player -> mc.thePlayer.getDistanceToEntity(player)));
		}
		if (this.priority.isCurrentMode("Fov")) {
			this.targets.sort(Comparator.comparingDouble(o -> CombatUtil
					.getDistanceBetweenAngles(mc.thePlayer.rotationPitch, CombatUtil.getRotations(o)[0])));
		}
		if (this.priority.isCurrentMode("Angle")) {
			this.targets.sort((o1, o2) -> {
				float[] rot1 = RotationUtils.getRotations(o1);
				float[] rot2 = RotationUtils.getRotations(o2);
				return (int) (mc.thePlayer.rotationYaw - rot1[0] - (mc.thePlayer.rotationYaw - rot2[0]));
			});
		}
		if (this.priority.isCurrentMode("Health")) {
			targets.sort((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
		}

	}

	private boolean isValidEntity(EntityLivingBase ent, double range) {
		if (ent instanceof EntityArmorStand) {
			return false;
		}
		if (ent == null) {
			return false;
		}
		if (ent == mc.thePlayer) {
			return false;
		}
		if (ent instanceof EntityPlayer && !this.attackPlayers.getValueState().booleanValue()) {
			return false;
		}
		if ((ent instanceof EntityAnimal || ent instanceof EntitySquid || ent instanceof EntityVillager
				|| ent instanceof EntityBat) && !this.attackAnimals.getValueState().booleanValue()) {
			return false;
		}
		if ((ent instanceof EntityMob || ent instanceof EntityDragon || ent instanceof EntityGhast
				|| ent instanceof EntitySlime || ent instanceof EntityIronGolem || ent instanceof EntitySnowman)
				&& !this.attackMobs.getValueState().booleanValue()) {
			return false;
		}
		if ((double) mc.thePlayer.getDistanceToEntity(ent) >= range) {
			return false;
		}
		if (!this.inFov((EntityLivingBase) ent, this.fov.getValueState().intValue())) {
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

		if (mc.thePlayer.isDead) {
			return false;
		}
		if (this.mc.thePlayer.isDead) {
			return false;
		}
		if (ent instanceof EntityPlayer && Teams.isOnSameTeam(ent)) {
			return false;
		}
		return this.blockRayTrace.getValueState() == false
				|| !ClientUtil.isBlockBetween(
						new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(),
								mc.thePlayer.posZ),
						new BlockPos(ent.posX, ent.posY + (double) ent.getEyeHeight(), ent.posZ));
	}

	public boolean inFov(final EntityLivingBase entityLivingBase, float n) {
		n *= 0.5;
		final double n2 = ((mc.thePlayer.rotationYaw - this.faceTarget(entityLivingBase)[0]) % 360.0 + 540.0) % 360.0
				- 180.0;
		return (n2 > 0.0 && n2 < n) || (-n < n2 && n2 < 0.0);
	}

	public double[] faceTarget(EntityLivingBase entityLivingBase) {
		double x = entityLivingBase.posX - mc.thePlayer.posX;
		double y = entityLivingBase.posY - mc.thePlayer.posY;
		double z = entityLivingBase.posZ - mc.thePlayer.posZ;
		y /= mc.thePlayer.getDistanceToEntity(entityLivingBase);
		final double yaw = -(Math.atan2(x, z) * 57.29577951308232);
		final double pitch = -(Math.asin(y) * 57.29577951308232);
		return new double[] { yaw, pitch };
	}
}
