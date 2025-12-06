package cn.Power.util.handler;

import org.lwjgl.input.Mouse;

public class MouseInputHandler {
	public boolean clicked;
	public boolean clicked2;
	public boolean clicked3;
	private int button;

	public MouseInputHandler(int key) {
		this.button = key;
	}

	public boolean canExcecute() {
		if (Mouse.isButtonDown((int) this.button)) {
			if (!this.clicked) {
				this.clicked = true;
				return true;
			}
		} else {
			this.clicked = false;
		}
		return false;
	}

	public boolean canExcecute2() {
		if (Mouse.isButtonDown((int) 1)) {
			if (!this.clicked2) {
				this.clicked2 = true;
				return true;
			}
		} else {
			this.clicked2 = false;
		}
		return false;
	}

	public boolean canExcecute3() {
		if (Mouse.isButtonDown((int) 2)) {
			if (!this.clicked3) {
				this.clicked3 = true;
				return true;
			}
		} else {
			this.clicked3 = false;
		}
		return false;
	}
}
