package cn.Power.irc.network.server.packets.server;

import cn.Power.irc.network.server.data.User;
import cn.Power.irc.network.server.packets.ClientPacket;
import cn.Power.irc.network.server.packets.Packet;
import cn.Power.irc.network.server.packets.PacketType;
import cn.Power.irc.network.server.packets.ServerPacket;
import cn.Power.irc.network.server.util.JsonUtil;

public class S06PacketUserList extends ServerPacket implements Packet {
	
	public S06PacketUserList(User[] userlist) {
		this.UserList = userlist;
		this.packetType = PacketType.S06;
	}
    
    @Override
    public String getJson() {
        return JsonUtil.toJson(this);
    }

    @Override
    public ClientPacket parsePacket(String j) {
        return (ClientPacket) JsonUtil.parseJson(j,this.getClass());
    }
}
