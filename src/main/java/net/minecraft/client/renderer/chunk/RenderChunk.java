package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.world.ChunkCache;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockRedstoneWire;
import net.optifine.CustomBlockLayers;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.optifine.render.RenderEnv;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import java.util.Iterator;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.optifine.override.ChunkCacheOF;
import java.util.HashSet;
import java.util.Collection;
import java.util.BitSet;
import net.optifine.shaders.SVertexBuilder;
import net.minecraft.world.IBlockAccess;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.optifine.reflect.ReflectorForge;
import net.optifine.BlockPosM;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3i;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.OpenGlHelper;
import net.optifine.reflect.Reflector;
import net.minecraft.src.Config;
import net.minecraft.client.renderer.GLAllocation;
import com.google.common.collect.Sets;
import net.optifine.render.AabbFrame;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.EnumFacing;
import java.util.EnumMap;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import java.nio.FloatBuffer;
import net.minecraft.tileentity.TileEntity;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.util.BlockPos;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.world.World;

public class RenderChunk
{
    private final World world;
    private final RenderGlobal renderGlobal;
    public static int renderChunksUpdated;
    private BlockPos position;
    public CompiledChunk compiledChunk;
    private final ReentrantLock lockCompileTask;
    private final ReentrantLock lockCompiledChunk;
    private ChunkCompileTaskGenerator compileTask;
    private final Set<TileEntity> setTileEntities;
    private final int index;
    private final FloatBuffer modelviewMatrix;
    private final VertexBuffer[] vertexBuffers;
    public AxisAlignedBB boundingBox;
    private int frameIndex;
    private boolean needsUpdate;
    private EnumMap<EnumFacing, BlockPos> mapEnumFacing;
    private BlockPos[] positionOffsets16;
    public static final EnumWorldBlockLayer[] ENUM_WORLD_BLOCK_LAYERS;
    private final EnumWorldBlockLayer[] blockLayersSingle;
    private final boolean isMipmaps;
    private final boolean fixBlockLayer;
    private boolean playerUpdate;
    public int regionX;
    public int regionZ;
    private final RenderChunk[] renderChunksOfset16;
    private boolean renderChunksOffset16Updated;
    private Chunk chunk;
    private RenderChunk[] renderChunkNeighbours;
    private RenderChunk[] renderChunkNeighboursValid;
    private boolean renderChunkNeighboursUpated;
    private RenderGlobal.ContainerLocalRenderInformation renderInfo;
    public AabbFrame boundingBoxParent;
    
    public RenderChunk(final World worldIn, final RenderGlobal renderGlobalIn, final BlockPos blockPosIn, final int indexIn) {
        this.compiledChunk = CompiledChunk.DUMMY;
        this.lockCompileTask = new ReentrantLock();
        this.lockCompiledChunk = new ReentrantLock();
        this.compileTask = null;
        this.setTileEntities = Sets.newHashSet();
        this.modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
        this.vertexBuffers = new VertexBuffer[EnumWorldBlockLayer.values().length];
        this.frameIndex = -1;
        this.needsUpdate = true;
        this.mapEnumFacing = null;
        this.positionOffsets16 = new BlockPos[EnumFacing.VALUES.length];
        this.blockLayersSingle = new EnumWorldBlockLayer[1];
        this.isMipmaps = Config.isMipmaps();
        this.fixBlockLayer = true;
        this.playerUpdate = false;
        this.renderChunksOfset16 = new RenderChunk[6];
        this.renderChunksOffset16Updated = false;
        this.renderChunkNeighbours = new RenderChunk[EnumFacing.VALUES.length];
        this.renderChunkNeighboursValid = new RenderChunk[EnumFacing.VALUES.length];
        this.renderChunkNeighboursUpated = false;
        this.renderInfo = new RenderGlobal.ContainerLocalRenderInformation(this, null, 0);
        this.world = worldIn;
        this.renderGlobal = renderGlobalIn;
        this.index = indexIn;
        if (!blockPosIn.equals(this.getPosition())) {
            this.setPosition(blockPosIn);
        }
        if (OpenGlHelper.useVbo()) {
            for (int i = 0; i < EnumWorldBlockLayer.values().length; ++i) {
                this.vertexBuffers[i] = new VertexBuffer(DefaultVertexFormats.BLOCK);
            }
        }
    }
    
