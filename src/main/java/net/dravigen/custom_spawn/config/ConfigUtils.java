package net.dravigen.custom_spawn.config;

import api.config.AddonConfig;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.minecraft.src.BiomeGenBase;

import java.util.*;

public class ConfigUtils {
	public static final String suitableBiomesKey = "suitable-biomes";
	public static final String onlyBiomeKey = "only-biome";
	public static final String wantedBiomesInSpawnKey = "wanted-biomes-in-spawn";
	public static final String unwantedBiomesInSpawnKey = "unwanted-biomes-in-spawn";
	public static final String rangeKey = "range";
	public static final String scanStepKey = "stepScan";
	public static final String affectHRKey = "affect-HR";
	
	private static final Map<String, Object> configValues = new HashMap<>();
	
	private static final List<BaseSetting> settings = new ArrayList<>();
	
	public static List<BaseSetting> getSettings() {
		return settings;
	}
	
	public static void registerConfigs(AddonConfig config) {
		config.registerString(wantedBiomesInSpawnKey,
							  "none",
							  "List of every biome you would want in spawn's chunks with associated score (higher score mean that potential spawn is more likely to get picked over another).",
							  "Example: \"Jungle:10,Swampland:10,Forest:5\"");
		register(wantedBiomesInSpawnKey, Type.MUL_STRING, "Wanted Biomes in Spawn", "none", 0, 8, "List of every biomes that should be in Spawn", "1.Wanted Biomes");
		
		config.registerString(unwantedBiomesInSpawnKey,
							  "none",
							  "List of every biome you wouldn't want in spawn's chunks (higher score means that the presence of that biome will make that potential spawn less likely to get picked).",
							  "Example: \"Ice_Plains:5,Ocean:10\"");
		register(unwantedBiomesInSpawnKey, Type.MUL_STRING, "Unwanted Biomes in Spawn", "none", 0, 8, "List of every biomes that should NOT be in Spawn", "2.Unwanted biomes");
		
		for (BiomeGenBase biome : CustomSpawnAddon.allBiomes) {
			String name = biome.biomeName.replace(" ", "");
			String path = suitableBiomesKey + "." + name;
			boolean defaultValue = !HardcoreSpawnUtils.blacklistedBiomes.contains(biome);
			
			config.registerString(path, String.valueOf(defaultValue));
			register(path, Type.BOOLEAN, name, defaultValue, 0, 8, "Player can spawn in: " + name, "3.Suitable Biomes");
		}
		
		config.registerString(onlyBiomeKey,
							  "none",
							  "Player only spawn in this biome (if used, the suitable biomes list is ignored");
		register(onlyBiomeKey, Type.STRING, "Spawn Only In Biome", "none", 0, 8, "The only biome the player should spawn in", "");
		
		config.registerString(rangeKey, "2048", "Range of valid spawn search.");
		register(rangeKey, Type.INT, "Scan Range", 2048, 512, 8192, "Range of valid spawn search", "");
		
		config.registerString(scanStepKey, "128", "Distance in blocks between each spawn attempt.");
		register(scanStepKey, Type.INT, "Scan Interval", 128, 16, 256, "How many block are skipped before attempting another spawn", "");
		
		config.registerString(affectHRKey, "false", "Should the biome whitelist affect hardcore respawn.");
		register(affectHRKey, Type.BOOLEAN, "Affect Hardcore Respawn", false, 0, 8, "Should the suitable biome list impacts hardcore respawn", "");
	}
	
	public static void reloadConfigs(AddonConfig config) {
		for (BaseSetting setting : settings) {
			if (setting.type() == Type.BOOLEAN) configValues.put(setting.id(), Boolean.parseBoolean(config.getString(setting.id())));
			else if (setting.type() == Type.INT) configValues.put(setting.id(), Integer.parseInt(config.getString(setting.id())));
			else if (setting.type() == Type.STRING) configValues.put(setting.id(), config.getString(setting.id()));
			else if (setting.type() == Type.MUL_STRING) configValues.put(setting.id(), config.getString(setting.id()));
		}
		
		updateInternalConfigs();
	}
	
	public static void updateInternalConfigs() {
		CustomSpawnAddon.wantedBiomesInSpawn.clear();
		CustomSpawnAddon.unwantedBiomesInSpawn.clear();
		CustomSpawnAddon.spawneableBiomes.clear();
		CustomSpawnAddon.unSpawneableBiomes.clear();
		
		String[] splitu = configValues.get(wantedBiomesInSpawnKey).toString().split(",");
		String[] splitw = configValues.get(unwantedBiomesInSpawnKey).toString().split(",");
		
		for (int i = 0; i < splitu.length; i++) {
			if (i == 0) CustomSpawnAddon.wantedBiomesInSpawn.add(i, "none");
			else CustomSpawnAddon.wantedBiomesInSpawn.add(i, splitu[i]);
		}
		
		for (int i = 0; i < splitw.length; i++) {
			if (i == 0) CustomSpawnAddon.unwantedBiomesInSpawn.add(i, "none");
			else CustomSpawnAddon.unwantedBiomesInSpawn.add(i, splitw[i]);
		}
		
		CustomSpawnAddon.onlyBiome = configValues.get(onlyBiomeKey).toString();
		
		for (String biomeName : CustomSpawnAddon.allBiomesName) {
			if (Boolean.parseBoolean(configValues.get(suitableBiomesKey + "." + biomeName)
											 .toString()) || biomeName.equalsIgnoreCase(CustomSpawnAddon.onlyBiome)) {
				CustomSpawnAddon.spawneableBiomes.add(biomeName);
			}
			else {
				CustomSpawnAddon.unSpawneableBiomes.add(biomeName);
			}
		}
	
		CustomSpawnAddon.range = Integer.parseInt(configValues.get(rangeKey).toString());
		
		CustomSpawnAddon.scanStep = Integer.parseInt(configValues.get(scanStepKey).toString());
		
		if (Boolean.parseBoolean(configValues.get(affectHRKey).toString())) {
			ArrayList<BiomeGenBase> biomes = new ArrayList<>();
			
			for (BiomeGenBase biome : CustomSpawnAddon.allBiomes) {
				if (CustomSpawnAddon.unSpawneableBiomes.contains(biome.biomeName.replace(" ", ""))) {
					biomes.add(biome);
				}
			}
			
			HardcoreSpawnUtils.blacklistedBiomes = biomes;
		}
	}
	
	public static int getInt(String id) {
		return (int) configValues.get(id);
	}
	
	public static double getDouble(String id) {
		return (double) configValues.get(id);
	}
	
	public static boolean getBoolean(String id) {
		return (boolean) configValues.get(id);
	}
	
	public static String getString(String id) {
		return (String) configValues.get(id);
	}
	
	public static void setValue(String id, Object value) {
		if (configValues.containsKey(id)) {
			configValues.put(id, value);
			ConfigUpdater.updateValue(CustomSpawnAddon.getInstance().addonConfig, id, value);
		}
	}
	
	public static <T> void register(String id, Type type, String name, T defaultValue, double min, double max,
			String description, String category) {
		BaseSetting setting = new BaseSetting(id, type, name, defaultValue, min, max, description, category);
		settings.add(setting);
	}
		
	public Object getValue(String id) {
		return configValues.get(id);
	}
	
	public enum Type {
		STRING,
		INT,
		BOOLEAN,
		MUL_STRING
	}
}
