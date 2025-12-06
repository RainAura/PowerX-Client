package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.item.ItemBlock;

public class FastPlace extends Mod {

	public FastPlace() {
		super("FastPlace", Category.WORLD);
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
