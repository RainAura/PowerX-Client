package cn.Power.mod.mods.MOVEMENT;

import java.util.HashMap;
import java.util.Map.Entry;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.BlockUtils;
import cn.Power.util.MathUtils;
import cn.Power.util.misc.STimer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AutoMLG extends Mod {
	private double fallStartY = 0;
	private STimer timer = new STimer();
	private BlockData blockBelowData;
	private boolean nextPlaceWater = false;
	private boolean nextRemoveWater = false;

	public AutoMLG() {
		super("AutoMLG", Category.MOVEMENT);
	}

	@EventTarget
	public void onPre(EventPreMotion eu) {

		if (!mc.thePlayer.onGround && mc.thePlayer.motionY < 0) {

			if (fallStartY < mc.thePlayer.posY)
				fallStartY = mc.thePlayer.posY;

			if (fallStartY - mc.thePlayer.posY > 2) {

				// Get block based off of movement
				double x = mc.thePlayer.posX + mc.thePlayer.motionX * 1.25;
				double y = mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
				double z = mc.thePlayer.posZ + mc.thePlayer.motionZ * 1.25;

				// Checks if the block below is a valid block + timer delay
				BlockPos blockBelow = new BlockPos(x, y, z);
				IBlockState blockState = mc.theWorld.getBlockState(blockBelow);
				IBlockState underBlockState = mc.theWorld.getBlockState(blockBelow.down());

				if (underBlockState.getBlock().isBlockNormalCube() && !mc.thePlayer.isSneaking()
						&& (blockState.getBlock() == Blocks.air || blockState.getBlock() == Blocks.snow_layer
								|| blockState.getBlock() == Blocks.tallgrass)
						&& timer.delay(100)) {
					timer.reset();
					blockBelowData = getBlockData(blockBelow);
					if (blockBelowData != null) {
						nextPlaceWater = true;
						nextRemoveWater = false;

						float[] rotations = MathUtils.getRotationsBlock(blockBelowData.position, blockBelowData.face);
						eu.setYaw(rotations[0]);
						eu.setPitch(rotations[1]);
					}
				}

			}
		} else {
			fallStartY = mc.thePlayer.posY;
		}
		if (blockBelowData != null && (mc.thePlayer.isInWater())) {
			nextRemoveWater = true;
			float[] rotations = MathUtils.getRotationsBlock(blockBelowData.position, blockBelowData.face);
			eu.setYaw(rotations[0]);
			eu.setPitch(rotations[1]);
		} else {
			if (blockBelowData != null && nextPlaceWater)
				placeWater();
			else if (blockBelowData != null && nextRemoveWater)
				getWaterBack();
		}

	}

	/*
	 * Swaps to item slot and returns the previous one
	 */
	private int swapToItem(int item) {
		mc.rightClickDelayTimer = 2;
		int currentItem = mc.thePlayer.inventory.currentItem;

		mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(item - 36));
		mc.thePlayer.inventory.currentItem = item - 36;

		mc.playerController.updateController();
		return currentItem;
	}

	/*
	 * Places the water
	 */
	private void placeWater() {
		for (Entry<Integer, Item> item : getHotbarItems().entrySet()) {
			if (item.getValue().equals(Items.water_bucket)) {
				int currentItem = swapToItem(item.getKey());

				mc.playerController.netClientHandler
						.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));

				// Reset to current hand.
				mc.thePlayer.inventory.currentItem = currentItem;
				mc.playerController.updateController();
				break;
			}
		}
		nextPlaceWater = false;
	}

	/*
	 * Gets the water back
	 */
	private void getWaterBack() {
		for (Entry<Integer, Item> item : getHotbarItems().entrySet()) {
			if (item.getValue().equals(Items.bucket)) {
				int currentItem = swapToItem(item.getKey());

				mc.playerController.netClientHandler
						.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));

				// Reset to current hand.
				mc.thePlayer.inventory.currentItem = currentItem;
				mc.playerController.updateController();
				break;
			}
		}
		blockBelowData = null;
		nextRemoveWater = false;
	}

	/**
	 * Return's a map with the current hotbar items
	 * 
	 * @author Tomygames
	 */
	private HashMap<Integer, Item> getHotbarItems() {
		HashMap<Integer, Item> items = new HashMap<Integer, Item>();

		for (int i = 36; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				items.put(i, itemStack.getItem());
			}
		}

		return items;
	}

	private BlockData getBlockData(BlockPos pos) {
		if (!BlockUtils.getBlacklistedBlocks().contains(mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock())) {
			return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
		}
		return null;
	}

	class BlockData {
		public BlockPos position;
		public EnumFacing face;

		public BlockData(BlockPos position, EnumFacing face) {
			this.position = position;
			this.face = face;
		}
	}
}
