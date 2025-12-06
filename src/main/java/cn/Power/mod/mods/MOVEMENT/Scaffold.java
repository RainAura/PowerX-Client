package cn.Power.mod.mods.MOVEMENT;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.Value;
import cn.Power.native0;
import cn.Power.Font.FontManager;
import cn.Power.events.EventJump;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender;
import cn.Power.events.EventRender2D;
import cn.Power.events.EventRespawn;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.MathUtils;
import cn.Power.util.PlayerUtil;
import cn.Power.util.RenderUtil;
import cn.Power.util.timeUtils.NovoTimer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class Scaffold extends Mod {

	private BlockData blockData;

	private static final Rotation rotation = new Rotation(555.0f, 75.0f);

	private Value<Boolean> silent = new Value<Boolean>("Scaffold_Silent", false);
	private Value<Boolean> PICKER = new Value<Boolean>("Scaffold_Picker", false);
	private Value<Boolean> DOWN = new Value<Boolean>("Scaffold_DownScaffold", false);
	private Value<Boolean> Tower = new Value<Boolean>("Scaffold_Tower", true);
	private Value<Boolean> noSwing = new Value<Boolean>("Scaffold_NoSwing", false);
	private Value<Boolean> Safewalk = new Value<Boolean>("Scaffold_Safewalk", true);
	public static Value<Boolean> Rotary_animation = new Value<Boolean>("Scaffold_Rotary animation", false);

	public static Value<Double> TimerNormalXZ = new Value<Double>("Scaffold_TimerXZ", 1.05, 0.5, 2.0, 0.01);
	public static Value<Double> TimerNormalY = new Value<Double>("Scaffold_TimerY", 1.05, 0.5, 2.0, 0.01);

	public static Value<Double> TimerBoostTicks = new Value<Double>("Scaffold_TimerBoostLimit", 2.0, 0.0, 20.0, 1);

	public static List<Block> blacklisted;
	private static List<Block> blacklistedBlocks, invalid;
	private int block = -1;
	private double moveSpeed;
	public int boost;

	public BlockPos blockBelow;

	public int oldSlot = 0;

	public static List<Block> getBlacklistedBlocks() {
		return blacklistedBlocks;
	}

	public Scaffold() {
		super("Scaffold", Category.MOVEMENT);

		blacklisted = Arrays.asList(new Block[] { Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava,
				Blocks.flowing_lava, Blocks.enchanting_table, Blocks.ender_chest, Blocks.yellow_flower, Blocks.carpet,
				Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.crafting_table,
				Blocks.snow_layer, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore,
				Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.gold_ore,
				Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate,
				Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
				Blocks.stone_button, Blocks.wooden_button, Blocks.cactus, Blocks.lever, Blocks.activator_rail,
				Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.furnace, Blocks.ladder, Blocks.oak_fence,
				Blocks.redstone_torch, Blocks.iron_trapdoor, Blocks.trapdoor, Blocks.tripwire_hook, Blocks.hopper,
				Blocks.sand, Blocks.acacia_fence_gate, Blocks.birch_fence_gate, Blocks.dark_oak_fence_gate,
				Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate, Blocks.dispenser,
				Blocks.sapling, Blocks.tallgrass, Blocks.deadbush, Blocks.web, Blocks.red_flower, Blocks.red_mushroom,
				Blocks.tnt, Blocks.waterlily, Blocks.brown_mushroom, Blocks.nether_brick_fence, Blocks.vine,
				Blocks.double_plant, Blocks.flower_pot, Blocks.beacon, Blocks.pumpkin, Blocks.lit_pumpkin,
				Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest,
				Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water,
				Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox,
				Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate,
				Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate,
				Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2,
				Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil,
				Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder,
				Blocks.web, Blocks.ender_chest });
		blacklistedBlocks = invalid = Arrays.asList(new Block[] { Blocks.air, Blocks.water, Blocks.flowing_water,
				Blocks.lava, Blocks.flowing_lava, Blocks.ender_chest, Blocks.enchanting_table, Blocks.stone_button,
				Blocks.wooden_button, Blocks.crafting_table, Blocks.beacon, Blocks.furnace, Blocks.chest,
				Blocks.trapped_chest, Blocks.iron_bars, Blocks.cactus, Blocks.ladder });

	}

	@EventTarget(3)
	public void onRender2D(EventRender2D event) {
		ScaledResolution res = new ScaledResolution(this.mc);
		int color = new Color(255, 0, 0).getRGB();
		if (this.getBlockCount() >= 64 && 128 > this.getBlockCount()) {
			color = new Color(255, 255, 0).getRGB();
		} else if (this.getBlockCount() >= 128) {
			color = new Color(0, 255, 0).getRGB();
		}
		// mc.fontRendererObj.drawStringWithShadow("B:"+String.valueOf(this.getBlockCount()),
		// res.getScaledWidth() / 2+7, res.getScaledHeight() / 2 - 3, color);

		ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(block + 36).getStack();
		GL11.glPushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack != null ? stack : new ItemStack(Blocks.barrier),
				res.getScaledWidth() / 2 + 10, res.getScaledHeight() / 2 - 8);
		RenderHelper.disableStandardItemLighting();
		GL11.glPopMatrix();

		FontManager.baloo18.drawStringWithShadow(this.getBlockCount() + "", res.getScaledWidth() / 2 + 28,
				res.getScaledHeight() / 2 - 6, color);

	}

	@EventTarget
	public void onRespawn(EventRespawn respawnEvent) {
		this.toggle();
		ClientUtil.sendChatMessage(String.valueOf(" " + this.getName()) + EnumChatFormatting.RED + " Disabled "
				+ EnumChatFormatting.RESET + " (Auto)", ChatType.INFO);
	}

	public float[] GetPosV2(final Vec3 vec) {
		double X = vec.xCoord - Minecraft.thePlayer.posX;
		double Y = vec.yCoord - (Minecraft.thePlayer.posY + (double) Minecraft.thePlayer.getEyeHeight());
		double Z = vec.zCoord - Minecraft.thePlayer.posZ;
		double dist = MathHelper.sqrt_double(X * X + Z * Z);
		float yaw = (float) (Math.atan2(Z, X) * 180.0 / 3.141592653589793) - 90.0f;
		float pitch = (float) (-Math.atan2(Y, dist) * 180.0 / 3.141592653589793);
		float Yaw = Minecraft.thePlayer.rotationYaw
				+ MathHelper.wrapAngleTo180_float(yaw - Minecraft.thePlayer.rotationYaw);
		float Pitch = Minecraft.thePlayer.rotationPitch
				+ MathHelper.wrapAngleTo180_float(pitch - Minecraft.thePlayer.rotationPitch);
		return new float[] { Yaw, Pitch };
	}

	public static int Down(final double n) {
		final int n2 = (int) n;
		try {
			if (n < n2) {
				return n2 - 1;
			}
		} catch (IllegalArgumentException ex) {
		}
		return n2;
	}

	public void d(final EventPreMotion event) {

		EventPreMotion e = event;

		final double n = e.y % 1.0;
		final double n2 = (double) Down(e.y);

		List<Double> list = Arrays.asList(0.41999998688698D, 0.7531999805212D);

		if (n > 0.419D && n < 0.753D) {
			e.y = (n2 + (double) Double.valueOf(list.get(0)));
		} else if (n > 0.753D) {
			e.y = (n2 + (double) Double.valueOf(list.get(1)));
		} else {
			e.y = n2;

			e.onGround = (true);
		}

		if (!Minecraft.thePlayer.isMovingKeyBindingActive()) {

			Minecraft.thePlayer.setSpeed(0);

			Minecraft.thePlayer.motionX = Minecraft.thePlayer.motionZ = 0;

			e.x = (e.x + ((Minecraft.thePlayer.ticksExisted % 2 == 0)
					? ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D)
					: (-ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D))));
			e.z = (e.z + ((Minecraft.thePlayer.ticksExisted % 2 != 0)
					? ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D)
					: (-ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D))));
		}

	}

	static int ticksGround;

	public double getMoveSpeed() {
		return Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
	}

	@EventTarget
	public void onPre(EventPreMotion event) {
//		getBestBlocks();

		ticksGround = (mc.thePlayer.onGround ? (ticksGround + 1) : 0);

		if (ModManager.getModByClass(KillAura.class).isEnabled())
			ModManager.getModByClass(KillAura.class).toggle();

		this.block = this.block == -1 ? this.getBiggestBlockSlotHotbar() - 36 : this.getBlockSlot();

		if (ModManager.getModByClass(Speed.class).isEnabled())
			ModManager.getModByClass(Speed.class).set(false);

		boost++;
		if (boost >= 14) {
			boost = 0;
		}

		if (mc.thePlayer.fallDistance < 3
				&& boost >= TimerBoostTicks.getValueState().intValue() + ThreadLocalRandom.current().nextInt(3)
				&& (mc.thePlayer.isMovingKeyBindingActive() || mc.gameSettings.keyBindJump.isKeyDown())) {

			float value = mc.gameSettings.keyBindJump.isKeyDown() ? TimerNormalY.getValueState().floatValue()
					: TimerNormalXZ.getValueState().floatValue();

			if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
				if (mc.thePlayer.onGround && getMoveSpeed() > 0.0) {
					double p = (0.28630206268501246d * 0.98 / getMoveSpeed());

					if (p < 1.3 && p > 0.2)
						mc.timer.timerSpeed = (float) ThreadLocalRandom.current().nextDouble(p - 0.01,
								Math.max(p, value));

				}
			} else {
				
				
				mc.timer.timerSpeed = (float) ThreadLocalRandom.current().nextDouble(value - 0.1, value);

			}

		} else {
			mc.timer.timerSpeed = 0.98F;
		}

		double x = mc.thePlayer.posX;
		double y = mc.thePlayer.posY - 1;
		double z = mc.thePlayer.posZ;

		this.setDisplayName("Hypixel");

		blockBelow = new BlockPos(x, y, z);
		if (block != -1) {
			if (invCheck()) {
				return;
			}

			if (this.block != -1)
				mc.thePlayer.inventory.currentItem = block;

//			if (this.getMaterial(blockBelow).isReplaceable()) {

			this.blockData = this.getBlockData(blockBelow);
			if (this.blockData == null)
				this.blockData = this.getBlockData(blockBelow.down());

//			}else {
//				this.blockData = null;
//			}

			if (this.blockData != null && this.blockData.hitVec != null) {
				float[] rot = GetPosV2(blockData.hitVec);

				rotation.setYaw(rot[0]);

				rotation.setPitch(rot[1]);

			}

			if (mc.gameSettings.keyBindJump.getIsKeyPressed() && Tower.getValueState() && mc.thePlayer.motionY > 0)
				d(event);

			if (rotation.getYaw() != 555.0f) {

				event.setYaw(this.blockData == null ? GetYawStage() : rotation.getYaw());
			}
			if (rotation.getPitch() != 75.0f) {

				event.setPitch(rotation.getPitch());
			}

			if (invCheck() || block == -1 || !this.Tower.getValueState() || !mc.gameSettings.keyBindJump.isKeyDown()
					|| !Minecraft.thePlayer.isMovingKeyBindingActive()) {
				return;
			}

			if (Scaffold.isOnGround(0.76) && !isOnGround(0.75) && Minecraft.thePlayer.motionY > 0.23
					&& Minecraft.thePlayer.motionY < 0.25) {
				Minecraft.thePlayer.motionY = Math.round(Minecraft.thePlayer.posY) - Minecraft.thePlayer.posY;
			}

			if (Scaffold.isOnGround(1.0E-4)) {
				Minecraft.thePlayer.motionY = mc.thePlayer.getJumpHeight(0.41999998688698);
			} else if (Minecraft.thePlayer.posY >= Math.round(Minecraft.thePlayer.posY) - 1.0E-4
					&& Minecraft.thePlayer.posY <= Math.round(Minecraft.thePlayer.posY) + 1.0E-4
					&& !mc.gameSettings.keyBindSneak.isKeyDown()) {
				Minecraft.thePlayer.motionY = 0.0;
			}

		}

	}

	public static boolean isOnGround(final double height) {
		if (!Minecraft.theWorld.getCollidingBoundingBoxes(Minecraft.thePlayer,
				Minecraft.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty()) {
			return true;
		}
		return false;
	}

	@EventTarget
	public void onMoveTower(EventMove e) {

		if (invCheck() || block == -1 || !this.Tower.getValueState() || !mc.gameSettings.keyBindJump.isKeyDown()
				|| Minecraft.thePlayer.isMovingKeyBindingActive()) {
			return;
		}

		if (!Minecraft.thePlayer.isMovingKeyBindingActive()) {

			;

			Minecraft.thePlayer.setSpeed(e.x = e.z = Minecraft.thePlayer.motionX = Minecraft.thePlayer.motionZ = 0);

			;

			if (mc.thePlayer.onGround)
				Minecraft.thePlayer.setPosition(Scaffold.Down(Minecraft.thePlayer.posX) + 0.5, Minecraft.thePlayer.posY,
						Scaffold.Down(Minecraft.thePlayer.posZ) + 0.5);

		}

		if (Scaffold.isOnGround(0.76) && !isOnGround(0.75) && Minecraft.thePlayer.motionY > 0.23
				&& Minecraft.thePlayer.motionY < 0.25) {
			e.y = Minecraft.thePlayer.motionY = Math.round(Minecraft.thePlayer.posY) - Minecraft.thePlayer.posY;
		}

		if (Scaffold.isOnGround(1.0E-4)) {
			e.y = Minecraft.thePlayer.motionY = mc.thePlayer.getJumpHeight(0.41999998688698);
		} else if (Minecraft.thePlayer.posY >= Math.round(Minecraft.thePlayer.posY) - 1.0E-4
				&& Minecraft.thePlayer.posY <= Math.round(Minecraft.thePlayer.posY) + 1.0E-4
				&& !mc.gameSettings.keyBindSneak.isKeyDown()) {
			e.y = Minecraft.thePlayer.motionY = 0.0;
		}
	}

	public float GetYawStage() {
		float method454 = MathHelper.wrapAngleTo180_float(Minecraft.thePlayer.rotationYaw);

		float method457 = Minecraft.thePlayer.moveForward;
		float method456 = Minecraft.thePlayer.moveStrafing;

		if (method457 != 0.0f) {
			if (method456 < 0.0f) {
				method454 += ((method457 < 0.0f) ? 135.0f : 45.0f);
			}
			if (method456 > 0.0f) {
				method454 -= ((method457 < 0.0f) ? 135.0f : 45.0f);
			}
			if (method456 != 0.0f || method457 >= 0.0f) {
				return MathHelper.wrapAngleTo180_float(method454 - 180.0f);
			}
			method454 -= 180.0f;
		}
		if (method456 < 0.0f) {
			method454 += 90.0f;
		}
		if (method456 > 0.0f) {
			method454 -= 90.0f;
		}
		return MathHelper.wrapAngleTo180_float(method454 - 180.0f);
	}

	public static double getBaseMoveSpeed() {
		double baseSpeed = 0.2873D;
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
			int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
		}
		return baseSpeed;
	}

	private boolean canPlace1(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack, BlockPos hitPos,
			EnumFacing side, Vec3 vec3) {
		if (heldStack != null && heldStack.getItem() != null && heldStack.getItem() instanceof ItemBlock) {
			return ((ItemBlock) heldStack.getItem()).canPlaceBlockOnSide(worldIn, hitPos, side, player, heldStack);
		}
		return false;
	}

	static int ticksPlace;

	@EventTarget
	public void onSafe(EventPostMotion event) {
		if (this.getMaterial(blockBelow).isReplaceable()) {
			for (int i = 36; i < 45; i++) {
				if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
					Item item = is.getItem();

					;

					blockBelow = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
					if (this.getMaterial(blockBelow).isReplaceable() && block != -1 && item instanceof ItemBlock
							&& !blacklisted.contains(((ItemBlock) item).getBlock())
							&& !((ItemBlock) item).getBlock().getLocalizedName().toLowerCase().contains("chest")
							&& blockData != null
							&& canPlace1(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(block),
									this.blockData.position, this.blockData.face, this.blockData.hitVec)) {
						if ((!DOWNSCAFFOLD() && DOWN.getValueState() && !mc.gameSettings.keyBindJump.getIsKeyPressed()
								&& mc.gameSettings.keyBindSneak.getIsKeyPressed())) {
							return;
						}

						if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
								mc.thePlayer.inventory.getStackInSlot(block), this.blockData.position,
								this.blockData.face, this.blockData.hitVec)) {
							if (this.noSwing.getValueState().booleanValue()) {
								mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
							} else {
								mc.thePlayer.swingItem();
							}

							ticksPlace++;

						}

						mc.playerController.updateController();
						return;
					}
				}
			}
		}

	}

	public void ScaffoldESP(EventRender event) {
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glDisable((int) 3553);
		for (Object o : this.mc.theWorld.loadedEntityList) {
			if (o instanceof EntityPlayer && o == mc.thePlayer) {
				EntityPlayer ent = (EntityPlayer) o;
//                GL11.glRotated((double)(-mc.thePlayer.rotationYaw % 360.0f), 0.0, 1.0, 0.0);
				if (mc.gameSettings.keyBindJump.getIsKeyPressed()) {
					RenderUtil.scaffoldESP(ent, new Color(255, 170, 0), event);
				} else if (ent.isSneaking()) {
					RenderUtil.scaffoldESP(ent, new Color(0, 255, 170), event);
				} else {
					RenderUtil.scaffoldESP(ent, new Color(0, 0, 0), event);
				}
			}
		}
		GL11.glEnable((int) 3553);
		GL11.glEnable((int) 2929);
		GL11.glDisable((int) 3042);
	}

	public Material getMaterial(final BlockPos pos) {
		return this.getBlock(pos).getMaterial();
	}

	public Block getBlock(final BlockPos pos) {
		return this.getState(pos).getBlock();
	}

	public IBlockState getState(final BlockPos pos) {
		return this.mc.theWorld.getBlockState(pos);
	}

	private void grabBlocks() {
		for (int i = 9; i < 36; ++i) {
			final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			if (stack != null && stack.getItem() instanceof ItemBlock && stack.stackSize >= 1
					&& Block.getBlockFromItem(stack.getItem()).getDefaultState().getBlock().isFullBlock()) {
				final PlayerControllerMP playerController = this.mc.playerController;
				final int windowId = this.mc.thePlayer.openContainer.windowId;
				final int slotId = i;
				playerController.windowClick(windowId, slotId, 1, 2, this.mc.thePlayer);
				break;
			}
		}
	}

	protected void swap(int slot, int hotbarNum) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, mc.thePlayer);
	}

	private double getDoubleRandom(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	private boolean canPlace(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack, BlockPos hitPos,
			EnumFacing side, Vec3 vec3) {
		if (heldStack.getItem() instanceof ItemBlock) {
			return ((ItemBlock) heldStack.getItem()).canPlaceBlockOnSide(worldIn, hitPos, side, player, heldStack);
		}
		return false;
	}

	/*
	 * private int getBlockCount() { int blockCount = 0; int i = 0; while (i < 45) {
	 * if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) { ItemStack
	 * itemStack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack(); Item
	 * item = itemStack.getItem(); if (itemStack.getItem() instanceof ItemBlock &&
	 * !blacklisted.contains(((ItemBlock)item).getBlock())) { blockCount +=
	 * itemStack.stackSize; } } ++i; } return blockCount; }
	 */

	public int getBlockCount() {
		int blockCount = 0;
		for (int i = 0; i < 45; ++i) {
			if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
				continue;
			ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			Item item = is.getItem();
			if (!(is.getItem() instanceof ItemBlock) || this.blacklisted.contains(((ItemBlock) item).getBlock()))
				continue;
			blockCount += is.stackSize;
		}
		return blockCount;
	}
	/*
	 * private int getblock() { int blockCount = 0; for(int i = 36; i < 45; ++i) {
	 * if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) { ItemStack
	 * itemStack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack(); Item
	 * item = itemStack.getItem(); if (itemStack.getItem() instanceof ItemBlock &&
	 * !blacklisted.contains(((ItemBlock)item).getBlock())) { blockCount +=
	 * itemStack.stackSize; } } } return blockCount; }
	 */

	private boolean invCheck() {
		int i = 36;
		while (i < 45) {
			Item item;
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
					&& (item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()) instanceof ItemBlock
					&& !blacklisted.contains(((ItemBlock) item).getBlock())) {
				return false;
			}
			++i;
		}
		return true;
	}

	private boolean isHotbarEmpty() {
		for (int i = 36; i < 45; ++i) {
			Item item;
			if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
					|| !((item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()) instanceof ItemBlock)
					|| this.blacklisted.contains(((ItemBlock) item).getBlock()))
				continue;
			return false;
		}
		return true;
	}

	private int getBlockSlot() {
		if (Minecraft.thePlayer.inventoryContainer.getSlot((int) (this.block + 36)).getHasStack()
				&& (Minecraft.thePlayer.inventoryContainer.getSlot((int) (this.block + 36)).getStack()
						.getItem() instanceof ItemBlock)
				&& this.isValid(
						Minecraft.thePlayer.inventoryContainer.getSlot((int) (this.block + 36)).getStack().getItem())
				&& Minecraft.thePlayer.inventoryContainer.getSlot((int) (this.block + 36)).getStack().stackSize > 10) {

			return this.block;

		}

		for (int i = 36; i < 45; ++i) {
			ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || itemStack.stackSize <= 0)
				continue;
			if (blacklisted.stream().anyMatch(e -> e.equals(((ItemBlock) itemStack.getItem()).getBlock())))
				continue;
			return i - 36;
		}
		return -1;
	}

	public int getBiggestBlockSlotHotbar() {

		oldSlot = mc.thePlayer.inventory.currentItem;

		int slot = -1;
		int size = 0;
		if (getBlockCount() == 0)
			return -1;
		for (int i = 36; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (item instanceof ItemBlock && isValid(item)) {
					if (is.stackSize > size) {
						size = is.stackSize;
						slot = i;
					}
				}
			}
		}
		return slot;
	}

	private boolean hotbarContainBlock() {
		int i = 36;

		while (i < 45) {
			try {
				ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if ((stack == null) || (stack.getItem() == null) || !(stack.getItem() instanceof ItemBlock)
						|| !isValid(stack.getItem())) {
					i++;
					continue;
				}
				return true;
			} catch (Exception e) {

			}
		}

		return false;

	}

	public boolean isAirBlock(Block block) {
		if (block.getMaterial().isReplaceable()) {
			if (block instanceof BlockSnow && block.getBlockBoundsMaxY() > 0.125) {
				return false;
			}
			return true;
		}

		return false;
	}

	private boolean isValid(Item item) {
		if (!(item instanceof ItemBlock)) {
			return false;
		} else {
			ItemBlock iBlock = (ItemBlock) item;
			Block block = iBlock.getBlock();
			if (blacklisted.contains(block)) {
				return false;
			}
		}
		return true;
	}

	public int getBiggestBlockSlotInv() {
		int slot = -1;
		int size = 0;
		if (getBlockCount() == 0)
			return -1;
		for (int i = 9; i < 36; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (item instanceof ItemBlock && isValid(item)) {
					if (is.stackSize > size) {
						size = is.stackSize;
						slot = i;
					}
				}
			}
		}
		return slot;
	}

	public void getBestBlocks() {

		if (getBlockCount() == 0)
			return;
		if ((Boolean) PICKER.getValueState()) {
			ItemStack is = new ItemStack(Item.getItemById(261));
			int bestInvSlot = getBiggestBlockSlotInv();
			int bestHotbarSlot = getBiggestBlockSlotHotbar();
			int bestSlot = getBiggestBlockSlotHotbar() > 0 ? getBiggestBlockSlotHotbar() : getBiggestBlockSlotInv();
			int spoofSlot = 42;
			if (bestHotbarSlot > 0 && bestInvSlot > 0) {
				if (mc.thePlayer.inventoryContainer.getSlot(bestInvSlot).getHasStack()
						&& mc.thePlayer.inventoryContainer.getSlot(bestHotbarSlot).getHasStack()) {
					if (mc.thePlayer.inventoryContainer.getSlot(bestHotbarSlot)
							.getStack().stackSize < mc.thePlayer.inventoryContainer.getSlot(bestInvSlot)
									.getStack().stackSize) {
						bestSlot = bestInvSlot;
					}
				}
			}
			if (hotbarContainBlock()) {
				for (int a = 36; a < 45; a++) {
					if (mc.thePlayer.inventoryContainer.getSlot(a).getHasStack()) {
						Item item = mc.thePlayer.inventoryContainer.getSlot(a).getStack().getItem();
						if (item instanceof ItemBlock && isValid(item)) {
							spoofSlot = a;
							break;
						}
					}
				}
			} else {
				for (int a = 36; a < 45; a++) {
					if (!mc.thePlayer.inventoryContainer.getSlot(a).getHasStack()) {
						spoofSlot = a;
						break;
					}
				}
			}

			if (mc.thePlayer.inventoryContainer.getSlot(spoofSlot).slotNumber != bestSlot) {
				swap(bestSlot, spoofSlot - 36);
				mc.playerController.updateController();

			}
		} else {
//			if (invCheck() || block == -1) {
//				ItemStack is = new ItemStack(Item.getItemById(261));
//				for (int i = 9; i < 36; i++) {
//					if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
//						Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
//						int count = 0;
//						if (item instanceof ItemBlock && isValid(item)) {
//							for (int a = 36; a < 45; a++) {
//								if (mc.thePlayer.inventoryContainer
//										.canAddItemToSlot(mc.thePlayer.inventoryContainer.getSlot(a), is, true)) {
//									swap(i, a - 36);
//									count++;
//									break;
//								}
//							}
//							if (count == 0) {
//								swap(i, 7);
//							}
//							break;
//						}
//					}
//				}
//			}
		}
	}

	private boolean DOWNSCAFFOLD() {
		if (DOWN.getValueState() && !mc.thePlayer.onGround && mc.gameSettings.keyBindSneak.getIsKeyPressed()
				&& !mc.gameSettings.keyBindJump.getIsKeyPressed()) {
			return true;
		}
		return false;
	}

	private boolean isPosSolid(final Block block) {
		return !blacklistedBlocks.contains(block)
				&& (block.getMaterial().isSolid() || !block.isTranslucent() || block.isVisuallyOpaque()
						|| block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow
						|| block instanceof BlockSkull)
				&& !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
	}

	private BlockData getBlockData(final BlockPos pos) {

		if (isPosSolid(mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock())) {
			return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
		}

		if (isPosSolid(mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock())) {
			return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
		}
		if (isPosSolid(mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
		}
		if (isPosSolid(mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
		}
		if (isPosSolid(mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock())) {
			return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
		}

		// EAST
		BlockPos add = pos.add(0, 0, 0);
		/*
		 * if (isPosSolid(mc.theWorld.getBlockState(pos.add(-1, 1, 0)).getBlock())) {
		 * return new BlockData(pos.add(-1, 1, 0), EnumFacing.DOWN); }
		 */
		if (isPosSolid(mc.theWorld.getBlockState(add.add(-1, 0, 0)).getBlock())) {
			return new BlockData(add.add(-1, 0, 0), EnumFacing.EAST);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add.add(1, 0, 0)).getBlock())) {
			return new BlockData(add.add(1, 0, 0), EnumFacing.WEST);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add.add(0, 0, -1)).getBlock())) {
			return new BlockData(add.add(0, 0, -1), EnumFacing.SOUTH);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add.add(0, 0, 1)).getBlock())) {
			return new BlockData(add.add(0, 0, 1), EnumFacing.NORTH);
		}

		// WEST
		BlockPos add2 = pos.add(1, 0, 0);
		/*
		 * if (isPosSolid(mc.theWorld.getBlockState(pos.add(1, 1, 0)).getBlock())) {
		 * return new BlockData(pos.add(1, 1, 0), EnumFacing.DOWN); }
		 */
		if (isPosSolid(mc.theWorld.getBlockState(add2.add(-1, 0, 0)).getBlock())) {
			return new BlockData(add2.add(-1, 0, 0), EnumFacing.EAST);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add2.add(1, 0, 0)).getBlock())) {
			return new BlockData(add2.add(1, 0, 0), EnumFacing.WEST);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add2.add(0, 0, -1)).getBlock())) {
			return new BlockData(add2.add(0, 0, -1), EnumFacing.SOUTH);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add2.add(0, 0, 1)).getBlock())) {
			return new BlockData(add2.add(0, 0, 1), EnumFacing.NORTH);
		}

		// SOUTH
		BlockPos add3 = pos.add(0, 0, -1);
		/*
		 * if (isPosSolid(mc.theWorld.getBlockState(pos.add(0, 1, -1)).getBlock())) {
		 * return new BlockData(pos.add(0, 1, -1), EnumFacing.DOWN); }
		 */
		if (isPosSolid(mc.theWorld.getBlockState(add3.add(-1, 0, 0)).getBlock())) {
			return new BlockData(add3.add(-1, 0, 0), EnumFacing.EAST);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add3.add(1, 0, 0)).getBlock())) {
			return new BlockData(add3.add(1, 0, 0), EnumFacing.WEST);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add3.add(0, 0, -1)).getBlock())) {
			return new BlockData(add3.add(0, 0, -1), EnumFacing.SOUTH);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add3.add(0, 0, 1)).getBlock())) {
			return new BlockData(add3.add(0, 0, 1), EnumFacing.NORTH);
		}

		// NORTH
		BlockPos add4 = pos.add(0, 0, 1);
		/*
		 * if (isPosSolid(mc.theWorld.getBlockState(pos.add(0, 1, 1)).getBlock())) {
		 * return new BlockData(pos.add(0, 1, 1), EnumFacing.DOWN); }
		 */
		if (isPosSolid(mc.theWorld.getBlockState(add4.add(-1, 0, 0)).getBlock())) {
			return new BlockData(add4.add(-1, 0, 0), EnumFacing.EAST);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add4.add(1, 0, 0)).getBlock())) {
			return new BlockData(add4.add(1, 0, 0), EnumFacing.WEST);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add4.add(0, 0, -1)).getBlock())) {
			return new BlockData(add4.add(0, 0, -1), EnumFacing.SOUTH);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add4.add(0, 0, 1)).getBlock())) {
			return new BlockData(add4.add(0, 0, 1), EnumFacing.NORTH);
		}
		// DOWN
		if (isPosSolid(mc.theWorld.getBlockState(add.add(1, 1, 0)).getBlock())) {
			return new BlockData(add.add(1, 1, 0), EnumFacing.DOWN);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add.add(-1, 2, -1)).getBlock())) {
			return new BlockData(add.add(-1, 2, -1), EnumFacing.DOWN);
		}
		// DOWN
		if (isPosSolid(mc.theWorld.getBlockState(add2.add(-2, 1, 0)).getBlock())) {
			return new BlockData(add2.add(-2, 1, 0), EnumFacing.DOWN);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add2.add(0, 2, 1)).getBlock())) {
			return new BlockData(add2.add(0, 2, 1), EnumFacing.DOWN);
		}
		// DOWN
		if (isPosSolid(mc.theWorld.getBlockState(add3.add(0, 1, 2)).getBlock())) {
			return new BlockData(add3.add(0, 1, 2), EnumFacing.DOWN);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add3.add(1, 2, 0)).getBlock())) {
			return new BlockData(add3.add(1, 2, 0), EnumFacing.DOWN);
		}
		// DOWN
		if (isPosSolid(mc.theWorld.getBlockState(add4.add(0, 1, -2)).getBlock())) {
			return new BlockData(add4.add(0, 1, -2), EnumFacing.DOWN);
		}
		if (isPosSolid(mc.theWorld.getBlockState(add4.add(-1, 2, 0)).getBlock())) {
			return new BlockData(add4.add(-1, 2, 0), EnumFacing.DOWN);
		}
		return null;
	}

	public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;
		x += (double) face.getFrontOffsetX() / 2;
		z += (double) face.getFrontOffsetZ() / 2;
		y += (double) face.getFrontOffsetY() / 2;
		if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
			x += randomNumber(0.3, -0.3);
			z += randomNumber(0.3, -0.3);
		} else {
			y += randomNumber(0.3, -0.3);
		}
		if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
			z += randomNumber(0.3, -0.3);
		}
		if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
			x += randomNumber(0.3, -0.3);
		}
		return new Vec3(x, y, z);
	}

	public static double randomNumber(double max, double min) {
		return (Math.random() * (max - min)) + min;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		this.block = -1;
		ticksPlace = 0;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		blockData = null;
		ticksPlace = 0;
		Timer.timerSpeed = 1.0f;
		if (this.silent.getValueState()) {
			mc.thePlayer.inventory.currentItem = oldSlot;
//			mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));
		} else {
			mc.thePlayer.inventory.currentItem = oldSlot;
		}

		mc.playerController.updateController();
		this.block = -1;
	}

	private static class BlockData {
		private Vec3 vec;
		private final BlockPos position;
		private final EnumFacing face;

		private final Vec3 hitVec;

		public BlockData(final BlockPos pos, final EnumFacing facing) {
			this.position = pos;
			this.face = facing;

			this.hitVec = getHitVec();
		}

		private Vec3 getHitVec() {
			Vec3i directionVec = face.getDirectionVec();
			double x = directionVec.getX() * 0.5D;
			double z = directionVec.getZ() * 0.5D;

			if (face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
				x = -x;
				z = -z;
			}

			Vec3 hitVec = (new Vec3((Vec3i) this.position)).addVector(x + z, directionVec.getY() * 0.5D, x + z);

			Vec3 src = Minecraft.thePlayer.getPositionEyes(1.0F);
			MovingObjectPosition obj = Minecraft.theWorld.rayTraceBlocks(src, hitVec, false, false, true);

			if (obj == null || obj.hitVec == null || obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
				return null;
			}
			if (face != EnumFacing.DOWN && face != EnumFacing.UP) {
				obj.hitVec = obj.hitVec.addVector(0.0D, -0.2D, 0.0D);
			}
			return obj.hitVec;
		}

		public Vec3 getVec() {
			return this.vec;
		}

		public void setVec(final Vec3 vec) {
			this.vec = vec;
		}

		public BlockPos getBlockPos() {
			return this.position;
		}

		public EnumFacing getEnumFacing() {
			return this.face;
		}
	}

	@EventTarget
	public void onJump(EventJump e) {
		if (this.blockData != null && this.Tower.getValueState())
			e.setCancelled(true);
	}

	public NovoTimer timer = new NovoTimer();

	@EventTarget
	public void onPacket(EventPacket e) {
		if (e.getEventType() == EventType.RECEIVE && e.getPacket() instanceof S08PacketPlayerPosLook) {
			timer.b();
		} else if (e.getEventType() == EventType.SEND && e.getPacket() instanceof C07PacketPlayerDigging) {
			final C07PacketPlayerDigging p = (C07PacketPlayerDigging) e.getPacket();
			if (p.getStatus().equals(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)
					|| p.getStatus().equals(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
				e.setCancelled(true);
			}
		}
	}

	private boolean shouldBoost() {
		return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? (mc.thePlayer.ticksExisted % 3 != 0)
				: (mc.thePlayer.ticksExisted % 2 == 0);
	}

	// safewalk
	@EventTarget
	public void onMove(EventMove e) {

		if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown())
			e.setMoveSpeed(0.218630206268501246d);
		else if (this.timer.a(1000.0) && this.ticksPlace > 1 && this.ticksGround > 10)
			e.setMoveSpeed(this.shouldBoost()
					? (0.224012013630206268501246d * ThreadLocalRandom.current().nextDouble(0.968, 0.9680000019073486))
					: (0.224012013630206268501246d * ThreadLocalRandom.current().nextDouble(0.98, 0.9800000190734863)));
		else
			e.setMoveSpeed(0.2d * ThreadLocalRandom.current().nextDouble(0.968, 0.968000019073486));

		if (DOWN.getValueState().booleanValue()) {
			if (mc.gameSettings.keyBindSneak.getIsKeyPressed() && !mc.gameSettings.keyBindJump.getIsKeyPressed()) {
				mc.thePlayer.motionX *= 0.7;
				mc.thePlayer.motionZ *= 0.7;
				return;
			}
		}
		if (this.Safewalk.getValueState()) {
			double x = e.getX();
			double y = e.getY();
			double z = e.getZ();
			if (mc.thePlayer.onGround) {
				double increment = 0.05;
				while (x != 0.0) {
					if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
							mc.thePlayer.getEntityBoundingBox().offset(x, -1.0, 0.0)).isEmpty()) {
						break;
					}
					if (x < increment && x >= -increment) {
						x = 0.0;
					} else if (x > 0.0) {
						x -= increment;
					} else {
						x += increment;
					}
				}
				while (z != 0.0) {
					if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
							mc.thePlayer.getEntityBoundingBox().offset(0.0, -1.0, z)).isEmpty()) {
						break;
					}
					if (z < increment && z >= -increment) {
						z = 0.0;
					} else if (z > 0.0) {
						z -= increment;
					} else {
						z += increment;
					}
				}
				while (x != 0.0 && z != 0.0 && mc.theWorld
						.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(x, -1.0, z))
						.isEmpty()) {
					if (x < increment && x >= -increment) {
						x = 0.0;
					} else if (x > 0.0) {
						x -= increment;
					} else {
						x += increment;
					}
					if (z < increment && z >= -increment) {
						z = 0.0;
					} else if (z > 0.0) {
						z -= increment;
					} else {
						z += increment;
					}
				}

				e.setX(x);
				e.setY(y);
				e.setZ(z);
			}
		}
	}

	private static class Rotation {
		float yaw;
		float pitch;

		public Rotation(final float yaw, final float pitch) {
			this.yaw = yaw;
			this.pitch = pitch;
		}

		public float getYaw() {
			return this.yaw;
		}

		public float getPitch() {
			return this.pitch;
		}

		public void toPlayer(final EntityPlayer player) {
			if (Float.isNaN(this.yaw) || Float.isNaN(this.pitch)) {
				return;
			}
			this.fixedSensitivity(Minecraft.getMinecraft().gameSettings.mouseSensitivity);
			player.rotationYaw = this.yaw;
			player.rotationPitch = this.pitch;
		}

		public void fixedSensitivity(final Float sensitivity) {
			final float f = sensitivity * 0.6f + 0.2f;
			final float gcd = f * f * f * 1.2f;
			this.yaw -= this.yaw % gcd;
			this.pitch -= this.pitch % gcd;
		}

		public void setYaw(final float f) {
			this.yaw = f;

		}

		public void setPitch(final float f) {
			this.pitch = f;

		}
	}

}
