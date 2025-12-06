/*
 * Recode By Suchen
 */
package cn.Power.mod.mods.MOVEMENT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventBlockBB;
import cn.Power.events.EventJump;
import cn.Power.events.EventLiquidCollide;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.WORLD.Dismount;
import cn.Power.util.BlockUtils;
import cn.Power.util.MathUtils;
import cn.Power.util.PlayerUtil;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Timer;

public class Jesus extends Mod {
	int stage, water;
	TimeHelper timer = new TimeHelper();
	private int tick;

	public static boolean Jesuss = false;
	public static int Delay = 0;
	boolean getDown;
	private boolean wasWater;

	public double offset = 0.0d;
	

	public Value<Double> Speed = new Value("Jesus_Speed", 0.2073d, 0.05d, 0.2999d, 0.0001d);

	public boolean down;

	public Jesus() {
		super("Jesus", Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		stage = 0;
		water = 0;
		wasWater = false;
		getDown = Jesuss = false;
	}

	@Override
	public void onDisable() {
		stage = 0;
		water = 0;
		wasWater = false;
		getDown = Jesuss = false;

		Minecraft.thePlayer.stepHeight = 0.6f;

		Timer.timerSpeed = 1.00f;
	}

	private boolean isInLiquid() {
		if (Minecraft.thePlayer == null) {
			return false;
		}
		int x = MathHelper.floor_double(Minecraft.thePlayer.boundingBox.minX);
		while (true) {
			final int n = x;
			if (n >= MathHelper.floor_double(Minecraft.thePlayer.boundingBox.maxX) + 1) {
				return false;
			}
			int z = MathHelper.floor_double(Minecraft.thePlayer.boundingBox.minZ);
			while (true) {
				final int n2 = z;
				if (n2 >= MathHelper.floor_double(Minecraft.thePlayer.boundingBox.maxZ) + 1) {
					++x;
					break;
				}
				final int x2 = x;
				final BlockPos pos = new BlockPos(x2, (int) Minecraft.thePlayer.boundingBox.minY, z);
				final Block block = Minecraft.theWorld.getBlockState(pos).getBlock();
				if (block != null && !(block instanceof BlockAir)) {
					
					if (block instanceof BlockLiquid 
							&& Minecraft.theWorld.getBlockState(pos).getProperties()
							.get(BlockLiquid.LEVEL) instanceof Integer) {
						if ((int) Minecraft.theWorld.getBlockState(pos).getProperties()
								.get(BlockLiquid.LEVEL) >= 1) {
							return false;
						}
					}
					return block instanceof BlockLiquid;
				}
				++z;
			}
		}
	}

	@EventTarget
	public void onMove(EventMove e) {

		if (isOnLiquid() && !this.isInLiquid())
			e.setMoveSpeed(Speed.getValueState().doubleValue());
	}

	public boolean isInLiquiddol() {
		if (Minecraft.thePlayer == null) {
			return false;
		}
		boolean inLiquid = false;
		final int y = (int) (Minecraft.thePlayer.boundingBox.minY + 0.02);
		for (int x = MathHelper.floor_double(Minecraft.thePlayer.boundingBox.minX); x < MathHelper
				.floor_double(Minecraft.thePlayer.boundingBox.maxX) + 1; ++x) {
			for (int z = MathHelper.floor_double(Minecraft.thePlayer.boundingBox.minZ); z < MathHelper
					.floor_double(Minecraft.thePlayer.boundingBox.maxZ) + 1; ++z) {
				final Block block = Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (block != null && !(block instanceof BlockAir)) {
					if (!(block instanceof BlockLiquid)) {
						return false;
					}
					
					if (block instanceof BlockLiquid 
							&& Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
							.get(BlockLiquid.LEVEL) instanceof Integer) {
						if ((int) Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
								.get(BlockLiquid.LEVEL) >= 1) {
							return false;
						}
					}

					inLiquid = true;
				}
			}
		}
		return inLiquid;
	}

	public boolean isOnLiquid2() {
		if (Minecraft.thePlayer == null) {
			return false;
		}
		boolean onLiquid = false;
		final int y = (int) Minecraft.thePlayer.boundingBox.offset(0.0, -0.0, 0.0).minY;
		for (int x = MathHelper.floor_double(Minecraft.thePlayer.boundingBox.minX); x < MathHelper
				.floor_double(Minecraft.thePlayer.boundingBox.maxX) + 1; ++x) {
			for (int z = MathHelper.floor_double(Minecraft.thePlayer.boundingBox.minZ); z < MathHelper
					.floor_double(Minecraft.thePlayer.boundingBox.maxZ) + 1; ++z) {
				final Block block = Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (block != null && !(block instanceof BlockAir)) {
					if (!(block instanceof BlockLiquid)) {
						return false;
					}

					if (Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
							.get(BlockLiquid.LEVEL) instanceof Integer) {
						if ((int) Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
								.get(BlockLiquid.LEVEL) > 2) {
							return false;
						}
					}
					onLiquid = true;
				}
			}
		}
		return onLiquid;
	}

	public static boolean isOnLiquid() {
		if (Minecraft.thePlayer == null)
			return false;
		boolean onLiquid = false;
		final int y = (int) Minecraft.thePlayer.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;
		for (int x = MathHelper.floor_double(Minecraft.thePlayer.getEntityBoundingBox().minX); x < MathHelper
				.floor_double(Minecraft.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
			for (int z = MathHelper.floor_double(Minecraft.thePlayer.getEntityBoundingBox().minZ); z < MathHelper
					.floor_double(Minecraft.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
				final Block block = Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (block != null && block.getMaterial() != Material.air) {
					if (!(block instanceof BlockLiquid))
						return false;

					if (Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
							.get(BlockLiquid.LEVEL) instanceof Integer) {
						if ((int) Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
								.get(BlockLiquid.LEVEL) >= 1) {
							return false;
						}
					}
					onLiquid = true;
				}
			}
		}
		return onLiquid;
	}

	public static boolean isInLiquidB() {
		if (Minecraft.thePlayer == null)
			return false;
		boolean onLiquid = false;
		final int y = (int) Minecraft.thePlayer.getEntityBoundingBox().offset(0.0D, +0.01D, 0.0D).minY;
		for (int x = MathHelper.floor_double(Minecraft.thePlayer.getEntityBoundingBox().minX); x < MathHelper
				.floor_double(Minecraft.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
			for (int z = MathHelper.floor_double(Minecraft.thePlayer.getEntityBoundingBox().minZ); z < MathHelper
					.floor_double(Minecraft.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
				final Block block = Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (block != null && block.getMaterial() != Material.air) {
					if (!(block instanceof BlockLiquid))
						return false;

					onLiquid = true;
				}
			}
		}
		return onLiquid;
	}

	public static boolean isOnLiquid(double profondeur) {
		boolean onLiquid = false;

		if (Minecraft.theWorld.getBlockState(
				new BlockPos(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY - profondeur, Minecraft.thePlayer.posZ))
				.getBlock().getMaterial().isLiquid()) {
			onLiquid = true;
		}
		return onLiquid;
	}

	public static boolean isTotalOnLiquid(double profondeur) {
		for (double x = Minecraft.thePlayer.boundingBox.minX; x < Minecraft.thePlayer.boundingBox.maxX; x += 0.01f) {

			for (double z = Minecraft.thePlayer.boundingBox.minZ; z < Minecraft.thePlayer.boundingBox.maxZ; z += 0.01f) {
				Block block = Minecraft.theWorld
						.getBlockState(new BlockPos(x, Minecraft.thePlayer.posY - profondeur, z)).getBlock();
				if (!(block instanceof BlockLiquid) && !(block instanceof BlockAir)) {
					return false;
				}
			}
		}
		return true;
	}

	@EventTarget
	public void onJump(EventJump e) {
		if (isOnLiquid()) {
			e.setCancelled(true);
		}
	}
	
	@native0
	public void DoNative(EventPreMotion em) {
		if (Minecraft.thePlayer.fallDistance != 0.0f) {
			return;
		}
		
		
		Minecraft.thePlayer.stepHeight = 0.015625f;
	
		++this.stage;
		if (this.stage == 1) {
			em.setY(em.y - ThreadLocalRandom.current().nextDouble(0.015625D - 1.000000001E-4D, 0.015625D));
		}
		if (this.stage == 2) {
			em.setY(em.y + ThreadLocalRandom.current().nextDouble(0.015D - 1.000000001E-4D, 0.015D));
		}
		if (this.stage == 3) {
			em.setY(em.y + ThreadLocalRandom.current().nextDouble(0.02D - 1.000000001E-4D, 0.02D));
		}
		if (this.stage >= 4) {
			em.setY(em.y + 0.015625D);
			this.stage = 0;
		}
		
		if (this.stage % 2 == 0) {
			em.setY(em.y - 1.0E-13);
		}
		em.setY(em.y + 1.0E-13);
		
		
		em.setOnGround(shouldGround(em.y));

	}
	
	@native0
	public void onNative2() {
		Minecraft.thePlayer.motionY = 0.11999998688698;
	}

	@EventTarget
	public void onUpdate(EventPreMotion em) {
		if (!ModManager.getModByClass(Speed.class).isEnabled() && !mc.gameSettings.keyBindSneak.isKeyDown()
				&& !mc.gameSettings.keyBindJump.isKeyDown()) {
			if (isOnLiquid() && !this.isInLiquid() && !isInLiquiddol()) {

				DoNative(em);

				return;
			} else if (this.isInLiquid() && isInLiquiddol()) {
				onNative2();
			}
		}

		this.stage = 0;

		if (Minecraft.thePlayer.stepHeight < 0.02f)
			Minecraft.thePlayer.stepHeight = 0.6f;

	}

	@native0
	public boolean shouldGround(final double n) {
		return n % 1.0 == 0.015625 || n % 1.0 == 0.0625 || n % 0.125 == 0.0;
	}

	@EventTarget
	public void onBlockCollide(EventLiquidCollide event) {
		int x = event.getPos().getX();
		int y = event.getPos().getY();
		int z = event.getPos().getZ();

		if (!ModManager.getModByClass(Speed.class).isEnabled() && !this.isInLiquid()
				&& !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {

			if (Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
					.get(BlockLiquid.LEVEL) instanceof Integer) {
				if ((int) Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
						.get(BlockLiquid.LEVEL) >= 1) {
					return;
				}
			}

			final Object v2 = Minecraft.theWorld.getBlockState(event.getPos()).getProperties()
					.get((Object) BlockLiquid.LEVEL);
			if ((!(v2 instanceof Integer) || (int) v2 <= 3)
					&& Minecraft.theWorld.getBlockState(event.getPos()).getBlock() instanceof BlockLiquid
					&& !Minecraft.thePlayer.isSneaking()) {

				event.setCancelled(true);
				event.setBounds(new AxisAlignedBB(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(),
						event.getPos().getX() + 0.98, event.getPos().getY() + 1.0, event.getPos().getZ() + 0.98));

			}
		}

	}

	private boolean b() {
		double var2 = Minecraft.thePlayer.posX;
		double var4 = Minecraft.thePlayer.posY;
		double var6 = Minecraft.thePlayer.posZ;
		BlockPos[] var8 = new BlockPos[] { new BlockPos(var2 + 0.3D, var4, var6 + 0.3D),
				new BlockPos(var2 - 0.3D, var4, var6 + 0.3D), new BlockPos(var2 + 0.3D, var4, var6 - 0.3D),
				new BlockPos(var2 - 0.3D, var4, var6 - 0.3D) };
		BlockPos[] var9 = var8;
		int var10 = var8.length;
		int v0 = 0;

		boolean var13;
		while (true) {
			if (v0 < var10) {
				BlockPos v1 = var9[v0];
				var13 = Minecraft.theWorld.getBlockState(v1).getBlock() instanceof BlockLiquid;

				if ((var13)
						&& Minecraft.theWorld.getBlockState(v1).getProperties()
								.get(BlockLiquid.LEVEL) instanceof Integer
						&& (Integer) (Minecraft.theWorld.getBlockState(v1).getProperties()
								.get(BlockLiquid.LEVEL)) <= 3) {
					return true;
				}

				++v0;
			}

			var13 = false;
			break;
		}

		return var13;
	}

}