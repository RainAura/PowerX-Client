package cn.Power.command.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.native0;
import cn.Power.command.Command;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventTick;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.LookTP;
import cn.Power.mod.mods.WORLD.Dismount;
import cn.Power.notification.Notification.Type;
import cn.Power.util.AStarCustomPathFinder;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.misc.ChatUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

public class CommandTP extends Command {
	final static Minecraft mc = Minecraft.getMinecraft();
	boolean tp;
	public static double x;
	public static double y;
	public static double z;
	public static Vec3 lastsuccVec;
	public LinkedList<Vec3> result = new LinkedList<Vec3>();
	boolean LOOKTP = false;
	boolean warnning = false;
	boolean maga;
	boolean oldflying;
	boolean damaged;
	public double downY = 0;
	public double lastY = 0;

	public int mode;

	private float serverYaw;

	public CommandTP(String[] command) {
		super(command);
		this.setArgs("TP <x> <y> <z>");
	}

	@native0
	@Override
	public void onCmd(String[] args) {

		try {
			lastY = mc.thePlayer.posY;
			if (!tp && args.length == 1) {
				oldflying = false;
				damaged = false;
				warnning = false;
				unreg();

				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
						"§8[§c" + Client.CLIENT_name + "§8]§r " + "\247aTP \247r: \2472Stop Teleport!"));
				EventManager.unregister(this);
			}
			if (args.length >= 4) {
				try {
					EventManager.unregister(this);
					x = CommandBase
							.parseCoordinate(mc.thePlayer.posX, args[1], Integer.MIN_VALUE, Integer.MAX_VALUE, true)
							.func_179628_a();
					y = CommandBase
							.parseCoordinate(mc.thePlayer.posY, args[2], Integer.MIN_VALUE, Integer.MAX_VALUE, true)
							.func_179628_a();
					z = CommandBase
							.parseCoordinate(mc.thePlayer.posZ, args[3], Integer.MIN_VALUE, Integer.MAX_VALUE, true)
							.func_179628_a();


					Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
							"§8[§c" + Client.CLIENT_name + "§8]§r " + "\247aTP \247r: \247bPlease wait Server Lag!"));
					reg();

