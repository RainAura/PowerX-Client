package cn.Power.mod.mods.RENDER;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.events.EventRespawn;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.AntiBot;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.util.RenderUtil;
import cn.Power.util.friendManager.FriendManager;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import static org.lwjgl.opengl.GL11.*;

public class Skeletal extends Mod {
	private final HashMap<EntityPlayer, float[][]> modelRotation = new HashMap<EntityPlayer, float[][]>();

	public Value<Boolean> invis = new Value("Skeletal_Invisibles", true);

	public Skeletal() {
		super("Skeletal", Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender event) {
		doSkeltalESP();
		
	}
	
    public static void startSmooth() {
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
    }

    public static void endSmooth() {
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
    }
	
	@EventTarget
	public void reSpawn(EventRespawn e) {
		modelRotation.clear();
	}

	private void doSkeltalESP() {
		
		RenderUtil.setupRender(true);
		startSmooth();
		
		GL11.glDisable((int) 2848);
		GlStateManager.disableLighting();
		modelRotation.keySet().removeIf(player -> {
			if (mc.theWorld.playerEntities.contains(player))
				return false;
			return true;
		});
		mc.theWorld.playerEntities.forEach(player -> {
			
			if(player == null)
				return;
			
			if(player.isDead)
				return;
			
			if (player == mc.thePlayer)
				return;
			if (player.isInvisible() && !this.invis.getValueState().booleanValue()) {
				return;
			}

			float[][] modelRotations = modelRotation.get(player);
			if (modelRotations == null) {
				return;
			}

			// GL11.glEnable((int)2848);
			GL11.glPushMatrix();
			GL11.glLineWidth((float) 1.2f);
			if (FriendManager.isFriend(player)) {
				GL11.glColor4f(0, 255, 255, 1);
			} else if (Teams.isOnSameTeam(player)) {
				GL11.glColor4f(0, 255, 0, 1);
			} else if (player.hurtTime > 0) {
				GL11.glColor4f(255, 0, 0, 1);
			} else if (player.isInvisible()) {
				GL11.glColor4f(255, 255, 0, 1);

			} else {
				GL11.glColor4f(255, 255, 255, 1);
			}
			
	

			Vec3 interp = RenderUtil.interpolateRender(player);
			double x = interp.getX() - mc.getRenderManager().renderPosX;
			double y = interp.getY() - mc.getRenderManager().renderPosY;
			double z = interp.getZ() - mc.getRenderManager().renderPosZ;
			GL11.glTranslated((double) x, (double) y, (double) z);
			float bodyYawOffset = player.prevRenderYawOffset
					+ (player.renderYawOffset - player.prevRenderYawOffset) * mc.timer.renderPartialTicks;
			GL11.glRotatef((float) (-bodyYawOffset), (float) 0.0f, (float) 1.0f, (float) 0.0f);
			GL11.glTranslated((double) 0.0, (double) 0.0, (double) (player.isSneaking() ? -0.235 : 0.0));
			float legHeight = player.isSneaking() ? 0.6f : 0.75f;
			GL11.glPushMatrix();
			
	
			
			GL11.glTranslated((double) -0.125, (double) legHeight, (double) 0.0);
			if (modelRotations[3][0] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[3][0] * 57.295776f), (float) 1.0f, (float) 0.0f, (float) 0.0f);
			}
			if (modelRotations[3][1] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[3][1] * 57.295776f), (float) 0.0f, (float) 1.0f, (float) 0.0f);
			}
			if (modelRotations[3][2] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[3][2] * 57.295776f), (float) 0.0f, (float) 0.0f, (float) 1.0f);
			}
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) 0.0, (double) 0.0, (double) 0.0);
			GL11.glVertex3d((double) 0.0, (double) (-legHeight), (double) 0.0);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated((double) 0.125, (double) legHeight, (double) 0.0);
			if (modelRotations[4][0] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[4][0] * 57.295776f), (float) 1.0f, (float) 0.0f, (float) 0.0f);
			}
			if (modelRotations[4][1] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[4][1] * 57.295776f), (float) 0.0f, (float) 1.0f, (float) 0.0f);
			}
			if (modelRotations[4][2] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[4][2] * 57.295776f), (float) 0.0f, (float) 0.0f, (float) 1.0f);
			}
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) 0.0, (double) 0.0, (double) 0.0);
			GL11.glVertex3d((double) 0.0, (double) (-legHeight), (double) 0.0);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glTranslated((double) 0.0, (double) 0.0, (double) (player.isSneaking() ? 0.25 : 0.0));
			GL11.glPushMatrix();
			GL11.glTranslated((double) 0.0, (double) (player.isSneaking() ? -0.05 : 0.0),
					(double) (player.isSneaking() ? -0.01725 : 0.0));
			GL11.glPushMatrix();
			GL11.glTranslated((double) -0.375, (double) ((double) legHeight + 0.55), (double) 0.0);
			if (modelRotations[1][0] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[1][0] * 57.295776f), (float) 1.0f, (float) 0.0f, (float) 0.0f);
			}
			if (modelRotations[1][1] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[1][1] * 57.295776f), (float) 0.0f, (float) 1.0f, (float) 0.0f);
			}
			if (modelRotations[1][2] != 0.0f) {
				GL11.glRotatef((float) ((-modelRotations[1][2]) * 57.295776f), (float) 0.0f, (float) 0.0f,
						(float) 1.0f);
			}
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) 0.0, (double) 0.0, (double) 0.0);
			GL11.glVertex3d((double) 0.0, (double) -0.5, (double) 0.0);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated((double) 0.375, (double) ((double) legHeight + 0.55), (double) 0.0);
			if (modelRotations[2][0] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[2][0] * 57.295776f), (float) 1.0f, (float) 0.0f, (float) 0.0f);
			}
			if (modelRotations[2][1] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[2][1] * 57.295776f), (float) 0.0f, (float) 1.0f, (float) 0.0f);
			}
			if (modelRotations[2][2] != 0.0f) {
				GL11.glRotatef((float) ((-modelRotations[2][2]) * 57.295776f), (float) 0.0f, (float) 0.0f,
						(float) 1.0f);
			}
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) 0.0, (double) 0.0, (double) 0.0);
			GL11.glVertex3d((double) 0.0, (double) -0.5, (double) 0.0);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glRotatef((float) (bodyYawOffset - player.rotationYawHead), (float) 0.0f, (float) 1.0f, (float) 0.0f);
			GL11.glPushMatrix();
			GL11.glTranslated((double) 0.0, (double) ((double) legHeight + 0.55), (double) 0.0);
			if (modelRotations[0][0] != 0.0f) {
				GL11.glRotatef((float) (modelRotations[0][0] * 57.295776f), (float) 1.0f, (float) 0.0f, (float) 0.0f);
			}
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) 0.0, (double) 0.0, (double) 0.0);
			GL11.glVertex3d((double) 0.0, (double) 0.3, (double) 0.0);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPopMatrix();
			GL11.glRotatef((float) (player.isSneaking() ? 25.0f : 0.0f), (float) 1.0f, (float) 0.0f, (float) 0.0f);
			GL11.glTranslated((double) 0.0, (double) (player.isSneaking() ? -0.16175 : 0.0),
					(double) (player.isSneaking() ? -0.48025 : 0.0));
			GL11.glPushMatrix();
			GL11.glTranslated((double) 0.0, (double) legHeight, (double) 0.0);
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) -0.125, (double) 0.0, (double) 0.0);
			GL11.glVertex3d((double) 0.125, (double) 0.0, (double) 0.0);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated((double) 0.0, (double) legHeight, (double) 0.0);
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) 0.0, (double) 0.0, (double) 0.0);
			GL11.glVertex3d((double) 0.0, (double) 0.55, (double) 0.0);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated((double) 0.0, (double) ((double) legHeight + 0.55), (double) 0.0);
			GL11.glBegin((int) 3);
			GL11.glVertex3d((double) -0.375, (double) 0.0, (double) 0.0);
			GL11.glVertex3d((double) 0.375, (double) 0.0, (double) 0.0);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		});

		
		endSmooth();
		RenderUtil.setupRender(false);
	}

	public static void updateModel(EntityPlayer player, ModelPlayer model) {
		
		if(ModManager.getModByClass(Skeletal.class).isEnabled()) {
			
			
			((Skeletal)ModManager.getModByClass(Skeletal.class)).modelRotation.keySet().removeIf(playe -> {
				if (mc.theWorld.playerEntities.contains(playe))
					return false;
				return true;
			});
			
		((Skeletal)ModManager.getModByClass(Skeletal.class)).modelRotation.put(player, new float[][] {
				{ model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ },
				{ model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY,
						model.bipedRightArm.rotateAngleZ },
				{ model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ },
				{ model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY,
						model.bipedRightLeg.rotateAngleZ },
				{ model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY,
						model.bipedLeftLeg.rotateAngleZ } });
		}
	}
}
