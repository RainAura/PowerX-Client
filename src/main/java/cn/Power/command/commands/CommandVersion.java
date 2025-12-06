package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.notification.Notification.Type;

public class CommandVersion extends Command {

	public CommandVersion(String[] commands) {
		super(commands);
	}

	@Override
	public void onCmd(String[] args) {
		Client.instance.getNotificationManager().addNotification(Client.CLIENT_name + " vX" + " by BestLaoLiu <3",Type.INFO);
	}

}
