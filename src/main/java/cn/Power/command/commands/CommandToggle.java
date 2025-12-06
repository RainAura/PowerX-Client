package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.notification.Notification.Type;

public class CommandToggle extends Command {

	public CommandToggle(String[] commands) {
		super(commands);
		this.setArgs("toggle <module>");
	}

	@Override
	public void onCmd(String[] args) {
		if (args.length != 2) {
			Client.instance.getNotificationManager().addNotification(this.getArgs(), Type.INFO);
			return;
		}
		boolean found = false;
		for (Object m1 : ModManager.modList.values().stream().toArray()) {
			Mod mod = (Mod)m1;
			if (!args[1].equalsIgnoreCase(mod.getName()))
				continue;
			mod.set(!mod.isEnabled());
			found = true;
			Client.instance.getNotificationManager().addNotification(String.valueOf(mod.getName()) + " was toggled",
					Type.SUCCESS);
			break;
		}
		if (!found) {
			Client.instance.getNotificationManager().addNotification("Cannot find Module : " + args[1], Type.WARNING);
		}
	}
}
