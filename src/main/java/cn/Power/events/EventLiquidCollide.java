package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class EventLiquidCollide implements Event {
	protected boolean cancelled;
	private Block block;
	private BlockPos pos;
	private AxisAlignedBB bounds;

	public void fire(Block block, BlockPos pos, AxisAlignedBB bounds) {
		this.block = block;
		this.pos = pos;
		this.bounds = bounds;
	}

	public AxisAlignedBB getBounds() {
		return bounds;
	}

	public void setBounds(AxisAlignedBB bounds) {
		this.bounds = bounds;
	}

	public BlockPos getPos() {
		return pos;
	}

	public Block getBlock() {
		return block;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
