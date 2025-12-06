package cn.Power.mod.mods.RENDER;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.events.EventRender2D;
import cn.Power.irc.network.server.data.User;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.AntiBot;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.util.ColorManager;
import cn.Power.util.ColorObject;
import cn.Power.util.Colors2;
import cn.Power.util.MathUtils;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ESP2D extends Mod {

	public static Value<String> mode = new Value("ESP2D", "Mode", 0);
	public static Value<String> boxmode = new Value("ESP2D", "Box", 0);
	public Value<Boolean> invis = new Value("ESP2D_Invisibles", true);
	public Value<Boolean> ARMOR = new Value("ESP2D_Armor", false);
	public Value<Boolean> HEALTH = new Value("ESP2D_Health", true);
	public Value<Boolean> box = new Value("ESP2D_Box", true);
	public Value<Boolean> items = new Value("ESP2D_Items", false);
	public Value<Boolean> team = new Value("ESP2D_Team", false);
	public Value<Boolean> name = new Value("ESP2D_Name", false);
	public Value<Boolean> Antibot = new Value("ESP2D_AntiBot", false);
	private static double gradualFOVModifier;

	public static Map entityPositionstop = new HashMap();
	public static Map entityPositionsbottom = new HashMap();
	private Map<EntityLivingBase, double[]> entityConvertedPointsMap = new HashMap<EntityLivingBase, double[]>();

	public ESP2D() {
		super("ESP2D", Category.RENDER);
		ArrayList<String> settings = new ArrayList();
		this.mode.mode.add("CSGO");

		this.boxmode.mode.add("Box");
		this.boxmode.mode.add("Corner A");
		this.boxmode.mode.add("Corner B");
		this.boxmode.mode.add("Split");
	}

	@Override
	public void onDisable() {

		super.onDisable();
	}

	@EventTarget
	public void onRender(EventRender event) {

		try {
			this.updatePositions();
		} catch (Exception exception) {
		}
	}

	@EventTarget(0)
	public void on2D(EventRender2D event) {
		if (this.mode.isCurrentMode("CSGO")) {
			setDisplayName("CSGO");
			GlStateManager.pushMatrix();
			ScaledResolution scaledRes = new ScaledResolution(mc);
			double twoDscale = (double) scaledRes.getScaleFactor()
					/ Math.pow((double) scaledRes.getScaleFactor(), 2.0D);
			GlStateManager.scale(twoDscale, twoDscale, twoDscale);
			Iterator var6 = this.entityConvertedPointsMap.keySet().iterator();

			while (true) {
				EntityPlayer ent;

				double[] renderPositions;
				double[] renderPositionsBottom;
				double[] renderPositionsX;
				double[] renderPositionsX1;
				double[] renderPositionsZ;
				double[] renderPositionsZ1;
				double[] renderPositionsTop1;
				double[] renderPositionsTop2;
				boolean shouldRender;
				do {
					if (!var6.hasNext()) {
						GL11.glScalef(1.0F, 1.0F, 1.0F);
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GlStateManager.popMatrix();
						RenderUtils.rectangle(0.0D, 0.0D, 0.0D, 0.0D, -1);
						return;
					}

					Entity entity = (Entity) var6.next();
					ent = (EntityPlayer) entity;
					renderPositions = (double[]) this.entityConvertedPointsMap.get(entity);
					renderPositionsBottom = new double[] { renderPositions[4], renderPositions[5], renderPositions[6] };
					renderPositionsX = new double[] { renderPositions[7], renderPositions[8], renderPositions[9] };
					renderPositionsX1 = new double[] { renderPositions[10], renderPositions[11], renderPositions[12] };
					renderPositionsZ = new double[] { renderPositions[13], renderPositions[14], renderPositions[15] };
					renderPositionsZ1 = new double[] { renderPositions[16], renderPositions[17], renderPositions[18] };
					renderPositionsTop1 = new double[] { renderPositions[19], renderPositions[20],
							renderPositions[21] };
					renderPositionsTop2 = new double[] { renderPositions[22], renderPositions[23],
							renderPositions[24] };
					shouldRender = renderPositions[3] > 0.0D && renderPositions[3] <= 1.0D
							&& renderPositionsBottom[2] > 0.0D && renderPositionsBottom[2] <= 1.0D
							&& renderPositionsX[2] > 0.0D && renderPositionsX[2] <= 1.0D && renderPositionsX1[2] > 0.0D
							&& renderPositionsX1[2] <= 1.0D && renderPositionsZ[2] > 0.0D && renderPositionsZ[2] <= 1.0D
							&& renderPositionsZ1[2] > 0.0D && renderPositionsZ1[2] <= 1.0D
							&& renderPositionsTop1[2] > 0.0D && renderPositionsTop1[2] <= 1.0D
							&& renderPositionsTop2[2] > 0.0D && renderPositionsTop2[2] <= 1.0D;
					if ((double) mc.thePlayer.getDistanceToEntity(ent) < 2.5D && renderPositionsTop1[1] < 0.0D) {
						shouldRender = false;
					}
				} while (!shouldRender);
				
				
				GlStateManager.pushMatrix();
				if ((invis.getValueState() || !ent.isInvisible()) && ent instanceof EntityPlayer
						&& !(ent instanceof EntityPlayerSP)) {

					try {
						GL11.glEnable(3042);
						GL11.glDisable(3553);
						RenderUtils.rectangle(0.0D, 0.0D, 0.0D, 0.0D, Colors2.getColor(0, 0));
						double[] xValues = new double[] { renderPositions[0], renderPositionsBottom[0],
								renderPositionsX[0], renderPositionsX1[0], renderPositionsZ[0], renderPositionsZ1[0],
								renderPositionsTop1[0], renderPositionsTop2[0] };
						double[] yValues = new double[] { renderPositions[1], renderPositionsBottom[1],
								renderPositionsX[1], renderPositionsX1[1], renderPositionsZ[1], renderPositionsZ1[1],
								renderPositionsTop1[1], renderPositionsTop2[1] };
						double x = renderPositions[0];
						double y = renderPositions[1];
						double endx = renderPositionsBottom[0];
						double endy = renderPositionsBottom[1];
						double[] mX = xValues;
						int mY = xValues.length;

						int hovering;
						double var1;
						for (hovering = 0; hovering < mY; ++hovering) {
							var1 = mX[hovering];
							if (var1 < x) {
								x = var1;
							}
						}

						mX = xValues;
						mY = xValues.length;

						for (hovering = 0; hovering < mY; ++hovering) {
							var1 = mX[hovering];
							if (var1 > endx) {
								endx = var1;
							}
						}

						mX = yValues;
						mY = yValues.length;

						for (hovering = 0; hovering < mY; ++hovering) {
							var1 = mX[hovering];
							if (var1 < y) {
								y = var1;
							}
						}

						mX = yValues;
						mY = yValues.length;

						for (hovering = 0; hovering < mY; ++hovering) {
							var1 = mX[hovering];
							if (var1 > endy) {
								endy = var1;
							}
						}

						double stack2;
						int var45;
						if (box.getValueState()) {
							var45 = ColorManager.getEnemyVisible().getColorInt();
							/*
							 * if(FriendManager.isFriend(ent.getName())) { var45 =
							 * mc.thePlayer.canEntityBeSeen(ent)?ColorManager.getFriendlyVisible().
							 * getColorInt():ColorManager.getFriendlyInvisible().getColorInt(); } else
							 */ if (!mc.thePlayer.canEntityBeSeen(ent)) {
								var45 = ColorManager.getEnemyInvisible().getColorInt();
							}

							if (team.getValueState()) {
								if (Teams.isOnSameTeam(ent)) {
									var45 = ColorManager.fTeam.getColorInt();
								} else {
									var45 = ColorManager.eTeam.getColorInt();
								}
							}
							
							
							double var46 = (endx - x) / 4.0D;
							var1 = (endx - x) / (double) (boxmode.isCurrentMode("Corner B") ? 4 : 3);
							stack2 = boxmode.isCurrentMode("Corner B") ? var46 : (endy - y) / 4.0D;

							if (boxmode.isCurrentMode("Corner B")) {
								RenderUtils.rectangle(x + 0.5D, y + 0.5D, x + 1.5D, y + stack2 + 0.5D, var45);
								RenderUtils.rectangle(x + 0.5D, endy - 0.5D, x + 1.5D, endy - stack2 - 0.5D, var45);
								RenderUtils.rectangle(x - 0.5D, y + 0.5D, x + 0.5D, y + stack2 + 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.5D, y + 2.5D, x + 2.5D, y + stack2 + 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x - 0.5D, y + stack2 + 0.5D, x + 2.5D, y + stack2 + 1.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x - 0.5D, endy - 0.5D, x + 0.5D, endy - stack2 - 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.5D, endy - 2.5D, x + 2.5D, endy - stack2 - 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x - 0.5D, endy - stack2 - 0.5D, x + 2.5D, endy - stack2 - 1.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.0D, y + 0.5D, x + var1, y + 1.5D, var45);
								RenderUtils.rectangle(x - 0.5D, y - 0.5D, x + var1, y + 0.5D, Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.5D, y + 1.5D, x + var1, y + 2.5D, Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + var1, y - 0.5D, x + var1 + 1.0D, y + 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.0D, endy - 0.5D, x + var1, endy - 1.5D, var45);
								RenderUtils.rectangle(x - 0.5D, endy + 0.5D, x + var1, endy - 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.5D, endy - 1.5D, x + var1, endy - 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + var1, endy + 0.5D, x + var1 + 1.0D, endy - 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 0.5D, y + 0.5D, endx - 1.5D, y + stack2 + 0.5D, var45);
								RenderUtils.rectangle(endx - 0.5D, endy - 0.5D, endx - 1.5D, endy - stack2 - 0.5D,
										var45);
								RenderUtils.rectangle(endx + 0.5D, y + 0.5D, endx - 0.5D, y + stack2 + 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.5D, y + 2.5D, endx - 2.5D, y + stack2 + 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx + 0.5D, y + stack2 + 0.5D, endx - 2.5D, y + stack2 + 1.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx + 0.5D, endy - 0.5D, endx - 0.5D, endy - stack2 - 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.5D, endy - 2.5D, endx - 2.5D, endy - stack2 - 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx + 0.5D, endy - stack2 - 0.5D, endx - 2.5D,
										endy - stack2 - 1.5D, Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.0D, y + 0.5D, endx - var1, y + 1.5D, var45);
								RenderUtils.rectangle(endx + 0.5D, y - 0.5D, endx - var1, y + 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.5D, y + 1.5D, endx - var1, y + 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - var1, y - 0.5D, endx - var1 - 1.0D, y + 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.0D, endy - 0.5D, endx - var1, endy - 1.5D, var45);
								RenderUtils.rectangle(endx + 0.5D, endy + 0.5D, endx - var1, endy - 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.5D, endy - 1.5D, endx - var1, endy - 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - var1, endy + 0.5D, endx - var1 - 1.0D, endy - 2.5D,
										Colors2.getColor(0, 150));
							} else if (boxmode.isCurrentMode("Box")) {
								RenderUtils.rectangleBordered(x + 0.5D, y + 0.5D, endx - 0.5D, endy - 0.5D, 1.0D,
										Colors2.getColor(0, 0, 0, 0), var45);
								RenderUtils.rectangleBordered(x - 0.5D, y - 0.5D, endx + 0.5D, endy + 0.5D, 1.0D,
										Colors2.getColor(0, 0), Colors2.getColor(0, 150));
								RenderUtils.rectangleBordered(x + 1.5D, y + 1.5D, endx - 1.5D, endy - 1.5D, 1.0D,
										Colors2.getColor(0, 0), Colors2.getColor(0, 150));
							} else if (boxmode.isCurrentMode("Split")) {
								RenderUtils.rectangle(x + 0.5D, y + 0.5D, x + 1.5D, endy - 0.5D, var45);
								RenderUtils.rectangle(x - 0.5D, y + 0.5D, x + 0.5D, endy - 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.5D, y + 2.5D, x + 2.5D, endy - 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.0D, y + 0.5D, x + var46, y + 1.5D, var45);
								RenderUtils.rectangle(x - 0.5D, y - 0.5D, x + var46, y + 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.5D, y + 1.5D, x + var46, y + 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + var46, y - 0.5D, x + var46 + 1.0D, y + 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.0D, endy - 0.5D, x + var46, endy - 1.5D, var45);
								RenderUtils.rectangle(x - 0.5D, endy + 0.5D, x + var46, endy - 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + 1.5D, endy - 1.5D, x + var46, endy - 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(x + var46, endy + 0.5D, x + var46 + 1.0D, endy - 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 0.5D, y + 0.5D, endx - 1.5D, endy - 0.5D, var45);
								RenderUtils.rectangle(endx + 0.5D, y + 0.5D, endx - 0.5D, endy - 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.5D, y + 2.5D, endx - 2.5D, endy - 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.0D, y + 0.5D, endx - var46, y + 1.5D, var45);
								RenderUtils.rectangle(endx + 0.5D, y - 0.5D, endx - var46, y + 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.5D, y + 1.5D, endx - var46, y + 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - var46, y - 0.5D, endx - var46 - 1.0D, y + 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.0D, endy - 0.5D, endx - var46, endy - 1.5D, var45);
								RenderUtils.rectangle(endx + 0.5D, endy + 0.5D, endx - var46, endy - 0.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - 1.5D, endy - 1.5D, endx - var46, endy - 2.5D,
										Colors2.getColor(0, 150));
								RenderUtils.rectangle(endx - var46, endy + 0.5D, endx - var46 - 1.0D, endy - 2.5D,
										Colors2.getColor(0, 150));
							}
						}

						int percent;
						float var54;
						double var63;

						if (HEALTH.getValueState()) {
							float var50 = ent.getHealth();
							float[] var47 = new float[] { 0.0F, 0.5F, 1.0F };
							Color[] var53 = new Color[] { Color.RED, Color.YELLOW, Color.GREEN };
							var54 = var50 / ent.getMaxHealth();
							Color stack = var50 >= 0.0F ? blendColors(var47, var53, var54).brighter() : Color.RED;
							stack2 = y - endy + 0.5D;
							var63 = endy + stack2 * (double) var54;
							RenderUtils.rectangleBordered(x - 6.5D, y - 0.5D, x - 2.5D, endy, 1.0D,
									Colors2.getColor(0, 100), Colors2.getColor(0, 150));
							RenderUtils.rectangle(x - 5.5D, endy - 1.0D, x - 3.5D, var63, stack.getRGB());
//	                             if(-stack2 > 50.0D) {
//	                                for(percent = 1; percent < 10; ++percent) {
//	                                   double pLevel = stack2 / 10.0D * (double)percent;
//	                                   RenderUtils.rectangle(x - 6.5D, endy - 0.5D + pLevel, x - 2.5D, endy - 0.5D + pLevel - 1.0D, Colors2.getColor(0));
//	                                }
//	                             }

							if (mc.thePlayer.getDistanceToEntity(ent) < 10) {
								GlStateManager.pushMatrix();
								GlStateManager.scale(1.0F, 1.0F, 1.0F);
								String var66 = (int) MathUtils.getIncremental((double) (var50), 1.0D) + " \247c❤";
								drawTags(var66, (int) (x - (double) (mc.fontRendererObj.getStringWidth(var66) * 1.5F)),
										((int) ((int) var63)), -1);
								GlStateManager.popMatrix();
							}
						}

						float var56;

						if (!ModManager.getModByClass(NameTag.class).isEnabled() && name.getValueState()) {
							BigDecimal bigDecimal = new BigDecimal((double) ent.getHealth());
							bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
							double HEALTH = bigDecimal.doubleValue();
							// RenderUtils.rectangle(0.0D, 0.0D, 0.0D, 0.0D, Colors2.getColor(0, 0));
							GlStateManager.pushMatrix();
							GlStateManager.scale(1.0F, 1.0F, 1.0F);
							
							 String User = "";
						        if (!Client.instance.IRC.userlist.isEmpty()) {
						            for (User user : Client.instance.IRC.userlist) {
						                if (ent.getName().equals(user.gameID)) {
						                    if (user.client.equalsIgnoreCase("FoodByte")) {
						                        User = "\2477(\247d" + user.name + "\2477)\247r";
						                    } else if (user.client.equalsIgnoreCase("PowerX")) {
						                        User = "\2477(\247b" + user.name + "\2477)\247r";
						                    } else {
						                        User = "\2477(\2472" + user.name + "\2477)\247r";
						                    }
						                    if (user.hide) {
						                        if(Client.instance.IRC.user.getRankLevel() >= 5){
						                            if (user.client.equalsIgnoreCase("FoodByte")) {
						                                User = ("\2477[\247d" + user.name + "\2477]\247r").replace(user.name,"\247o"+user.name);
						                            } else if (user.client.equalsIgnoreCase("PowerX")) {
						                                User = ("\2477[\247b" + user.name + "\2477]\247r").replace(user.name,"\247o"+user.name);
						                            } else {
						                                User = ("\2477[\2472" + user.name + "\2477]\247r").replace(user.name,"\247o"+user.name);
						                            }
						                        }else {
						                            User = "";
						                        }
						                    }
						                }
						            }
						        }
						        
							String var51 = ent.getDisplayName().getFormattedText() + " \2477[\247f" + HEALTH + "\247c❤"
									+ "\2477]"+User;
							var56 = (float) ((endx - x) / 2.0D
									- (double) (mc.fontRendererObj.getStringWidth(var51) / 2.0F));
							ColorObject var57 = ColorManager.getFriendlyVisible();
							int STRING_WIDTH = (int) (mc.fontRendererObj.getStringWidth(var51) / 2.0F);
							RenderUtil.drawRect((float) (x + (double) var56) - 2.0,
									(y - (double) (mc.fontRendererObj.FONT_HEIGHT / 1.5F * 2.0F)) / 1.0F - 6.0F,
									(float) (x + (double) var56) + mc.fontRendererObj.getStringWidth(var51) + 2,
									(y - (double) (mc.fontRendererObj.FONT_HEIGHT / 1.5F * 2.0F)) / 1.0F + 7F,
									new Color(0, 0, 0, 140).getRGB());
							drawTags(var51, (float) (x + (double) var56),
									(float) (y - (double) (mc.fontRendererObj.FONT_HEIGHT / 1.5F * 2.0F)) / 1.0F - 3.0F,
									-1);
							GlStateManager.popMatrix();
						}

						if (ent.getCurrentEquippedItem() != null && items.getValueState()) {
							GlStateManager.pushMatrix();
							GlStateManager.scale(2.5f, 2.5f, 2.5f);
							final ItemStack stack = ent.getCurrentEquippedItem();
							final String customName = ent.getCurrentEquippedItem().getDisplayName();
							final float meme3 = (float) ((endx - x) / 2.0
									- mc.fontRendererObj.getStringWidth(customName) / 2.0f);
							drawTags(customName, (float) (x + meme3 + mc.fontRendererObj.FONT_HEIGHT / 2.0f),
									(float) (endy + mc.fontRendererObj.FONT_HEIGHT / 2.0f * 2.0f) - 1, -1);
							
							GlStateManager.popMatrix();
						}

						var45 = scaledRes.getScaledWidth();
						mY = scaledRes.getScaledHeight();
						boolean var58 = (double) var45 > x - 15.0D && (double) var45 < endx + 15.0D
								&& (double) mY > y - 15.0D && (double) mY < endy + 15.0D;

						if (ARMOR.getValueState()) {
							var54 = (float) ((endy - y) / 4.0D);
							ItemStack var52 = ent.getEquipmentInSlot(4);
							int pLevel1;
							int var68;
							if (var52 != null) {
								RenderUtils.rectangleBordered(endx + 1.0D, y + 1.0D, endx + 5.0D, y + (double) var54,
										1.0D, Colors2.getColor(28, 156, 179, 100), Colors2.getColor(0, 150));
								float var59 = (float) (y + (double) var54 - 1.0D - (y + 2.0D));
								double stack3 = 1.0D - (double) var52.getItemDamage() / (double) var52.getMaxDamage();
								RenderUtils.rectangle(endx + 2.0D, y + (double) var54 - 1.0D, endx + 4.0D,
										y + (double) var54 - 1.0D - (double) var59 * stack3,
										Colors2.getColor(78, 206, 229));
								if (var58) {
									drawTags(var52.getMaxDamage() - var52.getItemDamage() + "", (float) endx + 22.0F,
											(float) (y + (double) var54 - 1.0D - (double) (var59 / 2.0F)), -1);
									GlStateManager.pushMatrix();
									GlStateManager.translate(endx + 4.0D,
											y + (double) var54 - 6.0D - (double) (var59 / 2.0F), 0.0D);
									RenderHelper.enableGUIStandardItemLighting();
									mc.getRenderItem().renderItemIntoGUI(var52, 0, 0);
									mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, var52, 0, 0);
									RenderHelper.disableStandardItemLighting();
									int var67 = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId,
											var52);
									percent = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, var52);
									var68 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId,
											var52);
									pLevel1 = 0;
									if (var67 > 0) {
										drawTags("P" + this.getColor(var67) + var67, 40.0F, 5.0F, -1);
										pLevel1 += 15;
									}

									if (percent > 0) {
										drawTags("Th" + this.getColor(percent) + percent, (float) (40 + pLevel1), 5.0F,
												-1);
										pLevel1 += 25;
									}

									if (var68 > 0) {
										drawTags("Unb" + this.getColor(var68) + var68, (float) (40 + pLevel1), 5.0F,
												-1);
									}

									GlStateManager.popMatrix();
								}
							}

							ItemStack var60 = ent.getEquipmentInSlot(3);
							int tLevel;
							if (var60 != null) {
								RenderUtils.rectangleBordered(endx + 1.0D, y + (double) var54, endx + 5.0D,
										y + (double) (var54 * 2.0F), 1.0D, Colors2.getColor(28, 156, 179, 100),
										Colors2.getColor(0, 150));
								float var61 = (float) (y + (double) (var54 * 2.0F) - (y + (double) var54 + 2.0D));
								var63 = 1.0D - (double) var60.getItemDamage() * 1.0D / (double) var60.getMaxDamage();
								RenderUtils.rectangle(endx + 2.0D, y + (double) (var54 * 2.0F), endx + 4.0D,
										y + (double) (var54 * 2.0F) - (double) var61 * var63,
										Colors2.getColor(78, 206, 229));
								if (var58) {
									drawTags(var60.getMaxDamage() - var60.getItemDamage() + "", (float) endx + 22.0F,
											(float) (y + (double) (var54 * 2.0F) - (double) (var61 / 2.0F)), -1);
									GlStateManager.pushMatrix();
									GlStateManager.translate(endx + 4.0D,
											y + (double) (var54 * 2.0F) - 6.0D - (double) (var61 / 2.0F), 0.0D);
									RenderHelper.enableGUIStandardItemLighting();
									mc.getRenderItem().renderItemIntoGUI(var60, 0, 0);
									mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, var60, 0, 0);
									RenderHelper.disableStandardItemLighting();
									percent = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId,
											var60);
									var68 = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, var60);
									pLevel1 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId,
											var60);
									tLevel = 0;
									if (percent > 0) {
										drawTags("P" + this.getColor(percent) + percent, 40.0F, 5.0F, -1);
										tLevel += 15;
									}

									if (var68 > 0) {
										drawTags("Th" + this.getColor(var68) + var68, (float) (40 + tLevel), 5.0F, -1);
										tLevel += 25;
									}

									if (pLevel1 > 0) {
										drawTags("Unb" + this.getColor(pLevel1) + pLevel1, (float) (40 + tLevel), 5.0F,
												-1);
									}

									GlStateManager.popMatrix();
								}
							}

							ItemStack var62 = ent.getEquipmentInSlot(2);
							int uLevel;
							if (var62 != null) {
								RenderUtils.rectangleBordered(endx + 1.0D, y + (double) (var54 * 2.0F), endx + 5.0D,
										y + (double) (var54 * 3.0F), 1.0D, Colors2.getColor(28, 156, 179, 100),
										Colors2.getColor(0, 150));
								float var64 = (float) (y + (double) (var54 * 3.0F)
										- (y + (double) (var54 * 2.0F) + 2.0D));
								double var69 = 1.0D
										- (double) var62.getItemDamage() * 1.0D / (double) var62.getMaxDamage();
								RenderUtils.rectangle(endx + 2.0D, y + (double) (var54 * 3.0F), endx + 4.0D,
										y + (double) (var54 * 3.0F) - (double) var64 * var69,
										Colors2.getColor(78, 206, 229));
								if (var58) {
									drawTags(var62.getMaxDamage() - var62.getItemDamage() + "", (float) endx + 22.0F,
											(float) (y + (double) (var54 * 3.0F) - (double) (var64 / 2.0F)), -1);
									GlStateManager.pushMatrix();
									GlStateManager.translate(endx + 4.0D,
											y + (double) (var54 * 3.0F) - 6.0D - (double) (var64 / 2.0F), 0.0D);
									RenderHelper.enableGUIStandardItemLighting();
									mc.getRenderItem().renderItemIntoGUI(var62, 0, 0);
									mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, var62, 0, 0);
									RenderHelper.disableStandardItemLighting();
									var68 = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId,
											var62);
									pLevel1 = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, var62);
									tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId,
											var62);
									uLevel = 0;
									if (var68 > 0) {
										drawTags("P" + this.getColor(var68) + var68, 40.0F, 5.0F, -1);
										uLevel += 15;
									}

									if (pLevel1 > 0) {
										drawTags("Th" + this.getColor(pLevel1) + pLevel1, (float) (40 + uLevel), 5.0F,
												-1);
										uLevel += 25;
									}

									if (tLevel > 0) {
										drawTags("Unb" + this.getColor(tLevel) + tLevel, (float) (40 + uLevel), 5.0F,
												-1);
									}

									GlStateManager.popMatrix();
								}
							}

							ItemStack var65 = ent.getEquipmentInSlot(1);
							if (var65 != null) {
								RenderUtils.rectangleBordered(endx + 1.0D, y + (double) (var54 * 3.0F), endx + 5.0D,
										y + (double) (var54 * 4.0F), 1.0D, Colors2.getColor(28, 156, 179, 100),
										Colors2.getColor(0, 150));
								float var70 = (float) (y + (double) (var54 * 4.0F)
										- (y + (double) (var54 * 3.0F) + 2.0D));
								double var71 = 1.0D
										- (double) var65.getItemDamage() * 1.0D / (double) var65.getMaxDamage();
								RenderUtils.rectangle(endx + 2.0D, y + (double) (var54 * 4.0F) - 1.0D, endx + 4.0D,
										y + (double) (var54 * 4.0F) - (double) var70 * var71,
										Colors2.getColor(78, 206, 229));
								if (var58) {
									drawTags(var65.getMaxDamage() - var65.getItemDamage() + "", (float) endx + 22.0F,
											(float) (y + (double) (var54 * 4.0F) - (double) (var70 / 2.0F)), -1);
									GlStateManager.pushMatrix();
									GlStateManager.translate(endx + 4.0D,
											y + (double) (var54 * 4.0F) - 6.0D - (double) (var70 / 2.0F), 0.0D);
									RenderHelper.enableGUIStandardItemLighting();
									mc.getRenderItem().renderItemIntoGUI(var65, 0, 0);
									mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, var65, 0, 0);
									RenderHelper.disableStandardItemLighting();
									pLevel1 = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId,
											var65);
									tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, var65);
									uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId,
											var65);
									int xOff = 0;
									if (pLevel1 > 0) {
										drawTags("P" + this.getColor(pLevel1) + pLevel1, 40.0F, 5.0F, -1);
										xOff += 15;
									}

									if (tLevel > 0) {
										drawTags("Th" + this.getColor(tLevel) + tLevel, (float) (40 + xOff), 5.0F, -1);
										xOff += 25;
									}

									if (uLevel > 0) {
										drawTags("Unb" + this.getColor(uLevel) + uLevel, (float) (40 + xOff), 5.0F, -1);
									}

									GlStateManager.popMatrix();
								}
							}
						}
					} catch (Exception var44) {
						;
					}
				}
				GlStateManager.popMatrix();
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
	}

	private static void drawTags(String Enchant, float f, float g, int color) {
		String Enchants = Enchant;
		String[] LIST = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f",
				"m", "o", "r", "g" };
		for (String str : LIST) {
			Enchant = Enchant.replaceAll("\247" + str, "\2470");
		}
		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
