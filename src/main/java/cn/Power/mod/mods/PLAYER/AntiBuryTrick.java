package cn.Power.mod.mods.PLAYER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class AntiBuryTrick  extends Mod {
	public static boolean status;
	public static Value<Boolean> teamMate_only = new Value("AntiBuryTrick_TeamMateOnly", true);
	
	public AntiBuryTrick() {
		super("AntiBuryTrick", Category.PLAYER);
	}

	@Override
	public void onEnable() {
		status = true;
	}
	
	@Override
	public void onDisable() {
		status = false;
	}
}
