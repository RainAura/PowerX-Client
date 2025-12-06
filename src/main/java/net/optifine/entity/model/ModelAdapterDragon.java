package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.boss.EntityDragon;
import net.optifine.reflect.Reflector;

public class ModelAdapterDragon extends ModelAdapter {
	public ModelAdapterDragon() {
		super(EntityDragon.class, "dragon", 0.5F);
	}

	public ModelBase makeModel() {
		return new ModelDragon(0.0F);
	}

	public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
		if (!(model instanceof ModelDragon)) {
			return null;
		} else {
			ModelDragon modeldragon = (ModelDragon) model;

			if (modelPart.equals("head")) {
				return modeldragon.head;
			} else if (modelPart.equals("spine")) {
				return modeldragon.spine;
			} else if (modelPart.equals("jaw")) {
				return modeldragon.jaw;
			} else if (modelPart.equals("body")) {
				return modeldragon.body;
			} else if (modelPart.equals("rear_leg")) {
				return modeldragon.rearLeg;
			} else if (modelPart.equals("front_leg")) {
				return modeldragon.frontLeg;
			} else if (modelPart.equals("rear_leg_tip")) {
				return modeldragon.rearLegTip;
			} else if (modelPart.equals("front_leg_tip")) {
				return modeldragon.frontLegTip;
			} else if (modelPart.equals("rear_foot")) {
				return modeldragon.rearFoot;
			} else if (modelPart.equals("front_foot")) {
				return modeldragon.frontFoot;
			} else if (modelPart.equals("wing")) {
				return modeldragon.wing;
			} else {
				return modelPart.equals("wing_tip")
						?  modeldragon.wingTip
						: null;
			}
		}
	}

	public String[] getModelRendererNames() {
		return new String[] { "head", "spine", "jaw", "body", "rear_leg", "front_leg", "rear_leg_tip", "front_leg_tip",
				"rear_foot", "front_foot", "wing", "wing_tip" };
	}

	public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		RenderDragon renderdragon = new RenderDragon(rendermanager);
		renderdragon.mainModel = modelBase;
		renderdragon.shadowSize = shadowSize;
		return renderdragon;
	}
}
