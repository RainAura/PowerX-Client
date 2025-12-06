package cn.Power.mod.mods.WORLD;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_POINT_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_POINT_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_POLYGON_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_POLYGON_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventRender;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.COMBAT.AntiBot;
import cn.Power.util.friendManager.FriendManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;

public class Tracers extends Mod {
	public Tracers() {
		super("Tracers", Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender event) {
		Iterator var3 = this.mc.theWorld.playerEntities.iterator();

		while (var3.hasNext()) {
			EntityPlayer player = (EntityPlayer) var3.next();
			if (this.mc.thePlayer != player && !player.isInvisible()) {
				double posX = player.posX;
				double posY = player.posY;
				double posZ = player.posZ;
				this.drawLine(player);
			}
		}

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

	private void drawLine(EntityPlayer player) {
		
		startSmooth();
		
		this.mc.getRenderManager();
		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) this.mc.timer.renderPartialTicks
				- mc.getRenderManager().renderPosX;
		this.mc.getRenderManager();
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) this.mc.timer.renderPartialTicks
				- mc.getRenderManager().renderPosY;
		this.mc.getRenderManager();
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) this.mc.timer.renderPartialTicks
				- mc.getRenderManager().renderPosZ;
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glDisable((int) 2929);
		GL11.glDisable((int) 3553);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glLineWidth((float) 1f);
		float DISTANCE = this.mc.thePlayer.getDistanceToEntity(player);
		if (FriendManager.isFriend(player)) {

			GL11.glColor3f((float) 0.0f, (float) 1.0f, (float) 1.0f);
		} else if (Teams.isOnSameTeam(player)) {
			GL11.glColor3f((float) 0.0f, (float) 1.0f, (float) 0.0f);
		} else if (DISTANCE <= 200.0f) {
			GL11.glColor3f((float) 1.0f, (float) (DISTANCE / 40.0f), (float) 0.0f);
		}

		GL11.glLoadIdentity();
		boolean bobbing = this.mc.gameSettings.viewBobbing;
		this.mc.gameSettings.viewBobbing = false;
		this.mc.entityRenderer.orientCamera(this.mc.timer.renderPartialTicks);
		GL11.glBegin((int) 3);
		GL11.glVertex3d((double) 0.0, (double) this.mc.thePlayer.getEyeHeight(), (double) 0.0);
		GL11.glVertex3d((double) x, (double) y, (double) z);
		GL11.glEnd();
		this.mc.gameSettings.viewBobbing = bobbing;
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDisable((int) 3042);
		GL11.glPopMatrix();
		
		endSmooth();
	}

}