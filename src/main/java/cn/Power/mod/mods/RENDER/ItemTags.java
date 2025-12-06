package cn.Power.mod.mods.RENDER;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.Font.FontManager;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.notification.Notification;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.Colors;
import cn.Power.util.GLUtil;
import cn.Power.util.Helper;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.SkyBlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.potion.PotionEffect;

public class ItemTags extends Mod {
	
	public static String[] LIST = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f",
			"m", "o", "r", "g" };
	
	public Value<Boolean> heads = new Value("ItemTags_OnlyHeads", false);
	public Value<Boolean> displayname = new Value("ItemTags_hasDisplayName", true);
	
	public ItemTags() {
		super("ItemTags", Category.RENDER);
	}
	
	@Override
	public void onEnable() {

	}
	
	@EventTarget
	public void on3D(EventRender e) {
		Iterator var3 = Minecraft.getMinecraft().theWorld.loadedEntityList.iterator();

		while (var3.hasNext()) {
			Object o = var3.next();
			Entity entity = (Entity) o;
			if (entity instanceof EntityItem) {
				if(((EntityItem) entity).getEntityItem() != null && !((EntityItem) entity).isInvisible())
					if(!heads.getValueState() || (((EntityItem) entity).getEntityItem().hasDisplayName() && ((EntityItem) entity).getEntityItem().getDisplayName().toLowerCase().contains("head")))
					 if(((EntityItem) entity).getEntityItem().hasDisplayName() || !displayname.getValueState())	
						drawNameTag((EntityItem) entity);
			}
		}
	}
	
	private void drawNameTag(EntityItem ei) {
		double x = ei.posX - mc.getRenderManager().renderPosX;
		double y = ei.posY - mc.getRenderManager().renderPosY;
		double z = ei.posZ - mc.getRenderManager().renderPosZ;
		double dist = mc.thePlayer.getDistance(ei.posX, ei.posY, ei.posZ);
		
		final String text = ei.getEntityItem().getDisplayName();
		double far = this.mc.gameSettings.renderDistanceChunks * 1.8D;
		double dl = Math.sqrt(x * x + z * z + y * y);
		double d;
		if (dl > far) {
			d = far / dl;
			dist *= d;
			x *= d;
			y *= d;
			z *= d;
		}
//		float var13 = (float) ((float) dist / 5 <= 2 ? 2.0F : (float) dist * scale.getValueState().floatValue());
//		float var14 = 0.016666668F * var13;
		GlStateManager.pushMatrix();
	//	RenderUtils.R3DUtils.startDrawing();
		GlStateManager.translate(x, y + 1.05F + ei.getAge() / 10000 + ei.ticksExisted/ 6000, z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(mc.gameSettings.thirdPersonView == 2 ? -mc.getRenderManager().playerViewX : mc.getRenderManager().playerViewX,
				1.0F, 0.0F, 0.0F);
		GL11.glScalef(-(float)0.235, -(float)0.235, (float)0.235);
		GLUtil.setGLCap(2896, false);
		GLUtil.setGLCap(2929, false);

		GLUtil.setGLCap(3042, true);
		GL11.glBlendFunc(770, 771);
		
		GlStateManager.scale(-0.075, -0.075, -0.75);
		
		GL11.glRotatef(-180f, 0.0F, 0.0F, 1.0F);
//		if (mc.gameSettings.thirdPersonView == 2) {
//			GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
//			GlStateManager.rotate(mc.getRenderManager().playerViewX, -1.0F, 0.0F, 0.0F);
//		} else {
//			GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
//			GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
//		}
//	
		int var18 = (int) (mc.fontRendererObj.getStringWidth(text) / 2);
		
		mc.fontRendererObj.drawStringWithShadow(text, -var18, 0, 0xFFFFFFFF);
		
		RenderHelper.enableGUIStandardItemLighting();
		
		ItemStack stack = ei.getEntityItem();

		GlStateManager.translate(x, y - 0.5, z);
		
		x = 0;
		
		
		
		if (stack != null) {
			
			RenderHelper.disableStandardItemLighting();
			
			int y1 = 76;
			int sLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId,
					stack);
			int fLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId,
					stack);
			int kLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId,
					stack);
			if (sLevel > 0) {
				drawEnchantTag("Sharpness " + getColor(sLevel) + sLevel, (int) x, y1);
				y1 -= 9;
			}
			if (fLevel > 0) {
				drawEnchantTag("FireAspect " + getColor(fLevel) + fLevel, (int) x, y1);
				y1 -= 9;
			}
			if (kLevel > 0) {
				drawEnchantTag("Knockback " + getColor(kLevel) + kLevel, (int) x, y1);
			} else if ((stack.getItem() instanceof ItemArmor)) {
				int pLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId,
						stack);
				int tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId,
						stack);
				int uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId,
						stack);
				if (pLevel > 0) {
					drawEnchantTag("Protection " + getColor(pLevel) + pLevel, (int) x, y1);
					y1 -= 9;
				}
				if (tLevel > 0) {
					drawEnchantTag("Thorns " + getColor(tLevel) + tLevel, (int) x, y1);
					y1 -= 9;
				}
				if (uLevel > 0) {
					drawEnchantTag("Unbreaking " + getColor(uLevel) + uLevel, (int) x, y1);
				}
			} else if ((stack.getItem() instanceof ItemBow)) {
				int powLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId,
						stack);
				int punLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId,
						stack);
				int fireLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId,
						stack);
				if (powLevel > 0) {
					drawEnchantTag("Power " + getColor(powLevel) + powLevel, (int) x, y1);
					y1 -= 9;
				}
				if (punLevel > 0) {
					drawEnchantTag("Punch " + getColor(punLevel) + punLevel, (int) x, y1);
					y1 -= 9;
				}
				if (fireLevel > 0) {
					drawEnchantTag("FireAspect " + getColor(fireLevel) + fireLevel, (int) x, y1);
				}
			} else if (stack.getRarity() == EnumRarity.EPIC) {
				drawEnchantTag("\2476\247lGod ", (int) x, y1);
			}
			int var7 = (int) Math.round(255.0D
					- (double) stack.getItemDamage() * 255.0D / (double) stack.getMaxDamage());
			int var10 = 255 - var7 << 16 | var7 << 8;
			Color customColor = new Color(var10).brighter();

			float x2 = (float) (x * 1.75D);
			/*
			 * if ((stack.getMaxDamage() - stack.getItemDamage()) > 0) {
			 * GlStateManager.pushMatrix(); GlStateManager.disableDepth();
			 * GL11.glScalef(0.5F, 0.5F, 0.5F); font.drawStringWithShadow("" +
			 * (stack.getMaxDamage() - stack.getItemDamage()), x2, -54,
			 * customColor.getRGB()); GlStateManager.enableDepth();
			 * GlStateManager.popMatrix(); }
			 */
			y1 = -20 - 33;
			x += 12;
		}

	//	RenderUtils.R3DUtils.stopDrawing();
		
		GLUtil.revertAllCaps();
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}
	
	private String getColor(int level) {
		if (level == 1) {

		} else if (level == 2) {
			return "\247a";
		} else if (level == 3) {
			return "\2473";
		} else if (level == 4) {
			return "\2474";
		} else if (level >= 5) {
			return "\2476";
		}
		return "\247f";
	}
	
	private static void drawEnchantTag(String Enchant, int x, int y) {
		String Enchants = Enchant;
		for (String str : LIST) {
			Enchant = Enchant.replaceAll("§" + str, "");
		}
		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		x = (int) (x * 2);
		y -= 48;
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x + 1, y * 2, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x - 1, y * 2, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x, y * 2 + 1, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x, y * 2 - 1, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchants, x, y * 2, Colors.getColor(255));
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}
	
}
