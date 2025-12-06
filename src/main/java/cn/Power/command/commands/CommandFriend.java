package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.notification.Notification.Type;
import cn.Power.util.friendManager.Friend;
import cn.Power.util.friendManager.FriendManager;

public class CommandFriend extends Command {
	public CommandFriend(String[] commands) {
		super(commands);
		this.setArgs("Args: <add/a/remove/r> <name> <alias>");
	}

	@Override
	public void onCmd(String[] args) {
		if (args.length < 3) {
			Client.instance.getNotificationManager().addNotification(this.getArgs(), Type.INFO);
			return;
		}
		String option = args[1];
		String name = args[2];
		String alias = args.length > 3 ? args[3] : name;
		Friend friend = FriendManager.getFriend(name);
		if (option.equalsIgnoreCase("a") || option.equalsIgnoreCase("add")) {
			if (friend == null) {
				Friend newFriend = new Friend(name, alias);
				Client.instance.getNotificationManager().addNotification("Added friend " + name + " as " + alias, Type.SUCCESS);
				FriendManager.getFriends().add(newFriend);
			} else {
				friend.setAlias(alias);
				Client.instance.getNotificationManager().addNotification("Changed alias to " + alias, Type.INFO);
			}
		} else if (option.equalsIgnoreCase("r") || option.equalsIgnoreCase("remove")) {
			if (friend != null) {
				FriendManager.getFriends().remove(friend);
				Client.instance.getNotificationManager().addNotification("Removed friend", Type.ERROR);
			}
		} else {
			Client.instance.getNotificationManager().addNotification(this.getArgs(), Type.INFO);
		}
		Client.instance.fileMgr.saveFriends();
	}

}