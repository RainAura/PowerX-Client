package cn.Power.mod.mods.RENDER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.entity.Entity;

public class AntiInvis extends Mod {
	public AntiInvis() {
		super("AntiInvis", Category.RENDER);
	}

	@EventTarget
	public void onUpdate(EventUpdate event) {
		// I'm using a loaded entity list so that we can make mobs unvanish as well.
		for (Object possibleEntity : mc.theWorld.loadedEntityList) {
			if (possibleEntity instanceof Entity) {
				Entity entity = (Entity) possibleEntity;
				if (entity.isInvisible()) {
					entity.setInvisible(false);
				}
			}
		}
	}
}
