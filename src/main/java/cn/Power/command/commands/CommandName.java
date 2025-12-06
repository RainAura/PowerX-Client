package cn.Power.command.commands;

import org.lwjgl.opengl.Display;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.notification.Notification.Type;

public class CommandName extends Command {
	public CommandName(String[] command) {
		super(command);
		this.setArgs("Name <txet>");
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

		Client.CLIENT_name = msg.substring(0, msg.length() - 1);
		Display.setTitle((String) msg.substring(0, msg.length() - 1));
		super.onCmd(args);
	}
}
