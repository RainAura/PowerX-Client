package net.optifine.override;

import net.optifine.reflect.Reflector;
import net.minecraft.world.WorldType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;
import java.util.Arrays;
import net.optifine.DynamicLights;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.optifine.util.ArrayCache;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;

public class ChunkCacheOF implements IBlockAccess
{
    private final ChunkCache chunkCache;
    private final int posX;
    private final int posY;
    private final int posZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final int sizeXY;
    private int[] combinedLights;
    private IBlockState[] blockStates;
    private final int arraySize;
    private final boolean dynamicLights;
    private static final ArrayCache cacheCombinedLights;
    private static final ArrayCache cacheBlockStates;
    
    public ChunkCacheOF(final ChunkCache chunkCache, final BlockPos posFromIn, final BlockPos posToIn, final int subIn) {
        this.dynamicLights = Config.isDynamicLights();
        this.chunkCache = chunkCache;
        final int minChunkX = posFromIn.getX() - subIn >> 4;
        final int minChunkY = posFromIn.getY() - subIn >> 4;
        final int minChunkZ = posFromIn.getZ() - subIn >> 4;
        final int maxChunkX = posToIn.getX() + subIn >> 4;
        final int maxChunkY = posToIn.getY() + subIn >> 4;
        final int maxChunkZ = posToIn.getZ() + subIn >> 4;
        this.sizeX = maxChunkX - minChunkX + 1 << 4;
        this.sizeY = maxChunkY - minChunkY + 1 << 4;
        this.sizeZ = maxChunkZ - minChunkZ + 1 << 4;
        this.sizeXY = this.sizeX * this.sizeY;
        this.arraySize = this.sizeX * this.sizeY * this.sizeZ;
        this.posX = minChunkX << 4;
        this.posY = minChunkY << 4;
        this.posZ = minChunkZ << 4;
    }
    
    private int getPositionIndex(final BlockPos pos) {
        final int dx = pos.getX() - this.posX;
        if (dx < 0 || dx >= this.sizeX) {
            return -1;
        }
        final int dy = pos.getY() - this.posY;
        if (dy < 0 || dy >= this.sizeY) {
            return -1;
        }
        final int dz = pos.getZ() - this.posZ;
        if (dz < 0 || dz >= this.sizeZ) {
            return -1;
        }
        return dz * this.sizeXY + dy * this.sizeX + dx;
    }
    
    @Override
    public int getCombinedLight(final BlockPos pos, final int lightValue) {
        final int index = this.getPositionIndex(pos);
        if (index < 0 || index >= this.arraySize || this.combinedLights == null) {
            return this.getCombinedLightRaw(pos, lightValue);
        }
        int light = this.combinedLights[index];
        if (light == -1) {
            light = this.getCombinedLightRaw(pos, lightValue);
            this.combinedLights[index] = light;
        }
        return light;
    }
    
    private int getCombinedLightRaw(final BlockPos pos, final int lightValue) {
        int light = this.chunkCache.getCombinedLight(pos, lightValue);
        if (this.dynamicLights && !this.getBlockState(pos).getBlock().isOpaqueCube()) {
            light = DynamicLights.getCombinedLight(pos, light);
        }
        return light;
    }
    
    @Override
    public IBlockState getBlockState(final BlockPos pos) {
        final int index = this.getPositionIndex(pos);
        if (index < 0 || index >= this.arraySize || this.blockStates == null) {
            return this.chunkCache.getBlockState(pos);
        }
        IBlockState iblockstate = this.blockStates[index];
        if (iblockstate == null) {
            iblockstate = this.chunkCache.getBlockState(pos);
            this.blockStates[index] = iblockstate;
        }
        return iblockstate;
    }
    
    public void renderStart() {
        if (this.combinedLights == null) {
            this.combinedLights = (int[])ChunkCacheOF.cacheCombinedLights.allocate(this.arraySize);
        }
        Arrays.fill(this.combinedLights, -1);
        if (this.blockStates == null) {
            this.blockStates = (IBlockState[])ChunkCacheOF.cacheBlockStates.allocate(this.arraySize);
        }
        Arrays.fill(this.blockStates, null);
    }
    
    public void renderFinish() {
        ChunkCacheOF.cacheCombinedLights.free(this.combinedLights);
        this.combinedLights = null;
        ChunkCacheOF.cacheBlockStates.free(this.blockStates);
        this.blockStates = null;
    }
    
    @Override
    public boolean extendedLevelsInChunkCache() {
        return this.chunkCache.extendedLevelsInChunkCache();
    }
    
    @Override
    public BiomeGenBase getBiomeGenForCoords(final BlockPos pos) {
        return this.chunkCache.getBiomeGenForCoords(pos);
    }
    
    @Override
    public int getStrongPower(final BlockPos pos, final EnumFacing direction) {
        return this.chunkCache.getStrongPower(pos, direction);
    }
    
    @Override
    public TileEntity getTileEntity(final BlockPos pos) {
        return this.chunkCache.getTileEntity(pos);
    }
    
    @Override
    public WorldType getWorldType() {
        return this.chunkCache.getWorldType();
    }
    
    @Override
    public boolean isAirBlock(final BlockPos pos) {
        return this.chunkCache.isAirBlock(pos);
    }
    
    
    static {
        cacheCombinedLights = new ArrayCache(Integer.TYPE, 16);
        cacheBlockStates = new ArrayCache(IBlockState.class, 16);
    }
}
