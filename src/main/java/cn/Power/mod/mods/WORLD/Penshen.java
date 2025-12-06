package cn.Power.mod.mods.WORLD;

import java.util.Collections;
import java.util.Random;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemStack;

public class Penshen extends Mod {

	TimeHelper delay = new TimeHelper();
	public Value<Double> spammerdelay = new Value("Penshen_Delay", 30d, 0d, 100d, 1d);
	public Value<Boolean> XuanChuan = new Value("Penshen_XuanChuan", false);

	public Penshen() {
		super("Penshen", Category.WORLD);

	}

	@EventTarget
	public void onUpdate(EventUpdate event) {
		if(mc.currentScreen instanceof GuiChest) {
			GuiChest guiChest = (GuiChest) mc.currentScreen;
		
		for (int index = guiChest.lowerChestInventory.getSizeInventory() + 1; index > -1; index--) {
			ItemStack stack = guiChest.lowerChestInventory.getStackInSlot(index);
			if (stack != null) {
				
				System.out.println(index + " " + stack.getDisplayName());
				//mc.playerController.windowClick(guiChest.inventorySlots.windowId, -999, 0, 0,mc.thePlayer);
			}
		}
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();

	}

	public void onEnable() {
		super.isEnabled();

	}
}
