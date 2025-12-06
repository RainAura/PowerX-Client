package cn.Power;

import java.util.ArrayList;
import java.util.List;

public class Value<T> {
	private T value;
	public T valueMin;
	public T valueMax;
	private double step;
	private T defaultValue;
	private String name;
	public boolean isValueBoolean;
	public boolean isValueInteger;
	public boolean isValueFloat;
	public boolean isValueDouble;
	public boolean isValueMode;
	public boolean isValueLong;
	public boolean isValueByte;
	private int current;
	public ArrayList<String> mode;
	public double sliderX;
	public boolean set;
	public static List<Value> list;
	public boolean isSettingMode;
	public boolean openMods;
	public double maxSliderSize;
	public int RADIUS = 4;
	public float currentRadius;
	public boolean disabled;
	private String modeTitle;

	static {
		list = new ArrayList<Value>();
	}

	public Value(String classname, String modeTitle, int current) {
		this.set = false;
		this.currentRadius = 4.0f;
		this.isValueBoolean = false;
		this.isValueInteger = false;
		this.isValueFloat = false;
		this.isValueDouble = false;
		this.isValueLong = false;
		this.isValueByte = false;
		this.defaultValue = this.value;
		this.isValueMode = true;
		this.step = 0.1;
		this.mode = new ArrayList<String>();
		this.current = current;
		this.name = String.valueOf(classname.intern()) + "_" + "Mode";
		this.modeTitle = modeTitle;
		Value.list.add(this);
	}

	public Value(String name, T defaultValue, T valueMin, T valueMax) {
		this.set = false;
		this.currentRadius = 4.0f;
		this.isValueBoolean = false;
		this.isValueInteger = false;
		this.isValueFloat = false;
		this.isValueDouble = false;
		this.isValueLong = false;
		this.isValueByte = false;
		this.defaultValue = this.value;
		this.name = name;
		this.value = defaultValue;
		this.valueMin = valueMin;
		this.valueMax = valueMax;
		this.step = 0.1;
		if (this.value instanceof Double) {
			this.isValueDouble = true;
		}
		Value.list.add(this);
	}

	public Value(String name, T value, T valueMin, T valueMax, double steps) {
		this.set = false;
		this.currentRadius = 4.0f;
		this.isValueBoolean = false;
		this.isValueInteger = false;
		this.isValueFloat = false;
		this.isValueDouble = false;
		this.isValueLong = false;
		this.isValueByte = false;
		this.defaultValue = value;
		this.name = name;
		this.value = value;
		this.valueMin = valueMin;
		this.valueMax = valueMax;
		this.step = steps;
		if (value instanceof Double) {
			this.isValueDouble = true;
		}
		Value.list.add(this);
	}

	public Value(String name, T value) {
		this.set = false;
		this.currentRadius = 4.0f;
		this.isValueBoolean = false;
		this.isValueInteger = false;
		this.isValueFloat = false;
		this.isValueDouble = false;
		this.isValueLong = false;
		this.isValueByte = false;
		this.defaultValue = value;
		this.name = name;
		this.value = value;
		if (value instanceof Boolean) {
			this.isValueBoolean = true;
		} else if (value instanceof Integer) {
			this.isValueInteger = true;
		} else if (value instanceof Float) {
			this.isValueFloat = true;
		} else if (value instanceof Long) {
			this.isValueLong = true;
		} else if (value instanceof Byte) {
			this.isValueByte = true;
		}
		Value.list.add(this);
	}

	public Value(String name, String name2, String nam3, T value, T value2, T value3) {
		this.set = false;
		this.currentRadius = 4.0f;
		this.isValueBoolean = false;
		this.isValueInteger = false;
		this.isValueFloat = false;
		this.isValueDouble = false;
		this.isValueLong = false;
		this.isValueByte = false;
		this.defaultValue = value;
		this.name = name;
		this.value = value;
		if (value instanceof Boolean) {
			this.isValueBoolean = true;
		} else if (value instanceof Integer) {
			this.isValueInteger = true;
		} else if (value instanceof Float) {
			this.isValueFloat = true;
		} else if (value instanceof Double) {
			this.isValueDouble = true;
		} else if (value instanceof Long) {
			this.isValueLong = true;
		} else if (value instanceof Byte) {
			this.isValueByte = true;
		}
		Value.list.add(this);
	}

	public void addValue(String valueName) {
		this.mode.add(valueName.intern());
	}

	public void setCurrentMode(int current) {
		if (current > this.mode.size() - 1) {
			System.out.println("Value is to big! Set to 0. (" + this.mode.size() + ")");
			return;
		}
		this.current = current;
	}

	public int getCurrentMode() {
		return this.current;
	}

	public ArrayList<String> listModes() {
		return this.mode;
	}

	public String getModeTitle() {
		return this.modeTitle;
	}

	public String getModeAt(int index) {
		return this.mode.get(index).intern();
	}

	public String getModeAt(String modeName) {
		for (int i = 0; i < this.mode.size(); ++i) {
			if (this.mode.get(i).equalsIgnoreCase(modeName)) {
				return this.mode.get(i);
			}
		}
		return "NULL";
	}

	public int getModeInt(String modeName) {
		for (int i = 0; i < this.mode.size(); ++i) {
			if (this.mode.get(i).equalsIgnoreCase(modeName)) {
				return i;
			}
		}
		return 0;
	}

	public boolean isCurrentMode(String modeName) {
		return this.getModeAt(this.getCurrentMode()).equalsIgnoreCase(modeName);
	}

	public String getAllModes() {
		String all = "";
		for (int i = 0; i < this.mode.size(); ++i) {
			all = String.valueOf(all) + this.mode.get(i).toString();
		}
		return all;
	}

	public String getValueName() {
		return this.name;
	}

	public String getDisplayTitle() {
		if (this.isValueMode) {
			return this.getModeTitle();
		}
		return this.getValueName().split("_")[1];
	}

	public T getValueMin() {
		if (this.value instanceof Double) {
			return this.valueMin;
		}
		return null;
	}

	public double getSteps() {
		return this.step;
	}

	public T getValueMax() {
		if (this.value instanceof Double) {
			return this.valueMax;
		}
		return null;
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}

	public T getValueState() {
		return this.value;
	}

	public void setValueState(T value) {
		this.value = value;
	}

	public static Value getBooleanValueByName(String name) {
		for (Value value : Value.list) {
			if (value.isValueBoolean && value.getValueName().equalsIgnoreCase(name)) {
				return value;
			}
		}
		return null;
	}

	public static Value getDoubleValueByName(String name) {
		for (Value value : Value.list) {
			if (value.isValueDouble && value.getValueName().equalsIgnoreCase(name)) {
				return value;
			}
		}
		return null;
	}

	public static Value getModeValue(String valueName, String title) {
		for (Value value : Value.list) {
			if (value.isValueMode && value.getValueName().equalsIgnoreCase(valueName)
					&& value.getModeTitle().equalsIgnoreCase(title)) {
				return value;
			}
		}
		return null;
	}
}
