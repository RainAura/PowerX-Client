package cn.Power.mod.mods.RENDER;

import java.awt.Color;
import java.util.HashMap;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.COMBAT.AntiBot;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.ui.CFont.CFontRenderer;
import cn.Power.ui.CFont.FontLoaders;
import cn.Power.util.ColorManager;
import cn.Power.util.Colors;
import cn.Power.util.Colors2;
import cn.Power.util.MathUtils;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.StringConversions;
import cn.Power.util.Timer;
import cn.Power.util.friendManager.FriendManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Radar extends Mod {
	public static HashMap<String, double[]> wp = new HashMap();

	private boolean dragging;
	float hue;
	private Timer timer = new Timer();

	private Value<Double> scale = new Value<Double>("Radar_Scale", Double.valueOf(2.0D), Double.valueOf(1.0D),
			Double.valueOf(5.0D), 0.1D);
	private Value<Double> x = new Value<Double>("Radar_X", Double.valueOf(500D), Double.valueOf(1D),
			Double.valueOf(1920D), 10D);
	private Value<Double> y = new Value<Double>("Radar_Y", Double.valueOf(2D), Double.valueOf(1D),
			Double.valueOf(1080D), 10D);
	private Value<Double> size = new Value<Double>("Radar_Size", Double.valueOf(125D), Double.valueOf(50D),
			Double.valueOf(500D), 10D);
	
	private Value<Double> BackGroundRed = new Value<Double>("Radar_BackGroundRed", Double.valueOf(0D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> BackGroundBlue = new Value<Double>("Radar_BackGroundBlue", Double.valueOf(0D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> BackGroundG = new Value<Double>("Radar_BackGroundGreen", Double.valueOf(0D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> BackGroundA = new Value<Double>("Radar_BackGroundAlpha", Double.valueOf(46D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	
	private Value<Double> BackGroundBBRed = new Value<Double>("Radar_BackGroundBordRed", Double.valueOf(77D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> BackGroundBBBlue = new Value<Double>("Radar_BackGroundBordBlue", Double.valueOf(77D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> BackGroundBBG = new Value<Double>("Radar_BackGroundBordGreen", Double.valueOf(166D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> BackGroundBBA = new Value<Double>("Radar_BackGrounBorddAlpha", Double.valueOf(77D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	
	
	private Value<Double> LineVRed = new Value<Double>("Radar_LineVRed", Double.valueOf(111D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> LineVBlue = new Value<Double>("Radar_LineVBlue", Double.valueOf(255D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> LineVG = new Value<Double>("Radar_LineVGreen", Double.valueOf(255D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> LineVA = new Value<Double>("Radar_LineVAlpha", Double.valueOf(70D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	
	private Value<Double> LineHRed = new Value<Double>("Radar_LineHRed", Double.valueOf(111D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> LineHBlue = new Value<Double>("Radar_LineHBlue", Double.valueOf(255D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> LineHG = new Value<Double>("Radar_LineHGreen", Double.valueOf(255D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	private Value<Double> LineHA = new Value<Double>("Radar_LineHAlpha", Double.valueOf(70D), Double.valueOf(0D),
			Double.valueOf(255D), 1D);
	
	private Value<Double> LineWeight = new Value<Double>("Radar_LineWeight", Double.valueOf(0.2D), Double.valueOf(0.1D),
			Double.valueOf(0.9D), 0.01D);
	
	
	
	public Value<Boolean> WP = new Value("Radar_WayPoints", true);
	public Value<Boolean> rb = new Value("Radar_RainBow", false);
	public Value<String> mode = new Value("Radar", "Mode", 0);

	public Radar() {
		super("Radar", Category.RENDER);
		mode.addValue("Exhibition");
		mode.addValue("Sigma");
		wp.put("PVP", new double[] { Double.parseDouble("0"), Double.parseDouble("80"), Double.parseDouble("0") });
	}

	CFontRenderer Sigma = null;

	@Override
	public void onDisable() {
		Sigma = null;
	}


    public int moveX = 0;
    public int moveY = 0;
    
    
	@EventTarget
	public void onRender2D(EventRender2D event) {
		if (mode.isCurrentMode("Sigma")) {
			int size = this.size.getValueState().intValue();
			float xOffset = ((Number) x.getValueState()).floatValue();
			float yOffset = ((Number) y.getValueState()).floatValue(); // Global Y
			float playerOffsetX = (float) (mc.thePlayer.posX);
			float playerOffSetZ = (float) (mc.thePlayer.posZ);

			if (Sigma == null)
				Sigma = new CFontRenderer(FontLoaders.Sigma(14), true, true);

			/*
			 * Horizontal line of the cross
			 */
			GlStateManager.pushMatrix();
			float angle2 = -mc.thePlayer.rotationYaw + 90;
			float x2 = (float) ((size / 2 + 4) * Math.cos(Math.toRadians(angle2))) + xOffset + size / 2; // angle is in
																											// radians
			float y2 = (float) ((size / 2 + 4) * Math.sin(Math.toRadians(angle2))) + yOffset + size / 2;
			Sigma.drawStringWithShadow("N", x2 - Sigma.getStringWidth("N") / 2, y2 - 1, -1);
			x2 = (float) ((size / 2 + 4) * Math.cos(Math.toRadians(angle2 + 90))) + xOffset + size / 2; // angle is in
																										// radians
			y2 = (float) ((size / 2 + 4) * Math.sin(Math.toRadians(angle2 + 90))) + yOffset + size / 2;
			Sigma.drawStringWithShadow("E", x2 - Sigma.getStringWidth("E") / 2, y2 - 1, -1);
			x2 = (float) ((size / 2 + 4) * Math.cos(Math.toRadians(angle2 + 180))) + xOffset + size / 2; // angle is in
																											// radians
			y2 = (float) ((size / 2 + 4) * Math.sin(Math.toRadians(angle2 + 180))) + yOffset + size / 2;
			Sigma.drawStringWithShadow("S", x2 - Sigma.getStringWidth("S") / 2, y2 - 1, -1);
			x2 = (float) ((size / 2 + 4) * Math.cos(Math.toRadians(angle2 - 90))) + xOffset + size / 2; // angle is in
																										// radians
			y2 = (float) ((size / 2 + 4) * Math.sin(Math.toRadians(angle2 - 90))) + yOffset + size / 2;
			Sigma.drawStringWithShadow("W", x2 - Sigma.getStringWidth("W") / 2, y2 - 1, -1);

			GlStateManager.translate(xOffset + size / 2, yOffset + size / 2, 0);
			GlStateManager.rotate(-mc.thePlayer.rotationYaw, 0, 0, 1);

			RenderUtils.rectangle((-0.5), -size / 2 + 4, (0.5), size / 2 - 4, Colors2.getColor(255, 80));
			RenderUtils.rectangle(-size / 2 + 4, (-0.5), size / 2 - 4, (+0.5), Colors2.getColor(255, 80));

			/*
			 * Vertical line of the cross
			 */

			GlStateManager.popMatrix();

			RenderUtils.drawCircle(xOffset + (size / 2), yOffset + size / 2, size / 2 - 4, 72,
					Colors2.getColor(0, 200));

			Gui.drawFilledCircle(xOffset + (size / 2), yOffset + size / 2, size / 2 - 4, Colors2.getColor(50, 100));

			ScaledResolution sr = new ScaledResolution(mc);
			int var141 = sr.getScaledWidth();
			int var151 = sr.getScaledHeight();
			final int mouseX = Mouse.getX() * var141 / mc.displayWidth;
			final int mouseY = var151 - Mouse.getY() * var151 / mc.displayHeight - 1;

			if (mouseX >= xOffset && mouseX <= xOffset + size && mouseY >= yOffset - 3 && mouseY <= yOffset + 10
					&& Mouse.getEventButton() == 0 && timer.delay(20)) {
				timer.reset();
				dragging = !dragging;
			}
			if (dragging && mc.currentScreen instanceof GuiChat) {
				Object newValue = (StringConversions.castNumber(Double.toString(mouseX - size / 2), 5));
				x.setValueState((Double) newValue);
				Object newValueY = (StringConversions.castNumber(Double.toString(mouseY - 2), 5));
				y.setValueState((Double) newValueY);
			} else {
				dragging = false;
			}
			for (Object o : mc.theWorld.loadedEntityList) {
				if (o instanceof EntityPlayer) {
					EntityPlayer ent = (EntityPlayer) o;
					if (ent.isEntityAlive() && ent != mc.thePlayer
							&& !(ent.isInvisible() || ent.isInvisibleToPlayer(mc.thePlayer))) {
						/*
						 * (targetPlayer posX - localPlayer posX) * Distance Scale
						 */

						float pTicks = mc.timer.renderPartialTicks;
						float posX = (float) (((ent.posX + (ent.posX - ent.lastTickPosX) * pTicks) - playerOffsetX)
								* ((Number) scale.getValueState()).doubleValue());
						/*
						 * (targetPlayer posZ - localPlayer posZ) * Distance Scale
						 */
						float posZ = (float) (((ent.posZ + (ent.posZ - ent.lastTickPosZ) * pTicks) - playerOffSetZ)
								* ((Number) scale.getValueState()).doubleValue());
						int color = 0;
						// Gay Friend Check
						if (FriendManager.isFriend(ent.getName())) {
							color = Colors2.getColor(0, 195, 255);
						} else if (Teams.isOnSameTeam(ent)) {
							color = ColorManager.getEnemyTeam().getColorInt();
						} else if (mc.thePlayer.canEntityBeSeen(ent)) {
							color = ColorManager.getEnemyVisible().getColorInt();
						} else if (!mc.thePlayer.canEntityBeSeen(ent)) {
							color = ColorManager.getEnemyInvisible().getColorInt();
						}

						/*
						 * Fuck Ms. Goble's geometry class. Rotate the circle based off of the player
						 * yaw with some gay trig.
						 */

						float cos = (float) Math.cos(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
						float sin = (float) Math.sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
						float rotY = -(posZ * cos - posX * sin);
						float rotX = -(posX * cos + posZ * sin);
						float var7 = 0 - rotX;
						float var9 = 0 - rotY;
						if (MathHelper.sqrt_double(var7 * var7 + var9 * var9) > size / 2 - 4) {
							float angle = findAngle(0, rotX, 0, rotY);
							float x = (float) ((size / 2) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2; // angle
																													// is
																													// in
																													// radians
							float y = (float) ((size / 2) * Math.sin(Math.toRadians(angle))) + yOffset + size / 2;
							GlStateManager.pushMatrix();

							GlStateManager.translate(x, y, 0);
							GlStateManager.rotate(angle, 0, 0, 1);
							GlStateManager.scale(1.5f, 0.5, 0.5);

							RenderUtils.drawCircle(0, 0, 1.5f, 3, Colors2.getColor(46));
							RenderUtils.drawCircle(0, 0, 1, 3, color);

							GlStateManager.popMatrix();
						} else {
							RenderUtil.rectangleBordered(xOffset + (size / 2) + rotX - 1.5,
									yOffset + (size / 2) + rotY - 1.5, xOffset + (size / 2) + rotX + 1.5,
									yOffset + (size / 2) + rotY + 1.5, 0.5, color, Colors2.getColor(46));
						}

						/*
						 * Clamps to the edge of the radar, have it less than the radar if you don't
						 * want squares to come out.
						 */

					}
				}
			}
		}
	}

	@EventTarget
	public void onGui(EventRender2D e) {
		if (mode.isCurrentMode("Exhibition")) {
			if (!this.mc.gameSettings.showDebugInfo) {
				ScaledResolution sr = new ScaledResolution(this.mc);
				int size1;
				float xOffset;
				float yOffset;
				float playerOffsetX;
				float playerOffSetZ;
				size1 = size.getValueState().intValue();
				xOffset = x.getValueState().floatValue();
				yOffset = y.getValueState().floatValue();
				playerOffsetX = (float) mc.thePlayer.posX;
				playerOffSetZ = (float) mc.thePlayer.posZ;

	            int mouseX = (int) MathUtils.map(Mouse.getX(), 0.0f, Display.getWidth(), 0.0f, sr.getScaledWidth());
	            int mouseY = (int) MathUtils.map(Display.getHeight() - Mouse.getY(), 0.0f, Display.getHeight(), 0.0f, sr.getScaledHeight());

	            boolean Hover = MathUtils.contains(mouseX, mouseY,  xOffset + 2.5f,  yOffset + 2.5f,  (xOffset + (float) size1) - 2.5f, (yOffset + (float) size1) - 2.5f) && mc.currentScreen instanceof GuiChat;

	            if(Hover && Mouse.isButtonDown(0)) {
	                this.dragging = true;
	            }else if (!Mouse.isButtonDown(0)) {
	                this.dragging = false;
	            }

	            if (this.dragging) {
	                if (moveX == 0 && moveY == 0) {
	                    moveX = mouseX - x.getValueState().intValue();
	                    moveY = mouseY - y.getValueState().intValue();
	                } else {
	                    x.setValueState((double) (mouseX - moveX));
	                    y.setValueState((double) (mouseY - moveY));
	                }
	            } else {
	                if (moveX != 0 || moveY != 0) {
	                    moveX = 0;
	                    moveY = 0;
	                }
	            }
	            if (xOffset > (float) (sr.getScaledWidth() - size1)) {
	                xOffset = sr.getScaledWidth() - size1;
	                x.setValueState((double) xOffset);
	            }
	            if (xOffset < 0) {
	                xOffset = 0;
	                x.setValueState((double) xOffset);
	            }
	            if (yOffset > (float) (sr.getScaledHeight() - size1)) {
	                yOffset = sr.getScaledHeight() - size1;
	                y.setValueState((double) yOffset);
	            }
	            if (yOffset < 0.0f) {
	                yOffset = 0;
	                y.setValueState((double) yOffset);
	            }
				if (this.hue > 255.0f) {
					this.hue = 0.0f;
				}
				float h = this.hue;
				float h2 = this.hue + 85.0f;
				float h3 = this.hue + 170.0f;
				if (h > 255.0f) {
					h = 0.0f;
				}
				if (h2 > 255.0f) {
					h2 -= 255.0f;
				}
				if (h3 > 255.0f) {
					h3 -= 255.0f;
				}
				Color color33 = Color.getHSBColor(h / 255.0f, 0.9f, 1.0f);
				Color color332 = Color.getHSBColor(h2 / 255.0f, 0.9f, 1.0f);
				Color color333 = Color.getHSBColor(h3 / 255.0f, 0.9f, 1.0f);
				int color1 = color33.getRGB();
				int color2 = color332.getRGB();
				int color3 = color333.getRGB();
				this.hue = (float) ((double) this.hue + 0.1);
				// RenderUtil.rectangleBordered(xOffset, yOffset, xOffset + (float)size1,
				// yOffset + (float)size1, 0.5, Colors2.getColor(90), Colors2.getColor(0));
				// RenderUtil.rectangleBordered(xOffset + 1.0f, yOffset + 1.0f, xOffset +
				// (float)size1 - 1.0f, yOffset + (float)size1 - 1.0f, 1.0,
				// Colors2.getColor(90), Colors2.getColor(61));
				RenderUtil.rectangleBordered((double) xOffset + 2.5, (double) yOffset + 2.5,
						(double) (xOffset + (float) size1) - 2.5, (double) (yOffset + (float) size1) - 2.5, LineWeight.getValueState().doubleValue(),
						(new Color(this.BackGroundRed.getValueState().intValue(), this.BackGroundG.getValueState().intValue(), this.BackGroundBlue.getValueState().intValue(), this.BackGroundA.getValueState().intValue())).getRGB(), Colors2.getColor(this.BackGroundBBRed.getValueState().intValue(), this.BackGroundBBG.getValueState().intValue(), this.BackGroundBBBlue.getValueState().intValue(), this.BackGroundBBA.getValueState().intValue()));

				if (rb.getValueState()) {
					RenderUtil.drawGradientSideways(xOffset + 3.0f, yOffset + 3.0f, xOffset + (float) (size1 / 2),
							(double) yOffset + 3.6, color1, color2);
					RenderUtil.drawGradientSideways(xOffset + (float) (size1 / 2), yOffset + 3.0f,
							xOffset + (float) size1 - 3.0f, (double) yOffset + 3.5, color2, color3);
				}
				
				RenderUtil.rectangle((double) xOffset + ((double) (size1 / 2) - LineWeight.getValueState().doubleValue()), (double) yOffset + 3.5,
						(double) xOffset + ((double) (size1 / 2) + LineWeight.getValueState().doubleValue()), (double) (yOffset + (float) size1) - 3,
						Colors2.getColor(this.LineVRed.getValueState().intValue(), this.LineVG.getValueState().intValue(), this.LineVBlue.getValueState().intValue(), this.LineVA.getValueState().intValue()));
				RenderUtil.rectangle((double) xOffset + 3, (double) yOffset + ((double) (size1 / 2) - LineWeight.getValueState().doubleValue()),
						(double) (xOffset + (float) size1) - 3, (double) yOffset + ((double) (size1 / 2) + LineWeight.getValueState().doubleValue()),
						Colors2.getColor(this.LineHRed.getValueState().intValue(), this.LineHG.getValueState().intValue(), this.LineHBlue.getValueState().intValue(), this.LineHA.getValueState().intValue()));

				for (Object o : mc.theWorld.loadedEntityList) {
					EntityPlayer ent;
					// if (ent.isEntityAlive() && ent != mc.thePlayer && !ent.isInvisible() &&
					// !ent.isInvisibleToPlayer(mc.thePlayer)) {e
					if (!(o instanceof EntityPlayer) || !(ent = (EntityPlayer) o).isEntityAlive() || ent == mc.thePlayer
							|| ent.isInvisible() || ent.isInvisibleToPlayer(mc.thePlayer))
						continue;

					float pTicks = mc.timer.renderPartialTicks;
					float posX = (float) ((ent.posX + (ent.posX - ent.lastTickPosX) * pTicks - playerOffsetX)
							* scale.getValueState().doubleValue());
					float posZ = (float) ((ent.posZ + (ent.posZ - ent.lastTickPosZ) * pTicks - playerOffSetZ)
							* scale.getValueState().doubleValue());
					int color;

					color = RenderUtil.rainbow(1000);
					if (FriendManager.isFriend(ent.getName())) {
						color = Colors2.getColor(0, 195, 255);
					} else if (Teams.isOnSameTeam(ent)) {
						color = ColorManager.getEnemyTeam().getColorInt();
					} else if (mc.thePlayer.canEntityBeSeen(ent)) {
						color = ColorManager.getEnemyVisible().getColorInt();
					} else if (!mc.thePlayer.canEntityBeSeen(ent)) {
						color = ColorManager.getEnemyInvisible().getColorInt();
					}

					float cos = (float) Math.cos((double) mc.thePlayer.rotationYaw * 0.017453292519943295);
					float sin = (float) Math.sin((double) mc.thePlayer.rotationYaw * 0.017453292519943295);
					float rotY = -(posZ * cos - posX * sin);
					float rotX = -(posX * cos + posZ * sin);
					if (rotY > (float) (size1 / 2 - 5)) {
						rotY = (float) (size1 / 2 - 5.0f);

					} else if (rotY < (float) (-size1 / 2 + 5)) {
						rotY = -size1 / 2 + 5;

					}
					if (rotX > (float) (size1 / 2) - 5.0f) {
						rotX = size1 / 2 - 5;

					} else if (rotX < (float) (-size1 / 2 + 5)) {
						rotX = -(float) (size1 / 2) + 5.0f;
					}

					RenderUtil.rectangleBordered((double) (xOffset + (float) (size1 / 2) + rotX) - 1.5,
							(double) (yOffset + (float) (size1 / 2) + rotY) - 1.5,
							(double) (xOffset + (float) (size1 / 2) + rotX) + 1.5,
							(double) (yOffset + (float) (size1 / 2) + rotY) + 1.5, 0.5, color, Colors2.getColor(44));
				}
				if (WP.getValueState() && wp.size() > 0) {
	                float finalXOffset = xOffset;
	                float finalYOffset = yOffset;
					wp.keySet().forEach(name -> {
						double[] pos = wp.get(name);
						final Vec3 targetVec = new Vec3(pos[0], pos[1], pos[2]);
//		        System.out.println(targetVec);
						Vec3 tV = targetVec;
						final int chunkX = (int) Math.floor(mc.thePlayer.getPositionVector().xCoord / 16.0);
						final int chunkZ = (int) Math.floor(mc.thePlayer.getPositionVector().zCoord / 16.0);
						if (!mc.theWorld.getChunkFromChunkCoords(chunkX, chunkZ).isLoaded()) {
							if (targetVec.distanceTo(
									mc.thePlayer.getPositionVector()) > mc.gameSettings.renderDistanceChunks) {
								tV = tV.scale(mc.gameSettings.renderDistanceChunks);
							}
						}
						float pTicks = mc.timer.renderPartialTicks;
						float posX = (float) ((tV.xCoord - playerOffsetX) * scale.getValueState().doubleValue());
						float posZ = (float) ((tV.zCoord - playerOffSetZ) * scale.getValueState().doubleValue());
						int color;

						// 雷达玩家颜色
						color = RenderUtil.rainbow(980);

						float cos = (float) Math.cos((double) mc.thePlayer.rotationYaw * 0.017453292519943295);
						float sin = (float) Math.sin((double) mc.thePlayer.rotationYaw * 0.017453292519943295);
						float rotY = -(posZ * cos - posX * sin);
						float rotX = -(posX * cos + posZ * sin);
						if (rotY > (float) (size1 / 2 - 3)) {
							rotY = (float) (size1 / 2) - 3.0f;

						} else if (rotY < (float) (-size1 / 2 + 3)) {
							rotY = -size1 / 2 + 3;

						}
						if (rotX > (float) (size1 / 2) - 3.0f) {
							rotX = size1 / 2 - 3;

						} else if (rotX < (float) (-size1 / 2 + 3)) {
							rotX = -(float) (size1 / 2) + 3.0f;
						}

						// 雷达玩家

						RenderUtil.rectangleBordered((double) (finalXOffset + (float) (size1 / 2) + rotX) - 1.5,
								(double) (finalYOffset + (float) (size1 / 2) + rotY) - 1.5,
								(double) (finalXOffset + (float) (size1 / 2) + rotX) + 1.5,
								(double) (finalYOffset + (float) (size1 / 2) + rotY) + 1.5, 0.5,
								Colors.getColor(0, 153, 255, 200), Colors2.getColor(12));
//						drawEnchantTags(name,
//								(int) ((finalXOffset + (float) (size1 / 2) + rotX)
//										- mc.fontRendererObj.getStringWidth(name) / 5),
//								(int) ((finalYOffset + (float) (size1 / 2) + rotY) + 2));
					});
				}
			}
		}
	}

	private static void drawEnchantTags(String Enchant, int x, int y) {
		String Enchants = Enchant;
		String[] LIST = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f",
				"m", "o", "r", "g" };
		for (String str : LIST) {
			Enchant = Enchant.replaceAll("§" + str, "");
		}
		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		x = x * 2;
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x + 1, y * 2, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x - 1, y * 2, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x, y * 2 + 1, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x, y * 2 - 1, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchants, x, y * 2, Colors.getColor(0, 153, 255, 255));
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	private float findAngle(float x, float x2, float y, float y2) {
		return (float) (Math.atan2(y2 - y, x2 - x) * 180 / Math.PI);
	}
}