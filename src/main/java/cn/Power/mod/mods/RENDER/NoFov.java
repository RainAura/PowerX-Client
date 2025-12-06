package cn.Power.mod.mods.RENDER;

import cn.Power.Value;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class NoFov extends Mod {
	public NoFov() {
		super("NoFov", Category.RENDER);

	}

	public static Value<Double> Fov = new Value<Double>("NoFov_Fov", 1.0, 1.0, 1.5, 0.1D);

	@Override
	public void onDisable() {
		super.onDisable();

	}

	public void onEnable() {
		super.isEnabled();

	}
}