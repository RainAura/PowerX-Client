package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.command.Command;
import cn.Power.command.CommandManager;
import cn.Power.command.commands.CommandStp;
import cn.Power.events.EventChat;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;

public class ChatCommands extends Mod {
	public ChatCommands() {
		super("Commands", Category.WORLD);
		HideMod = true;
	}

	@EventTarget
	public void onChat(EventChat ec) {
		String s = CommandManager.removeSpaces(ec.getMessage());
		/*
		 * if (!ec.getMessage().startsWith("-")) { return; }
		 */
		if (ec.getMessage().startsWith("-")) {
			for (Command cmd : CommandManager.getCommands()) {
				int i = 0;
				while (i < cmd.getCommands().length) {
					if (s.toLowerCase().split(" ")[0].equals("-" + cmd.getCommands()[i].toLowerCase())) {
						ec.setCancelled(true);
						cmd.onCmd(s.split(" "));
						return;
					}
					
					++i;
				}

			}
			return;
		}
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

}