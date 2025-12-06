package cn.Power.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import cn.Power.Client;
import cn.Power.events.EventRender;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.mod.mods.RENDER.Hud;
import cn.Power.mod.mods.WORLD.Xray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public enum RenderUtil {
	INSTANCE;

	public static Minecraft mc = Minecraft.getMinecraft();
	public static float delta;

	public static void enableGL3D(float lineWidth) {
		GL11.glDisable(3008);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glDisable(3553);
		GL11.glDisable(2929);
		GL11.glDepthMask(false);
		GL11.glEnable(2884);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
		GL11.glLineWidth(lineWidth);
	}

	public static void entityESPBox(EntityLivingBase e, Color color, EventRender event) {
		double posX = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) event.getPartialTicks()
				- Minecraft.getMinecraft().getRenderManager().renderPosX;
		double posY = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) event.getPartialTicks()
				- Minecraft.getMinecraft().getRenderManager().renderPosY;
		double posZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) event.getPartialTicks()
				- Minecraft.getMinecraft().getRenderManager().renderPosZ;
		AxisAlignedBB box = AxisAlignedBB.fromBounds(posX - (double) e.width, posY, posZ - (double) e.width,
				posX + (double) e.width, posY + (double) e.height + 0.2, posZ + (double) e.width);
		AxisAlignedBB box1 = AxisAlignedBB.fromBounds(posX - (double) e.width, posY, posZ - (double) e.width,
				posX + (double) e.width, posY + (double) e.height + 0.2, posZ + (double) e.width);
		AxisAlignedBB box2 = AxisAlignedBB.fromBounds(posX - (double) e.width, posY, posZ - (double) e.width,
				posX + (double) e.width, posY + (double) e.height + 0.2, posZ + (double) e.width);

		if (e instanceof EntityLivingBase) {
			box = AxisAlignedBB.fromBounds(posX - (double) e.width + 0.2, posY, posZ - (double) e.width + 0.2,
					posX + (double) e.width - 0.2, posY + (double) e.height + (e.isSneaking() ? 0 : 0.2),
					posZ + (double) e.width - 0.2);
			box1 = AxisAlignedBB.fromBounds(posX - (double) e.width + 0.2, posY, posZ - (double) e.width + 0.2,
					posX + (double) e.width - 0.2, posY + (double) e.height + (e.isSneaking() ? 0 : 0.2),
					posZ + (double) e.width - 0.2);
		}
		GL11.glPushMatrix();
		GL11.glColor4f((float) ((float) color.getRed() / 255.0f), (float) ((float) color.getGreen() / 255.0f),
				(float) ((float) color.getBlue() / 255.0f), (float) 1f);
		GL11.glTranslated(posX, posY, posZ);
		GL11.glRotated((double) (-e.rotationYaw % 360.0f), 0.0, 1.0, 0.0);
		GL11.glTranslated(-(posX), -(posY), -(posZ));
		RenderUtil.renderOne(2.0F);
		RenderUtil.drawBoundingBox(box1);
		RenderUtil.renderTwo();
		RenderUtil.drawBoundingBox(box1);
		RenderUtil.renderThree();
		RenderUtil.renderFour();
		RenderUtil.drawBoundingBox(box1);
		RenderUtil.renderFive();
		GL11.glPopMatrix();
	}

	public static void PlayerESPBox(Entity e, Color color) {

		double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * mc.timer.renderPartialTicks
				- mc.getRenderManager().renderPosX;

		double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * mc.timer.renderPartialTicks
				- mc.getRenderManager().renderPosY;

		double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * mc.timer.renderPartialTicks
				- mc.getRenderManager().renderPosZ;
		EntityLivingBase base = (EntityLivingBase) e;
		double widthX = (e.boundingBox.maxX - e.boundingBox.minX) / 2 + 0.05;
		double widthZ = (e.boundingBox.maxZ - e.boundingBox.minZ) / 2 + 0.05;
		double height = (e.boundingBox.maxY - e.boundingBox.minY) + (e.isSneaking() ? -0.2 : 0.0);

		if (e instanceof EntityPlayer)
			height *= 1.1;
		RenderUtils.pre3D();

		RenderUtils.glColor(Colors.getColor(Color.black));
		GL11.glTranslated(x, y, z);
		GL11.glRotated((double) (-e.rotationYaw % 360.0f), 0.0, 1.0, 0.0);
		GL11.glTranslated(-(x), -(y), -(z));
		for (int i = 0; i < 2; i++) {
			if (i == 1)
				GL11.glColor4f((float) ((float) color.getRed() / 255.0f), (float) ((float) color.getGreen() / 255.0f),
						(float) ((float) color.getBlue() / 255.0f), (float) 1f);
			GL11.glLineWidth(3 - i * 2);
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(x - widthX, y, z - widthZ);
			GL11.glVertex3d(x - widthX, y, z - widthZ);
			GL11.glVertex3d(x - widthX, y + height, z - widthZ);
			GL11.glVertex3d(x + widthX, y + height, z - widthZ);
			GL11.glVertex3d(x + widthX, y, z - widthZ);
			GL11.glVertex3d(x - widthX, y, z - widthZ);
			GL11.glVertex3d(x - widthX, y, z + widthZ);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(x + widthX, y, z + widthZ);
			GL11.glVertex3d(x + widthX, y + height, z + widthZ);
			GL11.glVertex3d(x - widthX, y + height, z + widthZ);
			GL11.glVertex3d(x - widthX, y, z + widthZ);
			GL11.glVertex3d(x + widthX, y, z + widthZ);
			GL11.glVertex3d(x + widthX, y, z - widthZ);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(x + widthX, y + height, z + widthZ);
			GL11.glVertex3d(x + widthX, y + height, z - widthZ);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(x - widthX, y + height, z + widthZ);
			GL11.glVertex3d(x - widthX, y + height, z - widthZ);
			GL11.glEnd();
		}

		RenderUtils.post3D();
	}

	public static void scaffoldESP(Entity e, Color color, EventRender event) {
		double posX = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) event.getPartialTicks()
				- Minecraft.getMinecraft().getRenderManager().renderPosX;
		double posY = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) event.getPartialTicks()
				- Minecraft.getMinecraft().getRenderManager().renderPosY;
		double posZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) event.getPartialTicks()
				- Minecraft.getMinecraft().getRenderManager().renderPosZ;
		AxisAlignedBB box = AxisAlignedBB.fromBounds(posX - (double) e.width, posY, posZ - (double) e.width,
				posX + (double) e.width, posY + (double) e.height + 0.2, posZ + (double) e.width);
		AxisAlignedBB box1 = AxisAlignedBB.fromBounds(posX - (double) e.width, posY, posZ - (double) e.width,
				posX + (double) e.width, posY + (double) e.height + 0.2, posZ + (double) e.width);
		if (e instanceof EntityLivingBase) {
			box = AxisAlignedBB.fromBounds(posX - (double) e.width + 0.11, posY + 0.05, posZ - (double) e.width + 0.11,
					posX + (double) e.width - 0.11, posY + (double) e.height + -1.75, posZ + (double) e.width - 0.11);
			box1 = AxisAlignedBB.fromBounds(posX - (double) e.width + 0.0, posY + 0.05, posZ - (double) e.width + 0.0,
					posX + (double) e.width - 0.0, posY + (double) e.height + -1.75, posZ + (double) e.width - 0.0);
		}
		GL11.glLineWidth(1.5f);
		GL11.glEnable((int) 3042);
		GL11.glEnable((int) 2848);
		GL11.glColor4f((float) ((float) 0.0f), (float) ((float) 0.0f), (float) ((float) 0.0f), (float) 1f);
		RenderUtil.drawOutlinedBoundingBox(box);
		GL11.glColor4f((float) ((float) color.getRed() / 255.0f), (float) ((float) color.getGreen() / 255.0f),
				(float) ((float) color.getBlue() / 255.0f), (float) 1f);
		RenderUtil.drawOutlinedBoundingBox(box1);
	}

	public static int rainbow(int delay) {
		double rainbow = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 4.0);
		return Color.getHSBColor((float) ((rainbow %= 360.0) / 360.0), 0.6f, 1.0f).getRGB();
	}

	public static void disableGL3D() {
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glDisable(3042);
		GL11.glEnable(3008);
		GL11.glDepthMask(true);
		GL11.glCullFace(1029);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
	}

	public static void drawCircle(double x, double y, double radius, int c) {
		float f2 = (float) (c >> 24 & 255) / 255.0f;
		float f22 = (float) (c >> 16 & 255) / 255.0f;
		float f3 = (float) (c >> 8 & 255) / 255.0f;
		float f4 = (float) (c & 255) / 255.0f;
		GlStateManager.alphaFunc(516, 0.001f);
		GlStateManager.color(f22, f3, f4, f2);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		Tessellator tes = Tessellator.getInstance();
		double i = 0.0;
		while (i < 360.0) {
			double f5 = Math.sin(i * 3.141592653589793 / 180.0) * radius;
			double f6 = Math.cos(i * 3.141592653589793 / 180.0) * radius;
			GL11.glVertex2d((double) ((double) f3 + x), (double) ((double) f4 + y));
			i += 1.0;
		}
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.alphaFunc(516, 0.1f);
	}

	public static void drawCircle(double x, double y, float radius, int color) {
		float alpha = (float) (color >> 24 & 255) / 255.0f;
		float red = (float) (color >> 16 & 255) / 255.0f;
		float green = (float) (color >> 8 & 255) / 255.0f;
		float blue = (float) (color & 255) / 255.0f;
		GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
		GL11.glBegin((int) 9);
		int i = 0;
		while (i <= 360) {
			GL11.glVertex2d((double) ((double) x + Math.sin((double) i * 3.141526 / 180.0) * (double) radius),
					(double) ((double) y + Math.cos((double) i * 3.141526 / 180.0) * (double) radius));
			++i;
		}
		GL11.glEnd();
	}

	public static void drawFilledCircle(double x, double y, double r, int c, int id) {
		float f = (float) (c >> 24 & 0xff) / 255F;
		float f1 = (float) (c >> 16 & 0xff) / 255F;
		float f2 = (float) (c >> 8 & 0xff) / 255F;
		float f3 = (float) (c & 0xff) / 255F;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glBegin(GL11.GL_POLYGON);
		if (id == 1) {
			GL11.glVertex2d(x, y);
			for (int i = 0; i <= 90; i++) {
				double x2 = Math.sin((i * 3.141526D / 180)) * r;
				double y2 = Math.cos((i * 3.141526D / 180)) * r;
				GL11.glVertex2d(x - x2, y - y2);
			}
		} else if (id == 2) {
			GL11.glVertex2d(x, y);
			for (int i = 90; i <= 180; i++) {
				double x2 = Math.sin((i * 3.141526D / 180)) * r;
				double y2 = Math.cos((i * 3.141526D / 180)) * r;
				GL11.glVertex2d(x - x2, y - y2);
			}
		} else if (id == 3) {
			GL11.glVertex2d(x, y);
			for (int i = 270; i <= 360; i++) {
				double x2 = Math.sin((i * 3.141526D / 180)) * r;
				double y2 = Math.cos((i * 3.141526D / 180)) * r;
				GL11.glVertex2d(x - x2, y - y2);
			}
		} else if (id == 4) {
			GL11.glVertex2d(x, y);
			for (int i = 180; i <= 270; i++) {
				double x2 = Math.sin((i * 3.141526D / 180)) * r;
				double y2 = Math.cos((i * 3.141526D / 180)) * r;
				GL11.glVertex2d(x - x2, y - y2);
			}
		} else {
			for (int i = 0; i <= 360; i++) {
				double x2 = Math.sin((i * 3.141526D / 180)) * r;
				double y2 = Math.cos((i * 3.141526D / 180)) * r;
				GL11.glVertex2f((float) (x - x2), (float) (y - y2));
			}
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void rectangle(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double var5 = left;
			left = right;
			right = var5;
		}
		if (top < bottom) {
			double var5 = top;
			top = bottom;
			bottom = var5;
		}
		float var11 = (color >> 24 & 0xFF) / 255.0F;
		float var6 = (color >> 16 & 0xFF) / 255.0F;
		float var7 = (color >> 8 & 0xFF) / 255.0F;
		float var8 = (color & 0xFF) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(var6, var7, var8, var11);
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(left, bottom, 0.0D).endVertex();
		worldRenderer.pos(right, bottom, 0.0D).endVertex();
		worldRenderer.pos(right, top, 0.0D).endVertex();
		worldRenderer.pos(left, top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
		float f = (col1 >> 24 & 0xFF) / 255.0F;
		float f1 = (col1 >> 16 & 0xFF) / 255.0F;
		float f2 = (col1 >> 8 & 0xFF) / 255.0F;
		float f3 = (col1 & 0xFF) / 255.0F;

		float f4 = (col2 >> 24 & 0xFF) / 255.0F;
		float f5 = (col2 >> 16 & 0xFF) / 255.0F;
		float f6 = (col2 >> 8 & 0xFF) / 255.0F;
		float f7 = (col2 & 0xFF) / 255.0F;

		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glShadeModel(7425);

		GL11.glPushMatrix();
		GL11.glBegin(7);
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glVertex2d(left, top);
		GL11.glVertex2d(left, bottom);

		GL11.glColor4f(f5, f6, f7, f4);
		GL11.glVertex2d(right, bottom);
		GL11.glVertex2d(right, top);
		GL11.glEnd();
		GL11.glPopMatrix();

		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glShadeModel(7424);
	}

	public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor,
			int borderColor) {
		rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		rectangle(x + width, y, x1 - width, y + width, borderColor);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		rectangle(x, y, x + width, y1, borderColor);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		rectangle(x1 - width, y, x1, y1, borderColor);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		rectangle(x + width, y1 - width, x1 - width, y1, borderColor);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void drawFullCircle(int cx, int cy, double r, int segments, float lineWidth, int part, int c) {
		GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
		r *= 2.0;
		cx *= 2;
		cy *= 2;
		float f2 = (float) (c >> 24 & 255) / 255.0f;
		float f22 = (float) (c >> 16 & 255) / 255.0f;
		float f3 = (float) (c >> 8 & 255) / 255.0f;
		float f4 = (float) (c & 255) / 255.0f;
		GL11.glEnable((int) 3042);
		GL11.glLineWidth((float) lineWidth);
		GL11.glDisable((int) 3553);
		GL11.glEnable((int) 2848);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glColor4f((float) f22, (float) f3, (float) f4, (float) f2);
		GL11.glBegin((int) 3);
		int i = segments - part;
		while (i <= segments) {
			double x = Math.sin((double) i * 3.141592653589793 / 180.0) * r;
			double y = Math.cos((double) i * 3.141592653589793 / 180.0) * r;
			GL11.glVertex2d((double) ((double) cx + x), (double) ((double) cy + y));
			++i;
		}
		GL11.glEnd();
		GL11.glDisable((int) 2848);
		GL11.glEnable((int) 3553);
		GL11.glDisable((int) 3042);
		GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
	}

	public static void drawBox(Box box) {
		if (box == null) {
			return;
		}
		// back
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.minX, box.minY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
		GL11.glVertex3d(box.minX, box.minY, box.maxZ);
		GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
		GL11.glEnd();
		// left
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.minX, box.minY, box.minZ);
		GL11.glVertex3d(box.minX, box.minY, box.maxZ);
		GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.minX, box.maxY, box.minZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.minX, box.minY, box.maxZ);
		GL11.glVertex3d(box.minX, box.minY, box.minZ);
		GL11.glVertex3d(box.minX, box.maxY, box.minZ);
		GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
		GL11.glEnd();
		// right
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.minY, box.minZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.maxX, box.minY, box.minZ);
		GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
		GL11.glEnd();
		// front
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.minX, box.minY, box.minZ);
		GL11.glVertex3d(box.maxX, box.minY, box.minZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
		GL11.glVertex3d(box.minX, box.maxY, box.minZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.maxX, box.minY, box.minZ);
		GL11.glVertex3d(box.minX, box.minY, box.minZ);
		GL11.glVertex3d(box.minX, box.maxY, box.minZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
		GL11.glEnd();
		// top
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.minX, box.maxY, box.minZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
		GL11.glVertex3d(box.minX, box.maxY, box.minZ);
		GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
		GL11.glEnd();
		// bottom
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.minX, box.minY, box.minZ);
		GL11.glVertex3d(box.maxX, box.minY, box.minZ);
		GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
		GL11.glVertex3d(box.minX, box.minY, box.maxZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(box.maxX, box.minY, box.minZ);
		GL11.glVertex3d(box.minX, box.minY, box.minZ);
		GL11.glVertex3d(box.minX, box.minY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
		GL11.glEnd();
	}

	public static void glColor(int hex) {
		float alpha = (hex >> 24 & 0xFF) / 255.0F;
		float red = (hex >> 16 & 0xFF) / 255.0F;
		float green = (hex >> 8 & 0xFF) / 255.0F;
		float blue = (hex & 0xFF) / 255.0F;
		GL11.glColor4f(red, green, blue, alpha);
	}

	public static double interpolate(double newPos, double oldPos) {
		return oldPos + (newPos - oldPos) * (double) Minecraft.getMinecraft().timer.renderPartialTicks;
	}

	public static Color rainbowEffect(int delay) {
		float hue = (float) (System.nanoTime() + delay) / 2.0E10F % 1.0F;
		Color color = new Color((int) Long
				.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, 1.0F, 1.0F)).intValue()), 16));
		return new Color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
				color.getAlpha() / 255.0F);
	}

	public static void drawFullscreenImage(ResourceLocation image) {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		GL11.glDisable((int) 2929);
		GL11.glDepthMask((boolean) false);
		OpenGlHelper.glBlendFunc((int) 770, (int) 771, (int) 1, (int) 0);
		GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
		GL11.glDisable((int) 3008);
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		Gui.drawModalRectWithCustomSizedTexture((int) 0, (int) 0, (float) 0.0f, (float) 0.0f,
				(int) scaledResolution.getScaledWidth(), (int) scaledResolution.getScaledHeight(),
				(float) scaledResolution.getScaledWidth(), (float) scaledResolution.getScaledHeight());
		GL11.glDepthMask((boolean) true);
		GL11.glEnable((int) 2929);
		GL11.glEnable((int) 3008);
		GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
	}

	public static void drawPlayerHead(String playerName, int x, int y, int width, int height) {
		for (Object player : Minecraft.getMinecraft().theWorld.getLoadedEntityList()) {
			if (player instanceof EntityPlayer) {
				EntityPlayer ply = (EntityPlayer) player;
				if (playerName.equalsIgnoreCase(ply.getName())) {
					GameProfile profile = new GameProfile(ply.getUniqueID(), ply.getName());
					NetworkPlayerInfo networkplayerinfo1 = new NetworkPlayerInfo(profile);
					ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
					GL11.glDisable((int) 2929);
					GL11.glEnable((int) 3042);
					GL11.glDepthMask((boolean) false);
					OpenGlHelper.glBlendFunc(770, 771, 1, 0);
					GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
					Minecraft.getMinecraft().getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
					Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
					GL11.glDepthMask((boolean) true);
					GL11.glDisable((int) 3042);
					GL11.glEnable((int) 2929);
				}
			}
		}
	}

	public static double getAnimationState(double animation, double finalState, double speed) {
		float add = (float) (delta * speed);
		if (animation < finalState) {
			if (animation + add < finalState)
				animation += add;
			else
				animation = finalState;
		} else {
			if (animation - add > finalState)
				animation -= add;
			else
				animation = finalState;
		}
		return animation;
	}

	public static double GetAnimationState(double animation, double finalState, double speed) {
		float add = (float) ((double) delta * speed);
		animation = animation < finalState
				? (animation + (double) add < finalState ? (animation += (double) add) : finalState)
				: (animation - (double) add > finalState ? (animation -= (double) add) : finalState);
		return animation;
	}

	public static String getShaderCode(InputStreamReader file) {
		String shaderSource = "";
		try {
			String line;
			BufferedReader reader = new BufferedReader((Reader) file);
			while ((line = reader.readLine()) != null) {
				shaderSource = String.valueOf((Object) shaderSource) + line + "\n";
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit((int) -1);
		}
		return shaderSource.toString();
	}

	public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		GL11.glDisable((int) 2929);
		GL11.glEnable((int) 3042);
		GL11.glDepthMask((boolean) false);
		OpenGlHelper.glBlendFunc((int) 770, (int) 771, (int) 1, (int) 0);
		GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, (float) 0.0f, (float) 0.0f, (int) width, (int) height,
				(float) width, (float) height);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 3042);
