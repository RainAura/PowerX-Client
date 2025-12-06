package cn.Power.mod.mods.COMBAT;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4d;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.util.AStarCustomPathFinder;
import cn.Power.util.Colors;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.RotationUtils;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.TargetHUD;
import cn.Power.util.friendManager.FriendManager;
import cn.Power.util.misc.Timer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Tpaura extends Mod {

	private double dashDistance = 5;
	public Value<Boolean> ESP = new Value<Boolean>("Tpaura_ESP", true);
	public Value<Boolean> Targethud = new Value<Boolean>("Tpaura_TargetHUD", true);
	public Value<Boolean> PATHESP = new Value<Boolean>("Tpaura_Path", true);
	public Value<Boolean> Rejoin = new Value<Boolean>("Tpaura_Rejoin", true);
	public Value<Boolean> Player = new Value<Boolean>("Tpaura_Player", true);
	public Value<Boolean> Wither = new Value<Boolean>("Tpaura_Wither", true);
//	public Value<Boolean> LOGGED = new Value<Boolean>("Tpaura_LoggedOut", true);
	public Value<Boolean> CheckMove = new Value<Boolean>("Tpaura_CheckMove", true);
	private Value<Double> RANGE = new Value<Double>("Tpaura_Reach", 30.0, 8.0, 200.0, 2.0);
	private Value<Double> CPS = new Value<Double>("Tpaura_Aps", 2.0, 1.0, 20.0, 1.0);
	private Value<Double> YDistanceLimit = new Value<Double>("Tpaura_YDistanceLimit", 16.0,0.0, 50.0, 0.5);

	private ArrayList<Vec3> path = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	private List<Vec3>[] test = new ArrayList[50];
	private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();
	private Timer cps = new Timer();
	public static Timer timer = new Timer();
	public static boolean canReach;
	public static int stage = 0;

	public ScorePlayerTeam team;

	public Tpaura() {
		super("Tpaura", Category.COMBAT);
	}

	@Override
	public void onDisable() {

		Minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(
				C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));

	}

	@Override
	public void onEnable() {
		timer.reset();
		targets.clear();

		team = (ScorePlayerTeam) Minecraft.thePlayer.getTeam();

		path.clear();

		canReach = false;

		Tpaura.stage = Rejoin.getValueState() ? 0 : 2;

		if (!Rejoin.getValueState())
			canReach = true;
	}

	@EventTarget
	public void onReceivePacket(EventPacket event) {

		try {
			switch (Tpaura.stage) {
			case 1: {

				if ((event.getPacket() instanceof S39PacketPlayerAbilities) && !canReach && SkyBlockUtils.isMWgame()) {

					PlayerCapabilities cap = new PlayerCapabilities();
					
					cap.setPlayerWalkSpeed(0.15f);

					cap.allowFlying = true;

					cap.isFlying = true;

					Minecraft.getNetHandler().addToSendQueue(new C13PacketPlayerAbilities(cap));

					cap.allowFlying = false;

					cap.isFlying = false;

					Minecraft.getNetHandler().addToSendQueue(new C13PacketPlayerAbilities(cap));

					if (Minecraft.theWorld != null) {

						ScorePlayerTeam teamx = Minecraft.theWorld.getScoreboard().createTeam("PX_KEEPNAME");

						teamx.setTeamName(team.getTeamName());
						teamx.setNamePrefix(team.getColorPrefix());
						teamx.setNameSuffix(team.getColorSuffix());
						teamx.setChatFormat(team.getChatFormat());
						teamx.func_98298_a(team.func_98299_i());

						teamx.setNameTagVisibility(team.getNameTagVisibility());

						Minecraft.theWorld.getScoreboard().addPlayerToTeam(Minecraft.thePlayer.getName(), "PX_KEEPNAME");

					}

				} else if ((event.getPacket() instanceof S02PacketChat) && !Tpaura.canReach) {

					S02PacketChat ab = (S02PacketChat) event.getPacket();

					if (ab.getChatComponent().toString().contains("You logged back in!")
							|| ab.getChatComponent().toString().contains("你已经重新登录")) {

						if (Minecraft.theWorld != null) {

							ScorePlayerTeam teamx = Minecraft.theWorld.getScoreboard().createTeam("PX_KEEPNAME");

							teamx.setTeamName(team.getTeamName());
							teamx.setNamePrefix(team.getColorPrefix());
							teamx.setNameSuffix(team.getColorSuffix());
							teamx.setChatFormat(team.getChatFormat());
							teamx.func_98298_a(team.func_98299_i());

							teamx.setNameTagVisibility(team.getNameTagVisibility());

							Minecraft.theWorld.getScoreboard().addPlayerToTeam(Minecraft.thePlayer.getName(), "PX_KEEPNAME");

						}

						Tpaura.canReach = true;

						path.clear();

						Tpaura.timer.reset();

						Tpaura.stage = 2;
					}

				} else if (event.getPacket() instanceof S3EPacketTeams) {

					// if(((S3EPacketTeams)event.packet).getAction() == 1 ||
					// ((S3EPacketTeams)event.packet).getAction() == 4)
					// ((S3EPacketTeams)event.packet).players.removeIf(k->k.equals(mc.thePlayer.getName()));

					if (Minecraft.theWorld != null) {

						ScorePlayerTeam teamx = Minecraft.theWorld.getScoreboard().createTeam("PX_KEEPNAME");

						teamx.setTeamName(team.getTeamName());
						teamx.setNamePrefix(team.getColorPrefix());
						teamx.setNameSuffix(team.getColorSuffix());
						teamx.setChatFormat(team.getChatFormat());
						teamx.func_98298_a(team.func_98299_i());

						teamx.setNameTagVisibility(team.getNameTagVisibility());

						Minecraft.theWorld.getScoreboard().addPlayerToTeam(Minecraft.thePlayer.getName(), "PX_KEEPNAME");

					}

				}

				break;
			}

			case 2:

				// if((event.getPacket() instanceof C03PacketPlayer))
				// ((C03PacketPlayer)event.getPacket()).onGround = true;

				break;
			}

		} catch (Throwable c) {
			c.printStackTrace();
		}

	}

	private boolean isSword() {
		return Minecraft.thePlayer.inventory.getCurrentItem() != null
				&& Minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
	}

	@SuppressWarnings("unchecked")
	@EventTarget(4)
	public void onPre(EventPreMotion em) {

		switch (Tpaura.stage) {
		case 0: {

			path.clear();

			canReach = false;

			if (this.isEnabled())
				Minecraft.thePlayer.sendChatMessage("-r");

			Tpaura.stage = 1;

			break;
		}

		case 2: {

			int maxtTargets = 1;
			int delayValue = (20 / (CPS.getValueState().intValue())) * 50;
			double hypixelTimer = 1.5D * 1000;

			if (!canReach) {

				return;

			} else {

				if (Rejoin.getValueState() && timer.check((long) hypixelTimer)) {
					Tpaura.stage = 0;

					return;
				}
			}

			if (Minecraft.theWorld != null) {

				ScorePlayerTeam teamx = Minecraft.theWorld.getScoreboard().createTeam("PX_KEEPNAME");

				teamx.setTeamName(team.getTeamName());
				teamx.setNamePrefix(team.getColorPrefix());
				teamx.setNameSuffix(team.getColorSuffix());
				teamx.setChatFormat(team.getChatFormat());
				teamx.func_98298_a(team.func_98299_i());

				teamx.setNameTagVisibility(team.getNameTagVisibility());

				Minecraft.theWorld.getScoreboard().addPlayerToTeam(Minecraft.thePlayer.getName(), "PX_KEEPNAME");

			}

			targets = getTargets();

			if (this.isSword() && targets.size() > 0)
				Minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(
						C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));

			if (cps.check(delayValue) && !Minecraft.thePlayer.isMovingKeyBindingActive())
				if (targets.size() > 0) {
					test = new ArrayList[50];

					for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
						EntityLivingBase T = targets.get(i);
						Vec3 topFrom = new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY,
								Minecraft.thePlayer.posZ);
						Vec3 to = new Vec3(T.posX, T.posY, T.posZ).subtract(T.getVectorForRotation(0, T.rotationYaw)).subtract(T.getVectorForRotation(0, T.rotationYaw));

						if (path.isEmpty() || Minecraft.thePlayer.isMovingKeyBindingActive()
								|| !(T instanceof EntityWither))
							path = computePath(topFrom, to);

						test[i] = path;

						for (Vec3 pathElm : path) {

							float[] b = RotationUtils.getRotations(T, pathElm.getX(), pathElm.getY(), pathElm.getZ());

							
							Minecraft.getNetHandler().getNetworkManager()
									.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(pathElm.getX() + Math.random() / 10,
											pathElm.getY() + Math.random() / 10, pathElm.getZ() + Math.random() / 10, b[0], b[1], true));
						}
						
						Minecraft.getNetHandler().getNetworkManager()
						.sendPacketNoEvent(new C03PacketPlayer(false));
		

						Minecraft.thePlayer.swingItem();
						Minecraft.getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C02PacketUseEntity(T, C02PacketUseEntity.Action.ATTACK));

						Minecraft.thePlayer.swingItem();
						Minecraft.getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C02PacketUseEntity(T, C02PacketUseEntity.Action.ATTACK));

						Minecraft.thePlayer.swingItem();
						Minecraft.getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C02PacketUseEntity(T, C02PacketUseEntity.Action.ATTACK));

						ArrayList<Vec3> path2 = (ArrayList<Vec3>) path.clone();

						Collections.reverse(path2);

						for (Vec3 pathElm : path2) {
							Minecraft.getNetHandler().getNetworkManager()
									.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(pathElm.getX(),
											pathElm.getY(), pathElm.getZ(), Minecraft.thePlayer.rotationYaw,
											Minecraft.thePlayer.rotationPitch, true));

							em.x = pathElm.xCoord;
							em.y = pathElm.yCoord;
							em.z = pathElm.zCoord;
						}

					}

					cps.reset();
				}

			if (this.isSword() && targets.size() > 0) {
				Minecraft.thePlayer.itemInUseCount = 520;

				Minecraft.thePlayer.sendQueue.getNetworkManager()
						.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(Minecraft.thePlayer.inventory.getCurrentItem()));

			}
			
			break;
		}

		}

	}

	@EventTarget(4)
	public void onRender2D(EventRender2D event) {
		if (Targethud.getValueState())

			if (targets.size() > 0)
				TargetHUD.onScreenDraw(sr, targets.get(0));
	}

	@SuppressWarnings("unchecked")
	@EventTarget
	public void on3D(EventRender er) {
		int maxtTargets = 1;

		if (!targets.isEmpty() && ESP.getValueState()) {
			if (targets.size() > 0) {
				for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
					int color = targets.get(i).hurtResistantTime > 15 ? Colors.getColor(new Color(255, 70, 70, 100))
							: Colors.getColor(new Color(0, 70, 255, 100));
					drawESP(targets.get(i), color);
				}
			}
		}
		if (!path.isEmpty() && PATHESP.getValueState()) {
			for (int i = 0; i < targets.size(); i++) {
				try {
					if (test != null)

						for (Vec3 pos : test[i]) {

							if (pos != null)
								drawPath(pos);
						}
					if (test != null) {
						glPushMatrix();

						glDisable(GL_TEXTURE_2D);
						glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
						glEnable(GL_LINE_SMOOTH);
						glEnable(GL_BLEND);
						glDisable(GL_DEPTH_TEST);
						mc.entityRenderer.disableLightmap();
						glBegin(GL_LINE_STRIP);
						RenderUtils.glColor(-1);
						final double renderPosX = mc.getRenderManager().viewerPosX;
						final double renderPosY = mc.getRenderManager().viewerPosY;
						final double renderPosZ = mc.getRenderManager().viewerPosZ;

						for (final Vec3 pos : test[i]) {

							glVertex3d(pos.getX() - renderPosX, pos.getY() - renderPosY, pos.getZ() - renderPosZ);

						}

						glColor4d(1, 1, 1, 1);
						glEnd();
						glEnable(GL_DEPTH_TEST);
						glDisable(GL_LINE_SMOOTH);
						glDisable(GL_BLEND);
						glEnable(GL_TEXTURE_2D);
						glPopMatrix();
					}
				} catch (Exception e) {

				}
			}

			if (cps.check(1000)) {
				test = new ArrayList[50];
				path.clear();
			}
		}
	}

	private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
		if (!canPassThrow(new BlockPos(topFrom.mc()))) {
			topFrom = topFrom.addVector(0, 1, 0);
		}
		AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
		pathfinder.compute();

		int i = 0;
		Vec3 lastLoc = null;
		Vec3 lastDashLoc = null;
		ArrayList<Vec3> path = new ArrayList<Vec3>();
		ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
		for (Vec3 pathElm : pathFinderPath) {
			if (i == 0 || i == pathFinderPath.size() - 1) {
				if (lastLoc != null) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
				}
				path.add(pathElm.addVector(0.5, 0, 0.5));
				lastDashLoc = pathElm;
			} else {
				boolean canContinue = true;
				if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
					canContinue = false;
				} else {
					double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
					double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
					double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
					double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
					double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
					double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
					cordsLoop: for (int x = (int) smallX; x <= bigX; x++) {
						for (int y = (int) smallY; y <= bigY; y++) {
							for (int z = (int) smallZ; z <= bigZ; z++) {
								if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
									canContinue = false;
									break cordsLoop;
								}
							}
						}
					}
				}
				if (!canContinue) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
					lastDashLoc = lastLoc;
				}
			}
			lastLoc = pathElm;
			i++;
		}
		return path;
	}

	private boolean canPassThrow(BlockPos pos) {
		Minecraft.getMinecraft();
		Block block = Minecraft.theWorld
				.getBlockState(new net.minecraft.util.BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
		return block.getMaterial() == Material.air || block.getMaterial() == Material.plants
				|| block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water
				|| block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
	}

	boolean validEntity(EntityLivingBase entity) {
		float range = RANGE.getValueState().floatValue();
		double diffY = entity.posY - Minecraft.thePlayer.posY;
		
		if(MathHelper.sqrt_double(diffY * diffY) > YDistanceLimit.getValueState().doubleValue())
			return false;
		
		if ((Minecraft.thePlayer.isEntityAlive()) && !(entity instanceof EntityPlayerSP)) {
			if (Minecraft.thePlayer.getDistanceToEntity(entity) <= range) {

				if (entity.getDisplayName().getUnformattedText().startsWith("[NPC]"))
					return false;

				if (entity == Minecraft.thePlayer)
					return false;

				if (entity.isPlayerSleeping()) {
					return false;
				}
				if (FriendManager.isFriend(entity.getName())) {
					return false;
				}
				
//				if (entity instanceof EntityArmorStand && LOGGED.getValueState()) {
//					if (!Teams.isOnSameTeam(entity) && ((EntityArmorStand)  entity).hasCustomName() && ((EntityArmorStand)  entity).getDisplayName().getUnformattedText().contains("LOGGED"))
//						return true;
//				}

				if (entity instanceof EntityWither && Wither.getValueState() && !entity.isInvisible()) {
					if (!Teams.isOnSameTeam(entity))
						return true;
				}

				if (entity instanceof EntityPlayer) {
					if (Player.getValueState()) {

						EntityPlayer player = (EntityPlayer) entity;
						if (!player.isEntityAlive() && player.getHealth() == 0.0) {
							return false;
						} else if (Teams.isOnSameTeam(player)) {
							return false;
						} else if (FriendManager.isFriend(player.getName())) {
							return false;
						} else if (!entity.isInvisible())
							return true;
					}
				} else {
					if (!entity.isEntityAlive()) {

						return false;
					}
				}

			}
		}

		return false;
	}

	public Exception ex = new Exception();

	private List<EntityLivingBase> getTargets() {
		List<EntityLivingBase> targets = new ArrayList<>();

		if (Minecraft.thePlayer.isDead)
			return targets;

		Minecraft.theWorld.getLoadedEntityList().stream().anyMatch(o -> {

			if (o instanceof EntityLivingBase) {
				EntityLivingBase entity = (EntityLivingBase) o;
				if (validEntity(entity)) {

					if (entity instanceof EntityWither) {

						targets.clear();
						targets.add(entity);

						return true;
					}

					targets.add(entity);
				}
			}

			return false;

		});

		targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(Minecraft.thePlayer)
				- o2.getDistanceToEntity(Minecraft.thePlayer)));

		return targets;
	}

	public void drawESP(Entity entity, int color) {
		double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;

		double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;

		double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
		double width = Math.abs(entity.boundingBox.maxX - entity.boundingBox.minX);
		double height = Math.abs(entity.boundingBox.maxY - entity.boundingBox.minY);
		Vec3 vec = new Vec3(x - width / 2, y, z - width / 2);
		Vec3 vec2 = new Vec3(x + width / 2, y + height, z + width / 2);
		RenderUtils.pre3D();
		mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
		RenderUtils.glColor(color);
		RenderUtil.drawBoundingBox(new AxisAlignedBB(vec.getX() - mc.getRenderManager().renderPosX,
				vec.getY() - mc.getRenderManager().renderPosY, vec.getZ() - mc.getRenderManager().renderPosZ,
				vec2.getX() - mc.getRenderManager().renderPosX, vec2.getY() - mc.getRenderManager().renderPosY,
				vec2.getZ() - mc.getRenderManager().renderPosZ));

		GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		RenderUtils.post3D();
	}

	public void drawPath(Vec3 vec) {
		double x = vec.getX() - mc.getRenderManager().renderPosX;
		double y = vec.getY() - mc.getRenderManager().renderPosY;
		double z = vec.getZ() - mc.getRenderManager().renderPosZ;
		double width = 0.3;
		double height = Minecraft.thePlayer.getEyeHeight();
		RenderUtils.pre3D();
		GL11.glLoadIdentity();
		mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
		int colors[] = { Colors.getColor(Color.black), Colors.getColor(Color.CYAN) };
		for (int i = 0; i < 2; i++) {
			RenderUtils.glColor(colors[i]);
			GL11.glLineWidth(3 - i * 2);
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(x - width, y, z - width);
			GL11.glVertex3d(x - width, y, z - width);
			GL11.glVertex3d(x - width, y + height, z - width);
			GL11.glVertex3d(x + width, y + height, z - width);
			GL11.glVertex3d(x + width, y, z - width);
			GL11.glVertex3d(x - width, y, z - width);
			GL11.glVertex3d(x - width, y, z + width);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(x + width, y, z + width);
			GL11.glVertex3d(x + width, y + height, z + width);
			GL11.glVertex3d(x - width, y + height, z + width);
			GL11.glVertex3d(x - width, y, z + width);
			GL11.glVertex3d(x + width, y, z + width);
			GL11.glVertex3d(x + width, y, z - width);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(x + width, y + height, z + width);
			GL11.glVertex3d(x + width, y + height, z - width);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(x - width, y + height, z + width);
			GL11.glVertex3d(x - width, y + height, z - width);
			GL11.glEnd();
		}

		RenderUtils.post3D();
	}

}
