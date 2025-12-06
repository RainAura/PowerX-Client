package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

import net.minecraft.network.Packet;

public class EventPostMotion implements Event {

	private Packet packet;
	public float yaw;
	public float pitch;

	public EventPostMotion(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public float getYaw() {
		return this.yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void setRotations(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public Packet getPacket() {
		return this.packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

}
