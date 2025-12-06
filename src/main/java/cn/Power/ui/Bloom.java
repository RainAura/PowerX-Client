package cn.Power.ui;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import cn.Power.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;

public class Bloom
{
    private static Container d;
    private static Framebuffer a;
    private static Framebuffer b;
    private static Framebuffer e;
    private static int g;
    private static int f;
    private static int c;
    private static final String[] h;

    
    public static void c() {
        GlStateManager.disableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        Bloom.b.bindFramebuffer(true);
        Bloom.d.b();
        a(Bloom.d.a, 1.0f, 0.0f);
        Bloom.d.a(Bloom.a);
        Bloom.d.a();
        Bloom.b.unbindFramebuffer();
        Bloom.d.b();
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        a(Bloom.d.a, 0.0f, 1.0f);
        GL13.glActiveTexture(34004);
        GL11.glBindTexture(3553, Bloom.a.framebufferTexture);
        GL13.glActiveTexture(33984);
        Bloom.d.a(Bloom.b);
        Bloom.d.a();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();
    }
    
    public static void a(final int n, final float n2, final float n3) {
//		GlStateManager.enableTexture2D();
//		GlStateManager.disableBlend();
//		GlStateManager.enableCull();

        GL20.glUniform1i(GL20.glGetUniformLocation(n, (CharSequence)"texture"), 0);
        GL20.glUniform1i(GL20.glGetUniformLocation(n, (CharSequence)"texture2"), 20);
        GL20.glUniform2f(GL20.glGetUniformLocation(n, (CharSequence)"texelSize"), 1.0f / Minecraft.getMinecraft().displayWidth, 1.0f / Minecraft.getMinecraft().displayHeight);
        GL20.glUniform2f(GL20.glGetUniformLocation(n, (CharSequence)"direction"), n2, n3);
        final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(256);
        final float n4 = 20.0f;
        int n5 = 0;
        while (n5 <= n4) {
            floatBuffer.put(a((float)n5, 10.0f));
            ++n5;
        }
        floatBuffer.rewind();
        GL20.glUniform1(GL20.glGetUniformLocation(n, (CharSequence)"weights"), floatBuffer);
    }
    
    private static float a(final float n, final float n2) {
        final float n3 = n / n2;
        return (float)(1.0 / (Math.abs(n2) * 2.50662827463) * Math.exp(-0.5 * n3 * n3));
    }
    
    public static void a() {
       
    	if(Bloom.d == null) {
    		 Bloom.d = new Container("bloom_shader.fsh", "vertex_shader.vsh");
    	}
    	
        Minecraft.getMinecraft().gameSettings.ofFastRender = false;
		ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        final int a = scale.getScaleFactor();
        
//		GlStateManager.enableTexture2D();
//		GlStateManager.disableBlend();
//		GlStateManager.enableCull();
		
        final int b = scale.getScaledWidth();
        final int c = scale.getScaledHeight();
        if (Bloom.a != null && Bloom.b != null) {
            Bloom.a.framebufferClear();
            Bloom.b.framebufferClear();
        }
        if (Bloom.g != a || Bloom.f != b || Bloom.c != c || Bloom.a == null || Bloom.b == null) {
            Bloom.a = Bloom.d.ga(Bloom.a);
            Bloom.b = Bloom.d.ga(Bloom.b);
            Bloom.c = c;
            Bloom.f = b;
            Bloom.g = a;
        }

        Bloom.a.bindFramebuffer(true);

        a(scale.getScaledHeight() / 2.0f - 150.0f, scale.getScaleFactor() / 2.0f - 105.0f, 300.0, 204.0, -1);
        
        Bloom.a.unbindFramebuffer();
        c();
    }
    
    public static void a(final double n, final double n2, final double n3, final double n4, final int n5) {
        GL11.glColor4f((n5 >> 16 & 0xFF) / 255.0f, (n5 >> 8 & 0xFF) / 255.0f, (n5 & 0xFF) / 255.0f, (n5 >> 24 & 0xFF) / 255.0f);
        a2(n, n2, n + n3, n2 + n4, n5);
    }
    
    public static void a2(double n, double n2, double n3, double n4, final int n5) {
        if (n < n3) {
            final double n6 = n;
            n = n3;
            n3 = n6;
        }
        if (n2 < n4) {
            final double n7 = n2;
            n2 = n4;
            n4 = n7;
        }
        final float p_a_3_ = (n5 >> 24 & 0xFF) / 255.0f;
        final float p_a_0_ = (n5 >> 16 & 0xFF) / 255.0f;
        final float p_a_1_ = (n5 >> 8 & 0xFF) / 255.0f;
        final float p_a_2_ = (n5 & 0xFF) / 255.0f;
        final Tessellator a = Tessellator.getInstance();
        final WorldRenderer a2 = a.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(p_a_0_, p_a_1_, p_a_2_, p_a_3_);
        a2.begin(7, DefaultVertexFormats.POSITION);
        a2.pos(n, n4, 0.0).endVertex();
        a2.pos(n3, n4, 0.0).endVertex();
        a2.pos(n3, n2, 0.0).endVertex();
        a2.pos(n, n2, 0.0).endVertex();
        a.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    static {
        h = new String[] { "texture", "ESP", "HUD", "KillAura", "direction", "bloom_shader.fsh", "weights", "Breadcrumbs", "texelSize", "ClickGUI", "HUD", "texture2", "Chams", "vertex_shader.vsh" };
        final String[] h2 = Bloom.h;
    
        Bloom.d = new Container("bloom_shader.fsh", "vertex_shader.vsh");
    	
    }
}
    