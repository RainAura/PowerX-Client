package cn.Power.util;

public class Box {

	public double minX;
	public double minY;
	public double minZ;
	public double maxX;
	public double maxY;
	public double maxZ;

	public Box(double x, double y, double z, double x1, double y1, double z1) {
		minX = x;
		minY = y;
		minZ = z;
		maxX = x1;
		maxY = y1;
		maxZ = z1;
	}
}