    public boolean setFrameIndex(final int frameIndexIn) {
        if (this.frameIndex == frameIndexIn) {
            return false;
        }
        this.frameIndex = frameIndexIn;
        return true;
    }
    
    public VertexBuffer getVertexBufferByLayer(final int layer) {
        return this.vertexBuffers[layer];
    }
    
    public void setPosition(final BlockPos pos) {
        this.stopCompileTask();
        this.position = pos;
        final int bits = 8;
        this.regionX = pos.getX() >> bits << bits;
        this.regionZ = pos.getZ() >> bits << bits;
        this.boundingBox = new AxisAlignedBB(pos, pos.add(16, 16, 16));
        this.initModelviewMatrix();
        for (int i = 0; i < this.positionOffsets16.length; ++i) {
            this.positionOffsets16[i] = null;
        }
        this.renderChunksOffset16Updated = false;
        this.renderChunkNeighboursUpated = false;
        for (int i = 0; i < this.renderChunkNeighbours.length; ++i) {
            final RenderChunk neighbour = this.renderChunkNeighbours[i];
            if (neighbour != null) {
                neighbour.renderChunkNeighboursUpated = false;
            }
        }
        this.chunk = null;
        this.boundingBoxParent = null;
    }
    
    public void resortTransparency(final float x, final float y, final float z, final ChunkCompileTaskGenerator generator) {
        final CompiledChunk compiledchunk = generator.getCompiledChunk();
        if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(EnumWorldBlockLayer.TRANSLUCENT)) {
            final WorldRenderer bufferTranslucent = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT);
            this.preRenderBlocks(bufferTranslucent, this.position);
            bufferTranslucent.setVertexState(compiledchunk.getState());
            this.postRenderBlocks(EnumWorldBlockLayer.TRANSLUCENT, x, y, z, bufferTranslucent, compiledchunk);
        }
    }
    
    public void rebuildChunk(final float x, final float y, final float z, final ChunkCompileTaskGenerator generator) {
        final CompiledChunk compiledchunk = new CompiledChunk();
        final int i = 1;
        final BlockPos blockpos = new BlockPos(this.position);
        final BlockPos blockpos2 = blockpos.add(15, 15, 15);
        generator.getLock().lock();
        try {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
                return;
            }
            generator.setCompiledChunk(compiledchunk);
        }
        finally {
            generator.getLock().unlock();
        }
        final VisGraph lvt_10_1_ = new VisGraph();
        final HashSet lvt_11_1_ = Sets.newHashSet();
        if (!this.isChunkRegionEmpty(blockpos)) {
            ++RenderChunk.renderChunksUpdated;
            final ChunkCacheOF blockAccess = this.makeChunkCacheOF(blockpos);
            blockAccess.renderStart();
            final boolean[] aboolean = new boolean[RenderChunk.ENUM_WORLD_BLOCK_LAYERS.length];
            final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            final boolean forgeBlockCanRenderInLayerExists = false;
            final boolean forgeHooksSetRenderLayerExists = false;
            for (final Object o : BlockPosM.getAllInBoxMutable(blockpos, blockpos2)) {
            	BlockPosM blockpos$mutableblockpos = (BlockPosM) o;
            	
                final IBlockState iblockstate = blockAccess.getBlockState(blockpos$mutableblockpos);
                final Block block = iblockstate.getBlock();
                if (block.isOpaqueCube()) {
                    lvt_10_1_.func_178606_a(blockpos$mutableblockpos);
                }
                if (iblockstate.getBlock().hasTileEntity()) {
                    final TileEntity tileentity = blockAccess.getTileEntity(new BlockPos(blockpos$mutableblockpos));
                    final TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tileentity);
                    if (tileentity != null && tileentityspecialrenderer != null) {
                        compiledchunk.addTileEntity(tileentity);
                        if (tileentityspecialrenderer.forceTileEntityRender()) {
                            lvt_11_1_.add(tileentity);
                        }
                    }
                }
                EnumWorldBlockLayer[] blockLayers;
                if (forgeBlockCanRenderInLayerExists) {
                    blockLayers = RenderChunk.ENUM_WORLD_BLOCK_LAYERS;
                }
                else {
                    blockLayers = this.blockLayersSingle;
                    blockLayers[0] = block.getBlockLayer();
                }
                for (int ix = 0; ix < blockLayers.length; ++ix) {
                    EnumWorldBlockLayer enumworldblocklayer1 = blockLayers[ix];

                    enumworldblocklayer1 = this.fixBlockLayer(iblockstate, enumworldblocklayer1);
                    final int j = enumworldblocklayer1.ordinal();
                    if (block.getRenderType() != -1) {
                        final WorldRenderer vertexbuffer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(j);
                        vertexbuffer.setBlockLayer(enumworldblocklayer1);
                        final RenderEnv renderEnv = vertexbuffer.getRenderEnv(iblockstate, blockpos$mutableblockpos);
                        renderEnv.setRegionRenderCacheBuilder(generator.getRegionRenderCacheBuilder());
                        if (!compiledchunk.isLayerStarted(enumworldblocklayer1)) {
                            compiledchunk.setLayerStarted(enumworldblocklayer1);
                            this.preRenderBlocks(vertexbuffer, blockpos);
                        }
                        final boolean[] array = aboolean;
                        final int n = j;
                        array[n] |= blockrendererdispatcher.renderBlock(iblockstate, blockpos$mutableblockpos, blockAccess, vertexbuffer);
                        if (renderEnv.isOverlaysRendered()) {
                            this.postRenderOverlays(generator.getRegionRenderCacheBuilder(), compiledchunk, aboolean);
                            renderEnv.setOverlaysRendered(false);
                        }
                    }
                }
            }
            for (final EnumWorldBlockLayer enumworldblocklayer2 : RenderChunk.ENUM_WORLD_BLOCK_LAYERS) {
                if (aboolean[enumworldblocklayer2.ordinal()]) {
                    compiledchunk.setLayerUsed(enumworldblocklayer2);
                }
                if (compiledchunk.isLayerStarted(enumworldblocklayer2)) {
                    if (Config.isShaders()) {
                        SVertexBuilder.calcNormalChunkLayer(generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumworldblocklayer2));
                    }
                    final WorldRenderer bufferBuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumworldblocklayer2);
                    this.postRenderBlocks(enumworldblocklayer2, x, y, z, bufferBuilder, compiledchunk);
                    if (bufferBuilder.animatedSprites != null) {
                        compiledchunk.setAnimatedSprites(enumworldblocklayer2, (BitSet)bufferBuilder.animatedSprites.clone());
                    }
                }
                else {
                    compiledchunk.setAnimatedSprites(enumworldblocklayer2, null);
                }
            }
            blockAccess.renderFinish();
        }
        compiledchunk.setVisibility(lvt_10_1_.computeVisibility());
        this.lockCompileTask.lock();
        try {
            final Set<TileEntity> set = (Set<TileEntity>)Sets.newHashSet((Iterable)lvt_11_1_);
            final Set<TileEntity> set2 = (Set<TileEntity>)Sets.newHashSet((Iterable)this.setTileEntities);
            set.removeAll(this.setTileEntities);
            set2.removeAll(lvt_11_1_);
            this.setTileEntities.clear();
            this.setTileEntities.addAll(lvt_11_1_);
            this.renderGlobal.updateTileEntities(set2, set);
        }
        finally {
            this.lockCompileTask.unlock();
        }
    }
    
    protected void finishCompileTask() {
        this.lockCompileTask.lock();
        try {
            if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
                this.compileTask.finish();
                this.compileTask = null;
            }
        }
        finally {
            this.lockCompileTask.unlock();
        }
    }
    
    public ReentrantLock getLockCompileTask() {
        return this.lockCompileTask;
    }
    
    public ChunkCompileTaskGenerator makeCompileTaskChunk() {
        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator chunkcompiletaskgenerator;
        try {
            this.finishCompileTask();
            this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
            chunkcompiletaskgenerator = this.compileTask;
        }
        finally {
            this.lockCompileTask.unlock();
        }
        return chunkcompiletaskgenerator;
    }
    
    public ChunkCompileTaskGenerator makeCompileTaskTransparency() {
        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator chunkcompiletaskgenerator;
        try {
            if (this.compileTask == null || this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.PENDING) {
                if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
                    this.compileTask.finish();
                    this.compileTask = null;
                }
                (this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY)).setCompiledChunk(this.compiledChunk);
                chunkcompiletaskgenerator = this.compileTask;
                return chunkcompiletaskgenerator;
            }
            chunkcompiletaskgenerator = null;
        }
        finally {
            this.lockCompileTask.unlock();
        }
        return chunkcompiletaskgenerator;
    }
    
    private void preRenderBlocks(final WorldRenderer worldRendererIn, final BlockPos pos) {
        worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
        if (Config.isRenderRegions()) {
            final int bits = 8;
            int dx = pos.getX() >> bits << bits;
            final int dy = pos.getY() >> bits << bits;
            int dz = pos.getZ() >> bits << bits;
            dx = this.regionX;
            dz = this.regionZ;
            worldRendererIn.setTranslation(-dx, -dy, -dz);
        }
        else {
            worldRendererIn.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
        }
    }
    
    private void postRenderBlocks(final EnumWorldBlockLayer layer, final float x, final float y, final float z, final WorldRenderer worldRendererIn, final CompiledChunk compiledChunkIn) {
        if (layer == EnumWorldBlockLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer)) {
            worldRendererIn.sortVertexData(x, y, z);
            compiledChunkIn.setState(worldRendererIn.getVertexState());
        }
        worldRendererIn.finishDrawing();
    }
    
    private void initModelviewMatrix() {
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        final float f = 1.000001f;
        GlStateManager.translate(-8.0f, -8.0f, -8.0f);
        GlStateManager.scale(f, f, f);
        GlStateManager.translate(8.0f, 8.0f, 8.0f);
        GlStateManager.getFloat(2982, this.modelviewMatrix);
        GlStateManager.popMatrix();
    }
    
    public void multModelviewMatrix() {
        GlStateManager.multMatrix(this.modelviewMatrix);
    }
    
    public CompiledChunk getCompiledChunk() {
        return this.compiledChunk;
    }
    
    public void setCompiledChunk(final CompiledChunk compiledChunkIn) {
        this.lockCompiledChunk.lock();
        try {
            this.compiledChunk = compiledChunkIn;
        }
        finally {
            this.lockCompiledChunk.unlock();
        }
    }
    
    public void stopCompileTask() {
        this.finishCompileTask();
        this.compiledChunk = CompiledChunk.DUMMY;
    }
    
    public void deleteGlResources() {
        this.stopCompileTask();
        for (int i = 0; i < EnumWorldBlockLayer.values().length; ++i) {
            if (this.vertexBuffers[i] != null) {
                this.vertexBuffers[i].deleteGlBuffers();
            }
        }
    }
    
    public BlockPos getPosition() {
        return this.position;
    }
    
    public void setNeedsUpdate(final boolean needsUpdateIn) {
        this.needsUpdate = needsUpdateIn;
        if (needsUpdateIn) {
            if (this.isWorldPlayerUpdate()) {
                this.playerUpdate = true;
            }
        }
        else {
            this.playerUpdate = false;
        }
    }
    
    public boolean isNeedsUpdate() {
        return this.needsUpdate;
    }
    
    public BlockPos getBlockPosOffset16(final EnumFacing p_181701_1_) {
        return this.getPositionOffset16(p_181701_1_);
    }
    
    public BlockPos getPositionOffset16(final EnumFacing p_getPositionOffset16_1_) {
        final int index = p_getPositionOffset16_1_.getIndex();
        BlockPos posOffset = this.positionOffsets16[index];
        if (posOffset == null) {
            posOffset = this.getPosition().offset(p_getPositionOffset16_1_, 16);
            this.positionOffsets16[index] = posOffset;
        }
        return posOffset;
    }
    
    private boolean isWorldPlayerUpdate() {
        if (this.world instanceof WorldClient) {
            final WorldClient worldClient = (WorldClient)this.world;
            return worldClient.isPlayerUpdate();
        }
        return false;
    }
    
    public boolean isPlayerUpdate() {
        return this.playerUpdate;
    }
    
    protected RegionRenderCache createRegionRenderCache(final World p_createRegionRenderCache_1_, final BlockPos p_createRegionRenderCache_2_, final BlockPos p_createRegionRenderCache_3_, final int p_createRegionRenderCache_4_) {
        return new RegionRenderCache(p_createRegionRenderCache_1_, p_createRegionRenderCache_2_, p_createRegionRenderCache_3_, p_createRegionRenderCache_4_);
    }
    
    private EnumWorldBlockLayer fixBlockLayer(final IBlockState p_fixBlockLayer_1_, final EnumWorldBlockLayer p_fixBlockLayer_2_) {
        if (CustomBlockLayers.isActive()) {
            final EnumWorldBlockLayer layerCustom = CustomBlockLayers.getRenderLayer(p_fixBlockLayer_1_);
            if (layerCustom != null) {
                return layerCustom;
            }
        }
        if (!this.fixBlockLayer) {
            return p_fixBlockLayer_2_;
        }
        if (this.isMipmaps) {
            if (p_fixBlockLayer_2_ == EnumWorldBlockLayer.CUTOUT) {
                final Block block = p_fixBlockLayer_1_.getBlock();
                if (block instanceof BlockRedstoneWire) {
                    return p_fixBlockLayer_2_;
                }
                if (block instanceof BlockCactus) {
                    return p_fixBlockLayer_2_;
                }
                return EnumWorldBlockLayer.CUTOUT_MIPPED;
            }
        }
        else if (p_fixBlockLayer_2_ == EnumWorldBlockLayer.CUTOUT_MIPPED) {
            return EnumWorldBlockLayer.CUTOUT;
        }
        return p_fixBlockLayer_2_;
    }
    
    private void postRenderOverlays(final RegionRenderCacheBuilder p_postRenderOverlays_1_, final CompiledChunk p_postRenderOverlays_2_, final boolean[] p_postRenderOverlays_3_) {
        this.postRenderOverlay(EnumWorldBlockLayer.CUTOUT, p_postRenderOverlays_1_, p_postRenderOverlays_2_, p_postRenderOverlays_3_);
        this.postRenderOverlay(EnumWorldBlockLayer.CUTOUT_MIPPED, p_postRenderOverlays_1_, p_postRenderOverlays_2_, p_postRenderOverlays_3_);
        this.postRenderOverlay(EnumWorldBlockLayer.TRANSLUCENT, p_postRenderOverlays_1_, p_postRenderOverlays_2_, p_postRenderOverlays_3_);
    }
    
    private void postRenderOverlay(final EnumWorldBlockLayer p_postRenderOverlay_1_, final RegionRenderCacheBuilder p_postRenderOverlay_2_, final CompiledChunk p_postRenderOverlay_3_, final boolean[] p_postRenderOverlay_4_) {
        final WorldRenderer bufferOverlay = p_postRenderOverlay_2_.getWorldRendererByLayer(p_postRenderOverlay_1_);
        if (bufferOverlay.isDrawing()) {
            p_postRenderOverlay_3_.setLayerStarted(p_postRenderOverlay_1_);
            p_postRenderOverlay_4_[p_postRenderOverlay_1_.ordinal()] = true;
        }
    }
    
    private ChunkCacheOF makeChunkCacheOF(final BlockPos p_makeChunkCacheOF_1_) {
        final BlockPos posFrom = p_makeChunkCacheOF_1_.add(-1, -1, -1);
        final BlockPos posTo = p_makeChunkCacheOF_1_.add(16, 16, 16);
        final ChunkCache chunkCache = this.createRegionRenderCache(this.world, posFrom, posTo, 1);

        final ChunkCacheOF chunkCacheOF = new ChunkCacheOF(chunkCache, posFrom, posTo, 1);
        return chunkCacheOF;
    }
    
    public RenderChunk getRenderChunkOffset16(final ViewFrustum p_getRenderChunkOffset16_1_, final EnumFacing p_getRenderChunkOffset16_2_) {
        if (!this.renderChunksOffset16Updated) {
            for (int i = 0; i < EnumFacing.VALUES.length; ++i) {
                final EnumFacing ef = EnumFacing.VALUES[i];
                final BlockPos posOffset16 = this.getBlockPosOffset16(ef);
                this.renderChunksOfset16[i] = p_getRenderChunkOffset16_1_.getRenderChunk(posOffset16);
            }
            this.renderChunksOffset16Updated = true;
        }
        return this.renderChunksOfset16[p_getRenderChunkOffset16_2_.ordinal()];
    }
    
    public Chunk getChunk() {
        return this.getChunk(this.position);
    }
    
    private Chunk getChunk(final BlockPos p_getChunk_1_) {
        Chunk chunkLocal = this.chunk;
        if (chunkLocal != null && chunkLocal.isLoaded()) {
            return chunkLocal;
        }
        chunkLocal = this.world.getChunkFromBlockCoords(p_getChunk_1_);
        return this.chunk = chunkLocal;
    }
    
    public boolean isChunkRegionEmpty() {
        return this.isChunkRegionEmpty(this.position);
    }
    
    private boolean isChunkRegionEmpty(final BlockPos p_isChunkRegionEmpty_1_) {
        final int yStart = p_isChunkRegionEmpty_1_.getY();
        final int yEnd = yStart + 15;
        return this.getChunk(p_isChunkRegionEmpty_1_).getAreLevelsEmpty(yStart, yEnd);
    }
    
    public void setRenderChunkNeighbour(final EnumFacing p_setRenderChunkNeighbour_1_, final RenderChunk p_setRenderChunkNeighbour_2_) {
        this.renderChunkNeighbours[p_setRenderChunkNeighbour_1_.ordinal()] = p_setRenderChunkNeighbour_2_;
        this.renderChunkNeighboursValid[p_setRenderChunkNeighbour_1_.ordinal()] = p_setRenderChunkNeighbour_2_;
    }
    
    public RenderChunk getRenderChunkNeighbour(final EnumFacing p_getRenderChunkNeighbour_1_) {
        if (!this.renderChunkNeighboursUpated) {
            this.updateRenderChunkNeighboursValid();
        }
        return this.renderChunkNeighboursValid[p_getRenderChunkNeighbour_1_.ordinal()];
    }
    
    public RenderGlobal.ContainerLocalRenderInformation getRenderInfo() {
        return this.renderInfo;
    }
    
    private void updateRenderChunkNeighboursValid() {
        final int x = this.getPosition().getX();
        final int z = this.getPosition().getZ();
        final int north = EnumFacing.NORTH.ordinal();
        final int south = EnumFacing.SOUTH.ordinal();
        final int west = EnumFacing.WEST.ordinal();
        final int east = EnumFacing.EAST.ordinal();
        this.renderChunkNeighboursValid[north] = ((this.renderChunkNeighbours[north].getPosition().getZ() == z - 16) ? this.renderChunkNeighbours[north] : null);
        this.renderChunkNeighboursValid[south] = ((this.renderChunkNeighbours[south].getPosition().getZ() == z + 16) ? this.renderChunkNeighbours[south] : null);
        this.renderChunkNeighboursValid[west] = ((this.renderChunkNeighbours[west].getPosition().getX() == x - 16) ? this.renderChunkNeighbours[west] : null);
        this.renderChunkNeighboursValid[east] = ((this.renderChunkNeighbours[east].getPosition().getX() == x + 16) ? this.renderChunkNeighbours[east] : null);
        this.renderChunkNeighboursUpated = true;
    }
    
    public boolean isBoundingBoxInFrustum(final ICamera p_isBoundingBoxInFrustum_1_, final int p_isBoundingBoxInFrustum_2_) {
        return this.getBoundingBoxParent().isBoundingBoxInFrustumFully(p_isBoundingBoxInFrustum_1_, p_isBoundingBoxInFrustum_2_) || p_isBoundingBoxInFrustum_1_.isBoundingBoxInFrustum(this.boundingBox);
    }
    
    public AabbFrame getBoundingBoxParent() {
        if (this.boundingBoxParent == null) {
            final BlockPos pos = this.getPosition();
            final int x = pos.getX();
            final int y = pos.getY();
            final int z = pos.getZ();
            final int bits = 5;
            final int xp = x >> bits << bits;
            final int yp = y >> bits << bits;
            final int zp = z >> bits << bits;
            if (xp != x || yp != y || zp != z) {
                final AabbFrame bbp = this.renderGlobal.getRenderChunk(new BlockPos(xp, yp, zp)).getBoundingBoxParent();
                if (bbp != null && bbp.minX == xp && bbp.minY == yp && bbp.minZ == zp) {
                    this.boundingBoxParent = bbp;
                }
            }
            if (this.boundingBoxParent == null) {
                final int delta = 1 << bits;
                this.boundingBoxParent = new AabbFrame(xp, yp, zp, xp + delta, yp + delta, zp + delta);
            }
        }
        return this.boundingBoxParent;
    }
    
    @Override
    public String toString() {
        return "pos: " + this.getPosition() + ", frameIndex: " + this.frameIndex;
    }
    
    static {
        ENUM_WORLD_BLOCK_LAYERS = EnumWorldBlockLayer.values();
    }
}
