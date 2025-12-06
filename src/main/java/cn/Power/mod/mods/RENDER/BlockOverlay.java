package cn.Power.mod.mods.RENDER;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.ClientUtil;
import cn.Power.util.Colors;
import cn.Power.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class BlockOverlay extends Mod {
	public Value<Double> r = new Value("BlockOverlay_Red", 255d, 0d, 255d, 1d);
	public Value<Double> g = new Value("BlockOverlay_Green", 255d, 0d, 255d, 1d);
	public Value<Double> b = new Value("BlockOverlay_Blue", 255d, 0d, 255d, 1d);
	public Value<Boolean> togg = new Value("BlockOverlay_RenderString", true);

	public BlockOverlay() {
		super("BlockOverlay", Category.RENDER);
	}

	public int getRed() {
		return (int) this.r.getValueState().intValue();
	}

	public int getGreen() {
		return (int) this.g.getValueState().intValue();
	}

	public int getBlue() {
		return (int) this.b.getValueState().intValue();
	}

	public boolean getRender() {
		return this.togg.getValueState().booleanValue();
	}

	@EventTarget
	public void onRender(EventRender2D event) {
//    	System.err.println(this.mc.theWorld.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock());
		if (this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			FontRenderer fr = this.mc.fontRendererObj;
			BlockPos pos = this.mc.objectMouseOver.getBlockPos();
			Block block = this.mc.theWorld.getBlockState(pos).getBlock();
			int id = Block.getIdFromBlock(block);
			String s = String.valueOf(block.getLocalizedName()) + " ID:" + id;
			String s2 = block.getLocalizedName();
			String s3 = " ID:" + id;
			if (this.mc.objectMouseOver != null && this.getRender()) {
				ScaledResolution res = new ScaledResolution(this.mc);
				int x = res.getScaledWidth() / 2 + 10;
				int y = res.getScaledHeight() / 2 + 6;
				Gui.drawRect((float) x, (float) y, (float) (x + fr.getStringWidth(s) + 3), y + fr.FONT_HEIGHT + 0.5f,
						ClientUtil.INSTANCE.reAlpha(Colors.BLACK.c, 0.7f));
				fr.drawStringWithShadow(s2, (float) (x + 2.0f), (float) (y) + 1.25f, Colors.WHITE.c);
				fr.drawStringWithShadow(s3, x + fr.getStringWidth(s2) + 2.0f, (float) (y) + 1.25f, Colors.GREY.c);
			}
		}
	}

	@EventTarget
	public void onRender3D(EventRender event) {
		if (this.mc.objectMouseOver != null
				&& this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			BlockPos pos = this.mc.objectMouseOver.getBlockPos();
			Block block = this.mc.theWorld.getBlockState(pos).getBlock();
			String s = block.getLocalizedName();
			this.mc.getRenderManager();
			double x = pos.getX() - mc.getRenderManager().renderPosX;
			this.mc.getRenderManager();
			double y = pos.getY() - mc.getRenderManager().renderPosY;
			this.mc.getRenderManager();
			double z = pos.getZ() - mc.getRenderManager().renderPosZ;
			GL11.glPushMatrix();
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glDisable(3553);
			GL11.glEnable(2848);
			GL11.glDisable(2929);
			GL11.glDepthMask(false);
			GL11.glColor4f(this.getRed() / 255.0f, this.getGreen() / 255.0f, this.getBlue() / 255.0f, 0.15f);
			double minX = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0
					: block.getBlockBoundsMinX();
			double minY = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0
					: block.getBlockBoundsMinY();
			double minZ = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0
					: block.getBlockBoundsMinZ();
			RenderUtil.drawBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(),
					y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
			GL11.glColor4f(this.getRed() / 255.0f, this.getGreen() / 255.0f, this.getBlue() / 255.0f, 1.0f);
			GL11.glLineWidth(1f);
			RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ,
					x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
			GL11.glDisable(2848);
			GL11.glEnable(3553);
			GL11.glEnable(2929);
			GL11.glDepthMask(true);
			GL11.glDisable(3042);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}

}
