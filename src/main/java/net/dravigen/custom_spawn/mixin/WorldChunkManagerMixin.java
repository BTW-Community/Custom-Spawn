package net.dravigen.custom_spawn.mixin;

import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.dravigen.custom_spawn.config.ConfigUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.dravigen.custom_spawn.CustomSpawnAddon.*;

@Mixin(WorldChunkManager.class)
public abstract class WorldChunkManagerMixin {
	@Shadow
	private GenLayer genBiomes;
	
	@Inject(method = "getBiomesToSpawnIn", at = @At("RETURN"), cancellable = true)
	private void customList(CallbackInfoReturnable<List> cir) {
		cir.setReturnValue(new ArrayList<>());
	}
	
	
	
	@Inject(method = "findBiomePosition", at = @At("HEAD"), cancellable = true)
	private void customSpawnSearch(int x, int z, int range, List allowedBiomes, Random rand,
			CallbackInfoReturnable<ChunkPosition> cir) {
		if (allowedBiomes.isEmpty()) {
			cir.cancel();
			List<String> uwBiomes = new ArrayList<>(CustomSpawnAddon.unwantedBiomesInSpawn);
			List<String> wBiomes = new ArrayList<>(CustomSpawnAddon.wantedBiomesInSpawn);
			
			if (!wBiomes.isEmpty()) {
				wBiomes.remove(0);
			}
			if (!uwBiomes.isEmpty()) {
				uwBiomes.remove(0);
			}
			
			cir.setReturnValue(findBestSpawnLocationWithBiomesCriteria(x,
																	   z,
																	   CustomSpawnAddon.range,
																	   wBiomes,
																	   uwBiomes,
																	   rand));
		}
	}
	
	
	@Unique
	public ChunkPosition findBestSpawnLocationWithBiomesCriteria(int originX, int originZ, int searchRadius, List<String> wantedBiomesInSpawn, List<String> unwantedBiomesInSpawn, Random random) {
		IntCache.resetIntCache();
		
		allBiomeFound.clear();
		wantedBiomesFound.clear();
		unwantedBiomesFound.clear();
		
		final int CHUNK_SIZE_BLOCKS = 16;
		final int EVALUATION_CHUNK_WIDTH = 12;
		final int EVALUATION_HALF_WIDTH = (EVALUATION_CHUNK_WIDTH * CHUNK_SIZE_BLOCKS) / 2;
		final int SCAN_STEP = scanStep;
		
		ChunkPosition bestGlobalPosition = null;
		int highestGlobalScore = -1;
		boolean foundBestSpawn = false;
		int maxScore = wantedBiomesInSpawn.size();
		
		for (int r = 0; r <= searchRadius; r += SCAN_STEP) {
			if (foundBestSpawn) break;
			
			int limitedScanXMin = originX - r;
			int limitedScanXMax = originX + r;
			int limitedScanZMin = originZ - r;
			int limitedScanZMax = originZ + r;
			
			for (int potentialCenterX = limitedScanXMin; potentialCenterX <= limitedScanXMax; potentialCenterX += SCAN_STEP) {
				if (foundBestSpawn) break;
				
				for (int potentialCenterZ = limitedScanZMin; potentialCenterZ <= limitedScanZMax; potentialCenterZ += SCAN_STEP) {
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
					
					int centerBiomeX = potentialCenterX >> 2;
					int centerBiomeZ = potentialCenterZ >> 2;
					
					int centerBiomeInt = this.genBiomes.getInts(centerBiomeX, centerBiomeZ, 1, 1)[0];
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
					
					int[] biomeInts = this.genBiomes.getInts(minBiomeX, minBiomeZ, mapWidth, mapHeight);
					
					Set<String> foundBiomes = new HashSet<>();
					int currentDiversityScore = 0;
					
					for (int mapIndex = 0; mapIndex < mapWidth * mapHeight; ++mapIndex) {
						BiomeGenBase currentBiome = BiomeGenBase.biomeList[biomeInts[mapIndex]];
						String name = currentBiome.biomeName.replace(" ", "");
						
						if (foundBiomes.contains(name)) continue;
						
						if (Arrays.stream(ConfigUtils.getString(ConfigUtils.wantedBiomesInSpawnKey).split(","))
								.anyMatch(s -> s.equalsIgnoreCase(name))) {
							foundBiomes.add(name);
							currentDiversityScore += 1;
						}
						
						if (Arrays.stream(ConfigUtils.getString(ConfigUtils.unwantedBiomesInSpawnKey).split(","))
								.anyMatch(s -> s.equalsIgnoreCase(name))) {
							foundBiomes.add(name);
							currentDiversityScore -= 3;
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
			
			int[] biomeInts = this.genBiomes.getInts(minBiomeX, minBiomeZ, mapWidth, mapHeight);
			
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
		}
		
		if (bestGlobalPosition != null) {
			System.out.println(bestGlobalPosition.x + " : " + bestGlobalPosition.y + " : " + bestGlobalPosition.z);
		}
		
		return bestGlobalPosition;
	}
}
