package cn.Power.util.friendManager;

import java.util.ArrayList;
import java.util.Iterator;

import cn.Power.Client;
import cn.Power.irc.network.server.data.User;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class FriendManager {
	private static ArrayList friends = new ArrayList();

	public static ArrayList getFriends() {
		return friends;
	}

	public static boolean isFriend(EntityPlayer player) {
		Iterator var2 = friends.iterator();
		while (var2.hasNext()) {
			Friend friend = (Friend) var2.next();
			if (friend.getName().equalsIgnoreCase(player.getName())) {
				return true;
			}
		}

		return false;
	}

	public static boolean isFriend(String player) {
		Iterator var2 = friends.iterator();

		while (var2.hasNext()) {
			Friend friend = (Friend) var2.next();
			if (friend.getName().equalsIgnoreCase(player)) {
				return true;
			}
		}
		return false;
	}

	public static Friend getFriend(String name) {
		Iterator var2 = friends.iterator();

		while (var2.hasNext()) {
			Friend friend = (Friend) var2.next();
			if (friend.getName().equalsIgnoreCase(name)) {
				return friend;
			}
		}

		return null;
	}

	   public static boolean isIRCMOD(Entity player) {

	        if (Client.instance.IRC.user.getRankLevel() >= 5) return false;
	        if (!Client.instance.IRC.userlist.isEmpty()) {
	            for (User user : Client.instance.IRC.userlist) {
	                if (player.getName().equals(user.gameID)) {
	                    return user.MT;
	                }
	            }
	        }
	        return false;
	    }
	   
}
