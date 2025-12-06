package cn.Power.irc.network.server.packets.client;

import cn.Power.irc.network.server.packets.ClientPacket;
import cn.Power.irc.network.server.packets.Packet;
import cn.Power.irc.network.server.packets.PacketType;
import cn.Power.irc.network.server.util.JsonUtil;

public class C05PacketUserWdr extends ClientPacket implements Packet {
	
	public C05PacketUserWdr(String wdrname) {
	    this.content = wdrname;
        this.packetType = PacketType.C05;
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
