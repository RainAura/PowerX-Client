package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

import net.minecraft.block.Block;

public class EventRenderBlock implements Event {
	public int x;
	public int y;
	public int z;
	private Block block;

	public EventRenderBlock(int x, int y, int z, Block block) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.block = block;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public Block getBlock() {
		return this.block;
	}
}