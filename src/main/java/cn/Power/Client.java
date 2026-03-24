package cn.Power;

import de.florianmichael.viamcp.ViaMCP;
import org.lwjgl.opengl.Display;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.command.CommandManager;
import cn.Power.events.EventPacket;
import cn.Power.events.EventRespawn;
import cn.Power.events.EventUpdate;
import cn.Power.irc.IRCManager;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.PLAYER.Blink;
import cn.Power.mod.mods.PLAYER.Freecam;
import cn.Power.mod.mods.WORLD.ChatCommands;
import cn.Power.mod.mods.WORLD.Xray;
import cn.Power.notification.Notification.Type;
import cn.Power.notification.NotificationManager;
import cn.Power.ui.Gui.GuiInvManager;
import cn.Power.ui.NewClickGui.NewClickGui;
import cn.Power.ui.login.AltManager;
import cn.Power.util.FileUtil;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.friendManager.FriendManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;

public class Client {
	public static String CLIENT_name = "Power";
	public final static String CLIENT_File = "Power";
	public final static String CLIENT_NAME2 = "Exhibition";
	public static String ClientCode = "PowerX";
	public static String ClientUser = System.getProperty("px_user", "\247cI");

    public static String Client_Name = "PowerX";
    public static String Client_Rank = System.getProperty("px_rank", "Dev");
	public static String HypixelKey = "SycbbY9J1yN8YEride6dw3N71SZMKGhA";
	public final static String CLIENT_VER = "X";
	public final static String CLIENT_Bulid = "211118";
	public static double CLEINT_VERSION = 20.1;
	public boolean BedWars;
	public static boolean loadConfig = false;
	public static boolean isBeta = false;
	public static Client instance = new Client();
	public static float delta;
	public NotificationManager notificationmgr = new NotificationManager();
	public AltManager altmanager;
	public ModManager modMgr;
	public FileUtil fileMgr;
	public static int taskbarprogress;
	public static NewClickGui Newclickface;
	public CommandManager cmdMgr;
	public static int lastSlot = -1;
	public static boolean blockActionsForHealing;

	public static boolean doDoubleRejoin;

    public IRCManager IRC;

//    public static WaypointManager wm = new WaypointManager();

//	public static Value<String> Array_Font = new Value("Global", "Client Font", 0);

	public final static Value<Double> r = new Value("Global_Client Color Red", 255d, 0d, 255d, 0.1d);
	public final static Value<Double> g = new Value("Global_Client Color Green", 255d, 0d, 255d, 0.1d);
	public final static Value<Double> b = new Value("Global_Client Color Blue", 255d, 0d, 255d, 0.1d);
	public final static Value<Double> alpha = new Value("Global_Client Color Alpha", 160d, 0d, 255d, 0.1d);

	public final static Value<Double> blurIntensity = new Value("Global_Blur Intensity", 4d, 1d, 20d, 1d);
	public final static Value<Double> ChatBackGround = new Value("Global_ChatBackGround Intensity", 110d, 0d, 243d, 1d);

	public final static Value<Boolean> tpdamage = new Value("Global_ TPDamage", true);

	public final static Value<Boolean> Rainbow = new Value("Global_Client Rainbow", true);

	public final static Value<Boolean> score = new Value("Global_No Score", false);
	public final static Value<Boolean> Cape = new Value("Global_Cape", true);
	public final static Value<Boolean> Sound = new Value("Global_Toggle Sound", true);
	public final static Value<Boolean> KeepCamera = new Value("Global_Keep Camera", false);
	public final static Value<Boolean> BlockEffect = new Value("Global_No Block Effect", false);
	public final static Value<Boolean> Notification = new Value("Global_Notification", true);
	public final static Value<Boolean> AutoRejoin = new Value("Global_AutoRejoin", true);
	
	public final static Value<Double> TargetHudOffsetX = new Value("Global_TargetHudOffsetX", 0d, 0d, 200d, 1d);
	public final static Value<Double> TargetHudOffsetY = new Value("Global_TargetHudOffsetY", 0d, 0d, 243d, 1d);

	public final static Value<Double> TargetHudOffsetY2 = new Value("Global_TargetHudOffsetY2", 0d, -100d, 100d, 1d);

	
//	public static Value<Boolean> NoShader = new Value("Global_Disable Blur", false);

