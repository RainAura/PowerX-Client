package cn.Power.util.waypoints;

import net.minecraft.util.Vec3;

public class Waypoint {

	private String name;
	private Vec3 vec3;

	private int color;

	public Waypoint(String name, Vec3 vec3, int color) {
		this.name = name;
		this.vec3 = vec3;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public Vec3 getVec3() {
		return vec3;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVec3(Vec3 vec3) {
		this.vec3 = vec3;
	}

	public void setVec3(double x, double y, double z) {
		this.vec3 = new Vec3(x, y, z);
	}

	public int getColor() {
		return color;
	}

}
