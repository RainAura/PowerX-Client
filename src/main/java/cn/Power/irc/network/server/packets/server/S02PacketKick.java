package cn.Power.irc.network.server.packets.server;

import cn.Power.irc.network.server.packets.ClientPacket;
import cn.Power.irc.network.server.packets.Packet;
import cn.Power.irc.network.server.packets.PacketType;
import cn.Power.irc.network.server.packets.ServerPacket;
import cn.Power.irc.network.server.util.JsonUtil;

public class S02PacketKick extends ServerPacket implements Packet {

    public S02PacketKick(String reason){
        content = reason;
        this.packetType = PacketType.S02;
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
