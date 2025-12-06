package cn.Power.util;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.WeakHashMap;

import org.lwjgl.opengl.GL11;

import cn.Power.Client;
import cn.Power.Font.FontManager;
import cn.Power.mod.mods.COMBAT.KillAura;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TargetHUD {
	public static Minecraft mc = Minecraft.getMinecraft();
	public static float AnimotaiSpeed;
	public static EntityLivingBase TargetHUD = null;
	private static  String DmgHealth = "";
	private static WeakHashMap<EntityLivingBase, Float> healthMap = new WeakHashMap<EntityLivingBase, Float>();
	private static float dmgtimer;
	
	public static void onScreenDraw(ScaledResolution res , EntityLivingBase player) {
		
		Client.TargetHudOffsetX.valueMin = (double) -res.getScaledWidth();
		Client.TargetHudOffsetX.valueMax = (double) res.getScaledWidth();
		
		Client.TargetHudOffsetY.valueMin = (double) -res.getScaledHeight();
		Client.TargetHudOffsetY.valueMax = (double) res.getScaledHeight();
		
		boolean Player = false;
		if (player != null) {
			int x = res.getScaledWidth() / 2 - 20 + Client.TargetHudOffsetX.getValueState().intValue();
			int y = res.getScaledHeight() / 2 + 20 + Client.TargetHudOffsetY.getValueState().intValue();
			if (player instanceof EntityPlayer ) {
				Player = true;
				x = res.getScaledWidth() / 2 + 10 + Client.TargetHudOffsetX.getValueState().intValue();
			}
			
			
			if(player instanceof EntityWither && KillAura.AutoBlockEntity != null) {
				onScreenDraw(res, KillAura.AutoBlockEntity);
				y += Client.TargetHudOffsetY2.getValueState().intValue();
			}

			GlStateManager.pushMatrix();
			String Name = player.getDisplayName().getFormattedText();
			float namewidth = FontManager.sw18.getStringWidth(Name);
			boolean widthcheck = namewidth > 65;
			RenderUtil.rectangleBordered(x - 1 + (Player ? 0 : +27), y - 1, x + (widthcheck ? 36 + namewidth : 101),y + 1 + (float) 28.0, 0.5, Colors2.getColor(0, 150), Colors2.getColor(153, 153, 153, 100));
//	            RenderUtil.rectangleBordered(x-1 , y-1 , x+1 + (float)100 , y+1 + (float)28 , 1.0, Colors2.getColor(90), Colors2.getColor(61));
//	            RenderUtils.drawRect(x, y, x+100.0, y+28.0, Colors.getColor(90, 0));
			if (widthcheck) {
				FontManager.sw18.drawString(Name, x + 31, y + 2, -1);
			} else {
				FontManager.sw18.drawCenteredString(Name, x + 63, y + 2, -1);
			}

			BigDecimal bigDecimal = new BigDecimal((double) player.getHealth());
			bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
			double HEALTH = bigDecimal.doubleValue();

			BigDecimal DT = new BigDecimal((double) Minecraft.getMinecraft().thePlayer.getDistanceToEntity(player));
			DT = DT.setScale(1, RoundingMode.HALF_UP);
			double Dis = DT.doubleValue();
			
			final float Ghealth = player.getAbsorptionAmount();
			final float health = player.getHealth();
			final boolean Absor = player.getAbsorptionAmount() > 0;
			final float pr = player.getTotalArmorValue();
			final float[] fractions = { 0.0f, 0.5f, 1.0f };
			final Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
			final float progress = health / player.getMaxHealth();
			final float Armorprogress = pr / 20;
			// double width =
			// (double)Minecraft.getMinecraft().fontRendererObj.getStringWidth(player.getName());
			double width = widthcheck ? namewidth - 9 : 56;

			final double Armor = width * Armorprogress;

			final double healthLocation = width * progress;

			String COLOR1;
			if (health > 20.0D) {
				COLOR1 = "\2479";
			} else if (health >= 10.0D) {
				COLOR1 = "\247a";
			} else if (health >= 3.0D) {
				COLOR1 = "\247e";
			} else {
				COLOR1 = "\2474";
			}

			if (player != TargetHUD) {
				TargetHUD = player;
				AnimotaiSpeed = (float) healthLocation;
				dmgtimer = 0;
			}

			AnimotaiSpeed = (float) RenderUtil.getAnimationState(AnimotaiSpeed, healthLocation, 100);
			
			if (!healthMap.containsKey(player)) {
				healthMap.put((EntityLivingBase) player, ((EntityLivingBase) player).getHealth());
			}
			float floatValue = healthMap.get(player);
			float healths = ((EntityLivingBase) player).getHealth();
			if (floatValue != healths) {
				if (floatValue - healths < 0.0f) {
					DmgHealth = "\247a+ " + roundToPlace((floatValue - healths) * -1.0f, 1);
				} else {
					DmgHealth = "\247c- " + roundToPlace(floatValue - healths, 1);
				}

				healthMap.remove(player);
				healthMap.put((EntityLivingBase) player, ((EntityLivingBase) player).getHealth());
				dmgtimer = 50;
			}
			if(dmgtimer > 0 ) {
				dmgtimer = (float) RenderUtil.getAnimationState(dmgtimer, 0, 100);
				FontManager.baloo18.drawCenteredStringWithShadow(DmgHealth, x + 12, y -17 + dmgtimer /10 ,-1);
			}
			
			String healthText;

			if (health % 1.0f != 0.0f) {
				healthText = new BigDecimal(health+Ghealth).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() + "";
			} else {
				healthText = new BigDecimal(health+Ghealth).setScale(0, 4).toString();
			}

//	            FontManager.icon15.drawString("o", x+30, y+19,  Colors.getColor(255,153,153));
//	            FontManager.roboto15.drawString(COLOR1+health+"", x+40, y+19,  Colors.getColor(153,153,153));
//	            

//	            

			FontManager.icon15.drawString("o", x + 30, y + 12, Absor ? Colors.getColor(219, 190, 1):Colors.getColor(255, 85, 85));
			RenderUtils.rectangleBordered(x + 39.5, y + 15.0, x + 40.5 + width, y + 18.0, 0.5,
					Colors.getColor(153, 153, 153, 150), Colors.getColor(0, 80));
			RenderUtils.drawRect(x + 40, y + 15.5, (x + 40 + AnimotaiSpeed), y + 17.5, Colors.getColor(255, 150, 0));
			RenderUtils.drawRect(x + 40, y + 15.5, (x + 40 + healthLocation), y + 17.5, Absor ? Colors.getColor(219, 190, 1):Colors.getColor(0, 150, 71));

			RenderUtils.rectangleBordered(x + 39.5, y + 22.0, x + 12.5 + width, y + 25.0, 0.5,
					Colors.getColor(153, 153, 153, 150), Colors.getColor(0, 80));
			FontManager.icon15.drawString("p", x + 30, y + 19, Colors.getColor(153, 153, 153));
			RenderUtils.drawRect(x + 40, y + 22.5, x + (40 + Armor / 2), y + 24.5, Colors.getColor(0, 180, 255));

//	            
			FontManager.icon15.drawCenteredString("o", x + (widthcheck ? namewidth + 12 : 76), y + 19 - 0.5f,
					Colors.getColor(0, 153, 255));
			FontManager.tiny.drawCenteredStringWithAlpha(healthText, x + (widthcheck ? namewidth + 24 : 88), y + 19, -1,
					0.5f);

//	            for (int i = 1; i < 10; ++i) {
//	                final double dThing = width / 10.0 * i;
//	                RenderUtils.drawRect(x+40.0 + dThing, y+15.0, x+40.0 + dThing + 0.5, y+18.0, Colors.getColor(0));
//	            }

//	            renderStuffStatus(x,y);
			if(player instanceof EntityPlayer) 
			drawFace(x + 1, y + 1, 8.0f, 8.0f, 8, 8, 26, 26, 64.0f, 64.0f, (AbstractClientPlayer) player);
			
	       
			GlStateManager.popMatrix();

		}
	}
	public static double roundToPlace(double p_roundToPlace_0_, int p_roundToPlace_2_) {
		if (p_roundToPlace_2_ < 0) {
			throw new IllegalArgumentException();
		}
		return new BigDecimal(p_roundToPlace_0_).setScale(p_roundToPlace_2_, RoundingMode.HALF_UP).doubleValue();
	}
	
    private static void drawFace(double x, double y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight, AbstractClientPlayer target) {
        try {
        	ScaledResolution sr = new ScaledResolution(mc);
            ResourceLocation skin = target.getLocationSkin();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(skin);
        	RenderUtil.rectangleBordered(x-1, y-1, x + 27, y + 27, 0.5, Colors2.getColor(0, 0),Colors2.getColor(153, 153, 153, 100));
            Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
            if (target.isWearing(EnumPlayerModelParts.HAT)) {
				Gui.drawScaledCustomSizeModalRect(x, y, u+32, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
			}
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception ignored) {
        }
    }
    

	protected static void drawGradientRect(float left, float top, float right, float bottom, int startColor,
			int endColor) {
		float f = (startColor >> 24 & 0xFF) / 255.0f;
		float f2 = (startColor >> 16 & 0xFF) / 255.0f;
		float f3 = (startColor >> 8 & 0xFF) / 255.0f;
		float f4 = (startColor & 0xFF) / 255.0f;
		float f5 = (endColor >> 24 & 0xFF) / 255.0f;
		float f6 = (endColor >> 16 & 0xFF) / 255.0f;
		float f7 = (endColor >> 8 & 0xFF) / 255.0f;
		float f8 = (endColor & 0xFF) / 255.0f;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(left, top, 0.0).color(f2, f3, f4, f).endVertex();
		worldrenderer.pos(left, bottom, 0.0).color(f2, f3, f4, f).endVertex();
		worldrenderer.pos(right, bottom, 0.0).color(f6, f7, f8, f5).endVertex();
		worldrenderer.pos(right, top, 0.0).color(f6, f7, f8, f5).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	protected static void drawGradientRects(float left, float top, float right, float bottom, int startColor,
			int endColor) {
		float f = (startColor >> 24 & 0xFF) / 255.0f;
		float f2 = (startColor >> 16 & 0xFF) / 255.0f;
		float f3 = (startColor >> 8 & 0xFF) / 255.0f;
		float f4 = (startColor & 0xFF) / 255.0f;
		float f5 = (endColor >> 24 & 0xFF) / 255.0f;
		float f6 = (endColor >> 16 & 0xFF) / 255.0f;
		float f7 = (endColor >> 8 & 0xFF) / 255.0f;
		float f8 = (endColor & 0xFF) / 255.0f;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(left, bottom, 0.0).tex(0.0, 1.0).color(f2, f3, f4, f).endVertex();
		worldrenderer.pos(right, bottom, 0.0).tex(1.0, 1.0).color(f2, f3, f4, f).endVertex();
		worldrenderer.pos(right, top, 0.0).tex(1.0, 0.0).color(f6, f7, f8, f5).endVertex();
		worldrenderer.pos(left, top, 0.0).tex(0.0, 0.0).color(f6, f7, f8, f5).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
}
