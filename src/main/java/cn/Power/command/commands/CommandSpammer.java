package cn.Power.command.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.mod.mods.WORLD.Spammer;
import cn.Power.notification.Notification.Type;
import cn.Power.util.misc.ChatUtil;
import net.minecraft.client.Minecraft;

public class CommandSpammer extends Command {

	private static String fileDir = String.valueOf(Minecraft.getMinecraft().mcDataDir.getAbsolutePath()) + "/"
			+ "Power";

	public CommandSpammer(String[] commands) {
		super(commands);
		this.setArgs("spammer <Text>");
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
		
		if(msg.substring(0, msg.length() - 1).equals("clear")) {
			Spammer.messages.clear();
			new File(String.valueOf(fileDir) + "/spammer.txt").delete();
			
			ChatUtil.printChat("Clear");
			
			return;
		}
		
		
		ChatUtil.printChat("Added " + msg.substring(0, msg.length() - 1).replace("%s", "{Random String}"));
		
		Spammer.messages.add(msg.substring(0, msg.length() - 1));
		CommandSpammer.saveMessage();
		
		Spammer.messages.forEach(msg_ -> ChatUtil.printChat("Spammer List : \n  " + msg_.substring(0, msg_.length() - 1).replace("%s", "{Random String}") + " \n"));


		super.onCmd(args);
	}

	public static void saveMessage() {
		File f = new File(String.valueOf(fileDir) + "/spammer.txt");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintWriter pw = new PrintWriter(f);
			pw.print(Spammer.messages + "\n");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadMessage() throws IOException {
		File f = new File(String.valueOf(fileDir) + "/spammer.txt");
		if (!f.exists()) {
			f.createNewFile();
		} else {
			String line;
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				try {
					Spammer.messages.add(String.valueOf(line));
				} catch (Exception message) {
				}
			}
		}
	}

}
