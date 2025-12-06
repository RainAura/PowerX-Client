package cn.Power.mod.mods.RENDER;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.COMBAT.AntiBot;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class Arrow extends Mod {

	private Value<Double> size = new Value<Double>("Arrow_Size", Double.valueOf(50D), Double.valueOf(50D),
			Double.valueOf(500D), 10D);
	private Value<Double> FadeMin = new Value<Double>("Arrow_FadeMin", Double.valueOf(0.12D), Double.valueOf(0D),
			Double.valueOf(0.8D), 0.01D);
	private Value<Double> FadeMax = new Value<Double>("Arrow_FadeMax", Double.valueOf(0.7D), Double.valueOf(0D),
			Double.valueOf(0.98D), 0.01D);
	private Value<Double> FadeSpan = new Value<Double>("Arrow_FadeSpan", Double.valueOf(0.01D), Double.valueOf(0D),
			Double.valueOf(0.1D), 0.001D);
	private Value<Double> FadeSpeed = new Value<Double>("Arrow_FadeSpeed", Double.valueOf(12D), Double.valueOf(1D),
			Double.valueOf(100D), 1D);
	private Value<Double> CoolDown = new Value<Double>("Arrow_FadeCoolDown", Double.valueOf(43D), Double.valueOf(0D),
			Double.valueOf(250D), 1D);
	public Value<Boolean> player = new Value("Arrow_Player", true);
	public Value<Boolean> animals = new Value("Arrow_Animals", false);
	public Value<Boolean> mobs = new Value("Arrow_Mobs", false);
	public Value<Boolean> invis = new Value("Arrow_Invisibles", true);
	public Value<Boolean> Fade = new Value("Arrow_Fade", true);

	public float inc = 0.01f;
	public int counter = 0;
	public int counter2 = 0;
	public boolean down,pause;

	public Arrow() {
		super("Arrow", Category.RENDER);
	}

	@EventTarget(1)
	public void onScreen(EventRender2D event) {
		final List<EntityLivingBase> Entity = Arrays
				.asList((EntityLivingBase[]) Minecraft.getMinecraft().theWorld.loadedEntityList.stream()
						.filter(entity -> entity instanceof EntityLivingBase)
						.filter(entity -> entity != Minecraft.getMinecraft().thePlayer).map(entity -> entity)
						.toArray(EntityLivingBase[]::new));
		ScaledResolution a = new ScaledResolution(mc);
		double size = this.size.getValueState();
		double xOffset = a.getScaledWidth() / 2 - size / 2 - 0.5;
		double yOffset = a.getScaledHeight() / 2 - size / 2 + 0.2;
		double playerOffsetX = mc.thePlayer.posX;
		double playerOffSetZ = mc.thePlayer.posZ;
		for (EntityLivingBase entity : Entity) {
			if (!(isvalid(entity)))
				continue;
			double loaddist = 0.2;
			float pTicks = mc.timer.renderPartialTicks;
			double pos1 = (((entity.posX + (entity.posX - entity.lastTickPosX) * pTicks) - playerOffsetX) * loaddist);
			double pos2 = (((entity.posZ + (entity.posZ - entity.lastTickPosZ) * pTicks) - playerOffSetZ) * loaddist);
			boolean view = mc.gameSettings.thirdPersonView == 2;
			double cos = Math
					.cos((view ? mc.thePlayer.rotationYaw - 180 : mc.thePlayer.rotationYaw) * (Math.PI * 2 / 360));
			double sin = Math
					.sin((view ? mc.thePlayer.rotationYaw - 180 : mc.thePlayer.rotationYaw) * (Math.PI * 2 / 360));
			double rotY = -(pos2 * cos - pos1 * sin);
			double rotX = -(pos1 * cos + pos2 * sin);
			double var7 = 0 - rotX;
			double var9 = 0 - rotY;
//                if (MathHelper.sqrt_double(var7 * var7 + var9 * var9) < size / 2 - 4) {
			float angle = (float) (Math.atan2(rotY - 0, rotX - 0) * 180 / Math.PI);
			double x = ((size / 2) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2;
			double y = ((size / 2) * Math.sin(Math.toRadians(angle))) + yOffset + size / 2;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, 0);
			GlStateManager.rotate(angle - 120, 0, 0, 1);
//                    GlStateManager.scale(1.5, 1.5, 1.5);

			Color color = new Color(Colors.WHITE.c);

			if (entity instanceof EntityPlayer && Teams.isOnSameTeam((EntityPlayer) entity)) {
				color = new Color(0, 255, 0);
			} else if (entity instanceof EntityPlayer) {
				color = new Color(255, 0, 0);
			} else if ((entity instanceof EntityMob || entity instanceof EntityDragon || entity instanceof EntityGhast
					|| entity instanceof EntitySlime || entity instanceof EntityIronGolem
					|| entity instanceof EntitySnowman)) {
				color = new Color(255, 255, 0);
			} else if ((entity instanceof EntityAnimal || entity instanceof EntitySquid
					|| entity instanceof EntityVillager || entity instanceof EntityBat)) {
				color = new Color(0, 135, 255);

			} else if (entity.isInvisible()) {
				color = new Color(150, 50, 170);
			}

			GL11.glLineWidth(1.0F);
			drawESPCircle(0, 0, 3.5, 3, color);
			drawESPCircle(0, 0, 3.0, 3, color);
			drawESPCircle(0, 0, 2.5, 3, color);
			drawESPCircle(0, 0, 2.0, 3, color);
			drawESPCircle(0, 0, 1.5, 3, color);
			drawESPCircle(0, 0, 1.0, 3, color);
			drawESPCircle(0, 0, 0.5, 3, color);
			GlStateManager.popMatrix();
		}

		if (Fade.getValueState()) {

			if (inc < this.FadeMin.getValueState().floatValue()) {
				
				pause = true;
				
				if((counter2 += this.FadeSpeed.getValueState().intValue()) > CoolDown.getValueState().intValue() * this.FadeSpeed.getValueState().intValue() * 2) {
					this.counter2 = 0;
					down = false;
					
					pause = false;
				}
			} else if (inc > this.FadeMax.getValueState().floatValue()) {
				
				pause = true;
				
				if((counter2 += this.FadeSpeed.getValueState().intValue()) > CoolDown.getValueState().intValue() * this.FadeSpeed.getValueState().intValue() * 2) {
					this.counter2 = 0;
					down = true;
					
					pause = false;
				}
			}

			
			if (!down) {

				if (counter % 2 == 0 && !pause)

					inc += this.FadeSpan.getValueState().floatValue();
			} else {

				if (counter % 2 == 0 && !pause)

					inc -= this.FadeSpan.getValueState().floatValue();
			}

			counter += this.FadeSpeed.getValueState().intValue();

			if (counter > 101)
				counter = 0;

			if (Float.isNaN(inc))
				inc = 0.5f;

		}

	}

	public void enableGL2D() {
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
	}

	public void disableGL2D() {
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
	}

	public void drawESPCircle(double cx, double cy, double r, double n, Color color) {
		GL11.glPushMatrix();
		cx *= 2.0;
		cy *= 2.0;
		double b = 6.2831852 / n;
		double p = Math.cos(b);
		double s = Math.sin(b);
		double x = r *= 2.0;
		double y = 0.0;
		enableGL2D();
		GL11.glScaled(0.6, 0.6, 0.6);
		GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (float) (inc));
		GL11.glBegin(3);
		double ii = 0;
		while (ii < n) {
			GL11.glVertex2d(x + cx, y + cy);
			double t = x;
			x = p * x - s * y;
			y = s * t + p * y;
			ii++;
		}
		GL11.glEnd();
		GL11.glScaled(2.0, 2.0, 2.0);
		disableGL2D();
		GL11.glPopMatrix();
	}

	public boolean isvalid(EntityLivingBase entity) {
		boolean players = this.player.getValueState();
		boolean Invis = this.invis.getValueState();
		boolean animal = this.animals.getValueState();
		boolean mob = this.mobs.getValueState();
		if (entity.isInvisible() && !Invis) {
			return false;
		}
		if (entity == mc.thePlayer) {
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

				return true;
			}
		} else {
			return false;
		}
	}
}
