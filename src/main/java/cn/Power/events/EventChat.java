
package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

import net.minecraft.network.Packet;

public class EventChat implements Event {
	public Packet packet;
	public String message;
	public boolean cancelled;

	public EventChat(Packet p) {
		this.packet = p;
	}

	public EventChat(String chat) {
		message = chat;
	}

	public String getMessage() {
		return message;
	}

	public void setCancelled(boolean b) {
		this.cancelled = b;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public Packet getPacket() {
		return this.packet;
	}

}
