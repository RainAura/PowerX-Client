package cn.Power.mod.mods.MOVEMENT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventMove;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class Safewalk extends Mod {

	public Safewalk() {
		super("Safewalk", Category.MOVEMENT);
	}

	@EventTarget
	public void onMove(EventMove e) {
		double x = e.getX();
		double y = 3.0D;
		double z = e.getZ();

		double xx = e.getX();
		double zz = e.getZ();

//       System.err.println();
//
//		if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.01, -1, 0)).isEmpty() && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.01, -1, 0)).isEmpty()) {
//			System.err.println(x);
//			if (xx >= 0.22) {
//				x = 0.21;
//			}
//
//			if (xx <= -0.22) {
//				x = -0.21;
//			}
//			
//		}

		double increment = 0.05;

		while (x != 0.0) {
			if (check5lock(x, -y, 0.0)) {
				break;
			}

			if (x < increment && x >= -increment) {
				x = 0.0;
			} else if (x > 0.0) {
				x -= increment;
			} else {
				x += increment;
			}
		}

		while (z != 0.0) {
			if (check5lock(0.0, -y, z)) {
				break;
			}

			if (z >= 0.25) {
				z = 0.25;
			}

			if (z <= -0.25) {
				z = -0.25;
			}

			if (z < increment && z >= -increment) {
				z = 0.0;
			} else if (z > 0.0) {
				z -= increment;
			} else {
				z += increment;
			}
		}

		while (x != 0.0 && z != 0.0 && !check5lock(x, -1.0, z)) {
			if (x < increment && x >= -increment) {
				x = 0.0;
			} else if (x > 0.0) {
				x -= increment;
			} else {
				x += increment;
			}
			if (z < increment && z >= -increment) {
				z = 0.0;
			} else if (z > 0.0) {
				z -= increment;
			} else {
				z += increment;
			}
		}

		System.err.println(mc.thePlayer.isMoving());
		e.setX(x);
//			e.setY(y);
		e.setZ(z);
	}

	public boolean check5lock(double x, double y, double z) {
		int i = 3;
		while (i > 0) {
			if (!mc.theWorld
					.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(x, -i, z))
					.isEmpty()) {
				return true;
			}
			--i;
		}
		return false;
	}

}
