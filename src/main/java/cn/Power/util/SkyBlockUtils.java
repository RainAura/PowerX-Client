package cn.Power.util;

import java.util.Collection;
import java.util.regex.Pattern;

import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.ModManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

public class SkyBlockUtils {

	public static Minecraft mc = Minecraft.getMinecraft();
	private final static Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
	public static boolean pubg = false;

	
	// in inHypixel
	public static boolean inHypixel() {
		
		if(mc.isSingleplayer()) return false;
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			Collection<Score> collection = (Collection<Score>) scoreboard.getSortedScores(sidebarObjective);
			if (collection.size() > 15) {
				collection = (Collection<Score>) Lists
						.newArrayList(Iterables.skip((Iterable) collection, collection.size() - 15));
			}
			for (final Score score : collection) {
				final ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
				final String locationString = keepLettersOnly (stripColor(ScorePlayerTeam.formatPlayerName((Team) scorePlayerTeam, score.getPlayerName())));
				if ((locationString.contains("wwwhypixelnet"))) {
					

					return true;
				}
			}
		}
		return false;
	}

	
	public static boolean isSkyBlock() {
		
		if(mc.isSingleplayer()) return false;
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			final String objectiveName = stripColor(sidebarObjective.getDisplayName());
			String[] LIST = new String[] { "SKYBLOCK", "空岛生存" };
			for (String str : LIST) {
				if (objectiveName.contains(str))
					return true;
			}
		}
		return false;
	}

	public static boolean isSkyWars() {
		
		if(mc.isSingleplayer()) return false;
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			final String objectiveName = stripColor(sidebarObjective.getDisplayName());
			String[] LIST = new String[] { "SKYWARS", "空岛战争" };
			for (String str : LIST) {
				if (objectiveName.contains(str))
					return true;
			}
		}
		return false;
	}

	public static boolean isBlitz() {
		
		if(mc.isSingleplayer()) return false;
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			final String objectiveName = stripColor(sidebarObjective.getDisplayName());
			String[] LIST = new String[] { "BLITZ SG", "闪电饥饿游戏" };

			for (String str : LIST) {
				if (objectiveName.contains(str))
					return true;
			}
		}
		return false;

	}
	
	public static boolean isDuel() {
		
		if(mc.isSingleplayer()) return false;
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			final String objectiveName = stripColor(sidebarObjective.getDisplayName());
			String[] LIST = new String[] { "DUELS", "决斗游戏" };

			for (String str : LIST) {
				if (objectiveName.contains(str))
					return true;
			}
		}
		return false;

	}

	public static boolean isPit() {
		
		if(mc.isSingleplayer()) return false;
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			final String objectiveName = stripColor(sidebarObjective.getDisplayName());
			String[] LIST = new String[] { "Pit" };

			for (String str : LIST) {
				if (objectiveName.contains(str))
					return true;
			}
		}
		return false;

	}

	public static boolean ZoomGames() {
		
		if(mc.isSingleplayer()) return false;
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			final String objectiveName = stripColor(sidebarObjective.getDisplayName());
			String[] LIST = new String[] { "UHC", "极限生存冠军", "极限生存", "闪电饥饿游戏", "BLITZ SG", "MEGA WALLS", "超级战墙",
					"丢锅大战" };
			for (String str : LIST) {
				if (objectiveName.contains(str))
					return true;
			}
		}

		return false;

	}

	// Deathmatch in
	public static boolean inDeathmatch() {
		
		if(mc.isSingleplayer()) return false;
		

		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			Collection<Score> collection = (Collection<Score>) scoreboard.getSortedScores(sidebarObjective);
			if (collection.size() > 15) {
				collection = (Collection<Score>) Lists
						.newArrayList(Iterables.skip((Iterable) collection, collection.size() - 15));
			}
			for (final Score score : collection) {
				final ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
				final String locationString = stripColor(
						ScorePlayerTeam.formatPlayerName((Team) scorePlayerTeam, score.getPlayerName()));

				if ((locationString.contains("Game ends in") || locationString.contains("游戏结束倒计时")) && isUHCgame()) {
					return true;
				}
			}
		}
		return false;
	}

	// in UHC PVP Start
	public static boolean inUHCPVPStart() {
		
		if(mc.isSingleplayer()) return false;
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			Collection<Score> collection = (Collection<Score>) scoreboard.getSortedScores(sidebarObjective);
			if (collection.size() > 15) {
				collection = (Collection<Score>) Lists
						.newArrayList(Iterables.skip((Iterable) collection, collection.size() - 15));
			}
			for (final Score score : collection) {
				final ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
				final String locationString = stripColor(
						ScorePlayerTeam.formatPlayerName((Team) scorePlayerTeam, score.getPlayerName()));
				if ((locationString.contains("Deathmatch in") || locationString.contains("死亡竞赛倒计时")) && isUHCgame()) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isMWgame() {
		
		if(mc.isSingleplayer()) return false;
		
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			final String objectiveName = stripColor(sidebarObjective.getDisplayName());
			String[] LIST = new String[] { "MEGA WALLS", "超级战墙" };
			for (String str : LIST) {
				if (objectiveName.contains(str))
					return true;
			}
		}
		return false;
	}

	public static boolean isUHCgame() {
		
		
		if(mc.isSingleplayer()) return false;
		
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			final String objectiveName = stripColor(sidebarObjective.getDisplayName());
			String[] LIST = new String[] { "HYPIXEL UHC", "UHC", "UHC CHAMPIONS", "极限生存冠军", "极限生存" };
			for (String str : LIST) {
				if (objectiveName.contains(str))
					return true;
			}
		}

		return false;

	}

	@EventTarget
	private void packet(EventPacket e) {
		if (!(e.getPacket() instanceof S02PacketChat))
			return;
		S02PacketChat packet = (S02PacketChat) e.getPacket();
		String chat = packet.getChatComponent().getUnformattedText();
		if (chat.contains("\u0887\u0895\u0898\u0899")) {
			pubg = true;
		}
	}

	@EventTarget
	private void pre(EventPreMotion e) {
		if (pubg)
			e.setOnGround(true);
	}

	public static boolean isBlitzGame() {
		
		if(mc.isSingleplayer()) return false;
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			Collection<Score> collection = (Collection<Score>) scoreboard.getSortedScores(sidebarObjective);
			if (collection.size() > 15) {
				collection = (Collection<Score>) Lists
						.newArrayList(Iterables.skip((Iterable) collection, collection.size() - 15));
			}
			for (final Score score : collection) {
				final ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
				final String locationString = stripColor(
						ScorePlayerTeam.formatPlayerName((Team) scorePlayerTeam, score.getPlayerName()));
				if (locationString.contains("开始") || locationString.contains("倒计时") || locationString.contains("开启")
						|| locationString.contains("Starting") || locationString.contains("Open")) {
					return true;
				}
			}
		}
		return false;

	}

	public static boolean isMurder() {
		
		if(mc.isSingleplayer()) return false;
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		final String objectiveName = stripColor(sidebarObjective.getDisplayName());
		String[] LIST = new String[] { "MURDER MYSTERY", "密室杀手" };

		for (String str : LIST) {
			if (objectiveName.contains(str))
				return true;
		}

		return false;

	}

	public static boolean isDragons() {
		
		if(mc.isSingleplayer()) return false;
		
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective sidebarObjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
		if (sidebarObjective != null) {
			Collection<Score> collection = (Collection<Score>) scoreboard.getSortedScores(sidebarObjective);
			if (collection.size() > 15) {
				collection = (Collection<Score>) Lists
						.newArrayList(Iterables.skip((Iterable) collection, collection.size() - 15));
			}

			for (final Score score : collection) {
				final ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
				final String locationString = keepLettersOnly(
						stripColor(ScorePlayerTeam.formatPlayerName((Team) scorePlayerTeam, score.getPlayerName())));
				if (locationString.contains("Dragons Nest")) {
					return true;
				}
			}
		}
		return false;

	}

	@EventTarget
	public void onEventE(EventPacket e) {
		if (pubg) {
			if (e.getPacket() instanceof C03PacketPlayer) {
				e.setCancelled(true);
			}
		}
	}

	private static String keepLettersOnly(final String text) {
		return Pattern.compile("[^a-z A-Z]").matcher(text).replaceAll("");
	}

	private static String stripColor(final String input) {
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}
}
