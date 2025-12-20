package net.dravigen.custom_spawn.config;

import api.config.AddonConfig;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.minecraft.src.BiomeGenBase;

import java.io.*;

public class ConfigUtils {
	public static final String suitableBiomesKey = "1.suitable-biomes";
	public static final String onlyBiomeKey = "2.only-biome";
	public static final String wantedBiomesInSpawnKey = "3.wanted-biomes-in-spawn";
	public static final String unwantedBiomesInSpawnKey = "4.unwanted-biomes-in-spawn";
	public static final String rangeKey = "5.range";
	public static final String scanStepKey = "6.range";
	public static final String affectHRKey = "7.affect-HR";
	
	
	public static void registerConfigs(AddonConfig config) {
		for (BiomeGenBase biome : CustomSpawnAddon.allBiomes) {
			String name = biome.biomeName.replace(" ", "");
			
			config.registerBoolean(suitableBiomesKey + "." + name,
								   !HardcoreSpawnUtils.blacklistedBiomes.contains(biome));
		}
		
		config.registerCategoryComment(suitableBiomesKey, "Suitable biomes for player's spawn");
		
		config.registerString(onlyBiomeKey, "", "Player only spawn in this biome (if used, the suitable biomes list is ignored");
		
		config.registerString(wantedBiomesInSpawnKey,
							  "",
							  "List of every biome you would want in spawn's chunks with associated score (higher score mean that potential spawn is more likely to get picked over another).",
							  "Example: \"Jungle:10,Swampland:10,Forest:5\"");
		config.registerString(unwantedBiomesInSpawnKey,
							  "",
							  "List of every biome you wouldn't want in spawn's chunks (higher score means that the presence of that biome will make that potential spawn less likely to get picked).",
							  "Example: \"Ice_Plains:5,Ocean:10\"");
	
		config.registerInt(rangeKey, 2048, "Range of valid spawn search.");
		
		config.registerInt(scanStepKey, 68, "Distance in blocks between each spawn attempt.");
		
		config.registerBoolean(affectHRKey, false, "Should the biome whitelist affect hardcore respawn.");
	}
	
	public static void reloadConfigs(AddonConfig config) {
		String onlyBiomeName = config.getString(onlyBiomeKey);
		
		String[] wantedBiomes = config.getString(wantedBiomesInSpawnKey).split(",");
		String[] unwantedBiomes = config.getString(unwantedBiomesInSpawnKey).split(",");
		
		for (BiomeGenBase biome : CustomSpawnAddon.allBiomes) {
			String biomeName = biome.biomeName.replace(" ", "");
			for (String wantedBiome : wantedBiomes) {
				String name = wantedBiome;
				String score = "1";
				
				if (wantedBiome.split(":").length == 2) {
					name = wantedBiome.split(":")[0];
					score = wantedBiome.split(":")[1];
				}
				
				if (biomeName.equalsIgnoreCase(name)) {
					CustomSpawnAddon.wantedBiomesInSpawn.add(biome);
					CustomSpawnAddon.biomesWithPriority.put(biome, Integer.parseInt(score));
				}
			}
			
			for (String unwantedBiome : unwantedBiomes) {
				String name = unwantedBiome;
				String score = "1";
				
				if (unwantedBiome.split(":").length == 2) {
					name = unwantedBiome.split(":")[0];
					score = unwantedBiome.split(":")[1];
				}
				
				if (biomeName.equalsIgnoreCase(name)) {
					CustomSpawnAddon.unwantedBiomesInSpawn.add(biome);
					CustomSpawnAddon.biomesWithPriority.put(biome, -Integer.parseInt(score));
				}
			}
			
			if (!onlyBiomeName.isEmpty()) {
				if (biomeName.equalsIgnoreCase(onlyBiomeName)) {
					CustomSpawnAddon.onlyBiome = biome;
				}
			}
			
			if (config.getBoolean(suitableBiomesKey + "." + biomeName) || biome == CustomSpawnAddon.onlyBiome) {
				CustomSpawnAddon.spawneableBiomes.add(biome);
			}
			else {
				CustomSpawnAddon.unSpawneableBiomes.add(biome);
			}
		}
		
		CustomSpawnAddon.range = config.getInt(rangeKey);

		CustomSpawnAddon.scanStep = config.getInt(scanStepKey);
		
		if (config.getBoolean(affectHRKey)) {
			HardcoreSpawnUtils.blacklistedBiomes = CustomSpawnAddon.unSpawneableBiomes;
		}
	}
}
