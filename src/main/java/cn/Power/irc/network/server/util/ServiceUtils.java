package cn.Power.irc.network.server.util;

import java.util.ArrayList;

public class ServiceUtils {
    public static ArrayList<String> ranks = new ArrayList<>();

	public static String getRank(String name) {
		switch (name) {
		case "Dev":
			return "\2473" + "[Dev]\247r";
		case "Admin":
			return "\2474" + "[Admin]\247r";
		case "Mod":
			return "\2472" + "[Mod]\247r";
		case "Help":
			return "\2479" + "[Help]\247r";
		case "Contributor":
			return "\2475" + "[Contributor]\247r";
		case "User":
			return "\247a" + "[User]\247r";
		}

		for (String s : ranks) {
			if (s.contains(name)) {
				return s.split(":")[1];
			}
		}

		return "";
	}
	
    public static String msg(String string){
        String[] args = string.split( " ");
        StringBuilder msg = new StringBuilder();
        int i = 1;
        while (i < args.length) {
            msg.append(args[i]).append(" ");
            ++i;
        }
        return  msg.toString();
    }
    
    public static String getTimeleft(long Targettime){
    	long l = Targettime - System.currentTimeMillis();
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		String mutetime = (day <= 0 ? "" : (day + "d ")) + (hour <= 0 ? "" : (hour + "h ")) + (min <= 0 ? "" : (min + "m ")) + (s <= 0 ? "" : (s + "s"));
    	return mutetime;
    }
    
}
