package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class Eagle extends Mod {
	public Eagle() {
		super("Eagle", Category.WORLD);
	}

	public Block getBlock(BlockPos pos) {
		return mc.theWorld.getBlockState(pos).getBlock();
	}

	public Block getBlockUnderPlayer(EntityPlayer player) {
		return getBlock(new BlockPos(player.posX, player.posY - 1.0d, player.posZ));
	}

	@EventTarget
	public void onUpdate(EventUpdate event) {
		if (getBlockUnderPlayer(mc.thePlayer) instanceof BlockAir) {
			if (mc.thePlayer.onGround) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
			}
		} else {
			if (mc.thePlayer.onGround) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
			}
		}
	}

	@Override
	public void onEnable() {
		mc.thePlayer.setSneaking(false);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
		super.onDisable();
	}
}
