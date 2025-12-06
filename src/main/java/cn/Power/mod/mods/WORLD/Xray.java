package cn.Power.mod.mods.WORLD;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Lists;

import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.events.EventTick;
import cn.Power.events.EventXray;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.util.Helper;
import cn.Power.util.RenderUtil;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.misc.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

public class Xray extends Mod {
	public static Value<Boolean> ESP = new Value("Xray_ESP", true);
//	public static Value<Boolean> Tracers = new Value("Xray_Tracers", false);
	public static Value<Boolean> Bypass = new Value("Xray_Cave", false);
	public static Value<Boolean> HPBypass = new Value("Xray_UHC", false);
	public static Value<Boolean> Coal_Ore = new Value("Xray_Coal", false);
	public static Value<Boolean> Iron_Ore = new Value("Xray_IronOre", false);
	public static Value<Boolean> Iron_Blo = new Value("Xray_IronBlock", false);
	public static Value<Boolean> Gold_Ore = new Value("Xray_Gold", true);
	public static Value<Boolean> Redstone_ore = new Value("Xray_Redstone", false);
	public static Value<Boolean> Diamond_Ore = new Value("Xray_Diamond", true);
	public static Value<Boolean> Emerald_Ore = new Value("Xray_Emerald", false);
	public static Value<Boolean> Lapix_Lazuli_Ore = new Value("Xray_Lapix Lazuli", false);
	public static Value<Boolean> Extreme = new Value("Xray_Extreme", true);

	public Value<Double> Reah = new Value<Double>("Xray_Reah", 50.0, 1.0, 60.0, 5.0);

	public Value<Double> ReachX = new Value<Double>("Xray_FurtherReachX", 5.0, 1.0, 6.0, 1.0);
	public Value<Double> ReachY = new Value<Double>("Xray_FurtherReachY", 5.0, 1.0, 6.0, 1.0);

	public Value<Double> DelayTaskInit = new Value<Double>("Xray_FurtherTaskDelayInit", 1.0, 1.0, 10.0, 1.0);
	public Value<Double> DelayTaskEach = new Value<Double>("Xray_FurtherTaskDelayEach", 1.0, 1.0, 10.0, 1.0);

	public Value<Double> Delay = new Value<Double>("Xray_FurtherDelayClick", 2.0, 0.0, 60.0, 0.1);

	public Value<Double> MaxDistance = new Value<Double>("Xray_FurtherMaxDistance", 9.0, 1.0, 12.0, 0.1);

	public Value<Double> Opacity = new Value<Double>("Xray_Xray Opacity", 160.0, 0.0, 255.0, 5.0);
	public Value<Double> EspOpacity = new Value<Double>("Xray_ESP Opacity", 160.0, 0.0, 255.0, 5.0);

	public static Value<Double> LineWteigh = new Value<Double>("Xray_ESP LineWeight", 1.0, 0.05, 7.0, 0.05);
	private static HashSet blockIDs = new HashSet();

	private final static Block[] checkblocks = { Blocks.obsidian, Blocks.clay, Blocks.mossy_cobblestone,
			Blocks.diamond_ore, Blocks.redstone_ore, Blocks.iron_ore, Blocks.coal_ore, Blocks.lapis_ore,
			Blocks.gold_ore, Blocks.emerald_ore, Blocks.quartz_ore };

	public static ArrayList<Integer> blocks;
	Thread updater;
	public boolean loaded;

	private static ArrayList<Integer> blockIds = Lists.newArrayList(new Integer[] {});
	public static CopyOnWriteArrayList<BlockPos> toRender = new CopyOnWriteArrayList();

