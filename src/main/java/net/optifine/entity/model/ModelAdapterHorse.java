package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityHorse;
import net.optifine.reflect.Reflector;

public class ModelAdapterHorse extends ModelAdapter {
	private static Map<String, Integer> mapPartFields = null;

	public ModelAdapterHorse() {
		super(EntityHorse.class, "horse", 0.75F);
	}

	protected ModelAdapterHorse(Class entityClass, String name, float shadowSize) {
		super(entityClass, name, shadowSize);
	}

	public ModelBase makeModel() {
		return new ModelHorse();
	}

	public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
		if (!(model instanceof ModelHorse)) {
			return null;
		} else {
			ModelHorse modelhorse = (ModelHorse) model;
			Map<String, Integer> map = getMapPartFields();

			if (map.containsKey(modelPart)) {
				int i = map.get(modelPart);

				switch (i) {
				case 0:
					return (ModelRenderer) modelhorse.head;
				case 1:
					return (ModelRenderer) modelhorse.field_178711_b;
				case 2:
					return (ModelRenderer) modelhorse.field_178712_c;
				case 3:
					return (ModelRenderer) modelhorse.horseLeftEar;

				case 4:
					return (ModelRenderer) modelhorse.horseRightEar;

				case 5:
					return (ModelRenderer) modelhorse.muleLeftEar;

				case 6:
					return (ModelRenderer) modelhorse.muleRightEar;

				case 7:
					return (ModelRenderer) modelhorse.neck;

				case 8:
					return (ModelRenderer) modelhorse.horseFaceRopes;

				case 9:
					return (ModelRenderer) modelhorse.mane;

				case 10:
					return (ModelRenderer) modelhorse.body;

				case 11:
					return (ModelRenderer) modelhorse.tailBase;

				case 12:
					return (ModelRenderer) modelhorse.tailMiddle;

				case 13:
					return (ModelRenderer) modelhorse.tailTip;

				case 14:
					return (ModelRenderer) modelhorse.backLeftLeg;

				case 15:
					return (ModelRenderer) modelhorse.backLeftShin;

				case 16:
					return (ModelRenderer) modelhorse.backLeftHoof;

				case 17:
					return (ModelRenderer) modelhorse.backRightLeg;

				case 18:
					return (ModelRenderer) modelhorse.backRightShin;

				case 19:
					return (ModelRenderer) modelhorse.backRightHoof;

				case 20:
					return (ModelRenderer) modelhorse.frontLeftLeg;
				case 21:
					return (ModelRenderer) modelhorse.frontLeftShin;
				case 22:
					return (ModelRenderer) modelhorse.frontLeftHoof;
				case 23:
					return (ModelRenderer) modelhorse.frontRightLeg;
				case 24:
					return (ModelRenderer) modelhorse.frontRightShin;
				case 25:
					return (ModelRenderer) modelhorse.frontRightHoof;

				case 26:
					return (ModelRenderer) modelhorse.muleLeftChest;

				case 27:
					return (ModelRenderer) modelhorse.muleRightChest;

				case 28:
					return (ModelRenderer) modelhorse.horseSaddleBottom;

				case 29:
					return (ModelRenderer) modelhorse.horseSaddleFront;

				case 30:
					return (ModelRenderer) modelhorse.horseSaddleBack;

				case 31:
					return (ModelRenderer) modelhorse.horseLeftSaddleRope;

				case 32:
					return (ModelRenderer) modelhorse.horseLeftSaddleMetal;

				case 33:
					return (ModelRenderer) modelhorse.horseRightSaddleRope;

				case 34:
					return (ModelRenderer) modelhorse.horseRightSaddleMetal;

				case 35:
					return (ModelRenderer) modelhorse.horseLeftFaceMetal;

				case 36:
					return (ModelRenderer) modelhorse.horseRightFaceMetal;

				case 37:
					return (ModelRenderer) modelhorse.horseLeftRein;

				case 38:
					return (ModelRenderer) modelhorse.horseRightRein;

				}

			} else {
				return null;
			}
		}
		
		return null;
	}

	public String[] getModelRendererNames() {
		return new String[] { "head", "upper_mouth", "lower_mouth", "horse_left_ear", "horse_right_ear",
				"mule_left_ear", "mule_right_ear", "neck", "horse_face_ropes", "mane", "body", "tail_base",
				"tail_middle", "tail_tip", "back_left_leg", "back_left_shin", "back_left_hoof", "back_right_leg",
				"back_right_shin", "back_right_hoof", "front_left_leg", "front_left_shin", "front_left_hoof",
				"front_right_leg", "front_right_shin", "front_right_hoof", "mule_left_chest", "mule_right_chest",
				"horse_saddle_bottom", "horse_saddle_front", "horse_saddle_back", "horse_left_saddle_rope",
				"horse_left_saddle_metal", "horse_right_saddle_rope", "horse_right_saddle_metal",
				"horse_left_face_metal", "horse_right_face_metal", "horse_left_rein", "horse_right_rein" };
	}

	private static Map<String, Integer> getMapPartFields() {
		if (mapPartFields != null) {
			return mapPartFields;
		} else {
			mapPartFields = new HashMap<>();
			mapPartFields.put("head", 0);
			mapPartFields.put("upper_mouth", 1);
			mapPartFields.put("lower_mouth", 2);
			mapPartFields.put("horse_left_ear", 3);
			mapPartFields.put("horse_right_ear", 4);
			mapPartFields.put("mule_left_ear", 5);
			mapPartFields.put("mule_right_ear", 6);
			mapPartFields.put("neck", 7);
			mapPartFields.put("horse_face_ropes", 8);
			mapPartFields.put("mane", 9);
			mapPartFields.put("body", 10);
			mapPartFields.put("tail_base", 11);
			mapPartFields.put("tail_middle", 12);
			mapPartFields.put("tail_tip", 13);
			mapPartFields.put("back_left_leg", 14);
			mapPartFields.put("back_left_shin", 15);
			mapPartFields.put("back_left_hoof", 16);
			mapPartFields.put("back_right_leg", 17);
			mapPartFields.put("back_right_shin", 18);
			mapPartFields.put("back_right_hoof", 19);
			mapPartFields.put("front_left_leg", 20);
			mapPartFields.put("front_left_shin", 21);
			mapPartFields.put("front_left_hoof", 22);
			mapPartFields.put("front_right_leg", 23);
			mapPartFields.put("front_right_shin", 24);
			mapPartFields.put("front_right_hoof", 25);
			mapPartFields.put("mule_left_chest", 26);
			mapPartFields.put("mule_right_chest", 27);
			mapPartFields.put("horse_saddle_bottom", 28);
			mapPartFields.put("horse_saddle_front", 29);
			mapPartFields.put("horse_saddle_back", 30);
			mapPartFields.put("horse_left_saddle_rope", 31);
			mapPartFields.put("horse_left_saddle_metal", 32);
			mapPartFields.put("horse_right_saddle_rope", 33);
			mapPartFields.put("horse_right_saddle_metal", 34);
			mapPartFields.put("horse_left_face_metal", 35);
			mapPartFields.put("horse_right_face_metal", 36);
			mapPartFields.put("horse_left_rein", 37);
			mapPartFields.put("horse_right_rein", 38);
			return mapPartFields;
		}
	}

	public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		RenderHorse renderhorse = new RenderHorse(rendermanager, (ModelHorse) modelBase, shadowSize);
		return renderhorse;
	}
}