//	        f = (int) (f /2);
//	        GL11.glScalef(0.0F, 0.5F, 1.5F);
		
//		scale();
		
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, f + 1, g, 0, false);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, f - 1, g, 0, false);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, f, g + 1, 0, false);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, f, g - 1, 0, false);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchants, f, g, color, false);
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	private static void scale() {
		float scale = 1.0F;
		float target = scale * (mc.gameSettings.fovSetting / mc.gameSettings.fovSetting);
		if (gradualFOVModifier == 0.0D || Double.isNaN(gradualFOVModifier)) {
			gradualFOVModifier = (double) target;
		}

		gradualFOVModifier += ((double) target - gradualFOVModifier) / ((double) mc.getDebugFPS() * 0.7D);
		scale = (float) ((double) scale * gradualFOVModifier);
		GlStateManager.scale(scale, scale, scale);
	}

	public static Color blendColors(float[] fractions, Color[] colors, float progress) {
		Color color = null;
		if (fractions != null) {
			if (colors != null) {
				if (fractions.length == colors.length) {
					int[] indicies = getFractionIndicies(fractions, progress);
					float[] range = new float[] { fractions[indicies[0]], fractions[indicies[1]] };
					Color[] colorRange = new Color[] { colors[indicies[0]], colors[indicies[1]] };
					float max = range[1] - range[0];
					float value = progress - range[0];
					float weight = value / max;
					color = blend(colorRange[0], colorRange[1], (double) (1.0F - weight));
					return color;
				} else {
					throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
				}
			} else {
				throw new IllegalArgumentException("Colours can't be null");
			}
		} else {
			throw new IllegalArgumentException("Fractions can't be null");
		}
	}

	public static Color blend2(Color color1, Color color2, double ratio) {
		float r = (float) ratio;
		float ir = 1.0f - r;
		float[] rgb1 = new float[3];
		float[] rgb2 = new float[3];
		color1.getColorComponents(rgb1);
		color2.getColorComponents(rgb2);
		float red = rgb1[0] * r + rgb2[0] * ir;
		float green = rgb1[1] * r + rgb2[1] * ir;
		float blue = rgb1[2] * r + rgb2[2] * ir;
		if (red < 0.0f) {
			red = 0.0f;
		} else if (red > 255.0f) {
			red = 255.0f;
		}
		if (green < 0.0f) {
			green = 0.0f;
		} else if (green > 255.0f) {
			green = 255.0f;
		}
		if (blue < 0.0f) {
			blue = 0.0f;
		} else if (blue > 255.0f) {
			blue = 255.0f;
		}
		Color color = null;
		try {
			color = new Color(red, green, blue);
		} catch (IllegalArgumentException exp) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
			exp.printStackTrace();
		}
		return color;
	}

	public static void drawBorderedRect(float x, float y, float x2, float y2, float l1, int col1, int col2) {
		drawRect(x, y, x2, y2, col2);
		float f = (float) (col1 >> 24 & 255) / 255.0F;
		float f1 = (float) (col1 >> 16 & 255) / 255.0F;
		float f2 = (float) (col1 >> 8 & 255) / 255.0F;
		float f3 = (float) (col1 & 255) / 255.0F;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glPushMatrix();
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glLineWidth(l1);
		GL11.glBegin(1);
		GL11.glVertex2d((double) x, (double) y);
		GL11.glVertex2d((double) x, (double) y2);
		GL11.glVertex2d((double) x2, (double) y2);
		GL11.glVertex2d((double) x2, (double) y);
		GL11.glVertex2d((double) x, (double) y);
		GL11.glVertex2d((double) x2, (double) y);
		GL11.glVertex2d((double) x, (double) y2);
		GL11.glVertex2d((double) x2, (double) y2);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
	}

	public static void drawRect(float g, float h, float i, float j, int col1) {
		float f = (float) (col1 >> 24 & 255) / 255.0F;
		float f1 = (float) (col1 >> 16 & 255) / 255.0F;
		float f2 = (float) (col1 >> 8 & 255) / 255.0F;
		float f3 = (float) (col1 & 255) / 255.0F;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glPushMatrix();
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glBegin(7);
		GL11.glVertex2d((double) i, (double) h);
		GL11.glVertex2d((double) g, (double) h);
		GL11.glVertex2d((double) g, (double) j);
		GL11.glVertex2d((double) i, (double) j);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
	}

	public void pre() {
		GL11.glDisable((int) 2929);
		GL11.glDisable((int) 3553);
		GL11.glEnable((int) 3042);
		GL11.glBlendFunc((int) 770, (int) 771);
	}

	public void post() {
		GL11.glDisable((int) 3042);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glColor3d((double) 1.0, (double) 1.0, (double) 1.0);
	}

	private String getColor(int level) {
		return level == 2 ? "\u00a7a"
				: (level == 3 ? "\u00a73" : (level == 4 ? "\u00a74" : (level >= 5 ? "\u00a76" : "\u00a7f")));
	}

	public static int[] getFractionIndicies(float[] fractions, float progress) {
		int[] range = new int[2];

		int startPoint;
		for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
			;
		}

		if (startPoint >= fractions.length) {
			startPoint = fractions.length - 1;
		}

		range[0] = startPoint - 1;
		range[1] = startPoint;
		return range;
	}

	public static Color blend(Color color1, Color color2, double ratio) {
		float r = (float) ratio;
		float ir = 1.0F - r;
		float[] rgb1 = new float[3];
		float[] rgb2 = new float[3];
		color1.getColorComponents(rgb1);
		color2.getColorComponents(rgb2);
		float red = rgb1[0] * r + rgb2[0] * ir;
		float green = rgb1[1] * r + rgb2[1] * ir;
		float blue = rgb1[2] * r + rgb2[2] * ir;
		if (red < 0.0F) {
			red = 0.0F;
		} else if (red > 255.0F) {
			red = 255.0F;
		}

		if (green < 0.0F) {
			green = 0.0F;
		} else if (green > 255.0F) {
			green = 255.0F;
		}

		if (blue < 0.0F) {
			blue = 0.0F;
		} else if (blue > 255.0F) {
			blue = 255.0F;
		}

		Color color = null;

		try {
			color = new Color(red, green, blue);
		} catch (IllegalArgumentException var14) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			System.out.println(
					nf.format((double) red) + "; " + nf.format((double) green) + "; " + nf.format((double) blue));
			var14.printStackTrace();
		}

		return color;
	}

	private void updatePositions() {
		this.entityConvertedPointsMap.clear();
		float pTicks = mc.timer.renderPartialTicks;
		Iterator var2 = mc.theWorld.getLoadedEntityList().iterator();

		while (var2.hasNext()) {
			Object e2 = var2.next();
			if (e2 instanceof EntityPlayer) {
				EntityPlayer ent = (EntityPlayer) e2;
				if (ent != mc.thePlayer) {
					double x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks
							- mc.getRenderManager().viewerPosX + 0.36D;
					double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double) pTicks
							- mc.getRenderManager().viewerPosY;
					double z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks
							- mc.getRenderManager().viewerPosZ + 0.36D;
					y += (double) ent.height + 0.15D;
					double topY = y;
					double[] convertedPoints = RenderUtils.convertTo2D(x, y, z);
					double[] convertedPoints22 = RenderUtils.convertTo2D(x - 0.36D, y, z - 0.36D);
					double xd = 0.0D;
					if (convertedPoints22[2] >= 0.0D && convertedPoints22[2] < 1.0D) {
						x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks
								- mc.getRenderManager().viewerPosX - 0.36D;
						z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks
								- mc.getRenderManager().viewerPosZ - 0.36D;
						double[] convertedPointsBottom = RenderUtils.convertTo2D(x, y, z);
						y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double) pTicks
								- mc.getRenderManager().viewerPosY - 0.05D;
						double[] convertedPointsx = RenderUtils.convertTo2D(x, y, z);
						x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks
								- mc.getRenderManager().viewerPosX - 0.36D;
						z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks
								- mc.getRenderManager().viewerPosZ + 0.36D;
						double[] convertedPointsTop1 = RenderUtils.convertTo2D(x, topY, z);
						double[] convertedPointsx1 = RenderUtils.convertTo2D(x, y, z);
						x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks
								- mc.getRenderManager().viewerPosX + 0.36D;
						z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks
								- mc.getRenderManager().viewerPosZ + 0.36D;
						double[] convertedPointsz = RenderUtils.convertTo2D(x, y, z);
						x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks
								- mc.getRenderManager().viewerPosX + 0.36D;
						z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks
								- mc.getRenderManager().viewerPosZ - 0.36D;
						double[] convertedPointsTop2 = RenderUtils.convertTo2D(x, topY, z);
						double[] convertedPointsz1 = RenderUtils.convertTo2D(x, y, z);
						this.entityConvertedPointsMap.put(ent,
								new double[] { convertedPoints[0], convertedPoints[1], xd, convertedPoints[2],
										convertedPointsBottom[0], convertedPointsBottom[1], convertedPointsBottom[2],
										convertedPointsx[0], convertedPointsx[1], convertedPointsx[2],
										convertedPointsx1[0], convertedPointsx1[1], convertedPointsx1[2],
										convertedPointsz[0], convertedPointsz[1], convertedPointsz[2],
										convertedPointsz1[0], convertedPointsz1[1], convertedPointsz1[2],
										convertedPointsTop1[0], convertedPointsTop1[1], convertedPointsTop1[2],
										convertedPointsTop2[0], convertedPointsTop2[1], convertedPointsTop2[2] });
					}
				}
			}
		}

	}

	private double[] convertTo2D(double x, double y, double z) {
		double[] convertedPoints = RenderUtils.convertTo2D(x, y, z);
		return convertedPoints;
	}

}