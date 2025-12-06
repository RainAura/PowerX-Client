package cn.Power.mod.mods.RENDER;

import java.util.Collection;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Score;
import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.Value;
import cn.Power.events.EventChat;
import cn.Power.events.EventPacket;
import cn.Power.events.EventRender2D;
import cn.Power.events.EventRenderGui;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.RenderUtils;

import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S02PacketChat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class FinalKillsCounter extends Mod
{
    private static final String RED_WITHER_DEATH = "The Red Wither has died!";
    private static final String GREEN_WITHER_DEATH = "The Green Wither has died!";
    private static final String YELLOW_WITHER_DEATH = "The Yellow Wither has died!";
    private static final String BLUE_WITHER_DEATH = "The Blue Wither has died!";
    private static final String OWN_WITHER_DEATH = "Your wither has died. You can no longer respawn!";
    private static final String DEATHMATCH = "All withers are dead! 10 seconds till deathmatch!";
    private static final String PREP_PHASE = "Prepare your defenses!";
    private static final String[] KILL_MESSAGES;
    private static final int TEAMS = 4;
    public static final int RED_TEAM = 0;
    public static final int GREEN_TEAM = 1;
    public static final int YELLOW_TEAM = 2;
    public static final int BLUE_TEAM = 3;
    private static final String[] SCOREBOARD_PREFIXES;
    private static final String[] DEFAULT_PREFIXES;
    private boolean dummy;
    private boolean[] deadWithers;
    private String[] prefixes;
    private HashMap<String, Integer>[] teamKills;
    private ArrayList<String> deadPlayers;
	public Value<Double> posx = new Value<Double>("FinalKillsCounter_PosX", 1.0, 0.0, 200.0, 0.1);
	public Value<Double> posy = new Value<Double>("FinalKillsCounter_PosY", 1.0, 0.0, 200.0, 0.1);
	public Value<Boolean> compacthud = new Value<Boolean>("FinalKillsCounter_CompactHud", false);
	public Value<Boolean> background = new Value<Boolean>("FinalKillsCounter_DrawBackGround", false);
	
	public FinalKillsCounter() {
		super("FinalKillsCounter", Category.RENDER);
		reset();
	}
    
    
    static {
    	KILL_MESSAGES = new String[] { "(\\w+) was shot and killed by (\\w+).*", "(\\w+) was snowballed to death by (\\w+).*", "(\\w+) was killed by (\\w+).*", "(\\w+) was killed with a potion by (\\w+).*", "(\\w+) was killed with an explosion by (\\w+).*", "(\\w+) was killed with magic by (\\w+).*", "(\\w+) was filled full of lead by (\\w+).*", "(\\w+) was iced by (\\w+).*", "(\\w+) met their end by (\\w+).*", "(\\w+) lost a drinking contest with (\\w+).*", "(\\w+) was killed with dynamite by (\\w+).*", "(\\w+) lost the draw to (\\w+).*", "(\\w+) was struck down by (\\w+).*", "(\\w+) was turned to dust by (\\w+).*", "(\\w+) was turned to ash by (\\w+).*", "(\\w+) was melted by (\\w+).*", "(\\w+) was incinerated by (\\w+).*", "(\\w+) was vaporized by (\\w+).*", "(\\w+) was struck with Cupid's arrow by (\\w+).*", "(\\w+) was given the cold shoulder by (\\w+).*", "(\\w+) was hugged too hard by (\\w+).*", "(\\w+) drank a love potion from (\\w+).*", "(\\w+) was hit by a love bomb from (\\w+).*", "(\\w+) was no match for (\\w+).*", "(\\w+) was smote from afar by (\\w+).*", "(\\w+) was justly ended by (\\w+).*", "(\\w+) was purified by (\\w+).*", "(\\w+) was killed with holy water by (\\w+).*", "(\\w+) was dealt vengeful justice by (\\w+).*", "(\\w+) was returned to dust by (\\w+).*", "(\\w+) be shot and killed by (\\w+).*", "(\\w+) be snowballed to death by (\\w+).*", "(\\w+) be sent to Davy Jones' locker by (\\w+).*", "(\\w+) be killed with rum by (\\w+).*", "(\\w+) be shot with cannon by (\\w+).*", "(\\w+) be killed with magic by (\\w+).*", "(\\w+) was glazed in BBQ sauce by (\\w+).*", "(\\w+) was sprinked in chilli poweder by (\\w+).*", "(\\w+) was sliced up by (\\w+).*", "(\\w+) was overcooked by (\\w+).*", "(\\w+) was deep fried by (\\w+).*", "(\\w+) was boiled by (\\w+).*", "(\\w+) was injected with malware by (\\w+).*", "(\\w+) was DDoS'd by (\\w+).*", "(\\w+) was deleted by (\\w+).*", "(\\w+) was purged by an antivirus owned by (\\w+).*", "(\\w+) was fragmented by (\\w+).*", "(\\w+) was squeaked from a distance by (\\w+).*", "(\\w+) was hit by frozen cheese from (\\w+).*", "(\\w+) was chewed up by (\\w+).*", "(\\w+) was chemically cheesed by (\\w+).*", "(\\w+) was turned into cheese wiz by (\\w+).*", "(\\w+) was magically squeaked by (\\w+).*", "(\\w+) was corrupted by (\\w+).*" };
        SCOREBOARD_PREFIXES = new String[] { "[R]", "[G]", "[Y]", "[B]" };
        DEFAULT_PREFIXES = new String[] { "c", "a", "e", "9" };
    }
    
    public void reset() {
    	if(mc.thePlayer != null)
    		mc.thePlayer.sendChatMessage("-target clear");
        this.deadWithers = new boolean[4];
        this.prefixes = new String[4];
        this.teamKills = (HashMap<String, Integer>[])new HashMap[4];
        this.deadPlayers = new ArrayList<String>();
        for (int team = 0; team < 4; ++team) {
            this.prefixes[team] = FinalKillsCounter.DEFAULT_PREFIXES[team];
            this.teamKills[team] = new HashMap<String, Integer>();
        }
    }
    
    @Override
    public void onEnable() {
    	reset();
    }
    
    @EventTarget
    public void onRenderUI(EventRender2D ui) {
    	try {
   
    	ScaledResolution sr = new ScaledResolution(mc);
    	posx.valueMax = (double) sr.getScaledWidth() + 10;
    	posy.valueMax = (double) sr.getScaledHeight() + 10;
    	
    	dummy = this.compacthud.getValueState();
    	//System.out.println("xxx");
    	
    	this.render(posx.getValueState().doubleValue(), posy.getValueState().doubleValue());
    	}catch(Throwable c) {c.printStackTrace();}
    }
    
    
	@EventTarget
	private void packet(EventPacket e) {
			if(e.getPacket() instanceof S02PacketChat) {
				S02PacketChat ps = ((S02PacketChat)e.getPacket());
        final String rawMessage = ps.getChatComponent().getUnformattedText();
        final String colorMessage = ps.getChatComponent().getFormattedText();
        if (rawMessage.equals("                                 Mega Walls")) {
        	reset();
        }
        if (rawMessage.equals("       You have 6 minutes until the walls fall down!")) {
        	reset();
        }
        if (rawMessage.equals("Prepare your defenses!")) {
            this.setTeamPrefixes();
        }
        final String s = rawMessage;
        switch (s) {
            case "The Red Wither has died!": {
                this.deadWithers[0] = true;
            }
            case "The Green Wither has died!": {
                this.deadWithers[1] = true;
            }
            case "The Yellow Wither has died!": {
                this.deadWithers[2] = true;
            }
            case "The Blue Wither has died!": {
                this.deadWithers[3] = true;
            }
            case "Your wither has died. You can no longer respawn!": {
                final String teamColor = Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName().split("§")[1].substring(0, 1);
                this.setWitherDead(teamColor);
            }
            case "All withers are dead! 10 seconds till deathmatch!": {
                for (int team = 0; team < 4; ++team) {
                    this.deadWithers[team] = true;
                }
            }
            default: {
                final String[] kill_MESSAGES = FinalKillsCounter.KILL_MESSAGES;
                final int length = kill_MESSAGES.length;
                int i = 0;
                while (i < length) {
                    final String p = kill_MESSAGES[i];
                    final Matcher killMessageMatcher = Pattern.compile(p).matcher(rawMessage);
                    if (killMessageMatcher.matches()) {
                        final String killed = killMessageMatcher.group(1);
                        final String killer = killMessageMatcher.group(2);
                        final String killedTeam = colorMessage.split("§")[2].substring(0, 1);
                        final String killerTeam = colorMessage.split("§")[8].substring(0, 1);
                        this.removeKilledPlayer(killed, killedTeam);
                        if (this.getWitherDead(killedTeam)) {
                            this.addKill(killer, killerTeam);
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
        }
    }
	}
    
    public int getKills(final int team) {
        if (!this.isValidTeam(team)) {
            return 0;
        }
        int kills = 0;
        for (final int k : this.teamKills[team].values()) {
            kills += k;
        }
        return kills;
    }
    
    public HashMap<String, Integer> getPlayers(final int team) {
        if (!this.isValidTeam(team)) {
            return new HashMap<String, Integer>();
        }
        return this.teamKills[team];
    }
    
    private void setWitherDead(final String color) {
        final int team = this.getTeamFromColor(color);
        if (this.isValidTeam(team)) {
            this.deadWithers[team] = true;
        }
    }
    
    private boolean getWitherDead(final String color) {
        final int team = this.getTeamFromColor(color);
        return this.isValidTeam(team) && this.deadWithers[team];
    }
    
    private void setTeamPrefixes() {
        for (final String line : getScoreboardNames()) {
            for (int team = 0; team < 4; ++team) {
                if (line.contains(FinalKillsCounter.SCOREBOARD_PREFIXES[team])) {
                    this.prefixes[team] = line.split("§")[1].substring(0, 1);
                }
            }
        }
    }
    
    private void removeKilledPlayer(final String player, final String color) {
        final int team = this.getTeamFromColor(color);
        if (!this.isValidTeam(team)) {
            return;
        }
        if (this.deadWithers[team]) {
            this.teamKills[team].remove(player);
            this.deadPlayers.add(player);
        }
    }
    
    private void addKill(final String player, final String color) {
        final int team = this.getTeamFromColor(color);
        if (!this.isValidTeam(team)) {
            return;
        }
        if (this.deadPlayers.contains(player)) {
            return;
        }
        if (this.teamKills[team].containsKey(player)) {
            this.teamKills[team].put(player, this.teamKills[team].get(player) + 1);
        }
        else {
            this.teamKills[team].put(player, 1);
        }
        this.sortTeamKills(team);
    }
    
    private void sortTeamKills(final int team) {
        if (!this.isValidTeam(team)) {
            return;
        }
        this.teamKills[team] = this.teamKills[team].entrySet().stream().sorted(Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
    
    private int getTeamFromColor(final String color) {
        for (int team = 0; team < 4; ++team) {
            if (this.prefixes[team].equalsIgnoreCase(color)) {
                return team;
            }
        }
        return -1;
    }
    
    private boolean isValidTeam(final int team) {
        return team >= 0 && team < 4;
    }
    
    private static ArrayList<String> getScoreboardNames() {
        final ArrayList<String> scoreboardNames = new ArrayList<String>();
        try {
            final Scoreboard scoreboard = Minecraft.theWorld.getScoreboard();
            final ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
            final Collection<Score> scores = (Collection<Score>)scoreboard.getSortedScores(sidebarObjective);
            for (final Score score : scores) {
                final ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
                scoreboardNames.add(ScorePlayerTeam.formatPlayerName((Team)team, score.getPlayerName()));
            }
        }
        catch (Exception ex) {}
        return scoreboardNames;
    }

    
    public int getHeight() {
        if (this.compacthud.getValueState()) {
            return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
        }
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 4;
    }
    
    public int getWidth() {
            int width = 0;
            for (final String m : this.getDisplayText().split("\n")) {
                final int mWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(m);
                if (mWidth > width) {
                    width = mWidth;
                }
            }
            return width;
    }
    
    public void render(double x, double y) {
        if (background.getValueState()) {
            RenderUtils.drawRect(x - 1, y - 1, x + this.getWidth(), y + this.getHeight(), new Color(0, 0, 0, 64).getRGB());
        }
        this.drawMultilineString(this.getDisplayText(), (float)x, (float)y);
    }
    
    private void drawMultilineString(final String msg, final float x, float y) {
        for (final String m : msg.split("\n")) {
            Minecraft.getMinecraft().fontRendererObj.drawString(m, x, y, 16777215, false);
            y += Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
        }
    }
    
    private String getDisplayText() {
        String msg = "";
        final FinalKillsCounter kc = this;
        if (kc != null) {
            if (dummy) {
                msg = msg + "" + EnumChatFormatting.RED + kc.getKills(0) + EnumChatFormatting.GRAY + " / " + EnumChatFormatting.GREEN + kc.getKills(1) + EnumChatFormatting.GRAY + " / " + EnumChatFormatting.YELLOW + kc.getKills(2) + EnumChatFormatting.GRAY + " / " + EnumChatFormatting.BLUE + kc.getKills(3);
            }
            else {
                msg = msg + EnumChatFormatting.RED + "RED" + EnumChatFormatting.WHITE + ": " + kc.getKills(0) + "\n";
                msg = msg + EnumChatFormatting.GREEN + "GREEN" + EnumChatFormatting.WHITE + ": " + kc.getKills(1) + "\n";
                msg = msg + EnumChatFormatting.YELLOW + "YELLOW" + EnumChatFormatting.WHITE + ": " + kc.getKills(2) + "\n";
                msg = msg + EnumChatFormatting.BLUE + "BLUE" + EnumChatFormatting.WHITE + ": " + kc.getKills(3);
            }
        }
        return msg;
    }

}

