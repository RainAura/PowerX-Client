package cn.Power.util;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cn.Power.Client;
import cn.Power.mod.mods.COMBAT.KillAura;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TargetHUDOld {
	public static Minecraft mc = Minecraft.getMinecraft();
	public static float AnimotaiSpeed;
	public static EntityLivingBase TargetHUD = null;

	public static void onScreenDraw(ScaledResolution res) {
		
		
		Client.TargetHudOffsetX.valueMin = (double) -res.getScaledWidth();
		Client.TargetHudOffsetX.valueMax = (double) res.getScaledWidth();
		
		Client.TargetHudOffsetY.valueMin = (double) -res.getScaledHeight();
		Client.TargetHudOffsetY.valueMax = (double) res.getScaledHeight();

		int x = res.getScaledWidth() / 2 + 10 + Client.TargetHudOffsetX.getValueState().intValue();
		int y = res.getScaledHeight() / 2 + 11 + Client.TargetHudOffsetY.getValueState().intValue();
		final EntityLivingBase player = KillAura.Target;
		if (player != null) {

			if (player instanceof EntityWither && KillAura.AutoBlockEntity != null)
				drawNewTargetHUD(res, x, y + Client.TargetHudOffsetY2.getValueState().intValue(), KillAura.AutoBlockEntity);

			drawNewTargetHUD(res, x, y, player);
		}
	}
	

	public static void drawNewTargetHUD(ScaledResolution res, int x, int y, EntityLivingBase player) {
		GlStateManager.pushMatrix();
		RenderUtil.rectangleBordered(x - 2, y - 2, x + 2 + 125.0, y + 2 + (float) 36.0, 0.5, Colors2.getColor(90),
				Colors2.getColor(0));
		RenderUtil.rectangleBordered(x - 1, y - 1, x + 1 + (float) 125, y + 1 + (float) 36, 1.0, Colors2.getColor(90),
				Colors2.getColor(61));
		RenderUtils.drawRect(x, y, x + 125.0, y + 36.0, Colors.getColor(0, 150));

		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(player.getName(), x + 32.0f, y + 1.0f, -1);

		BigDecimal bigDecimal = new BigDecimal((double) player.getHealth());
		bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
		double HEALTH = bigDecimal.doubleValue();

		BigDecimal DT = new BigDecimal((double) Minecraft.getMinecraft().thePlayer.getDistanceToEntity(player));
		DT = DT.setScale(1, RoundingMode.HALF_UP);
		double Dis = DT.doubleValue();

		final float health = player.getHealth();
		final float[] fractions = { 0.0f, 0.5f, 1.0f };
		final Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
		final float progress = health / player.getMaxHealth();
		final Color customColor = (health >= 0.0f) ? blendColors(fractions, colors, progress).brighter() : Color.RED;
		// double width =
		// (double)Minecraft.getMinecraft().fontRendererObj.getStringWidth(player.getName());
		double width = 125.0D - 37;

		final double healthLocation = width * progress;

		if (player != TargetHUD) {
			TargetHUD = player;
			AnimotaiSpeed = (float) healthLocation;
		}

		AnimotaiSpeed = (float) RenderUtil.getAnimationState(AnimotaiSpeed, healthLocation, 120);

		RenderUtils.drawRect(x + 32, y + 27.5, (x + 32 + AnimotaiSpeed), y + 35.5, customColor.getRGB());

		RenderUtils.rectangleBordered(x + 32, y + 27.0, x + 32 + width, y + 36.0, 0.5, Colors.getColor(0, 0),
				Colors.getColor(0));
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
		renderStuffStatus(x, y);
		final String str = "\247c❤" + COLOR1 + HEALTH;
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(str, x + 1, y + 27.5f, -1);
		GlStateManager.scale(0.5f, 0.5f, 0.5f);
		final String str2 = String.format("Yaw: %s Pitch: %s BodyYaw: %s", (int) player.rotationYaw,
				(int) player.rotationPitch, (int) player.renderYawOffset);
		// Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(str2,
		// x*2+76.0f, y*2+47.0f, -1);
		final String str3 = String.format("G: %s HURT: %s TE: %s", player.onGround, player.hurtTime,
				player.ticksExisted);
		// Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(str3,
		// x*2+76.0f, y*2+59.0f, -1);

		GlStateManager.scale(2.0f, 2.0f, 2.0f);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		if (player instanceof EntityPlayer) {
			int size = 26;
			ResourceLocation skin = ((AbstractClientPlayer) player).getLocationSkin();
			Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
			Gui.drawScaledCustomSizeModalRect(x + 2, y + 1, 8.0f, 8.0f, 8, 8, size, size, 64.0f, 64.0f);
			if (((EntityPlayer) player).isWearing(EnumPlayerModelParts.HAT)) {
				Gui.drawScaledCustomSizeModalRect(x + 2, y + 1, 40.0f, 8.0f, 8, 8, size, size, 64.0f, 64.0f);
			}
		}
		GlStateManager.popMatrix();
	}

	public static int[] getFractionIndicies(final float[] fractions, final float progress) {
		final int[] range = new int[2];
		int startPoint;
		for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
		}
		if (startPoint >= fractions.length) {
			startPoint = fractions.length - 1;
		}
		range[0] = startPoint - 1;
		range[1] = startPoint;
		return range;
	}

	public static Color blendColors(final float[] fractions, final Color[] colors, final float progress) {
		Color color = null;
		if (fractions == null) {
			throw new IllegalArgumentException("Fractions can't be null");
		}
		if (colors == null) {
			throw new IllegalArgumentException("Colours can't be null");
		}
		if (fractions.length == colors.length) {
			final int[] indicies = getFractionIndicies(fractions, progress);
			final float[] range = { fractions[indicies[0]], fractions[indicies[1]] };
			final Color[] colorRange = { colors[indicies[0]], colors[indicies[1]] };
			final float max = range[1] - range[0];
			final float value = progress - range[0];
			final float weight = value / max;
			color = blend(colorRange[0], colorRange[1], 1.0f - weight);
			return color;
		}
		throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
	}

	public static Color blend(final Color color1, final Color color2, final double ratio) {
		final float r = (float) ratio;
		final float ir = 1.0f - r;
		final float[] rgb1 = new float[3];
		final float[] rgb2 = new float[3];
		color1.getColorComponents(rgb1);
		color2.getColorComponents(rgb2);
		float red = rgb1[0] * r + rgb2[0] * ir;
		float green = rgb1[1] * r + rgb2[1] * ir;
		float blue = rgb1[2] * r + rgb2[2] * ir;
		if (red < 0.0f) {
			red = 0.0f;
		} else if (red > 255.0f) {
			red = 255.0f;
		}
		if (green < 0.0f) {
			green = 0.0f;
		} else if (green > 255.0f) {
			green = 255.0f;
		}
		if (blue < 0.0f) {
			blue = 0.0f;
		} else if (blue > 255.0f) {
			blue = 255.0f;
		}
		Color color3 = null;
		try {
			color3 = new Color(red, green, blue);
		} catch (IllegalArgumentException exp) {
			final NumberFormat nf = NumberFormat.getNumberInstance();
			System.out.println(
					nf.format((double) red) + "; " + nf.format((double) green) + "; " + nf.format((double) blue));
			exp.printStackTrace();
		}
		return color3;
	}

	public static void renderStuffStatus(int x, int y) {
		int xm = x;
		int ym = y;
		for (int i = 0; i < 5; i++) {
			if (mc.theWorld != null) {
				xm += 18;
			}
			GlStateManager.pushMatrix();
			RenderUtils.rectangleBordered(xm + 14, ym + 10, xm + 30.0, ym + 26.0, 0.5, Colors.getColor(90),
					Colors.getColor(0));
			GlStateManager.popMatrix();
		}
		final EntityLivingBase player = KillAura.Target;
		if (!(player instanceof EntityPlayer))
			return;
		GL11.glPushMatrix();
		ArrayList<ItemStack> stuff = new ArrayList<ItemStack>();
		boolean onwater = player.isEntityAlive() && player.isInsideOfMaterial(Material.water);

		for (int index = 3; index >= 0; --index) {
			ItemStack armer = ((EntityPlayer) player).inventory.armorInventory[index];
			if (armer == null)
				continue;
			stuff.add(armer);
		}
		if (((EntityPlayer) player).getCurrentEquippedItem() != null) {
			stuff.add(((EntityPlayer) player).getCurrentEquippedItem());
		}

		for (ItemStack errything : stuff) {
			if (mc.theWorld != null) {
				RenderHelper.enableGUIStandardItemLighting();
				x += 18;
			}
			GlStateManager.pushMatrix();
			GlStateManager.disableAlpha();
			GlStateManager.clear(256);
			mc.getRenderItem().zLevel = -150.0f;
			mc.getRenderItem().renderItemAndEffectIntoGUI(errything, x + 14, y + 10);
			mc.getRenderItem().zLevel = 0.0f;
			GlStateManager.disableBlend();
			GlStateManager.scale(0.5, 0.5, 0.5);
			GlStateManager.disableDepth();
			GlStateManager.disableLighting();
			GlStateManager.enableDepth();
			GlStateManager.scale(2.0f, 2.0f, 2.0f);
			GlStateManager.enableAlpha();
			GlStateManager.popMatrix();
			errything.getEnchantmentTagList();
		}

		GL11.glPopMatrix();

	}

}
