package cn.Power.events;

import com.darkmagician6.eventapi.events.Event;

import net.minecraft.network.Packet;

public class EventPreMotion implements Event {
	public double x, y, z;
	public float yaw;
	public float pitch;
	public boolean onGround;
	public boolean cancel;
	public static float YAW, PITCH, PREVYAW, PREVPITCH;
	public static boolean SNEAKING;

	public EventPreMotion(double x, double y, double z, float yaw, float pitch, boolean onGround) {
		this.y = y;
		this.x = x;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
	}

	public void fire() {
		PREVYAW = YAW;
		PREVPITCH = PITCH;
		YAW = this.yaw;
		PITCH = this.pitch;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public boolean isCancelled() {
		return this.cancel;
	}

	public void setCancelled(boolean state) {
		this.cancel = state;
	}

	public void setRotations(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}


}
