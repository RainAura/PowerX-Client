package cn.Power.mod.mods.RENDER.TabGui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.events.EventKeyboard;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.RENDER.Hud;
import cn.Power.ui.CFont.CFontRenderer;
import cn.Power.ui.CFont.FontLoaders;
import cn.Power.util.RenderUtils;

public class TABGUI {

	static CFontRenderer font = FontLoaders.kiona28;
	public static float YPort = font.getHeight() + 7.0F;;

	public ArrayList categoryValues = new ArrayList();
	public int currentCategoryIndex = 0;
	public int currentModIndex = 0;
	public int currentSettingIndex = 0;
	public int screen = 0;

	public void renderTabgui() {
		this.categoryValues.addAll(Arrays.asList(Category.values()));
		int[] var12 = new int[1];
		byte var14;
		var14 = 0;
		byte var15 = 2;
		int var16 = (int) YPort + 2;
		RenderUtils.drawBorderedRect((float) var15, (float) var16, (float) (var15 + this.getWidestCategory() + 3),
				(float) (var16 + this.categoryValues.size() * 11), 0.1F, Integer.MIN_VALUE,
				(new Color(0, 0, 0, 255)).getRGB());
		for (Iterator var19 = this.categoryValues.iterator(); var19.hasNext(); ++var12[0]) {
			Category var17 = (Category) var19.next();
			int var13;
			var13 = -1;

			if (this.getCurrentCategorry().equals(var17)) {
				RenderUtils.drawGradientSideways((float) var15 + 0.3, (float) var16 + 0.3,
						(float) (var15 + this.getWidestCategory() + 3) - 0.3, (float) (var16 + 9 + 2) - 0.3,
						Hud.getColor(), Integer.MIN_VALUE);
			}

			String var21 = var17.name();
			FontLoaders.tahoma18.drawStringWithShadow1(
					var21.substring(0, 1).toUpperCase() + var21.substring(1, var21.length()).toLowerCase(),
					(float) (var15 + 2), (float) var16 + (float) var14 * 1.5F + 1.5, var13);
			var16 += 11;
		}

		if (this.screen == 1 || this.screen == 2) {
			int var18 = var15 + this.getWidestCategory() + 6;
			int var20 = 21 + this.currentCategoryIndex * 11;

			RenderUtils.drawBorderedRect((float) var18, (float) var20, (float) (var18 + this.getWidestMod() + 3),
					(float) (var20 + this.getModsForCurrentCategory().size() * 11), 0.1F, Integer.MIN_VALUE,
					(new Color(0, 0, 0, 50)).getRGB());

			for (Iterator var23 = this.getModsForCurrentCategory().iterator(); var23.hasNext(); var20 += 11) {
				Mod var22 = (Mod) var23.next();
				if (this.getCurrentModule().equals(var22)) {
					RenderUtils.drawGradientSideways((float) var18 + 0.3, (float) var20 + 0.3,
							(float) (var18 + this.getWidestMod() + 3) - 0.3, (float) (var20 + 9 + 2) - 0.3,
							Hud.getColor(), Integer.MIN_VALUE);

				}
				FontLoaders.tahoma18.drawStringWithShadow1(var22.getName(), (float) (var18 + 1),
						(float) var20 + (float) var14 * 1.5F + 1.5, var22.isEnabled() ? -1 : 11184810);// Color.GRAY.getRGB());
			}

		}

	}

	@EventTarget
	public void onKey(EventKeyboard e) {
		switch (e.getKey()) {
		case Keyboard.KEY_UP:
			this.up();
			break;
		case Keyboard.KEY_DOWN:
			this.down();
			break;
		case Keyboard.KEY_RIGHT:
			this.right(Keyboard.KEY_RIGHT);
			break;
		case Keyboard.KEY_LEFT:
			this.left();
			break;
		case Keyboard.KEY_RETURN:
			this.ok(Keyboard.KEY_RETURN);
			break;
		}
	}

	public void up() {
		if (this.currentCategoryIndex > 0 && this.screen == 0) {
			--this.currentCategoryIndex;
		} else if (this.currentCategoryIndex == 0 && this.screen == 0) {
			this.currentCategoryIndex = this.categoryValues.size() - 1;
		} else if (this.currentModIndex > 0 && this.screen == 1) {
			--this.currentModIndex;
		} else if (this.currentModIndex == 0 && this.screen == 1) {
			this.currentModIndex = this.getModsForCurrentCategory().size() - 1;
		} else if (this.currentSettingIndex > 0 && this.screen == 2) {
			--this.currentSettingIndex;
		}

	}

	public void down() {
		if (this.currentCategoryIndex < this.categoryValues.size() - 1 && this.screen == 0) {
			++this.currentCategoryIndex;
		} else if (this.currentCategoryIndex == this.categoryValues.size() - 1 && this.screen == 0) {
			this.currentCategoryIndex = 0;
		} else if (this.currentModIndex < this.getModsForCurrentCategory().size() - 1 && this.screen == 1) {
			++this.currentModIndex;
		} else if (this.currentModIndex == this.getModsForCurrentCategory().size() - 1 && this.screen == 1) {
			this.currentModIndex = 0;
		}

	}

	public void right(int var1) {
		if (this.screen == 0) {
			this.screen = 1;
		}
	}

	public void ok(int var1) {
		if (this.screen == 1 && this.getCurrentModule() != null) {
			this.getCurrentModule().toggle();
		}

	}

	public void left() {
		if (this.screen == 1) {
			this.screen = 0;
			this.currentModIndex = 0;
		} else if (this.screen == 2) {
			this.screen = 1;
			this.currentSettingIndex = 0;
		}

	}

	public Category getCurrentCategorry() {
		return (Category) this.categoryValues.get(this.currentCategoryIndex);
	}

	public Mod getCurrentModule() {
		return (Mod) this.getModsForCurrentCategory().get(this.currentModIndex);
	}

	public ArrayList getModsForCurrentCategory() {
		ArrayList var1 = new ArrayList();
		Category var2 = this.getCurrentCategorry();
		Iterator var4 = ModManager.modList.values().stream().iterator();
		while (var4.hasNext()) {
			Mod var3 = (Mod) var4.next();
			if (var3.getCategory().equals(var2)) {
				var1.add(var3);
			}
		}
		return var1;
	}

	public int getWidestCategory() {
		int var1 = 0;
		Iterator var3 = this.categoryValues.iterator();

		while (var3.hasNext()) {
			Category var2 = (Category) var3.next();
			String var4 = var2.name();
			String p = ">";
			if (this.screen == 1 || this.screen == 2) {
				p = "<";
			} else {
				p = ">";
			}
			int var5 = FontLoaders.tahoma18.getStringWidth(
					var4.substring(0, 1).toUpperCase() + var4.substring(1, var4.length()).toLowerCase()) + 3;
			if (var5 > var1) {
				var1 = var5;
			}
		}

		return var1 + 2;
	}

	public int getWidestMod() {
		int var1 = 0;
		Iterator var3 = ModManager.modList.values().stream().iterator();

		while (var3.hasNext()) {
			Mod var2 = (Mod) var3.next();
			int var4 = FontLoaders.tahoma18.getStringWidth(var2.getName());
			if (var4 > var1) {
				var1 = var4;
			}
		}

		return var1;
	}
}
