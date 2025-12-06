package cn.Power.mod.mods.RENDER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.client.gui.ScaledResolution;

public class ScoreBoard extends Mod {

	public Value<Double> X = new Value<Double>("ScoreBoard_X", 0.0D, -2.0D, 2.0D, 0.1D);
	public Value<Double> Y = new Value<Double>("ScoreBoard_Y", 0.0D, -2.0D, 2.0D, 0.1D);

	public ScoreBoard() {
		super("ScoreBoard", Category.RENDER);
		HideMod = true;
	}

	@EventTarget
	private void R2D(EventRender2D e) {
		X.valueMax = (double) sr.getScaledWidth() / 3.5;
		X.valueMin = (double) -sr.getScaledWidth() / 3.5;
		
		Y.valueMax = (double) sr.getScaledHeight() * 3.2f;
	}
}
