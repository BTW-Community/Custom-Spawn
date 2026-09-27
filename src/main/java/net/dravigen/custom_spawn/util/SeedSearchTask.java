package net.dravigen.custom_spawn.util;

import net.dravigen.custom_spawn.config.ConfigUtils;
import net.minecraft.src.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static net.dravigen.custom_spawn.CustomSpawnAddon.*;

public class SeedSearchTask implements Runnable {
	private final AtomicLong attemptCounter;
	private final SearchController controller;
	private final Random random = new Random();
	
	public SeedSearchTask(AtomicLong attemptCounter, SearchController controller) {
		this.attemptCounter = attemptCounter;
		this.controller = controller;
	}

	@Override
	public void run() {
		try {
			long i = this.controller.startSeed;
			final int SPAWN_RADIUS = (17 * 16) / 2;
			int bestScore = 0;
			long bestSeed = i;
			ChunkPosition bestChunk = null;
			int centerX;
			int centerZ;
			WorldChunkManager manager;
			int maxScore = (wantedBiomesInSpawn.size() - 1) * 2;
			GenLayer genBiomes;
			List biomeToSpawnIn = spawneableBiomesGen;
			ChunkPosition chunkPosition;
			DummyWorld dummyWorld;
			ChunkProviderGenerate chunkProvider;
			byte[] metadata;
			short[] blockIDs;
			int centerBiomeInt;
			BiomeGenBase centerBiome;
			String centerName;
			Random randomFromSeed;
			final byte GRASS_BLOCK_ID = 2;
			final byte AIR_ID = 0;
			int index;
			short blockId;
			int localX;
			int localZ;
			int[] biomeInts;
			BiomeGenBase currentBiome;
			String name;
			Set<String> foundBiomes = new HashSet<>();
			
			while (this.attemptCounter.get() < ConfigUtils.getInt(ConfigUtils.seedLimitKey) && controller.isSearching()) {
				dummyWorld = new DummyWorld(i, WorldType.worldTypes[this.controller.worldTypeId]);
				chunkProvider = new ChunkProviderGenerate(dummyWorld, i, false);
				manager = dummyWorld.getWorldChunkManager();
				
				genBiomes = manager.genBiomes;
				
				randomFromSeed = new Random(i);
				chunkPosition = manager.findBiomePosition(0, 0, 256, biomeToSpawnIn, randomFromSeed);
				
				if (chunkPosition == null) {
					i = newSeed();
					
					continue;
				}
				
				centerX = chunkPosition.x;
				centerZ = chunkPosition.z;
				metadata = new byte[32768];
				blockIDs = new short[32768];
				
				int chunkX = centerX >> 4;
				int chunkZ = centerZ >> 4;
				chunkProvider.generateTerrain(chunkX, chunkZ, blockIDs, metadata);
				BiomeGenBase[] chunkBiomes = dummyWorld.getWorldChunkManager()
						.loadBlockGeneratorData(null, chunkX * 16, chunkZ * 16, 16, 16);
				chunkProvider.replaceBlocksForBiome(chunkX, chunkZ, blockIDs, metadata, chunkBiomes);
				
				centerBiomeInt = genBiomes.getInts(centerX >> 2, centerZ >> 2, 1, 1)[0];
				centerBiome = BiomeGenBase.biomeList[centerBiomeInt];
				centerName = centerBiome.biomeName.replace(" ", "");
				
				boolean isGrassSpawn = false;
				if (biomeToSpawnIn.contains(centerBiome)) {
					localX = centerX & 15;
					localZ = centerZ & 15;
					
					for (int y = 127; y >= 0; y--) {
						index = (localX * 16 + localZ) * 128 + y;
						
						blockId = blockIDs[index];
						
						if (blockId != AIR_ID) {
							if (blockId == GRASS_BLOCK_ID) {
								isGrassSpawn = true;
							}
							
							break;
						}
					}
				}
				
				
				int var9 = 0;
				while (!biomeToSpawnIn.contains(centerBiome) || !isGrassSpawn) {
					centerX += randomFromSeed.nextInt(64) - randomFromSeed.nextInt(64);
					centerZ += randomFromSeed.nextInt(64) - randomFromSeed.nextInt(64);
					
					if (++var9 != 1000) {
						centerBiomeInt = genBiomes.getInts(centerX >> 2, centerZ >> 2, 1, 1)[0];
						centerBiome = BiomeGenBase.biomeList[centerBiomeInt];
						
						continue;
					}
					
					break;
				}
				
				if (!allBiomesName.contains(onlyBiome)) {
					if (!ConfigUtils.getBoolean(ConfigUtils.suitableBiomesKey + "." + centerName)) {
						i = newSeed();
						
						continue;
					}
				}
				else if (!onlyBiome.equalsIgnoreCase(centerName)) {
					i = newSeed();
					
					continue;
				}
				
				int minBiomeX = centerX - SPAWN_RADIUS >> 2;
				int minBiomeZ = centerZ - SPAWN_RADIUS >> 2;
				
				int maxBiomeX = centerX + SPAWN_RADIUS >> 2;
				int maxBiomeZ = centerZ + SPAWN_RADIUS >> 2;
				
				int mapWidth = maxBiomeX - minBiomeX + 1;
				int mapHeight = maxBiomeZ - minBiomeZ + 1;
				
				biomeInts = genBiomes.getInts(minBiomeX, minBiomeZ, mapWidth, mapHeight);
				
				foundBiomes.clear();
				int currentDiversityScore = 0;
				
				for (int mapIndex = 0; mapIndex < mapWidth * mapHeight; ++mapIndex) {
					currentBiome = BiomeGenBase.biomeList[biomeInts[mapIndex]];
					name = currentBiome.biomeName.replace(" ", "");
					
					if (foundBiomes.contains(name)) {
						continue;
					}
					
					String finalName = name;
					if (Arrays.stream(ConfigUtils.getString(ConfigUtils.wantedBiomesInSpawnKey).split(","))
							.anyMatch(s -> s.equalsIgnoreCase(finalName))) {
						foundBiomes.add(name);
						currentDiversityScore += 2;
					}
					
					if (Arrays.stream(ConfigUtils.getString(ConfigUtils.unwantedBiomesInSpawnKey).split(","))
							.anyMatch(s -> s.equalsIgnoreCase(finalName))) {
						foundBiomes.add(name);
						currentDiversityScore -= 1;
					}
				}
				
				
				if (currentDiversityScore > bestScore) {
					bestChunk = chunkPosition;
					bestScore = currentDiversityScore;
					bestSeed = i;
					
					if (maxScore == currentDiversityScore) {
						break;
					}
				}
				
				i = newSeed();
			}
			
			controller.setFoundSeed(bestSeed, bestChunk, bestScore);
			controller.stopSearch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private long newSeed() {
		attemptCounter.incrementAndGet();
		return random.nextLong();
	}
}