package cn.Power.mod.mods.RENDER;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.irc.network.server.data.User;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.util.GLUtil;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.friendManager.FriendManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class NameTag extends Mod {

	public Value<Boolean> Health = new Value("NameTag_Health", true);
	public Value<Boolean> Armor = new Value("NameTag_Armor", true);
	public Value<Boolean> Dis = new Value("NameTag_Dis", false);
	public Value<Boolean> Antibot = new Value("NameTag_Antibot", false);
	public Value<Boolean> invis = new Value("NameTag_Invisibles", true);

	public Value<Boolean> Effect = new Value("NameTag_Effect", false);
	public Value<Boolean> self = new Value("NameTag_Self", false);

	private Value<Double> size = new Value<Double>("NameTag_Size", Double.valueOf(1D), Double.valueOf(1D),
			Double.valueOf(5D), 0.1D);

	public NameTag() {
		super("NameTag", Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender event) {
		// render00();
		mc.theWorld.loadedEntityList.stream()
				.filter(o2 -> o2 instanceof EntityPlayer && (self.getValueState() || o2 != Minecraft.thePlayer)
						&& (!(o2).isInvisible() || this.invis.getValueState().booleanValue())
						)
				.forEach(entity -> {

					double pX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks
							- mc.getRenderManager().renderPosX;
					double pY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks
							- mc.getRenderManager().renderPosY;
					double pZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks
							- mc.getRenderManager().renderPosZ;

					renderNameTag((EntityPlayer) entity, entity.getName(), pX, pY, pZ);
				});

	}

	private void renderNameTag(EntityPlayer entity, String tag, double pX, double pY, double pZ) {
		ScaledResolution sr = new ScaledResolution(this.mc);

		FontRenderer fr = mc.fontRendererObj;
		float size = mc.thePlayer.getDistanceToEntity(entity) / 6.0f;
		if (size < 1.1f) {
			size = 1.1f;
		}
		pY += (entity.isSneaking() ? 0.5D : 0.7D);
		float scale = (float) (size * this.size.getValueState().doubleValue());
		scale /= 100f;
		tag = entity.getDisplayName().getFormattedText();
		String frend = entity.getName();
		String bot = "";
		String team = "";
		if (Teams.isOnSameTeam(entity)) {
			team = "\247a[T] ";
		} else {
			team = "";
		}
		String Friend = "";
		if (FriendManager.isFriend(entity)) {
			Friend = "\247b[F] ";
		}
		
		  String User = "";
	        if (!Client.instance.IRC.userlist.isEmpty()) {
	            for (User user : Client.instance.IRC.userlist) {
	                if (entity.getName().equals(user.gameID)) {
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
	        
		String lol = (FriendManager.isFriend(entity) ? "" : team)
				+ (KillAura.vips.contains(entity.getName().toLowerCase()) ? "\2478[Target] " : "") + bot + Friend
				+ (FriendManager.isFriend(entity) ? frend : tag) + User;

		double plyHeal = entity.getHealth();
		BigDecimal bigDecimal = new BigDecimal((double) entity.getHealth());
		bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
		double HEALTH = bigDecimal.doubleValue();
		String COLOR1;
		if (HEALTH > 20.0D) {
			COLOR1 = " \247b";
		} else if (HEALTH >= 10.0D) {
			COLOR1 = " \247a";
		} else if (HEALTH >= 3.0D) {
			COLOR1 = " \247e";
		} else {
			COLOR1 = " \2474";
		}
		String hp = "";
		if (this.Health.getValueState().booleanValue()) {

			hp = COLOR1 + String.valueOf(HEALTH) /* +" \247c" */;
		} else {
			hp = "";
		}

		String dt = "";
		if (this.Dis.getValueState().booleanValue()) {

			dt = "\247a[\2476" + (int) entity.getDistanceToEntity(mc.thePlayer) + "\247a]\247r";
		} else {
			dt = "";
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) pX, (float) pY + 1.4F, (float) pZ);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(mc.gameSettings.thirdPersonView == 2 ? -mc.getRenderManager().playerViewX
				: mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-scale, -scale, scale);
		GLUtil.setGLCap(2896, false);
		GLUtil.setGLCap(2929, false);
		int width = sr.getScaledHeight() / 2;
		int Height = sr.getScaledHeight() / 2;
		GLUtil.setGLCap(3042, true);
		GL11.glBlendFunc(770, 771);
//hui zhi
		String USERNAME = dt + lol + hp + " ";
		int STRING_WIDTH = (int) (fr.getStringWidth(USERNAME) / 2.0F);
		RenderUtil.drawRect(-STRING_WIDTH - 1.0f, -15.0f, STRING_WIDTH, -4.0f, new Color(0, 0, 0, 120).getRGB());
		GL11.glColor3f(1, 1, 1);
		fr.drawStringWithShadow(USERNAME, -fr.getStringWidth(USERNAME) / 2.0F + 2, fr.FONT_HEIGHT - 22, 16777215);
		GL11.glScaled(0.6f, 0.6f, 0.6f);
		GL11.glScaled(1, 1, 1);
		int COLOR = new Color(188, 0, 0).getRGB();
		if (entity.getHealth() > 20) {
			COLOR = -65292;
		}

		// RenderUtil.drawRect(-STRING_WIDTH - 1.0f + 149.2f * Math.min(1.0f,
		// entity.getHealth() / 20.0f), -5f, -STRING_WIDTH , -8f, COLOR);

		GL11.glPushMatrix();
		GL11.glPopMatrix();
		GL11.glScaled(1.5d, 1.5d, 1.5d);

		if (this.Armor.getValueState().booleanValue() && entity instanceof EntityPlayer) {

			int xOffset = 0;

			for (ItemStack armourStack : (entity).inventory.armorInventory) {
				if (armourStack != null)
					xOffset -= 10;
			}
			Object renderStack;
			if (entity.getHeldItem() != null) {
				xOffset -= 8;
				renderStack = entity.getHeldItem().copy();
				if ((((ItemStack) renderStack).hasEffect())
						&& (((((ItemStack) renderStack).getItem() instanceof ItemTool))
								|| ((((ItemStack) renderStack).getItem() instanceof ItemArmor))))
					((ItemStack) renderStack).stackSize = 1;

				renderItemStack((ItemStack) renderStack, xOffset, -36);
				xOffset += 20;
			}
			
			
			for (ItemStack armourStack : (entity).inventory.armorInventory)
				if (armourStack != null) {
					ItemStack renderStack1 = armourStack.copy();
					if ((renderStack1.hasEffect()) && (((renderStack1.getItem() instanceof ItemTool))
							|| ((renderStack1.getItem() instanceof ItemArmor))))
						renderStack1.stackSize = 1;
					renderItemStack(renderStack1, xOffset, -36);
					xOffset += 20;
				}
		}
		
		
		
		
		if(Effect.getValueState()) {
		
		final int var1 = 23;
		int var2 = -70;
		final int w = -12;
		final int h = 36;
		final boolean flippedVer = false;
		final boolean flippedHor = false;
		if (flippedVer) {
			var2 = var2 + h - 30;
		}
		final Collection var3 = entity.getActivePotionEffects();
		if (!var3.isEmpty()) {
			GlStateManager.color(1.0f, 1.0f, 1.0f, 0.9f);
			GL11.glDisable(2896);
			int var4 = 27;
			final int defaultEffectAmount = 182 / var4;
			if (var3.size() > defaultEffectAmount) {
				var4 = 182 / var3.size();
			}
			final int totalSize = var3.size() * var4;
			int currentX = var1 - w / 2 - totalSize - (var4 - 18) / 2;
			final int currentY = var2 + 7;
			for (final PotionEffect var101 : entity.getActivePotionEffects()) {

				final int duration2 = var101.getDuration();
				// GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				if (duration2 >= 300) {
					GlStateManager.color(1.0f, 1.0f, 1.0f, 0.75f);
				} else if (duration2 >= 150) {
					GlStateManager.color(1.0f, 1.0f, 1.0f, 0.4f);
				} else {
					GlStateManager.color(1.0f, 1.0f, 1.0f, 0.2f);
				}

				final Potion var11 = Potion.potionTypes[var101.getPotionID()];
				this.mc.getTextureManager().bindTexture(Hud.inv);
				if (var11.hasStatusIcon()) {
					final int var12 = var11.getStatusIconIndex();
					((Gui) this.mc.ingameGUI).drawTexturedModalRect(currentX, currentY, 0 + var12 % 8 * 18,
							198 + var12 / 8 * 18, 18, 18);
				}
				currentX += var4;
			}

		}
		
		}

		GLUtil.revertAllCaps();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	private void render00() {
		ScaledResolution sr = new ScaledResolution(this.mc);
		BlockPos pos = new BlockPos(0, 80, 0);
		float xDiff = (float) (mc.thePlayer.posX - pos.getX());
		float yDiff = (float) (mc.thePlayer.posY - pos.getY());
		float zDiff = (float) (mc.thePlayer.posZ - pos.getZ());
		float dis = MathHelper.sqrt_float(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
		this.mc.getRenderManager();
		double xRender = (double) pos.getX() - mc.getRenderManager().renderPosX;
		this.mc.getRenderManager();
		double yRender = (double) pos.getY() - mc.getRenderManager().renderPosY;
		this.mc.getRenderManager();
		double zRender = (double) pos.getZ() - mc.getRenderManager().renderPosZ;

		FontRenderer fr = mc.fontRendererObj;
		float size = dis / 6;
		if (size < 1.0f) {
			size = 1.0f;
		}
		float scale = (float) (size * this.size.getValueState().doubleValue());
		scale /= 100f;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) xRender, (float) yRender + 1.4F, (float) zRender);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(mc.gameSettings.thirdPersonView == 2 ? -mc.getRenderManager().playerViewX
				: mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-scale, -scale, scale);
		GLUtil.setGLCap(2896, false);
		GLUtil.setGLCap(2929, false);
		int width = sr.getScaledHeight() / 2;
		int Height = sr.getScaledHeight() / 2;
		GL11.glBlendFunc(770, 771);
//hui zhi
		String USERNAME = "00";
		int STRING_WIDTH = (int) (fr.getStringWidth(USERNAME) / 2.0F);
		RenderUtils.drawRect(-STRING_WIDTH - 1.0f, -15.0f, STRING_WIDTH, -4.0f, new Color(0, 0, 0, 120).getRGB());
		fr.drawStringWithShadow(USERNAME, -fr.getStringWidth(USERNAME) / 2.0F + 2, fr.FONT_HEIGHT - 22, 16777215);

		GLUtil.revertAllCaps();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	public void renderItemStack(ItemStack stack, int x, int y) {
		/*
		 * ArrayList<ItemStack> itemsToRender = new ArrayList<ItemStack>();
		 * GlStateManager.pushMatrix(); GlStateManager.depthMask(true);
		 * 
		 * RenderHelper.enableStandardItemLighting();
		 * 
		 * mc.getRenderItem().zLevel = -150.0f; GlStateManager.disableDepth();
		 * GlStateManager.disableTexture2D(); GlStateManager.enableBlend();
		 * GlStateManager.enableAlpha(); GlStateManager.enableTexture2D();
		 * GlStateManager.enableLighting();
		 * 
		 * mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		 * 
		 * mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, y);
		 * mc.getRenderItem().zLevel = 0.0f;
		 * 
		 * RenderHelper.disableStandardItemLighting();
		 * 
		 * GlStateManager.enableAlpha(); GlStateManager.disableBlend();
		 * GlStateManager.disableLighting();
		 * 
		 * GlStateManager.scale(0.5, 0.5, 0.5);
		 * 
		 * 
		 * GlStateManager.disableDepth(); renderEnchantText(stack, x, y-17);
		 * GlStateManager.enableDepth(); GlStateManager.scale(2.0f, 2.0f, 2.0f);
		 * GlStateManager.popMatrix();
		 */
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.clear(256);
		RenderHelper.enableStandardItemLighting();
		this.mc.getRenderItem().zLevel = -150.0f;
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		this.mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, stack, x, y);
		this.mc.getRenderItem().zLevel = 0.0f;
		RenderHelper.disableStandardItemLighting();
//		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
		final double s = 0.5;
		GlStateManager.scale(s, s, s);
		GlStateManager.disableDepth();
		this.renderEnchantText(stack, x, y - 17);
		GlStateManager.enableDepth();
		GlStateManager.scale(2.0f, 2.0f, 2.0f);
		GlStateManager.popMatrix();
//		GlStateManager.enableCull();
	}

	public static void drawBorderedRect(float x, float y, float x2, float y2, float l1, int col1, int col2) {
		NameTag.drawRect(x, y, x2, y2, col2);
		float f = (float) (col1 >> 24 & 255) / 255.0f;
		float f1 = (float) (col1 >> 16 & 255) / 255.0f;
		float f2 = (float) (col1 >> 8 & 255) / 255.0f;
		float f3 = (float) (col1 & 255) / 255.0f;
		GL11.glEnable((int) 3042);
		GL11.glDisable((int) 3553);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glEnable((int) 2848);
		GL11.glPushMatrix();
		GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
		GL11.glLineWidth((float) l1);
		GL11.glBegin((int) 1);
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
		GL11.glEnable((int) 3553);
		GL11.glDisable((int) 3042);
		GL11.glDisable((int) 2848);
	}

	public static void drawRect(float g, float h, float i, float j, int col1) {
		float f = (float) (col1 >> 24 & 255) / 255.0f;
		float f1 = (float) (col1 >> 16 & 255) / 255.0f;
		float f2 = (float) (col1 >> 8 & 255) / 255.0f;
		float f3 = (float) (col1 & 255) / 255.0f;
		GL11.glEnable((int) 3042);
		GL11.glDisable((int) 3553);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glEnable((int) 2848);
		GL11.glPushMatrix();
		GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
		GL11.glBegin((int) 7);
		GL11.glVertex2d((double) i, (double) h);
		GL11.glVertex2d((double) g, (double) h);
		GL11.glVertex2d((double) g, (double) j);
		GL11.glVertex2d((double) i, (double) j);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable((int) 3553);
		GL11.glDisable((int) 3042);
		GL11.glDisable((int) 2848);
	}

	private void renderEnchantText(final ItemStack stack, final int x, final int y) {
		int enchantmentY = y - 24;
		if (stack.getEnchantmentTagList() != null && stack.getEnchantmentTagList().tagCount() >= 6) {
			mc.fontRendererObj.drawStringWithShadow("god", x * 2, enchantmentY, 16711680);
			return;
		}
		if (stack.getItem() instanceof ItemArmor) {
			final int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
			final int projectileProtectionLevel = EnchantmentHelper
					.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack);
			final int blastProtectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId,
					stack);
			final int fireProtectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId,
					stack);
			final int thornsLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
			final int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
			if (stack.getItem() instanceof ItemArmor) {
				int damage = stack.getMaxDamage() - stack.getItemDamage();
				// mc.fontRendererObj.drawStringWithShadow("" + damage, x * 2, y, 0xFFFFFF);
			}
			if (protectionLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("pr" + protectionLevel, x * 2, enchantmentY, 0x00CCFF);
				enchantmentY += 8;
			}
			if (projectileProtectionLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("pp" + projectileProtectionLevel, x * 2, enchantmentY,
						0x00CCFF);
				enchantmentY += 8;
			}
			if (blastProtectionLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("bp" + blastProtectionLevel, x * 2, enchantmentY, 0x00CCFF);
				enchantmentY += 8;
			}
			if (fireProtectionLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("fp" + fireProtectionLevel, x * 2, enchantmentY, 0x00CCFF);
				enchantmentY += 8;
			}
			if (thornsLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("t" + thornsLevel, x * 2, enchantmentY, 0x00CCFF);
				enchantmentY += 8;
			}
			if (unbreakingLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("u" + unbreakingLevel, x * 2, enchantmentY, 0x00CCFF);
				enchantmentY += 8;
			}
		}
		if (stack.getItem() instanceof ItemBow) {
			final int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
			final int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
			final int flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
			final int unbreakingLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
			if (powerLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("po" + powerLevel, x * 2, enchantmentY, 0x00FFFF);
				enchantmentY += 8;
			}
			if (punchLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("pu" + punchLevel, x * 2, enchantmentY, 0x00FFFF);
				enchantmentY += 8;
			}
			if (flameLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("f" + flameLevel, x * 2, enchantmentY, 0x00FFFF);
				enchantmentY += 8;
			}
			if (unbreakingLevel2 > 0) {
				mc.fontRendererObj.drawStringWithShadow("u" + unbreakingLevel2, x * 2, enchantmentY, 0x00FFFF);
				enchantmentY += 8;
			}
		}
		if (stack.getItem() instanceof ItemSword) {
			final int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
			final int knockbackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
			final int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
			final int unbreakingLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);

			if (sharpnessLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("sh" + sharpnessLevel, x * 2, enchantmentY, 0x00FFFF);
				enchantmentY += 8;
			}
			if (knockbackLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("kn" + knockbackLevel, x * 2, enchantmentY, 0x00FFFF);
				enchantmentY += 8;
			}
			if (fireAspectLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("f" + fireAspectLevel, x * 2, enchantmentY, 0x00FFFF);
				enchantmentY += 8;
			}
			if (unbreakingLevel2 > 0) {
				mc.fontRendererObj.drawStringWithShadow("ub" + unbreakingLevel2, x * 2, enchantmentY, 0x00FFFF);
			}
		}
		if (stack.getItem() instanceof ItemTool) {
			final int unbreakingLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
			final int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
			final int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
			final int silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack);
			if (efficiencyLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("eff" + efficiencyLevel, x * 2, enchantmentY, 0x00FFFF);
				enchantmentY += 8;
			}
			if (fortuneLevel > 0) {
				mc.fontRendererObj.drawStringWithShadow("fo" + fortuneLevel, x * 2, enchantmentY, 0x00FFFF);
				enchantmentY += 8;
			}
			if (silkTouch > 0) {
				mc.fontRendererObj.drawStringWithShadow("st" + silkTouch, x * 2, enchantmentY, 0x00FFFF);
				enchantmentY += 8;
			}
			if (unbreakingLevel2 > 0) {
				mc.fontRendererObj.drawStringWithShadow("ub" + unbreakingLevel2, x * 2, enchantmentY, 0x00FFFF);
			}
		}
		if (stack.getItem() == Items.golden_apple && stack.hasEffect()) {
			mc.fontRendererObj.drawStringWithShadow("god", x * 2, enchantmentY, 0x00CCFF);
		}
	}

}
