package cn.Power.mod.mods.COMBAT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class Reach extends Mod {

	public static Value<Double> Reach = new Value<Double>("Reach_Reach", 3.5d, 3d, 7.0d, 0.1d);
	public static Value<Double> BlockReach = new Value<Double>("Reach_BlockReach", 5.0d, 4.5d, 7.0d, 0.1d);

	public Reach() {
		super("Reach", Category.COMBAT);
	}

	public final float getMaxRange() {
		float combatRange = Reach.getValueState().floatValue();
		float buildRange = BlockReach.getValueState().floatValue();
		return combatRange > buildRange ? combatRange : buildRange;
	}

	@EventTarget
	public void onpre(EventPreMotion e) {
		this.setDisplayName("" + Reach.getValueState().doubleValue());
	}

	@Override
	public void onEnable() {

	}

}
