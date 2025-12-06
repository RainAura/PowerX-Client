package cn.Power.mod.mods.MOVEMENT;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;

import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.util.PlayerUtil;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.RotationUtils;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.optifine.util.MathUtils;

public class TargetStrafe extends Mod {
	public static Value<Double> max = new Value<Double>("TargetStrafe_DistanceMax", 0.85d, 0.1d, 5.0d, 0.05d);
	public static Value<Double> min = new Value<Double>("TargetStrafe_DistanceMin", 0.55d, 0.1d, 5.0d, 0.05d);
	public static Value<Double> spaceRange = new Value<Double>("TargetStrafe_Hit Reach", 4.5d, 1.0d, 7.0d, 0.05d);
	public static Value<Double> Sides = new Value<Double>("TargetStrafe_Sides", 6.0d, 3.0d, 25.0d, 1.0d);
	public static Value<Double> TargetHurtTime = new Value<Double>("TargetStrafe_TargetHurtTime", 5.0d, 1.0d, 120.0d,
			1.0d);
	public static Value<Double> SwapHurtTime = new Value<Double>("TargetStrafe_SwapDireHurtTime", 11.0d, 1.0d, 20.0d,
			1.0d);
	public Value<Boolean> onlyInRange = new Value<Boolean>("TargetStrafe_Only Hit Reach", true);
	public Value<Boolean> check = new Value<Boolean>("TargetStrafe_Wall&void Check", true);
	public Value<Boolean> esp = new Value<Boolean>("TargetStrafe_Draw", true);
	public Value<Boolean> Spiral = new Value<Boolean>("TargetStrafe_Spiral", false);
	public Value<Boolean> OnlyBhop = new Value<Boolean>("TargetStrafe_OnlyBhop", false);
	public Value<Boolean> OnlySpace = new Value<Boolean>("TargetStrafe_OnlySpace", true);
	public Value<Boolean> KnockBackBoost = new Value<Boolean>("TargetStrafe_KnockBackBoost", true);

	public EntityLivingBase target;

	private double degree = 0.0D;

	private float groundY;

	private boolean left = true;

	public double offs = 0.0;

	public S27PacketExplosion toP = null;

	private boolean shouldSpoof = false;
	private boolean freeze;
	private double distance;

	private int ticksSinceDamage;
	private int damageBoostCooldown;

	public TargetStrafe() {
		super("TargetStrafe", Category.MOVEMENT);
	}

