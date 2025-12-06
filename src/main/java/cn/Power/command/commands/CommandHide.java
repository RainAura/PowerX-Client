package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.notification.Notification.Type;

public class CommandHide extends Command {

	public CommandHide(String[] commands) {
		super(commands);
		this.setArgs("Hide <module>");
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
			mod.HideMod = !mod.HideMod;
			found = true;
			Client.instance.getNotificationManager().addNotification(String.valueOf(mod.getName()) + " Module hide toggled!",
					Type.SUCCESS);
			mod.getModuleProgressionX().setValue(0);
			mod.getModuleProgressionY().setValue(0);
			Client.instance.fileMgr.saveHideMods();
			break;
		}
		if (!found) {
			Client.instance.getNotificationManager().addNotification("Hide to Module : " + args[1], Type.WARNING);
		}
	}
}
