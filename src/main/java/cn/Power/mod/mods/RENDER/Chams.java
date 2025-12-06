package cn.Power.mod.mods.RENDER;

import cn.Power.Value;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
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

public class Chams extends Mod {
	public static Value<Boolean> COLORED = new Value("Chams_Colored", true);
	public Value<Boolean> hands = new Value("Chams_Hands", false);
	public Value<Boolean> showArmor = new Value("Chams_Show Armor", false);
	public Value<Boolean> player = new Value("Chams_Player", true);
	public Value<Boolean> animals = new Value("Chams_Animals", false);
	public Value<Boolean> mobs = new Value("Chams_Mobs", false);
	
	public Value<Boolean> invis = new Value("Chams_Invisibles", true);
	public Value<Boolean> team = new Value("Chams_Team", true);
	
	public Value<Double> alpha = new Value("Chams_Alpha", 1.0d, 0.1d, 1.0d, 0.1d);
	public static Minecraft mc = Minecraft.getMinecraft();

	public static boolean isEnabled;
	
	public Chams() {
		super("Chams", Category.RENDER);

	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		isEnabled = true;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		isEnabled = false;
	}
	

	public boolean isvalid(EntityLivingBase entity) {
		boolean players = player.getValueState();
		boolean Invis = invis.getValueState();
		boolean animal = animals.getValueState();
		boolean mob = mobs.getValueState();
		if (entity.isInvisible() && !Invis) {
			return false;
		}
		if (entity == null) {
			return false;
		}
		if (entity == mc.thePlayer) {
			return false;
		}
		if ((players && entity instanceof EntityPlayer)
				|| (mob && (entity instanceof EntityMob || entity instanceof EntityDragon
						|| entity instanceof EntityGhast || entity instanceof EntitySlime
						|| entity instanceof EntityIronGolem || entity instanceof EntitySnowman))
				|| (animal && (entity instanceof EntityAnimal || entity instanceof EntitySquid
						|| entity instanceof EntityVillager || entity instanceof EntityBat))) {
			if (entity instanceof EntityPlayerSP) {

				return mc.gameSettings.thirdPersonView != 0;
			} else {

				return true;
			}
		} else {
			return false;
		}
	}

}
