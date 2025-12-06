package cn.Power.mod.mods.RENDER;

import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Speed;

public class NoHurtcam extends Mod {

	public NoHurtcam() {
		super("NoHurtcam", Category.RENDER);
		ModManager.getModByClass(Speed.class);
	}

}
