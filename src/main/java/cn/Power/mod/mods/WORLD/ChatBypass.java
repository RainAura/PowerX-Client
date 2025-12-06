package cn.Power.mod.mods.WORLD;

import java.util.Arrays;
import java.util.stream.Stream;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class ChatBypass extends Mod {
	public Value mode = new Value("ChatBypass", "ChatMode", 0);
	private int randbypass = 0;

	public ChatBypass() {
		super("ChatBypass", Category.WORLD);
		this.mode.addValue("Insert");
	}

	@EventTarget
	public void onChat(EventUpdate e) {
		this.setDisplayName(this.mode.getModeAt(this.mode.getCurrentMode()));
	}

	@EventTarget
	private void pub(EventPacket e) {
		if (e.getPacket() instanceof C01PacketChatMessage) {
			C01PacketChatMessage chat = (C01PacketChatMessage) e.getPacket();
			final int a2 = 1;
			final String v1 = chat.getMessage();
			final String[] v2 = v1.split(" ");
			final String v3 = v2[0];
			if (v1.toLowerCase().equalsIgnoreCase("gg") || v1.toLowerCase().equalsIgnoreCase("/ac gg"))
				return;
			Label_0315: {
				if (v1.startsWith("/")) {
					final String[] array = new String[10];
					final int n = 0;
					array[n] = "/r";
					array[1] = "/shout";
					array[2] = "/msg";
					array[3] = "/m";
					array[4] = "/tell";
					array[5] = "/whisper";
					array[6] = "/w";
					array[7] = "/ac";
					array[8] = "/pc";
					array[9] = "/gc";
					if (Stream.of(array).anyMatch(v3::equalsIgnoreCase)) {
						if (Stream.of(new String[] { "/r", "/shout", "/ac", "/pc", "/gc" })
								.anyMatch(v3::equalsIgnoreCase)) {
							chat.message = v2[0] + " " + this
									.bypassMsg(String.join(" ", (CharSequence[]) Arrays.copyOfRange(v2, 1, v2.length)));
							if (a2 != 0) {
								break Label_0315;
							}
						}
						if (v2.length > 1) {
							chat.message = v2[0] + " " + v2[1] + " " + this
									.bypassMsg(String.join(" ", (CharSequence[]) Arrays.copyOfRange(v2, 2, v2.length)));
						}
					}
				}
			}
			if (!chat.message.startsWith("/") && !chat.message.startsWith("-")) {
				chat.message = this.bypassMsg(v1);
			}
		}

	}

	private String bypassMsg(String v1) {
		final StringBuilder stringBuilder = new StringBuilder();
		int v3 = this.mode.getCurrentMode();
		for (char c : v1.toCharArray()) {
			if (c >= 33 && c <= 65535) {
				randbypass++;
				stringBuilder.append(c + rand());
			} else {
				stringBuilder.append(c);
			}
		}
		return stringBuilder.toString();
	}

	private String rand() {
		switch (randbypass) {
		case 0: {
			return "\u0379";
		}
		case 1: {
			return "\u0378";
		}
		case 2: {
			return "\u0381";
		}
		case 3: {
			return "\u0379";
		}
		case 4: {
			return "\u0382";
		}
		case 5: {
			randbypass = 0;
			return "\u0383";
		}
		}
		return "\u0378";

	}
}
