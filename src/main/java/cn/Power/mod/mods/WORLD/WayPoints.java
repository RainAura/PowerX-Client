package cn.Power.mod.mods.WORLD;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.Font.FontManager;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender;
import cn.Power.events.EventRespawn;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.RENDER.ItemTags;
import cn.Power.notification.Notification;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.Helper;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.RenderUtils.R3DUtils;
import cn.Power.util.RenderUtils.Stencil;
import cn.Power.util.SkyBlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

public class WayPoints extends Mod {
	public Value<Boolean> nametags = new Value("WayPoints_Nametags", true);
	public Value<Boolean> zero = new Value("WayPoints_zerozeroUHC", true);
	
	Waypoint zerot = new Waypoint("0 0",0,120,0 );
	

	public static Value<Double> Size = new Value<Double>("WayPoints_Size", 0.65, 0.05, 0.95, 0.01);
	
	public static List<Waypoint> waypoints = new CopyOnWriteArrayList<Waypoint>();

	public WayPoints() {
		super("WayPoints", Category.WORLD);
		waypoints.add(zerot);
	}
	
	@Override
	public void onEnable() {
		if(zero.getValueState() && !waypoints.contains(zerot))
			waypoints.add(zerot);
	}
	
	@Override
	public void onDisable() {
		this.clear();
	}
	
	@EventTarget
	public void onRespawn(EventRespawn res) {
		this.clear();
	}
	
	public void clear() {
		WayPoints.waypoints.removeIf(wp -> wp.name.startsWith("LightningTrack"));
	}
	
	@EventTarget
	public void on3D(EventRender e) {
		if (mc.isSingleplayer())
			return;
		RenderUtils.Stencil.getInstance();
		Stencil.checkSetupFBO();
		Helper.get3DUtils();
		R3DUtils.startDrawing();
		String server = getServer();
		for (Waypoint wp : waypoints) {
			if (nametags.getValueState())
				drawNameTag(wp);
		}
		Helper.get3DUtils();
		R3DUtils.stopDrawing();
		mc.getFramebuffer().bindFramebuffer(true);
		mc.getFramebuffer().bindFramebufferTexture();
	}
	
	private void drawNameTag(Waypoint wp) {
		double x = wp.getX() - mc.getRenderManager().renderPosX;
		double y = wp.getY() - mc.getRenderManager().renderPosY;
		double z = wp.getZ() - mc.getRenderManager().renderPosZ;
		double dist = Minecraft.thePlayer.getDistance(wp.getX(), wp.getY(), wp.getZ());
		final String text = wp.getName() + " \247c" + Math.round(dist) + "m\247r";
		double far = Mod.mc.gameSettings.renderDistanceChunks * 12.8D;
		double dl = Math.sqrt(x * x + z * z + y * y);
		double d;
		if (dl > far) {
			d = far / dl;
			dist *= d;
			x *= d;
			y *= d;
			z *= d;
		}
		
	
		
//		float var13 = (float) ((float) dist / 5 <= 2 ? 2.0F : (float) dist * scale.getValueState().floatValue());
//		float var14 = 0.016666668F * var13;
		GlStateManager.pushMatrix();
		RenderUtils.R3DUtils.startDrawing();
		GlStateManager.translate(x, dl < 10 ? y + 25 : y + 2.5F, z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		if (mc.gameSettings.thirdPersonView == 2) {
			GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(mc.getRenderManager().playerViewX, -1.0F, 0.0F, 0.0F);
		} else {
			GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
		}
		GlStateManager.scale(-Size.getValueState().doubleValue(), -Size.getValueState().doubleValue(), Size.getValueState().doubleValue());
		int var18 = (int) (FontManager.sw15.getStringWidth(text) / 2);
	
		RenderUtil.drawRect(-var18-1, 0, var18+2, 9, new Color(0,0,0,111).getRGB());
		FontManager.sw15.drawStringWithShadow(text, -var18, 0, 0xFFFFFFFF);

		RenderUtils.R3DUtils.stopDrawing();
		GlStateManager.popMatrix();
	}
	
	public static class Waypoint {
		private String name;
		private double x, y, z;

		public Waypoint(String name, double x, double y, double z) {
			this.name = name;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public String getName() {
			return name;
		}


		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getZ() {
			return z;
		}

	}
	
	public static String getServer() {
		return (mc.getCurrentServerData() == null) ? "singleplayer" : mc.getCurrentServerData().serverIP;
	}
	


}
