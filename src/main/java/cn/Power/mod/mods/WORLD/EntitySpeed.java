package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventMove;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.PLAYER.Freecam;
import cn.Power.util.misc.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.MovementInput;

public class EntitySpeed extends Mod {
	Entity entity;

	public static Value speed = new Value("EntitySpeed_Speed", 1.0D, 0.0D, 5.0D, 0.1D);
	public static Value motion = new Value("EntitySpeed_MotionY", 3.0D, 0.1D, 5.0D, 0.1D);

	public EntitySpeed() {
		super("EntitySpeed", Category.WORLD);
	}

	@EventTarget
	public void onUpdate(EventMove event) {
		
		
		mc.thePlayer.ridingEntity.setInvisible(false);
		
		mc.thePlayer.horseJumpPowerCounter = 9;
		mc.thePlayer.horseJumpPower = 1f;
		
		if (!mc.thePlayer.isRiding()) {
			if (mc.thePlayer.ridingEntity == null) {
				return;
			}
		}

		
		if(Dismount.entity != null || mc.getNetHandler().getNetworkManager().user == null)
		{
			
		}else {
		
		
//		PacketWrapper exloit = new PacketWrapper(0x10, null, mc.getNetHandler().getNetworkManager().user);
//		exloit.write(Type.DOUBLE, mc.thePlayer.ridingEntity.posX );
//		exloit.write(Type.DOUBLE, mc.thePlayer.ridingEntity.posY);
//		exloit.write(Type.DOUBLE, mc.thePlayer.ridingEntity.posZ);
//		exloit.write(Type.FLOAT, (float)1.0);
//		exloit.write(Type.FLOAT, mc.thePlayer.ridingEntity instanceof EntityBoat ? 0.000000f : mc.thePlayer.rotationPitch / 2);
//		try {
//			exloit.sendToServer(Protocol1_8TO1_9.class, true, false);
//		} catch (Throwable e) {
//		}
		

		
		}
		
//		if(mc.thePlayer.ridingEntity instanceof EntityBoat) {
//			((EntityBoat)mc.thePlayer.ridingEntity).MountedYOffset = -0.00001;
//			
//			PacketWrapper exloit = new PacketWrapper(0x16, null, mc.getNetHandler().getNetworkManager().user);
//			exloit.write(Type.FLOAT, (float)1.0);
//			exloit.write(Type.FLOAT, (float)1.0);
//			exloit.write(Type.UNSIGNED_BYTE, (short)0);
//
//			try {
//				exloit.sendToServer(Protocol1_8TO1_9.class, true, false);
//			} catch (Throwable e) {
//			}
//		}

		double speed = (Double) EntitySpeed.speed.getValueState();
		MovementInput movementInput = mc.thePlayer.movementInput;
		double forward = (double) movementInput.moveForward;
		double strafe = (double) movementInput.moveStrafe;
		

		float yaw = mc.thePlayer.rotationYaw;
		if (forward == 0.0D && strafe == 0.0D) {
			mc.thePlayer.ridingEntity.motionX = 0.0D;
			mc.thePlayer.ridingEntity.motionZ = 0.0D;
		} else {
			if (forward != 0.0D) {
				if (strafe > 0.0D) {
					yaw += (float) (forward > 0.0D ? -45 : 45);
				} else if (strafe < 0.0D) {
					yaw += (float) (forward > 0.0D ? 45 : -45);
				}
				strafe = 0.0D;
				if (forward > 0.0D) {
					forward = 1.0D;
				} else if (forward < 0.0D) {
					forward = -1.0D;
				}
			}
			mc.thePlayer.ridingEntity.motionX = forward * speed * Math.cos(Math.toRadians((double) (yaw + 90.0F)))
					+ strafe * speed * Math.sin(Math.toRadians((double) (yaw + 90.0F)));
			mc.thePlayer.ridingEntity.motionZ = forward * speed * Math.sin(Math.toRadians((double) (yaw + 90.0F)))
					- strafe * speed * Math.cos(Math.toRadians((double) (yaw + 90.0F)));
		}

		double Y = (Double) motion.getValueState();
		
		if(mc.thePlayer.ridingEntity instanceof EntityLivingBase) {
			((EntityLivingBase)mc.thePlayer.ridingEntity).moveForward = (float) forward;
			((EntityLivingBase)mc.thePlayer.ridingEntity).moveStrafing = (float) strafe;
			((EntityLivingBase)mc.thePlayer.ridingEntity).gravity = false;
		}
		
		mc.thePlayer.ridingEntity.motionY = 0.0D;
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.thePlayer.ridingEntity.motionY = Y;
		}

		if (mc.gameSettings.keyBindSprint.isKeyDown()) {
			mc.thePlayer.ridingEntity.motionY = -Y;
		}

	}

	@Override
	public void onEnable() {
		if(mc.thePlayer.ridingEntity != null)
			ChatUtil.printChat(""+mc.thePlayer.ridingEntity.posY);
		super.onEnable();
	}

}