    public static HashMap<BlockPos, Integer> BlockMap = new HashMap<>();

    
	public static CopyOnWriteArrayList<BlockPos> Que = new CopyOnWriteArrayList();

	ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);

	List KEY_IDS = Lists.newArrayList(new Integer[] { 10, 11, 8, 9, 14, 15, 16, 21, 41, 42, 46, 48, 52, 56, 57, 61, 62,
			73, 74, 84, 89, 103, 116, 117, 118, 120, 129, 133, 137, 145, 152, 153, 154 });

	static {
		Xray.blocks = new ArrayList<Integer>();
	}

	public Xray() {
		super("Xray", Category.WORLD);

	}

	@EventTarget
	public void onxray(EventXray e) {
		if (ESP.getValueState()) {
			BlockPos pos = new BlockPos(e.pos.getX(), e.pos.getY(), e.pos.getZ());
			float xDiff = (float) (mc.thePlayer.posX - pos.getX());
			float yDiff = (float) (mc.thePlayer.posY - pos.getY());
			float zDiff = (float) (mc.thePlayer.posZ - pos.getZ());
			float dis = MathHelper.sqrt_float(xDiff * xDiff + 0 + zDiff * zDiff);

	           if (!BlockMap.containsKey(pos) && blockIds.contains(Block.getIdFromBlock(mc.theWorld.getBlockState(pos).getBlock()))) {
	                BlockMap.put(pos, 0);
	            }
	           
			if (blockIds.contains(new Integer(Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos).getBlock())))) {
				if (dis < (mc.thePlayer.posY >= 50 ? 50F : Reah.getValueState()) && !this.toRender.contains(pos))
					this.toRender.add(pos);
			}

		}
	}

	   public double getDistance(BlockPos pos) {
	        float xDiff = (float) (mc.thePlayer.posX - pos.getX());
	        float yDiff = (float) (mc.thePlayer.posY - pos.getY());
	        float zDiff = (float) (mc.thePlayer.posZ - pos.getZ());
	        float dis = MathHelper.sqrt_float(xDiff * xDiff + 0 + zDiff * zDiff);
	        return dis;
	    }

	   
	@EventTarget
	public void tick(EventTick event) {
//		int i = 0;
//		while (i < this.toRender.size()) {
//			BlockPos pos_1 = this.toRender.get(i);
//			int id = Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos_1).getBlock());
//			if (!blockIds.contains(id)) {
//				this.toRender.remove(i);
//			}
//			++i;
//		}

		if (!BlockMap.isEmpty())
			for (BlockPos pos : BlockMap.keySet()) {
				int id = Block.getIdFromBlock(mc.theWorld.getBlockState(pos).getBlock());
				if (!blockIds.contains(id)) {
					BlockMap.remove(pos);
				}
			}

	}

	@EventTarget
	public void on3D(EventRender event) {

		if (ESP.getValueState()) {
//			for (BlockPos pos : this.toRender) {
//				if (blockIds.contains(Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos).getBlock())))
//					this.renderBlock(pos);
//
//				// System.out.println(Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos).getBlock()));
//
//			}
			
			 for (BlockPos pos : BlockMap.keySet()) {
	                int value = BlockMap.get(pos);
	                if (Block.getIdFromBlock(mc.theWorld.getBlockState(pos).getBlock()) == 56) {
	                	 if (value == 0 && getDistance(pos) <= 50) {
	 	                    this.renderBlock(pos);
	 	                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	 	                }
	 	                if (value == 1) {
	 	                    this.renderBlock(pos);
	 	                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	 	                }
	                }
	            }
			 
		}
//		if (Tracers.getValueState()) {
//			if(this.Extreme.getValueState())
//				return;
//			
//			for (BlockPos pos : this.toRender) {
//				this.drawLine(pos);
//			}
//		}
	}

	private void renderBlock(BlockPos pos) {
		double x = (double) pos.getX() - mc.getRenderManager().renderPosX;
		double y = (double) pos.getY() - mc.getRenderManager().renderPosY;
		double z = (double) pos.getZ() - mc.getRenderManager().renderPosZ;
		int ID = Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos).getBlock());
		int A = EspOpacity.getValueState().intValue();
		if (ID == 16 && Coal_Ore.getValueState()) {
			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(12, 12, 12, A)).getRGB());
		} else if (ID == 15 && Iron_Ore.getValueState()) {
			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(210, 210, 210, A)).getRGB());
		} else if (ID == 14 && Gold_Ore.getValueState()) {
			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(255, 255, 0, A)).getRGB());
		} else if (ID == 56 && Diamond_Ore.getValueState()) {
			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(0, 200, 200, A)).getRGB());
		} else if ((ID == 73 || ID == 74) && Redstone_ore.getValueState()) {
			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(255, 0, 0, A)).getRGB());
		} else if (ID == 129 && Emerald_Ore.getValueState()) {
			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(0, 202, 48, A)).getRGB());
		} else if (ID == 21 && Lapix_Lazuli_Ore.getValueState()) {
			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(0, 28, 122, A)).getRGB());
		} else /* if (ID == 42 && Iron_Blo.getValueState()) */ {
			RenderUtil.DrawSolidBlockESP(x, y, z, (new Color(210, 210, 210, A)).getRGB());
		}
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
		} else if ((ID == 73 || ID == 74) && Redstone_ore.getValueState()) {
			GL11.glColor4f(255, 0, 0, A);
		} else if (ID == 129 && Emerald_Ore.getValueState()) {
			GL11.glColor4f(0, 202, 48, A);
		} else if (ID == 21 && Lapix_Lazuli_Ore.getValueState()) {
			GL11.glColor4f(0, 0, 99, A);
		} else if (ID == 42 && Iron_Blo.getValueState()) {
			GL11.glColor4f(210, 210, 210, A);
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


        
		scheduledThreadPool = Executors.newScheduledThreadPool(2);

		if (!SkyBlockUtils.isMWgame() && Extreme.getValueState())
			scheduledThreadPool.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {

					try {

//						toRender.removeIf(pos -> {
//							return Math.sqrt(pos.distanceSq(mc.thePlayer.posX , mc.thePlayer.posY, mc.thePlayer.posZ)) > 4;
//								
//						});

						int radius1 = ReachX.getValueState().intValue();
						int height1 = ReachY.getValueState().intValue();
						for (int y = height1; y >= -height1; --y) {
							for (int x = -radius1; x < radius1; ++x) {
								for (int z = -radius1; z < radius1; ++z) {

									int xposX = (int) Math.floor(mc.thePlayer.posX) + x;
									int xposY = (int) Math.floor(mc.thePlayer.posY) + y;
									int xposZ = (int) Math.floor(mc.thePlayer.posZ) + z;

									if (Math.sqrt(new BlockPos(xposX, xposY, xposZ).distanceSq(mc.thePlayer.posX,
											mc.thePlayer.posY, mc.thePlayer.posZ)) > MaxDistance.getValueState()
													.intValue())
										continue;

									if (toRender.contains(new BlockPos(xposX, xposY, xposZ)))
										continue;

									Block block = mc.theWorld.getBlockState(new BlockPos(xposX, xposY, xposZ))
											.getBlock();

									boolean blockChecks = false;

									for (Block block1 : checkblocks) {
										if (block.equals(block1)) {
											blockChecks = true;
											break;
										}
									}

									blockChecks = blockChecks
											&& (block.getBlockHardness(mc.theWorld, BlockPos.ORIGIN) != -1.0F
													|| mc.playerController.isInCreativeMode());
									if (blockChecks) {

										if (KillAura.Target != null)
											break;

										if ((mc.objectMouseOver.getBlockPos() == null || mc.objectMouseOver
												.getBlockPos() != new BlockPos(xposX, xposY, xposZ))) {

											C07PacketPlayerDigging packet = new C07PacketPlayerDigging(
													C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
													new BlockPos(xposX, xposY, xposZ), EnumFacing.UP);

											Que.add(new BlockPos(xposX, xposY, xposZ));

											mc.getMinecraft().getNetHandler().getNetworkManager()
													.sendPacketNoEvent(packet);

											try {
												Thread.sleep(Delay.getValueState().longValue());
											} catch (Throwable c) {
											}
										}

									}

									if (!Xray.this.isEnabled())
										return;

								}
							}
						}

//						mc.addScheduledTask(() -> {
//							mc.renderGlobal.updateChunks(System.nanoTime() + 1000000000L);
//						});
//
//						final int var0 = (int) mc.thePlayer.posX;
//						final int var = (int) mc.thePlayer.posY;
//						final int var2 = (int) mc.thePlayer.posZ;
//						mc.renderGlobal.markBlockRangeForRenderUpdate(var0 - 900, var - 900, var2 - 900, var0 + 900,
//								var + 900, var2 + 900);

					} catch (Throwable c) {
						c.printStackTrace();
					}

				}

			}, this.DelayTaskInit.getValueState().intValue(), this.DelayTaskEach.getValueState().intValue(),
					TimeUnit.SECONDS);

		if (Coal_Ore.getValueState() && !blockIds.contains(16)) {
			blockIds.add(16);
		}
		if (Iron_Ore.getValueState() && !blockIds.contains(15)) {
			blockIds.add(15);
		}
		if (Iron_Blo.getValueState() && !blockIds.contains(42)) {
			blockIds.add(42);
		}
		if (Gold_Ore.getValueState() && !blockIds.contains(14)) {
			blockIds.add(14);
		}
		if (Diamond_Ore.getValueState() && !blockIds.contains(56)) {
			blockIds.add(56);
		}
		if (Redstone_ore.getValueState() && !blockIds.contains(73)) {
			blockIds.add(73);
		}
		if (Redstone_ore.getValueState() && !blockIds.contains(74)) {
			blockIds.add(74);
		}
		if (Emerald_Ore.getValueState() && !blockIds.contains(129)) {
			blockIds.add(129);
		}
		if (Lapix_Lazuli_Ore.getValueState() && !blockIds.contains(21)) {
			blockIds.add(21);
		}
		if (Extreme.getValueState() && !ESP.getValueState()) {
			ChatUtil.printChat("You need to turn ESP on to use Extreme!");
		}
		if (Extreme.getValueState() && blockIds.isEmpty()) {
			ChatUtil.printChat("ESP blocks is empty. Please choose Some blocks!");
		}

		this.mc.renderGlobal.loadRenderers();
		this.toRender.clear();
		Helper.dimblock.clear();
		blockIDs.clear();
		Helper.glow.clear();
		if (HPBypass.getValueState()) {
			Helper.UHCBypass = true;
		} else {
			Helper.UHCBypass = false;
		}
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
		Helper.opacity = Opacity.getValueState().intValue();
		Helper.blockIDs = blockIDs;

		toRender = new CopyOnWriteArrayList();

		Helper.xray = true;
		mc.renderGlobal.loadRenderers();
		final int var0 = (int) mc.thePlayer.posX;
		final int var = (int) mc.thePlayer.posY;
		final int var2 = (int) mc.thePlayer.posZ;
		mc.renderGlobal.markBlockRangeForRenderUpdate(var0 - 900, var - 900, var2 - 900, var0 + 900, var + 900,
				var2 + 900);

		loaded = true;

		BlockMap.clear();
	}

	@Override
	public void onDisable() {
		
//		BlockPos pos = new BlockPos(1,1,0);
//		
//        if (!BlockMap.containsKey(pos)) {
//            BlockMap.put(pos, 0);
//        }

        
        System.err.println(BlockMap.size());
		super.onDisable();
		blockIds.clear();
		Helper.xray = false;
		mc.renderGlobal.loadRenderers();
		Helper.dimblock.clear();

		this.toRender.clear();
		Que.clear();

		loaded = false;
		try {
			this.updater.interrupt();
			this.updater = null;
		} catch (Exception ex) {
		}

		scheduledThreadPool.shutdownNow();
	}

}