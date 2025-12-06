package cn.Power.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

public class GLUtil {
	private static final Map<Integer, Boolean> glCapMap = new HashMap();

	public GLUtil() {
	}

	public static void setGLCap(int cap, boolean flag) {
		glCapMap.put(cap, GL11.glGetBoolean(cap));
		if (flag) {
			GL11.glEnable(cap);
		} else {
			GL11.glDisable(cap);
		}
	}

	private static void revertGLCap(int cap) {
		Boolean origCap = glCapMap.get(cap);
		if (origCap != null) {
			if (origCap) {
				GL11.glEnable(cap);
			} else {
				GL11.glDisable(cap);
			}
		}

	}

	public static void glEnable(int cap) {
		setGLCap(cap, true);
	}

	public static void glDisable(int cap) {
		setGLCap(cap, false);
	}

	public static void revertAllCaps() {
		Iterator var0 = glCapMap.keySet().iterator();

		while (var0.hasNext()) {
			Integer cap = (Integer) var0.next();
			revertGLCap(cap);
		}
	}

    public static int getMouseX() {
        return Mouse.getX() * getScreenWidth() / Minecraft.getMinecraft().displayWidth;
    }
    
    public static int getMouseY() {
        return getScreenHeight() - Mouse.getY() * getScreenHeight() / Minecraft.getMinecraft().displayWidth - 1;
    }
    public static int getScreenWidth() {
        return Minecraft.getMinecraft().displayWidth / getScaleFactor();
    }
    
    public static int getScreenHeight() {
        return Minecraft.getMinecraft().displayHeight / getScaleFactor();
    }
    
    public static int getScaleFactor() {
        int scaleFactor = 1;
        final boolean isUnicode = Minecraft.getMinecraft().isUnicode();
        int guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
        if (guiScale == 0) {
            guiScale = 1000;
        }
        while (scaleFactor < guiScale && Minecraft.getMinecraft().displayWidth / (scaleFactor + 1) >= 320 && Minecraft.getMinecraft().displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        if (isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }
        return scaleFactor;
    }
}