	@native0
	public void onClientStart() {
		if (this.ClientUser.toLowerCase().contains("\247cbeta") || this.ClientUser.toLowerCase().contains("\247cdev")) {
			this.isBeta = true;
		}
		modMgr = new ModManager();
		Newclickface = new NewClickGui();
		cmdMgr = new CommandManager();
		altmanager = new AltManager();
		AltManager.init();
		AltManager.setupAlts();
		fileMgr = new FileUtil();

		GuiInvManager.loadConfig();
        IRC = new IRCManager();
		Display.setTitle((String) CLIENT_name + " " + CLIENT_VER);
		EventManager.register(new SkyBlockUtils());
		EventManager.register(this);
		if (ModManager.getModByClass(Xray.class).isEnabled()) {
			ModManager.getModByClass(Xray.class).set(false);
		}
		if (ModManager.getModByClass(Blink.class).isEnabled()) {
			ModManager.getModByClass(Blink.class).set(false);
		}
		if (!ModManager.getModByClass(ChatCommands.class).isEnabled()) {
			ModManager.getModByClass(ChatCommands.class).set(true);
		}
		if (ModManager.getModByClass(Freecam.class).isEnabled()) {
			ModManager.getModByClass(Freecam.class).set(false);
		}
		try {
			ViaMCP.create();
			ViaMCP.INSTANCE.initAsyncSlider();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventTarget
	private void packet(EventPacket e) {
		if (e.getEventType() == EventType.RECEIVE) {
			if (e.getPacket() instanceof S02PacketChat) {
				S02PacketChat pk = (S02PacketChat) e.getPacket();

		//		 System.out.println(pk.getChatComponent().getUnformattedText());

				if (AutoRejoin.getValueState()) {

					if (pk.getChatComponent().toString().startsWith("TextComponent{text='Flying or related.',")
							|| pk.getChatComponent().toString().startsWith("TextComponent{text='Hovering?',")
				|| pk.getChatComponent().toString().startsWith("TextComponent{text='A Kick occ")) {

						Client.instance.getNotificationManager()
								.addNotification("\2476AutoRejoin\2477 : \247cRejoinNow", 5000, Type.INFO);

						Minecraft.thePlayer.sendChatMessage("/rejoin");

					} else if (pk.getChatComponent().toString()
							.startsWith("TextComponent{text='You were spawned in Limbo.',")) {

						Client.instance.getNotificationManager()
								.addNotification("\2476AutoRejoin\2477 : \247cRejoinNow", 5000, Type.INFO);

						Minecraft.thePlayer.sendChatMessage("/lobby");
						Minecraft.thePlayer.sendChatMessage("/back");

						Minecraft.thePlayer.sendChatMessage("/rejoin");

						doDoubleRejoin = true;

					} 
				}
				

				if (!BedWars && (pk.getChatComponent().toString()
						.startsWith("TextComponent{text='起床战争', siblings=[], style=Style{hasParent=true, color=§f")
						|| pk.getChatComponent().toString().startsWith(
								"TextComponent{text='Bed Wars', siblings=[], style=Style{hasParent=true, color=§f")
						|| pk.getChatComponent().toString().startsWith(
								"TextComponent{text='床戰', siblings=[], style=Style{hasParent=true, color=§f"))) {
//					Client.instance.getNotificationManager().addNotification(
//							"\2476BedWars\2477 : \247cUsing BedFucker u need to point to the bed", 15000,
//							Type.INFO);
					BedWars = true;
				}
			} else if (e.getPacket() instanceof S45PacketTitle) {
				S45PacketTitle p = (S45PacketTitle) e.getPacket();

				if (p.getMessage() != null) {
					if (p.getMessage().toString().startsWith("TextComponent{text='SYNCING WORLD'")) {

						Minecraft.thePlayer.motionX = 0;
						Minecraft.thePlayer.motionY = 0;
						Minecraft.thePlayer.motionZ = 0;
					}
				}

			}

		} else if (e.getPacket() instanceof C09PacketHeldItemChange) {

			C09PacketHeldItemChange packet = (C09PacketHeldItemChange) e.getPacket();
			Client.lastSlot = packet.getSlotId();
		}

		if (Minecraft.getMinecraft().ingameGUI != null && !Minecraft.getMinecraft().ingameGUI.displayedTitle.isEmpty()
				&& Minecraft.getMinecraft().ingameGUI.displayedTitle.contains("SYNCING WORLD")) {

			Minecraft.thePlayer.motionX = 0;
			Minecraft.thePlayer.motionY = 0;
			Minecraft.thePlayer.motionZ = 0;
		}
	}

	  @EventTarget
	    public void onHorse(EventPacket e) {
		  if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
			  C08PacketPlayerBlockPlacement fml = (C08PacketPlayerBlockPlacement) e.getPacket();
				System.err.println(fml.position);
//				if(fml.channel().contains("REGISTER"))
//				e.setCancelled(true);
			}
		  
	            if (e.getPacket() instanceof C02PacketUseEntity) {
	                C02PacketUseEntity pk = (C02PacketUseEntity) e.getPacket();
	                if (pk.getAction() == C02PacketUseEntity.Action.ATTACK && pk.getEntityFromWorld(Minecraft.getMinecraft().theWorld) instanceof EntityPlayer && Minecraft.getMinecraft().thePlayer.getHeldItem() != null) {
	                    EntityPlayer player = (EntityPlayer) pk.getEntityFromWorld(Minecraft.getMinecraft().theWorld);
	                    if (FriendManager.isIRCMOD(player) && player.isSneaking()) {
	                        e.setCancelled(true);
	                    }
	                }
	            }
	        }
	  
	@EventTarget
	private void Respawn(EventRespawn e) {

		Client.lastSlot = Minecraft.thePlayer.inventory.currentItem;


		BedWars = false;
	}

	@EventTarget
	private void updata(EventUpdate e) {
		
		if (doDoubleRejoin && Minecraft.theWorld.getScoreboard() != null
				&& Minecraft.theWorld.getScoreboard().teams != null
				&& Minecraft.theWorld.getScoreboard().teams.containsKey("team_1")) {

			for (int i = 0; i < 2; i++)
				Minecraft.thePlayer.sendChatMessage("/rejoin");

			Minecraft.thePlayer.sendChatMessage("/back");

			doDoubleRejoin = false;

		}
	}

	public static Client getInstance() {
		return instance;
	}

	public NewClickGui NewClickGui() {
		return Client.Newclickface;
	}

	public AltManager getAltManager() {
		return this.altmanager;
	}

	public NotificationManager getNotificationManager() {
		return notificationmgr;
	}
}
