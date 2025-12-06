package cn.Power.mod.mods.WORLD;

import java.util.Iterator;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRespawn;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.util.Helper;
import cn.Power.util.PlayerUtil;
import cn.Power.util.RotationUtil;
import cn.Power.util.misc.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

public class Nuker extends Mod {
	
	private Value<Double> radius = new Value<Double>("Nuker_radius", 3.0, 1.0, 6.0, 1);
	private Value<Double> height = new Value<Double>("Nuker_height", 3.0, 1.0, 6.0, 1);
	public Value<Boolean> chest = new Value<Boolean>("Nuker_Chest", false);
	public Value<Boolean> delay = new Value<Boolean>("Nuker_Delay", false);
	
    private int posX, posY, posZ;
    private boolean isRunning;
    private Timer timer = new Timer();

	public Nuker() {
		super("Nuker", Category.WORLD);
	}

	@Override
	public void onEnable() {
	      	isRunning = false;
	        posX = posY = posZ = 0;
	}	
	
	@Override
	public void onDisable() {
	      	isRunning = false;
	        posX = posY = posZ = 0;
	        
	        mc.timer.timerSpeed = 1.0f;
	}
	
	@EventTarget
	public void onRespawn(EventRespawn es) {
		EventManager.unregister(this);
		this.set(false);
		
		mc.timer.timerSpeed = 1.0f;
	}
	
	
	@EventTarget
	public void onPost(EventPostMotion e) {
		Block block = mc.theWorld.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getBlock();
        if(this.isRunning) {
        	
       
        	
            this.mc.thePlayer.swingItem();
            
            this.mc.playerController.onPlayerDamageBlock(new BlockPos(this.posX , this.posY, this.posZ), getFacing(new BlockPos(this.posX, this.posY, this.posZ)));
        	       	
        	
            if((double)this.mc.playerController.curBlockDamageMP >= 0.71D)
               timer.reset();

        }
	}
	
	@EventTarget
	public void onPre(EventPreMotion e) {
		this.isRunning = false;
		
        int radius1 = radius.getValueState().intValue();
        int height1 = height.getValueState().intValue();
        for(int y = height1; y >= -height1; --y) {
            for(int x = -radius1; x < radius1; ++x) {
                for(int z = -radius1; z < radius1; ++z) {
                    this.posX = (int)Math.floor(this.mc.thePlayer.posX) + x;
                    this.posY = (int)Math.floor(this.mc.thePlayer.posY) + y;
                    this.posZ = (int)Math.floor(this.mc.thePlayer.posZ) + z;
                    if(this.mc.thePlayer.getDistanceSq(this.mc.thePlayer.posX + (double)x, this.mc.thePlayer.posY + (double)y, this.mc.thePlayer.posZ + (double)z) <= 16.0D) {
                        Block block = mc.theWorld.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getBlock();
                        boolean blockChecks = timer.check(2L) || !delay.getValueState();
                        Block selected =  mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();

                        blockChecks = blockChecks && this.getFacing(new BlockPos(this.posX + 0.5F, this.posY + 0.9f, this.posZ + 0.5F)) != null && !(block instanceof BlockAir) && (!(block instanceof BlockChest) || chest.getValueState());
                        blockChecks = blockChecks && (block.getBlockHardness(this.mc.theWorld, BlockPos.ORIGIN) != -1.0F || this.mc.playerController.isInCreativeMode());
                        if(blockChecks) {
                            this.isRunning = true;
                            
                            float[] angles = RotationUtil.grabBlockRotations(new BlockPos(this.posX + 0.5F, this.posY + 0.9, this.posZ + 0.5F));
//                            if(silent.value){
//
                            mc.thePlayer.rotationYawHead = angles[0];
                            mc.thePlayer.renderArmPitch = angles[1];
                            
                            	e.setYaw(angles[0]);
                                e.setPitch(angles[1]);
//                            } else {
                            	
 //                               mc.thePlayer.rotationYaw = angles[0];
 //                               mc.thePlayer.rotationPitch = angles[1];
 //                           }
                            return;
                        }
                    }
                }
            }
        }
	}
	
    public static EnumFacing getFacing(BlockPos pos) {
        EnumFacing[] orderedValues = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.DOWN};
        EnumFacing[] var2 = orderedValues;
        int var3 = orderedValues.length;
        for(int var4 = 0; var4 < var3; ++var4) {
            EnumFacing facing = var2[var4];
            EntitySnowball temp = new EntitySnowball(Helper.world());
            temp.posX = (double)pos.getX() + 0.5D;
            temp.posY = (double)pos.getY() + 0.5D;
            temp.posZ = (double)pos.getZ() + 0.5D;
            temp.posX += (double)facing.getDirectionVec().getX() * 0.5D;
            temp.posY += (double)facing.getDirectionVec().getY() * 0.5D;
            temp.posZ += (double)facing.getDirectionVec().getZ() * 0.5D;
            if(Helper.mc().thePlayer.canEntityBeSeen(temp)) {
                return facing;
            }
        }

        return null;
    }

}