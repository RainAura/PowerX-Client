package cn.Power.mod.mods.RENDER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.events.EventRenderBlock;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BlockESP extends Mod {
	private Minecraft mc = Minecraft.getMinecraft();
	public static List<BlockPos> toRender = new ArrayList();
	public Value<Boolean> dia = new Value("BlockESP_Diamond", Boolean.valueOf(true));
	public Value<Boolean> gold = new Value("BlockESP_Gold", Boolean.valueOf(true));
	public Value<Boolean> iron = new Value("BlockESP_Iron", Boolean.valueOf(true));
	public Value<Boolean> lapis = new Value("BlockESP_Lapis", Boolean.valueOf(true));
	public Value<Boolean> emerald = new Value("BlockESP_Emerald", Boolean.valueOf(true));
	public Value<Boolean> coal = new Value("BlockESP_Coal", Boolean.valueOf(true));
	public Value<Boolean> redstone = new Value("BlockESP_Redstone", Boolean.valueOf(true));
	public Value<Boolean> bypass = new Value("BlockESP_TouchingAirOrLiquidTest", Boolean.valueOf(true));
	public Value<Double> depth = new Value("BlockESP_TestDepth", Double.valueOf(2.0D), Double.valueOf(1.0D),
			Double.valueOf(5.0D), 1.0D);
	public Value<Boolean> radiusOn = new Value("BlockESP_DistanceLimitEnabled", Boolean.valueOf(true));
	public Value<Double> radius = new Value("BlockESP_DistanceLimit", Double.valueOf(10.0D), Double.valueOf(5.0D),
			Double.valueOf(100.0D), 5.0D);
	public Value<Boolean> limitEnabled = new Value("BlockESP_RenderLimitEnabled", Boolean.valueOf(true));
	public Value<Double> limit = new Value("BlockESP_RenderLimit", Double.valueOf(10.0D), Double.valueOf(5.0D),
			Double.valueOf(100.0D), 5.0D);
	public Value<Double> refresh_timer = new Value("BlockESP_RefreshListDelayMillis", Double.valueOf(20.0D),
			Double.valueOf(0.0D), Double.valueOf(1000.0D), 20.0D);
	public Value<Double> alpha = new Value("BlockESP_Alpha", Double.valueOf(0.25D), Double.valueOf(0.0D),
			Double.valueOf(1.0D), 0.05D);
	public Value<Double> width = new Value("BlockESP_LineWidth", Double.valueOf(2.5D), Double.valueOf(1.0D),
			Double.valueOf(10.0D), 0.5D);
	private TimeHelper refresh = new TimeHelper();

	public BlockESP() {
		super("BlockESP", Category.RENDER);
	}

	public void onEnable() {
		toRender.clear();
		this.mc.renderGlobal.loadRenderers();
	}

	@EventTarget
	public void onTick(EventUpdate e) {
		if (this.refresh.delay(((Double) this.refresh_timer.getValueState()).floatValue())) {
			List<BlockPos> list = toRender;
			list = (List) list.stream().filter(this::test).collect(Collectors.toList());
			toRender = list;
		}
	}

	@EventTarget
	public void onRenderBlock(EventRenderBlock event) {
		BlockPos blockpos = new BlockPos(event.x, event.y, event.z);
		if (!toRender.contains(blockpos) && this.test(blockpos)
				&& ((double) toRender.size() <= ((Double) this.limit.getValueState()).doubleValue()
						|| !((Boolean) this.limitEnabled.getValueState()).booleanValue())) {
			toRender.add(blockpos);
		}

	}

	public boolean isTarget(BlockPos pos) {
		Block block = Minecraft.theWorld.getBlockState(pos).getBlock();
		return Blocks.diamond_ore.equals(block) ? ((Boolean) this.dia.getValueState()).booleanValue()
				: (Blocks.lapis_ore.equals(block) ? ((Boolean) this.lapis.getValueState()).booleanValue()
						: (Blocks.iron_ore.equals(block) ? ((Boolean) this.iron.getValueState()).booleanValue()
								: (Blocks.gold_ore.equals(block) ? ((Boolean) this.gold.getValueState()).booleanValue()
										: (Blocks.coal_ore.equals(block)
												? ((Boolean) this.coal.getValueState()).booleanValue()
												: (Blocks.emerald_ore.equals(block)
														? ((Boolean) this.emerald.getValueState()).booleanValue()
														: (!Blocks.redstone_ore.equals(block)
																&& !Blocks.lit_redstone_ore.equals(block) ? false
																		: ((Boolean) this.redstone.getValueState())
																				.booleanValue()))))));
	}

	private Boolean oreTest(BlockPos origPos, Double depth) {
		Collection<BlockPos> collection = new ArrayList<BlockPos>();
		Collection<BlockPos> collection1 = new ArrayList<BlockPos>(Collections.singletonList(origPos));
		Collection<BlockPos> collection2 = new ArrayList<BlockPos>();

		for (int i = 0; (double) i < depth.doubleValue(); ++i) {
			for (BlockPos blockpos : collection1) {
				collection.add(blockpos.up());
				collection.add(blockpos.down());
				collection.add(blockpos.north());
				collection.add(blockpos.south());
				collection.add(blockpos.west());
				collection.add(blockpos.east());
			}

			for (BlockPos blockpos1 : collection) {
				if (collection1.contains(blockpos1)) {
					collection.remove(blockpos1);
				}
			}

			collection1 = collection;
			collection2.addAll(collection);
			collection = new ArrayList();
		}

		List<Block> legitBlocks = Arrays
				.<Block>asList(new Block[] { Blocks.water, Blocks.lava, Blocks.flowing_lava, Blocks.air });
		return collection2.stream()
				.anyMatch(blockPos -> legitBlocks.contains(Minecraft.theWorld.getBlockState(blockPos).getBlock()));
	}

	public static float[] getColor(BlockPos pos) {
		Minecraft.getMinecraft();
		Block block = Minecraft.theWorld.getBlockState(pos).getBlock();
		return Blocks.diamond_ore.equals(block) ? new float[] { 0.0F, 1.0F, 1.0F }
				: (Blocks.lapis_ore.equals(block) ? new float[] { 0.0F, 0.0F, 1.0F }
						: (Blocks.iron_ore.equals(block) ? new float[] { 1.0F, 1.0F, 1.0F }
								: (Blocks.gold_ore.equals(block) ? new float[] { 1.0F, 1.0F, 0.0F }
										: (Blocks.coal_ore.equals(block) ? new float[] { 0.0F, 0.0F, 0.0F }
												: (Blocks.emerald_ore.equals(block) ? new float[] { 0.0F, 1.0F, 0.0F }
														: (!Blocks.redstone_ore.equals(block)
																&& !Blocks.lit_redstone_ore.equals(block)
																		? new float[] { 0.0F, 0.0F, 0.0F }
																		: new float[] { 1.0F, 0.0F, 0.0F }))))));
	}

	public boolean test(BlockPos pos1) {
		return !this
				.isTarget(pos1)
						? false
						: (((Boolean) this.bypass
								.getValueState()).booleanValue() && !this
										.oreTest(pos1, (Double) this.depth.getValueState()).booleanValue()
												? false
												: (((Boolean) this.radiusOn.getValueState()).booleanValue()
														? Minecraft.thePlayer.getDistance((double) pos1.getX(),
																(double) pos1.getY(),
																(double) pos1.getZ()) < ((Double) this.radius
																		.getValueState()).doubleValue()
														: true));
	}

	@EventTarget
	public void onRender(EventRender event) {
		for (BlockPos blockpos : toRender) {
			this.renderBlock(blockpos);
		}
	}

	private void renderBlock(BlockPos pos) {
		this.mc.getRenderManager();
		double x = (double) pos.getX() - mc.getRenderManager().renderPosX;
		this.mc.getRenderManager();
		double y = (double) pos.getY() - mc.getRenderManager().renderPosY;
		this.mc.getRenderManager();
		double z = (double) pos.getZ() - mc.getRenderManager().renderPosZ;
		float[] afloat = getColor(pos);
		drawOutlinedBlockESP(x, y, z, afloat[0], afloat[1], afloat[2],
				((Double) this.alpha.getValueState()).floatValue(), ((Double) this.width.getValueState()).floatValue());
	}

	public static void drawOutlinedBlockESP(double x, double y, double z, float red, float green, float blue,
			float alpha, float lineWidth) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glLineWidth(lineWidth);
		GL11.glColor4f(red, green, blue, alpha);
		drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D));
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(3, DefaultVertexFormats.POSITION);
		worldrenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldrenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldrenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldrenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldrenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		tessellator.draw();
		worldrenderer.begin(3, DefaultVertexFormats.POSITION);
		worldrenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldrenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldrenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldrenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldrenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		tessellator.draw();
		worldrenderer.begin(1, DefaultVertexFormats.POSITION);
		worldrenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldrenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldrenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldrenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldrenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldrenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldrenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldrenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		tessellator.draw();
	}
}
