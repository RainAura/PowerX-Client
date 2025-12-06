package cn.Power.mod.mods.RENDER;

import java.awt.Color;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.ClientUtil;
import cn.Power.util.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;

public class Crosshair extends Mod {

	public static Value<Boolean> DYNAMIC = new Value("Crosshair_Dynamicd", false);
	public Value<Double> GAP = new Value<Double>("Crosshair_Gap", 0.25, 0.25, 15.0, 0.25);
	public Value<Double> WIDTH = new Value<Double>("Crosshair_Width", 0.5, 0.25, 10.0, 0.25);
	public Value<Double> SIZE = new Value<Double>("Crosshair_Size", 4.5, 0.25, 15.0, 0.25);

	public Crosshair() {
		super("Crosshair", Category.RENDER);
	}

	@EventTarget
	public void onRender2D(EventRender2D event) {
		double gap = this.GAP.getValueState().doubleValue();
		double width = this.WIDTH.getValueState().doubleValue();
		double size = this.SIZE.getValueState().doubleValue();
		ScaledResolution scaledRes = new ScaledResolution(mc);
		RenderUtils.rectangleBordered((double) (scaledRes.getScaledWidth() / 2) - width,
				(double) (scaledRes.getScaledHeight() / 2) - gap - size - (double) (this.isMoving() ? 2 : 0),
				(double) ((float) (scaledRes.getScaledWidth() / 2) + 1.0f) + width,
				(double) (scaledRes.getScaledHeight() / 2) - gap - (double) (this.isMoving() ? 2 : 0), 0.5,
				ClientUtil.reAlpha(Hud.getColor(), 0.8F), new Color(0, 0, 0, 200).getRGB());
		RenderUtils.rectangleBordered((double) (scaledRes.getScaledWidth() / 2) - width,
				(double) (scaledRes.getScaledHeight() / 2) + gap + 1.0 + (double) (this.isMoving() ? 2 : 0) - 0.15,
				(double) ((float) (scaledRes.getScaledWidth() / 2) + 1.0f) + width,
				(double) (scaledRes.getScaledHeight() / 2 + 1) + gap + size + (double) (this.isMoving() ? 2 : 0) - 0.15,
				0.5, ClientUtil.reAlpha(Hud.getColor(), 0.8F), new Color(0, 0, 0, 200).getRGB());
		RenderUtils.rectangleBordered(
				(double) (scaledRes.getScaledWidth() / 2) - gap - size - (double) (this.isMoving() ? 2 : 0) + 0.15,
				(double) (scaledRes.getScaledHeight() / 2) - width,
				(double) (scaledRes.getScaledWidth() / 2) - gap - (double) (this.isMoving() ? 2 : 0) + 0.15,
				(double) ((float) (scaledRes.getScaledHeight() / 2) + 1.0f) + width, 0.5,
				ClientUtil.reAlpha(Hud.getColor(), 0.8F), new Color(0, 0, 0, 200).getRGB());
		RenderUtils.rectangleBordered(
				(double) (scaledRes.getScaledWidth() / 2 + 1) + gap + (double) (this.isMoving() ? 2 : 0),
				(double) (scaledRes.getScaledHeight() / 2) - width,
				(double) (scaledRes.getScaledWidth() / 2) + size + gap + 1.0 + (double) (this.isMoving() ? 2 : 0),
				(double) ((float) (scaledRes.getScaledHeight() / 2) + 1.0f) + width, 0.5,
				ClientUtil.reAlpha(Hud.getColor(), 0.8F), new Color(0, 0, 0, 200).getRGB());

	}

	public boolean isMoving() {

		if (this.DYNAMIC.getValueState().booleanValue() == false)
			return false;
		if (mc.thePlayer.isCollidedHorizontally)
			return false;
		if (mc.thePlayer.isSneaking())
			return false;
		if (mc.thePlayer.movementInput.moveForward != 0.0f)
			return true;
		if (mc.thePlayer.movementInput.moveStrafe == 0.0f)
			return false;
		return true;
	}

}
