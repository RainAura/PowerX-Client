package net.minecraft.client.renderer.chunk;

import org.apache.logging.log4j.LogManager;
import net.minecraft.entity.Entity;
import java.util.concurrent.CancellationException;
import java.util.List;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Futures;
import net.minecraft.util.EnumWorldBlockLayer;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import org.apache.logging.log4j.Logger;

public class ChunkRenderWorker implements Runnable
{
    private static final Logger LOGGER;
    private final ChunkRenderDispatcher chunkRenderDispatcher;
    private final RegionRenderCacheBuilder regionRenderCacheBuilder;
    
    public ChunkRenderWorker(final ChunkRenderDispatcher p_i46201_1_) {
        this(p_i46201_1_, null);
    }
    
    public ChunkRenderWorker(final ChunkRenderDispatcher chunkRenderDispatcherIn, final RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
        this.chunkRenderDispatcher = chunkRenderDispatcherIn;
        this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                this.processTask(this.chunkRenderDispatcher.getNextChunkUpdate());
            }
        }
        catch (InterruptedException lvt_1_1_) {
            ChunkRenderWorker.LOGGER.debug("Stopping due to interrupt");
        }
        catch (Throwable lvt_1_2_) {
            final CrashReport lvt_2_1_ = CrashReport.makeCrashReport(lvt_1_2_, "Batching chunks");
            Minecraft.getMinecraft().crashed(Minecraft.getMinecraft().addGraphicsAndWorldToCrashReport(lvt_2_1_));
        }
    }
    
    @SuppressWarnings("unchecked")
	protected void processTask(final ChunkCompileTaskGenerator generator) throws InterruptedException {
        generator.getLock().lock();
        try {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.PENDING) {
                if (!generator.isFinished()) {
                    ChunkRenderWorker.LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be pending; ignoring task");
                }
                return;
            }
            generator.setStatus(ChunkCompileTaskGenerator.Status.COMPILING);
        }
        finally {
            generator.getLock().unlock();
        }
        final Entity lvt_2_1_ = Minecraft.getMinecraft().getRenderViewEntity();
        if (lvt_2_1_ == null) {
            generator.finish();
            return;
        }
        generator.setRegionRenderCacheBuilder(this.getRegionRenderCacheBuilder());
        final float lvt_3_1_ = (float)lvt_2_1_.posX;
        final float lvt_4_1_ = (float)lvt_2_1_.posY + lvt_2_1_.getEyeHeight();
        final float lvt_5_1_ = (float)lvt_2_1_.posZ;
        final ChunkCompileTaskGenerator.Type lvt_6_1_ = generator.getType();
        if (lvt_6_1_ == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
            generator.getRenderChunk().rebuildChunk(lvt_3_1_, lvt_4_1_, lvt_5_1_, generator);
        }
        else if (lvt_6_1_ == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
            generator.getRenderChunk().resortTransparency(lvt_3_1_, lvt_4_1_, lvt_5_1_, generator);
        }
        generator.getLock().lock();
        try {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
                if (!generator.isFinished()) {
                    ChunkRenderWorker.LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be compiling; aborting task");
                }
                this.freeRenderBuilder(generator);
                return;
            }
            generator.setStatus(ChunkCompileTaskGenerator.Status.UPLOADING);
        }
        finally {
            generator.getLock().unlock();
        }
        final CompiledChunk lvt_7_1_ = generator.getCompiledChunk();
        final List<ListenableFuture<Object>> lvt_8_1_ = Lists.newArrayList();
        if (lvt_6_1_ == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
            for (final EnumWorldBlockLayer lvt_12_1_ : EnumWorldBlockLayer.values()) {
                if (lvt_7_1_.isLayerStarted(lvt_12_1_)) {
                    lvt_8_1_.add(this.chunkRenderDispatcher.uploadChunk(lvt_12_1_, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(lvt_12_1_), generator.getRenderChunk(), lvt_7_1_));
                }
            }
        }
        else if (lvt_6_1_ == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
            lvt_8_1_.add(this.chunkRenderDispatcher.uploadChunk(EnumWorldBlockLayer.TRANSLUCENT, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT), generator.getRenderChunk(), lvt_7_1_));
        }
        final ListenableFuture<List<Object>> lvt_9_2_ = (ListenableFuture<List<Object>>)Futures.allAsList((Iterable)lvt_8_1_);
        generator.addFinishRunnable(new Runnable() {
            @Override
            public void run() {
                lvt_9_2_.cancel(false);
            }
        });
        Futures.addCallback((ListenableFuture)lvt_9_2_, (FutureCallback)new FutureCallback<List<Object>>() {
            public void onSuccess(final List<Object> p_onSuccess_1_) {
                ChunkRenderWorker.this.freeRenderBuilder(generator);
                generator.getLock().lock();
                try {
                    if (generator.getStatus() != ChunkCompileTaskGenerator.Status.UPLOADING) {
                        if (!generator.isFinished()) {
                            ChunkRenderWorker.LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be uploading; aborting task");
                        }
                        return;
                    }
                    generator.setStatus(ChunkCompileTaskGenerator.Status.DONE);
                }
                finally {
                    generator.getLock().unlock();
                }
                generator.getRenderChunk().setCompiledChunk(lvt_7_1_);
            }
            
            public void onFailure(final Throwable p_onFailure_1_) {
                ChunkRenderWorker.this.freeRenderBuilder(generator);
                if (!(p_onFailure_1_ instanceof CancellationException) && !(p_onFailure_1_ instanceof InterruptedException)) {
                    Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(p_onFailure_1_, "Rendering chunk"));
                }
            }
        });
    }
    
    private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException {
        return (this.regionRenderCacheBuilder != null) ? this.regionRenderCacheBuilder : this.chunkRenderDispatcher.allocateRenderBuilder();
    }
    
    private void freeRenderBuilder(final ChunkCompileTaskGenerator taskGenerator) {
        if (this.regionRenderCacheBuilder == null) {
            this.chunkRenderDispatcher.freeRenderBuilder(taskGenerator.getRegionRenderCacheBuilder());
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
