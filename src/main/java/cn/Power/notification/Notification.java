package cn.Power.notification;


import java.awt.Color;
import java.math.BigDecimal;

import org.lwjgl.opengl.GL11;

import cn.Power.Client;
import cn.Power.Font.FontManager;
import cn.Power.Font.FontUtils;
import cn.Power.util.ClientUtil;
import cn.Power.util.Colors;
import cn.Power.util.RenderUtil;
import cn.Power.util.ETB.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class Notification {
    private TimerUtil timer;
    private static Minecraft mc = Minecraft.getMinecraft();
    private float x, oldX, y, oldY, width;
    private String text;
    private int stayTime;
    private boolean done;
    private float stayBar;
	private int color;
	
    Notification(String text, int stayTime , final Type Type) {
        this.x = new ScaledResolution(mc).getScaledWidth() - 2;
        this.y = new ScaledResolution(mc).getScaledHeight() - 2;
        this.text = text;
        this.stayTime = stayTime;
        timer = new TimerUtil();
        timer.reset();
        stayBar = stayTime;
        done = false;
        if (Type.equals(Type.INFO)) {
			this.color = ClientUtil.reAlpha(Colors.getColor(Color.WHITE), 1F);
			if (Client.Sound.getValueState().booleanValue()) {
				Minecraft.getMinecraft().thePlayer.playSound("random.click", 20.0F, 20.0F);
			}
		} else if (Type.equals(Type.ERROR)) {
			this.color = ClientUtil.reAlpha(Colors.getColor(Color.RED), 1F);
			if (Client.Sound.getValueState().booleanValue()) {
				Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 0.8F);
			}
		} else if (Type.equals(Type.SUCCESS)) {
			this.color = ClientUtil.reAlpha(Colors.getColor(Color.GREEN), 1F);
			if (Client.Sound.getValueState().booleanValue()) {
				Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 1.0F);
			}
		} else if (Type.equals(Type.WARNING)) {
			this.color = ClientUtil.reAlpha(Colors.getColor(Color.YELLOW), 1F);
			if (Client.Sound.getValueState().booleanValue()) {
				Minecraft.getMinecraft().thePlayer.playSound("random.orb", 1.0F, 1.0F);
			}
		}
    }

    public void draw(float prevY) {
    	FontUtils font = FontManager.sw18;
    	String text = this.text.replace("%timer"," \2477("+new BigDecimal((stayTime - stayBar )/1000 ).setScale(1, BigDecimal.ROUND_HALF_UP)+"s)");
         float xSpeed = 1.0f * Client.delta;
        final float ySpeed = (new ScaledResolution(mc).getScaledHeight() - prevY) /*/ (Minecraft.getDebugFPS() / 8) Client.delta*/;
		if (width != font.getStringWidth(text) + 8) {
			width = font.getStringWidth(text) + 8;
		}
        if (done) {
            oldX = x;
            x += xSpeed;
            y += ySpeed;
        }
        if (!done() && !done) {
            oldX = x;
            if (x <= new ScaledResolution(mc).getScaledWidth()  - width + xSpeed)
                x = new ScaledResolution(mc).getScaledWidth()  - width;
            else x -= xSpeed;
        } else if (timer.reach(stayTime)) done = true;
        if (x < new ScaledResolution(mc).getScaledWidth()  - width) {
            oldX = x;
            x += xSpeed;
        }
        if (y != prevY) {
            if (y != prevY) {
                if (y > prevY + ySpeed) {
                    y -= ySpeed;
                } else {
                    y = prevY;
                }
            } else if (y < prevY) {
                oldY = y;
                y += ySpeed;
            }
        }
        if (done() && !done) {
            stayBar = timer.time();
        }
        final float finishedX = oldX + (x - oldX);
        final float finishedY = oldY + (y - oldY);
        drawBorderedRect(finishedX, finishedY+7, width, 14, (float) 0.5, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 120).getRGB());
        if (done()) drawRect(finishedX , finishedY+20, ((width - 1) / stayTime) * stayBar, 1, color);
        font.drawStringWithShadow(text, finishedX + 3, finishedY + 9, -1);
        if (delete()) Client.instance.getNotificationManager().getNotifications().remove(this);
    }

    public boolean done() {
        return x <= new ScaledResolution(mc).getScaledWidth()  - width;
    }

    public boolean delete() {
        return x >= new ScaledResolution(mc).getScaledWidth()  && done;
    }
    
    public enum Type {
		SUCCESS("SUCCESS", 0), INFO("INFO", 1), WARNING("WARNING", 2), ERROR("ERROR", 3);

		private Type(final String s, final int n) {
		}
	}
    
    public static void drawBorderedRect(double x, double y, double width, double height, double lineSize, int borderColor, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
        Gui.drawRect(x, y, x + width, y + lineSize, borderColor);
        Gui.drawRect(x, y, x + lineSize, y + height, borderColor);
        Gui.drawRect(x + width, y, x + width - lineSize, y + height, borderColor);
        Gui.drawRect(x, y + height, x + width, y + height - lineSize, borderColor);
    }
    public static void drawRect(double x, double y, double width, double height, int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
        Gui.drawRect(x, y, x + width, y + height, color);
    }
}
