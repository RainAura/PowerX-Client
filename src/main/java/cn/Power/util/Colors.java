package cn.Power.util;

import java.awt.Color;

import net.minecraft.util.MathHelper;

public enum Colors {
	BLACK(-16711423), BLUE(-12028161), DARKBLUE(-12621684), GREEN(-9830551), DARKGREEN(-9320847), WHITE(-65794),
	AQUA(-7820064), DARKAQUA(-12621684), GREY(-9868951), DARKGREY(-14342875), RED(-65536), DARKRED(-8388608),
	ORANGE(-29696), DARKORANGE(-2263808), YELLOW(-256), DARKYELLOW(-2702025), MAGENTA(-18751), DARKMAGENTA(-2252579);

	public int c;

	private Colors(int co) {
		this.c = co;
	}

	public static int getColor(final Color color) {
		return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static int getColor(final int brightness) {
		return getColor(brightness, brightness, brightness, 255);
	}

	public static int getColor(final int brightness, final int alpha) {
		return getColor(brightness, brightness, brightness, alpha);
	}

	public static int getColor(final int red, final int green, final int blue) {
		return getColor(red, green, blue, 255);
	}

	public static int getColor(final int red, final int green, final int blue, final int alpha) {
		int color = 0;
		color |= MathHelper.clamp_int(alpha, 0, 255) << 24;
		color |= MathHelper.clamp_int(red, 0, 255) << 16;
		color |= MathHelper.clamp_int(green, 0, 255) << 8;
		color |= MathHelper.clamp_int(blue, 0, 255);
		return color;
	}

	public static int getColor1(int red, int green, int blue, int alpha) {
		int color = 0;
		color |= MathHelper.clamp_int(alpha, 0, 255) << 24;
		color |= MathHelper.clamp_int(red, 0, 255) << 16;
		color |= MathHelper.clamp_int(green, 0, 255) << 8;
		return color |= MathHelper.clamp_int(blue, 0, 255);
	}

}
