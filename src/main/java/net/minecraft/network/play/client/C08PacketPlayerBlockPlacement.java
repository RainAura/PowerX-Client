package net.minecraft.network.play.client;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.network.play.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import java.io.*;
import net.minecraft.network.*;

public class C08PacketPlayerBlockPlacement implements Packet<INetHandlerPlayServer>
{
	private static final BlockPos field_179726_a;
	public BlockPos position;
	private int placedBlockDirection;
	private ItemStack stack;
	public float facingX;
	public float facingY;
	public float facingZ;

	public C08PacketPlayerBlockPlacement() {
	}

	public C08PacketPlayerBlockPlacement(final ItemStack stackIn) {
		this(C08PacketPlayerBlockPlacement.field_179726_a, 255, stackIn, 0.0f, 0.0f, 0.0f);
	}

	public C08PacketPlayerBlockPlacement(final BlockPos positionIn, final int placedBlockDirectionIn, final ItemStack stackIn, final float facingXIn, final float facingYIn, final float facingZIn) {
		this.position = positionIn;
		this.placedBlockDirection = placedBlockDirectionIn;
		this.stack = ((stackIn != null) ? stackIn.copy() : null);
		this.facingX = facingXIn;
		this.facingY = facingYIn;
		this.facingZ = facingZIn;
	}

	public void readPacketData(final PacketBuffer buf) throws IOException {
		final float unsignedByte = (ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() <= 47) ? (buf.readUnsignedByte() / 16.0f) : buf.readUnsignedByte();
		this.position = buf.readBlockPos();
		this.placedBlockDirection = buf.readUnsignedByte();
		this.stack = buf.readItemStackFromBuffer();
		this.facingX = unsignedByte;
		this.facingY = unsignedByte;
		this.facingZ = unsignedByte;
	}

	public void writePacketData(final PacketBuffer buf) throws IOException {
		buf.writeBlockPos(this.position);
		buf.writeByte(this.placedBlockDirection);
		buf.writeItemStackToBuffer(this.stack);
		buf.writeByte((int)((ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() <= 47) ? (this.facingX * 16.0f) : this.facingX));
		buf.writeByte((int)((ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() <= 47) ? (this.facingY * 16.0f) : this.facingY));
		buf.writeByte((int)((ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() <= 47) ? (this.facingZ * 16.0f) : this.facingZ));
	}

	public void processPacket(final INetHandlerPlayServer handler) {
		handler.processPlayerBlockPlacement(this);
	}

	public BlockPos getPosition() {
		return this.position;
	}

	public int getPlacedBlockDirection() {
		return this.placedBlockDirection;
	}

	public ItemStack getStack() {
		return this.stack;
	}

	public void setStack(final ItemStack itemStack) {
		this.stack = itemStack;
	}

	public float getPlacedBlockOffsetX() {
		return this.facingX;
	}

	public float getPlacedBlockOffsetY() {
		return this.facingY;
	}

	public float getPlacedBlockOffsetZ() {
		return this.facingZ;
	}

	static {
		field_179726_a = new BlockPos(-1, -1, -1);
	}
}
