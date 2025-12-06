package cn.Power.ui;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;

import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class Container
{
    public int a;
    
    public Container(final String s, final String s2) {

        try {
        //    final Class<? extends Container> class1 = this.getClass();
        //    final StringBuilder sb = new StringBuilder();
        //    final InputStream resourceAsStream = class1.getResourceAsStream(sb.append("/assets/minecraft/textures/shaders/").append(s2).toString());
            int a = this.a("void main(void) {\n" + 
            		"    gl_TexCoord[0] = gl_MultiTexCoord0;\n" + 
            		"    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" + 
            		"}\n" + 
            		"", 35633);
            Thread.dumpStack();
       //     IOUtils.closeQuietly(resourceAsStream);
       //     final InputStream resourceAsStream2 = this.getClass().getResourceAsStream("/assets/minecraft/textures/shaders/" + s);
            int a2 = this.a("#version 120" + 
            		"" + 
            		"uniform sampler2D texture;\n" + 
            		"uniform sampler2D texture2;\n" + 
            		"uniform vec2 texelSize;\n" + 
            		"uniform vec2 direction;\n" + 
            		"uniform float weights[256];\n" + 
            		"\n" + 
            		"void main() {\n" + 
            		"    vec2 texCoord = gl_TexCoord[0].st;\n" + 
            		"    if(direction.y == 1)\n" + 
            		"        if (texture2D(texture2, texCoord).a != 0.0) return;\n" + 
            		"    vec4 blurred_color = vec4(0.0);\n" + 
            		"    for(float r = -10; r <= 10; r++) {\n" + 
            		"        blurred_color += texture2D(texture, gl_TexCoord[0].st + r * texelSize * direction) * (weights[int(abs(r))]);\n" + 
            		"    }\n" + 
            		"    gl_FragColor = vec4(0.0f, 0.0f, 0.0f, blurred_color.a);\n" + 
            		"}", 35632);
     //    IOUtils.closeQuietly(resourceAsStream2);
           
            
            this.a = GL20.glCreateProgram();

            GL20.glAttachShader(this.a, a);
            GL20.glAttachShader(this.a, a2);
            GL20.glLinkProgram(this.a);
            GL20.glValidateProgram(this.a);
            
            String programLog = GL20.glGetProgramInfoLog(this.a, GL20.glGetProgrami(this.a, GL20.GL_INFO_LOG_LENGTH));
    		if (programLog.trim().length() > 0) {
    			System.err.println(programLog);
    		}
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void b() {
        if (Minecraft.getMinecraft().gameSettings.ofFastRender) {
            return;
        }
        GL20.glUseProgram(0);
    }
    
    public void a() {
        if (Minecraft.getMinecraft().gameSettings.ofFastRender) {
            return;
        }
        GL20.glUseProgram(0);
    }
    
    private int a(final String s, final int n) {
    	int glCreateShader = 0;
        try {
            glCreateShader = GL20.glCreateShader(n);
            if (glCreateShader == 0) {
                return 0;
            }
            GL20.glShaderSource(glCreateShader, (CharSequence)s);
            GL20.glCompileShader(glCreateShader);
            
           return glCreateShader;
        }
        catch (Exception ex) {
            GL20.glDeleteShader(glCreateShader);
            throw ex;
        }
    }
    
    public Framebuffer ga(Framebuffer fbuffer) {
        if (fbuffer != null) {
        	fbuffer.deleteFramebuffer();
        }
        final Framebuffer fbuffer2;
        fbuffer2 = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, true);

        return fbuffer2;
    }
    
    public void a(final Framebuffer fbuffer) {
        if (Minecraft.getMinecraft().gameSettings.ofFastRender) {
            return;
        }
		ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        final int b = scale.getScaledWidth();
        final int c = scale.getScaledHeight();
        GL11.glBindTexture(3553, fbuffer.framebufferTexture);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(0.0f, (float)c);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f((float)b, (float)c);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f((float)b, 0.0f);
        GL11.glEnd();
    }
}
