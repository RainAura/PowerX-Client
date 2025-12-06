package cn.Power.mod.mods.RENDER;

import java.util.Map;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventTick;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.MotionBlurResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

public class MotionBlur extends Mod {
	private Map domainResourceManagers;

	public MotionBlur() {
		super("MotionBlur", Category.RENDER);
		if (this.domainResourceManagers == null) {
			this.domainResourceManagers = ((SimpleReloadableResourceManager) mc.mcResourceManager)
					.getDomainResourceManagers();
		}

		if (!this.domainResourceManagers.containsKey("motionblur")) {
			this.domainResourceManagers.put("motionblur", new MotionBlurResourceManager());
		}
	}

	public void onEnable() {
		super.onEnable();
		if (mc.entityRenderer.getShaderGroup() != null) {
			mc.entityRenderer.getShaderGroup().deleteShaderGroup();
		}

		mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
	}

	public void onDisable() {
		super.onDisable();
		if (mc.entityRenderer.getShaderGroup() != null) {
			mc.entityRenderer.getShaderGroup().deleteShaderGroup();
		}

	}

	@EventTarget
	public void onTIck(EventTick e) {
		if (mc.thePlayer != null && mc.theWorld != null) {
			if (mc.entityRenderer.getShaderGroup() == null) {
				mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
			}

		}
	}

}
