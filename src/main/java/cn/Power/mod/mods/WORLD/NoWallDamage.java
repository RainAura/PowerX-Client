package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventPacket;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Scaffold;
import cn.Power.mod.mods.RENDER.ViewClip;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.SkyBlockUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

public class NoWallDamage extends Mod{
	boolean status = false;

	public NoWallDamage() {
		super("NoWallDamage", Category.WORLD);
	}
	
	@EventTarget
	public void onPacket(EventPacket p) {
		if(p.getPacket() instanceof C03PacketPlayer) {
			if(!ModManager.getModByClass(Scaffold.class).isEnabled() && this.isEntityInsideOpaqueBlock()) {
				if(!status) {

					  status = true;
				      ClientUtil.sendChatMessage(String.valueOf(" "+ this.getName()) + EnumChatFormatting.RED + " Press Left-Shift to Disable Wall Damage! " + EnumChatFormatting.RESET + " (Press Left-Shift)", ChatType.INFO);
				}
				
				if(mc.thePlayer.isSneaking())
					p.setCancelled(true);
			}else {
				
					status = false;
			}
		}
	}

	public boolean isEntityInsideOpaqueBlock() {
		if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.ADVENTURE && SkyBlockUtils.isUHCgame()) {
			return false;
		} else {
			BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(Integer.MIN_VALUE,
					Integer.MIN_VALUE, Integer.MIN_VALUE);

			for (int i = 0; i < 8; ++i) {
				int j = MathHelper.floor_double(
						mc.thePlayer.posY + (double) (((float) ((i >> 0) % 2) - 0.5F) * 0.1F) + (double) mc.thePlayer.getEyeHeight());
				int k = MathHelper
						.floor_double(mc.thePlayer.posX + (double) (((float) ((i >> 1) % 2) - 0.5F) * mc.thePlayer.width * 0.8F));
				int l = MathHelper
						.floor_double(mc.thePlayer.posZ + (double) (((float) ((i >> 2) % 2) - 0.5F) * mc.thePlayer.width * 0.8F));

				if (blockpos$mutableblockpos.getX() != k || blockpos$mutableblockpos.getY() != j
						|| blockpos$mutableblockpos.getZ() != l) {

					blockpos$mutableblockpos.set(k, j, l);

					if (mc.theWorld.getBlockState(blockpos$mutableblockpos).getBlock().isFullBlock() && mc.theWorld.getBlockState(blockpos$mutableblockpos).getBlock().isVisuallyOpaque()) {
						return true;
					}
				}
			}

			return false;
		}
	}
}
