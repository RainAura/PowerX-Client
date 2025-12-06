package cn.Power.util;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Timer;

public class RenderUtils {
	private static final Frustum frustum = new Frustum();

	public static double interpolate(double newPos, double oldPos) {
		return oldPos + (newPos - oldPos) * (double) Minecraft.getMinecraft().timer.renderPartialTicks;
	}

	public static boolean isInFrustumView(Entity ent) {
		Entity current = Minecraft.getMinecraft().getRenderViewEntity();
		double x = RenderUtils.interpolate(current.posX, current.lastTickPosX);
		double y = RenderUtils.interpolate(current.posY, current.lastTickPosY);
		double z = RenderUtils.interpolate(current.posZ, current.lastTickPosZ);
		frustum.setPosition(x, y, z);
		if (!frustum.isBoundingBoxInFrustum(ent.getEntityBoundingBox()) && !ent.ignoreFrustumCheck) {
			return false;
		}
		return true;
	}

	public static void pre() {
		GL11.glDisable((int) 2929);
		GL11.glDisable((int) 3553);
		GL11.glEnable((int) 3042);
		GL11.glBlendFunc((int) 770, (int) 771);
	}

	public static void post() {
		GL11.glDisable((int) 3042);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glColor3d((double) 1.0, (double) 1.0, (double) 1.0);
	}

	public static int getHexRGB(int hex) {
		return -16777216 | hex;
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
		// GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glShadeModel(7424);
		GL11.glColor4d(255, 255, 255, 255);
	}

	public static void drawBordered1(double x, double y, double width, double height, double length, int innerColor,
			int outerColor) {
		Gui.drawRect(x, y, x + width, y + height, innerColor);
		Gui.drawRect(x, y, x, y, outerColor);
	}

	public static int withTransparency(int rgb, float alpha) {
		float r = (float) (rgb >> 16 & 255) / 255.0f;
		float g = (float) (rgb >> 8 & 255) / 255.0f;
		float b = (float) (rgb >> 0 & 255) / 255.0f;
		return new Color(r, g, b, alpha).getRGB();
	}

	public static final class Stencil {
		private static final Stencil INSTANCE = new Stencil();
		private final HashMap<Integer, StencilFunc> stencilFuncs = new HashMap();
		private int layers = 1;
		private boolean renderMask;

		public static Stencil getInstance() {
			return INSTANCE;
		}

		public void setRenderMask(boolean renderMask) {
			this.renderMask = renderMask;
		}

		public static void checkSetupFBO() {
			Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
			if (fbo != null && fbo.depthBuffer > -1) {
				EXTFramebufferObject.glDeleteRenderbuffersEXT((int) fbo.depthBuffer);
				int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
				EXTFramebufferObject.glBindRenderbufferEXT((int) 36161, (int) stencil_depth_buffer_ID);
				EXTFramebufferObject.glRenderbufferStorageEXT((int) 36161, (int) 34041,
						(int) Minecraft.getMinecraft().displayWidth, (int) Minecraft.getMinecraft().displayHeight);
				EXTFramebufferObject.glFramebufferRenderbufferEXT((int) 36160, (int) 36128, (int) 36161,
						(int) stencil_depth_buffer_ID);
				EXTFramebufferObject.glFramebufferRenderbufferEXT((int) 36160, (int) 36096, (int) 36161,
						(int) stencil_depth_buffer_ID);
				fbo.depthBuffer = -1;
			}
		}

		public static void setupFBO(Framebuffer fbo) {
			EXTFramebufferObject.glDeleteRenderbuffersEXT((int) fbo.depthBuffer);
			int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
			EXTFramebufferObject.glBindRenderbufferEXT((int) 36161, (int) stencil_depth_buffer_ID);
			EXTFramebufferObject.glRenderbufferStorageEXT((int) 36161, (int) 34041,
					(int) Minecraft.getMinecraft().displayWidth, (int) Minecraft.getMinecraft().displayHeight);
			EXTFramebufferObject.glFramebufferRenderbufferEXT((int) 36160, (int) 36128, (int) 36161,
					(int) stencil_depth_buffer_ID);
			EXTFramebufferObject.glFramebufferRenderbufferEXT((int) 36160, (int) 36096, (int) 36161,
					(int) stencil_depth_buffer_ID);
		}

		public void startLayer() {
			if (this.layers == 1) {
				GL11.glClearStencil((int) 0);
				GL11.glClear((int) 1024);
			}
			GL11.glEnable((int) 2960);
			++this.layers;
			if (this.layers > this.getMaximumLayers()) {
				System.out.println("StencilUtil: Reached maximum amount of layers!");
				this.layers = 1;
			}
		}

		public void stopLayer() {
			if (this.layers == 1) {
				System.out.println("StencilUtil: No layers found!");
				return;
			}
			--this.layers;
			if (this.layers == 1) {
				GL11.glDisable((int) 2960);
			} else {
				StencilFunc lastStencilFunc = this.stencilFuncs.remove(this.layers);
				if (lastStencilFunc != null) {
					lastStencilFunc.use();
				}
			}
		}

		public void clear() {
			GL11.glClearStencil((int) 0);
			GL11.glClear((int) 1024);
			this.stencilFuncs.clear();
			this.layers = 1;
		}

