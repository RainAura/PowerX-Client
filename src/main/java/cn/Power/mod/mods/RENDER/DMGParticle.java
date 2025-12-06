package cn.Power.mod.mods.RENDER;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Font.FontManager;
import cn.Power.events.EventRender;
import cn.Power.events.EventTick;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.Location;
import cn.Power.util.Particles;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;

public class DMGParticle extends Mod {
	private HashMap<EntityLivingBase, Float> healthMap;
	private List<Particles> particles;

	public DMGParticle() {
		super("DMGParticle", Category.RENDER);
		this.healthMap = new HashMap<EntityLivingBase, Float>();
		this.particles = new CopyOnWriteArrayList<Particles>();
	}

	@EventTarget
	public void onUpdate(EventTick e) {
		this.particles.forEach(this::onUpdate);
	}

	@EventTarget
	public void onLivingUpdate(EventRender e) {
		for (Object o : mc.theWorld.loadedEntityList) {
			if (!(o instanceof EntityLivingBase) || o == mc.thePlayer || (o instanceof EntityArmorStand))
				continue;
			EntityLivingBase entity = (EntityLivingBase) o;
			if (!this.healthMap.containsKey(entity)) {
				this.healthMap.put((EntityLivingBase) entity, ((EntityLivingBase) entity).getHealth());
			}
			float floatValue = this.healthMap.get(entity);
			float health = ((EntityLivingBase) entity).getHealth();
			if (floatValue != health) {

				String DmgHealth;
				if (floatValue - health < 0.0f) {
					DmgHealth = "\247a" + roundToPlace((floatValue - health) * -1.0f, 1);
				} else {
					DmgHealth = "\247c" + roundToPlace(floatValue - health, 1);
				}
				Location p_i1238_1_ = new Location(entity);
				p_i1238_1_.setY(entity.getEntityBoundingBox().minY
						+ (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) / 2.0);
				p_i1238_1_.setX(p_i1238_1_.getX() - 0.5 + new Random(System.currentTimeMillis()).nextInt(5) * 0.1);
				p_i1238_1_.setZ(p_i1238_1_.getZ() - 0.5
						+ new Random(System.currentTimeMillis() + (0x203FF36645D9EA2EL ^ 0x203FF36645D9EA2FL))
								.nextInt(5) * 0.1);
				this.particles.add(new Particles(p_i1238_1_, DmgHealth));
				this.healthMap.remove(entity);
				this.healthMap.put((EntityLivingBase) entity, ((EntityLivingBase) entity).getHealth());
			}
		}
	}

	public static double roundToPlace(double p_roundToPlace_0_, int p_roundToPlace_2_) {
		if (p_roundToPlace_2_ < 0) {
			throw new IllegalArgumentException();
		}
		return new BigDecimal(p_roundToPlace_0_).setScale(p_roundToPlace_2_, RoundingMode.HALF_UP).doubleValue();
	}

	@EventTarget
	public void onRender(EventRender event) {
		for (Particles Particles : this.particles) {
			double x = Particles.location.getX();
			this.mc.getRenderManager();
			double n = x - mc.getRenderManager().renderPosX;
			double y = Particles.location.getY();
			this.mc.getRenderManager();
			double n2 = y - mc.getRenderManager().renderPosY;
			double z = Particles.location.getZ();
			this.mc.getRenderManager();
			double n3 = z - mc.getRenderManager().renderPosZ;
			GlStateManager.pushMatrix();
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
			GlStateManager.translate((float) n, (float) n2, (float) n3);
			GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
			float p_rotate_1_;
			if (this.mc.gameSettings.thirdPersonView == 2) {
				p_rotate_1_ = -1.0f;
			} else {
				p_rotate_1_ = 1.0f;
			}
			GlStateManager.rotate(this.mc.getRenderManager().playerViewX, p_rotate_1_, 0.0f, 0.0f);
			double p_scale_4_ = 0.03;
			GlStateManager.scale(-p_scale_4_, -p_scale_4_, p_scale_4_);
//            RenderUtils.enableGL2D();
//            RenderUtils.disableGL2D();
			GL11.glDepthMask(false);
			FontManager.baloo18.drawStringWithShadow(Particles.text,
					(float) (-(this.mc.fontRendererObj.getStringWidth(Particles.text) / 2)),
					(float) (-(this.mc.fontRendererObj.FONT_HEIGHT - 1)), -1);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glDepthMask(true);
			GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
			GlStateManager.disablePolygonOffset();
			GlStateManager.popMatrix();
		}
	}

	private void onUpdate(Particles Particles) {
		++Particles.ticks;
		if (Particles.ticks <= 10) {
			Particles.location.setY(Particles.location.getY() + Particles.ticks * 0.005);
		}
		if (Particles.ticks > 20) {
			this.particles.remove(Particles);
		}
	}
}
