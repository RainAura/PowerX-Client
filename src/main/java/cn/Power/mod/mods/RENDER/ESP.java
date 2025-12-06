package cn.Power.mod.mods.RENDER;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.events.EventRenderGui;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.AntiBot;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.notification.Notification.Type;
import cn.Power.util.Colors;
import cn.Power.util.FlatColors;
import cn.Power.util.RenderUtil;
import cn.Power.util.friendManager.FriendManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityHopper;

public class ESP extends Mod {

	public static Value<String> mode = new Value("ESP", "Mode", 0);
	public Value<Boolean> player = new Value("ESP_Player", true);
	public Value<Boolean> animals = new Value("ESP_Animals", false);
	public Value<Boolean> mobs = new Value("ESP_Mobs", false);
	public Value<Boolean> invis = new Value("ESP_Invisibles", true);
	public Value<Boolean> Antibot = new Value("ESP_Antibot", true);
	
    private Framebuffer entityFBO;
    public boolean render;
    private Outline entityOutline;

	public ESP() {
		super("ESP", Category.RENDER);
		ArrayList<String> settings = new ArrayList();
		this.mode.mode.add("2D");
		this.mode.mode.add("Box");
		this.mode.mode.add("3DBox");
		this.mode.mode.add("Candy");
		this.mode.mode.add("Other2D");
		this.mode.mode.add("Outline");
		this.mode.mode.add("Twinkle");
	}

	public static boolean isOutline = false;

	@Override
	public void onDisable() {
		isOutline = false;
		super.onDisable();
	}

	@EventTarget
	public void onRender(EventRender event) {
		if (this.mode.isCurrentMode("2D")) {
			this.setDisplayName("2D");
			this.doCornerESP();
		}
		if (this.mode.isCurrentMode("Box")) {
			this.setDisplayName("Box");
			this.doBoxESP(event);
			// this.onRenderWorldToScreen();
		}
		if (this.mode.isCurrentMode("3DBox")) {
			this.setDisplayName("3DBox");
			this.PlayerBoxESP(event);
		}
		if (this.mode.isCurrentMode("Candy")) {
			this.setDisplayName("Candy");
			this.doCandyESP();
		}
		if (this.mode.isCurrentMode("Other2D")) {
			this.setDisplayName("Other2D");
			this.doOther2DESP();
		}
		if (this.mode.isCurrentMode("Outline")) {
			this.setDisplayName("Outline");

			isOutline = true;
		} else {
			isOutline = false;
		}
	
	}
	