		public void setBuffer() {
			this.setStencilFunc(new StencilFunc(this, this.renderMask ? 519 : 512, this.layers, this.getMaximumLayers(),
					7681, 7680, 7680));
		}

		public void setBuffer(boolean set) {
			this.setStencilFunc(new StencilFunc(this, this.renderMask ? 519 : 512, set ? this.layers : this.layers - 1,
					this.getMaximumLayers(), 7681, 7681, 7681));
		}

		public void cropOutside() {
			this.setStencilFunc(new StencilFunc(this, 517, this.layers, this.getMaximumLayers(), 7680, 7680, 7680));
		}

		public void cropInside() {
			this.setStencilFunc(new StencilFunc(this, 514, this.layers, this.getMaximumLayers(), 7680, 7680, 7680));
		}

		public void setStencilFunc(StencilFunc stencilFunc) {
			GL11.glStencilFunc((int) StencilFunc.func_func, (int) StencilFunc.func_ref, (int) StencilFunc.func_mask);
			GL11.glStencilOp((int) StencilFunc.op_fail, (int) StencilFunc.op_zfail, (int) StencilFunc.op_zpass);
			this.stencilFuncs.put(this.layers, stencilFunc);
		}

		public StencilFunc getStencilFunc() {
			return this.stencilFuncs.get(this.layers);
		}

		public int getLayer() {
			return this.layers;
		}

		public int getStencilBufferSize() {
			return GL11.glGetInteger((int) 3415);
		}

		public int getMaximumLayers() {
			return (int) (Math.pow(2.0, this.getStencilBufferSize()) - 1.0);
		}

		public void createCirlce(double x, double y, double radius) {
			GL11.glBegin((int) 6);
			for (int i = 0; i <= 360; ++i) {
				double sin = Math.sin((double) i * 3.141592653589793 / 180.0) * radius;
				double cos = Math.cos((double) i * 3.141592653589793 / 180.0) * radius;
				GL11.glVertex2d((double) (x + sin), (double) (y + cos));
			}
			GL11.glEnd();
		}

		public void createRect(double x, double y, double x2, double y2) {
			GL11.glBegin((int) 7);
			GL11.glVertex2d((double) x, (double) y2);
			GL11.glVertex2d((double) x2, (double) y2);
			GL11.glVertex2d((double) x2, (double) y);
			GL11.glVertex2d((double) x, (double) y);
			GL11.glEnd();
		}

		public static class StencilFunc {
			public static int func_func;
			public static int func_ref;
			public static int func_mask;
			public static int op_fail;
			public static int op_zfail;
			public static int op_zpass;

			public StencilFunc(Stencil paramStencil, int func_func, int func_ref, int func_mask, int op_fail,
					int op_zfail, int op_zpass) {
				StencilFunc.func_func = func_func;
				StencilFunc.func_ref = func_ref;
				StencilFunc.func_mask = func_mask;
				StencilFunc.op_fail = op_fail;
				StencilFunc.op_zfail = op_zfail;
				StencilFunc.op_zpass = op_zpass;
			}

			public void use() {
				GL11.glStencilFunc((int) func_func, (int) func_ref, (int) func_mask);
				GL11.glStencilOp((int) op_fail, (int) op_zfail, (int) op_zpass);
			}
		}

	}

	public static class R3DUtils {
		public static void startDrawing() {
			GL11.glEnable((int) 3042);
			GL11.glEnable((int) 3042);
			GL11.glBlendFunc((int) 770, (int) 771);
			GL11.glEnable((int) 2848);
			GL11.glDisable((int) 3553);
			GL11.glDisable((int) 2929);
			Minecraft.getMinecraft().entityRenderer
					.setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks, 0);
		}

		public static void stopDrawing() {
			GL11.glDisable((int) 3042);
			GL11.glEnable((int) 3553);
			GL11.glDisable((int) 2848);
			GL11.glDisable((int) 3042);
			GL11.glEnable((int) 2929);
		}

