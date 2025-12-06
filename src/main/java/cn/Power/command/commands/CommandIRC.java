package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.irc.network.server.PacketUtil;
import cn.Power.irc.network.server.packets.client.C01PacketChat;
import cn.Power.irc.network.server.packets.client.C02PacketCommand;
import cn.Power.util.misc.ChatUtil;

public class CommandIRC extends Command {
	public CommandIRC(String[] commands) {
		super(commands);
		this.setArgs("IRC <Text>");
	}

	@Override
	public void onCmd(String[] args) {
		   if (args.length == 1) {
	            ChatUtil.sendIRCToPlayer(this.getArgs());
	        } else {
	            final String message = joinArray(args, " ", 1, args.length);
	            if (message.startsWith("/reconnect")) {
	                if (Client.instance.IRC.cc.isClosed()) {
	                	Client.instance.IRC.init();
	                } else {
	                    ChatUtil.sendIRCToPlayer("你已经连接了IRC!");
	                }
	                return;
	            }
	            if(Client.instance.IRC.cc.isClosed()){
	                ChatUtil.sendIRCToPlayer("你还没有连接IRC!");
	                return;
	            }
	            if (message.startsWith("/")) {
	            	Client.instance.IRC.cc.send(PacketUtil.SendClientPacket(new C02PacketCommand(message)));
	            } else {
	            	Client.instance.IRC.cc.send(PacketUtil.SendClientPacket(new C01PacketChat(message)));
	            }
	        }
	}
}