	@EventTarget
	public void EventGui(EventRenderGui ui) {
		if (this.mode.isCurrentMode("Twinkle")) {
			this.setDisplayName("Twinkle");
			if (this.mc.gameSettings.ofFastRender) {
				this.set(false);
				Client.instance.getNotificationManager().addNotification("Options->Video Settings->Performance->Fast Render->Off",
						Type.ERROR);
				return;
			}else {
			
				   if (this.entityFBO == null) {
			            this.entityFBO = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
			        }
			        else if (this.entityFBO.framebufferWidth != Minecraft.getMinecraft().displayWidth || this.entityFBO.framebufferHeight != Minecraft.getMinecraft().displayHeight) {
			            this.entityFBO.unbindFramebuffer();
			            this.entityFBO = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
			            if (this.entityOutline != null) {
			                this.entityOutline.delete();
			                final ScaledResolution sr = new ScaledResolution(mc);
			                this.entityOutline = new Outline(this.entityFBO.framebufferTexture, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, (float)3.0f, ((float)20 >= 10.0f) ? Integer.valueOf(String.valueOf(20).substring(0, 2)) : Integer.valueOf(String.valueOf(20).substring(0, 1)));
			            }
			        }
			        if (this.entityOutline == null) {
			            final ScaledResolution sr = new ScaledResolution(mc);
			            this.entityOutline = new Outline(this.entityFBO.framebufferTexture, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, (float)3.0f, (int)20);
			        }
			        this.entityOutline.setOutlineSize((float)3.0f);
			        final float partialTicks = mc.timer.renderPartialTicks;
			        Minecraft.getMinecraft().entityRenderer.setupCameraTransform(partialTicks, 0);
			        GL11.glMatrixMode(5888);
			        RenderHelper.enableStandardItemLighting();
			        final double[] polPosP = interpolate(Minecraft.thePlayer);
			        final double polPosXP = polPosP[0];
			        final double polPosYP = polPosP[1];
			        final double polPosZP = polPosP[2];
			        this.entityFBO.bindFramebuffer(false);
			        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			        GL11.glClear(16640);
			        Minecraft.getMinecraft();
			        for (final Object obj : Minecraft.theWorld.loadedEntityList) {
			            final Entity entity3;
			            final Entity entity = entity3 = (Entity)obj;
			            Minecraft.getMinecraft();
			            if (entity3 != Minecraft.thePlayer && !(entity instanceof EntityBoat) && !(entity instanceof EntityFallingBlock) && !(entity instanceof EntityFX)) {
			                if (entity instanceof EntityHanging) {
			                    continue;
			                }
			                if (!(boolean)this.player.getValueState() && entity instanceof EntityPlayer) {
			                    continue;
			                }
			                if (!(boolean)this.mobs.getValueState() && entity instanceof IAnimals) {
			                    continue;
			                }
			                if (!(boolean)this.animals.getValueState() && entity instanceof IMob) {
			                    continue;
			                }
//			                if (!(boolean)this.items.getValueState() && entity instanceof EntityItem) {
//			                    continue;
//			                }
			                Minecraft.getMinecraft().entityRenderer.disableLightmap();
			                RenderHelper.disableStandardItemLighting();
			                GL11.glEnable(3553);
			                final double[] polPosE = interpolate(entity);
			                final double polPosXE = polPosE[0];
			                final double polPosYE = polPosE[1];
			                final double polPosZE = polPosE[2];
			                GL11.glPushMatrix();
			                final Render entityRender = mc.getRenderManager().getEntityRenderObject(entity);
			                if (entityRender != null) {
			                    try {
			    //                    LayerArmorBase.field_177193_i = false;
			    //                    RenderItem.shouldRenderEffect = false;
			                        entityRender.doRender(entity, polPosXE - polPosXP, polPosYE - polPosYP, polPosZE - polPosZP, 0.0f, 0.0f);
			     //                   LayerArmorBase.field_177193_i = true;
			     //                   RenderItem.shouldRenderEffect = true;
			                    }
			                    catch (Exception ex2) {}
			                }
			                GL11.glDisable(3553);
			                GL11.glPopMatrix();
			            }
			        }
			//        if (this.chests.getValueState()) {
//			            try {
//			                final Minecraft mc2 = mc;
//			                for (final Object obj2 : Minecraft.theWorld.loadedTileEntityList) {
//			                    final TileEntity entity2 = (TileEntity)obj2;
//			                    if (entity2 instanceof TileEntityChest || entity2 instanceof TileEntityEnderChest || entity2 instanceof TileEntityDropper || entity2 instanceof TileEntityDispenser || entity2 instanceof TileEntityHopper || entity2 instanceof TileEntityFurnace || entity2 instanceof TileEntityBrewingStand || entity2 instanceof TileEntityEnchantmentTable || entity2 instanceof TileEntityHopper) {
//			                        Minecraft.getMinecraft().entityRenderer.disableLightmap();
//			                        RenderHelper.disableStandardItemLighting();
//			                        GL11.glEnable(3553);
//			                        GL11.glPushMatrix();
//			                        TileEntityRendererDispatcher.instance.renderTileEntityAt(entity2, entity2.getPos().getX() - mc.getRenderManager().renderPosX, entity2.getPos().getY() - mc.getRenderManager().renderPosY, entity2.getPos().getZ() - mc.getRenderManager().renderPosZ, partialTicks);
//			                        GL11.glEnable(3553);
//			                        GL11.glPopMatrix();
//			                    }
//			                }
//			            }
//			            catch (Exception ex) {
//			                ex.printStackTrace();
//			            }
			  //      }
			        Minecraft.getMinecraft().entityRenderer.disableLightmap();
			        RenderHelper.disableStandardItemLighting();
			        Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();
			        GL11.glEnable(3042);
			        GL11.glBlendFunc(770, 771);
			        this.entityOutline.update();
			        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
			        final ScaledResolution sr2 = new ScaledResolution(mc);
			        GL11.glColor4f(0.0f, 1.25f, 2.55f, 1.0f);
			        GL11.glEnable(3553);
			        GL11.glBindTexture(3553, this.entityOutline.getTextureID());
			        GL11.glBegin(4);
			        GL11.glTexCoord2d(0.0, 1.0);
			        GL11.glVertex2d(0.0, 0.0);
			        GL11.glTexCoord2d(0.0, 0.0);
			        GL11.glVertex2d(0.0, (double)(sr2.getScaledHeight() * 2));
			        GL11.glTexCoord2d(1.0, 0.0);
			        GL11.glVertex2d((double)(sr2.getScaledWidth() * 2), (double)(sr2.getScaledHeight() * 2));
			        GL11.glTexCoord2d(1.0, 0.0);
			        GL11.glVertex2d((double)(sr2.getScaledWidth() * 2), (double)(sr2.getScaledHeight() * 2));
			        GL11.glTexCoord2d(1.0, 1.0);
			        GL11.glVertex2d((double)(sr2.getScaledWidth() * 2), 0.0);
			        GL11.glTexCoord2d(0.0, 1.0);
			        GL11.glVertex2d(0.0, 0.0);
			        GL11.glEnd();
			
			}
		}
	}
	
