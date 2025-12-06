package cn.Power.irc.network.server.data;

public class User {
    public String client = "", rank = "", name = "", gameID = "undefined";
    public long lastTime;
    public long muteTime;
    public long loginTime;
    public boolean hide = false;
    public boolean MT = false;

    public User(String client, String rank, String name, String gameID) {
        this.client = client;
        this.rank = rank;
        this.name = name;
        this.gameID = gameID;
        this.lastTime = System.currentTimeMillis();
        this.muteTime = System.currentTimeMillis();
        this.loginTime = System.currentTimeMillis();
    }

    public String getClient() {
        if (this.client.equalsIgnoreCase("FoodByte")) {
            return "§e[FB]";
        }
        if (this.client.equalsIgnoreCase("PowerX")) {
            return "§d[PX]";
        }
        if (this.client.equalsIgnoreCase("Server")) {
            return "§c[Server]";
        }
        return "§2[" + this.client + "]";
    }

    public String getRank() {
        switch (this.rank) {
            case "Owner":
                return "\2473" + "[Owner]\247r";
            case "Dev":
                return "\2473" + "[Dev]\247r";
            case "Admin":
                return "\2474" + "[Admin]\247r";
            case "Mod":
                return "\2472" + "[Mod]\247r";
            case "Help":
                return "\2479" + "[Help]\247r";
            case "Contributor":
                return "\2475" + "[Contributor]\247r";
            case "User":
                return "\247a" + "[User]\247r";
        }

        return "";
    }

    public int getRankLevel() {
        switch (this.rank) {
            case "Owner":
                return 999;
            case "Dev":
                return 998;
            case "Admin":
                return 5;
            case "Mod":
                return 4;
            case "Help":
                return 3;
            case "Beta":
            case "Backer":
                return 2;
            case "User":
                return 1;
        }
        return 0;
    }

}