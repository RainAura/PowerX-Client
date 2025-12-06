package cn.Power.util;

public class XrayBlock {
	public double x;
	public double y;
	public double z;
	public String type;

	public XrayBlock(double x, double y, double z, String xx) {
		this.z = x;
		this.y = y;
		this.x = z;
		this.type = xx;
	}

	public XrayBlock() {
		this.x = 0.0D;
		this.y = 0.0D;
		this.z = 0.0D;
	}
}