    public static double interpolate(final double now, final double then) {
        return then + (now - then) * Minecraft.getMinecraft().timer.renderPartialTicks;
    }
    
    public static double[] interpolate(final Entity entity) {
        final double posX = interpolate(entity.posX, entity.lastTickPosX) - mc.getRenderManager().renderPosX;
        final double posY = interpolate(entity.posY, entity.lastTickPosY) - mc.getRenderManager().renderPosY;
        final double posZ = interpolate(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().renderPosZ;
        return new double[] { posX, posY, posZ };
    }

	
	public EntityLivingBase renderEntitys() {
        EntityLivingBase saveEnt = null;
        for (final Object o : Minecraft.theWorld.loadedEntityList) {
            if (this.isValid(o)) {
                final EntityLivingBase entity = (EntityLivingBase)o;
                final Frustum var8 = new Frustum();
                final Entity var9 = mc.getRenderViewEntity();
                final double var10 = var9.lastTickPosX + (var9.posX - var9.lastTickPosX) * mc.timer.renderPartialTicks;
                final double var11 = var9.lastTickPosY + (var9.posY - var9.lastTickPosY) * mc.timer.renderPartialTicks;
                final double var12 = var9.lastTickPosZ + (var9.posZ - var9.lastTickPosZ) * mc.timer.renderPartialTicks;
                var8.setPosition(var10, var11, var12);
                if (!var8.isBoundingBoxInFrustum(entity.getEntityBoundingBox())) {
                    continue;
                }
                if (saveEnt == null) {
                    saveEnt = entity;
                }
                this.setColor(entity);
                mc.renderGlobal.renderedEntity = entity;
                final Boolean water = entity.isInWater();
                entity.inWater = false;
                mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
                entity.inWater = water;
                mc.renderGlobal.renderedEntity = null;
            }
        }
        return saveEnt;
    }
    
    public TileEntity renderTileEntity() {
        TileEntity saveEnt = null;
        for (final Object o : Minecraft.theWorld.loadedTileEntityList) {
            if (o instanceof TileEntityChest || o instanceof TileEntityEnderChest) {
                final TileEntity entity = (TileEntity)o;
                final Frustum var8 = new Frustum();
                final Entity var9 = mc.getRenderViewEntity();
                final double var10 = var9.posX + (var9.posX - var9.lastTickPosX) * mc.timer.renderPartialTicks;
                final double var11 = var9.lastTickPosY + (var9.posY - var9.lastTickPosY) * mc.timer.renderPartialTicks;
                final double var12 = var9.lastTickPosZ + (var9.posZ - var9.lastTickPosZ) * mc.timer.renderPartialTicks;
                var8.setPosition(var10, var11, var12);
                if (!var8.isBoxInFrustum(entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(), entity.getPos().getX() + 1, entity.getPos().getY() + 1, entity.getPos().getZ() + 1)) {
                    continue;
                }
                if (saveEnt == null) {
                    saveEnt = entity;
                }
                this.setColor(entity);
                TileEntityRendererDispatcher.instance.renderTileEntity(entity, mc.timer.renderPartialTicks, -1);
            }
        }
        return saveEnt;
    }
    
    public static void checkSetupFBO() {
        final Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
        if (fbo != null && fbo.depthBuffer > -1) {
            setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }
    
    public static void setupFBO(final Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        final int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);
    }
    
    public void outlineOne() {
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3008);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth((float)3.0);
        GL11.glEnable(2848);
        GL11.glEnable(2960);
        GL11.glClear(1024);
        GL11.glClearStencil(15);
        GL11.glStencilFunc(512, 1, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1028, 6913);
    }
    
