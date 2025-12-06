package cn.Power.mod;

import java.awt.Color;

import com.darkmagician6.eventapi.EventManager;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.notification.Notification;
import cn.Power.util.ClientUtil;
import cn.Power.util.Colors;
import cn.Power.util.animations.easings.utilities.Progression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class Mod {

	public final static ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
	public final static Minecraft mc = Minecraft.getMinecraft();
	public Value showValue;
	private String name;
	private int color1;
	private int key;
	private Category category;
	private boolean isEnabled;
	private String desc;
	public boolean openValues;
	public double arrowAnlge;
	public double animateX;
	public double hoverOpacity;
	public float circleValue;
	public boolean canSeeCircle;
	public int[] circleCoords;
	public boolean clickgui;
	public String displayName = "";
	public float posX;

	public boolean HideMod;

	private double animX, animY;
	private final Progression moduleProgressionX;
	private final Progression moduleProgressionY;

	public Mod(String name, Category category) {
		this.arrowAnlge = 0.0;
		this.animateX = 0.0;
		this.hoverOpacity = 0.0;
		this.name = name;
		this.key = 0;
		this.category = category;
		this.circleCoords = new int[2];
		this.moduleProgressionX = new Progression();
		this.moduleProgressionY = new Progression();
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		String suffix = displayName.toString();
		if (suffix.isEmpty()) {
			this.displayName = suffix;
		} else {
			this.displayName = String.format(" %s", EnumChatFormatting.GRAY + displayName);

		}
	}

	public void onEnable() {

	}

	public void onDisable() {

	}

	public void onToggle() {
	}

	public void disableValues() {
	}

	public void toggle() {
		try {
			if (this.isEnabled()) {
				this.set(false);
			} else {
				this.set(true);
			}
		} catch (Exception var2) {
			var2.printStackTrace();
		}

	}

	public String getValue() {
		if (this.showValue == null) {
			return "";
		}
		return this.showValue.isValueMode ? this.showValue.getModeAt(this.showValue.getCurrentMode())
				: String.valueOf(this.showValue.getValueState());
	}

	public void set(boolean state) {
		this.set(state, false);
		Client.instance.fileMgr.saveMods();
	}

	public void set(boolean state, boolean safe) {

		this.isEnabled = state;
		
		
		if (state) {
			if (this.mc.theWorld != null) {
				try {
					if (!clickgui && Client.Notification.getValueState()) {
						Client.instance.getNotificationManager().addNotification(this.getName() + "\247a Enabled", 500,
								Notification.Type.SUCCESS);
					} else if (!clickgui && Client.Sound.getValueState().booleanValue()) {
						Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 1.0F);
					}
				} catch (Throwable c) {
					c.printStackTrace();
				}
				try {

					this.onEnable();
				} catch (Throwable c) {
					c.printStackTrace();
				}
			}
			EventManager.register(this);
		} else {

			if (this.mc.theWorld != null) {
				try {
					if (!clickgui && Client.Notification.getValueState()) {
						Client.instance.getNotificationManager().addNotification(this.getName() + "\247c Disabled", 500,
								Notification.Type.ERROR);
					} else if (!clickgui && Client.Sound.getValueState().booleanValue()) {
						Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 0.8F);
					}

				} catch (Throwable c) {
					c.printStackTrace();
				}

				EventManager.unregister(this);
				

				try {
					this.onDisable();
				} catch (Throwable c) {
					c.printStackTrace();
				}
				
				
			}

		}
		if (safe) {
			Client.instance.fileMgr.saveMods();
		}
		


		this.onToggle();
		this.moduleProgressionX.setValue(0);
		this.moduleProgressionY.setValue(0);
	}

	public String getName() {
		return this.name;
	}

	public void setColor(int color) {
		this.color1 = color;
	}

	public int getKey() {
		return this.key;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Category getCategory() {
		return this.category;
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void cooldown(int time) {
		this.set(false);
		new Thread(() -> {
			try {
				Thread.sleep(time);
			} catch (InterruptedException ex) {
			}
			if (!this.isEnabled()) {
				this.set(true);
			}
		}).start();
	}

	public boolean hasValues() {
		for (Value value : Value.list) {
			String name = value.getValueName().split("_")[0];
			if (!name.equalsIgnoreCase(this.getName()))
				continue;
			return true;
		}
		return false;
	}

	public void portMove(float yaw, float multiplyer, float up) {
		double moveX = -Math.sin(Math.toRadians((double) yaw)) * (double) multiplyer;
		double moveZ = Math.cos(Math.toRadians((double) yaw)) * (double) multiplyer;
		double moveY = (double) up;
		this.mc.thePlayer.setPosition(moveX + this.mc.thePlayer.posX, moveY + this.mc.thePlayer.posY,
				moveZ + this.mc.thePlayer.posZ);
	}

	public void move(float yaw, float multiplyer, float up) {
		double moveX = -Math.sin(Math.toRadians((double) yaw)) * (double) multiplyer;
		double moveZ = Math.cos(Math.toRadians((double) yaw)) * (double) multiplyer;
		this.mc.thePlayer.motionX = moveX;
		this.mc.thePlayer.motionY = (double) up;
		this.mc.thePlayer.motionZ = moveZ;
	}

	public void move(float yaw, float multiplyer) {
		double moveX = -Math.sin(Math.toRadians((double) yaw)) * (double) multiplyer;
		double moveZ = Math.cos(Math.toRadians((double) yaw)) * (double) multiplyer;
		this.mc.thePlayer.motionX = moveX;
		this.mc.thePlayer.motionZ = moveZ;
	}

	public void setAnimX(double animX) {
		this.animX = animX;
	}

	public void setAnimY(double animY) {
		this.animY = animY;
	}

	public double getAnimX() {
		return animX;
	}

	public double getAnimY() {
		return animY;
	}

	public Progression getModuleProgressionX() {
		return moduleProgressionX;
	}

	public Progression getModuleProgressionY() {
		return moduleProgressionY;
	}
}
