package cn.Power.mod.mods.PLAYER;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.PlayerUtil;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.misc.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Teleport extends Mod {
	private boolean canTP;
	private int delay;
	public BlockPos endPos;
	Timer cooldown = new Timer();
	public Value<String> Mode = new Value("Teleport", "Mode", 2);

	public Teleport() {
		super("Teleport", Category.PLAYER);
		this.Mode.mode.add("Basic");
		this.Mode.mode.add("Hypixel");
		this.Mode.mode.add("HypixelExploit");
	}

	public MovingObjectPosition getBlinkBlock() {
		Vec3 var4 = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
		Vec3 var5 = mc.thePlayer.getLook(mc.timer.renderPartialTicks);
		Vec3 var6 = var4.addVector(var5.xCoord * 370, var5.yCoord * 370, var5.zCoord * 370);
		return mc.thePlayer.worldObj.rayTraceBlocks(var4, var6, false, true, true, true);
	}
	
	

	@EventTarget
	public void onPre(EventPreMotion em) {
		try {
			if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood
					|| mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
				return;
			}
		} catch (Exception e) {
		}

		if (Mode.isCurrentMode("Basic")) {
			if (canTP && Mouse.isButtonDown(1) && !mc.thePlayer.isSneaking() && delay == 0 && mc.inGameHasFocus
					&& getBlinkBlock().entityHit == null
					&& !(getBlock(getBlinkBlock().getBlockPos()) instanceof BlockChest)) {
				em.setCancelled(true);
				endPos = getBlinkBlock().getBlockPos().add(0, 1, 0);
				final double[] startPos = { mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ };
				PlayerUtil.teleport(startPos, endPos);
				delay = 5;
				em.setCancelled(false);
			}
		} else if (Mode.isCurrentMode("Hypixel")) {
			if (cooldown.check(500) && canTP && Mouse.isButtonDown(1) && !mc.thePlayer.isSneaking() && delay == 0
					&& mc.inGameHasFocus && getBlinkBlock().entityHit == null
					&& !(getBlock(getBlinkBlock().getBlockPos()) instanceof BlockChest)) {
				cooldown.reset();
				em.setCancelled(true);
				endPos = getBlinkBlock().getBlockPos();
				if (endPos.getY() + 1 > mc.thePlayer.posY) {
					ClientUtil.sendChatMessage("\247CPlease select a valid position.", ChatType.INFO);
					return;
				}
				endPos = new BlockPos(endPos.getX(), mc.thePlayer.posY, endPos.getZ());
				final double[] startPos = { mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ };
				PlayerUtil.hypixelTeleport(startPos, endPos);
				delay = 5;
				em.setCancelled(false);
			}
		} else if (Mode.isCurrentMode("HypixelExploit"))
			if (canTP && Mouse.isButtonDown(1) && !mc.thePlayer.isSneaking() && delay <= 1 && mc.inGameHasFocus
					&& getBlinkBlock().entityHit == null
					&& !(getBlock(getBlinkBlock().getBlockPos()) instanceof BlockChest)) {
				em.setCancelled(true);
				endPos = getBlinkBlock().getBlockPos().add(0, 1, 0);
				final double[] startPos = { mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ };
		//		PlayerUtil.teleport(startPos, endPos);
				
				if (endPos.getY() + 1 > mc.thePlayer.posY) {
					ClientUtil.sendChatMessage("\247CPlease select a valid position.", ChatType.INFO);
					return;
				}
				
				prot();
				
				delay = 5;
				em.setCancelled(false);
			}
		if (delay > 0) {
			--delay;
		}
	}
	
	@native0
	public void prot() {
		C03PacketPlayer c2 = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
				mc.thePlayer.posY + .00001, mc.thePlayer.posZ, true);

		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(c2);

		for (int i = 0; i < 4; i++)
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));


		C03PacketPlayer c211 = new C03PacketPlayer.C04PacketPlayerPosition(endPos.getX(), endPos.getY(), endPos.getZ(), false);

		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(c211);
	}
	

	@EventTarget
	public void on3D(EventRender er) {
		try {
			final int x = getBlinkBlock().getBlockPos().getX();
			final int y = getBlinkBlock().getBlockPos().getY();
			final int z = getBlinkBlock().getBlockPos().getZ();
			final Block block1 = getBlock(x, y, z);
			final Block block2 = getBlock(x, y + 1, z);
			final Block block3 = getBlock(x, y + 2, z);
			final boolean blockBelow = !(block1 instanceof BlockSign) && block1.getMaterial().isSolid();
			final boolean blockLevel = !(block2 instanceof BlockSign) && block1.getMaterial().isSolid();
			final boolean blockAbove = !(block3 instanceof BlockSign) && block1.getMaterial().isSolid();
			if (getBlock(getBlinkBlock().getBlockPos()).getMaterial() != Material.air && blockBelow && blockLevel
					&& blockAbove && !(getBlock(getBlinkBlock().getBlockPos()) instanceof BlockChest)) {
				canTP = true;
				GL11.glPushMatrix();
				RenderUtils.pre3D();
				mc.entityRenderer.setupCameraTransform(er.getPartialTicks(), 2);

				GL11.glColor4d(0, 0.6, 0, 0.25);
				if (Mode.isCurrentMode("Hypixel") || Mode.isCurrentMode("HypixelExploit")) {
					if (mc.thePlayer.posY < (y + 1)) {
						GL11.glColor4d(0.6, 0, 0, 0.25);
					}
				}
				RenderUtil.drawBoundingBox(
						new AxisAlignedBB(x - mc.getRenderManager().renderPosX, y - mc.getRenderManager().renderPosY,
								z - mc.getRenderManager().renderPosZ, x - mc.getRenderManager().renderPosX + 1.0,
								y + getBlock(getBlinkBlock().getBlockPos()).getBlockBoundsMaxY()
										- mc.getRenderManager().renderPosY,
								z - mc.getRenderManager().renderPosZ + 1.0));
				GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
				RenderUtils.post3D();
				GL11.glPopMatrix();
			} else {
				canTP = false;
			}
		} catch (Exception e) {

		}
	}

	public static Block getBlock(final int x, final int y, final int z) {
		return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
	}

	public static Block getBlock(final BlockPos pos) {
		return mc.theWorld.getBlockState(pos).getBlock();
	}
}
