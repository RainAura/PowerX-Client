package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.notification.Notification.Type;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import net.minecraft.client.Minecraft;

public class CommandWdr extends Command {

	public CommandWdr(String[] commands) {
		super(commands);
		this.setArgs("wdr <Playername>");
	}

	@Override
	public void onCmd(String[] args) {
		if (args.length < 2) {
			Client.instance.getNotificationManager().addNotification(this.getArgs(), Type.WARNING);
		} else {
			// ClientUtil.sendClientMessage("Reported " + args[1],
			// ClientNotification.Type.SUCCESS);
			ClientUtil.sendChatMessage("\247cReported \2476" + args[1], ChatType.INFO);
			Minecraft.getMinecraft().thePlayer
					.sendChatMessage("/wdr " + args[1] + " Fly KillAura AutoClicker Speed AntiKnockBack Reach Dolphin");

		}
	}

}
