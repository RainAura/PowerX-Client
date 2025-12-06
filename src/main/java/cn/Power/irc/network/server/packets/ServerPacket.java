package cn.Power.irc.network.server.packets;

import cn.Power.irc.network.server.data.User;

public class ServerPacket {
    public String content;
    public PacketType packetType;
    public User user;
//	public ArrayList<User> userlist = new ArrayList<User>();
    public User[] UserList;
}