    public void outlineTwo() {
        GL11.glStencilFunc(512, 0, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1028, 6914);
    }
    
    public void outlineThree() {
        GL11.glStencilFunc(515, 1, 15);
        GL11.glStencilOp(7680, 7680, 7680);
        GL11.glPolygonMode(1028, 6913);
    }
    
    public void outlineFour() {
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(10754);
        GL11.glPolygonOffset(1.0f, -2000000.0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
    }
    
    public void outlineFive() {
        GL11.glPolygonOffset(1.0f, 2000000.0f);
        GL11.glDisable(10754);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(2960);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        GL11.glPopAttrib();
    }
    
    public void setColor(final EntityLivingBase ent) {
        if ((ent instanceof EntityAnimal || ent instanceof EntityAmbientCreature || ent instanceof EntityWaterMob || ent instanceof EntityAgeable) && (boolean)this.animals.getDefaultValue()) {
            if (ent.hurtTime > 0) {
                setColor(new Color(255, 0, 0, 255));
            }
            else {
                setColor(new Color(86, 163, 255, 255));
            }
        }
        if (ent instanceof IMob && !(ent instanceof IEntityMultiPart) && (boolean)this.mobs.getDefaultValue()) {
            setColor(new Color(200, 60, 60, 255));
        }
        if (ent instanceof EntityPlayer && (boolean)this.player.getValueState()) {
            if (ent.hurtTime > 0) {
                setColor(new Color(180, 30, 30, 255));
            }
            else {
                setColor(new Color(33, 88, 255, 255));
            }
        }
    }
    
    public static void setColor(final Color c) {
        GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, c.getAlpha() / 255.0f);
    }
    
    public void setColor(final TileEntity renderEntity) {
        if (renderEntity instanceof TileEntityChest) {
            setColor(new Color(200, 200, 100));
        }
        else if (renderEntity instanceof TileEntityEnderChest) {
            setColor(new Color(204, 0, 204, 255));
        }
        else {
            setColor(new Color(60, 200, 60, 255));
        }
    }
    
    public Boolean isValid(final Object ent) {
        if ((ent instanceof EntityAnimal || ent instanceof EntityAmbientCreature || ent instanceof EntityWaterMob || ent instanceof EntityAgeable) && (boolean)this.animals.getValueState()) {
            return true;
        }
        if (ent instanceof IMob && !(ent instanceof IEntityMultiPart) && (boolean)this.mobs.getValueState()) {
            return true;
        }
        if (ent instanceof EntityPlayer) {
            if (ent != Minecraft.thePlayer && (boolean)this.player.getValueState()) {
                return true;
            }
        }
        return false;
    }
	
	public boolean isValid(EntityLivingBase entity) {
		boolean players = player.getValueState();
		boolean Invis = invis.getValueState();
		boolean animal = animals.getValueState();
		boolean mob = mobs.getValueState();
		if (entity.isInvisible() && !Invis) {
			return false;
		}
		if ((players && entity instanceof EntityPlayer)
				|| (mob && (entity instanceof EntityMob || entity instanceof EntityDragon
						|| entity instanceof EntityGhast || entity instanceof EntitySlime
						|| entity instanceof EntityIronGolem || entity instanceof EntitySnowman))
				|| (animal && (entity instanceof EntityAnimal || entity instanceof EntitySquid
						|| entity instanceof EntityVillager || entity instanceof EntityBat))) {
			if (entity instanceof EntityPlayerSP) {

				return mc.gameSettings.thirdPersonView != 0;
			} else {

				if(entity.getHealth() > 0)
					return true;
				else 
					return false;
			}
		} else {
			return false;
		}
	}

