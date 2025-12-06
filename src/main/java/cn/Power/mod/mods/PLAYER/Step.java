package cn.Power.mod.mods.PLAYER;

import java.util.Arrays;
import java.util.List;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventStep;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.PlayerUtil;
import cn.Power.util.misc.STimer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class Step extends Mod {
	public static Value<String> mode;
	private Value<Double> STEP = new Value<Double>("Step_Step", 2.0, 0.5, 10.0, 0.5);
	private Value<Double> TIMER = new Value<Double>("Step_Timer", 0.37, 0.2, 1.0, 0.01);
	private Value<Double> DELAY = new Value<Double>("Step_Delay", 0.5, 0.0, 2.0, 0.1);
	boolean resetTimer;
	public boolean noJumping;
	STimer time = new STimer();
	public static STimer lastStep = new STimer();

	public Step() {
		super("Step", Category.PLAYER);
		mode = new Value("Step", "Mode", 0);
		mode.mode.add("NCP");
		mode.mode.add("Vanilla");
		mode.mode.add("AAC");
		mode.mode.add("Cubecraft");
	}

	public boolean getNoJumping() {
		return this.noJumping;
	}

	@Override
	public void onEnable() {
		resetTimer = false;

	}

	@Override
	public void onDisable() {
		noJumping = false;
		mc.timer.timerSpeed = 1;
		mc.thePlayer.stepHeight = 0.5f;
	}

	@EventTarget
	public void onUpdate(EventUpdate event) {
		this.setDisplayName(this.mode.getModeAt(this.mode.getCurrentMode()));
		if (resetTimer) {
			resetTimer = !resetTimer;
			noJumping = false;
			mc.timer.timerSpeed = 1;
		}
	}

	@EventTarget
	public void onEvent(EventStep es) {
		double x = mc.thePlayer.posX;
		double y = mc.thePlayer.posY;
		double z = mc.thePlayer.posZ;
		double stepValue = 1.5D;
		final float timer = TIMER.getValueState().floatValue();
		final float delay = DELAY.getValueState().floatValue() * 1000;
		stepValue = STEP.getValueState().doubleValue();
		if (!PlayerUtil.isInLiquid())
			if (es.isPre()) {
				if (mc.thePlayer.isCollidedVertically && !mc.gameSettings.keyBindJump.isPressed()
						&& time.delay(delay)) {
					es.setStepHeight(stepValue);
					es.setActive(true);
				}

			} else {
				double rheight = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
				boolean canStep = rheight >= 0.625;
				if (canStep) {
					lastStep.reset();
					time.reset();
				}

				if (mode.isCurrentMode("NCP")) {
					if (canStep) {
						this.noJumping = true;
						mc.timer.timerSpeed = timer
								- (rheight >= 1 ? Math.abs(1 - (float) rheight) * ((float) timer * 0.55f) : 0);
						if (mc.timer.timerSpeed <= 0.05f) {
							mc.timer.timerSpeed = 0.05f;
						}
						resetTimer = true;
						doNCPStep(rheight);
					}
				}
				if (mode.isCurrentMode("Cubecraft")) {
					if (canStep) {
						cubeStep(rheight);
						resetTimer = true;
						mc.timer.timerSpeed = rheight < 2 ? 0.6f : 0.3f;
					}
				}
				if (mode.isCurrentMode("AAC")) {
					if (canStep) {
						if (rheight < 1.1) {
							mc.timer.timerSpeed = 0.5F;
							resetTimer = true;
						} else {
							mc.timer.timerSpeed = 1 - (float) rheight * 0.57f;
							resetTimer = true;
						}
						aacStep(rheight);
					}
				}
				if (mode.isCurrentMode("Vanilla")) {

				}

			}
	}

	void cubeStep(double height) {
		double posX = mc.thePlayer.posX;
		double posZ = mc.thePlayer.posZ;
		double y = mc.thePlayer.posY;
		double first = 0.42;
		double second = 0.75;
		mc.thePlayer.sendQueue
				.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + first, posZ, false));
	}

	
	private void doNCPStep(double height) {

        final double posX = mc.thePlayer.posX, posY = mc.thePlayer.posY, posZ = mc.thePlayer.posZ;

        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        mc.thePlayer.setSprinting(false);

        if (height <= 1) {

            final float[] values = {
                    .42F,
                    .75F
            };

            if (height != 1) {
                values[0] *= height;
                values[1] *= height;

                if (values[0] > .425) values[0] = .425F;
                if (values[1] > .78) values[1] = .78F;
                if (values[1] < .49) values[1] = .49F;
            }

            if (values[0] == .42) values[0] = .41999998688698F;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + values[0], posZ, false));

            if (posY + values[1] < posY + height)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + values[1], posZ, false));
        } else if (height <= 1.5) {

            final float[] values = {
                    .41999998688698F,
                    .7531999805212F,
                    1.00133597911215F,
                    1.06083597911215F,
                    0.9824359775862711F
            };

            for (double val : values)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + val, posZ, false));
        }

        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        mc.thePlayer.stepHeight = 0.625F;
    }
	
	void ncpStep(double height) {
		List<Double> offset = Arrays.asList(0.42, 0.333, 0.248, 0.083, -0.078);
		double posX = mc.thePlayer.posX;
		double posZ = mc.thePlayer.posZ;
		double y = mc.thePlayer.posY;
		if (height < 1.1) {
			double first = 0.41999998688698D;
			double second = 0.75;
			if (height != 1) {
				first *= height;
				second *= height;
				if (first > 0.425) {
					first = 0.425;
				}
				if (second > 0.78) {
					second = 0.78;
				}
				if (second < 0.49) {
					second = 0.49;
				}
			}
			if (first == 0.42)
				first = 0.41999998688698;
			mc.thePlayer.sendQueue
					.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + first, posZ, false));
			if (y + second < y + height)
				mc.thePlayer.sendQueue
						.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + second, posZ, false));
			return;
		} else if (height < 1.6) {
			for (int i = 0; i < offset.size(); i++) {
				double off = offset.get(i);
				y += off;
				mc.thePlayer.sendQueue
						.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y, posZ, false));
			}
		} else if (height < 2.1) {
			double[] heights = { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869 };
			for (double off : heights) {
				mc.thePlayer.sendQueue
						.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
			}
		} else {
			double[] heights = { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 };
			for (double off : heights) {
				mc.thePlayer.sendQueue
						.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
			}
		}

	}

	void aacStep(double height) {
		double posX = mc.thePlayer.posX;
		double posY = mc.thePlayer.posY;
		double posZ = mc.thePlayer.posZ;
		if (height < 1.1) {
			double first = 0.42;
			double second = 0.75;

			if (height > 1) {
				first *= height;
				second *= height;
				if (first > 0.4349) {
					first = 0.4349;
				} else if (first < 0.405) {
					first = 0.405;
				}
			}
			mc.thePlayer.sendQueue
					.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + first, posZ, false));
			if (posY + second < posY + height)
				mc.thePlayer.sendQueue
						.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + second, posZ, false));
			return;
		}
		List<Double> offset = Arrays.asList(0.434999999999998, 0.360899999999992, 0.290241999999991, 0.220997159999987,
				0.13786084000003104, 0.055);
		double y = mc.thePlayer.posY;
		for (int i = 0; i < offset.size(); i++) {
			double off = offset.get(i);
			y += off;
			if (y > mc.thePlayer.posY + height) {
				double x = mc.thePlayer.posX;
				double z = mc.thePlayer.posZ;
				double forward = mc.thePlayer.movementInput.moveForward;
				double strafe = mc.thePlayer.movementInput.moveStrafe;
				float YAW = mc.thePlayer.rotationYaw;
				double speed = 0.3;
				if (forward != 0 && strafe != 0)
					speed -= 0.09;
				x += (forward * speed * Math.cos(Math.toRadians(YAW + 90.0f))
						+ strafe * speed * Math.sin(Math.toRadians(YAW + 90.0f))) * 1;
				z += (forward * speed * Math.sin(Math.toRadians(YAW + 90.0f))
						- strafe * speed * Math.cos(Math.toRadians(YAW + 90.0f))) * 1;
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));

				break;

			}
			if (i == offset.size() - 1) {
				double x = mc.thePlayer.posX;
				double z = mc.thePlayer.posZ;
				double forward = mc.thePlayer.movementInput.moveForward;
				double strafe = mc.thePlayer.movementInput.moveStrafe;
				float YAW = mc.thePlayer.rotationYaw;
				double speed = 0.3;
				if (forward != 0 && strafe != 0)
					speed -= 0.09;
				x += (forward * speed * Math.cos(Math.toRadians(YAW + 90.0f))
						+ strafe * speed * Math.sin(Math.toRadians(YAW + 90.0f))) * 1;
				z += (forward * speed * Math.sin(Math.toRadians(YAW + 90.0f))
						- strafe * speed * Math.cos(Math.toRadians(YAW + 90.0f))) * 1;
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
			} else {
				mc.thePlayer.sendQueue
						.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y, posZ, false));
			}
		}
	}
}
