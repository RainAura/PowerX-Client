package cn.Power.events;

import java.util.Random;

import com.darkmagician6.eventapi.events.Event;

import cn.Power.mod.mods.MOVEMENT.Speed;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

public class EventMove implements Event {
	public double x;
	public double y;
	public double z;
	public double movespeed = 0.2873D;

	public EventMove(double a, double b, double c) {
		this.x = a;
		this.y = b;
		this.z = c;
	}

	public double getX() {
		return x;
	}
 
	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
    public double getMotionY(double mY) {
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
			mY += (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;
		}
		return mY;
    }
    
    public double getLegitMotion() {
    	return 0.41999998688697815D;
    }
    
    public void setBaseSpeed(double speed) {
    	movespeed = speed;
    }

	public double getMovementSpeed() {
		double baseSpeed = (Minecraft.thePlayer.isInWater()) ? 0.201573D : (movespeed);
		Minecraft.getMinecraft();
		if (Minecraft.thePlayer.isPotionActive(Potion.moveSpeed)) {
			Minecraft.getMinecraft();
			int amplifier = Minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
		}
		return baseSpeed;
	}

	public double getMovementSpeed(double baseSpeed) {
		double speed = baseSpeed;
		Minecraft.getMinecraft();
		if (Minecraft.thePlayer.isPotionActive(Potion.moveSpeed)) {
			Minecraft.getMinecraft();
			int amplifier = Minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			return speed *= 1.0 + 0.2 * (amplifier + 1);
		}
		return speed;
	}

	public void setMoveSpeed(double moveSpeed) {

		
		double moveForward = MovementInput.moveForward;
		
		double moveStrafe = MovementInput.moveStrafe;
		double yaw = Minecraft.thePlayer.rotationYaw;
		if (moveForward == 0.0D && moveStrafe == 0.0D) {
			setX(0.0D);
			setZ(0.0D);
		} else {
			if (moveStrafe > 0) {
				moveStrafe = 1;
			} else if (moveStrafe < 0) {
				moveStrafe = -1;
			}
			if (moveForward != 0.0D) {
				if (moveStrafe > 0.0D) {
					yaw += (moveForward > 0.0D ? -45 : 45);
				} else if (moveStrafe < 0.0D) {
					yaw += (moveForward > 0.0D ? 45 : -45);
				}
				moveStrafe = 0.0D;
				if (moveForward > 0.0D) {
					moveForward = 1.0D;
				} else if (moveForward < 0.0D) {
					moveForward = -1.0D;
				}
			}
			
			yaw += randomDouble(-2.5, 2.5);
			
			setX(moveForward * moveSpeed * Math.cos(Math.toRadians(yaw + 88.0))
					+ moveStrafe * moveSpeed * Math.sin(Math.toRadians(yaw + 87.9000015258789)));
			setZ(moveForward * moveSpeed * Math.sin(Math.toRadians(yaw + 88.0))
					- moveStrafe * moveSpeed * Math.cos(Math.toRadians(yaw + 87.9000015258789)));
		}
	}
	
    
    public static double randomDouble(final double n, final double n2) {
        final Random random = new Random();
        
        double n3 = random.nextDouble() * (n2 - n);
        if (n3 > n2) {
            n3 = n2;
        }
        double n4 = n3 + n;
        if (n4 > n2) {
            n4 = n2;
        }
        return n4;
    }

	public double getJumpBoostModifier(double baseJumpHeight) {
		if (Minecraft.thePlayer.isPotionActive(Potion.jump)) {
			Minecraft.getMinecraft();
			int amplifier = Minecraft.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
			baseJumpHeight += (float) (amplifier + 1) * 0.1F;
		}

		return baseJumpHeight;
	}

}
