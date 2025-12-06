package cn.Power.irc.network.server.packets;

public interface Packet {
    String getJson();

    ClientPacket parsePacket(String j);

}
