package cn.Power.mod.mods.RENDER;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.Font.FontManager;
import cn.Power.events.EventRender;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.Colors;
import cn.Power.util.Opacity;
import cn.Power.util.RenderUtils;
import cn.Power.util.RotationUtils;
import cn.Power.util.waypoints.Waypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;

/**
 * 
 * Replace with
 * World
 * @author acer
 *
 */
@Deprecated
public class Waypoints extends Mod {
	
	///render
	private double gradualFOVModifier;
	public static Map<Waypoint, double[]> waypointMap = new HashMap();
	private Opacity opacity = new Opacity(0);
	boolean forward = true;
	public static Value<Boolean> ARROWS = new Value("Waypoints_Arrows", false);

	@Deprecated
	public Waypoints() {
		super("Waypoints", Category.RENDER);
	}

	@EventTarget
	private void event3d(EventRender e) {
		updatePositions();
	}

	@EventTarget
	private void event2d(EventRender2D e) {
		GlStateManager.pushMatrix();
		ScaledResolution scaledRes = new ScaledResolution(mc);
		float radius = scaledRes.getScaledHeight() / 2 - 25;
		float radius2 = scaledRes.getScaledWidth() / 2 - 25;
		float w = scaledRes.getScaledWidth() / 2;
		float h = scaledRes.getScaledHeight() / 2;
		for (Waypoint waypoint : waypointMap.keySet()) {
			double[] renderPositions = waypointMap.get(waypoint);
			if ((isInView(renderPositions[0] / scaledRes.getScaleFactor(),
					renderPositions[1] / scaledRes.getScaleFactor(), scaledRes, waypoint))) {
				GlStateManager.pushMatrix();
				String str = waypoint.getName() + " \247a" + (int) mc.thePlayer.getDistance(waypoint.getVec3().xCoord,
						waypoint.getVec3().yCoord, waypoint.getVec3().zCoord) + "m";
				GlStateManager.translate(renderPositions[0] / scaledRes.getScaleFactor(),
						renderPositions[1] / scaledRes.getScaleFactor(), 0.0D);
				scale();
				GlStateManager.translate(0.0D, -2.5D, 0.0D);
				float strWidth = FontManager.sw18.getStringWidth(str);
				RenderUtils.rectangleBordered(-strWidth / 2 - 3, -12.0D, strWidth / 2 + 3, 1.0D, 0.5f,
						Colors.getColor(0, 100), waypoint.getColor());
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				FontManager.sw18.drawStringWithShadow(str, -strWidth / 2, -7.5f, -1);
				GlStateManager.rotate(90, 0, 0, 1);
				RenderUtils.drawCircle(3f, 0, 4, 3, waypoint.getColor());
				RenderUtils.drawCircle(3f, 0, 3, 3, waypoint.getColor());
				RenderUtils.drawCircle(3f, 0, 2, 3, waypoint.getColor());
				RenderUtils.drawCircle(3f, 0, 1, 3, waypoint.getColor());

				GlStateManager.popMatrix();

			} else if (ARROWS.getValueState()) {

				float angle = findAngle(w, renderPositions[0] / scaledRes.getScaleFactor(), h,
						renderPositions[1] / scaledRes.getScaleFactor()) + (renderPositions[3] > 1 ? 180 : 0);

				double x = (radius2) * Math.cos(Math.toRadians(angle)); // angle is in radians
				double y = (radius) * Math.sin(Math.toRadians(angle));

				GlStateManager.pushMatrix();
				GlStateManager.translate(x + scaledRes.getScaledWidth() / 2, y + scaledRes.getScaledHeight() / 2, 0);
				GlStateManager.rotate(angle, 0, 0, 1);
				GlStateManager.scale(1.5f, 1, 1);

				if (forward && opacity.getOpacity() >= 300) {
					forward = false;
				} else if (!forward && opacity.getOpacity() <= 51) {
					forward = true;
				}
				opacity.interp(forward ? 300 : 50, 3);

				int alpha = (int) opacity.getOpacity();
				if (alpha > 255) {
					alpha = 255;
				} else if (alpha < 0) {
					alpha = 0;
				}

				int f = alpha;
				int f1 = (waypoint.getColor() >> 16 & 0xFF);
				int f2 = (waypoint.getColor() >> 8 & 0xFF);
				int f3 = (waypoint.getColor() & 0xFF);
				int color = Colors.getColor(f1, f2, f3, f);

				RenderUtils.drawCircle(0, 0, 6, 3, Colors.getColor(0, f));
				RenderUtils.drawCircle(0, 0, 5, 3, color);
				RenderUtils.drawCircle(0, 0, 4, 3, color);
				RenderUtils.drawCircle(0, 0, 3, 3, color);
				RenderUtils.drawCircle(0, 0, 2, 3, color);
				RenderUtils.drawCircle(0, 0, 1, 3, color);
				RenderUtils.drawCircle(0, 0, 0f, 3, color);

				GlStateManager.popMatrix();

			}
			/*
			 * mc.fontRendererObj.drawStringWithShadow(waypoint.getName() + "\247a " +
			 * (int)renderPositions[0]/scaledRes.getScaleFactor() + "\247b " +
			 * (int)renderPositions[1]/scaledRes.getScaleFactor() + "\247c " +
			 * (int)renderPositions[3] + "\247e " + (isInView(renderPositions[0] /
			 * scaledRes.getScaleFactor(), renderPositions[1] / scaledRes.getScaleFactor(),
			 * scaledRes, waypoint)),300,y1,-1);
			 */
		}

		GlStateManager.popMatrix();
	}

