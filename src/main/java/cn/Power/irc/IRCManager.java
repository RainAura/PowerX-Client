package cn.Power.irc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.authlib.GameProfile;

import cn.Power.Client;
import cn.Power.native0;
import cn.Power.events.EventPacket;
import cn.Power.events.EventTick;
import cn.Power.irc.network.server.PacketUtil;
import cn.Power.irc.network.server.data.User;
import cn.Power.irc.network.server.packets.PacketType;
import cn.Power.irc.network.server.packets.ServerPacket;
import cn.Power.irc.network.server.packets.client.C00PacketConnect;
import cn.Power.irc.network.server.packets.client.C03PacketHypixelban;
import cn.Power.irc.network.server.packets.client.C04PacketData;
import cn.Power.irc.network.server.packets.client.C05PacketUserWdr;
import cn.Power.irc.network.server.packets.server.S01PacketConnect;
import cn.Power.irc.network.server.packets.server.S02PacketKick;
import cn.Power.irc.network.server.packets.server.S03PacketChat;
import cn.Power.irc.network.server.packets.server.S04PacketData;
import cn.Power.irc.network.server.packets.server.S05PacketDisconnect;
import cn.Power.irc.network.server.packets.server.S06PacketUserList;
import cn.Power.irc.network.server.packets.server.S07PacketCommand;
import cn.Power.irc.network.server.packets.server.S08PacketExit;
import cn.Power.irc.network.server.util.encryption.EncryptionUtils;
import cn.Power.util.GetBan;
import cn.Power.util.misc.ChatUtil;
import cn.Power.util.timeUtils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class IRCManager {

	public CopyOnWriteArrayList<User> userlist = new CopyOnWriteArrayList<User>();	
	
	public String IRC_local = "ws://localhost:8887";

	@native0
	public String IRC_HostName = "wss://foodbyte.top:443";

	public WebSocketClient cc;

	public User user;
	
	public TimerUtil timer = new TimerUtil();
	
	public IRCManager() {
		this.init();
		EventManager.register(this);
	}

	@native0
	public void init() {
		try {
			cc = new WebSocketClient(new URI(IRC_HostName + PacketUtil.SendLoginClientPacket(new C00PacketConnect(user = new User(Client.Client_Name,Client.Client_Rank,Client.ClientUser,Minecraft.getMinecraft().getSession().getProfile().getName()))))) {
				@Override
				public void onMessage(String message) {
					if (message.startsWith("/SERVER")) {
						String PACKET = message;
						PACKET = EncryptionUtils.Decode(PACKET.replace("/SERVER", ""));
						ServerPacket sp = PacketUtil.parsePacketServer(PACKET, ServerPacket.class);
						if (sp.packetType == PacketType.S01) {
							S01PacketConnect S01 = (S01PacketConnect) PacketUtil.parsePacketServer(PACKET,S01PacketConnect.class);
							Client.instance.HypixelKey = S01.content;
						} else if (sp.packetType == PacketType.S02) {
							S02PacketKick S02 = (S02PacketKick) PacketUtil.parsePacketServer(PACKET,S02PacketKick.class);
							ChatUtil.sendIRCToPlayer(S02.content);
						} else if (sp.packetType == PacketType.S03) {
							S03PacketChat S03 = (S03PacketChat) PacketUtil.parsePacketServer(PACKET,S03PacketChat.class);
							ChatUtil.sendIRCToPlayer(S03.content);
						} else if (sp.packetType == PacketType.S04) {
							 S04PacketData S04 = (S04PacketData) PacketUtil.parsePacketServer(PACKET, S04PacketData.class);
	                            switch (S04.content) {
	                                case "/set":
	                                    if (S04.user.name.equals(user.name) && S04.user.loginTime == user.loginTime) {
	                                        user.client = S04.user.client;
	                                        user.name = S04.user.name;
	                                        user.gameID = S04.user.gameID;
	                                        user.hide = S04.user.hide;
	                                        user.MT = S04.user.MT;
	                                    }
	                                    userlist.forEach(user -> {
	                                        if (S04.user.name.equals(user.name) && S04.user.loginTime == user.loginTime) {
	                                            user.client = S04.user.client;
	                                            user.name = S04.user.name;
	                                            user.gameID = S04.user.gameID;
	                                            user.hide = S04.user.hide;
	                                            user.MT = S04.user.MT;
	                                        }
	                                    });
	                                    break;
	                                case "/add":
	                                    userlist.add(S04.user);
	                                    break;
	                                case "/remove":
	                                    userlist.removeIf(user -> (S04.user.name.equals(user.name) && S04.user.loginTime == user.loginTime));
	                                    break;
	                                case "/data":
	                                    userlist.forEach(user -> {
	                                        if (S04.user.name.equals(user.name) && S04.user.loginTime == user.loginTime) {
	                                            Client.instance.IRC.user = S04.user;
	                                        }
	                                    });
	                                    break;
	                                case "/hide":
	                                    userlist.forEach(user -> {
	                                        if (S04.user.name.equals(user.name) && S04.user.loginTime == user.loginTime) {
	                                            user.hide = S04.user.hide;
	                                        }
	                                    });
	                                    break;
	                                default:
	                                    userlist.forEach(user -> {
	                                        if (S04.user.name.equals(user.name) && S04.user.loginTime == user.loginTime) {
	                                            user.gameID = S04.content;
	                                        }
	                                    });
	                                    break;
	                            }
						} else if (sp.packetType == PacketType.S05) {
							S05PacketDisconnect S05 = (S05PacketDisconnect) PacketUtil.parsePacketServer(PACKET,S05PacketDisconnect.class);
							if(Minecraft.getMinecraft().getCurrentServerData() != null)
							Minecraft.getMinecraft().getNetHandler().getNetworkManager().closeChannel(new ChatComponentText(S05.content));
						} else if (sp.packetType == PacketType.S06) {
							S06PacketUserList S06 = (S06PacketUserList) PacketUtil.parsePacketServer(PACKET,S06PacketUserList.class);
							userlist = new CopyOnWriteArrayList<User>(Arrays.asList(S06.UserList));
						} else if (sp.packetType == PacketType.S07) {
							S07PacketCommand S07 = (S07PacketCommand) PacketUtil.parsePacketServer(PACKET,S07PacketCommand.class);
							if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer != null)
								Minecraft.getMinecraft().thePlayer.sendChatMessage(S07.content);
						 } else if (sp.packetType == PacketType.S08) {
	                            S08PacketExit S08 = (S08PacketExit) PacketUtil.parsePacketServer(PACKET, S08PacketExit.class);
	                            if (S08.content.equalsIgnoreCase("kick")) {
	                                Minecraft.getMinecraft().shutdown();
	                            }
	                        }
					}
				}

				@Override
				public void onOpen(ServerHandshake handshake) {
					
				}

				@Override
				public void onClose(int code, String reason, boolean remote) {
					System.out.println("You have been disconnected from: " + " " + reason + code);
					if (code == CloseFrame.TRY_AGAIN_LATER) {
						waskick();
					}
					timer.reset();
					userlist.clear();
					ChatUtil.sendIRCToPlayer("IRC to disconnect.....");
				}

				@Override
				public void onError(Exception ex) {
					System.out.println("Exception occurred ...\n" + ex);
//					ChatUtil.sendIRCToPlayer("IRC Exception occurred ...\n" +ex);
					ex.printStackTrace();
				}

				@Override
				public String set_Cf_IP(String s) {
					return null;
				}

				@Override
				public String get_Cf_IP() {
					return null;
				}
			};
			this.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	public void connect() {
		cc.connect();
	}
	
    public void waskick() {
        EventManager.unregister(this);
    }
	
    @EventTarget
    public void JoinServer(EventPacket e) {
        if (e.getPacket() instanceof S07PacketRespawn || e.getPacket() instanceof S02PacketLoginSuccess) {
                timer.setTime(29000);
        }
        if (e.getPacket() instanceof C01PacketChatMessage) {
        	C01PacketChatMessage c01 = (C01PacketChatMessage)e.getPacket();
        	String message = c01.getMessage();
        	if(message.toLowerCase().startsWith("/wdr")) {
        		String[] cmd = message.split(" ");
        		if(cmd.length >= 3) {
        			cc.send(PacketUtil.SendClientPacket(new C05PacketUserWdr(cmd[1])));
        		}
        	}
        }
    }
	
    @EventTarget
    public void UserUpdate(EventTick e) {
        if (!cc.isClosed()) {
            if (timer.hasTimeElapsed(30000, true)) {
                String nickname = Minecraft.getMinecraft().getSession().getUsername();
                if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null)
                    for (final NetworkPlayerInfo networkPlayerInfo : Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap()) {
                        GameProfile gameProfile = networkPlayerInfo.getGameProfile();
                        if (gameProfile.getId() != null && gameProfile.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId())) {
                            if (gameProfile.getName().equalsIgnoreCase(Minecraft.getMinecraft().getSession().getProfile().getName()))
                                continue;
                            nickname = gameProfile.getName();
                        }
                    }
                user.gameID = nickname;
                user.lastTime = System.currentTimeMillis();
                cc.send(PacketUtil.SendClientPacket(new C04PacketData(new User(Client.Client_Name,Client.Client_Rank,Client.ClientUser, nickname), "Update")));
            }
        } else {
            if (timer.hasTimeElapsed(120000, true)) {
                init();
            }
        }
    }
    
}
