package cn.Power.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class Blur2pointZero {
	private static ShaderGroup blurShader;
	private static Minecraft mc = Minecraft.getMinecraft();
	private static Framebuffer buffer;
	private static int lastScale;
	private static int lastScaleWidth;
	private static int lastScaleHeight;
	private static ResourceLocation shader = new ResourceLocation("shaders/post/blur.json");

	public static void initFboAndShader() {
		try {

			(blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader)).createBindFramebuffers(mc.displayWidth, mc.displayHeight);;
			(buffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true)).setFramebufferColor(0.0f,0.0f,0.0f,0.0f);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void blurArea(int x, int y, int width, int height, float intensity) {
		ScaledResolution scale = new ScaledResolution(mc);
		int factor = scale.getScaleFactor();
		int factor2 = scale.getScaledWidth();
		int factor3 = scale.getScaledHeight();
		if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null
				|| blurShader == null) {
			initFboAndShader();
		}
		lastScale = factor;
		lastScaleWidth = factor2;
		lastScaleHeight = factor3;

		blurShader.listShaders.get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
		blurShader.listShaders.get(1).getShaderManager().getShaderUniform("Radius").set(intensity);
		
		buffer.bindFramebuffer(true);
		
		
		blurShader.listShaders.get(0).getShaderManager().getShaderUniform("BlurDir").set(0.0f, 1.0f);
		
		blurShader.listShaders.get(0).loadShader(mc.timer.renderPartialTicks / 20.0f);
		
		buffer.unbindFramebuffer();
		
		
		blurShader.listShaders.get(1).getShaderManager().getShaderUniform("BlurDir").set(1.0f, 0.0f);
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		
		GL11.glScissor(x * factor, (mc.displayHeight - (y * factor) - height * factor), width * factor,
				height * factor);
		
		blurShader.listShaders.get(1).loadShader(mc.timer.renderPartialTicks / 20.0f);

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		
		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
	}

	
}
