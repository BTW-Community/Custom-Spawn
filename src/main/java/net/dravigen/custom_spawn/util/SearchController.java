package net.dravigen.custom_spawn.util;

import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.minecraft.src.ChunkPosition;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class SearchController {
	private final AtomicLong attemptCounter = new AtomicLong(0);
	public long startSeed;
	public int worldTypeId;
	private volatile Long foundSeed = null;
	private volatile ChunkPosition foundChunk = null;
	private volatile int bestScore = 0;
	private ExecutorService threadPool;
	private boolean searching = false;
	
	public SearchController(long startSeed, int worldTypeId) {
		this.startSeed = startSeed;
		this.worldTypeId = worldTypeId;
	}
	
	public void startSearch() {
		this.searching = true;
		CustomSpawnAddon.isSearching = true;
		this.bestScore = 0;
		this.foundSeed = null;
		this.foundChunk = null;
		this.attemptCounter.set(0);
		int cores = Runtime.getRuntime().availableProcessors();
		this.threadPool = Executors.newFixedThreadPool(cores);
		
		for (int i = 0; i < cores; i++) {
			threadPool.submit(new SeedSearchTask(attemptCounter, this));
		}
	}
	
	public void stopSearch() {
		this.searching = false;
		
		if (threadPool != null && !threadPool.isShutdown()) {
			threadPool.shutdownNow();
		}
	}
	
	
	public long getAttempts() {
		return attemptCounter.get();
	}
	
	public Long getFoundSeed() {
		return foundSeed;
	}
	
	public ChunkPosition getFoundChunk() {
		return this.foundChunk;
	}
	
	public boolean isSearching() {
		return this.searching;
	}
	
	public void setFoundSeed(long seed, ChunkPosition position, int bestScore) {
		if (this.bestScore < bestScore) {
			this.bestScore = bestScore;
			this.foundSeed = seed;
			this.foundChunk = position;
		}
	}
}