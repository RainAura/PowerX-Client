package net.minecraft.network.play.client;

import net.minecraft.network.play.*;
import net.minecraft.entity.*;
import java.io.*;
import net.minecraft.network.*;

public class C20PacketVehicleMove implements Packet<INetHandlerPlayServer>
{
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    
    public C20PacketVehicleMove() {
    	
    }
    
    public C20PacketVehicleMove(final Entity entityIn) {
        this.x = entityIn.posX;
        this.y = entityIn.posY;
        this.z = entityIn.posZ;
        this.yaw = entityIn.rotationYaw;
        this.pitch = entityIn.rotationPitch;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
    }

    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public float getPitch() {
        return this.pitch;
    }


	public void processPacket(INetHandlerPlayServer handler) {
	}
    
}
