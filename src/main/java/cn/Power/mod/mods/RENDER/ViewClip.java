package cn.Power.mod.mods.RENDER;

import cn.Power.Value;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class ViewClip extends Mod {
	
	public static Value<Double> distance = new Value<Double>("ViewClip_3rdDistance", 6d, 0.5d, 20.0d, 0.01d);

	public ViewClip() {
		super("ViewClip", Category.RENDER);
	}
}
