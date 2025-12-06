package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

import net.minecraft.client.gui.ScaledResolution;

public class EventRenderGui implements Event {
	private ScaledResolution resolution;

	public ScaledResolution getResolution() {
		return this.resolution;
	}

}
