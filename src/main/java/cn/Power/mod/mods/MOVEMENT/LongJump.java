package cn.Power.mod.mods.MOVEMENT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventMove;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.MathUtils;
import net.minecraft.network.Packet;
import net.minecraft.potion.Potion;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.LinkedList;

public class LongJump extends Mod {
	public int stage, groundTicks;
	public double lastDistance;
	public double movementSpeed;
	private LinkedList<Packet> packets = new LinkedList<>();
	public Value<Boolean> togg = new Value("LongJump_AutoToggle", true);

	public LongJump() {
		super("LongJump", Category.MOVEMENT);
	}

	@EventTarget
	public void onUpdate(EventUpdate event) {
		if (mc.thePlayer == null)
			return;
		double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
		double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
		lastDistance = Math.sqrt(xDist * xDist + zDist * zDist);

		if (mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
			if (stage < 2) {
				mc.thePlayer.posY += 1.8995E-35D;
			}
			if (stage > 2 && mc.thePlayer.isMoving()) {
				if(togg.getValueState())
					this.toggle();
				else {
					lastDistance = movementSpeed = 0.0D;
					stage = groundTicks = 0;
				}
			}
		}
	}

	@EventTarget
	public void onUpdate(EventMove event) {
		if (stage == 1) {
			movementSpeed = 0;
		} else if (stage == 2) {
			event.setY(mc.thePlayer.motionY = event.getMotionY(event.getLegitMotion()));
			movementSpeed = 1.9 * event.getMovementSpeed();
		} else if (stage == 3) {
			movementSpeed = 2.14999 * event.getMovementSpeed();
		} else if (stage == 4) {
			movementSpeed *= 1.22;
		} else {
			if (stage < 15) {
				if (mc.thePlayer.motionY < 0) {
					event.setY(mc.thePlayer.motionY *= .7225f);
				}
				movementSpeed = lastDistance - lastDistance / 159;
			} else {
				movementSpeed *= .75;
			}
		}
		event.setMoveSpeed(Math.max(movementSpeed, event.getMovementSpeed()));
		stage++;
	}

	private double getBaseMoveSpeed() {
		double n = 0.2873;
		if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
			n *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
		}
		return n;
	}

	@Override
	public void onEnable() {
		lastDistance = movementSpeed = 0.0D;
		stage = groundTicks = 0;
	}

}