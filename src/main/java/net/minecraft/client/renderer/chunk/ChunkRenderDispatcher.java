package net.minecraft.client.renderer.chunk;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import com.google.common.util.concurrent.Futures;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.Minecraft;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import java.util.ArrayList;
import com.google.common.collect.Queues;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFutureTask;
import java.util.Queue;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import java.util.concurrent.BlockingQueue;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class ChunkRenderDispatcher
{
    private static final ThreadFactory threadFactory;
    private final List<ChunkRenderWorker> listThreadedWorkers;
    private final BlockingQueue<ChunkCompileTaskGenerator> queueChunkUpdates;
    private final BlockingQueue<RegionRenderCacheBuilder> queueFreeRenderBuilders;
    private final WorldVertexBufferUploader worldVertexUploader;
    private final VertexBufferUploader vertexUploader;
    private final Queue<ListenableFutureTask<?>> queueChunkUploads;
    private final ChunkRenderWorker renderWorker;
    private final int countRenderBuilders;
    private List<RegionRenderCacheBuilder> listPausedBuilders;
    
    public ChunkRenderDispatcher() {
        this(-1);
    }
    
    public ChunkRenderDispatcher(final int p_i4_1_) {
        this.listThreadedWorkers = Lists.newArrayList();
        this.queueChunkUpdates = Queues.newArrayBlockingQueue(100);
        this.worldVertexUploader = new WorldVertexBufferUploader();
        this.vertexUploader = new VertexBufferUploader();
        this.queueChunkUploads = Queues.newArrayDeque();
        this.listPausedBuilders = new ArrayList<RegionRenderCacheBuilder>();
        final int m = Math.max(1, (int)(Runtime.getRuntime().maxMemory() * 0.3) / 10485760);
        final int c = Math.max(1, MathHelper.clamp_int(Runtime.getRuntime().availableProcessors() - 2, 1, m / 5));
        if (p_i4_1_ < 0) {
            this.countRenderBuilders = MathHelper.clamp_int(c * 8, 1, m);
        }
        else {
            this.countRenderBuilders = p_i4_1_;
        }
        for (int i = 0; i < c; ++i) {
            final ChunkRenderWorker chunkrenderworker = new ChunkRenderWorker(this);
            final Thread thread = ChunkRenderDispatcher.threadFactory.newThread(chunkrenderworker);
            thread.start();
            this.listThreadedWorkers.add(chunkrenderworker);
        }
        this.queueFreeRenderBuilders = Queues.newArrayBlockingQueue(this.countRenderBuilders);
        for (int j = 0; j < this.countRenderBuilders; ++j) {
            this.queueFreeRenderBuilders.add(new RegionRenderCacheBuilder());
        }
        this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
    }
    
    public String getDebugInfo() {
        return String.format("pC: %03d, pU: %1d, aB: %1d", this.queueChunkUpdates.size(), this.queueChunkUploads.size(), this.queueFreeRenderBuilders.size());
    }
    
    public boolean runChunkUploads(final long p_178516_1_) {
        boolean flag = false;
        long i;
        do {
            boolean flag2 = false;
            ListenableFutureTask<?> pendingUpload = null;
            synchronized (this.queueChunkUploads) {
                pendingUpload = this.queueChunkUploads.poll();
            }
            if (pendingUpload != null) {
                pendingUpload.run();
                flag2 = true;
                flag = true;
            }
            if (p_178516_1_ == 0L) {
                break;
            }
            if (!flag2) {
                break;
            }
            i = p_178516_1_ - System.nanoTime();
        } while (i >= 0L);
        return flag;
    }
    
    public boolean updateChunkLater(final RenderChunk chunkRenderer) {
        chunkRenderer.getLockCompileTask().lock();
        boolean flag2;
        try {
            final ChunkCompileTaskGenerator chunkcompiletaskgenerator = chunkRenderer.makeCompileTaskChunk();
            chunkcompiletaskgenerator.addFinishRunnable(new Runnable() {
                @Override
                public void run() {
                    ChunkRenderDispatcher.this.queueChunkUpdates.remove(chunkcompiletaskgenerator);
                }
            });
            final boolean flag = this.queueChunkUpdates.offer(chunkcompiletaskgenerator);
            if (!flag) {
                chunkcompiletaskgenerator.finish();
            }
            flag2 = flag;
        }
        finally {
            chunkRenderer.getLockCompileTask().unlock();
        }
        return flag2;
    }
    
    public boolean updateChunkNow(final RenderChunk chunkRenderer) {
        chunkRenderer.getLockCompileTask().lock();
        boolean flag;
        try {
            final ChunkCompileTaskGenerator chunkcompiletaskgenerator = chunkRenderer.makeCompileTaskChunk();
            try {
                this.renderWorker.processTask(chunkcompiletaskgenerator);
            }
            catch (InterruptedException ex) {}
            flag = true;
        }
        finally {
            chunkRenderer.getLockCompileTask().unlock();
        }
        return flag;
    }
    
    public void stopChunkUpdates() {
        this.clearChunkUpdates();
        while (this.runChunkUploads(0L)) {}
        final List<RegionRenderCacheBuilder> list = Lists.newArrayList();
        while (list.size() != this.countRenderBuilders) {
            try {
                list.add(this.allocateRenderBuilder());
            }
            catch (InterruptedException var3) {}
        }
        this.queueFreeRenderBuilders.addAll(list);
    }
    
    public void freeRenderBuilder(final RegionRenderCacheBuilder p_178512_1_) {
        this.queueFreeRenderBuilders.add(p_178512_1_);
    }
    
    public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException {
        return this.queueFreeRenderBuilders.take();
    }
    
    public ChunkCompileTaskGenerator getNextChunkUpdate() throws InterruptedException {
        return this.queueChunkUpdates.take();
    }
    
    public boolean updateTransparencyLater(final RenderChunk chunkRenderer) {
        chunkRenderer.getLockCompileTask().lock();
        boolean flag;
        try {
            final ChunkCompileTaskGenerator chunkcompiletaskgenerator = chunkRenderer.makeCompileTaskTransparency();
            if (chunkcompiletaskgenerator == null) {
                flag = true;
                return flag;
            }
            chunkcompiletaskgenerator.addFinishRunnable(new Runnable() {
                @Override
                public void run() {
                    ChunkRenderDispatcher.this.queueChunkUpdates.remove(chunkcompiletaskgenerator);
                }
            });
            flag = this.queueChunkUpdates.offer(chunkcompiletaskgenerator);
        }
        finally {
            chunkRenderer.getLockCompileTask().unlock();
        }
        return flag;
    }
    
    public ListenableFuture<Object> uploadChunk(final EnumWorldBlockLayer player, final WorldRenderer p_178503_2_, final RenderChunk chunkRenderer, final CompiledChunk compiledChunkIn) {
        if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            if (OpenGlHelper.useVbo()) {
                this.uploadVertexBuffer(p_178503_2_, chunkRenderer.getVertexBufferByLayer(player.ordinal()));
            }
            else {
                this.uploadDisplayList(p_178503_2_, ((ListedRenderChunk)chunkRenderer).getDisplayList(player, compiledChunkIn), chunkRenderer);
            }
            p_178503_2_.setTranslation(0.0, 0.0, 0.0);
            return (ListenableFuture<Object>)Futures.immediateFuture((Object)null);
        }
        final ListenableFutureTask<Object> listenablefuturetask = (ListenableFutureTask<Object>)ListenableFutureTask.create((Runnable)new Runnable() {
            @Override
            public void run() {
                ChunkRenderDispatcher.this.uploadChunk(player, p_178503_2_, chunkRenderer, compiledChunkIn);
            }
        }, (Object)null);
        synchronized (this.queueChunkUploads) {
            this.queueChunkUploads.add(listenablefuturetask);
            return (ListenableFuture<Object>)listenablefuturetask;
        }
    }
    
    private void uploadDisplayList(final WorldRenderer p_178510_1_, final int p_178510_2_, final RenderChunk chunkRenderer) {
        GL11.glNewList(p_178510_2_, 4864);
        GlStateManager.pushMatrix();
        chunkRenderer.multModelviewMatrix();
        this.worldVertexUploader.draw(p_178510_1_);
        GlStateManager.popMatrix();
        GL11.glEndList();
    }
    
    private void uploadVertexBuffer(final WorldRenderer p_178506_1_, final VertexBuffer vertexBufferIn) {
        this.vertexUploader.setVertexBuffer(vertexBufferIn);
        this.vertexUploader.draw(p_178506_1_);
    }
    
    public void clearChunkUpdates() {
        while (!this.queueChunkUpdates.isEmpty()) {
            final ChunkCompileTaskGenerator chunkcompiletaskgenerator = this.queueChunkUpdates.poll();
            if (chunkcompiletaskgenerator != null) {
                chunkcompiletaskgenerator.finish();
            }
        }
    }
    
    public boolean hasChunkUpdates() {
        return this.queueChunkUpdates.isEmpty() && this.queueChunkUploads.isEmpty();
    }
    
    public void pauseChunkUpdates() {
        while (this.listPausedBuilders.size() != this.countRenderBuilders) {
            try {
                this.runChunkUploads(Long.MAX_VALUE);
                final RegionRenderCacheBuilder builder = this.queueFreeRenderBuilders.poll(100L, TimeUnit.MILLISECONDS);
                if (builder == null) {
                    continue;
                }
                this.listPausedBuilders.add(builder);
            }
            catch (InterruptedException ie) {}
        }
    }
    
    public void resumeChunkUpdates() {
        this.queueFreeRenderBuilders.addAll(this.listPausedBuilders);
        this.listPausedBuilders.clear();
    }
    
    static {
        LogManager.getLogger();
        threadFactory = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).build();
    }
}
