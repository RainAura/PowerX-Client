package cn.Power.mod.mods;

import org.lwjgl.input.Keyboard;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class ClickGui extends Mod {
	
	public Value<Double> MaxHeight = new Value<Double>("ClickGui_MaxHeight", 300.0, 100.0, 1000.0, 5.0);
	public Value<Double> BackGround_Round = new Value<Double>("ClickGui_Round", 2.69, 0.0, 5.0, 0.01);

	public Value<Boolean> BackGround = new Value("ClickGui_BackGround", true);

	public ClickGui() {
		super("ClickGui", Category.RENDER);
		this.setKey(Keyboard.KEY_RSHIFT);
		this.clickgui = true;

	}

	@Override
	public void onEnable() {
		this.mc.displayGuiScreen(Client.instance.NewClickGui());
		
		this.set(false);
	}

}
