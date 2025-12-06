package cn.Power.command.commands;

import cn.Power.command.Command;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class CommandDown extends Command {

	public CommandDown(String[] commands) {
		super(commands);
		this.setArgs("Down");
	}

	@Override
	public void onCmd(String[] args) {
		for (int offset = 0; offset < Minecraft.thePlayer.posY - 2; offset += 1) {
			BlockPos bp = Minecraft.thePlayer.getPosition().offset(EnumFacing.DOWN, offset);
			
			if(Minecraft.theWorld.getBlockState(bp).getBlock() == Blocks.bedrock) {
				
				ClientUtil.sendChatMessage("No Space down!", ChatType.ERROR);
				
				break;
			}
			

			if (bp.getY() > 2 && Minecraft.theWorld.getBlockState(bp).getBlock().getMaterial() == Material.air
					&& Minecraft.theWorld.getBlockState(bp.down()).getBlock().getMaterial() == Material.air
							&& Minecraft.theWorld.getBlockState(bp.down().down()).getBlock().getMaterial() == Material.air
									&& Minecraft.theWorld.getBlockState(bp.down().down().down()).getBlock().getMaterial() == Material.air
						&& Minecraft.theWorld.getBlockState(bp.down().down().down().down()).getBlock().getMaterial() != Material.lava) {

				Minecraft.thePlayer.setPosition(Minecraft.thePlayer.posX, bp.down().down().getY(), Minecraft.thePlayer.posZ);
			
				break;
			}
		}
	}

	private void removeId(int number) {
		/*
		 * int i = 0; while (i < BlockESP.getBlockIds().size()) { int id =
		 * BlockESP.getBlockIds().get(i); if (id == number) {
		 * BlockESP.getBlockIds().remove(i); } ++i; }
		 */
	}
}
