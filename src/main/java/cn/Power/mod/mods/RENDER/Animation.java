package cn.Power.mod.mods.RENDER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class Animation extends Mod {

	public static Value<String> mode = new Value("Animation", "Mode", 0);
	public static Value<Double> SwingSpeed = new Value<Double>("Animation_Swing Speed", 6.0D, 2.0D, 25.0D, 1D);
	public static Value<Double> X = new Value<Double>("Animation_X", 0.0D, -2.0D, 2.0D, 0.1D);
	public static Value<Double> Y = new Value<Double>("Animation_Y", 0.0D, -2.0D, 2.0D, 0.1D);
	public static Value<Double> ZOOM = new Value<Double>("Animation_Zoom", 0.0D, -2.0D, 2.0D, 0.1D);
	public static Value<Double> SCALE = new Value<Double>("Animation_Scale", 2.0D, -4.0D, 4.0D, 0.1D);
	public static Value<Boolean> DigSpeed = new Value("Animation_Dig Speed", false);
	public static Value<Boolean> Damage = new Value("Animation_No Item Damage Move", true);
	public static Value<Boolean> mini = new Value("Animation_Mini Sword", true);
	public static Value<Boolean> mini_item = new Value("Animation_Mini Items", false);
	public static int thisFloat;

	public Animation() {
		super("Animation", Category.RENDER);
		HideMod = true;
		this.mode.mode.add("Polaris");
		this.mode.mode.add("Vanilla");
		this.mode.mode.add("Swing");
		this.mode.mode.add("Swank");
		this.mode.mode.add("Gay");
		this.mode.mode.add("Slide");
		this.mode.mode.add("ETB");
		this.mode.mode.add("Sigma");
		this.mode.mode.add("Power");
		this.mode.mode.add("Power2");
		this.mode.mode.add("Exhibition");
		this.mode.mode.add("LiquidBounce");
		this.mode.mode.add("Tap");
		this.mode.mode.add("Avatar");
		this.mode.mode.add("Leaked");
		this.mode.mode.add("Rotate");
		this.mode.mode.add("Custom");
	}
	
	@EventTarget
	public void onPre(EventPreMotion p) {
		
		if(mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.isBlocking())
			mc.thePlayer.renderArmPitch -= 18f;
	}

	@EventTarget
	private void R2D(EventRender2D e) {
		if (thisFloat > 360f) {
			thisFloat = 0;
		} else {
			thisFloat += 5;
		}
	}
}
