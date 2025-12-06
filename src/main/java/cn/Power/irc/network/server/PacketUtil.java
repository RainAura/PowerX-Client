package cn.Power.irc.network.server;

import cn.Power.irc.network.server.packets.ClientPacket;
import cn.Power.irc.network.server.packets.ServerPacket;
import cn.Power.irc.network.server.util.JsonUtil;
import cn.Power.irc.network.server.util.encryption.EncryptionUtils;

public class PacketUtil {

	public static String getClientJson(ClientPacket p) {
		return JsonUtil.toJson(p);
	}

	public static String getServerJson(ServerPacket p) {
		return JsonUtil.toJson(p);
	}

	public static ServerPacket parsePacketServer(String j, Class cls) {
		return (ServerPacket) JsonUtil.parseJson(j, cls);
	}

	public static ClientPacket parsePacketClient(String j, Class cls) {
		return (ClientPacket) JsonUtil.parseJson(j, cls);
	}

	public static String SendLoginClientPacket(ClientPacket packet) {
		String msg = PacketUtil.getClientJson(packet);
//		return "/PACKET" + Base64Util.encode(msg);
		return "/USER" + EncryptionUtils.Encode(msg)+ "gg";
	}
	
	public static String SendClientPacket(ClientPacket packet) {
		String msg = PacketUtil.getClientJson(packet);
//		return "/PACKET" + Base64Util.encode(msg);
		return "/PACKET" + EncryptionUtils.Encode(msg);
	}

	public static String SendServerPacket(ServerPacket packet) {
		String msg = PacketUtil.getServerJson(packet);
//		return "/SERVER" + Base64Util.encode(msg);
		return "/SERVER" + EncryptionUtils.Encode(msg);
	}
    
}
