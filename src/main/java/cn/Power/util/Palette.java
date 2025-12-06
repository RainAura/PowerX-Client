package cn.Power.util;

import java.awt.Color;
import java.util.function.Supplier;

public enum Palette {
	GREEN((Supplier<Color>) ((Supplier) () -> new Color(0, 255, 138))),
	WHITE((Supplier<Color>) ((Supplier) () -> Color.WHITE)),
	PURPLE((Supplier<Color>) ((Supplier) () -> new Color(198, 139, 255))),
	DARK_PURPLE((Supplier<Color>) ((Supplier) () -> new Color(133, 46, 215))),
	BLUE((Supplier<Color>) ((Supplier) () -> new Color(116, 202, 255)));

	private final Supplier<Color> colorSupplier;

	private Palette(Supplier<Color> colorSupplier) {
		this.colorSupplier = colorSupplier;
	}

	public static Color fade(Color color) {
		return Palette.fade(color, 2, 100);
	}

	public static Color fade(Color color, int index, int count) {
		float[] hsb = new float[3];
		Color.RGBtoHSB((int) color.getRed(), (int) color.getGreen(), (int) color.getBlue(), (float[]) hsb);
		float brightness = Math.abs(
				(float) (((float) (System.currentTimeMillis() % 2000L) / 1000.0f + (float) index / (float) count * 2.0f)
						% 2.0f - 1.0f));
		brightness = 0.5f + 0.5f * brightness;
		hsb[2] = brightness % 2.0f;
		return new Color(Color.HSBtoRGB((float) hsb[0], (float) hsb[1], (float) hsb[2]));
	}

	public Color getColor() {
		return (Color) this.colorSupplier.get();
	}
}