	public void onEnable() {
		this.degree = 0.0D;
		this.left = true;
		this.target = null;
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@EventTarget
	public void donative2(EventPostMotion e) {
		double xDist = Minecraft.thePlayer.posX - Minecraft.thePlayer.prevPosX;
		double zDist = Minecraft.thePlayer.posZ - Minecraft.thePlayer.prevPosZ;
		this.distance = Math.sqrt(xDist * xDist + zDist * zDist);
	}

	@EventTarget
	public void onPacket(EventPacket e) {
		if (e.getPacket() instanceof S27PacketExplosion) {
			S27PacketExplosion pe = (S27PacketExplosion) e.packet;

			if (pe.getStrength() == 0 && pe.getAffectedBlockPositions().isEmpty() && KnockBackBoost.getValueState()) {

				toP = pe;
			}

		}
//		if (e.getPacket() instanceof S12PacketEntityVelocity) {
//			if (((S12PacketEntityVelocity) e.getPacket()).entityID == this.mc.thePlayer.getEntityId()
//					&& mc.gameSettings.keyBindJump.isKeyDown()
//					&& KnockBackPause.getValueState()) {
//				mc.thePlayer.setSpeed(0);
//				this.shouldSpoof = true;
//			}
//
//		}
	}

	@EventTarget
	public void onRender(EventRender event) {
		if (KillAura.Target != null && esp.getValueState()) {

//			final double x = KillAura.Target.lastTickPosX
//					+ (KillAura.Target.posX - KillAura.Target.lastTickPosX) * event.partialTicks
//					- this.mc.getRenderManager().viewerPosX;
//			final double y = KillAura.Target.lastTickPosY
//					+ (KillAura.Target.posY - KillAura.Target.lastTickPosY) * event.partialTicks
//					- this.mc.getRenderManager().viewerPosY;
//			final double z = KillAura.Target.lastTickPosZ
//					+ (KillAura.Target.posZ - KillAura.Target.lastTickPosZ) * event.partialTicks
//					- this.mc.getRenderManager().viewerPosZ;

			Color color = mc.gameSettings.keyBindJump.pressed ? new Color(0, 150, 255) : new Color(255, 255, 255);

			final double xD = KillAura.Target.lastTickPosX
					+ (KillAura.Target.posX - KillAura.Target.lastTickPosX) * event.partialTicks
					- mc.getRenderManager().viewerPosX;
			final double yD = KillAura.Target.lastTickPosY
					+ (KillAura.Target.posY - KillAura.Target.lastTickPosY) * event.partialTicks
					- mc.getRenderManager().viewerPosY;
			final double zD = KillAura.Target.lastTickPosZ
					+ (KillAura.Target.posZ - KillAura.Target.lastTickPosZ) * event.partialTicks
					- mc.getRenderManager().viewerPosZ;

			final float rD = Color.black.getRGB();
			final float gD = Color.black.getRGB();
			final float bD = Color.black.getRGB();

			final double pix3 = Math.PI * 2.0D;

			float rad = 2.5f;

			RenderUtils.pre3D();
			GL11.glTranslated((double) xD, (double) yD, (double) zD);
			GL11.glRotatef((float) (-KillAura.Target.width), (float) 0.0f, (float) 1.0f, (float) 0.0f);
			Cylinder c = new Cylinder();
			GlStateManager.rotate((float) 90.0f, (float) 1.0f, (float) 0.0f, (float) 0.0f);
			GL11.glLineWidth(0.5F);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			c.setDrawStyle(GLU.GLU_SILHOUETTE);
			c.setNormals(GLU.GLU_SMOOTH);
			// c.setDrawStyle(100011);
			GL11.glColor4f(0, 0, 0, 1);
			GL11.glLineWidth((float) 3);

			c.draw(min.getValueState().floatValue(), min.getValueState().floatValue(), 0.0f,
					Sides.getValueState().intValue() == 25 ? 360 : Sides.getValueState().intValue(), 0);

			GL11.glColor4f((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255,
					1);
			GL11.glLineWidth((float) 2);
			c.draw(min.getValueState().floatValue(), min.getValueState().floatValue(), 0.0f,
					Sides.getValueState().intValue() == 25 ? 360 : Sides.getValueState().intValue(), 0);
			RenderUtils.post3D();

			if (!Spiral.getValueState())
				return;

			GL11.glPushMatrix();
			GlStateManager.disableTexture2D();
			GlStateManager.disableDepth();
			GL11.glDepthMask(false);
			GL11.glLineWidth(9f);
			GL11.glBegin(1);
			final double x = KillAura.Target.lastTickPosX
					+ (KillAura.Target.posX - KillAura.Target.lastTickPosX) * event.partialTicks
					- mc.getRenderManager().viewerPosX;
			double y = KillAura.Target.lastTickPosY
					+ (KillAura.Target.posY - KillAura.Target.lastTickPosY) * event.partialTicks
					- mc.getRenderManager().viewerPosY;
			final double z = KillAura.Target.lastTickPosZ
					+ (KillAura.Target.posZ - KillAura.Target.lastTickPosZ) * event.partialTicks
					- mc.getRenderManager().viewerPosZ;

			final double pix2 = Math.PI * 1.4D;

			float speed = 3200;
			float baseHue = (float) (System.currentTimeMillis() % (int) speed);
			while (baseHue > speed)
				baseHue -= speed;
			baseHue /= speed;
			if (baseHue > 0.5D)
				baseHue = 0.5F - baseHue - 0.5F;
			baseHue += 0.5F;

			float offset = 0.0f;

			for (int i = 0; i <= 90; ++i) {
				float max = ((float) i) / 45F;
				float hue = max + baseHue;
				while (hue > 1) {
					hue -= 1;
				}
				float f3 = (float) (color.getRGB() >> 24 & 255) / 255.0F;
				float f = (float) (color.getRGB() >> 16 & 255) / 255.0F;
				float f1 = (float) (color.getRGB() >> 8 & 255) / 255.0F;
				float f2 = (float) (color.getRGB() & 255) / 255.0F;
				final float red = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 0.7F)).getRed();
				final float green = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 0.7F)).getGreen();
				final float blue = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 0.7F)).getBlue();
				GL11.glColor3f(red, green, blue);
				GL11.glVertex3d(x + rad * Math.cos(i * pix2 / 45.0 + offs), y + (offset * (offset += 0.03)),
						z + rad * Math.sin(i * pix2 / 45.0 + offs));
			}

			offs += 0.1f;

			if (offs > 361.0f)
				offs = 0.0f;

			GL11.glEnd();
			GL11.glDepthMask(true);
			GlStateManager.enableDepth();
			GlStateManager.enableTexture2D();
			GL11.glPopMatrix();

		}
	}

	public float getRandomInRange(float min, float max) {
		Random random = ThreadLocalRandom.current();
		float range = max - min;
		float scaled = random.nextFloat() * range;
		if (scaled > max) {
			scaled = max;
		}
		float shifted = scaled + min;

		if (shifted > max) {
			shifted = max;
		}

		return (shifted + (float) ((target.hurtResistantTime + 0.1) / TargetHurtTime.getValueState().intValue()));
	}

	@EventTarget(4)
	public void onPre(EventPreMotion pr) {
		if (this.toP != null) {
			++this.ticksSinceDamage;
		} else {
			++this.damageBoostCooldown;
		}

		if (mc.thePlayer.isMovingKeyBindingActive() && !mc.thePlayer.isSneaking()
				&& !ModManager.getModByClass(Scaffold.class).isEnabled()
				&& (canStrafe() && (mc.gameSettings.keyBindJump.pressed || !OnlySpace.getValueState()))) {
			double pX = Minecraft.getMinecraft().thePlayer.lastReportedPosX;
			double pY = Minecraft.getMinecraft().thePlayer.lastReportedPosY
					+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight();
			double pZ = Minecraft.getMinecraft().thePlayer.lastReportedPosZ;
			double eX = Minecraft.getMinecraft().thePlayer.posX;
			double eY = Minecraft.getMinecraft().thePlayer.posY
					+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight();
			double eZ = Minecraft.getMinecraft().thePlayer.posZ;
			double dX = pX - eX;
			double dY = pY - eY;
			double dZ = pZ - eZ;
			double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
			double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
			double pitch = Math.toDegrees(Math.atan2(dH, dY));
			pr.yaw = (float) yaw;

			mc.thePlayer.rotationYawHead = (float) yaw;

		}
		

	}

	@EventTarget(Priority.LOWEST)
	private void onMove(EventMove e) {
		Speed speedM = (Speed) ModManager.getModByClass(Speed.class);

		if (this.target != KillAura.Target) {
			this.freeze = true;

			this.target = KillAura.Target;

			return;
		}

		if (this.freeze && !mc.thePlayer.onGround) {
			return;
		} else {
			this.freeze = false;
		}

		if (KillAura.Target != null) {
			this.target = KillAura.Target;
		} else {
			this.target = null;
		}
		if (mc.gameSettings.keyBindJump.pressed && mc.thePlayer.isMoving() && speedM.isEnabled()) {
			mc.thePlayer.jumpTicks = 4;
		}

		if (canStrafe() && (mc.gameSettings.keyBindJump.pressed || !OnlySpace.getValueState())) {
			
			
			if(OnlyBhop.getValueState() && !speedM.isEnabled())
				return;

			double speed = !speedM.isEnabled() ? e.getMovementSpeed() : speedM.moveSpeed;

			this.degree = StrictMath.atan2(this.mc.thePlayer.posZ - this.target.posZ,
					this.mc.thePlayer.posX - this.target.posX);

			try {

//				this.degree += -(speed / (this.mc.thePlayer.getDistanceXZToEntity(this.target)+ 0.1));

				this.degree += (this.left ? (speed / (this.mc.thePlayer.getDistanceXZToEntity(this.target)+ 0.1))
						: -(speed / (this.mc.thePlayer.getDistanceXZToEntity(this.target)+ 0.1)));
				
			} catch (Throwable c) {
				c.printStackTrace();
			}

			double x = this.target.posX
					+ this.getRandomInRange(min.getValueState().floatValue(), max.getValueState().floatValue())
							* Math.cos(this.degree);

			double z = this.target.posZ
					+ (this.getRandomInRange(min.getValueState().floatValue(), max.getValueState().floatValue()))
							* Math.sin(this.degree);

			if ((this.check.getValueState()).booleanValue() && needToChange(x, z)) {
				this.left = !this.left;
//				this.degree += 2.0D * (this.left ? (speed / (this.mc.thePlayer.getDistanceXZToEntity(this.target)
//						- this.mc.thePlayer.getDistanceXZToEntity(this.target) % 1 + 0.1))
//						: -(speed / (this.mc.thePlayer.getDistanceXZToEntity(this.target)
//								- this.mc.thePlayer.getDistanceXZToEntity(this.target) % 1 + 0.1)));
//				x = this.target.posX
//						+ this.getRandomInRange(min.getValueState().floatValue(), max.getValueState().floatValue())
//								* Math.cos(this.degree);
//
//				z = this.target.posZ
//						+ (this.getRandomInRange(min.getValueState().floatValue(), max.getValueState().floatValue()))
//								* Math.sin(this.degree);
				}
			

			float degr = MathUtils.toDegree(x, z);

			float multifierX = 0;
			float multifierZ = 0;
			float multifierY = 0;

			if (this.toP != null && this.damageBoostCooldown >= 10 && KnockBackBoost.getValueState()) {
				if (this.ticksSinceDamage < 5) {
					multifierX = toP.field_149152_f;
					multifierZ = toP.field_149159_h;
					multifierY = toP.field_149153_g;

					if (!ModManager.getModByClass(Speed.class).isEnabled())
						speed += MathHelper
								.clamp_float((float) ((Math.sqrt(multifierX * multifierX + multifierZ * multifierZ))
										- this.distance), 0.0f, 0.015f);

					this.damageBoostCooldown = 0;
				}

				toP = null;
			}

			e.setX(mc.thePlayer.motionX = speed * -Math.sin(Math.toRadians(degr)));
			e.setZ(mc.thePlayer.motionZ = (speed * Math.cos(Math.toRadians(degr))));

		}
	}

	public boolean canStrafe() {
		KillAura ka = (KillAura) ModManager.getModByClass(KillAura.class);
		return (this.target != null && PlayerUtil.isMoving() && ka.isEnabled()
				&& (!((Boolean) this.onlyInRange.getValueState()).booleanValue() || this.target.getDistanceXZToEntity(
						(Entity) this.mc.thePlayer) < ((Double) this.spaceRange.getValueState()).doubleValue()));
	}

	public boolean needToChange(double x, double z) {
		ZoomFly zoom = (ZoomFly) ModManager.getModByClass(ZoomFly.class);
//
		if (
				mc.thePlayer.hurtResistantTime > 0 && mc.thePlayer.hurtResistantTime <= SwapHurtTime.getValueState().intValue()
				
//				mc.thePlayer.isCollidedHorizontally
				
				&& this.mc.thePlayer.ticksExisted % 2 == 0)
			return true;
		
		if(!validatePoint(x, z))
			return true;
		
		for (int i = (int) (this.mc.thePlayer.posY + 4.0D); i >= 0; i--) {
			BlockPos playerPos = new BlockPos(x, i, z);
			if (this.mc.theWorld.getBlockState(playerPos).getBlock().equals(Blocks.lava)
					|| this.mc.theWorld.getBlockState(playerPos).getBlock().equals(Blocks.fire))
				return true;
			if (!this.mc.theWorld.isAirBlock(playerPos) || zoom.isEnabled())
				return false;
		}
		return true;
	}
	
    private boolean validatePoint(final double x, final double z) {
        final Vec3 pointVec = new Vec3(x, mc.thePlayer.posY, z);
        final IBlockState blockState = mc.theWorld.getBlockState(new BlockPos(pointVec));
        final boolean canBeSeen = mc.theWorld.rayTraceBlocks(mc.thePlayer.getPositionVector(), pointVec, false, false, false) == null;
        return !this.isOverVoid(x, z) && !blockState.getBlock().canCollideCheck(blockState, false) && canBeSeen;
    }
    
    private boolean isOverVoid(final double x, final double z) {
        for (double posY = mc.thePlayer.posY; posY > 0.0; --posY) {
        	
        	Block block = mc.theWorld.getBlockState(new BlockPos(x, posY, z)).getBlock();
        	
        	if(block instanceof BlockWeb)
        		return true;
        	
            if (!(block instanceof BlockAir)) {
                return false;
            }
        }
        return true;
    }
}
