package cn.Power.mod.mods.RENDER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FullBright extends Mod {

	public FullBright() {
		super("FullBright", Category.RENDER);
		HideMod = true;
	}

	@EventTarget
	public void onUpdate(EventUpdate event) {
		mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 5200, 3));
		this.mc.thePlayer.removePotionEffect(Potion.confusion.getId());
		this.mc.thePlayer.removePotionEffect(Potion.blindness.getId());
		
		
		this.mc.gameSettings.gammaSetting = 10.0f;
	}

	@Override

	public void onDisable() {
		super.onDisable();
		this.mc.gameSettings.gammaSetting = 1.0f;
		this.mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
	}
}
