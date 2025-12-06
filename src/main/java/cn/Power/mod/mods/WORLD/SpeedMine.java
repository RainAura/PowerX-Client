package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPlayerDamageBlock;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

public class SpeedMine extends Mod {
	public Value<String> mode = new Value("SpeedMine", "Mode", 0);
	public Value<Double> Speed = new Value("SpeedMine_Packet Speed", 1.6, 1.0, 3d, 0.1d);

	public SpeedMine() {
		super("SpeedMine", Category.WORLD);
		this.mode.mode.add("Vanilla");
		this.mode.mode.add("Basic");
		this.mode.mode.add("Packet");
		this.mode.mode.add("FastPacket");
		this.mode.mode.add("NewFast");
	}

	@EventTarget
	public void onTick(EventUpdate e) {
		if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
			return;
		BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
		Block block = this.mc.theWorld.getBlockState(blockpos).getBlock();
		if (mode.isCurrentMode("Vanilla")) {
			mc.playerController.blockHitDelay = 0;
		}
		
	}

	@EventTarget
	private void OnUpdate(EventPlayerDamageBlock e) {

		Block block = this.mc.theWorld.getBlockState(e.getPos()).getBlock();
		int id = Block.getIdFromBlock(block);
		final PlayerControllerMP playerController = mc.playerController;
		if (mode.isCurrentMode("Basic")) {
			mc.playerController.blockHitDelay = 0;
			playerController.curBlockDamageMP += block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld,
					e.getPos()) * Speed.getValueState().floatValue();
		}
		if (mode.isCurrentMode("FastPacket")) {
			mc.playerController.blockHitDelay = 0;
			if (id != 7) {
				this.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
						C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, e.getPos(), e.getFace()));
			}
		}
		if (mode.isCurrentMode("NewFast")) {
			
			

	          
			mc.playerController.blockHitDelay = 0;
			playerController.curBlockDamageMP += block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld,
					e.getPos()) * Speed.getValueState().floatValue();
			if (mc.playerController.curBlockDamageMP >= 1.0f) {
				mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
						C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, e.getPos(), e.getFace()));
			}
			
			
			
			
		}
	}

	private boolean bzs = false;
	private float bzx = 0.0f;
	public BlockPos blockPos;
	public EnumFacing facing;

	@EventTarget
	public void onDamageBlock(EventPacket event) {
		if (!mode.isCurrentMode("Packet"))
			return;
		if (event.packet instanceof C07PacketPlayerDigging && !Minecraft.playerController.extendedReach()
				&& Minecraft.playerController != null) {
			C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging) event.packet;
			if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
				this.bzs = true;
				this.blockPos = c07PacketPlayerDigging.getPosition();
				this.facing = c07PacketPlayerDigging.getFacing();
				this.bzx = 0.0f;
			} else if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK
					|| c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
				this.bzs = false;
				this.blockPos = null;
				this.facing = null;
			}
		}
	}

	@EventTarget
	public void onUpdate(EventPlayerDamageBlock event) {
		if (!mode.isCurrentMode("Packet"))
			return;
		if (Minecraft.playerController.extendedReach()) {
			Minecraft.playerController.blockHitDelay = 0;
		} else if (this.bzs) {
			Block block = this.mc.theWorld.getBlockState(this.blockPos).getBlock();
			this.bzx += (float) ((double) block.getPlayerRelativeBlockHardness(Minecraft.thePlayer, this.mc.theWorld,
					this.blockPos) * Speed.getValueState());
			if (this.bzx >= 1.0f) {
				mc.playerController.curBlockDamageMP = 0f;
				this.mc.theWorld.setBlockState(this.blockPos, Blocks.air.getDefaultState(), 11);
				Minecraft.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(
						C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.facing));
				this.bzx = 0.0f;
				this.bzs = false;
			}
		}
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	public void onDisable() {
		super.onDisable();
	}
}