					Client.instance.getNotificationManager().addNotification(
							"\247bTeleport to \247a" + args[1] + "," + args[2] + "," + args[3] + "%timer", 5000,
							Type.INFO);
					EventManager.register(this);
					LOOKTP = false;
				} catch (Exception var11) {

				}
			} else if (args.length >= 3) {
				try {
					EventManager.unregister(this);
					x = CommandBase
							.parseCoordinate(mc.thePlayer.posX, args[1], Integer.MIN_VALUE, Integer.MAX_VALUE, true)
							.func_179628_a();
					y = 120;
					z = CommandBase
							.parseCoordinate(mc.thePlayer.posZ, args[2], Integer.MIN_VALUE, Integer.MAX_VALUE, true)
							.func_179628_a();
					if (ModManager.getModByClass(Dismount.class).isEnabled()) {
						mc.thePlayer.setPosition(x, y, z);
						return;
					}
					Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
							"§8[§c" + Client.CLIENT_name + "§8]§r " + "\247aTP \247r: \247bPlease wait Server Lag!"));
					reg();
					Client.instance.getNotificationManager().addNotification(
							"\247bTeleport to \247a" + args[1] + "," + y + "," + args[2] + "%timer", 5000, Type.INFO);
					EventManager.register(this);
					LOOKTP = false;
				} catch (Exception var11) {

				}
			} else if (args.length >= 2 && args[1].contains("LookTP")) {
				if (ModManager.getModByClass(Dismount.class).isEnabled()) {
					mc.thePlayer.setPosition(LookTP.TragetEntity.posX, LookTP.TragetEntity.posY - 1,
							LookTP.TragetEntity.posZ);
					return;
				}
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
						"§8[§c" + Client.CLIENT_name + "§8]§r " + "\247aTP \247r: \247bPlease wait Server Lag!"));
				reg();
				Client.instance.getNotificationManager().addNotification(
						"Teleport to \247c" + LookTP.TragetEntity.getName() + "%timer", 5000, Type.INFO);
				EventManager.register(this);
				LOOKTP = true;
			} else if (args.length >= 1 && args[1].contains("maga") && lastsuccVec != null) {
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
						"§8[§c" + Client.CLIENT_name + "§8]§r " + "\247aTP \247r: \247bPlease wait Server Lag!"));
				maga = true;
				tp = true;
				x = lastsuccVec.xCoord;
				y = lastsuccVec.yCoord;
				z = lastsuccVec.zCoord;

				Client.instance.getNotificationManager().addNotification(
						"Teleport to \247c" + LookTP.TragetEntity.getName() + "%timer", 5000, Type.INFO);
				EventManager.register(this);
				LOOKTP = false;
			} else if (args.length >= 1 && args[1].contains("last") && lastsuccVec != null) {
				x = lastsuccVec.xCoord;
				y = lastsuccVec.yCoord;
				z = lastsuccVec.zCoord;

				if (ModManager.getModByClass(Dismount.class).isEnabled()) {
					mc.thePlayer.setPosition(x, y, z);
					return;
				}

				EventManager.unregister(this);
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
						"§8[§c" + Client.CLIENT_name + "§8]§r " + "\247aTP \247r: \247bPlease wait Server Lag!"));
				reg();
				Client.instance.getNotificationManager()
						.addNotification("Teleport to \247c" + lastsuccVec.toString() + "%timer", 5000, Type.INFO);
				EventManager.register(this);
				LOOKTP = false;
			}

		} catch (Throwable c) {
//			c.printStackTrace();
		}

	}

	@native0
	@EventTarget
	public final void onSendPacket(EventPacket event) {

		if (mc.thePlayer == null) {
			unreg();
			EventManager.unregister(this);

			// Minecraft.thePlayer.sendChatMessage("/rejoin");

		}

		if (!tp && (event.getPacket() instanceof C03PacketPlayer)) {
			event.setCancelled(true);
		}
	}

	@native0
	@EventTarget
	public void onReceivePacket(EventPacket event) {

		if (mode == 1) {

			if ((event.getPacket() instanceof S39PacketPlayerAbilities) && !this.tp) {

				S39PacketPlayerAbilities ab = (S39PacketPlayerAbilities) event.getPacket();

				PlayerCapabilities cap = new PlayerCapabilities();

				cap.setFlySpeed(0.1f);

				cap.allowFlying = true;

				cap.isFlying = true;

				Minecraft.getNetHandler().addToSendQueue(new C13PacketPlayerAbilities(cap));

				cap.allowFlying = false;

				cap.isFlying = false;

				Minecraft.getNetHandler().addToSendQueue(new C13PacketPlayerAbilities(cap));

				// this.tp = true;

			} else if ((event.getPacket() instanceof S02PacketChat) && !this.tp) {

				S02PacketChat ab = (S02PacketChat) event.getPacket();

				if (ab.getChatComponent().toString().contains("You logged back in!")
						|| ab.getChatComponent().toString().contains("你已经重新登录")) {

					mc.thePlayer.capabilities.isFlying = false;

					mc.getNetHandler().addToSendQueue(new C13PacketPlayerAbilities(mc.thePlayer.capabilities));

					this.tp = true;
				}

			}

		} else if ((event.getPacket() instanceof S08PacketPlayerPosLook) && !this.tp) {

			this.tp = true;

		} else if ((event.getPacket() instanceof S12PacketEntityVelocity) && !this.tp) {

			if (event.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
				if (packet.entityID == this.mc.thePlayer.getEntityId()) {
					this.tp = true;
				}
			}

		}

//		if(mc.thePlayer.capabilities.allowFlying) {
//			
//			PlayerCapabilities cp = mc.thePlayer.capabilities;
//			
//			cp.isFlying = true;
//			
//			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(cp));
//		}
	}

	@native0
	@EventTarget
	public void onMotionUpdate(EventTick event) {
		if (mc.thePlayer.hurtTime > 0)
			this.damaged = true;

		if (LOOKTP) {
			tp((double) (LookTP.TragetEntity.posX + Math.random()),
					(double) (LookTP.TragetEntity.posY + 2 + Math.random()),
					(double) (LookTP.TragetEntity.posZ + Math.random()));
			tp((double) (LookTP.TragetEntity.posX + Math.random()),
					(double) (LookTP.TragetEntity.posY + 2 + Math.random()),
					(double) (LookTP.TragetEntity.posZ + Math.random()));
		} else {
			tp((double) (x + Math.random()), (double) (y + Math.random()), (double) (z + Math.random()));
			tp((double) (x + Math.random()), (double) (y + Math.random()), (double) (z + Math.random()));
		}

	}

	private boolean isUnderBlock() {
		for (int i = (int) (Minecraft.getMinecraft().thePlayer.posY + 2.0D); i < 255; ++i) {
			BlockPos pos = new BlockPos(Minecraft.getMinecraft().thePlayer.posX, (double) i,
					Minecraft.getMinecraft().thePlayer.posZ);
			if (!(Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockAir)
					&& !(Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockFenceGate)
					&& !(Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockSign)
					&& !(Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockButton)) {
				return true;
			}
		}

		return false;
	}

	@native0
	private void tp(double x2, double y2, double z2) {
		try {
			if (tp) {

				if (!this.damaged) {
					ChatUtil.printChat("Damage failed.");

					if (mode == 1) {
						ChatUtil.printChat("Don't do -tp near the spawn point");
						ChatUtil.printChat("请不要在出生地附近tp!");
					}

					unreg();
					EventManager.unregister(this);

					return;
				}

				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
						new ChatComponentText("§8[§c" + Client.CLIENT_name + "§8]§r " + "TP~~!"));

				lastY = mc.thePlayer.posY;
				downY = 0;

				Vec3 lastVec3 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

				result.clear();

				double posY = mc.thePlayer.posY;

				boolean shouldUp = false;

				if (y2 > mc.thePlayer.posY && !isUnderBlock()) {

					for (; (posY = (posY += 6.83)) <= y2 - 3;) {

						mc.getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, posY,
										mc.thePlayer.posZ, this.serverYaw += 5f, this.getPitch(), false));

					}

					shouldUp = true;

				}

				result.clear();

				result = computePath((Vec3) new Vec3(mc.thePlayer.posX, posY, mc.thePlayer.posZ), new Vec3(x2, y2, z2),
						5);

				for (Vec3 vec3 : result) {

					this.serverYaw += 50;

					if (vec3.getY() < lastY) {
						downY += lastY - vec3.getY();
					}

					mc.getNetHandler().getNetworkManager()
							.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(vec3.getX(),
									shouldUp ? posY += 0.09623853219757 : vec3.getY(), vec3.getZ(),
									this.serverYaw += 5f, this.getPitch(), false));

					lastY = vec3.getY();

					lastsuccVec = vec3;
				}

				result.clear();

				mc.thePlayer.setPosition(lastVec3.xCoord, lastVec3.yCoord, lastVec3.zCoord);

				unreg();
				EventManager.unregister(this);

				// Minecraft.thePlayer.sendChatMessage("/rejoin");

				// mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new
				// C03PacketPlayer(mc.thePlayer.onGround));

				mc.renderGlobal.loadRenderers();

				result.clear();

			}
		} catch (Exception e) {

		}

	}

	public static <T> List<List<T>> averageAssign(List<T> source, int limit) {
		if (null == source || source.isEmpty()) {
			return Collections.emptyList();
		}
		List<List<T>> result = new ArrayList<>();
		int listCount = (source.size() - 1) / limit + 1;
		int remaider = source.size() % listCount; // (先计算出余数)
		int number = source.size() / listCount; // 然后是商
		int offset = 0;// 偏移量
		for (int i = 0; i < listCount; i++) {
			List<T> value;
			if (remaider > 0) {
				value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
				remaider--;
				offset++;
			} else {
				value = source.subList(i * number + offset, (i + 1) * number + offset);
			}
			result.add(value);
		}
		return result;
	}

	@native0
	@EventTarget
	public void EventMove(EventMove event) {

		setSpeed(event, 0.0);
		if (mc.thePlayer.hurtTime > 0)
			this.damaged = true;
		mc.thePlayer.motionY = 0.0;
		event.y = 0.0;
	}

	private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
		if (!canPassThrow(new BlockPos(topFrom.mc()))) {
			topFrom = topFrom.addVector(0, 1, 0);
		}
		AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
		pathfinder.compute();

		int i = 0;
		Vec3 lastLoc = null;
		Vec3 lastDashLoc = null;
		ArrayList<Vec3> path = new ArrayList<Vec3>();
		ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
		for (Vec3 pathElm : pathFinderPath) {
			if (i == 0 || i == pathFinderPath.size() - 1) {
				if (lastLoc != null) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
				}
				path.add(pathElm.addVector(0.5, 0, 0.5));
				lastDashLoc = pathElm;
			} else {
				boolean canContinue = true;
				if (pathElm.squareDistanceTo(lastDashLoc) > 1980) {
					canContinue = false;
				} else {
					double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
					double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
					double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
					double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
					double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
					double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
					cordsLoop: for (int x = (int) smallX; x <= bigX; x++) {
						for (int y = (int) smallY; y <= bigY; y++) {
							for (int z = (int) smallZ; z <= bigZ; z++) {
								if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
									canContinue = false;
									break cordsLoop;
								}
							}
						}
					}
				}
				if (!canContinue) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
					lastDashLoc = lastLoc;
				}
			}
			lastLoc = pathElm;
			i++;
		}
		return path;
	}

	public static int getSpeedEffect() {
		if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
			return mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
		else
			return 0;
	}

	public static void hypixelTeleport(final double[] startPos, final BlockPos endPos) {

		double distx = startPos[0] - endPos.getX() + 0.5;
		double disty = startPos[1] - endPos.getY();
		double distz = startPos[2] - endPos.getZ() + 0.5;
		double dist = Math.sqrt(mc.thePlayer.getDistanceSq(endPos));
		double distanceEntreLesPackets = 0.31 + getSpeedEffect() / 20;
		double xtp, ytp, ztp = 0;
		if (dist > distanceEntreLesPackets) {

			double nbPackets = Math.round(dist) - 1;

			xtp = mc.thePlayer.posX;
			ytp = mc.thePlayer.posY;
			ztp = mc.thePlayer.posZ;
			double count = 0;
			for (int i = 1; i < nbPackets; i++) {
				double xdi = (endPos.getX() - mc.thePlayer.posX) / (nbPackets);
				xtp += xdi;

				double zdi = (endPos.getZ() - mc.thePlayer.posZ) / (nbPackets);
				ztp += zdi;

				double ydi = (endPos.getY() - mc.thePlayer.posY) / (nbPackets);
				ytp += ydi;
				count++;

				if (!mc.theWorld.getBlockState(new BlockPos(xtp, ytp - 1, ztp)).getBlock().isFullBlock()) {
					if (count <= 2) {
						ytp += 2E-8;
					} else if (count >= 4) {
						count = 0;
					}
				}
				C03PacketPlayer.C04PacketPlayerPosition Packet = new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp,
						ztp, false);
			}

			C03PacketPlayer.C04PacketPlayerPosition Packet = new C03PacketPlayer.C04PacketPlayerPosition(endPos.getX(),
					endPos.getY(), endPos.getZ(), true);
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(Packet);
			// mc.thePlayer.setPosition(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() +
			// 0.5);

		} else {
			C03PacketPlayer.C04PacketPlayerPosition Packet = new C03PacketPlayer.C04PacketPlayerPosition(endPos.getX(),
					endPos.getY(), endPos.getZ(), true);
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(Packet);
			// mc.thePlayer.setPosition(endPos.getX(), endPos.getY(), endPos.getZ());

		}
	}

	@native0
	public static LinkedList<Vec3> computePath(Vec3 topFrom, Vec3 to, int len) {
		if (!canPassThrow(new BlockPos(topFrom.mc()))) {
			topFrom = topFrom.addVector(0.0D, 1.0D, 0.0D);
		}

		AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
		pathfinder.compute();
		int i = 0;
		Vec3 lastLoc = null;
		Vec3 lastDashLoc = null;
		LinkedList<Vec3> path = new LinkedList();
		ArrayList pathFinderPath = pathfinder.getPath();

		for (Iterator var10 = pathFinderPath.iterator(); var10.hasNext(); ++i) {
			Vec3 pathElm = (Vec3) var10.next();
			if (i != 0 && i != pathFinderPath.size() - 1) {
				boolean canContinue = true;
				if (pathElm.squareDistanceTo(lastDashLoc) > (double) len) {
					canContinue = false;
				} else {
					double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
					double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
					double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
					double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
					double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
					double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());

					label54: for (int x = (int) smallX; (double) x <= bigX; ++x) {
						for (int y = (int) smallY; (double) y <= bigY; ++y) {
							for (int z = (int) smallZ; (double) z <= bigZ; ++z) {
								if (!AStarCustomPathFinder
										.checkPositionValidity(new Vec3((double) x, (double) y, (double) z))) {
									canContinue = false;
									break label54;
								}
							}
						}
					}
				}

				if (!canContinue) {
					path.add(lastLoc.addVector(0.5D, 0.0D, 0.5D));
					lastDashLoc = lastLoc;
				}
			} else {
				if (lastLoc != null) {
					path.add(lastLoc.addVector(0.5D, 0.0D, 0.5D));
				}

				path.add(pathElm.addVector(0.5D, 0.0D, 0.5D));
				lastDashLoc = pathElm;
			}

			lastLoc = pathElm;
		}

		path.add(to);
		return path;
	}

	private static boolean canPassThrow(BlockPos pos) {
		Block block = Minecraft.getMinecraft().theWorld
				.getBlockState(new net.minecraft.util.BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
		return block.getMaterial() == Material.air || block.getMaterial() == Material.plants
				|| block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water
				|| block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
	}

	public static void setSpeed(EventMove moveEvent, double moveSpeed) {
		setSpeed(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe,
				mc.thePlayer.movementInput.moveForward);
	}

	public static void setSpeed(EventMove moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe,
			double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;

		if (forward != 0.0D) {
			if (strafe > 0.0D) {
				yaw += ((forward > 0.0D) ? -45 : 45);
			} else if (strafe < 0.0D) {
				yaw += ((forward > 0.0D) ? 45 : -45);
			}
			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1.0D;
			} else if (forward < 0.0D) {
				forward = -1.0D;
			}
		}

		if (strafe > 0.0D) {
			strafe = 1.0D;
		} else if (strafe < 0.0D) {
			strafe = -1.0D;
		}
		double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
		double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
		moveEvent.x = forward * moveSpeed * mx + strafe * moveSpeed * mz;
		moveEvent.z = forward * moveSpeed * mz - strafe * moveSpeed * mx;
	}

	public void reg() {
		if (mc.thePlayer == null)
			return;

		boolean damageToggle = Client.tpdamage.getValueState().booleanValue();

		{
			damaged = true;
		}

		tp = false;

		if (mc.thePlayer.ridingEntity == null) {

//			if (SkyBlockUtils.isMWgame()) {
//				mc.thePlayer.sendChatMessage("-r");
//
//				mode = 1;
//			} else {

				mode = 2;

				tp = false;
//			}

//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
//				mc.thePlayer.posX, mc.thePlayer.posY + .00053424, mc.thePlayer.posZ, false));
//
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
//				mc.thePlayer.posX, mc.thePlayer.posY + .42, mc.thePlayer.posZ, false));
//
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
//				mc.thePlayer.posX, mc.thePlayer.posY + .71449694860846, mc.thePlayer.posZ, false));
//
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
//				mc.thePlayer.posX, mc.thePlayer.posY + .94386002147323, mc.thePlayer.posZ, false));
//
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
//				mc.thePlayer.posX, mc.thePlayer.posY + 1.09023583572959, mc.thePlayer.posZ, false));
//
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
//				mc.thePlayer.posX, mc.thePlayer.posY + 1.15528413496683, mc.thePlayer.posZ, false));
//
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
//				mc.thePlayer.posX, mc.thePlayer.posY + 1.14063146793416, mc.thePlayer.posZ, false));
//
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
//				mc.thePlayer.posX, mc.thePlayer.posY + 1.04787185243677, mc.thePlayer.posZ, false));
//
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
//				mc.thePlayer.posX, mc.thePlayer.posY + .87856742595422, mc.thePlayer.posZ, false));
//
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
//				mc.thePlayer.posX, mc.thePlayer.posY + .6342490832462, mc.thePlayer.posZ, false));

		} else {

			mc.getNetHandler().getNetworkManager().sendPacket(new C0CPacketInput(0.0f, 1.0f, false, true));

			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
					mc.thePlayer.posX, mc.thePlayer.ridingEntity.boundingBox.maxY, mc.thePlayer.posZ, false));

		}

		mc.thePlayer.stepHeight = 0.0f;
		mc.thePlayer.motionX = 0.0;
		mc.thePlayer.motionZ = 0.0;

	}

	public static float getPitch() {
		return ThreadLocalRandom.current().nextBoolean() ? 89f : -89f;
	}

	public void unreg() {
		EntityPlayerSP player = mc.thePlayer;
		mc.timer.timerSpeed = 1.0f;
		player.stepHeight = 0.625f;
		player.motionX = 0.0;
		player.motionZ = 0.0;

		// Minecraft.thePlayer.sendChatMessage("/rejoin");
	}

}
