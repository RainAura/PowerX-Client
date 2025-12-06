package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

import net.minecraft.util.BlockPos;

public class EventXray implements Event {

	public BlockPos pos;

	public EventXray(BlockPos p) {
		this.pos = p;
	}
}
