package cn.Power.command.commands;

import cn.Power.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class CommandBlock extends Command {

	public CommandBlock(String[] commands) {
		super(commands);
		this.setArgs("block (add | remove) <id>, (clear | list)");
	}

	@Override
	public void onCmd(String[] args) {

	}

	private void removeId(int number) {
		/*
		 * int i = 0; while (i < BlockESP.getBlockIds().size()) { int id =
		 * BlockESP.getBlockIds().get(i); if (id == number) {
		 * BlockESP.getBlockIds().remove(i); } ++i; }
		 */
	}
}
