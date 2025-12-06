
package cn.Power.mod.mods.PLAYER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventMove;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class Respawn extends Mod {
	public Respawn() {
		super("Respawn", Category.PLAYER);
	}

	@EventTarget
	public void onUpdate(EventMove event) {
		if (!this.mc.thePlayer.isEntityAlive()) {
			this.mc.thePlayer.respawnPlayer();
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