	private void scale() {
		float scale = 1;
		float target = scale
				* (mc.gameSettings.fovSetting / (mc.gameSettings.fovSetting * mc.thePlayer.func_175156_o()));
		if ((this.gradualFOVModifier == 0.0D) || (Double.isNaN(this.gradualFOVModifier))) {
			this.gradualFOVModifier = target;
		}
		this.gradualFOVModifier += (target - this.gradualFOVModifier) / (Minecraft.debugFPS * 0.7D);

		scale = (float) (scale * this.gradualFOVModifier);

		scale *= ((mc.currentScreen == null) && (GameSettings.isKeyDown(mc.gameSettings.ofKeyBindZoom)) ? 1 : 1);
		GlStateManager.scale(scale, scale, scale);
	}

	private void updatePositions() {
		waypointMap.clear();
//		        for (Waypoint waypoint : Client.wm.getWaypoints()) {
//		            double x = waypoint.getVec3().xCoord - mc.getRenderManager().viewerPosX;
//		            double y = waypoint.getVec3().yCoord - mc.getRenderManager().viewerPosY;
//		            double z = waypoint.getVec3().zCoord - mc.getRenderManager().viewerPosZ;
//		            y += 0.2D;
//		            waypointMap.put(waypoint,
//		                    new double[]{convertTo2D(x, y, z)[0], convertTo2D(x, y, z)[1],
//		                            Math.abs(convertTo2D(x, y + 1.0D, z, waypoint)[1] - convertTo2D(x, y, z, waypoint)[1]),
//		                            convertTo2D(x, y, z)[2]});
//		        }
	}

	private double[] convertTo2D(double x, double y, double z, Waypoint waypoint) {
		float pTicks = mc.timer.renderPartialTicks;
		float prevYaw = mc.thePlayer.rotationYaw;
		float prevPrevYaw = mc.thePlayer.prevRotationYaw;
		float[] rotations = RotationUtils.getRotationFromPosition(waypoint.getVec3().xCoord, waypoint.getVec3().zCoord,
				waypoint.getVec3().yCoord - 1.6D);
		mc.getRenderViewEntity().rotationYaw = (mc.getRenderViewEntity().prevRotationYaw = rotations[0]);
		mc.entityRenderer.setupCameraTransform(pTicks, 0);
		double[] convertedPoints = convertTo2D(x, y, z);
		mc.getRenderViewEntity().rotationYaw = prevYaw;
		mc.getRenderViewEntity().prevRotationYaw = prevPrevYaw;
		mc.entityRenderer.setupCameraTransform(pTicks, 0);
		return convertedPoints;
	}

	private double[] convertTo2D(double x, double y, double z) {
		FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
		IntBuffer viewport = BufferUtils.createIntBuffer(16);
		FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
		FloatBuffer projection = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(2982, modelView);
		GL11.glGetFloat(2983, projection);
		GL11.glGetInteger(2978, viewport);
		boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
		if (result) {
			return new double[] { screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2) };
		}
		return null;
	}

	private float findAngle(double x, double x2, double y, double y2) {

		ScaledResolution scaledRes = new ScaledResolution(mc);
		float a = scaledRes.getScaledHeight() - 25;
		float b = scaledRes.getScaledWidth() - 25;
		return (float) (Math.toDegrees(Math.atan2(y2 - y, x2 - x)));
	}

	private boolean isInView(double x, double y, ScaledResolution resolution, Waypoint waypoint) {
		float angle = RotationUtils.getRotationFromPosition(waypoint.getVec3().xCoord, waypoint.getVec3().zCoord,
				waypoint.getVec3().yCoord - 1.6D)[0];
		float cameraYaw = mc.getRenderViewEntity().rotationYaw + (mc.gameSettings.thirdPersonView == 2 ? 180 : 0);
		return (x < resolution.getScaledWidth() && x > 0) && (y < resolution.getScaledHeight() && y > 0)
				&& (RotationUtils.getDistanceBetweenAngles(angle, RotationUtils.getNewAngle(cameraYaw)) < 100);
	}
}
