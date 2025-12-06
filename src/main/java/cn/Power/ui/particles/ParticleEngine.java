package cn.Power.ui.particles;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import cn.Power.util.GLUtil;
import cn.Power.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class ParticleEngine {

	public CopyOnWriteArrayList<Particle> particles = Lists.newCopyOnWriteArrayList();
	public float lastMouseX;
	public float lastMouseY;
	
	public void render(float mouseX, float mouseY){
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.color(1, 1, 1, 1);
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float xOffset = sr.getScaledWidth()/2-mouseX;
        float yOffset = sr.getScaledHeight()/2-mouseY;
		for(particles.size(); particles.size() < (int)(sr.getScaledWidth()/19.2f); particles.add(new Particle(sr, new Random().nextFloat()*2 + 2, new Random().nextFloat()*5 + 5)));
		List<Particle> toremove = Lists.newArrayList();
		for(Particle p : particles){
			if(p.opacity < 40){
				p.opacity += 1;
			}
			if(p.opacity > 40){
				p.opacity = 40;
			}
			Color c = new Color((int)255, (int)255, (int)255, (int)p.opacity);
			RenderUtil.drawBorderedCircle(p.x + Math.sin(p.ticks/2)*50 + -xOffset/5, (p.ticks*p.speed)*p.ticks/10 + -yOffset/5, p.radius*(p.opacity/32), c.getRGB(), c.getRGB());
			p.ticks += 0.05;// +(0.005*1.777*(GLUtils.getMouseX()-lastMouseX) + 0.005*(GLUtils.getMouseY()-lastMouseY));
			if(((p.ticks*p.speed)*p.ticks/10 + -yOffset/5) > sr.getScaledHeight() || ((p.ticks*p.speed)*p.ticks/10 + -yOffset/5) < 0 || (p.x + Math.sin(p.ticks/2)*50 + -xOffset/5) > sr.getScaledWidth()|| (p.x + Math.sin(p.ticks/2)*50 + -xOffset/5) < 0){
				toremove.add(p);
			}
		}
		
		particles.removeAll(toremove);
		GlStateManager.color(1, 1, 1, 1);
		GL11.glColor4f(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		lastMouseX = GLUtil.getMouseX();
		lastMouseY = GLUtil.getMouseY();
	}
	
}
