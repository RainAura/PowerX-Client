package net.minecraft.client.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Gui {
	public static final ResourceLocation optionsBackground = new ResourceLocation(
			"textures/gui/options_background.png");
	public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
	public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
	public float zLevel;

	/**
	 * Draw a 1 pixel wide horizontal line. Args: x1, x2, y, color
	 */
	protected void drawHorizontalLine(int startX, int endX, int y, int color) {
		if (endX < startX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		drawRect(startX, y, endX + 1, y + 1, color);
	}

	/**
	 * Draw a 1 pixel wide vertical line. Args : x, y1, y2, color
	 */
	protected void drawVerticalLine(int x, int startY, int endY, int color) {
		if (endY < startY) {
			int i = startY;
			startY = endY;
			endY = i;
		}

		drawRect(x, startY + 1, x + 1, endY, color);
	}

	/**
	 * Draws a solid color rectangle with the specified coordinates and color (ARGB
	 * format). Args: x1, y1, x2, y2, color
	 */
	public static void drawRect(int left, int top, int right, int bottom, int color) {
		if (left < right) {
			int i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			int j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos((double) left, (double) bottom, 0.0D).endVertex();
		worldrenderer.pos((double) right, (double) bottom, 0.0D).endVertex();
		worldrenderer.pos((double) right, (double) top, 0.0D).endVertex();
		worldrenderer.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static void drawRect(float left, float top, float right, float bottom, int color) {
		float f3;
		if (left < right) {
			f3 = left;
			left = right;
			right = f3;
		}

		if (top < bottom) {
			f3 = top;
			top = bottom;
			bottom = f3;
		}

		f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos((double) left, (double) bottom, 0.0D).endVertex();
		worldrenderer.pos((double) right, (double) bottom, 0.0D).endVertex();
		worldrenderer.pos((double) right, (double) top, 0.0D).endVertex();
		worldrenderer.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static void drawRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}
		if (top < bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}
		float f3 = (float) (color >> 24 & 255) / 255.0f;
		float f2 = (float) (color >> 16 & 255) / 255.0f;
		float f1 = (float) (color >> 8 & 255) / 255.0f;
		float f22 = (float) (color & 255) / 255.0f;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f2, f1, f22, f3);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos(left, bottom, 0.0).endVertex();
		worldrenderer.pos(right, bottom, 0.0).endVertex();
		worldrenderer.pos(right, top, 0.0).endVertex();
		worldrenderer.pos(left, top, 0.0).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	/**
	 * Draws a rectangle with a vertical gradient between the specified colors (ARGB
	 * format). Args : x1, y1, x2, y2, topColor, bottomColor
	 */
	protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double) right, (double) top, (double) this.zLevel).color(f1, f2, f3, f).endVertex();
		worldrenderer.pos((double) left, (double) top, (double) this.zLevel).color(f1, f2, f3, f).endVertex();
		worldrenderer.pos((double) left, (double) bottom, (double) this.zLevel).color(f5, f6, f7, f4).endVertex();
		worldrenderer.pos((double) right, (double) bottom, (double) this.zLevel).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	protected void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double) right, (double) top, (double) this.zLevel).color(f1, f2, f3, f).endVertex();
		worldrenderer.pos((double) left, (double) top, (double) this.zLevel).color(f1, f2, f3, f).endVertex();
		worldrenderer.pos((double) left, (double) bottom, (double) this.zLevel).color(f5, f6, f7, f4).endVertex();
		worldrenderer.pos((double) right, (double) bottom, (double) this.zLevel).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	
	/**
	 * Renders the specified text to the screen, center-aligned. Args : renderer,
	 * string, x, y, color
	 */
	public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.drawStringWithShadow(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y,
				color);
	}

	/**
	 * Renders the specified text to the screen. Args : renderer, string, x, y,
	 * color
	 */
	public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.drawStringWithShadow(text, (float) x, (float) y, color);
	}

	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width,
	 * height
	 */
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {

		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos((double) (x + 0), (double) (y + height), (double) this.zLevel)
				.tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + height) * f1)).endVertex();
		worldrenderer.pos((double) (x + width), (double) (y + height), (double) this.zLevel)
				.tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + height) * f1))
				.endVertex();
		worldrenderer.pos((double) (x + width), (double) (y + 0), (double) this.zLevel)
				.tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + 0) * f1)).endVertex();
		worldrenderer.pos((double) (x + 0), (double) (y + 0), (double) this.zLevel)
				.tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + 0) * f1)).endVertex();
		tessellator.draw();
	}

	/**
	 * Draws a textured rectangle using the texture currently bound to the
	 * TextureManager
	 */
	public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos((double) (xCoord + 0.0F), (double) (yCoord + (float) maxV), (double) this.zLevel)
				.tex((double) ((float) (minU + 0) * f), (double) ((float) (minV + maxV) * f1)).endVertex();
		worldrenderer.pos((double) (xCoord + (float) maxU), (double) (yCoord + (float) maxV), (double) this.zLevel)
				.tex((double) ((float) (minU + maxU) * f), (double) ((float) (minV + maxV) * f1)).endVertex();
		worldrenderer.pos((double) (xCoord + (float) maxU), (double) (yCoord + 0.0F), (double) this.zLevel)
				.tex((double) ((float) (minU + maxU) * f), (double) ((float) (minV + 0) * f1)).endVertex();
		worldrenderer.pos((double) (xCoord + 0.0F), (double) (yCoord + 0.0F), (double) this.zLevel)
				.tex((double) ((float) (minU + 0) * f), (double) ((float) (minV + 0) * f1)).endVertex();
		tessellator.draw();
	}

	/**
	 * Draws a texture rectangle using the texture currently bound to the
	 * TextureManager
	 */
	public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn,
			int heightIn) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos((double) (xCoord + 0), (double) (yCoord + heightIn), (double) this.zLevel)
				.tex((double) textureSprite.getMinU(), (double) textureSprite.getMaxV()).endVertex();
		worldrenderer.pos((double) (xCoord + widthIn), (double) (yCoord + heightIn), (double) this.zLevel)
				.tex((double) textureSprite.getMaxU(), (double) textureSprite.getMaxV()).endVertex();
		worldrenderer.pos((double) (xCoord + widthIn), (double) (yCoord + 0), (double) this.zLevel)
				.tex((double) textureSprite.getMaxU(), (double) textureSprite.getMinV()).endVertex();
		worldrenderer.pos((double) (xCoord + 0), (double) (yCoord + 0), (double) this.zLevel)
				.tex((double) textureSprite.getMinU(), (double) textureSprite.getMinV()).endVertex();
		tessellator.draw();
	}

	/**
	 * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height,
	 * textureWidth, textureHeight
	 */
	public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height,
			float textureWidth, float textureHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos((double) x, (double) (y + height), 0.0D)
				.tex((double) (u * f), (double) ((v + (float) height) * f1)).endVertex();
		worldrenderer.pos((double) (x + width), (double) (y + height), 0.0D)
				.tex((double) ((u + (float) width) * f), (double) ((v + (float) height) * f1)).endVertex();
		worldrenderer.pos((double) (x + width), (double) y, 0.0D)
				.tex((double) ((u + (float) width) * f), (double) (v * f1)).endVertex();
		worldrenderer.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
		tessellator.draw();
	}

	/**
	 * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used
	 * anywhere in vanilla code.
	 */
	public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
			int height, float tileWidth, float tileHeight) {
		float f = 1.0F / tileWidth;
		float f1 = 1.0F / tileHeight;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos((double) x, (double) (y + height), 0.0D)
				.tex((double) (u * f), (double) ((v + (float) vHeight) * f1)).endVertex();
		worldrenderer.pos((double) (x + width), (double) (y + height), 0.0D)
				.tex((double) ((u + (float) uWidth) * f), (double) ((v + (float) vHeight) * f1)).endVertex();
		worldrenderer.pos((double) (x + width), (double) y, 0.0D)
				.tex((double) ((u + (float) uWidth) * f), (double) (v * f1)).endVertex();
		worldrenderer.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
		tessellator.draw();
	}

	public static void drawScaledCustomSizeModalRect(double x, double y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight)
    {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)x, (double)(y + height), 0.0D).tex((double)(u * f), (double)((v + (float)vHeight) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((u + (float)uWidth) * f), (double)((v + (float)vHeight) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)y, 0.0D).tex((double)((u + (float)uWidth) * f), (double)(v * f1)).endVertex();
        worldrenderer.pos((double)x, (double)y, 0.0D).tex((double)(u * f), (double)(v * f1)).endVertex();
        tessellator.draw();
    }
	
	public static void arcEllipse(float x, float y, float start, float end, float w, float h, int color) {
		GlStateManager.color(0.0F, 0.0F, 0.0F);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
		float temp = 0.0F;
		if (start > end) {
			temp = end;
			end = start;
			start = temp;
		}

		float var11 = (float) (color >> 24 & 255) / 255.0F;
		float var6 = (float) (color >> 16 & 255) / 255.0F;
		float var7 = (float) (color >> 8 & 255) / 255.0F;
		float var8 = (float) (color & 255) / 255.0F;
		Tessellator var9 = Tessellator.getInstance();
		WorldRenderer var10 = var9.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(var6, var7, var8, var11);
		float i;
		float ldx;
		float ldy;
		if (var11 > 0.5F) {
			GL11.glEnable(2848);
			GL11.glLineWidth(2.0F);
			GL11.glBegin(3);

			for (i = end; i >= start; i -= 4.0F) {
				ldx = (float) Math.cos((double) i * 3.141592653589793D / 180.0D) * w * 1.001F;
				ldy = (float) Math.sin((double) i * 3.141592653589793D / 180.0D) * h * 1.001F;
				GL11.glVertex2f(x + ldx, y + ldy);
			}

			GL11.glEnd();
			GL11.glDisable(2848);
		}

		GL11.glBegin(6);

		for (i = end; i >= start; i -= 4.0F) {
			ldx = (float) Math.cos((double) i * 3.141592653589793D / 180.0D) * w;
			ldy = (float) Math.sin((double) i * 3.141592653589793D / 180.0D) * h;
			GL11.glVertex2f(x + ldx, y + ldy);
		}

		GL11.glEnd();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void arc(float x, float y, float start, float end, float radius, int color) {
		arcEllipse(x, y, start, end, radius, radius, color);
	}

	public static void circle(float x, float y, float radius, int fill) {
		arc(x, y, 0.0F, 360.0F, radius, fill);
	}

	public static void drawCircle(int xx, int yy, int radius, Color col) {
		byte sections = 70;
		double dAngle = 6.283185307179586D / (double) sections;
		GL11.glPushMatrix();
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glShadeModel(7425);
		GL11.glBegin(2);

		for (int i = 0; i < sections; ++i) {
			float x = (float) ((double) radius * Math.cos((double) i * dAngle));
			float y = (float) ((double) radius * Math.sin((double) i * dAngle));
			GL11.glColor4f((float) col.getRed() / 255.0F, (float) col.getGreen() / 255.0F,
					(float) col.getBlue() / 255.0F, (float) col.getAlpha() / 255.0F);
			GL11.glVertex2f((float) xx + x, (float) yy + y);
		}

		GlStateManager.color(0.0F, 0.0F, 0.0F);
		GL11.glEnd();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glPopMatrix();
	}

	public static void drawFilledCircle(float xx, float yy, float radius, Color col) {
		int sections = 50;
		double dAngle = 6.283185307179586 / (double) sections;
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glDisable((int) 3553);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glEnable((int) 2848);
		GL11.glBegin((int) 6);
		int i = 0;
		while (i < sections) {
			float x = (float) ((double) radius * Math.sin((double) i * dAngle));
			float y = (float) ((double) radius * Math.cos((double) i * dAngle));
			GL11.glColor4f((float) ((float) col.getRed() / 255.0f), (float) ((float) col.getGreen() / 255.0f),
					(float) ((float) col.getBlue() / 255.0f), (float) ((float) col.getAlpha() / 255.0f));
			GL11.glVertex2f((float) (xx + x), (float) (yy + y));
			++i;
		}
		GlStateManager.color(0.0f, 0.0f, 0.0f);
		GL11.glEnd();
		GL11.glEnable((int) 3553);
		GL11.glDisable((int) 3042);
		GL11.glDisable((int) 2848);
		GL11.glPopMatrix();
	}

	public static void drawFilledCircle(float xx, float yy, float radius, int col) {
		float f = (float) (col >> 24 & 255) / 255.0f;
		float f1 = (float) (col >> 16 & 255) / 255.0f;
		float f2 = (float) (col >> 8 & 255) / 255.0f;
		float f3 = (float) (col & 255) / 255.0f;
		int sections = 50;
		double dAngle = 6.283185307179586 / (double) sections;
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glDisable((int) 3553);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glEnable((int) 2848);
		GL11.glBegin((int) 6);
		int i = 0;
		while (i < sections) {
			float x = (float) ((double) radius * Math.sin((double) ((double) i * dAngle)));
			float y = (float) ((double) radius * Math.cos((double) ((double) i * dAngle)));
			GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
			GL11.glVertex2f((float) (xx + x), (float) (yy + y));
			++i;
		}
		GlStateManager.color((float) 0.0f, (float) 0.0f, (float) 0.0f);
		GL11.glEnd();
		GL11.glEnable((int) 3553);
		GL11.glDisable((int) 3042);
		GL11.glDisable((int) 2848);
		GL11.glPopMatrix();
	}

	public static void drawLine(float x, float y, float x2, float y2, Color color) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GL11.glEnable((int) 2848);
		GL11.glLineWidth((float) 1.0f);
		GlStateManager.color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f,
				(float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
		worldrenderer.begin(1, DefaultVertexFormats.POSITION);
		worldrenderer.pos(x, y, 0.0).endVertex();
		worldrenderer.pos(x2, y2, 0.0).endVertex();
		tessellator.draw();
		GL11.glDisable((int) 2848);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawRainbowRectVertical(int x, int y, int x1, int y1, int segments, float alpha) {
		if (segments < 1) {
			segments = 1;
		}
		if (segments > y1 - y) {
			segments = y1 - y;
		}
		int segmentLength = (y1 - y) / segments;
		long time = System.nanoTime();
		for (int i = 0; i < segments; ++i) {
			Minecraft.getMinecraft().ingameGUI.drawGradientRect(x, y + segmentLength * i - 1, x1,
					y + (segmentLength + 1) * i, rainbow(time, (float) i * 0.1F, alpha).getRGB(),
					rainbow(time, ((float) i + 0.1F) * 0.1F, alpha).getRGB());
		}
	}

	public static Color rainbow(long time, float count, float fade) {
		float hue = ((float) time + (1.0F + count) * 2.0E8F) / 1.0E10F % 1.0F;
		long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, 1.0F, 1.0F)).intValue()),
				16);
		Color c = new Color((int) color);
		return new Color((float) c.getRed() / 255.0F * fade, (float) c.getGreen() / 255.0F * fade,
				(float) c.getBlue() / 255.0F * fade, (float) c.getAlpha() / 255.0F);
	}


}
