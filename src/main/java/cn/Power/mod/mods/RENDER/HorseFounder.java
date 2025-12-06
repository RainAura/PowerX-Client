package cn.Power.mod.mods.RENDER;

import java.awt.Color;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Font.FontManager;
import cn.Power.events.EventRender;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.misc.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;

public class HorseFounder extends Mod{

	public HorseFounder() {
		super("HorseFounder", Category.RENDER);
	}
	
	@EventTarget
	public void onRender(EventRender e) {
		Minecraft.thePlayer.horseJumpPower = 1;
		Minecraft.thePlayer.horseJumpPowerCounter = 9;

		
		Iterator<?> var11 = Minecraft.theWorld.loadedEntityList.iterator();
		
		while (var11.hasNext()) {
			Entity entity = (Entity) var11.next();
			
			if (entity instanceof EntityHorse && mc.thePlayer.ridingEntity != entity) {

				mc.getRenderManager();
				double x = entity.posX - mc.getRenderManager().renderPosX;
				mc.getRenderManager();
				double y = entity.posY - mc.getRenderManager().renderPosY;
				mc.getRenderManager();
				double z = entity.posZ - mc.getRenderManager().renderPosZ;
				
				double dist = Minecraft.thePlayer.getDistance(entity.posX, entity.posY, entity.posZ);
				final String text = entity.getName();
				double far = mc.gameSettings.renderDistanceChunks * 12.8D;
				double dl = Math.sqrt(x * x + z * z + y * y);
				double d;
				if (dl > far) {
					d = far / dl;
					dist *= d;
					x *= d;
					y *= d;
					z *= d;
				}
//				float var13 = (float) ((float) dist / 5 <= 2 ? 2.0F : (float) dist * scale.getValueState().floatValue());
//				float var14 = 0.016666668F * var13;
				GlStateManager.pushMatrix();
				RenderUtils.R3DUtils.startDrawing();
				GlStateManager.translate(x, y + 2.5F, z);
				GL11.glNormal3f(0.0F, 1.0F, 0.0F);
				if (mc.gameSettings.thirdPersonView == 2) {
					mc.getRenderManager();
					GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
					mc.getRenderManager();
					GlStateManager.rotate(mc.getRenderManager().playerViewX, -1.0F, 0.0F, 0.0F);
				} else {
					mc.getRenderManager();
					GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
					mc.getRenderManager();
					GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
				}
				float size = Minecraft.thePlayer.getDistanceToEntity(entity) / 6.0f;
				if (size < 1.1f) {
					size = 1.1f;
				}
				float scale = (float) (size * 0.05);
				GL11.glScalef(-scale, -scale, scale);
				
				int var18 = (int) (FontManager.sw15.getStringWidth(text) / 2);
			
				RenderUtil.drawRect(-var18-1, 0, var18+2, 9, new Color(0,0,0,111).getRGB());
				FontManager.sw15.drawString(text, -var18, 0, new Color(255,0,0).getRGB());

				RenderUtils.R3DUtils.stopDrawing();
				GlStateManager.popMatrix();

			}
		}
		

	}
	
	
	public void PreformActions() {
		ChatUtil.printChat("一只马生成了");
	}

}
