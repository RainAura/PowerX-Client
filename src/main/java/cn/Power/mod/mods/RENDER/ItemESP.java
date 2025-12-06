package cn.Power.mod.mods.RENDER;

import java.awt.Color;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.WORLD.InvCleaner;
import cn.Power.util.ClientUtil;
import cn.Power.util.OutlineUtil;
import cn.Power.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Timer;

public class ItemESP extends Mod {
	private Value mode = new Value("ItemESP", "Mode", 0);
	private Value red = new Value("ItemESP_Red", 255.0D, 0.0D, 255.0D, 5.0D);
	private Value green = new Value("ItemESP_Green", 0.0D, 0.0D, 255.0D, 5.0D);
	private Value blue = new Value("ItemESP_Blue", 0.0D, 0.0D, 255.0D, 5.0D);
	private Value alpha = new Value("ItemESP_Alpha", 100.0D, 0.0D, 255.0D, 5.0D);
	public Value<Boolean> clean = new Value("ItemESP_CheckCleanner", false);
	// private ChestShader shader;
	private AxisAlignedBB Item = new AxisAlignedBB(-0.175D, 0.0D, -0.175D, 0.175D, 0.35D, 0.175D);

	public ItemESP() {
		super("ItemESP", Category.RENDER);
		this.mode.mode.add("2DBox");
		this.mode.mode.add("OutlinedBox");
		this.mode.mode.add("Shader");
		this.mode.mode.add("Box");
		this.mode.mode.add("Circle");
		this.mode.mode.add("2D");
		// this.shader = new ChestShader();
	}
	
	public boolean isTrash(EntityItem ei) {
		if(clean.getValueState().booleanValue()) {
			return ((InvCleaner)ModManager.getModByClass(InvCleaner.class)).shouldDrop(ei.getEntityItem(), mc.thePlayer.inventory.currentItem);
		}
		
		return false;
	}

