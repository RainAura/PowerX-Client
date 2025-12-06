package cn.Power.mod.mods.WORLD;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Lists;

import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.events.EventXray;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.Helper;
import cn.Power.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class XrayESP extends Mod {

	public static Value<Boolean> ESP = new Value("XrayESP_ESP", false);
	public static Value<Boolean> Tracers = new Value("XrayESP_Tracers", false);
	public static Value<Boolean> Bypass = new Value("XrayESP_Cave", false);
	public static Value<Boolean> HPBypass = new Value("XrayESP_UHC", false);
	public static Value<Boolean> Coal_Ore = new Value("XrayESP_Coal", false);
	public static Value<Boolean> Iron_Ore = new Value("XrayESP_Iron", false);
	public static Value<Boolean> Gold_Ore = new Value("XrayESP_Gold", true);
	public static Value<Boolean> Diamond_Ore = new Value("XrayESP_Diamond", true);
	public static Value<Boolean> Emerald_Ore = new Value("XrayESP_Emerald", false);
	public static Value<Boolean> Lapix_Lazuli_Ore = new Value("XrayESP_Lapix Lazuli", false);
	public Value<Double> EspOpacity = new Value<Double>("XrayESP_ESP Opacity", 160.0, 0.0, 255.0, 5.0);

	private static HashSet blockIDs = new HashSet();
	public static ArrayList<BlockPos> glow;
	public static ArrayList<Integer> blocks;
	Thread updater;
	public boolean loaded;

	private static ArrayList<Integer> blockIds = Lists.newArrayList(new Integer[] {});
	private ArrayList<BlockPos> toRender = new ArrayList();

	List KEY_IDS = Lists.newArrayList(new Integer[] { 10, 11, 8, 9, 14, 15, 16, 21, 41, 42, 46, 48, 52, 56, 57, 61, 62,
			73, 74, 84, 89, 103, 116, 117, 118, 120, 129, 133, 137, 145, 152, 153, 154 });

	static {
		Xray.blocks = new ArrayList<Integer>();
	}

	public XrayESP() {
		super("XrayESP", Category.WORLD);

	}

	@EventTarget
	public void onxray(EventXray e) {
		float xDiff = (float) (mc.thePlayer.posX - e.pos.getX());
		float yDiff = (float) (mc.thePlayer.posY - e.pos.getY());
		float zDiff = (float) (mc.thePlayer.posZ - e.pos.getZ());
		float dis = MathHelper.sqrt_float(xDiff * xDiff + 0 + zDiff * zDiff);

		if (ESP.getValueState() || Tracers.getValueState()) {
			BlockPos pos = new BlockPos(e.pos.getX(), e.pos.getY(), e.pos.getZ());
			if (!this.toRender.contains(pos) && dis < (mc.thePlayer.posY > 50 ? 50F : 60.0F) && blockIds
					.contains(new Integer(Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos).getBlock())))) {
				this.toRender.add(pos);
			}
			int i = 0;
			while (i < this.toRender.size()) {
				BlockPos pos_1 = this.toRender.get(i);
				int id = Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos_1).getBlock());
				if (!blockIds.contains(id)) {
					this.toRender.remove(i);
				}
				++i;
			}
		}
	}

	@EventTarget
	public void on3D(EventRender event) {
		if (ESP.getValueState()) {
			for (BlockPos pos : this.toRender) {
				this.renderBlock(pos);
			}
		}
		if (Tracers.getValueState()) {
			for (BlockPos pos : this.toRender) {
				this.drawLine(pos);
			}
		}
	}

	private void renderBlock(BlockPos pos) {
		this.mc.getRenderManager();
		double x = (double) pos.getX() - mc.getRenderManager().renderPosX;
		this.mc.getRenderManager();
		double y = (double) pos.getY() - mc.getRenderManager().renderPosY;
		this.mc.getRenderManager();
		double z = (double) pos.getZ() - mc.getRenderManager().renderPosZ;
		int ID = Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos).getBlock());
		int A = EspOpacity.getValueState().intValue();
