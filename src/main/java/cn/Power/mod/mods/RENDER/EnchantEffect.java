package cn.Power.mod.mods.RENDER;

import cn.Power.Value;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class EnchantEffect extends Mod {
	public static Value<Double> r = new Value("EnchantEffect_Red", 255d, 0d, 255d, 1d);
	public static Value<Double> g = new Value("EnchantEffect_Green", 255d, 0d, 255d, 1d);
	public static Value<Double> b = new Value("EnchantEffect_Blue", 255d, 0d, 255d, 1d);
	public static Value<Boolean> Rainbow = new Value("EnchantEffect_Rainbow", true);

	public EnchantEffect() {
		super("EnchantEffect", Category.RENDER);
	}

}
