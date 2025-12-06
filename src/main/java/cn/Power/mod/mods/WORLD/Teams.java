package cn.Power.mod.mods.WORLD;

import cn.Power.Value;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;

public class Teams extends Mod {

	public static Value<String> mode = new Value("Teams", "Mode", 0);

	public Teams() {
		super("Teams", Category.WORLD);

		this.mode.mode.add("Normal");
		this.mode.mode.add("Rank");
	}

	public static boolean isOnSameTeam(Entity entity) {
		if (entity instanceof EntityPlayer
				|| (entity instanceof EntityWither && ((EntityWither) entity).hasCustomName())) {
			if (ModManager.getModByClass(Teams.class).isEnabled()) {
				if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().startsWith("\247")) {
					if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().length() <= 2
							|| entity.getDisplayName().getUnformattedText().length() <= 2) {
						return false;
					}

					if (mode.isCurrentMode("Normal")) {
						if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().substring(0, 2)
								.equals(entity.getDisplayName().getUnformattedText().substring(0, 2))) {
							return true;
						}
					} else {
						return entity.getDisplayName().getFormattedText().charAt(1) == mc.thePlayer.getDisplayName()
								.getFormattedText().charAt(1);
					}
				}
			}
			return false;
		} else {

			return false;
		}
	}
}
