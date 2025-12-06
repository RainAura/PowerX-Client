package cn.Power.mod.mods.PLAYER;

import cn.Power.Value;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class DankBobbing extends Mod {
	public static boolean enabled = false;
	boolean oldBobbingSetting;
	public static Value<Double> Multiplier = new Value<Double>("DankBobbing_Multiplier", 2.0, 1.0, 5.0, 1.0);

	public DankBobbing() {
		super("DankBobbing", Category.PLAYER);
	}

	public static double getMultiplier() {
		return (double) Multiplier.getValueState() * 10;
	}

	@Override
	public void onDisable() {
		mc.gameSettings.viewBobbing = this.oldBobbingSetting;
		enabled = false;
		super.onDisable();
	}

	@Override
	public void onEnable() {
		this.oldBobbingSetting = mc.gameSettings.viewBobbing;
		mc.gameSettings.viewBobbing = true;
		enabled = true;
		super.onEnable();
	}

}