//        GL11.glEnable((int)2929);

	}

	public static void drawImage(ResourceLocation image, float x, float y, int width, int height) {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		GL11.glDisable((int) 2929);
		GL11.glEnable((int) 3042);
		GL11.glDepthMask((boolean) false);
		OpenGlHelper.glBlendFunc((int) 770, (int) 771, (int) 1, (int) 0);
		GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, (float) 0.0f, (float) 0.0f, (int) width, (int) height,
				(float) width, (float) height);
		GL11.glDepthMask((boolean) true);
		// GL11.glDisable((int)3042);
		GL11.glEnable((int) 2929);
	}

	public static void drawOutlinedRect(int x, int y, int width, int height, int lineSize, Color lineColor,
			Color backgroundColor) {
		RenderUtil.drawRect(x, y, width, height, backgroundColor.getRGB());
		RenderUtil.drawRect(x, y, width, y + lineSize, lineColor.getRGB());
		RenderUtil.drawRect(x, height - lineSize, width, height, lineColor.getRGB());
		RenderUtil.drawRect(x, y + lineSize, x + lineSize, height - lineSize, lineColor.getRGB());
		RenderUtil.drawRect(width - lineSize, y + lineSize, width, height - lineSize, lineColor.getRGB());
	}

	public static void drawImage(ResourceLocation image, int x, int y, int width, int height, Color color) {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		GL11.glDisable((int) 2929);
		GL11.glEnable((int) 3042);
		GL11.glDepthMask((boolean) false);
		OpenGlHelper.glBlendFunc((int) 770, (int) 771, (int) 1, (int) 0);
		GL11.glColor4f((float) ((float) color.getRed() / 255.0f), (float) ((float) color.getGreen() / 255.0f),
				(float) ((float) color.getBlue() / 255.0f), (float) ((float) color.getAlpha() / 255.0f));
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, (float) 0.0f, (float) 0.0f, (int) width, (int) height,
				(float) width, (float) height);
		GL11.glDepthMask((boolean) true);
		// GL11.glDisable((int)3042);
		GL11.glEnable((int) 2929);
	}

	public static void doGlScissor(int x, int y, int width, int height) {
		Minecraft mc = Minecraft.getMinecraft();
		int scaleFactor = 1;
		int k = mc.gameSettings.guiScale;
		if (k == 0) {
			k = 1000;
		}
		while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320
				&& mc.displayHeight / (scaleFactor + 1) >= 240) {
			++scaleFactor;
		}
		GL11.glScissor((int) (x * scaleFactor), (int) (mc.displayHeight - (y + height) * scaleFactor),
				(int) (width * scaleFactor), (int) (height * scaleFactor));
	}

	public static void doGlScissor1(float x, float y, float x1, float y1) {
		int scaleFactor = getScaleFactor();
		GL11.glScissor((int) (x * scaleFactor), (int) (Minecraft.getMinecraft().displayHeight - (y1 * scaleFactor)),
				(int) ((x1 - x) * scaleFactor), (int) ((y1 - y) * scaleFactor));
	}

	/**
	 * @return The scale factor used by the play's screen gui scale
	 */
	public static int getScaleFactor() {
		int scaleFactor = 1;
		boolean isUnicode = Minecraft.getMinecraft().isUnicode();
		int guiScale = Minecraft.getMinecraft().gameSettings.guiScale;

		if (guiScale == 0) {
			guiScale = 1000;
		}

		while (scaleFactor < guiScale && Minecraft.getMinecraft().displayWidth / (scaleFactor + 1) >= 320
				&& Minecraft.getMinecraft().displayHeight / (scaleFactor + 1) >= 240) {
			scaleFactor++;
		}

		if (isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1) {
			scaleFactor--;
		}

		return scaleFactor;
	}

	public static void drawRect(float x1, float y1, float x2, float y2, int color) {
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glDisable((int) 3553);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glEnable((int) 2848);
		GL11.glPushMatrix();
		RenderUtil.color(color);
		GL11.glBegin((int) 7);
		GL11.glVertex2d((double) x2, (double) y1);
		GL11.glVertex2d((double) x1, (double) y1);
		GL11.glVertex2d((double) x1, (double) y2);
		GL11.glVertex2d((double) x2, (double) y2);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable((int) 3553);
		// GL11.glDisable((int)3042);
		GL11.glDisable((int) 2848);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static void drawRect(double x1, double y1, double x2, double y2, int color) {
		Gui.drawRect(x1, y1, x2, y2, color);
	}

	public static void color(int color) {
		float f = (float) (color >> 24 & 255) / 255.0f;
		float f1 = (float) (color >> 16 & 255) / 255.0f;
		float f2 = (float) (color >> 8 & 255) / 255.0f;
		float f3 = (float) (color & 255) / 255.0f;
		GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
	}

	public static int createShader(String shaderCode, int shaderType) throws Exception {
		int shader;
		block4: {
			shader = 0;
			try {
				shader = ARBShaderObjects.glCreateShaderObjectARB((int) shaderType);
				if (shader != 0)
					break block4;
				return 0;
			} catch (Exception exc) {
				ARBShaderObjects.glDeleteObjectARB((int) shader);
				throw exc;
			}
		}
		ARBShaderObjects.glShaderSourceARB((int) shader, (CharSequence) shaderCode);
		ARBShaderObjects.glCompileShaderARB((int) shader);
		if (ARBShaderObjects.glGetObjectParameteriARB((int) shader, (int) 35713) == 0) {
			throw new RuntimeException("Error creating shader:");
		}
		return shader;
	}

	public void drawCircle(int x, int y, float radius, int color) {
		float alpha = (float) (color >> 24 & 255) / 255.0f;
		float red = (float) (color >> 16 & 255) / 255.0f;
		float green = (float) (color >> 8 & 255) / 255.0f;
		float blue = (float) (color & 255) / 255.0f;
		boolean blend = GL11.glIsEnabled((int) 3042);
		boolean line = GL11.glIsEnabled((int) 2848);
		boolean texture = GL11.glIsEnabled((int) 3553);
		if (!blend) {
			GL11.glEnable((int) 3042);
		}
		if (!line) {
			GL11.glEnable((int) 2848);
		}
		if (texture) {
			GL11.glDisable((int) 3553);
		}
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
		GL11.glBegin((int) 9);
		int i = 0;
		while (i <= 360) {
			GL11.glVertex2d(
					(double) ((double) x + Math.sin((double) ((double) i * 3.141526 / 180.0)) * (double) radius),
					(double) ((double) y + Math.cos((double) ((double) i * 3.141526 / 180.0)) * (double) radius));
			++i;
		}
		GL11.glEnd();
		if (texture) {
			GL11.glEnable((int) 3553);
		}
		if (!line) {
			GL11.glDisable((int) 2848);
		}
		if (!blend) {
			GL11.glDisable((int) 3042);
		}
	}

	public static void renderOne(float width) {
		checkSetupFBO();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glLineWidth(width);
//        GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glClearStencil(0xF);
		GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
	}

	public static void renderTwo() {
		GL11.glStencilFunc(GL11.GL_NEVER, 0, 0xF);
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	public static void renderThree() {
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
	}

	public static void renderFour() {
		setColor(new Color(255, 255, 255));
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
		GL11.glPolygonOffset(1.0F, -2000000F);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
	}

	public static void renderFive() {
		GL11.glPolygonOffset(1.0F, 2000000F);
		GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopAttrib();
	}

	public static void setColor(Color c) {
		GL11.glColor4d(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
	}

	public static void checkSetupFBO() {
		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
		if (fbo != null) {
			if (fbo.depthBuffer > -1) {
				setupFBO(fbo);
				fbo.depthBuffer = -1;
			}
		}
	}

	public static void setupFBO(Framebuffer fbo) {
		EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
		int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
		EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, Minecraft.getMinecraft().displayWidth,
				Minecraft.getMinecraft().displayHeight);
		EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				stencil_depth_buffer_ID);
		EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				stencil_depth_buffer_ID);
	}

	public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		worldRenderer.begin(3, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(3, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(1, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		tessellator.draw();
	}

	public static void drawBoundingBox(AxisAlignedBB aa) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		tessellator.draw();
	}

	public static void drawOutlinedBlockESP(double x, double y, double z, float red, float green, float blue,
			float alpha, float lineWidth) {
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glDisable((int) 3553);
		GL11.glEnable((int) 2848);
		GL11.glDisable((int) 2929);
		GL11.glDepthMask((boolean) false);
		GL11.glLineWidth((float) lineWidth);
		GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
		RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
		GL11.glDisable((int) 2848);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 3042);
		GL11.glPopMatrix();
	}

	public static void drawBlockESP(double x, double y, double z, float red, float green, float blue, float alpha,
			float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWidth) {
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glDisable((int) 2896);
		GL11.glDisable((int) 3553);
		GL11.glEnable((int) 2848);
		GL11.glDisable((int) 2929);
		GL11.glDepthMask((boolean) false);
		GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
		RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
		GL11.glLineWidth((float) lineWidth);
		GL11.glColor4f((float) lineRed, (float) lineGreen, (float) lineBlue, (float) lineAlpha);
		RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
		GL11.glDisable((int) 2848);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2896);
		GL11.glEnable((int) 2929);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 3042);
		GL11.glPopMatrix();
	}

	public static void drawSolidBlockESP(double x, double y, double z, float red, float green, float blue,
			float alpha) {
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glDisable((int) 3553);
		GL11.glEnable((int) 2848);
		GL11.glDisable((int) 2929);
		GL11.glDepthMask((boolean) false);
		GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
		RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
		GL11.glColor3f(1, 1, 1);
		GL11.glDisable((int) 2848);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 3042);
		GL11.glPopMatrix();
	}

	public static void DrawSolidBlockESP(double x, double y, double z, int Color) {
		float R = (float) (Color >> 16 & 255) / 255.0F;
		float G = (float) (Color >> 8 & 255) / 255.0F;
		float B = (float) (Color & 255) / 255.0F;
		float A = (float) (Color >> 24 & 255) / 255.0F;
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glDisable((int) 3553);
		GL11.glDisable((int) 2929);
		GL11.glDepthMask((boolean) false);
		GL11.glLineWidth(Xray.LineWteigh.getValueState().floatValue());
		GL11.glColor4f((float) R, (float) G, (float) B, (float) A);
		drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
//        RenderUtil.renderOne(2.0F);
//        RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
//        RenderUtil.renderTwo();
//        RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
//        RenderUtil.renderThree();
//        RenderUtil.renderFour();
//        RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
//        RenderUtil.renderFive();
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 3042);
		GL11.glPopMatrix();
	}

	public static void drawOutlinedEntityESP(double x, double y, double z, double width, double height, float red,
			float green, float blue, float alpha) {
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glDisable((int) 3553);
		GL11.glEnable((int) 2848);
		GL11.glDisable((int) 2929);
		GL11.glDepthMask((boolean) false);
		GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
		RenderUtil
				.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
		GL11.glDisable((int) 2848);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 3042);
		GL11.glPopMatrix();
	}

	public static void drawSolidEntityESP(double x, double y, double z, double width, double height, float red,
			float green, float blue, float alpha) {
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glDisable((int) 3553);
		GL11.glEnable((int) 2848);
		GL11.glDisable((int) 2929);
		GL11.glDepthMask((boolean) false);
		GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
		RenderUtil.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
		GL11.glDisable((int) 2848);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 3042);
		GL11.glPopMatrix();
	}

	public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green,
			float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
		final double mid = 0.5;
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);

		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glDisable((int) 3553);
		GL11.glEnable((int) 2848);
		GL11.glDisable((int) 2929);
		GL11.glTranslated(x + mid, y + mid, z + mid);
		GL11.glRotated((double) (-KillAura.Target.rotationYaw % 360.0f), 0.0, 1.0, 0.0);
		GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
		GL11.glDepthMask((boolean) false);
		GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
		// RenderUtil.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x +
		// width, y + height, z + width));

		GL11.glLineWidth((float) lineWdith);
		GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
		// GL11.glColor4f((float)lineRed, (float)lineGreen, (float)lineBlue,
		// (float)lineAlpha);
		// ESP�����

		RenderUtil
				.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
		GL11.glDisable((int) 2848);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 3042);
		GL11.glPopMatrix();
	}

	public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green,
			float blue, float alpha) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4f(red, green, blue, alpha);
		drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	private static void glColor(Color color) {
		GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
				color.getAlpha() / 255.0F);
	}

	public static void drawFilledBox(AxisAlignedBB mask) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(mask.minX, mask.minY, mask.minZ).endVertex();
		worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
		worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
		worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
		worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
		worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
		worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
		worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
		worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
		worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
		worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
		worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
		worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
		tessellator.draw();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(mask.minX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.minX, mask.minY, mask.maxZ);
		worldRenderer.pos(mask.minX, mask.maxY, mask.minZ);
		worldRenderer.pos(mask.minX, mask.minY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.minZ);
		worldRenderer.pos(mask.maxX, mask.maxY, mask.maxZ);
		worldRenderer.pos(mask.maxX, mask.minY, mask.maxZ);
		tessellator.draw();
	}

	public static void drawRoundedRect(int x0, int y0, int x1, int y1, float radius, int color) {
		int i = 18;
		float f = 90.0F / (float) i;
		float f1 = (float) (color >> 24 & 255) / 255.0F;
		float f2 = (float) (color >> 16 & 255) / 255.0F;
		float f3 = (float) (color >> 8 & 255) / 255.0F;
		float f4 = (float) (color & 255) / 255.0F;
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(f2, f3, f4, f1);
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glVertex2f((float) x0 + radius, (float) y0);
		GL11.glVertex2f((float) x0 + radius, (float) y1);
		GL11.glVertex2f((float) x1 - radius, (float) y0);
		GL11.glVertex2f((float) x1 - radius, (float) y1);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glVertex2f((float) x0, (float) y0 + radius);
		GL11.glVertex2f((float) x0 + radius, (float) y0 + radius);
		GL11.glVertex2f((float) x0, (float) y1 - radius);
		GL11.glVertex2f((float) x0 + radius, (float) y1 - radius);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glVertex2f((float) x1, (float) y0 + radius);
		GL11.glVertex2f((float) x1 - radius, (float) y0 + radius);
		GL11.glVertex2f((float) x1, (float) y1 - radius);
		GL11.glVertex2f((float) x1 - radius, (float) y1 - radius);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		float f5 = (float) x1 - radius;
		float f6 = (float) y0 + radius;
		GL11.glVertex2f(f5, f6);

		for (int j = 0; j <= i; ++j) {
			float f7 = (float) j * f;
			GL11.glVertex2f((float) ((double) f5 + (double) radius * Math.cos(Math.toRadians((double) f7))),
					(float) ((double) f6 - (double) radius * Math.sin(Math.toRadians((double) f7))));
		}

		GL11.glEnd();
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		f5 = (float) x0 + radius;
		f6 = (float) y0 + radius;
		GL11.glVertex2f(f5, f6);

		for (int k = 0; k <= i; ++k) {
			float f8 = (float) k * f;
			GL11.glVertex2f((float) ((double) f5 - (double) radius * Math.cos(Math.toRadians((double) f8))),
					(float) ((double) f6 - (double) radius * Math.sin(Math.toRadians((double) f8))));
		}

		GL11.glEnd();
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		f5 = (float) x0 + radius;
		f6 = (float) y1 - radius;
		GL11.glVertex2f(f5, f6);

		for (int l = 0; l <= i; ++l) {
			float f9 = (float) l * f;
			GL11.glVertex2f((float) ((double) f5 - (double) radius * Math.cos(Math.toRadians((double) f9))),
					(float) ((double) f6 + (double) radius * Math.sin(Math.toRadians((double) f9))));
		}

		GL11.glEnd();
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		f5 = (float) x1 - radius;
		f6 = (float) y1 - radius;
		GL11.glVertex2f(f5, f6);

		for (int i1 = 0; i1 <= i; ++i1) {
			float f10 = (float) i1 * f;
			GL11.glVertex2f((float) ((double) f5 + (double) radius * Math.cos(Math.toRadians((double) f10))),
					(float) ((double) f6 + (double) radius * Math.sin(Math.toRadians((double) f10))));
		}

		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawRoundedRect(float x, float y, float x2, float y2, final float round, final int color) {
		x += (float) (round / 2.0f + 0.5);
		y += (float) (round / 2.0f + 0.5);
		x2 -= (float) (round / 2.0f + 0.5);
		y2 -= (float) (round / 2.0f + 0.5);
		Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, color);
		circle(x2 - round / 2.0f, y + round / 2.0f, round, color);
		circle(x + round / 2.0f, y2 - round / 2.0f, round, color);
		circle(x + round / 2.0f, y + round / 2.0f, round, color);
		circle(x2 - round / 2.0f, y2 - round / 2.0f, round, color);
		Gui.drawRect((int) (x - round / 2.0f - 0.5f), (int) (y + round / 2.0f), (int) x2, (int) (y2 - round / 2.0f),
				color);
		Gui.drawRect((int) x, (int) (y + round / 2.0f), (int) (x2 + round / 2.0f + 0.5f), (int) (y2 - round / 2.0f),
				color);
		Gui.drawRect((int) (x + round / 2.0f), (int) (y - round / 2.0f - 0.5f), (int) (x2 - round / 2.0f),
				(int) (y2 - round / 2.0f), color);
		Gui.drawRect((int) (x + round / 2.0f), (int) y, (int) (x2 - round / 2.0f), (int) (y2 + round / 2.0f + 0.5f),
				color);
	}

	public static void circle(final float x, final float y, final float radius, final int fill) {
		arc(x, y, 0.0f, 360.0f, radius, fill);
	}

	public static void circle(final float x, final float y, final float radius, final Color fill) {
		arc(x, y, 0.0f, 360.0f, radius, fill);
	}

	public static void arc(final float x, final float y, final float start, final float end, final float radius,
			final int color) {
		arcEllipse(x, y, start, end, radius, radius, color);
	}

	public static void arc(final float x, final float y, final float start, final float end, final float radius,
			final Color color) {
		arcEllipse(x, y, start, end, radius, radius, color);
	}

	public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h,
			final int color) {
		GlStateManager.color(0.0f, 0.0f, 0.0f);
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
		float temp = 0.0f;
		if (start > end) {
			temp = end;
			end = start;
			start = temp;
		}
		final float var11 = (color >> 24 & 0xFF) / 255.0f;
		final float var12 = (color >> 16 & 0xFF) / 255.0f;
		final float var13 = (color >> 8 & 0xFF) / 255.0f;
		final float var14 = (color & 0xFF) / 255.0f;
		final Tessellator var15 = Tessellator.getInstance();
		final WorldRenderer var16 = var15.getWorldRenderer();
//        GlStateManager.enableBlend();
//        GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(var12, var13, var14, var11);
		if (var11 > 0.5f) {
			GL11.glEnable(2848);
			GL11.glLineWidth(2.0f);
			GL11.glBegin(3);
			for (float i = end; i >= start; i -= 4.0f) {
				final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
				final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
				GL11.glVertex2f(x + ldx, y + ldy);
			}
			GL11.glEnd();
			GL11.glDisable(2848);
		}
		GL11.glBegin(6);
		for (float i = end; i >= start; i -= 4.0f) {
			final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w;
			final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h;
			GL11.glVertex2f(x + ldx, y + ldy);
		}
		GL11.glEnd();
//        GlStateManager.enableTexture2D();
//        GlStateManager.disableBlend();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h,
			final Color color) {
		GlStateManager.color(0.0f, 0.0f, 0.0f);
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
		float temp = 0.0f;
		if (start > end) {
			temp = end;
			end = start;
			start = temp;
		}
		final Tessellator var9 = Tessellator.getInstance();
		final WorldRenderer var10 = var9.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f,
				color.getAlpha() / 255.0f);
		if (color.getAlpha() > 0.5f) {
			GL11.glEnable(2848);
			GL11.glLineWidth(2.0f);
			GL11.glBegin(3);
			for (float i = end; i >= start; i -= 4.0f) {
				final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
				final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
				GL11.glVertex2f(x + ldx, y + ldy);
			}
			GL11.glEnd();
			GL11.glDisable(2848);
		}
		GL11.glBegin(6);
		for (float i = end; i >= start; i -= 4.0f) {
			final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w;
			final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h;
			GL11.glVertex2f(x + ldx, y + ldy);
		}
		GL11.glEnd();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static void pre() {
		GL11.glDisable(2929);
		GL11.glDisable(3553);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
	}

	public static void post() {
		GL11.glDisable(3042);
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glColor3d(1.0, 1.0, 1.0);
	}

	public static void drawBorderedRect(final float x, final float y, final float x2, final float y2, final float l1,
			final int col1, final int col2) {
		Gui.drawRect(x, y, x2, y2, col2);
		final float f = (col1 >> 24 & 0xFF) / 255.0f;
		final float f2 = (col1 >> 16 & 0xFF) / 255.0f;
		final float f3 = (col1 >> 8 & 0xFF) / 255.0f;
		final float f4 = (col1 & 0xFF) / 255.0f;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glPushMatrix();
		GL11.glColor4f(f2, f3, f4, f);
		GL11.glLineWidth(l1);
		GL11.glBegin(1);
		GL11.glVertex2d((double) x, (double) y);
		GL11.glVertex2d((double) x, (double) y2);
		GL11.glVertex2d((double) x2, (double) y2);
		GL11.glVertex2d((double) x2, (double) y);
		GL11.glVertex2d((double) x, (double) y);
		GL11.glVertex2d((double) x2, (double) y);
		GL11.glVertex2d((double) x, (double) y2);
		GL11.glVertex2d((double) x2, (double) y2);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
	}

	public static void setupRender(boolean start) {
		if (start) {
			GlStateManager.enableBlend();
			GL11.glEnable((int) 2848);
			GlStateManager.disableDepth();
			GlStateManager.disableTexture2D();
			GlStateManager.blendFunc(770, 771);
			GL11.glHint((int) 3154, (int) 4354);
		} else {
			GlStateManager.disableBlend();
			GlStateManager.enableTexture2D();
			GL11.glDisable((int) 2848);
			GlStateManager.enableDepth();
		}
		GlStateManager.depthMask(!start);
	}

	public static Vec3 interpolateRender(EntityPlayer player) {
		float part = Minecraft.getMinecraft().timer.renderPartialTicks;
		double interpX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) part;
		double interpY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) part;
		double interpZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) part;
		return new Vec3(interpX, interpY, interpZ);
	}

	public static void drawblock(double a, double a2, double a3, int a5, float a6) {
		float a11 = (float) (a5 >> 24 & 255) / 255.0F;
		float a12 = (float) (a5 >> 16 & 255) / 255.0F;
		float a13 = (float) (a5 >> 8 & 255) / 255.0F;
		float a14 = (float) (a5 & 255) / 255.0F;
		GL11.glPushMatrix();
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		GL11.glDisable(2929);
		GL11.glDepthMask(false);
		GL11.glLineWidth(a6);
		GL11.glColor4f(a12, a13, a14, a11);
		drawOutlinedBoundingBox(new AxisAlignedBB(a, a2, a3, a + 1.0D, a2 + 1.0D, a3 + 1.0D));
		GL11.glDisable(2848);
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glDepthMask(true);
		GL11.glDisable(3042);
		GL11.glPopMatrix();
	}

	public static void drawUnfilledRect(float x, float y, int width, int height, int c, double lWidth) {
		GL11.glPushMatrix();
		float f = (c >> 24 & 0xFF) / 255.0F;
		float f1 = (c >> 16 & 0xFF) / 255.0F;
		float f2 = (c >> 8 & 0xFF) / 255.0F;
		float f3 = (c & 0xFF) / 255.0F;

		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(f1, f2, f3, f);

		Gui.drawRect(x, y + height, x + lWidth, y, c);
		Gui.drawRect(x + 1, y + lWidth, x + width + 1, y, c);
		Gui.drawRect(x + width, y + height, x + width + lWidth, y + 1, c);
		Gui.drawRect(x, y + height + lWidth, x + width + 1, y + height, c);
		GL11.glDisable(2848);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		GL11.glPopMatrix();
	}

	public static void ItemESP(Entity e, Color color, EventRender event) {
		GL11.glPushMatrix();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3042);
		GL11.glEnable(2848);
		GL11.glLineWidth(2.0f);
		GL11.glDisable(3553);
		GL11.glDisable(2929);
		GL11.glDepthMask(false);

		double var10000 = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) event.getPartialTicks();
		Minecraft.getMinecraft().getRenderManager();
		double posX = var10000 - Minecraft.getMinecraft().getRenderManager().renderPosX;
		var10000 = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) event.getPartialTicks();
		Minecraft.getMinecraft().getRenderManager();
		double posY = var10000 - Minecraft.getMinecraft().getRenderManager().renderPosY;
		var10000 = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) event.getPartialTicks();
		Minecraft.getMinecraft().getRenderManager();
		double posZ = var10000 - Minecraft.getMinecraft().getRenderManager().renderPosZ;
		AxisAlignedBB box = AxisAlignedBB.fromBounds(posX - (double) e.width, posY + 0.1, posZ - (double) e.width,
				posX + (double) e.width, posY + (double) e.height - 0.05, posZ + (double) e.width);
		if (e instanceof EntityLivingBase) {
			box = AxisAlignedBB.fromBounds(posX - (double) e.width + 0.2D, posY, posZ - (double) e.width + 0.2D,
					posX + (double) e.width - 0.2D, posY + (double) e.height + (0.2D), posZ + (double) e.width - 0.2D);
		}

		GL11.glLineWidth(1.0F);
		GL11.glTranslated(posX, posY, posZ);
		GL11.glRotated((double) (-e.rotationYaw % 360.0f), 0.0, 1.0, 0.0);
		GL11.glTranslated(-(posX), -(posY), -(posZ));
		GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F,
				(float) color.getBlue() / 255.0F, 1.0F);
		drawOutlinedBoundingBox(box);
		GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F,
				(float) color.getBlue() / 255.0F, 0.20F);
		drawBoundingBox(box);
		GL11.glDisable(2848);
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glDepthMask(true);
		GL11.glDisable(3042);
		GL11.glPopMatrix();
	}

	public static void outlineOne() {
		GL11.glPushAttrib((int) 1048575);
		GL11.glDisable((int) 3008);
		GL11.glDisable((int) 3553);
		GL11.glDisable((int) 2896);
		GL11.glEnable((int) 3042);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glLineWidth((float) 4.0f);
		GL11.glEnable((int) 2848);
		GL11.glEnable((int) 2960);
		GL11.glClear((int) 1024);
		GL11.glClearStencil((int) 15);
		GL11.glStencilFunc((int) 512, (int) 1, (int) 15);
		GL11.glStencilOp((int) 7681, (int) 7681, (int) 7681);
		GL11.glPolygonMode((int) 1032, (int) 6913);
	}

	public static void outlineTwo() {
		GL11.glStencilFunc((int) 512, (int) 0, (int) 15);
		GL11.glStencilOp((int) 7681, (int) 7681, (int) 7681);
		GL11.glPolygonMode((int) 1032, (int) 6914);
	}

	public static void outlineThree() {
		GL11.glStencilFunc((int) 514, (int) 1, (int) 15);
		GL11.glStencilOp((int) 7680, (int) 7680, (int) 7680);
		GL11.glPolygonMode((int) 1032, (int) 6913);
	}

	public static void outlineFour() {
		GL11.glDepthMask((boolean) false);
		GL11.glDisable((int) 2929);
		GL11.glEnable((int) 10754);
		GL11.glPolygonOffset((float) 1.0f, (float) -2000000.0f);
		GL11.glColor4f((float) 0.9529412f, (float) 0.6117647f, (float) 0.07058824f, (float) 1.0f);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
	}

	public static void outlineFive() {
		GL11.glPolygonOffset((float) 1.0f, (float) 2000000.0f);
		GL11.glDisable((int) 10754);
		GL11.glEnable((int) 2929);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 2960);
		GL11.glDisable((int) 2848);
		GL11.glHint((int) 3154, (int) 4352);
		GL11.glEnable((int) 3042);
		GL11.glEnable((int) 2896);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 3008);
		GL11.glPopAttrib();
	}

	public static void renderCircle(final double x, final double y, final double z, final double radius,
			final float lineWidth, final Color color, final float opacity) {
		GlStateManager.pushMatrix();
		GL11.glLineWidth(lineWidth + 1.2f);
		GL11.glBegin(1);
		for (int i = 0; i <= 90; ++i) {
			final double angle = i * ((3.141592653589793 * 2) / 45);
			GL11.glColor4f((float) 0, (float) 0, (float) 0, opacity);
			GL11.glVertex3d(x + radius * Math.cos(angle), y, z + radius * Math.sin(angle));

		}
		GL11.glEnd();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GL11.glLineWidth(lineWidth);
		GL11.glBegin(1);
		for (int i = 0; i <= 90; ++i) {
			final double angle = i * ((3.141592653589793 * 2) / 45);
			GL11.glColor4f((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255,
					opacity);
			GL11.glVertex3d(x + radius * Math.cos(angle), y, z + radius * Math.sin(angle));
		}
		GL11.glEnd();
		GlStateManager.popMatrix();
	}

	public static void makeScissorBox(final float x, final float y, final float x2, final float y2) {
		final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		final int factor = scaledResolution.getScaleFactor();
		GL11.glScissor((int) (x * factor), (int) ((scaledResolution.getScaledHeight() - y2) * factor),
				(int) ((x2 - x) * factor), (int) ((y2 - y) * factor));
	}

	public final static void rectCentered(double x, double y, double width, double height, Color color) {
		x -= width / 2;
		y -= height / 2;
		rect(x, y, width, height, true, color);
	}

	public final static void rect(double x, double y, double width, double height, Color color) {
		rect(x, y, width, height, true, color);
	}

	public final static void rect(double x, double y, double width, double height, boolean filled, Color color) {
		start();
		if (color != null)
			color(color);
		begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINES);
		{
			vertex(x, y);
			vertex(x + width, y);
			vertex(x + width, y + height);
			vertex(x, y + height);
			if (!filled) {
				vertex(x, y);
				vertex(x, y + height);
				vertex(x + width, y);
				vertex(x + width, y + height);
			}
		}
		end();
		stop();
	}

	public final static void begin(int glMode) {
		GL11.glBegin(glMode);
	}

	public final static void end() {
		GL11.glEnd();
	}

	public final static void vertex(double x, double y) {
		GL11.glVertex2d(x, y);
	}

	public final void push() {
		GL11.glPushMatrix();
	}

	public final void pop() {
		GL11.glPopMatrix();
	}

	public final static void enable(int glTarget) {
		GL11.glEnable(glTarget);
	}

	public final static void disable(int glTarget) {
		GL11.glDisable(glTarget);
	}

	public final static void start() {
		enable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		disable(GL11.GL_TEXTURE_2D);
		disable(GL11.GL_CULL_FACE);
		GlStateManager.disableAlpha();
	}

	public final static void stop() {
		GlStateManager.enableAlpha();
		enable(GL11.GL_CULL_FACE);
		enable(GL11.GL_TEXTURE_2D);
		disable(GL11.GL_BLEND);
		color(Color.white);
	}

	public final void startSmooth() {
		enable(GL11.GL_POLYGON_SMOOTH);
		enable(GL11.GL_LINE_SMOOTH);
		enable(GL11.GL_POINT_SMOOTH);
	}

	public final void endSmooth() {
		disable(GL11.GL_POINT_SMOOTH);
		disable(GL11.GL_LINE_SMOOTH);
		disable(GL11.GL_POLYGON_SMOOTH);
	}

	public final static void color(Color color) {
		if (color == null)
			color = Color.white;
		color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
	}

	public final static void color(double red, double green, double blue, double alpha) {
		GL11.glColor4d(red, green, blue, alpha);
	}

	public static boolean isHovering(final float mouseX, final float mouseY, final float xLeft, final float yUp,
			final float xRight, final float yBottom) {
		return mouseX > xLeft && mouseX < xRight && mouseY > yUp && mouseY < yBottom;
	}

	public static void drawBorderedCircle(double x, double y, float radius, int outsideC, int insideC) {
		// GL11.glEnable((int)3042);
		GL11.glDisable((int) 3553);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glEnable((int) 2848);
		GL11.glPushMatrix();
		float scale = 0.1f;
		GL11.glScalef((float) 0.1f, (float) 0.1f, (float) 0.1f);
		drawCircle(x *= 10, y *= 10, radius *= 10.0f, insideC);
		// drawUnfilledCircle(x, y, radius, 1.0f, outsideC);
		GL11.glScalef((float) 10.0f, (float) 10.0f, (float) 10.0f);
		GL11.glPopMatrix();
		GL11.glEnable((int) 3553);
		// GL11.glDisable((int)3042);
		GL11.glDisable((int) 2848);
	}

}
