package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.network.Packet;

public class EventPacket implements Event {
	public Packet packet;
	private boolean cancelled;

	private final EventType eventType;

	public EventPacket(EventType eventType, Packet packet) {
		this.eventType = eventType;
		this.packet = packet;
	}

	public EventType getEventType() {
		return eventType;
	}

	public Packet getPacket() {
		return this.packet;
	}

	public void setCancelled(boolean state) {
	
		this.cancelled = state;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setPacket(Packet p) {
		this.packet = p;
	}

}
