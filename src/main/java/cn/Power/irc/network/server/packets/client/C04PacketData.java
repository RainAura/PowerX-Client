package cn.Power.irc.network.server.packets.client;

import cn.Power.irc.network.server.data.User;
import cn.Power.irc.network.server.packets.ClientPacket;
import cn.Power.irc.network.server.packets.Packet;
import cn.Power.irc.network.server.packets.PacketType;
import cn.Power.irc.network.server.util.JsonUtil;

public class C04PacketData extends ClientPacket implements Packet {

    public C04PacketData(User u, String con) {
        this.content = con;
        this.user = u;
        this.packetType = PacketType.C04;
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
