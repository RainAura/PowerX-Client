package cn.Power.ui.Gui;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import cn.Power.Font.FontManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiLoading extends GuiScreen {

	GuiTextField loginBox;
	private String status;

	@Override
	public void initGui() {
		this.loginBox = new GuiTextField(520, this.mc.fontRendererObj, this.width / 2 - 100, height / 2 - 60, 200, 20);
		this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 - 20, 200, 20, "Login"));
		this.loginBox.setFocused(true);
		Keyboard.enableRepeatEvents(true);
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawBackground(0);
		this.loginBox.drawTextBox();

		FontManager.big.drawCenteredStringWithAlpha("Welcome To PowerX Client", width / 2, height / 2 - 120,
				new Color(0, 153, 255).getRGB(), 0.6f);

		FontManager.sw15.drawCenteredString(status != null ? status : "", width / 2, height / 2 - 35,
				new Color(0, 153, 255).getRGB());

		FontManager.baloo18.drawString("UID :", width / 2 - 100, height / 2 - 74, new Color(255, 0, 0).getRGB());

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	public void updateScreen() {
		loginBox.updateCursorCounter();
		super.updateScreen();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		loginBox.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char character, int key) throws IOException {
		if (key == Keyboard.KEY_RETURN) {
			if (loginBox.getText().length() < 3) {
				status = "\247ePlease enter the correct format";
				return;
			}
			// status = HWID.loginClient(loginBox.getText());
		}
		if ((loginBox.getText().length() < 3) && (key == Keyboard.KEY_0 || key == Keyboard.KEY_1
				|| key == Keyboard.KEY_2 || key == Keyboard.KEY_3 || key == Keyboard.KEY_4 || key == Keyboard.KEY_5
				|| key == Keyboard.KEY_6 || key == Keyboard.KEY_7 || key == Keyboard.KEY_8 || key == Keyboard.KEY_9
				|| key == 82 || key == 79 || key == 80 || key == 81 || key == 75 || key == 76 || key == 77 || key == 71
				|| key == 72 || key == 73) || key == 14)
			this.loginBox.textboxKeyTyped(character, key);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			if (loginBox.getText().length() < 3) {
				status = "\247ePlease enter the correct format";
				return;
			}
			// status = HWID.loginClient(loginBox.getText());
		} else if (button.id == 1) {

		}
		super.actionPerformed(button);
	}

}
