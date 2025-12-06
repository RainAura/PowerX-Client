package cn.Power.mod.mods.PLAYER;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.Value;
import cn.Power.events.EventBlockBB;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventPushBlock;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.Helper;
import cn.Power.util.MathUtils;
import cn.Power.util.PlayerUtil;
import cn.Power.util.misc.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class Phase extends Mod {
	public static Value<String> mode = new Value("Phase", "Mode", 0);
	
	public Value<Double> Mcf_Distance = new Value<Double>("Phase_HcfDistance", 2.0, 0.1, 3.0, 0.1);
	
	double rot1, rot2;
	private int delay;
	boolean shouldSpeed = false;
	float yaw, pitch;
	Timer timer = new Timer();
	private String currentPhase;

	public Phase() {
		super("Phase", Category.PLAYER);
		mode.mode.add("Normal");
		mode.mode.add("Spider");
		mode.mode.add("Skip");
		mode.mode.add("FullBlock");
		mode.mode.add("Silent");
		mode.mode.add("NCP");
		mode.mode.add("HCF");

	}

	@Override
	public void onDisable() {
		Helper.Phase = false;
		mc.timer.timerSpeed = 1;
	}

	@Override
	public void onEnable() {
		Helper.Phase = true;
		if (mc.theWorld == null)
			return;
		shouldSpeed = isInsideBlock();
		if ((MathUtils.isCollidedH(0.001) || mc.thePlayer.isCollidedHorizontally)) {
			mc.thePlayer.onGround = false;
			mc.thePlayer.noClip = true;
			mc.thePlayer.motionX *= 0;
			mc.thePlayer.motionZ *= 0;
			mc.thePlayer.jumpMovementFactor = 0;
			teleport(0.006000000238415);
			rot1 = 0;
			rot2 = 0;
		}
	}

	@EventTarget
	public void onPuch(EventPushBlock event) {
		if (mode.isCurrentMode("FullBlock")) {
			return;
		}
		event.setCancelled(true);
	}

	@EventTarget
	public void onAbb(EventBlockBB ebb) {
		if (mode.isCurrentMode("HCF")) {
		 if ((ebb.getBoundingBox() != null) && (isInsideBlock() || (ebb.getBoundingBox().maxY > mc.thePlayer.boundingBox.minY)) && (mc.thePlayer.isSneaking())) {
             ebb.setCancelled(true);
         }
		 
		 return;
		}
		
		if ((ebb.getBoundingBox() != null) && (ebb.getBoundingBox().maxY > mc.thePlayer.boundingBox.minY)) {
			ebb.setCancelled(true);
		}

	}

	@EventTarget
	public void onPre(EventPreMotion em) {

		
        if (mode.isCurrentMode("HCF") && isInsideBlock() && mc.thePlayer.isSneaking()) {
            final float yaw = mc.thePlayer.rotationYaw;
            float sped = Mcf_Distance.getValueState().floatValue();
            mc.thePlayer.boundingBox.offset(sped * Math.cos(Math.toRadians(yaw + 90.0f)), 0.0, sped * Math.sin(Math.toRadians(yaw + 90.0f)));
        
            return;
        }
        
		this.mc.thePlayer.noClip = true;
		
		if (mode.isCurrentMode("Normal")) {
			currentPhase = "Normal";
		}
		if (mode.isCurrentMode("Spider")) {
			currentPhase = "Spider";
		}
		if (mode.isCurrentMode("Skip")) {
			currentPhase = "Skip";
		}
		if (mode.isCurrentMode("FullBlock")) {
			currentPhase = "FullBlock";
		}
		if (mode.isCurrentMode("Silent")) {
			currentPhase = "Silent";
		}
		if (mode.isCurrentMode("NCP")) {
			currentPhase = "NCP";
		}
		if (!shouldSpeed && mode.isCurrentMode("NCP")) {
			if (isInsideBlock()) {
				mc.thePlayer.rotationYaw = yaw;
				mc.thePlayer.rotationPitch = pitch;
			} else {
				yaw = mc.thePlayer.rotationYaw;
				pitch = mc.thePlayer.rotationPitch;
			}
		}

		if (mode.isCurrentMode("NCP")) {
			if (shouldSpeed || isInsideBlock()) {
				if (!mc.thePlayer.isSneaking())
					mc.thePlayer.lastReportedPosY = 0;
				mc.thePlayer.lastReportedPitch = 999;
				mc.thePlayer.onGround = false;
				mc.thePlayer.noClip = true;
				mc.thePlayer.motionX = 0;
				mc.thePlayer.motionZ = 0;
				if (mc.gameSettings.keyBindJump.getIsKeyPressed() && mc.thePlayer.posY == (int) mc.thePlayer.posY)
					mc.thePlayer.jump();

				mc.thePlayer.jumpMovementFactor = 0;
			}
			rot1++;
			if (rot1 < 3) {
				if (rot1 == 1) {
					pitch += 15;
				} else {
					pitch -= 15;
				}
			}
			if (mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
				mc.thePlayer.lastReportedPitch = 999;
				double X = mc.thePlayer.posX;
				double Y = mc.thePlayer.posY;
				double Z = mc.thePlayer.posZ;
				if (!PlayerUtil.isMoving2())
					if (MathUtils.isOnGround(0.001) && !isInsideBlock()) {
						mc.thePlayer.lastReportedPosY = -99;
						em.setY(Y - 1);
						mc.thePlayer.setPosition(X, Y - 1, Z);
						timer.reset();
						mc.thePlayer.motionY = 0;
					} else if (timer.check(100) && mc.thePlayer.posY == (int) mc.thePlayer.posY) {
						mc.thePlayer.setPosition(X, Y - 0.3, Z);
					}

			}
			if (isInsideBlock() && rot1 >= 3) {
				if (shouldSpeed) {
					teleport(0.617);

					float sin = (float) Math.sin(rot2) * 0.1f;
					float cos = (float) Math.cos(rot2) * 0.1f;
					mc.thePlayer.rotationYaw += sin;
					mc.thePlayer.rotationPitch += cos;
					rot2++;
				} else {
					teleport(0.031);
				}
			}
		}

		double multiplier = 0.3;
		final double mx = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
		final double mz = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
		if (mode.isCurrentMode("FullBlock")) {
			multiplier = 0.4;
		}
		final double x = mc.thePlayer.movementInput.moveForward * multiplier * mx
				+ mc.thePlayer.movementInput.moveStrafe * multiplier * mz;
		final double z = mc.thePlayer.movementInput.moveForward * multiplier * mz
				- mc.thePlayer.movementInput.moveStrafe * multiplier * mx;
		switch (currentPhase) {
		case "FullBlock": {
//                      if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !isInsideBlock()) {
//                          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
//                          for (int i = 1; i < 11; ++i) {
//                              mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, Double.MAX_VALUE * i, mc.thePlayer.posZ, false));
//                            
//                          }
//                          final double posX = mc.thePlayer.posX;
//                          final double posY = mc.thePlayer.posY;
//                          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY - (PlayerUtil.isOnLiquid() ? 9000.0 : 0.1), mc.thePlayer.posZ, false));
//                          mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
//                          break;
//                      }else if(isInsideBlock()){
//                      	mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
//                      }
			break;
		}
		case "Normal": {
			if (mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
				double X = mc.thePlayer.posX;
				double Y = mc.thePlayer.posY;
				double Z = mc.thePlayer.posZ;
				if (mc.thePlayer.posY == (int) mc.thePlayer.posY) {
					mc.thePlayer.setPosition(X, Y - 0.3, Z);
				}
			}
			if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !isInsideBlock()) {
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x,
						mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
				final double posX2 = mc.thePlayer.posX;
				final double posY2 = mc.thePlayer.posY;
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX2,
						posY2 - (PlayerUtil.isOnLiquid() ? 9000.0 : 0.09), mc.thePlayer.posZ, false));
				mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
				break;
			}
			break;
		}
		case "Silent": {
			if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !isInsideBlock()) {
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x,
						mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
				for (int i = 1; i < 10; ++i) {
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
							8.988465674311579E307, mc.thePlayer.posZ, false));
				}
				mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
				break;
			}
			break;
		}
		case "Skip": {
			if (!mc.thePlayer.isCollidedHorizontally) {
				break;
			}
			mc.thePlayer.motionX *= 0.5;
			mc.thePlayer.motionZ *= 0.5;
			final double[] OPOP = { -0.02500000037252903, -0.028571428997176036, -0.033333333830038704,
					-0.04000000059604645, -0.05000000074505806, -0.06666666766007741, -0.10000000149011612, 0.0,
					-0.20000000298023224, -0.04000000059604645, -0.033333333830038704, -0.028571428997176036,
					-0.02500000037252903 };
			for (int j = 0; j < OPOP.length; ++j) {
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + OPOP[j], mc.thePlayer.posZ, false));
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
						mc.thePlayer.posX + x * j, mc.thePlayer.boundingBox.minY, mc.thePlayer.posZ + z * j, false));
			}
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.boundingBox.minY, mc.thePlayer.posZ, false));
			break;
		}
		case "Spider": {
			if (!isInsideBlock()) {
				break;
			}
			mc.thePlayer.posY += 0.1;
			mc.thePlayer.motionY = 0.065;
			mc.thePlayer.resetHeight();
			break;
		}
		}
	}

	@EventTarget
	public void onPack(EventPacket ep) {
		if (mode.isCurrentMode("FullBlock")) {
			return;
		}
		
		if (mode.isCurrentMode("HCF")) {
			return;
		}
		
		Packet p = ep.getPacket();
		if (p instanceof C03PacketPlayer) {
			C03PacketPlayer packet = (C03PacketPlayer) p;
			double y = packet.getPositionY();
			double x = packet.getPositionX();
			double z = packet.getPositionZ();
			String ground = packet.isOnGround() ? "\247a" : "\247c";

			if (y != 0) {
				// ChatUtil.printChat(packet.getClass().getSimpleName() + ground + " z : " + z);
			}

		}
		if (p instanceof S08PacketPlayerPosLook) {
			S08PacketPlayerPosLook pac = (S08PacketPlayerPosLook) ep.getPacket();

			shouldSpeed = true;
			if (!shouldSpeed)
				rot2 = 0;
		}
		if (isInsideBlock()) {
			return;
		}
		final double multiplier = 0.2;
		final double mx = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
		final double mz = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
		final double x = mc.thePlayer.movementInput.moveForward * multiplier * mx
				+ mc.thePlayer.movementInput.moveStrafe * multiplier * mz;
		final double z = mc.thePlayer.movementInput.moveForward * multiplier * mz
				- mc.thePlayer.movementInput.moveStrafe * multiplier * mx;
		if (mc.thePlayer.isCollidedHorizontally && ep.getPacket() instanceof C03PacketPlayer) {
			delay++;
			final C03PacketPlayer player = (C03PacketPlayer) ep.getPacket();
			if (this.delay >= 5) {
				player.x += x;
				player.z += z;
				--player.y;
				this.delay = 0;
			}
		}
	}

	@EventTarget
	public void onPackets(EventPacket e) {
			Packet packet = e.getPacket();
			 if (packet instanceof S08PacketPlayerPosLook) {
					S08PacketPlayerPosLook s08packet = (S08PacketPlayerPosLook) packet;

			}
	}

	@EventTarget
	public void onPacket(EventPacket e) {
		if (mode.isCurrentMode("FullBlock")) {

			if (e.getEventType() == EventType.SEND) {
				if (this.isInsideBlock()) {
					return;
				}
				double multiplier = 0.2;
				double mx = Math.cos((double) Math.toRadians((double) (this.mc.thePlayer.rotationYaw + 90.0f)));
				double mz = Math.sin((double) Math.toRadians((double) (this.mc.thePlayer.rotationYaw + 90.0f)));
				double x = (double) this.mc.thePlayer.movementInput.moveForward * 0.2 * mx
						+ (double) this.mc.thePlayer.movementInput.moveStrafe * 0.2 * mz;
				double z = (double) this.mc.thePlayer.movementInput.moveForward * 0.2 * mz
						- (double) this.mc.thePlayer.movementInput.moveStrafe * 0.2 * mx;
				Packet packet = e.getPacket();
				if (this.mc.thePlayer.isCollidedHorizontally && packet instanceof C03PacketPlayer) {
					++this.delay;
					C03PacketPlayer player = (C03PacketPlayer) packet;
					if (this.delay >= 5) {
						player.x = (x + player.getPositionX());
						player.y = (player.getPositionY() - 1.0);
						player.z = (z + player.getPositionZ());
						this.delay = 0;
					}
				}
			}
		}
	}

	@EventTarget
	public void onUpdate(EventPostMotion event) {
		if (mode.isCurrentMode("FullBlock")) {
			double multiplier = 0.4;
			double mx = Math.cos((double) Math.toRadians((double) (this.mc.thePlayer.rotationYaw + 90.0f)));
			double mz = Math.sin((double) Math.toRadians((double) (this.mc.thePlayer.rotationYaw + 90.0f)));
			double x = (double) this.mc.thePlayer.movementInput.moveForward * multiplier * mx
					+ (double) this.mc.thePlayer.movementInput.moveStrafe * multiplier * mz;
			double z = (double) this.mc.thePlayer.movementInput.moveForward * multiplier * mz
					- (double) this.mc.thePlayer.movementInput.moveStrafe * multiplier * mx;
			if (this.mc.thePlayer.isCollidedHorizontally && !this.mc.thePlayer.isOnLadder() && !this.isInsideBlock()) {
				this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
						this.mc.thePlayer.posX + x, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + z, false));
				for (int i = 1; i < 11; ++i) {
					this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
							this.mc.thePlayer.posX, Double.MAX_VALUE * (double) i, this.mc.thePlayer.posZ, false));
				}
				double posX = this.mc.thePlayer.posX;
				double posY = this.mc.thePlayer.posY;
				this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX,
						posY - (this.isOnLiquid() ? 9000.0 : 0.1), this.mc.thePlayer.posZ, false));
				this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY,
						this.mc.thePlayer.posZ + z);
			} else if (this.isInsideBlock()) {
				this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY,
						this.mc.thePlayer.posZ + z);
			}
		}
	}

	public boolean isOnLiquid() {
		AxisAlignedBB boundingBox = this.mc.thePlayer.getEntityBoundingBox();
		if (boundingBox == null) {
			return false;
		}
		boundingBox = boundingBox.contract(0.01, 0.0, 0.01).offset(0.0, -0.01, 0.0);
		boolean onLiquid = false;
		int y = (int) boundingBox.minY;
		for (int x = MathHelper.floor_double((double) boundingBox.minX); x < MathHelper
				.floor_double((double) (boundingBox.maxX + 1.0)); ++x) {
			for (int z = MathHelper.floor_double((double) boundingBox.minZ); z < MathHelper
					.floor_double((double) (boundingBox.maxZ + 1.0)); ++z) {
				Block block = this.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (block == Blocks.air)
					continue;
				if (!(block instanceof BlockLiquid)) {
					return false;
				}
				onLiquid = true;
			}
		}
		return onLiquid;
	}

	public boolean isInsideBlocks() {
		for (int x = MathHelper.floor_double((double) this.mc.thePlayer.boundingBox.minX); x < MathHelper
				.floor_double((double) this.mc.thePlayer.boundingBox.maxX) + 1; ++x) {
			for (int y = MathHelper.floor_double((double) this.mc.thePlayer.boundingBox.minY); y < MathHelper
					.floor_double((double) this.mc.thePlayer.boundingBox.maxY) + 1; ++y) {
				for (int z = MathHelper.floor_double((double) this.mc.thePlayer.boundingBox.minZ); z < MathHelper
						.floor_double((double) this.mc.thePlayer.boundingBox.maxZ) + 1; ++z) {
					Block block = this.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (block == null || block instanceof BlockAir)
						continue;
					AxisAlignedBB boundingBox = block.getCollisionBoundingBox((World) this.mc.theWorld,
							new BlockPos(x, y, z), this.mc.theWorld.getBlockState(new BlockPos(x, y, z)));
					if (block instanceof BlockHopper) {
						boundingBox = new AxisAlignedBB((double) x, (double) y, (double) z, (double) (x + 1),
								(double) (y + 1), (double) (z + 1));
					}
					if (boundingBox == null || !this.mc.thePlayer.boundingBox.intersectsWith(boundingBox))
						continue;
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isInsideBlock() {
		for (int x = MathHelper.floor_double(mc.thePlayer.boundingBox.minX); x < MathHelper
				.floor_double(mc.thePlayer.boundingBox.maxX) + 1; x++) {
			for (int y = MathHelper.floor_double(mc.thePlayer.boundingBox.minY); y < MathHelper
					.floor_double(mc.thePlayer.boundingBox.maxY) + 1; y++) {
				for (int z = MathHelper.floor_double(mc.thePlayer.boundingBox.minZ); z < MathHelper
						.floor_double(mc.thePlayer.boundingBox.maxZ) + 1; z++) {
					Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
					if ((block != null) && (!(block instanceof BlockAir))) {
						AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z),
								mc.theWorld.getBlockState(new BlockPos(x, y, z)));
						if ((block instanceof BlockHopper)) {
							boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
						}
						if (boundingBox != null) {
							if (mc.thePlayer.boundingBox.intersectsWith(boundingBox)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private void teleport(double dist) {
		double forward = mc.thePlayer.movementInput.moveForward;
		double strafe = mc.thePlayer.movementInput.moveStrafe;
		float yaw = mc.thePlayer.rotationYaw;
		if (forward != 0.0D) {
			if (strafe > 0.0D) {
				yaw += (forward > 0.0D ? -45 : 45);
			} else if (strafe < 0.0D) {
				yaw += (forward > 0.0D ? 45 : -45);
			}
			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1;
			} else if (forward < 0.0D) {
				forward = -1;
			}
		}
		double x = mc.thePlayer.posX;
		double y = mc.thePlayer.posY;
		double z = mc.thePlayer.posZ;
		double xspeed = forward * dist * Math.cos(Math.toRadians(yaw + 90.0F))
				+ strafe * dist * Math.sin(Math.toRadians(yaw + 90.0F));
		double zspeed = forward * dist * Math.sin(Math.toRadians(yaw + 90.0F))
				- strafe * dist * Math.cos(Math.toRadians(yaw + 90.0F));
		mc.thePlayer.setPosition(x + xspeed, y, z + zspeed);

	}
}