//		if (ID == 16 && Coal_Ore.getValueState()) {
//			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(12, 12, 12, A)).getRGB());
//		} else if (ID == 15 && Iron_Ore.getValueState()) {
//			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(210, 210, 210, A)).getRGB());
//		} else if (ID == 14 && Gold_Ore.getValueState()) {
//			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(255, 255, 0, A)).getRGB());
//		} else if (ID == 56 && Diamond_Ore.getValueState()) {
//			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(0, 200, 200, A)).getRGB());
//		} else if (ID == 129 && Emerald_Ore.getValueState()) {
//			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(0, 202, 48, A)).getRGB());
//		} else if (ID == 21 && Lapix_Lazuli_Ore.getValueState()) {
//			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(0, 28, 122, A)).getRGB());
//		}
	}

	private void drawLine(BlockPos pos) {
		this.mc.getRenderManager();
		double x = (double) pos.getX() - mc.getRenderManager().renderPosX + 0.5;
		this.mc.getRenderManager();
		double y = (double) pos.getY() - mc.getRenderManager().renderPosY + 0.5;
		this.mc.getRenderManager();
		double z = (double) pos.getZ() - mc.getRenderManager().renderPosZ + 0.5;
		GL11.glPushMatrix();
		GL11.glEnable((int) 3042);
		GL11.glEnable((int) 2848);
		GL11.glDisable((int) 2929);
		GL11.glDisable((int) 3553);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glLineWidth((float) 1f);
		float xDiff = (float) (mc.thePlayer.posX - pos.getX());
		float yDiff = (float) (mc.thePlayer.posY - pos.getY());
		float zDiff = (float) (mc.thePlayer.posZ - pos.getZ());
		int ID = Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos).getBlock());
		int A = EspOpacity.getValueState().intValue();
		if (ID == 16 && Coal_Ore.getValueState()) {
			GL11.glColor4f(12, 12, 12, A);
		} else if (ID == 15 && Iron_Ore.getValueState()) {
			GL11.glColor4f(210, 210, 210, A);
		} else if (ID == 14 && Gold_Ore.getValueState()) {
			GL11.glColor4f(255, 255, 0, A);
		} else if (ID == 56 && Diamond_Ore.getValueState()) {
			GL11.glColor4f(0, 200, 200, A);
		} else if (ID == 129 && Emerald_Ore.getValueState()) {
			GL11.glColor4f(0, 202, 48, A);
		} else if (ID == 21 && Lapix_Lazuli_Ore.getValueState()) {
			GL11.glColor4f(0, 28, 122, A);
		}

		GL11.glLoadIdentity();
		boolean bobbing = this.mc.gameSettings.viewBobbing;
		this.mc.gameSettings.viewBobbing = false;
		this.mc.entityRenderer.orientCamera(this.mc.timer.renderPartialTicks);
		GL11.glBegin((int) 3);
		GL11.glVertex3d((double) 0.0, (double) this.mc.thePlayer.getEyeHeight(), (double) 0.0);
		GL11.glVertex3d((double) x, (double) y, (double) z);
		GL11.glVertex3d((double) x, (double) (y), (double) z);
		GL11.glEnd();
		this.mc.gameSettings.viewBobbing = bobbing;
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDisable((int) 2848);
		GL11.glDisable((int) 3042);
		GL11.glPopMatrix();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		if (Coal_Ore.getValueState() && !blockIds.contains(16)) {
			blockIds.add(16);
		}
		if (Iron_Ore.getValueState() && !blockIds.contains(15)) {
			blockIds.add(15);
		}
		if (Gold_Ore.getValueState() && !blockIds.contains(14)) {
			blockIds.add(14);
		}
		if (Diamond_Ore.getValueState() && !blockIds.contains(56)) {
			blockIds.add(56);
		}
		if (Emerald_Ore.getValueState() && !blockIds.contains(129)) {
			blockIds.add(129);
		}
		if (Lapix_Lazuli_Ore.getValueState() && !blockIds.contains(21)) {
			blockIds.add(21);
		}
		this.mc.renderGlobal.loadRenderers();
		this.toRender.clear();
		Helper.dimblock.clear();
		blockIDs.clear();
		Helper.glow.clear();
		Helper.UHCBypass = HPBypass.getValueState() ? true : false;

		try {
			Iterator var1 = this.KEY_IDS.iterator();

			while (var1.hasNext()) {
				Integer o = (Integer) var1.next();
				blockIDs.add(o);
				Xray.blocks.add(o);
			}
		} catch (Exception var3) {
			var3.printStackTrace();
		}
		Helper.bypass = Bypass.getValueState() ? true : false;
		Helper.xrayesp = true;
		mc.renderGlobal.loadRenderers();
		final int var0 = (int) mc.thePlayer.posX;
		final int var = (int) mc.thePlayer.posY;
		final int var2 = (int) mc.thePlayer.posZ;
		mc.renderGlobal.markBlockRangeForRenderUpdate(var0 - 900, var - 900, var2 - 900, var0 + 900, var + 900,
				var2 + 900);

		loaded = true;

	}

	@Override
	public void onDisable() {
		super.onDisable();
		blockIds.clear();
		mc.renderGlobal.loadRenderers();
		Helper.dimblock.clear();
		Helper.xrayesp = false;
		loaded = false;
		try {
			this.updater.interrupt();
			this.updater = null;
		} catch (Exception ex) {
		}
	}
}
