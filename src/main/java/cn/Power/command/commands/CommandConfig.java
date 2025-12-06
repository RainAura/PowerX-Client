package cn.Power.command.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.darkmagician6.eventapi.EventManager;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.command.Command;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class CommandConfig extends Command {

	public List<String> ConfigList = new ArrayList<String>();
	
	public CommandConfig(String[] commands) {
		super(commands);
		this.setArgs("Config"); 
	}

	public void onCmd(String[] args) {
		if(args.length ==1) {
//			ClientUtil.sendChatMessage(" \247-c list", ChatType.ERROR);
			ClientUtil.sendChatMessage(" \247f-C \247aConfigName \247e[Load CFG]!", ChatType.ERROR);
			ClientUtil.sendChatMessage(" \247f-C Save \247aConfigName!", ChatType.ERROR);
			ClientUtil.sendChatMessage(" \247f-C Load \247aConfigName!", ChatType.ERROR);
			ClientUtil.sendChatMessage(" \247f-C Remove \247aConfigName!", ChatType.ERROR);
		}
//		if(args.length == 2 && args[1].toLowerCase().equals("list")) {
//			for (String L : ConfigList) {
//				ClientUtil.sendChatMessage(" Config List Name - \247b"+ L, ChatType.INFO);
//			}
//			
//		}
		if(args.length == 2) {
			File f = new File(String.valueOf(Minecraft.getMinecraft().mcDataDir.getAbsolutePath()) + "/"+ Client.CLIENT_File + "/prs-" + args[1].trim() + ".txt");
			try {
				if (!f.exists()) {
					ClientUtil.sendChatMessage(" Loaded \247c" + args[1] + " \247fConfig \247cError [Can't find]", ChatType.INFO);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Client.loadConfig = true;
			this.loadConfig(args[1]);
			ClientUtil.sendChatMessage(" Loaded \247c" + args[1] + " \247fConfig \247aSuccessfully", ChatType.INFO);
			Client.loadConfig = false;
		}
		if(args.length == 3 ) {
			
			if(args[1].toLowerCase().equals("save")) {
				this.saveConfig(args[2]);
				ConfigList.add(args[2]);
				ClientUtil.sendChatMessage(" Saved Config \247a"+ args[2], ChatType.INFO);
			}
			
			if(args[1].toLowerCase().equals("remove")) {
				this.deleteConfig(args[2]);
				ConfigList.remove(args[2]);
				ClientUtil.sendChatMessage(" removed Config \2476"+ args[2], ChatType.INFO);
			}
			
			if(args[1].toLowerCase().equals("load")) {
				Client.loadConfig = true;
				this.loadConfig(args[2]);
				ClientUtil.sendChatMessage(" Loaded \247c"+ args[2]+" \247fConfig \247aSuccessfully", ChatType.INFO);
				Client.loadConfig = false;
			}
			
		}
		
	}
	
	
    public void deleteConfig(String name) {
        try {
        	File file = new File(String.valueOf(Minecraft.getMinecraft().mcDataDir.getAbsolutePath()) + "/" + Client.CLIENT_File +"/prs-" + name + ".txt");
            if (file.exists()) {
                file.delete();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
	public void saveConfig(String name) {
		File f = new File(String.valueOf(Minecraft.getMinecraft().mcDataDir.getAbsolutePath()) + "/" + Client.CLIENT_File +"/prs-" + name.trim() + ".txt");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintWriter pw = new PrintWriter(f);
			for (Object m1 : ModManager.modList.values().stream().toArray()) {
				Mod m = (Mod)m1;
				pw.print(String.valueOf((Object) m.getName()) + ";" + m.isEnabled() + "\n");
			}
			for (Value value : Value.list) {
				String valueName = value.getValueName();
				if (value.isValueBoolean) {
					pw.print(String.valueOf((Object) valueName) + ":b:" + value.getValueState() + "\n");
					continue;
				}
				if (value.isValueDouble) {
					pw.print(String.valueOf((Object) valueName) + ":d:" + value.getValueState() + "\n");
					continue;
				}
				if (!value.isValueMode)
					continue;
				pw.print(String.valueOf((Object) valueName) + ":s:" + value.getModeTitle() + ":"
						+ value.getCurrentMode() + "\n");
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void loadConfig(String name) {
		File f = new File(String.valueOf(Minecraft.getMinecraft().mcDataDir.getAbsolutePath()) + "/" + Client.CLIENT_File  + "/prs-"+name+ ".txt");
		try {
			String line2;
			BufferedReader br2 = new BufferedReader((Reader) new FileReader(f));
			while ((line2 = br2.readLine()) != null) {
				if (!line2.contains((CharSequence) ";") && !line2.contains((CharSequence) ":"))continue;
				String[] splits = line2.split(";");
				Mod m = ModManager.getModByName((String) splits[0]);
					if(m == null)continue;
					boolean state = (Boolean.parseBoolean((String) splits[1]));
					if (m.isEnabled() != state)
						m.set(state);
			}
			
			String line;
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader((Reader) new FileReader(f));
			while ((line = br.readLine()) != null) {
				if (!line.contains((CharSequence) ":"))
					continue;
				String[] split = line.split(":");
				for (Value value : Value.list) {
					if (!split[0].equalsIgnoreCase(value.getValueName()))
						continue;
					if (value.isValueBoolean && split[1].equalsIgnoreCase("b")) {
						value.setValueState((Object) Boolean.parseBoolean((String) split[2]));
						continue;
					}
					if (value.isValueDouble && split[1].equalsIgnoreCase("d")) {
						value.setValueState((Object) Double.parseDouble((String) split[2]));
						continue;
					}
					if (!value.isValueMode || !split[1].equalsIgnoreCase("s")
							|| !split[2].equalsIgnoreCase(value.getModeTitle()))
						continue;
					value.setCurrentMode(Integer.parseInt((String) split[3]));
				}
			}
			
			} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
