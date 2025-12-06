package cn.Power.mod.mods.WORLD;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.common.primitives.Chars;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.command.commands.CommandSpammer;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.misc.ChatUtil;
import cn.Power.util.timeUtils.TimeHelper;
import io.netty.util.internal.ThreadLocalRandom;

public class Spammer extends Mod {

	TimeHelper timer = new TimeHelper();
	private Value<Double> delay = new Value<Double>("Spammer_Delay", Double.valueOf(1.0D), Double.valueOf(0.1D),
			Double.valueOf(10.0D), 0.1D);
	private Value<Double> random = new Value<Double>("Spammer_Random", Double.valueOf(6D), Double.valueOf(1D),
			Double.valueOf(36D), 1D);
	
	public static ArrayList<String> messages = new ArrayList<String>();

	public Spammer() {
		super("Spammer", Category.WORLD);

	}

	@Override
	public void onEnable() {
		
		try {
			CommandSpammer.loadMessage();
			;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		if(messages.isEmpty()) {
			ChatUtil.printChat("Please add messages by -spam <message u want> (%s == Random Strings)");

			EventManager.unregister(this);
			this.set(false);
		}
	}
	
	@EventTarget
	public void onUpdate(EventUpdate e) {
		if (this.timer.isDelayComplete(((Double) this.delay.getValueState()).longValue() * 1000L) && !messages.isEmpty()) {
				
			this.mc.thePlayer.sendChatMessage(messages.get(ThreadLocalRandom.current().nextInt(messages.size())).replace("%s", getRandomString(1 + ThreadLocalRandom.current().nextInt(random.getValueState().intValue()))));
			timer.reset();
		}
	}

	public static String getRandomString(double d) {
		String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < d; i++) {
			int number = random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
}