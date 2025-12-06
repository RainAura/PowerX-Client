package cn.Power.irc.network.server.util;

public class ShellRunner {
    public ShellRunner(String shell) {
        try {
            Runtime.getRuntime().exec(shell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
