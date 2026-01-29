package net.dravigen.custom_spawn.config;

import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.minecraft.src.BiomeGenBase;

public class ConfigUtils {
	public static final String suitableBiomesKey = "suitable-biomes";
	public static final String onlyBiomeKey = "only-biome";
	public static final String wantedBiomesInSpawnKey = "wanted-biomes-in-spawn";
	public static final String unwantedBiomesInSpawnKey = "unwanted-biomes-in-spawn";
	public static final String rangeKey = "range";
	public static final String scanStepKey = "stepScan";
	public static final String affectHRKey = "affect-HR";
	
	private static final String category = "Suitable Biomes";
	
	public static void registerAllSettings() {
		for (BiomeGenBase biome : CustomSpawnAddon.allBiomes) {
			String name = biome.biomeName.replace(" ", "");
			boolean defaultValue = !HardcoreSpawnUtils.blacklistedBiomes.contains(biome);
			
			DVS_ConfigManager.registerBool(name, name, defaultValue, "Can player spawn in: " + name, category);
		}
		
		DVS_ConfigManager.registerString(wantedBiomesInSpawnKey, "Wanted Biomes in Spawn", "none", "List of every biomes that should be in Spawn", "");
		
		DVS_ConfigManager.registerString(unwantedBiomesInSpawnKey, "Unwanted Biomes in Spawn", "none", "List of every biomes that should NOT be in Spawn", "");
		
		DVS_ConfigManager.registerString(onlyBiomeKey, "Spawn Only In Biome", "none", "", "");
		
		DVS_ConfigManager.registerInt(rangeKey, "Scan Range", 2048, 512, 8192, "", "");
		
		DVS_ConfigManager.registerInt(scanStepKey, "Scan Interval", 64, 1, 256, "How many block are skipped before attempting another spawn", "");
		
		DVS_ConfigManager.registerBool(affectHRKey, "Affect Hardcore Respawn", false, "Should the suitable biome list impact hardcore respawn", "");
		
	}

	
	
	
	/*
	
	private static final List<BaseSetting> settings = new ArrayList<>();
	
	public static void registerConfigs(AddonConfig config) {
		for (BiomeGenBase biome : CustomSpawnAddon.allBiomes) {
			String name = biome.biomeName.replace(" ", "");
			String path = suitableBiomesKey + "." + name;
			boolean defaultValue = !HardcoreSpawnUtils.blacklistedBiomes.contains(biome);
			
			config.registerBoolean(path, defaultValue);
			registerSetting(path, Type.BOOLEAN, name, defaultValue, 0, 8, "Can player spawn in: " + name, "Suitable Biomes", config.getBoolean(path));
		}
		
		config.registerCategoryComment(suitableBiomesKey, "Suitable biomes for player's spawn");
		
		config.registerString(onlyBiomeKey,
							  "none",
							  "Player only spawn in this biome (if used, the suitable biomes list is ignored");
		registerSetting(onlyBiomeKey, Type.STRING, "Spawn Only In Biome", "", 0, 8, "The only biome the player can spawn in", "", config.getString(onlyBiomeKey));
	
		config.registerString(wantedBiomesInSpawnKey,
							  "none",
							  "List of every biome you would want in spawn's chunks with associated score (higher score mean that potential spawn is more likely to get picked over another).",
							  "Example: \"Jungle:10,Swampland:10,Forest:5\"");
		registerSetting(wantedBiomesInSpawnKey, Type.STRING, "Wanted Biomes in Spawn", "", 0, 8, "List of every biomes that should be in Spawn", "", config.getString(wantedBiomesInSpawnKey));
		
		config.registerString(unwantedBiomesInSpawnKey,
							  "none",
							  "List of every biome you wouldn't want in spawn's chunks (higher score means that the presence of that biome will make that potential spawn less likely to get picked).",
							  "Example: \"Ice_Plains:5,Ocean:10\"");
		registerSetting(unwantedBiomesInSpawnKey, Type.STRING, "Unwanted Biomes in Spawn", "", 0, 8, "List of every biomes that should NOT be in Spawn", "", config.getString(unwantedBiomesInSpawnKey));
		
		config.registerInt(rangeKey, 2048, "Range of valid spawn search.");
		registerSetting(rangeKey, Type.INT, "Search Range", 2048, 512, 8192, "", "", config.getInt(rangeKey));
		
		config.registerInt(scanStepKey, 68, "Distance in blocks between each spawn attempt.");
		registerSetting(scanStepKey, Type.INT, "Scan Interval", 68, 1, 256, "How many block are skipped before attempting another spawn", "", config.getInt(scanStepKey));
		
		config.registerBoolean(affectHRKey, false, "Should the biome whitelist affect hardcore respawn.");
		registerSetting(affectHRKey, Type.BOOLEAN, "Affect Hardcore Respawn", false, 0, 8, "Should the suitable biome list impact hardcore respawn", "", config.getBoolean(affectHRKey));
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
	}*/
}
