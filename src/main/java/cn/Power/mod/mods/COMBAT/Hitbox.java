package cn.Power.mod.mods.COMBAT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.util.friendManager.FriendManager;
import cn.Power.util.misc.ChatUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
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

public class Hitbox extends Mod {

	public Value<Boolean> player = new Value("Hitbox_Player", true);
	public Value<Boolean> animals = new Value("Hitbox_Animals", false);
	public Value<Boolean> mobs = new Value("Hitbox_Mobs", false);
	public Value<Boolean> invis = new Value("Hitbox_Invisibles", true);

	public static Value<Double> size = new Value("Hitbox_Size", 0.5d, 0.1d, 1.0d, 0.01d);

	public Hitbox() {
		super("Hitbox", Category.COMBAT);
	}
	
	@EventTarget
	private void update(EventUpdate e) {
		this.setDisplayName(size.getValueState().toString());
	}

	public boolean isvalid(Entity entity) {
		boolean players = player.getValueState();
		boolean Invis = invis.getValueState();
		boolean animal = animals.getValueState();
		boolean mob = mobs.getValueState();
		if (entity.isInvisible() && !Invis) {
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

			if (entity instanceof EntityPlayer && FriendManager.isFriend(entity.getName())) {
				return false;
			}
			if (entity instanceof EntityPlayer && Teams.isOnSameTeam(entity)) {
				return false;
			}

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
