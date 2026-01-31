package net.dravigen.custom_spawn;

import api.AddonHandler;
import api.BTWAddon;
import api.config.AddonConfig;
import net.dravigen.custom_spawn.config.ConfigUtils;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CustomSpawnAddon extends BTWAddon {
	public static ChunkPosition customSpawnCoord = null;
	public static int attempts = 0;
	
	public static int range = 2048;
	public static int scanStep = 128;
	public static Set<String> spawneableBiomes = new TreeSet<>();
	public static Set<String> unSpawneableBiomes = new TreeSet<>();
	public static List<String> wantedBiomesInSpawn = new ArrayList<>();
	public static List<String> unwantedBiomesInSpawn = new ArrayList<>();
	public static List<String> allBiomesName = new ArrayList<>();
	public static List<BiomeGenBase> allBiomes = new ArrayList<>();
	public static String onlyBiome = "none";
	public static Set<String> allBiomeFound = new TreeSet<>();
	public static Set<String> wantedBiomesFound = new TreeSet<>();
	public static Set<String> unwantedBiomesFound = new TreeSet<>();
	public static int loadingProgress = 0;
	private static CustomSpawnAddon instance;
	
	public CustomSpawnAddon() {
		super();
		instance = this;
	}
	
	public static BTWAddon getInstance() {
		return instance;
	}
	
	public static @NotNull ChunkPosition createCustomSpawn(WorldChunkManager manager) {
		return createCustomSpawn(null, manager);
	}
	
	public static @NotNull ChunkPosition createCustomSpawn(WorldServer server, WorldChunkManager manager) {
		List<String> uwBiomes = new ArrayList<>(CustomSpawnAddon.unwantedBiomesInSpawn);
		List<String> wBiomes = new ArrayList<>(CustomSpawnAddon.wantedBiomesInSpawn);
		
		if (!wBiomes.isEmpty()) {
			wBiomes.remove(0);
		}
		if (!uwBiomes.isEmpty()) {
			uwBiomes.remove(0);
		}
		
		return findBestSpawnLocationWithBiomesCriteria(server, manager, 0, 0, range, wBiomes, uwBiomes);
	}
	
	public static ChunkPosition findBestSpawnLocationWithBiomesCriteria(WorldServer server, WorldChunkManager manager,
			int originX, int originZ, int searchRadius, List<String> wantedBiomesInSpawn,
			List<String> unwantedBiomesInSpawn) {
		IntCache.resetIntCache();
		
		attempts = 0;
		allBiomeFound.clear();
		wantedBiomesFound.clear();
		unwantedBiomesFound.clear();
		final int CHUNK_SIZE_BLOCKS = 16;
		final int EVALUATION_CHUNK_WIDTH = 17;
		final int EVALUATION_HALF_WIDTH = (EVALUATION_CHUNK_WIDTH * CHUNK_SIZE_BLOCKS) / 2;
		final int SCAN_STEP = scanStep;
		
		ChunkPosition bestGlobalPosition = null;
		int highestGlobalScore = -1;
		boolean foundBestSpawn = false;
		int maxScore = wantedBiomesInSpawn.size() * 2;
		GenLayer genBiomes = manager.genBiomes;
		
		for (int r = 0; r <= searchRadius; r += SCAN_STEP) {
			if (foundBestSpawn) break;
			
			int limitedScanXMin = originX - r;
			int limitedScanXMax = originX + r;
			int limitedScanZMin = originZ - r;
			int limitedScanZMax = originZ + r;
			
			for (int potentialCenterX = limitedScanXMin; potentialCenterX <=
					limitedScanXMax; potentialCenterX += SCAN_STEP) {
				if (foundBestSpawn) break;
				
				for (int potentialCenterZ = limitedScanZMin; potentialCenterZ <=
						limitedScanZMax; potentialCenterZ += SCAN_STEP) {
					if (foundBestSpawn) break;
					
					if (r > 0) {
						boolean isOnXEdge = (potentialCenterX == limitedScanXMin ||
								potentialCenterX == limitedScanXMax);
						boolean isOnZEdge = (potentialCenterZ == limitedScanZMin ||
								potentialCenterZ == limitedScanZMax);
						
						if (!isOnXEdge && !isOnZEdge) {
							continue;
						}
					}
					
					if (server != null) {
						attempts++;
					}
					
					boolean canSpawnInChunk = false;
					
					if (server != null) {
						int x = potentialCenterX;
						int z = potentialCenterZ;
						int stepLength = 1;
						int count = 0;
						int direction = 0;
						
						int i = 16;
						int maxIterations = i * i;
						int iterations = 0;
						
						while (iterations <= maxIterations) {
							if (server.provider.canCoordinateBeSpawn(x, z)) {
								canSpawnInChunk = true;
								
								break;
							}
							
							switch (direction) {
								case 0 -> x++;
								case 1 -> z++;
								case 2 -> x--;
								case 3 -> z--;
							}
							
							count++;
							
							if (count == stepLength) {
								count = 0;
								direction = (direction + 1) % 4;
								
								if (direction == 0 || direction == 2) {
									stepLength++;
								}
							}
							
							iterations++;
						}
						
						if (iterations != maxIterations) {
							potentialCenterX = x;
							potentialCenterZ = z;
						}
					}
					else canSpawnInChunk = true;
					
					if (!canSpawnInChunk) continue;
					
					int centerBiomeX = potentialCenterX >> 2;
					int centerBiomeZ = potentialCenterZ >> 2;
					
					int centerBiomeInt = genBiomes.getInts(centerBiomeX, centerBiomeZ, 1, 1)[0];
					BiomeGenBase centerBiome = BiomeGenBase.biomeList[centerBiomeInt];
					String centerName = centerBiome.biomeName.replace(" ", "");
					
					if (!allBiomesName.contains(onlyBiome)) {
						if (!ConfigUtils.getBoolean(ConfigUtils.suitableBiomesKey + "." + centerName)) continue;
					}
					else if (!onlyBiome.equalsIgnoreCase(centerName)) continue;
					
					int minBiomeX = potentialCenterX - EVALUATION_HALF_WIDTH >> 2;
					int minBiomeZ = potentialCenterZ - EVALUATION_HALF_WIDTH >> 2;
					
					int maxBiomeX = potentialCenterX + EVALUATION_HALF_WIDTH >> 2;
					int maxBiomeZ = potentialCenterZ + EVALUATION_HALF_WIDTH >> 2;
					
					int mapWidth = maxBiomeX - minBiomeX + 1;
					int mapHeight = maxBiomeZ - minBiomeZ + 1;
					
					int[] biomeInts = genBiomes.getInts(minBiomeX, minBiomeZ, mapWidth, mapHeight);
					
					Set<String> foundBiomes = new HashSet<>();
					int currentDiversityScore = 0;
					
					for (int mapIndex = 0; mapIndex < mapWidth * mapHeight; ++mapIndex) {
						BiomeGenBase currentBiome = BiomeGenBase.biomeList[biomeInts[mapIndex]];
						String name = currentBiome.biomeName.replace(" ", "");
						
						if (foundBiomes.contains(name)) continue;
						
						if (Arrays.stream(ConfigUtils.getString(ConfigUtils.wantedBiomesInSpawnKey).split(","))
								.anyMatch(s -> s.equalsIgnoreCase(name))) {
							foundBiomes.add(name);
							currentDiversityScore += 2;
						}
						
						if (Arrays.stream(ConfigUtils.getString(ConfigUtils.unwantedBiomesInSpawnKey).split(","))
								.anyMatch(s -> s.equalsIgnoreCase(name))) {
							foundBiomes.add(name);
							currentDiversityScore -= 1;
						}
					}
					
					if (currentDiversityScore > highestGlobalScore) {
						highestGlobalScore = currentDiversityScore;
						bestGlobalPosition = new ChunkPosition(potentialCenterX, 0, potentialCenterZ);
					}
					
					if (currentDiversityScore == maxScore) {
						foundBestSpawn = true;
					}
				}
			}
		}
		
		if (bestGlobalPosition != null) {
			int minBiomeX = bestGlobalPosition.x - EVALUATION_HALF_WIDTH >> 2;
			int minBiomeZ = bestGlobalPosition.z - EVALUATION_HALF_WIDTH >> 2;
			
			int maxBiomeX = bestGlobalPosition.x + EVALUATION_HALF_WIDTH >> 2;
			int maxBiomeZ = bestGlobalPosition.z + EVALUATION_HALF_WIDTH >> 2;
			
			int mapWidth = maxBiomeX - minBiomeX + 1;
			int mapHeight = maxBiomeZ - minBiomeZ + 1;
			
			int[] biomeInts = genBiomes.getInts(minBiomeX, minBiomeZ, mapWidth, mapHeight);
			
			for (int mapIndex = 0; mapIndex < mapWidth * mapHeight; ++mapIndex) {
				String currentBiome = BiomeGenBase.biomeList[biomeInts[mapIndex]].biomeName.replace(" ", "");
				
				if (wantedBiomesInSpawn.contains(currentBiome)) {
					wantedBiomesFound.add(currentBiome);
				}
				else if (unwantedBiomesInSpawn.contains(currentBiome)) {
					unwantedBiomesFound.add(currentBiome);
				}
				else {
					allBiomeFound.add(currentBiome);
				}
			}
			
			attempts = 0;
		}
		
		return bestGlobalPosition;
	}
	
	public static ChunkPosition findValidSpawnSpot(World server, int x0, int z0) {
		int x = x0;
		int z = z0;
		int stepLength = 1;
		int count = 0;
		int direction = 0;
		
		int i = 16;
		int maxIterations = i * i;
		int iterations = 0;
		
		while (iterations < maxIterations) {
			if (server.provider.canCoordinateBeSpawn(x, z)) {
				return new ChunkPosition(x, 0, z);
			}
			
			switch (direction) {
				case 0 -> x++;
				case 1 -> z++;
				case 2 -> x--;
				case 3 -> z--;
			}
			
			count++;
			
			if (count == stepLength) {
				count = 0;
				direction = (direction + 1) % 4;
				
				if (direction == 0 || direction == 2) {
					stepLength++;
				}
			}
			
			iterations++;
		}
		
		return new ChunkPosition(x0, 0, z0);
	}
	
	@Override
	public void preInitialize() {
		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome == null) continue;
			
			if (biome == BiomeGenBase.hell || biome == BiomeGenBase.sky) continue;
			
			allBiomesName.add(biome.biomeName.replace(" ", ""));
			allBiomes.add(biome);
		}
	}
	
	@Override
	public void initialize() {
		AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
	}
	
	@Override
	public void registerConfigProperties(AddonConfig config) {
		ConfigUtils.registerConfigs(config);
	}
	
	@Override
	public void handleConfigProperties(AddonConfig config) {
		ConfigUtils.reloadConfigs(config);
	}
}
