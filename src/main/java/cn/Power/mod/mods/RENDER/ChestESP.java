package cn.Power.mod.mods.RENDER;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.events.EventRenderTileEntity;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.OutlineUtil;
import cn.Power.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class ChestESP extends Mod {
	private Value mode = new Value("ChestESP", "Mode", 0);
//	public static Value<Boolean> chest = new Value("ChestESP_chest", true);
//	public static Value<Boolean> enderChest = new Value("ChestESP_enderChest", false);
//	public static Value<Boolean> box = new Value("ChestESP_box", false);
//	public Value<Double> Width = new Value<Double>("ChestESP_Width", 1.5, 0.5, 5.0, 0.1);
//	public Value<Double> Alpha = new Value<Double>("ChestESP_box Alpha", 0.5, 0.1, 1.0, 0.1);

	public ChestESP() {
		super("ChestESP", Category.RENDER);
//		this.mode.mode.add("Box");
		this.mode.mode.add("Outline");

	}

	@EventTarget
	public void onTileRender(EventRenderTileEntity event) {
//		if (this.mode.isCurrentMode("Outline")) {
//			TileEntity tileentityIn = event.getTileentityIn();
//			float partialTicks = event.getPartialTicks();
//			int destroyStage = event.getDestroyStage();
//			BlockPos blockpos = tileentityIn.getPos();
//			if (!(tileentityIn instanceof TileEntityChest)) {
//				return;
//			}
//
//			RenderUtil.checkSetupFBO();
//			event.getTileEntityRendererDispatcher().renderTileEntityAt(tileentityIn,
//					(double) blockpos.getX() - event.getStaticPlayerX(),
//					(double) blockpos.getY() - event.getStaticPlayerY(),
//					(double) blockpos.getZ() - event.getStaticPlayerZ(), partialTicks, destroyStage);
//			RenderUtil.outlineOne();
//			event.getTileEntityRendererDispatcher().renderTileEntityAt(tileentityIn,
//					(double) blockpos.getX() - event.getStaticPlayerX(),
//					(double) blockpos.getY() - event.getStaticPlayerY(),
//					(double) blockpos.getZ() - event.getStaticPlayerZ(), partialTicks, destroyStage);
//			RenderUtil.outlineTwo();
//			event.getTileEntityRendererDispatcher().renderTileEntityAt(tileentityIn,
//					(double) blockpos.getX() - event.getStaticPlayerX(),
//					(double) blockpos.getY() - event.getStaticPlayerY(),
//					(double) blockpos.getZ() - event.getStaticPlayerZ(), partialTicks, destroyStage);
//			RenderUtil.outlineThree();
//			RenderUtil.outlineFour();
//			GL11.glLineWidth(2.0F);
//			Color rainbow = Gui.rainbow(System.nanoTime(), 1.0F, 1.0F);
//			RenderUtil.color(new Color(255, 175, 0, 255).getRGB());
//			event.getTileEntityRendererDispatcher().renderTileEntityAt(tileentityIn,
//					(double) blockpos.getX() - event.getStaticPlayerX(),
//					(double) blockpos.getY() - event.getStaticPlayerY(),
//					(double) blockpos.getZ() - event.getStaticPlayerZ(), partialTicks, destroyStage);
//			RenderUtil.outlineFive();
//			event.cancel = true;
//		}

	}
	
	  @EventTarget
	    public void on3DRender(EventRender e) {
		  if (this.mode.isCurrentMode("Outline")) {
			OutlineUtil.renderOne(2.0f);
			rendertileEntityOutline(e.getPartialTicks());
			OutlineUtil.renderTwo();
			rendertileEntityOutline(e.getPartialTicks());
			OutlineUtil.renderThree();
			rendertileEntityOutline(e.getPartialTicks());
			OutlineUtil.renderFour(Hud.getColor());
			rendertileEntityOutline(e.getPartialTicks());
			OutlineUtil.renderFive();
		  }
	  }
	  
		private void rendertileEntityOutline(float partialTicks) {
			  for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
					if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest) {
		                float renderX = (float) (tileEntity.getPos().getX() - mc.getRenderManager().viewerPosX);
						float renderY = (float) (tileEntity.getPos().getY() - mc.getRenderManager().viewerPosY);
						float renderZ = (float) (tileEntity.getPos().getZ() - mc.getRenderManager().viewerPosZ);
						double minX = renderX;
						double minY = renderY;
						double minZ = renderZ;
						double maxX = renderX + tileEntity.getBlockType().getBlockBoundsMaxX();
						double maxY = renderY + tileEntity.getBlockType().getBlockBoundsMaxY();
						double maxZ = renderZ + tileEntity.getBlockType().getBlockBoundsMaxZ();
						double negXDoubleChest = 0;
						double posXDoubleChest = 0;
						double negZDoubleChest = 0;
						double posZDoubleChest = 0;
							if (tileEntity instanceof TileEntityChest) {
								negXDoubleChest = ((TileEntityChest) tileEntity).adjacentChestXNeg != null ? 1 : 0D;
								posXDoubleChest = ((TileEntityChest) tileEntity).adjacentChestXPos != null ? 0.875 : 0D;
								negZDoubleChest = ((TileEntityChest) tileEntity).adjacentChestZNeg != null ? 1 : 0D;
								posZDoubleChest = ((TileEntityChest) tileEntity).adjacentChestZPos != null ? 0.875 : 0D;
							}
							minX = (renderX + 0.0625) - negXDoubleChest;
							minY = renderY;
							minZ = (renderZ + 0.0625) - negZDoubleChest;
							maxX = (renderX + 0.9375) - posXDoubleChest;
							maxY = renderY + 0.875;
							maxZ = (renderZ + 0.9375) - posZDoubleChest;
							GL11.glPushMatrix();
							GL11.glEnable((int) 3042);
							GL11.glBlendFunc((int) 770, (int) 771);
							GL11.glDisable((int) 3553);
							GL11.glEnable((int) 2848);
							GL11.glDisable((int) 2929);
							GL11.glDepthMask((boolean) false);
							
							RenderUtil.drawBoundingBox(new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
							GL11.glDisable((int) 2848);
							GL11.glEnable((int) 3553);
							GL11.glEnable((int) 2929);
							GL11.glDepthMask((boolean) true);
							GL11.glDisable((int) 3042);
							GL11.glPopMatrix();
						}
//					TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, partialTicks, -1);
				}
		}
		

}
