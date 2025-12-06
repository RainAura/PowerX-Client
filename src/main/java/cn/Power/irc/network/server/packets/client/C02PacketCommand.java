package cn.Power.irc.network.server.packets.client;

import cn.Power.irc.network.server.packets.ClientPacket;
import cn.Power.irc.network.server.packets.Packet;
import cn.Power.irc.network.server.packets.PacketType;
import cn.Power.irc.network.server.util.JsonUtil;

public class C02PacketCommand extends ClientPacket implements Packet {

    public C02PacketCommand(String command){
        this.packetType = PacketType.C02;
        this.content = command;
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
