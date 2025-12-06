package cn.Power.command;

import java.util.ArrayList;

import cn.Power.command.commands.CommandBackTracking;
import cn.Power.command.commands.CommandBind;
import cn.Power.command.commands.CommandConfig;
import cn.Power.command.commands.CommandDown;
import cn.Power.command.commands.CommandFriend;
import cn.Power.command.commands.CommandGetPos;
import cn.Power.command.commands.CommandHelp;
import cn.Power.command.commands.CommandHide;
import cn.Power.command.commands.CommandIRC;
import cn.Power.command.commands.CommandName;
import cn.Power.command.commands.CommandPall;
import cn.Power.command.commands.CommandRejoin;
import cn.Power.command.commands.CommandSay;
import cn.Power.command.commands.CommandSpammer;
import cn.Power.command.commands.CommandStp;
import cn.Power.command.commands.CommandTP;
import cn.Power.command.commands.CommandTarget;
//import cn.Power.command.commands.CommandTP;
import cn.Power.command.commands.CommandToggle;
import cn.Power.command.commands.CommandUserName;
import cn.Power.command.commands.CommandWayPoints;
import cn.Power.command.commands.CommandWdr;

public class CommandManager {
	private static ArrayList<Command> commands = new ArrayList();

	public CommandManager() {
		add(new CommandBind(new String[] { "bind" }));
		add(new CommandToggle(new String[] { "toggle", "t" }));
		add(new CommandHide(new String[] { "hide", "h" }));
		add(new CommandWayPoints(new String[] {"wp", "way" , "waypoint" , "wayPoints", "WPs" , "WayP"}));
		add(new CommandFriend(new String[] { "friend", "f" }));
		add(new CommandTP(new String[] { "tp" }));
		add(new CommandWdr(new String[] { "wdr" }));
		
		add(new CommandDown(new String[] {"down"})); 
		add(new CommandStp(new String[] {"stp"}));
		
		add(new CommandTarget(new String[] {"target","tg" , "tar"}));
		
		add(new CommandBackTracking(new String[] { "bt", "tb", "tpback", "tpb" }));
		
		add(new CommandSpammer(new String[] { "spammer","spam" }));
		add(new CommandSay(new String[] { "say" }));
		add(new CommandHelp(new String[] { "help" }));
		add(new CommandRejoin(new String[] { "rejoin", "r" }));
		add(new CommandPall(new String[] {"pall"}));
		add(new CommandIRC(new String[] {"irc","i"}));
		add(new CommandConfig(new String[] { "config","c","CONFIG" ,"Config"}));
		add(new CommandName(new String[] { "name" }));
		add(new CommandUserName(new String[] { "fn", "FakeName" }));
		add(new CommandGetPos(new String[] { "getpos", "gpos", "GETPOS", "GetPos","Getpos","gepos" ,"gpos","gp","GP"}));
		
	}

	public void add(Command c) {
		this.commands.add(c);
	}

	public static ArrayList<Command> getCommands() {
		return commands;
	}

	public static String removeSpaces(String message) {
		String space = " ";
		String doubleSpace = "  ";
		while (message.contains(doubleSpace)) {
			message = message.replace(doubleSpace, space);
		}
		return message;
	}
}