	@EventTarget
	public void onRender(EventRender event) {
		int countMod = 0;
		int rainbow = Gui.rainbow(System.nanoTime(), (float) countMod, 1.0F).getRGB();
		GL11.glPushMatrix();
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(3553);
		GL11.glEnable(2884);
		GL11.glDisable(2929);
		double renderPosX = this.mc.getRenderManager().viewerPosX;
		double renderPosY = this.mc.getRenderManager().viewerPosY;
		double renderPosZ = this.mc.getRenderManager().viewerPosZ;
		GL11.glTranslated(-renderPosX, -renderPosY, -renderPosZ);
		GL11.glColor4d(((Double) this.red.getValueState()).doubleValue() / 255.0D,
				((Double) this.green.getValueState()).doubleValue() / 255.0D,
				((Double) this.blue.getValueState()).doubleValue() / 255.0D,
				((Double) this.alpha.getValueState()).doubleValue() / 255.0D);
		Iterator var11 = this.mc.theWorld.loadedEntityList.iterator();

		while (var11.hasNext()) {
			Entity entity = (Entity) var11.next();
			if (entity instanceof EntityItem && (!isTrash((EntityItem)entity))) {
				GL11.glPushMatrix();
				GL11.glTranslated(entity.posX, entity.posY, entity.posZ);
				if (this.mode.isCurrentMode("OutlinedBox")) {
					RenderUtil.drawOutlinedBoundingBox(this.Item);
				} else if (this.mode.isCurrentMode("Box")) {
					RenderUtil.drawBoundingBox(this.Item);
				} else if (this.mode.isCurrentMode("Circle")) {
					GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
					Gui.drawFilledCircle((float) ((int) this.Item.minX), (float) ((int) this.Item.minY) + 0.25F, 0.3F,
							ClientUtil.reAlpha(
									(new Color(((Double) this.red.getValueState()).intValue(),
											((Double) this.green.getValueState()).intValue(),
											((Double) this.blue.getValueState()).intValue())).getRGB(),
									((Double) this.alpha.getValueState()).floatValue() / 255.0F));
				} else if (this.mode.isCurrentMode("2D")) {
					GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
					GlStateManager.scale(-0.0267F, -0.0267F, 0.0267F);
					Gui.drawRect(-10, -18, 12, -20,
							ClientUtil.reAlpha(
									(new Color(((Double) this.red.getValueState()).intValue(),
											((Double) this.green.getValueState()).intValue(),
											((Double) this.blue.getValueState()).intValue())).getRGB(),
									((Double) this.alpha.getValueState()).floatValue() / 255.0F));
					Gui.drawRect(-10, 0, 12, -2,
							ClientUtil.reAlpha(
									(new Color(((Double) this.red.getValueState()).intValue(),
											((Double) this.green.getValueState()).intValue(),
											((Double) this.blue.getValueState()).intValue())).getRGB(),
									((Double) this.alpha.getValueState()).floatValue() / 255.0F));
					Gui.drawRect(10, -20, 12, 0,
							ClientUtil.reAlpha(
									(new Color(((Double) this.red.getValueState()).intValue(),
											((Double) this.green.getValueState()).intValue(),
											((Double) this.blue.getValueState()).intValue())).getRGB(),
									((Double) this.alpha.getValueState()).floatValue() / 255.0F));
					Gui.drawRect(-10, -20, -12, 0,
							ClientUtil.reAlpha(
									(new Color(((Double) this.red.getValueState()).intValue(),
											((Double) this.green.getValueState()).intValue(),
											((Double) this.blue.getValueState()).intValue())).getRGB(),
									((Double) this.alpha.getValueState()).floatValue() / 255.0F));
				}

				GL11.glPopMatrix();
			}
		}

		GL11.glColor4f(0.0F, 0.0F, 1.0F, 1.0F);
		GL11.glEnable(2929);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glPopMatrix();
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (this.mode.isCurrentMode("Shader")) {
			if (this.mc.gameSettings.ofFastRender) {
				this.set(false);
				// ClientUtil.sendClientMessage("Options->Video Settings->Performance->Fast
				// Render->Off", ClientNotification.Type.ERROR);
				return;
			}

			/*
			 * if (ModManager.getModByClass(ESP.class).isEnabled() &&
			 * ESP.modes.isCurrentMode("Shader")) { this.set(false);
			 * ClientUtil.sendClientMessage("Please disable ShaderESP",
			 * ClientNotification.Type.ERROR); return; }
			 */

			// this.shader.startShader();
			Minecraft.getMinecraft().entityRenderer.setupCameraTransform(this.mc.timer.renderPartialTicks, 0);
			Iterator var3 = Minecraft.getMinecraft().theWorld.loadedEntityList.iterator();

			while (var3.hasNext()) {
				Object o = var3.next();
				Entity entity = (Entity) o;
				if (entity instanceof EntityItem && (!isTrash((EntityItem)entity))) {
					Minecraft.getMinecraft().entityRenderer.disableLightmap();
					RenderHelper.disableStandardItemLighting();
					Render entityRender = this.mc.getRenderManager().getEntityRenderObject(entity);
					if (entityRender != null) {
						RenderItem.notRenderingEffectsInGUI = false;
						entityRender.doRender(entity,
								interpolate(entity.posX, entity.lastTickPosX) - mc.getRenderManager().renderPosX,
								interpolate(entity.posY, entity.lastTickPosY) - mc.getRenderManager().renderPosY,
								interpolate(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().renderPosZ,
								this.mc.thePlayer.rotationYaw, this.mc.timer.renderPartialTicks);
						RenderItem.notRenderingEffectsInGUI = true;
					}
				}
			}

			Minecraft.getMinecraft().entityRenderer.disableLightmap();
			RenderHelper.disableStandardItemLighting();
			Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();
			// this.shader.stopShader();
			Gui.drawRect(0, 0, 0, 0, 0);
		}

	}

	public static double interpolate(double now, double then) {
		return then + (now - then) * (double) Minecraft.getMinecraft().timer.renderPartialTicks;
	}

	public double[] interpolate(Entity entity) {
		double posX = interpolate(entity.posX, entity.lastTickPosX) - mc.getRenderManager().renderPosX;
		double posY = interpolate(entity.posY, entity.lastTickPosY) - mc.getRenderManager().renderPosY;
		double posZ = interpolate(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().renderPosZ;
		return new double[] { posX, posY, posZ };
	}

	@EventTarget
	public void onitemESP(EventRender event) {
		if (this.mode.isCurrentMode("2DBox")) {
			OutlineUtil.renderOne((float) 1.5);
			renderEntitiesBoxed(event.getPartialTicks(), new Color(255, 255, 255));
			OutlineUtil.renderTwo();
			renderEntitiesBoxed(event.getPartialTicks(), new Color(255, 255, 255));
			OutlineUtil.renderThree();
			renderEntitiesBoxed(event.getPartialTicks(), new Color(255, 255, 255));
			OutlineUtil.renderFour(new Color(255, 255, 255).getRGB());
			renderEntitiesBoxed(event.getPartialTicks(), new Color(255, 255, 255));
			OutlineUtil.renderFive();
			OutlineUtil.setColor(Color.white);
//			for (Object o : mc.theWorld.loadedEntityList) {
//				Entity entity = (Entity)o;
//		          if(entity instanceof EntityItem) {
//		   		//	RenderUtil.ItemESP((Entity) o, new Color(255, 255, 255), event);
//		   		    double x = entity.lastTickPosX - 0.2D + (entity.posX - 0.2D - (entity.lastTickPosX - 0.2D)) * (double)event.getPartialTicks() - mc.getRenderManager().renderPosX;
//		             double y = entity.lastTickPosY+ (entity.posY - entity.lastTickPosY) * (double)event.getPartialTicks() - mc.getRenderManager().renderPosY;
//		             double z = entity.lastTickPosZ - 0.2D + (entity.posZ - 0.2D - (entity.lastTickPosZ - 0.2D)) * (double)event.getPartialTicks() - mc.getRenderManager().renderPosZ;
//		             GL11.glPushMatrix();
//		             GL11.glColor4d(1.0D, 1.0D, 1.0D, 0.0D);
//		             RenderUtil.renderOne(2.0F);
//		             RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 0.4D, y + 0.4D, z + 0.4D));
//		             RenderUtil.renderTwo();
//		             RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 0.4D, y + 0.4D, z + 0.4D));
//		             RenderUtil.renderThree();
//		             RenderUtil.renderFour();
//		             RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 0.4D, y + 0.4D, z + 0.4D));
//		             RenderUtil.renderFive();
//		             GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
//		             GL11.glPopMatrix();
//		   			}
//					}
		}
	}

	private void renderEntitiesBoxed(float partialTicks, Color color) {
		for (final Entity entity : mc.theWorld.loadedEntityList) {
			if (entity instanceof EntityItem && (!isTrash((EntityItem)entity))) {
				drawBox(entity, color);

			}
		}
	}

	public final void drawBox(Entity entity, Color color) {
		final RenderManager renderManager = mc.getRenderManager();
		final Timer timer = mc.timer;

		final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks
				- mc.getRenderManager().renderPosX;
		final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks
				- mc.getRenderManager().renderPosY;
		final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks
				- mc.getRenderManager().renderPosZ;

		final AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox()
				.offset(-entity.posX, -entity.posY, -entity.posZ).offset(x, y, z);

		final double offset = -.1;

		drawAxisAlignedBBFilled(
				new AxisAlignedBB(axisAlignedBB.minX + offset, axisAlignedBB.minY, axisAlignedBB.minZ + offset,
						axisAlignedBB.maxX - offset, axisAlignedBB.maxY - offset, axisAlignedBB.maxZ - offset),
				color, true);
	}

	public final void drawAxisAlignedBBFilled(AxisAlignedBB axisAlignedBB, Color color, boolean depth) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if (depth)
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4d(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
		drawBoxFilled(axisAlignedBB);
		GlStateManager.resetColor();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		if (depth)
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public final void drawBoxFilled(AxisAlignedBB axisAlignedBB) {
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);

			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);

			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);

			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);

			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);

			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);

			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);

			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);

			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);

			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);

			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);

			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
			GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
			GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
		}
		GL11.glEnd();
	}

	@Override
	public void onDisable() {
		super.onDisable();

	}

	public void onEnable() {
		super.isEnabled();

	}
}
