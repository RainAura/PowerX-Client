package cn.Power.mod.mods.PLAYER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.item.ItemBlock;

public class InvManager extends Mod {
	
	//TODO:....未完工
	public Value<Double> BLOCKCAP = new Value<Double>("InvManager_Sword Slot", 128.0, 0.0, 512.0, 8.0);
	public Value<Double> DELAY = new Value<Double>("InvManager_Delay", 1.0, 1.0, 10.0, 1.0);
	public Value<Boolean> Tools = new Value("InvManager_Tools", true);
	public Value<Boolean> ARCHERY = new Value("InvManager_Archery", true);
	public Value<Boolean> FOOD = new Value("InvManager_FOOD", true);
	public Value<Boolean> UHC = new Value("InvManager_UHC", false);
	public Value<Boolean> AuraCheck = new Value("InvManager_AuraCheck", false);
	public Value<Boolean> TOGGLE = new Value("InvManager_Toggle", false);

	public InvManager() {
		super("InvManager", Category.PLAYER);
	}

	@EventTarget
	public void onUpdate(EventUpdate event) {
		if (mc.thePlayer.inventory.getCurrentItem() != null) {
			if (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock) {
				mc.rightClickDelayTimer = 0;
			} else {

			}
		}
	}

	@Override
	public void onDisable() {
		mc.rightClickDelayTimer = 4;
		super.onDisable();
	}
}
