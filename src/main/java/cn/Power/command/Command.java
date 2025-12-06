package cn.Power.command;

public class Command {
	private String[] commands;
	private String args;

	public Command(String[] commands) {
		this.commands = commands;
	}

	public String[] getCommands() {
		return this.commands;
	}

	public void onCmd(String[] args) {
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public String getArgs() {
		return this.args;
	}

	public static String joinArray(final String[] input, final String seperator, int begin, int end) {
		if (begin < 0)
			begin = 0;
		if (end > input.length)
			end = input.length;

		final StringBuilder builder = new StringBuilder();

		for (int i = begin; i < end; i++) {
			builder.append(input[i]).append(seperator);
		}
		return builder.substring(0, builder.toString().length() - 1);
	}
}
