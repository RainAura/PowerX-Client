package net.minecraft.client.gui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cn.Power.Client;
import cn.Power.irc.network.server.PacketUtil;
import cn.Power.irc.network.server.packets.client.C03PacketHypixelban;
import cn.Power.util.GetBan;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class GuiDisconnected extends GuiScreen {
	private String reason;
	private String key;
	private IChatComponent message;
	private List<String> multilineMessage;
	private final GuiScreen parentScreen;
	private int field_175353_i;
	private String[] FromApi;
	private JsonObject array;

	public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
		this.parentScreen = screen;
		this.reason = I18n.format(reasonLocalizationKey);
		this.key = reasonLocalizationKey;
		this.message = chatComp;
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}
	
	boolean needRetry = false;

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		this.buttonList.clear();
		this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(),
				this.width - 50);
		this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100,
				this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT,
				I18n.format("gui.toMenu")));

		if(this.message.toString().indexOf("#") != -1)
		CompletableFuture.runAsync(() -> {
			try {
				String p = this.message.toString();

				int i = p.indexOf("#") + 1;

				int i2;

				for (i2 = i; p.charAt(i2) != '\''; i2++) {

				}

				String BanID;
				System.out.println(BanID = p.substring(i, i2).trim());

				String string411 = "https://hypixel.net/api/players/"
						+ Minecraft.getMinecraft().getSession().getPlayerID().replace("-", "") + "/ban/" + BanID;
				CloseableHttpClient closeableHttpClient11 = HttpClients.createDefault();
				HttpGet httpGet11 = new HttpGet(string411);
				httpGet11.setHeader("user-agent",
						"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36");
				httpGet11.setHeader("xf-api-key", "lQnlsEl1D9txtJqEVWVgnr1hNjmvTZa1");

				// lQnlsEl1D9txtJqEVWVgnr1hNjmvTZa1

				String string5 = null;
				try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient11.execute((HttpUriRequest) httpGet11)) {
					string5 = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
				} catch (IOException iOException) {
					iOException.printStackTrace();
				}

				if (string5 == null)
					return;
				
				if(string5.contains("too_many_requests"))
					needRetry = true;

				Gson gson = new Gson();
				array = gson.fromJson(string5.trim(), JsonObject.class);
				FromApi = string5.split("\n");
				System.out.println(string5);
				String bancheck = array.get("punishmentCategory").getAsString();
				if(bancheck != null) {
	                 String UUID = Minecraft.getMinecraft().getSession().getProfile().getId().toString().replace("-", "");
	                 String BanType = ("Unknow"); 
	                 if (bancheck.endsWith("hacks")) {
	                	 BanType = ("WATCHDOG");
	                 } else if (bancheck.endsWith("other")) {
	                	 BanType = ("BLACKLISTED_MODIFICATIONS");
	                 }
	                 if(key.contains("disconnect.lost") && !Client.instance.IRC.cc.isClosed()) 
	                 Client.instance.IRC.cc.send(PacketUtil.SendClientPacket(new C03PacketHypixelban(BanType + ":" + BanID + ":" + UUID)));
				}

				
			} catch (Throwable c) {
				c.printStackTrace();
			}
		});
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(this.parentScreen);
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2,
				this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
		int i = this.height / 2 - this.field_175353_i / 2;

		if (this.multilineMessage != null) {
			for (String s : this.multilineMessage) {

				String res = "";

				try {
					if (array != null) {

						try {
							if (array.has("date") && !array.get("date").getAsString().isEmpty()) {
								
								String result2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.format(array.get("date").getAsLong());

								res += "[封禁日期] " + result2;
							}
						} catch (Throwable c) {
							c.printStackTrace();
						}
						try {
							if (array.has("punishmentCategory")) {

								String s2 = array.get("punishmentCategory").getAsString();
								
								res += "  " + (s2.contains("hacks") ? "[WatchDog]": (s2.contains("other") ? "[Mod]" : "[Unknow]"));
							}
						} catch (Throwable c) {
							c.printStackTrace();
						}

					}
				} catch (Throwable c) {
					c.printStackTrace();
				}

				if (s.contains("Reason: ")) {
					
					if(res.isEmpty())
						res = needRetry ? "获取频繁, 请重试" : "尝试获取中";
					
					this.drawCenteredString(this.fontRendererObj, res, this.width / 2, i, 16777215);

					i += this.fontRendererObj.FONT_HEIGHT;
				}

				this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
				i += this.fontRendererObj.FONT_HEIGHT;

			}

			i += this.fontRendererObj.FONT_HEIGHT * 3.7;

			if (this.FromApi != null) {

				for (String p : this.FromApi)
					this.drawCenteredString(this.fontRendererObj, p, this.width / 2,
							i += this.fontRendererObj.FONT_HEIGHT, 16777215);
			} else {
				this.drawCenteredString(this.fontRendererObj, "Please Wait for the Result...", this.width / 2, i,
						16777215);
			}
		}

		/*
		 * 
		 * 
		 */

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