	private void PlayerBoxESP(EventRender event) {
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glEnable((int) 3042);
		GL11.glEnable((int) 2848);
		GL11.glLineWidth((float) 2.0f);
		GL11.glDisable((int) 3553);
		GL11.glDisable((int) 2929);
		GL11.glDepthMask((boolean) false);

		for (Object o : this.mc.theWorld.loadedEntityList) {
			if (o instanceof EntityPlayer && o != mc.thePlayer) {
				EntityPlayer ent = (EntityPlayer) o;
				if (((Entity) o).isInvisible() && !this.invis.getValueState().booleanValue()) {
					continue;
				}
				if (FriendManager.isFriend(ent)) {
					RenderUtil.PlayerESPBox(ent, new Color(0, 255, 255));
				} else if (Teams.isOnSameTeam(ent)) {
					RenderUtil.PlayerESPBox(ent, new Color(0, 255, 0));
				} else if (ent.hurtTime > 0) {
					RenderUtil.PlayerESPBox(ent, new Color(255, 0, 0));
				} else if (ent.isInvisible()) {
					RenderUtil.PlayerESPBox(ent, new Color(255, 255, 0));
				} else {
					RenderUtil.PlayerESPBox(ent, new Color(255, 255, 255));
				}
			}
		}
		GL11.glDisable((int) 2848);
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 3042);
	}

	private void doBoxESP(EventRender event) {
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glEnable((int) 3042);
		GL11.glLineWidth((float) 2.0f);
		GL11.glDisable((int) 3553);
		GL11.glDisable((int) 2929);
		GL11.glDepthMask((boolean) false);

		for (Object o : this.mc.theWorld.loadedEntityList) {
			if (o instanceof EntityPlayer && o != mc.thePlayer) {
				EntityPlayer ent = (EntityPlayer) o;
				if (((Entity) o).isInvisible() && !this.invis.getValueState().booleanValue()) {
					continue;
				}

				if (FriendManager.isFriend(ent)) {
					RenderUtil.entityESPBox(ent, new Color(0, 255, 255), event);
				} else if (Teams.isOnSameTeam(ent)) {
					RenderUtil.entityESPBox(ent, new Color(0, 255, 0), event);
				} else if (ent.hurtTime > 0) {
					RenderUtil.entityESPBox(ent, new Color(255, 0, 0), event);
				} else if (ent.isInvisible()) {
					RenderUtil.entityESPBox(ent, new Color(255, 255, 0), event);
				} else {
					RenderUtil.entityESPBox(ent, new Color(255, 255, 255), event);
				}
			}
		}
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDepthMask((boolean) true);
		GL11.glDisable((int) 3042);
	}

