package cn.Power.mod.mods.RENDER;

import cn.Power.Value;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class DragWings extends Mod {
	public static Value<Double> r = new Value("DragWings_Red", 255d, 0d, 255d, 1d);
	public static Value<Double> g = new Value("DragWings_Green", 255d, 0d, 255d, 1d);
	public static Value<Double> b = new Value("DragWings_Blue", 255d, 0d, 255d, 1d);
	public static Value<Boolean> team = new Value("DragWings_TeamWings", true);

	public DragWings() {
		super("DragWings", Category.RENDER);
		HideMod = true;
	}

}
