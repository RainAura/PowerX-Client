package cn.Power.command.commands;

import org.lwjgl.input.Keyboard;

import cn.Power.Client;
import cn.Power.native0;
import cn.Power.command.Command;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.notification.Notification.Type;

@native0
public class CommandBind extends Command {
	public CommandBind(String[] command) {
		super(command);
		this.setArgs("bind <mod> <key>");
	}

	@native0
	@Override
	public void onCmd(String[] args) {
		if (args.length < 3) {
			Client.instance.getNotificationManager().addNotification(this.getArgs(), Type.WARNING);
		} else {
			String mod = args[1];
			int key = Keyboard.getKeyIndex((String) args[2].toUpperCase());
			for (Object m1 : ModManager.modList.values().toArray()) {
				Mod m = (Mod)m1;
				if (!m.getName().equalsIgnoreCase(mod))
					continue;
				m.setKey(key);
				Client.instance.getNotificationManager().addNotification(
						String.valueOf(m.getName()) + " was bound to " + Keyboard.getKeyName((int) key),
						Keyboard.getKeyName((int) key).equals("NONE") ? Type.ERROR
								: Type.SUCCESS);
				Client.instance.fileMgr.saveKeys();
				return;
			}
			Client.instance.getNotificationManager().addNotification("Cannot find Module : " + mod, Type.ERROR);
		}
	}
}
