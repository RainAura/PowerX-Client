package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.notification.Notification.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class CommandUserName extends Command {
	public CommandUserName(String[] command) {
		super(command);
		this.setArgs("FakeName <txet>");
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
		Client.ClientCode = msg.substring(0, msg.length() - 1).replace("&", "\247");
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
				new ChatComponentText("\u00a76[Power] " + "\u00a7r You New FakeName - " + Client.ClientCode));
		Client.instance.fileMgr.saveNameProtect();
		super.onCmd(args);
	}
}
