package cn.Power.util.shader.shaders;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL20;

import cn.Power.Client;
import cn.Power.util.shader.Shader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class BackgroundShader extends Shader {
	public final static BackgroundShader BACKGROUND_SHADER = new BackgroundShader();

	private float time;

	public BackgroundShader() {
		super("jiguang.frag");
	}

	@Override
	public void setupUniforms() {
		setupUniform("iResolution");
		setupUniform("iTime");
	}

	@Override
	public void updateUniforms() {
		Minecraft mc = Minecraft.getMinecraft();

		final ScaledResolution scaledResolution = new ScaledResolution(mc);

		final int resolutionID = getUniform("iResolution");
		if (resolutionID > -1)
			GL20.glUniform2f(resolutionID, (float) Display.getWidth(), (float) Display.getHeight());
		final int timeID = getUniform("iTime");
		if (timeID > -1)
			GL20.glUniform1f(timeID, time);

		time += 0.0001F * Client.instance.delta;
	}

}