		public void drawRombo(double x, double y, double z) {
			Minecraft.getMinecraft().entityRenderer
					.setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks, 0);
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) (x + 0.5), (double) (y += 1.0), (double) z);
			GL11.glVertex3d((double) x, (double) (y + 1.0), (double) z);
			GL11.glVertex3d((double) x, (double) y, (double) (z + 0.5));
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) x, (double) y, (double) (z + 0.5));
			GL11.glVertex3d((double) x, (double) (y + 1.0), (double) z);
			GL11.glVertex3d((double) (x + 0.5), (double) y, (double) z);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) x, (double) y, (double) (z - 0.5));
			GL11.glVertex3d((double) x, (double) (y + 1.0), (double) z);
			GL11.glVertex3d((double) (x - 0.5), (double) y, (double) z);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) (x - 0.5), (double) y, (double) z);
			GL11.glVertex3d((double) x, (double) (y + 1.0), (double) z);
			GL11.glVertex3d((double) x, (double) y, (double) (z - 0.5));
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) (x + 0.5), (double) y, (double) z);
			GL11.glVertex3d((double) x, (double) (y - 1.0), (double) z);
			GL11.glVertex3d((double) x, (double) y, (double) (z + 0.5));
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) x, (double) y, (double) (z + 0.5));
			GL11.glVertex3d((double) x, (double) (y - 1.0), (double) z);
			GL11.glVertex3d((double) (x + 0.5), (double) y, (double) z);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) x, (double) y, (double) (z - 0.5));
			GL11.glVertex3d((double) x, (double) (y - 1.0), (double) z);
			GL11.glVertex3d((double) (x - 0.5), (double) y, (double) z);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) (x - 0.5), (double) y, (double) z);
			GL11.glVertex3d((double) x, (double) (y - 1.0), (double) z);
			GL11.glVertex3d((double) x, (double) y, (double) (z - 0.5));
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) x, (double) y, (double) (z - 0.5));
			GL11.glVertex3d((double) x, (double) (y + 1.0), (double) z);
			GL11.glVertex3d((double) (x + 0.5), (double) y, (double) z);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) (x + 0.5), (double) y, (double) z);
			GL11.glVertex3d((double) x, (double) (y + 1.0), (double) z);
			GL11.glVertex3d((double) x, (double) y, (double) (z - 0.5));
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) x, (double) y, (double) (z + 0.5));
			GL11.glVertex3d((double) x, (double) (y + 1.0), (double) z);
			GL11.glVertex3d((double) (x - 0.5), (double) y, (double) z);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) (x - 0.5), (double) y, (double) z);
			GL11.glVertex3d((double) x, (double) (y + 1.0), (double) z);
			GL11.glVertex3d((double) x, (double) y, (double) (z + 0.5));
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) x, (double) y, (double) (z - 0.5));
			GL11.glVertex3d((double) x, (double) (y - 1.0), (double) z);
			GL11.glVertex3d((double) (x + 0.5), (double) y, (double) z);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) (x + 0.5), (double) y, (double) z);
			GL11.glVertex3d((double) x, (double) (y - 1.0), (double) z);
			GL11.glVertex3d((double) x, (double) y, (double) (z - 0.5));
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) x, (double) y, (double) (z + 0.5));
			GL11.glVertex3d((double) x, (double) (y - 1.0), (double) z);
			GL11.glVertex3d((double) (x - 0.5), (double) y, (double) z);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) (x - 0.5), (double) y, (double) z);
			GL11.glVertex3d((double) x, (double) (y - 1.0), (double) z);
			GL11.glVertex3d((double) x, (double) y, (double) (z + 0.5));
			GL11.glEnd();
		}

		public static void drawFilledBox(AxisAlignedBB mask) {
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
			GL11.glEnd();
			GL11.glBegin((int) 4);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
			GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
			GL11.glEnd();
		}

		public static void drawOutlinedBoundingBox(AxisAlignedBB aabb) {
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.minZ);
			GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.minZ);
			GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.maxZ);
			GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.maxZ);
			GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.minZ);
			GL11.glEnd();
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.minZ);
			GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.minZ);
			GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.maxZ);
			GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.maxZ);
			GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.minZ);
			GL11.glEnd();
			GL11.glBegin((int) 1);
			GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.minZ);
			GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.minZ);
			GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.minZ);
			GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.minZ);
			GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.maxZ);
			GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.maxZ);
			GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.maxZ);
			GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.maxZ);
			GL11.glEnd();
		}

		public static void drawOutlinedBox(AxisAlignedBB box) {
			if (box == null) {
				return;
			}
			Minecraft.getMinecraft().entityRenderer
					.setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks, 0);
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
			GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
			GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
			GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
			GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
			GL11.glEnd();
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
			GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
			GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
			GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
			GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
			GL11.glEnd();
			GL11.glBegin((int) 1);
			GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
			GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
			GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
			GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
			GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
			GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
			GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
			GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
			GL11.glEnd();
		}

		public static void drawTracerLine1(double x, double y, double z, float red, float green, float blue,
				float alpha, float lineWidth) {
			boolean temp = Minecraft.getMinecraft().gameSettings.viewBobbing;
			Minecraft.getMinecraft().gameSettings.viewBobbing = false;
			EntityRenderer var10000 = Minecraft.getMinecraft().entityRenderer;
			Timer var10001 = Minecraft.getMinecraft().timer;
			var10000.setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks, 2);
			Minecraft.getMinecraft().gameSettings.viewBobbing = temp;
			GL11.glPushMatrix();
			GL11.glEnable((int) 3042);
			GL11.glEnable((int) 2848);
			GL11.glDisable((int) 2929);
			GL11.glDisable((int) 3553);
			GL11.glBlendFunc((int) 770, (int) 771);
			GL11.glEnable((int) 3042);
			GL11.glLineWidth((float) 1.0f);
			GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
			GL11.glBegin((int) 2);
			Minecraft.getMinecraft();
			Minecraft.getMinecraft();
			GL11.glVertex3d((double) 0.0, (double) (0.0 + (double) Minecraft.thePlayer.getEyeHeight()), (double) 0.0);
			GL11.glVertex3d((double) x, (double) y, (double) z);
			GL11.glEnd();
			GL11.glDisable((int) 3042);
			GL11.glEnable((int) 3553);
			GL11.glEnable((int) 2929);
			GL11.glDisable((int) 2848);
			GL11.glDisable((int) 3042);
			GL11.glPopMatrix();
		}

		public static int getBlockColor(Block block) {
			int color = 0;
			switch (Block.getIdFromBlock(block)) {
			case 14:
			case 41: {
				color = -1711477173;
				break;
			}
			case 15:
			case 42: {
				color = -1715420992;
				break;
			}
			case 16:
			case 173: {
				color = -1724434633;
				break;
			}
			case 21:
			case 22: {
				color = -1726527803;
				break;
			}
			case 49: {
				color = -1724108714;
				break;
			}
			case 54:
			case 146: {
				color = -1711292672;
				break;
			}
			case 56:
			case 57:
			case 138: {
				color = -1721897739;
				break;
			}
			case 61:
			case 62: {
				color = -1711395081;
				break;
			}
			case 73:
			case 74:
			case 152: {
				color = -1711341568;
				break;
			}
			case 89: {
				color = -1712594866;
				break;
			}
			case 129:
			case 133: {
				color = -1726489246;
				break;
			}
			case 130: {
				color = -1713438249;
				break;
			}
			case 52: {
				color = 805728308;
				break;
			}
			default: {
				color = -1711276033;
			}
			}
			return color == 0 ? 806752583 : color;
		}
	}

	public static class R2DUtils {
		public static void enableGL2D() {
			GL11.glDisable((int) 2929);
			GL11.glEnable((int) 3042);
			GL11.glDisable((int) 3553);
			GL11.glBlendFunc((int) 770, (int) 771);
			GL11.glDepthMask((boolean) true);
			GL11.glEnable((int) 2848);
			GL11.glHint((int) 3154, (int) 4354);
			GL11.glHint((int) 3155, (int) 4354);
		}

		public static void disableGL2D() {
			GL11.glEnable((int) 3553);
			GL11.glDisable((int) 3042);
			GL11.glEnable((int) 2929);
			GL11.glDisable((int) 2848);
			GL11.glHint((int) 3154, (int) 4352);
			GL11.glHint((int) 3155, (int) 4352);
		}

		public static void draw2DCorner(Entity e, double posX, double posY, double posZ, int color) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(posX, posY, posZ);
			GL11.glNormal3f((float) 0.0f, (float) 0.0f, (float) 0.0f);
			GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
			GlStateManager.scale(-0.1, -0.1, 0.1);
			GL11.glDisable((int) 2896);
			GL11.glDisable((int) 2929);
			GL11.glEnable((int) 3042);
			GL11.glBlendFunc((int) 770, (int) 771);
			GlStateManager.depthMask(true);
			R2DUtils.drawRect(7.0, -20.0, 7.300000190734863, -17.5, color);
			R2DUtils.drawRect(-7.300000190734863, -20.0, -7.0, -17.5, color);
			R2DUtils.drawRect(4.0, -20.299999237060547, 7.300000190734863, -20.0, color);
			R2DUtils.drawRect(-7.300000190734863, -20.299999237060547, -4.0, -20.0, color);
			R2DUtils.drawRect(-7.0, 3.0, -4.0, 3.299999952316284, color);
			R2DUtils.drawRect(4.0, 3.0, 7.0, 3.299999952316284, color);
			R2DUtils.drawRect(-7.300000190734863, 0.8, -7.0, 3.299999952316284, color);
			R2DUtils.drawRect(7.0, 0.5, 7.300000190734863, 3.299999952316284, color);
			R2DUtils.drawRect(7.0, -20.0, 7.300000190734863, -17.5, color);
			R2DUtils.drawRect(-7.300000190734863, -20.0, -7.0, -17.5, color);
			R2DUtils.drawRect(4.0, -20.299999237060547, 7.300000190734863, -20.0, color);
			R2DUtils.drawRect(-7.300000190734863, -20.299999237060547, -4.0, -20.0, color);
			R2DUtils.drawRect(-7.0, 3.0, -4.0, 3.299999952316284, color);
			R2DUtils.drawRect(4.0, 3.0, 7.0, 3.299999952316284, color);
			R2DUtils.drawRect(-7.300000190734863, 0.8, -7.0, 3.299999952316284, color);
			R2DUtils.drawRect(7.0, 0.5, 7.300000190734863, 3.299999952316284, color);
			GL11.glDisable((int) 3042);
			GL11.glEnable((int) 2929);
			GlStateManager.popMatrix();
		}

		public static void drawRect(double x2, double y2, double x1, double y1, int color) {
			R2DUtils.enableGL2D();
			R2DUtils.glColor(color);
			R2DUtils.drawRect(x2, y2, x1, y1);
			R2DUtils.disableGL2D();
		}

		private static void drawRect(double x2, double y2, double x1, double y1) {
			GL11.glBegin((int) 7);
			GL11.glVertex2d((double) x2, (double) y1);
			GL11.glVertex2d((double) x1, (double) y1);
			GL11.glVertex2d((double) x1, (double) y2);
			GL11.glVertex2d((double) x2, (double) y2);
			GL11.glEnd();
		}

		public static void glColor(int hex) {
			float alpha = (float) (hex >> 24 & 255) / 255.0f;
			float red = (float) (hex >> 16 & 255) / 255.0f;
			float green = (float) (hex >> 8 & 255) / 255.0f;
			float blue = (float) (hex & 255) / 255.0f;
			GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
		}

		public static void drawRect(float x, float y, float x1, float y1, int color) {
			R2DUtils.enableGL2D();
			Helper.colorUtils().glColor(color);
			R2DUtils.drawRect(x, y, x1, y1);
			R2DUtils.disableGL2D();
		}

		public static void drawBorderedRect(float x, float y, float x1, float y1, float width, int borderColor) {
			R2DUtils.enableGL2D();
			Helper.colorUtils().glColor(borderColor);
			R2DUtils.drawRect(x + width, y, x1 - width, y + width);
			R2DUtils.drawRect(x, y, x + width, y1);
			R2DUtils.drawRect(x1 - width, y, x1, y1);
			R2DUtils.drawRect(x + width, y1 - width, x1 - width, y1);
			R2DUtils.disableGL2D();
		}

		public static void drawBorderedRect(float x, float y, float x1, float y1, int insideC, int borderC) {
			R2DUtils.enableGL2D();
			GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
			R2DUtils.drawVLine(x *= 2.0f, y *= 2.0f, y1 *= 2.0f, borderC);
			R2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y, y1, borderC);
			R2DUtils.drawHLine(x, x1 - 1.0f, y, borderC);
			R2DUtils.drawHLine(x, x1 - 2.0f, y1 - 1.0f, borderC);
			R2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
			GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
			R2DUtils.disableGL2D();
		}

		public static void drawGradientRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
			R2DUtils.enableGL2D();
			GL11.glShadeModel((int) 7425);
			GL11.glBegin((int) 7);
			Helper.colorUtils().glColor(topColor);
			GL11.glVertex2f((float) x, (float) y1);
			GL11.glVertex2f((float) x1, (float) y1);
			Helper.colorUtils().glColor(bottomColor);
			GL11.glVertex2f((float) x1, (float) y);
			GL11.glVertex2f((float) x, (float) y);
			GL11.glEnd();
			GL11.glShadeModel((int) 7424);
			R2DUtils.disableGL2D();
		}

		public static void drawHLine(float x, float y, float x1, int y1) {
			if (y < x) {
				float var5 = x;
				x = y;
				y = var5;
			}
			R2DUtils.drawRect(x, x1, y + 1.0f, x1 + 1.0f, y1);
		}

		public static void drawVLine(float x, float y, float x1, int y1) {
			if (x1 < y) {
				float var5 = y;
				y = x1;
				x1 = var5;
			}
			R2DUtils.drawRect(x, y + 1.0f, x + 1.0f, x1, y1);
		}

		public static void drawHLine(float x, float y, float x1, int y1, int y2) {
			if (y < x) {
				float var5 = x;
				x = y;
				y = var5;
			}
			R2DUtils.drawGradientRect(x, x1, y + 1.0f, x1 + 1.0f, y1, y2);
		}

		public static void drawRect(float x, float y, float x1, float y1) {
			GL11.glBegin((int) 7);
			GL11.glVertex2f((float) x, (float) y1);
			GL11.glVertex2f((float) x1, (float) y1);
			GL11.glVertex2f((float) x1, (float) y);
			GL11.glVertex2f((float) x, (float) y);
			GL11.glEnd();
		}

		public static void drawTri(double x1, double y1, double x2, double y2, double x3, double y3, double width,
				Color c) {
			GL11.glEnable((int) 3042);
			GL11.glDisable((int) 3553);
			GL11.glEnable((int) 2848);
			GL11.glBlendFunc((int) 770, (int) 771);
			Helper.colorUtils().glColor(c);
			GL11.glLineWidth((float) ((float) width));
			GL11.glBegin((int) 3);
			GL11.glVertex2d((double) x1, (double) y1);
			GL11.glVertex2d((double) x2, (double) y2);
			GL11.glVertex2d((double) x3, (double) y3);
			GL11.glEnd();
			GL11.glDisable((int) 2848);
			GL11.glEnable((int) 3553);
			GL11.glDisable((int) 3042);
		}
	}

	public static class ColorUtils {
		public int RGBtoHEX(int r, int g, int b, int a) {
			return (a << 24) + (r << 16) + (g << 8) + b;
		}

		public static Color getRainbow(long offset, float fade) {
			float hue = (float) (System.nanoTime() + offset) / 8.0E9f % 1.0f;
			long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
			Color c = new Color((int) color);
			return new Color((float) c.getRed() / 255.0f * fade, (float) c.getGreen() / 255.0f * fade,
					(float) c.getBlue() / 255.0f * fade, (float) c.getAlpha() / 255.0f);
		}

		public static Color blend(Color color1, Color color2, double ratio) {
			float r = (float) ratio;
			float ir = 1.0f - r;
			float[] rgb1 = new float[3];
			float[] rgb2 = new float[3];
			color1.getColorComponents(rgb1);
			color2.getColorComponents(rgb2);
			Color color = new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir);
			return color;
		}

		public static Color glColor(int color, float alpha) {
			float red = (float) (color >> 16 & 255) / 255.0f;
			float green = (float) (color >> 8 & 255) / 255.0f;
			float blue = (float) (color & 255) / 255.0f;
			GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
			return new Color(red, green, blue, alpha);
		}

		public void glColor(Color color) {
			GL11.glColor4f((float) ((float) color.getRed() / 255.0f), (float) ((float) color.getGreen() / 255.0f),
					(float) ((float) color.getBlue() / 255.0f), (float) ((float) color.getAlpha() / 255.0f));
		}

		public Color glColor(int hex) {
			float alpha = (float) (hex >> 24 & 255) / 256.0f;
			float red = (float) (hex >> 16 & 255) / 255.0f;
			float green = (float) (hex >> 8 & 255) / 255.0f;
			float blue = (float) (hex & 255) / 255.0f;
			GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
			return new Color(red, green, blue, alpha);
		}

		public Color glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
			float red = 0.003921569f * (float) redRGB;
			float green = 0.003921569f * (float) greenRGB;
			float blue = 0.003921569f * (float) blueRGB;
			GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
			return new Color(red, green, blue, alpha);
		}

		public static int transparency(int color, double alpha) {
			Color c = new Color(color);
			float r = 0.003921569f * (float) c.getRed();
			float g = 0.003921569f * (float) c.getGreen();
			float b = 0.003921569f * (float) c.getBlue();
			return new Color(r, g, b, (float) alpha).getRGB();
		}

		public static float[] getRGBA(int color) {
			float a = (float) (color >> 24 & 255) / 255.0f;
			float r = (float) (color >> 16 & 255) / 255.0f;
			float g = (float) (color >> 8 & 255) / 255.0f;
			float b = (float) (color & 255) / 255.0f;
			return new float[] { r, g, b, a };
		}

		public static int intFromHex(String hex) {
			try {
				return Integer.parseInt(hex, 15);
			} catch (NumberFormatException e) {
				return -1;
			}
		}

		public static String hexFromInt(int color) {
			return ColorUtils.hexFromInt(new Color(color));
		}

		public static String hexFromInt(Color color) {
			return Integer.toHexString(color.getRGB()).substring(2);
		}
	}

	public static class Camera {
		private final Minecraft mc = Minecraft.getMinecraft();
		private Timer timer;
		private double posX;
		private double posY;
		private double posZ;
		private float rotationYaw;
		private float rotationPitch;

		/*
		 * Enabled aggressive block sorting
		 */
		public Camera(Entity entity) {
			if (this.timer == null) {
				this.timer = this.mc.timer;
			}
			this.posX = entity.lastTickPosX
					+ (entity.posX - entity.lastTickPosX) * (double) this.timer.renderPartialTicks;
			this.posY = entity.lastTickPosY
					+ (entity.posY - entity.lastTickPosY) * (double) this.timer.renderPartialTicks;
			this.posZ = entity.lastTickPosZ
					+ (entity.posZ - entity.lastTickPosZ) * (double) this.timer.renderPartialTicks;
			this.setRotationYaw(entity.rotationYaw);
			this.setRotationPitch(entity.rotationPitch);
			if (entity instanceof EntityPlayer && Minecraft.getMinecraft().gameSettings.viewBobbing) {
				Minecraft.getMinecraft();
				if (entity == Minecraft.thePlayer) {
					EntityPlayer living1 = (EntityPlayer) entity;
					this.setRotationYaw(this.getRotationYaw() + living1.prevCameraYaw
							+ (living1.cameraYaw - living1.prevCameraYaw) * this.timer.renderPartialTicks);
					this.setRotationPitch(this.getRotationPitch() + living1.prevCameraPitch
							+ (living1.cameraPitch - living1.prevCameraPitch) * this.timer.renderPartialTicks);
					return;
				}
			}
			if (!(entity instanceof EntityLivingBase))
				return;
			EntityLivingBase living2 = (EntityLivingBase) entity;
			this.setRotationYaw(living2.rotationYawHead);
		}

		public Camera(Entity entity, double offsetX, double offsetY, double offsetZ, double offsetRotationYaw,
				double offsetRotationPitch) {
			this.posX = entity.lastTickPosX
					+ (entity.posX - entity.lastTickPosX) * (double) this.timer.renderPartialTicks;
			this.posY = entity.lastTickPosY
					+ (entity.posY - entity.lastTickPosY) * (double) this.timer.renderPartialTicks;
			this.posZ = entity.lastTickPosZ
					+ (entity.posZ - entity.lastTickPosZ) * (double) this.timer.renderPartialTicks;
			this.setRotationYaw(entity.rotationYaw);
			this.setRotationPitch(entity.rotationPitch);
			if (entity instanceof EntityPlayer && Minecraft.getMinecraft().gameSettings.viewBobbing) {
				Minecraft.getMinecraft();
				if (entity == Minecraft.thePlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					this.setRotationYaw(this.getRotationYaw() + player.prevCameraYaw
							+ (player.cameraYaw - player.prevCameraYaw) * this.timer.renderPartialTicks);
					this.setRotationPitch(this.getRotationPitch() + player.prevCameraPitch
							+ (player.cameraPitch - player.prevCameraPitch) * this.timer.renderPartialTicks);
				}
			}
			this.posX += offsetX;
			this.posY += offsetY;
			this.posZ += offsetZ;
			this.rotationYaw += (float) offsetRotationYaw;
			this.rotationPitch += (float) offsetRotationPitch;
		}

		public Camera(double posX, double posY, double posZ, float rotationYaw, float rotationPitch) {
			this.setPosX(posX);
			this.posY = posY;
			this.posZ = posZ;
			this.setRotationYaw(rotationYaw);
			this.setRotationPitch(rotationPitch);
		}

		public double getPosX() {
			return this.posX;
		}

		public void setPosX(double posX) {
			this.posX = posX;
		}

		public double getPosY() {
			return this.posY;
		}

		public void setPosY(double posY) {
			this.posY = posY;
		}

		public double getPosZ() {
			return this.posZ;
		}

		public void setPosZ(double posZ) {
			this.posZ = posZ;
		}

		public float getRotationYaw() {
			return this.rotationYaw;
		}

		public void setRotationYaw(float rotationYaw) {
			this.rotationYaw = rotationYaw;
		}

		public float getRotationPitch() {
			return this.rotationPitch;
		}

		public void setRotationPitch(float rotationPitch) {
			this.rotationPitch = rotationPitch;
		}

		public static float[] getRotation(double posX1, double posY1, double posZ1, double posX2, double posY2,
				double posZ2) {
			float[] rotation = new float[2];
			double diffX = posX2 - posX1;
			double diffZ = posZ2 - posZ1;
			double diffY = posY2 - posY1;
			double dist = Math.sqrt(diffZ * diffZ + diffX * diffX);
			double pitch = -Math.toDegrees(Math.atan(diffY / dist));
			rotation[1] = (float) pitch;
			double yaw = 0.0;
			if (diffZ >= 0.0 && diffX >= 0.0) {
				yaw = Math.toDegrees(-Math.atan(diffX / diffZ));
			} else if (diffZ >= 0.0 && diffX <= 0.0) {
				yaw = Math.toDegrees(-Math.atan(diffX / diffZ));
			} else if (diffZ <= 0.0 && diffX >= 0.0) {
				yaw = -90.0 + Math.toDegrees(Math.atan(diffZ / diffX));
			} else if (diffZ <= 0.0 && diffX <= 0.0) {
				yaw = 90.0 + Math.toDegrees(Math.atan(diffZ / diffX));
			}
			rotation[0] = (float) yaw;
			return rotation;
		}
	}

	public static void drawRect(final int x, final int y, final int x1, final int y1, final int color) {
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
		Gui.drawRect(x, y, x1, y1, color);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
	}

	public static void drawBorderRect2(final double left, final double top, final double right, final double bottom,
			final int bwidth, final int icolor, final int bcolor) {
		Gui.drawRect(left + bwidth, top + bwidth, right - bwidth, bottom - bwidth, icolor);
		Gui.drawRect(left, top, left + bwidth, bottom, bcolor);
		Gui.drawRect(left + bwidth, top, right, top + bwidth, bcolor);
		Gui.drawRect(left + bwidth, bottom - bwidth, right, bottom, bcolor);
		Gui.drawRect(right - bwidth, top + bwidth, right, bottom - bwidth, bcolor);
	}

	public static void drawBorderedRect(double x, double y, double x2, double y2, double thickness, int inside,
			int outline) {
		double fix = 0.0;
		if (thickness < 1.0) {
			fix = 1.0;
		}
		drawRect(x + thickness, y + thickness, x2 - thickness, y2 - thickness, inside);
		drawRect(x, y + 1.0 - fix, x + thickness, y2, outline);
		drawRect(x, y, x2 - 1.0 + fix, y + thickness, outline);
		drawRect(x2 - thickness, y, x2, y2 - 1.0 + fix, outline);
		drawRect(x + 1.0 - fix, y2 - thickness, x2, y2, outline);
	}

	public static void drawRect(double x1, double y1, double x2, double y2, int color) {
		Gui.drawRect(x1, y1, x2, y2, color);
	}

	public static void rectangleBordered(final double x, final double y, final double x1, final double y1,
			final double width, final int internalColor, final int borderColor) {
		drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		drawRect(x + width, y, x1 - width, y + width, borderColor);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		drawRect(x, y, x + width, y1, borderColor);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		drawRect(x1 - width, y, x1, y1, borderColor);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static double[] convertTo2D(double x, double y, double z) {
		FloatBuffer screenCoords = BufferUtils.createFloatBuffer((int) 3);
		IntBuffer viewport = BufferUtils.createIntBuffer((int) 16);
		FloatBuffer modelView = BufferUtils.createFloatBuffer((int) 16);
		FloatBuffer projection = BufferUtils.createFloatBuffer((int) 16);
		GL11.glGetFloat((int) 2982, (FloatBuffer) modelView);
		GL11.glGetFloat((int) 2983, (FloatBuffer) projection);
		GL11.glGetInteger((int) 2978, (IntBuffer) viewport);
		boolean result = GLU.gluProject((float) ((float) x), (float) ((float) y), (float) ((float) z),
				(FloatBuffer) modelView, (FloatBuffer) projection, (IntBuffer) viewport, (FloatBuffer) screenCoords);
		if (result) {
			return new double[] { screenCoords.get(0), (float) Display.getHeight() - screenCoords.get(1),
					screenCoords.get(2) };
		}
		return null;
	}

	public static void drawRoundedRect(float x, float y, float x1, float y1, int insideC) {
		drawRect(x + 0.5F, y, x1 - 0.5F, y + 0.5F, insideC);
		drawRect(x + 0.5F, y1 - 0.5F, x1 - 0.5F, y1, insideC);
		drawRect(x, y + 0.5F, x1, y1 - 0.5F, insideC);
	}

	public static void drawRoundedRect(int x, int y, int x1, int y1, int borderC, int insideC) {
		R2DUtils.enableGL2D();
		GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
		R2DUtils.drawVLine(x *= 2.0f, (y *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, borderC);
		R2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y + 1.0f, y1 - 2.0f, borderC);
		R2DUtils.drawHLine(x + 2.0f, x1 - 3.0f, y, borderC);
		R2DUtils.drawHLine(x + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC);
		R2DUtils.drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC);
		R2DUtils.drawHLine(x1 - 2.0f, x1 - 2.0f, y + 1.0f, borderC);
		R2DUtils.drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC);
		R2DUtils.drawHLine(x + 1.0f, x + 1.0f, y1 - 2.0f, borderC);
		R2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
		GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
		R2DUtils.disableGL2D();
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
		WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(var6, var7, var8, var11);
		worldRenderer.begin(7, DefaultVertexFormats.POSITION);
		worldRenderer.pos(left, bottom, 0.0D).endVertex();
		worldRenderer.pos(right, bottom, 0.0D).endVertex();
		worldRenderer.pos(right, top, 0.0D).endVertex();
		worldRenderer.pos(left, top, 0.0D).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1, 1);
	}

	public static void drawOutlinedBox(AxisAlignedBB box) {
		if (box == null) {
			return;
		}
		Minecraft.getMinecraft().entityRenderer.setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks,
				0);
		GL11.glBegin((int) 3);
		GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
		GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
		GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
		GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
		GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
		GL11.glEnd();
		GL11.glBegin((int) 3);
		GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
		GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
		GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
		GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
		GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
		GL11.glEnd();
		GL11.glBegin((int) 1);
		GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
		GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
		GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
		GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
		GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
		GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
		GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
		GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
		GL11.glEnd();
	}

	public static void drawFilledBox(AxisAlignedBB mask) {
		GL11.glBegin((int) 4);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glEnd();
		GL11.glBegin((int) 4);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
		GL11.glEnd();
		GL11.glBegin((int) 4);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
		GL11.glEnd();
		GL11.glBegin((int) 4);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
		GL11.glEnd();
		GL11.glBegin((int) 4);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
		GL11.glEnd();
		GL11.glBegin((int) 4);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
		GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
		GL11.glEnd();
	}

	public static void drawCircle(float cx, float cy, float r, int num_segments, int c) {
		GL11.glPushMatrix();
		cx *= 2.0F;
		cy *= 2.0F;
		float f = (float) (c >> 24 & 255) / 255.0F;
		float f1 = (float) (c >> 16 & 255) / 255.0F;
		float f2 = (float) (c >> 8 & 255) / 255.0F;
		float f3 = (float) (c & 255) / 255.0F;
		float theta = (float) (6.2831852D / (double) num_segments);
		float p = (float) Math.cos((double) theta);
		float s = (float) Math.sin((double) theta);
		float x = r *= 2.0F;
		float y = 0.0F;
		enableGL2D();
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glBegin(2);

		for (int ii = 0; ii < num_segments; ++ii) {
			GL11.glVertex2f(x + cx, y + cy);
			float t = x;
			x = p * x - s * y;
			y = s * t + p * y;
		}

		GL11.glEnd();
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		disableGL2D();
		GL11.glPopMatrix();
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

	public static void enableGL2D() {
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
	}

	public static void disableGL2D() {
		GL11.glEnable(3553);
		// GL11.glDisable(3042);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
	}

	public static void pre3D() {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
	}

	public static void post3D() {
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
	}

	public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
		float red = 0.003921569F * redRGB;
		float green = 0.003921569F * greenRGB;
		float blue = 0.003921569F * blueRGB;
		GL11.glColor4f(red, green, blue, alpha);
	}

	public static void glColor(int hex) {
		float alpha = (hex >> 24 & 0xFF) / 255.0F;
		float red = (hex >> 16 & 0xFF) / 255.0F;
		float green = (hex >> 8 & 0xFF) / 255.0F;
		float blue = (hex & 0xFF) / 255.0F;
		GL11.glColor4f(red, green, blue, alpha);
	}

	public static void drawESPCircle(double cx, double cy, double r, double n, Color color) {
		GL11.glPushMatrix();
		cx *= 2.0;
		cy *= 2.0;
		double b = 6.2831852 / n;
		double p = Math.cos(b);
		double s = Math.sin(b);
		double x = r *= 2.0;
		double y = 0.0;
		enableGL2D();
		GlStateManager.rotate(-30, 0, 0, 1);
		GL11.glScaled(0.5, 0.5, 0.5);
		GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.5f);
		GL11.glLineWidth(1.0F);

		GL11.glBegin(2);
		double ii = 0;
		while (ii < n) {
			GL11.glVertex2d(x + cx, y + cy);
			double t = x;
			x = p * x - s * y;
			y = s * t + p * y;
			ii++;
		}
		GL11.glEnd();
		GL11.glScaled(2.0, 2.0, 2.0);
		disableGL2D();
		GL11.glPopMatrix();
	}
}
