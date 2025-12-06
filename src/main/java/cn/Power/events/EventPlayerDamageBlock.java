package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class EventPlayerDamageBlock implements Event {
	private BlockPos pos;
	private EnumFacing face;
	private boolean cancelled;

	public EventPlayerDamageBlock(BlockPos pos, EnumFacing face) {
		this.pos = pos;
		this.face = face;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}

	public EnumFacing getFace() {
		return this.face;
	}

	public void setFace(EnumFacing face) {
		this.face = face;
	}

	public void setCancelled(boolean state) {
		this.cancelled = state;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

}
