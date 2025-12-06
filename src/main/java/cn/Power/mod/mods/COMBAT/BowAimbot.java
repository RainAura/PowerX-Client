package cn.Power.mod.mods.COMBAT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.PLAYER.Freecam;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.util.ClientUtil;
import cn.Power.util.CombatUtil;
import cn.Power.util.RotationUtils;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.friendManager.FriendManager;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class BowAimbot extends Mod {


	public Value<String> priority = new Value<String>("BowAimbot", "Priority", 0);
	
	public Value<Double> reach = new Value<Double>("BowAimbot_Reach", 120.0, 1.0, 500.0, 50.0);
	public Value<Double> mindis = new Value<Double>("BowAimbot_MinDistance", 120.0, 1.0, 500.0, 50.0);
	public Value<Double> smooth = new Value<Double>("BowAimbot_Smooth", 4.0, 1.0, 100.0, 0.1);
	
	public Value<Boolean> attackPlayers = new Value<Boolean>("BowAimbot_Players", true);
	public Value<Boolean> invisible = new Value<Boolean>("BowAimbot_Invisibles", false);
	public Value<Boolean> attackAnimals = new Value<Boolean>("BowAimbot_Animals", false);
	public Value<Boolean> Villager = new Value<Boolean>("BowAimbot_Villager", false);
	public Value<Boolean> attackMobs = new Value<Boolean>("BowAimbot_Mobs", false);
	
	public Value<Boolean> IgnoreMwAnimals = new Value<Boolean>("BowAimbot_IgnoreMwAnimals", true);
	
	
	boolean send, isFiring;
	private EntityLivingBase en;
	public static EntityLivingBase target;
	private static double sideMultiplier;
	private static double upMultiplier;
	public static boolean isValid;
	private static Vec3 toFace = null;

	public BowAimbot() {
		super("BowAimbot", Category.COMBAT);


		this.priority.mode.add("Health");
		this.priority.mode.add("Reach");
		this.priority.mode.add("Armor");
		this.priority.mode.add("Angle");
		this.priority.mode.add("Fov");
		this.priority.mode.add("HurtTime");
	}

	@Override
	public void onDisable() {
		target = null;
		
		toFace = null;
		isValid = false;
		
		super.onDisable();
		

	}

	@EventTarget
	public void onPre(EventUpdate em) {

		boolean isValid;
		if (!(mc.currentScreen == null)) {
			isValid = false;
			return;
		}
		try {
			if (mc.thePlayer.getCurrentEquippedItem() == null) {
				isValid = false;
				return;
			}
			if (mc.thePlayer.getCurrentEquippedItem().getItem() == null) {
				isValid = false;
				return;
			}
			if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow)) {
				isValid = false;
				return;
			}
			if (!(mc.thePlayer.isUsingItem())) {
				isValid = false;
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			isValid = false;
			return;
		}

		List<EntityLivingBase> ens = this.getOptimalTarget(this.reach.getValueState().doubleValue());
		double minDistance = this.mindis.getValueState().doubleValue();
		for (EntityLivingBase en : ens) {
			if (mc.theWorld.rayTraceBlocks(
					new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
					new Vec3(en.posX, en.posY + en.getEyeHeight(), en.posZ), false, true, false) != null) {
				continue;
			}
			if (mc.thePlayer.getDistanceToEntity(en) < minDistance) {
				minDistance = mc.thePlayer.getDistanceToEntity(en);
				this.en = en;
			}
		}
		if (en == null) {
			isValid = false;
			return;
		}
		if (mc.theWorld.rayTraceBlocks(
				new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
				new Vec3(en.posX, en.posY + en.getEyeHeight(), en.posZ), false, true, false) != null) {
			en = null;
			isValid = false;
		}
		if (en == null) {
			isValid = false;
			return;
		}
		isValid = true;
		sideMultiplier = mc.thePlayer.getDistanceToEntity(en) / ((mc.thePlayer.getDistanceToEntity(en) / 2) / 1) * 5;
		upMultiplier = (mc.thePlayer.getDistanceSqToEntity(en) / 320) * 1.1;
		generateToFace();

		smoothFacePos(toFace, smooth.getValueState().doubleValue());

	}
	
	private List<EntityLivingBase> getOptimalTarget(double range) {
		List<EntityLivingBase> load = new ArrayList<>();

		Minecraft.theWorld.getLoadedEntityList().stream().filter(o -> o instanceof EntityLivingBase).forEach(o -> {
			EntityLivingBase ent = (EntityLivingBase) o;
			if (isValidEntity(ent, range)) {
				load.add(ent);
			}
		});
		
		this.sortList(load);

		return load;
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

		if ((ent instanceof EntityAnimal || ent instanceof EntityMob) && SkyBlockUtils.isMWgame() && IgnoreMwAnimals.getValueState()
				&& !(ent instanceof EntityWither))
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
		
		return true;
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
		if (this.priority.isCurrentMode("Health")) {
			list.sort((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
		}

		if (this.priority.isCurrentMode("Armor")) {
			list.sort(Comparator.comparingDouble(player -> getArmorVal(player)));
		}
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
	
	public static void smoothFacePos(Vec3 vec, double addSmoothing) {
		double diffX = vec.xCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
		double diffY = vec.yCoord + 0.5
				- (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
		double diffZ = vec.zCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;

		float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

		Minecraft.getMinecraft().thePlayer.rotationYaw += (MathHelper
				.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw))
				/ (addSmoothing);
		Minecraft.getMinecraft().thePlayer.rotationPitch += (MathHelper
				.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch))
				/ (addSmoothing);
	}
	
	private void generateToFace() {
		double xPos = en.posX;
		double yPos = en.posY;
		double zPos = en.posZ;
		toFace = new Vec3((xPos - 0.5) + (xPos - en.lastTickPosX) * sideMultiplier,
				yPos + upMultiplier,
				(zPos - 0.5) + (zPos - en.lastTickPosZ) * sideMultiplier);
	}

	public static boolean shouldAim() {
		if (Minecraft.thePlayer.inventory.getCurrentItem() == null
				|| !(Minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow))
			return false;
		if (Minecraft.thePlayer.isUsingItem())
			return true;
		return false;
	}

	private EntityLivingBase getTarg() {
		List<EntityLivingBase> loaded = new ArrayList();
		for (Object o : mc.theWorld.getLoadedEntityList()) {
			if (o instanceof EntityLivingBase) {
				EntityLivingBase ent = (EntityLivingBase) o;
				if (ent instanceof EntityPlayer && ent != mc.thePlayer && mc.thePlayer.canEntityBeSeen(ent)
						&& !FriendManager.isFriend(ent.getName())) {

					loaded.add(ent);
				}
			}
		}
		if (loaded.isEmpty()) {
			return null;
		}
		loaded.sort((o1, o2) -> {
			float[] rot1 = RotationUtils.getRotations(o1);
			float[] rot2 = RotationUtils.getRotations(o2);
			return (int) ((RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationYaw, rot1[0])
					+ RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationPitch, rot1[1]))
					- (RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationYaw, rot2[0])
							+ RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationPitch, rot2[1])));
		});
		EntityLivingBase target = loaded.get(0);
		return target;
	}
}