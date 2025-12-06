package cn.Power.irc.network.server.packets.server;

import cn.Power.irc.network.server.data.User;
import cn.Power.irc.network.server.packets.ClientPacket;
import cn.Power.irc.network.server.packets.Packet;
import cn.Power.irc.network.server.packets.PacketType;
import cn.Power.irc.network.server.packets.ServerPacket;
import cn.Power.irc.network.server.util.JsonUtil;

public class S04PacketData extends ServerPacket implements Packet {
	
    public S04PacketData(User u, String con) {
        this.content = con;
        this.user = u;
        this.packetType = PacketType.S04;
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
