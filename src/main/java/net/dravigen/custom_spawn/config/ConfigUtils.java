package net.dravigen.custom_spawn.config;

import api.config.AddonConfig;
import com.prupe.mcpatcher.mal.biome.BiomeAPI;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.minecraft.src.BiomeGenBase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigUtils {
	public static final String suitableBiomesKey = "suitable-biomes";
	public static final String seedFilterKey = "seed-filter";
	public static final String seedLimitKey = "seed-limit";
	public static final String onlyBiomeKey = "only-biome";
	public static final String wantedBiomesInSpawnKey = "wanted-biomes-in-spawn";
	public static final String unwantedBiomesInSpawnKey = "unwanted-biomes-in-spawn";
	public static final String rangeKey = "range";
	public static final String scanStepKey = "stepScan";
	public static final String hideSpawnMsgKey = "hide-msg";
	public static final String disableCustomSpawnKey = "disable-custom-spawn";
	
	private static final Map<String, Object> configValues = new HashMap<>();
	
	private static final List<BaseSetting> settings = new ArrayList<>();
	
	public static List<BaseSetting> getSettings() {
		return settings;
	}
	
	public static void registerConfigs(AddonConfig config) {
		config.registerString(disableCustomSpawnKey, "false");
		register(disableCustomSpawnKey,
				 Type.BOOLEAN,
				 "customspawn.config.disableCustomSpawn.title",
				 false,
				 0,
				 8,
				 "",
				 "");
		
		config.registerString(seedFilterKey, "true");
		register(seedFilterKey,
				 Type.BOOLEAN,
				 "customspawn.config.seedFilter.title",
				 true,
				 0,
				 8,
				 "customspawn.config.seedFilter.shortdesc",
				 "");
		
		config.registerString(seedLimitKey, "4000");
		register(seedLimitKey,
				 Type.INT_SPECIAL,
				 "customspawn.config.seedLimit.title",
				 4000,
				 500,
				 100000,
				 "customspawn.config.seedLimit.shortdesc",
				 "customspawn.config.category.seedFiltering");
		
		config.registerString(onlyBiomeKey, "none");
		register(onlyBiomeKey,
				 Type.STRING,
				 "customspawn.config.onlyBiome.title",
				 "none",
				 0,
				 8,
				 "customspawn.config.onlyBiome.shortdesc",
				 "");
		
		config.registerString(rangeKey, "2048");
		register(rangeKey,
				 Type.INT,
				 "customspawn.config.range.title",
				 4096,
				 1024,
				 16384,
				 "customspawn.config.range.shortdesc",
				 "customspawn.config.category.seedScanning");
		
		config.registerString(scanStepKey, "128");
		register(scanStepKey,
				 Type.INT,
				 "customspawn.config.scanStep.title",
				 64,
				 16,
				 256,
				 "customspawn.config.scanStep.shortdesc",
				 "customspawn.config.category.seedScanning");
		
		config.registerString(hideSpawnMsgKey, "false");
		register(hideSpawnMsgKey,
				 Type.BOOLEAN,
				 "customspawn.config.hideSpawnMsg.title",
				 false,
				 0,
				 8,
				 "customspawn.config.hideSpawnMsg.shortdesc",
				 "");
		
		config.registerString(wantedBiomesInSpawnKey, "none");
		register(wantedBiomesInSpawnKey,
				 Type.MUL_STRING,
				 "customspawn.config.wantedBiomes.title",
				 "none",
				 0,
				 8,
				 "customspawn.config.wantedBiomes.shortdesc",
				 "customspawn.config.category.wanted");
		
		config.registerString(unwantedBiomesInSpawnKey, "none");
		register(unwantedBiomesInSpawnKey,
				 Type.MUL_STRING,
				 "customspawn.config.unwantedBiomes.title",
				 "none",
				 0,
				 8,
				 "customspawn.config.unwantedBiomes.shortdesc",
				 "customspawn.config.category.unwanted");
		
		ArrayList<BiomeGenBase> biomesToSpawnIn = new ArrayList<>();
		biomesToSpawnIn.add(BiomeGenBase.forest);
		biomesToSpawnIn.add(BiomeGenBase.plains);
		biomesToSpawnIn.add(BiomeGenBase.taiga);
		biomesToSpawnIn.add(BiomeGenBase.taigaHills);
		biomesToSpawnIn.add(BiomeGenBase.forestHills);
		
		for (BiomeGenBase biome : CustomSpawnAddon.allBiomes) {
			String name = biome.biomeName.replace(" ", "");
			String path = suitableBiomesKey + "." + name;
			boolean defaultValue = biomesToSpawnIn.contains(biome);
			
			config.registerString(path, String.valueOf(defaultValue));
			register(path,
					 Type.BOOLEAN,
					 "customspawn.biome." + name,
					 defaultValue,
					 0,
					 8,
					 "customspawn.config.suitableBiome.desc",
					 "customspawn.config.category.suitable");
		}
	}
	
	public static void reloadConfigs(AddonConfig config) {
		for (BaseSetting setting : settings) {
			try {
				if (setting.type() == Type.BOOLEAN)
					configValues.put(setting.id(), Boolean.parseBoolean(config.getString(setting.id())));
				else if (setting.type() == Type.INT || setting.type() == Type.INT_SPECIAL)
					configValues.put(setting.id(), Integer.parseInt(config.getString(setting.id())));
				else if (setting.type() == Type.STRING) configValues.put(setting.id(), config.getString(setting.id()));
				else if (setting.type() == Type.MUL_STRING)
					configValues.put(setting.id(), config.getString(setting.id()));
			}
			catch (Exception e) {
				configValues.put(setting.id(), setting.defaultValue());
			}
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
			if (Boolean.parseBoolean(configValues.get(suitableBiomesKey + "." + biomeName).toString()) ||
					biomeName.equalsIgnoreCase(CustomSpawnAddon.onlyBiome)) {
				CustomSpawnAddon.spawneableBiomes.add(biomeName);
				CustomSpawnAddon.spawneableBiomesGen.add(BiomeAPI.findBiomeByName(biomeName));
			}
			else {
				CustomSpawnAddon.unSpawneableBiomes.add(biomeName);
			}
		}
		
		CustomSpawnAddon.range = Integer.parseInt(configValues.get(rangeKey).toString());
		
		CustomSpawnAddon.scanStep = Integer.parseInt(configValues.get(scanStepKey).toString());
	}
	
	public static int getInt(String id) {
		try {
			return (int) configValues.get(id);
		}
		catch (Exception e) {
			return Integer.parseInt((String) configValues.get(id));
		}
	}
	
	public static double getDouble(String id) {
		try {
			return (double) configValues.get(id);
		}
		catch (Exception e) {
			return Double.parseDouble((String) configValues.get(id));
		}		}
	
	public static boolean getBoolean(String id) {
		try {
			return (boolean) configValues.get(id);
		}
		catch (Exception e) {
			return Boolean.parseBoolean((String) configValues.get(id));
		}
	}
	
	public static String getString(String id) {
		return (String) configValues.get(id);
	}
	
	public static void setValue(String id, Object value) {
		if (configValues.containsKey(id)) {
			configValues.put(id, value);
			
			try {
				AddonConfig addonConfig = CustomSpawnAddon.getInstance().addonConfig;
				
				Field configField = AddonConfig.class.getDeclaredField("currentConfig");
				configField.setAccessible(true);
				Config currentConfig = (Config) configField.get(addonConfig);
				
				String stringValue = String.valueOf(value);
				
				ConfigValue oldValue = currentConfig.getValue(id);
				ConfigValue newValueWithOrigin = ConfigValueFactory.fromAnyRef(stringValue).withOrigin(oldValue.origin());
				
				Config updatedConfig = currentConfig.withValue(id, newValueWithOrigin);
				configField.set(addonConfig, updatedConfig);
				
				ConfigUtils.updateInternalConfigs();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		MUL_STRING,
		INT_SPECIAL
	}
}