//	private boolean isValid(EntityLivingBase entity) {
//		if (entity instanceof EntityPlayer && entity.getHealth() >= 0.0f && entity != mc.thePlayer) {
//			return true;
//		}
//		return false;
//	}

	private void doCornerESP() {
		Iterator var2 = this.mc.theWorld.playerEntities.iterator();

		while (true) {
			EntityPlayer entity;
			do {
				if (!var2.hasNext()) {
					return;
				}
				entity = (EntityPlayer) var2.next();
			} while (entity == this.mc.thePlayer);
			if ((entity).isInvisible() && !this.invis.getValueState().booleanValue()) {
				continue;
			}
			if (!this.isValid(entity)) {
				return;
			}

			GL11.glPushMatrix();

			GL11.glEnable(3042);
			GL11.glDisable(2929);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GL11.glBlendFunc(770, 771);
			GL11.glDisable(3553);
			float partialTicks = this.mc.timer.renderPartialTicks;
			this.mc.getRenderManager();
			double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks
					- mc.getRenderManager().renderPosX;
			this.mc.getRenderManager();
			double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks
					- mc.getRenderManager().renderPosY;
			this.mc.getRenderManager();
			double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks
					- mc.getRenderManager().renderPosZ;
			float DISTANCE = this.mc.thePlayer.getDistanceToEntity(entity);
			float DISTANCE_SCALE = Math.min(DISTANCE * 0.15F, 2.5F);
			float SCALE = 0.035F;
			SCALE /= 2.0F;
			GlStateManager.translate((float) x,
					(float) y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float) z);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(-SCALE, -SCALE, -SCALE);
			Tessellator tesselator = Tessellator.getInstance();
			WorldRenderer worldRenderer = tesselator.getWorldRenderer();
			Color color = new Color(FlatColors.WHITE.c);
			if (entity.hurtTime > 0) {
				color = new Color(255, 0, 0);
			} else if (Teams.isOnSameTeam(entity)) {
				color = new Color(0, 255, 0);
			} else if (entity.isInvisible()) {
				color = new Color(255, 255, 0);
			} else if (entity == KillAura.Target && ModManager.getModByClass(KillAura.class).isEnabled()) {
				color = new Color(0, 0, 255);

			}

			Color gray = new Color(255, 255, 255, 120);
			double thickness = (double) (8.0F + DISTANCE * 0.08F);
			double xLeft = 0.0D;
			double xRight = 35.0D;
			double yUp = 80.0D;
			double yDown = 140.0D;
			double size = 35.0D;

			// RenderUtils.rectangleBordered(xLeft + size / 3.0D, yUp, size /
			// 2.0D,thickness, 0.5 , -0,-1);

			drawVerticalLine(xLeft, yUp, size, thickness + 120, gray);

			GL11.glEnable(3553);
			GL11.glEnable(2929);
			GlStateManager.disableBlend();
			GL11.glDisable(3042);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glNormal3f(1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	private static void drawVerticalLine(double xPos, double yPos, double xSize, double thickness, Color color) {
		Tessellator tesselator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tesselator.getWorldRenderer();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(xPos - xSize, yPos - thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos - xSize, yPos + thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos + xSize, yPos + thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos + xSize, yPos - thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		tesselator.draw();
	}

	private void drawHorizontalLine(double xPos, double yPos, double ySize, double thickness, Color color) {
		Tessellator tesselator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tesselator.getWorldRenderer();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(xPos - thickness / 2.0D, yPos - ySize, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos - thickness / 2.0D, yPos + ySize, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos + thickness / 2.0D, yPos + ySize, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos + thickness / 2.0D, yPos - ySize, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		tesselator.draw();
	}

	private void doCandyESP() {
		Iterator var2 = this.mc.theWorld.playerEntities.iterator();
		while (true) {
			EntityPlayer entity;
			do {
				do {
					if (!var2.hasNext()) {
						return;
					}
					entity = (EntityPlayer) var2.next();
				} while (entity == this.mc.thePlayer);
				if ((entity).isInvisible() && !this.invis.getValueState().booleanValue()) {
					continue;
				}
			} while (!this.isValid(entity));

			GL11.glPushMatrix();
			GL11.glEnable(3042);
			GL11.glDisable(2929);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GL11.glBlendFunc(770, 771);
			GL11.glDisable(3553);
			float partialTicks = this.mc.timer.renderPartialTicks;
			this.mc.getRenderManager();
			double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks
					- mc.getRenderManager().renderPosX;
			this.mc.getRenderManager();
			double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks
					- mc.getRenderManager().renderPosY;
			this.mc.getRenderManager();
			double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks
					- mc.getRenderManager().renderPosZ;
			float DISTANCE = this.mc.thePlayer.getDistanceToEntity(entity);
			float DISTANCE_SCALE = Math.min(DISTANCE * 0.15F, 0.15F);
			float SCALE = 0.035F;
			SCALE /= 2.0F;
			float xMid = (float) x;
			float yMid = (float) y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F);
			float zMid = (float) z;
			GlStateManager.translate((float) x,
					(float) y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float) z);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(-SCALE, -SCALE, -SCALE);
			Tessellator tesselator = Tessellator.getInstance();
			WorldRenderer worldRenderer = tesselator.getWorldRenderer();
			new Color(0, 0, 0);
			double thickness = 1.5D;
			double xLeft = -30.0D;
			double xRight = 30.0D;
			double yUp = 15.0D;
			double yDown = 140.0D;
			double size = 10.0D;
			Color color = new Color(255, 255, 255);

			if (entity.hurtTime > 0) {
				color = new Color(255, 0, 0);
			} else if (Teams.isOnSameTeam(entity)) {
				color = new Color(0, 255, 0);
			} else if (entity.isInvisible()) {
				color = new Color(255, 255, 0);
			} else if (entity == KillAura.Target && ModManager.getModByClass(KillAura.class).isEnabled()) {
				color = new Color(0, 0, 255);
			}

			drawBorderedRect((float) xLeft, (float) yUp, (float) xRight, (float) yDown, (float) thickness + 3.0F,
					Colors.BLACK.c, 0);
			drawBorderedRect((float) xLeft, (float) yUp, (float) xRight, (float) yDown, (float) thickness + 1.0F,
					color.getRGB(), 0);
			GL11.glEnable(3553);
			GL11.glEnable(2929);
			GlStateManager.disableBlend();
			GL11.glDisable(3042);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glNormal3f(1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	private void doOther2DESP() {
		for (EntityPlayer entity : this.mc.theWorld.playerEntities) {
			if ((entity).isInvisible() && !this.invis.getValueState().booleanValue()) {
				continue;
			}
			if (isValid(entity)) {
				GL11.glPushMatrix();
				GL11.glEnable(3042);
				GL11.glDisable(2929);
				GL11.glNormal3f(0.0f, 1.0f, 0.0f);
				GlStateManager.enableBlend();
				GL11.glBlendFunc(770, 771);
				GL11.glDisable(3553);
				float partialTicks = mc.timer.renderPartialTicks;
				this.mc.getRenderManager();
				double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks
						- mc.getRenderManager().renderPosX;
				this.mc.getRenderManager();
				double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks
						- mc.getRenderManager().renderPosY;
				this.mc.getRenderManager();
				double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks
						- mc.getRenderManager().renderPosZ;
				float DISTANCE = this.mc.thePlayer.getDistanceToEntity(entity);
				float DISTANCE_SCALE = Math.min(DISTANCE * 0.15f, 0.15f);
				float SCALE = 0.035f;
				SCALE /= 2.0f;
				float xMid = (float) x;
				float yMid = (float) y + entity.height + 0.5f - (entity.isChild() ? (entity.height / 2.0f) : 0.0f);
				float zMid = (float) z;
				GlStateManager.translate((float) x,
						(float) y + entity.height + 0.5f - (entity.isChild() ? (entity.height / 2.0f) : 0.0f),
						(float) z);
				GL11.glNormal3f(0.0f, 1.0f, 0.0f);
				GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
				GL11.glScalef(-SCALE, -SCALE, -SCALE);
				Tessellator tesselator = Tessellator.getInstance();
				WorldRenderer worldRenderer = tesselator.getWorldRenderer();
				float HEALTH = entity.getHealth();
				int COLOR = -1;
				if (HEALTH > 20.0) {
					COLOR = -65292;
				} else if (HEALTH >= 10.0) {
					COLOR = -16711936;
				} else if (HEALTH >= 3.0) {
					COLOR = -23296;
				} else {
					COLOR = -65536;
				}
				Color gray = new Color(0, 0, 0);
				double thickness = 1.5f + DISTANCE * 0.01f;
				double xLeft = -20.0;
				double xRight = 20.0;
				double yUp = 27.0;
				double yDown = 130.0;
				double size = 10.0;
				Color color = new Color(255, 255, 255);
				if (entity.hurtTime > 0) {
					color = new Color(255, 0, 0);
				} else if (Teams.isOnSameTeam(entity)) {
					color = new Color(0, 255, 0);
				} else if (entity.isInvisible()) {
					color = new Color(255, 255, 0);
				}
				drawBorderedRect((float) xLeft, (float) yUp, (float) xRight, (float) yDown, (float) thickness + 0.5f,
						Colors.BLACK.c, 0);
				drawBorderedRect((float) xLeft, (float) yUp, (float) xRight, (float) yDown, (float) thickness,
						color.getRGB(), 0);
				drawBorderedRect((float) xLeft - 3.0f - DISTANCE * 0.2f, (float) yDown - (float) (yDown - yUp),
						(float) xLeft - 2.0f, (float) yDown, 0.15f, Colors.BLACK.c, new Color(100, 100, 100).getRGB());
				drawBorderedRect((float) xLeft - 3.0f - DISTANCE * 0.2f,
						(float) yDown - (float) (yDown - yUp) * Math.min(1.0f, entity.getHealth() / 20.0f),
						(float) xLeft - 2.0f, (float) yDown, 0.15f, Colors.BLACK.c, COLOR);
				GL11.glEnable(3553);
				GL11.glEnable(2929);
				GlStateManager.disableBlend();
				GL11.glDisable(3042);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GL11.glNormal3f(1.0f, 1.0f, 1.0f);
				GL11.glPopMatrix();
			}
		}
	}

	private void drawBorderedRect(final double x, final double y, final double x2, final double y2,
			final double thickness, final int inside, final int outline) {
		double fix = 0.0;
		if (thickness < 1.0) {
			fix = 1.0;
		}
		Gui.drawRect(x + thickness, y + thickness, x2 - thickness, y2 - thickness, inside);
		Gui.drawRect(x, y + 1.0 - fix, x + thickness, y2, outline);
		Gui.drawRect(x, y, x2 - 1.0 + fix, y + thickness, outline);
		Gui.drawRect(x2 - thickness, y, x2, y2 - 1.0 + fix, outline);
		Gui.drawRect(x + 1.0 - fix, y2 - thickness, x2, y2, outline);
	}

	public Minecraft minecraft() {
		return Minecraft.getMinecraft();
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

}