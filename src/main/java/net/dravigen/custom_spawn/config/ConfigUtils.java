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
							  "none");
		register(wantedBiomesInSpawnKey, Type.MUL_STRING, "customspawn.config.wantedBiomes.title", "none", 0, 8, "customspawn.config.wantedBiomes.shortdesc", "customspawn.config.category.wanted");

		config.registerString(unwantedBiomesInSpawnKey,
							  "none");
		register(unwantedBiomesInSpawnKey, Type.MUL_STRING, "customspawn.config.unwantedBiomes.title", "none", 0, 8, "customspawn.config.unwantedBiomes.shortdesc", "customspawn.config.category.unwanted");

		for (BiomeGenBase biome : CustomSpawnAddon.allBiomes) {
			String name = biome.biomeName.replace(" ", "");
			String path = suitableBiomesKey + "." + name;
			boolean defaultValue = !HardcoreSpawnUtils.blacklistedBiomes.contains(biome);

			config.registerString(path, String.valueOf(defaultValue));
			register(path, Type.BOOLEAN, "customspawn.biome." + name, defaultValue, 0, 8, "customspawn.config.suitableBiome.desc", "customspawn.config.category.suitable");
		}

		config.registerString(onlyBiomeKey,
							  "none");
		register(onlyBiomeKey, Type.STRING, "customspawn.config.onlyBiome.title", "none", 0, 8, "customspawn.config.onlyBiome.shortdesc", "");

		config.registerString(rangeKey, "2048");
		register(rangeKey, Type.INT, "customspawn.config.range.title", 2048, 512, 8192, "customspawn.config.range.shortdesc", "");

		config.registerString(scanStepKey, "128");
		register(scanStepKey, Type.INT, "customspawn.config.scanStep.title", 128, 16, 256, "customspawn.config.scanStep.shortdesc", "");

		config.registerString(affectHRKey, "false");
		register(affectHRKey, Type.BOOLEAN, "customspawn.config.affectHR.title", false, 0, 8, "customspawn.config.affectHR.shortdesc", "");
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
