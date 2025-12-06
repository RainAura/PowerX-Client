package cn.Power.irc.network.server.packets.client;

import cn.Power.irc.network.server.data.User;
import cn.Power.irc.network.server.packets.ClientPacket;
import cn.Power.irc.network.server.packets.Packet;
import cn.Power.irc.network.server.packets.PacketType;
import cn.Power.irc.network.server.util.JsonUtil;

public class C00PacketConnect extends ClientPacket implements Packet {

    public C00PacketConnect(User user) {
        this.user = user;
        this.packetType = PacketType.C00;
    }

    @Override
    public String getJson() {
        return JsonUtil.toJson(this);
    }

    @Override
    public ClientPacket parsePacket(String j) {
        return (ClientPacket) JsonUtil.parseJson(j, this.getClass());
    }
}
