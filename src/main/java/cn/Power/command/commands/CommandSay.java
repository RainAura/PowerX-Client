package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.notification.Notification.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class CommandSay extends Command {
	public static boolean blockedmsg = false;

	public CommandSay(String[] commands) {
		super(commands);
		this.setArgs("say <text>");
	}

	@Override
	public void onCmd(String[] args) {
		String msg = "";
		if (args.length <= 1) {
			Client.instance.getNotificationManager().addNotification(this.getArgs(), Type.WARNING);
			return;
		}
		int i = 1;
		while (i < args.length) {
			msg = String.valueOf(String.valueOf(msg)) + args[i] + " ";
			++i;
		}
		Minecraft.getMinecraft().thePlayer.sendQueue
				.addToSendQueue(new C01PacketChatMessage(msg.substring(0, msg.length() - 1)));
		super.onCmd(args);
	}
}
