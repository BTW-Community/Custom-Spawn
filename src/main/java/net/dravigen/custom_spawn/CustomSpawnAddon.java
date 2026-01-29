package net.dravigen.custom_spawn;

import api.AddonHandler;
import api.BTWAddon;
import api.config.AddonConfig;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.dravigen.custom_spawn.config.ConfigUtils;
import net.dravigen.custom_spawn.config.DVS_ConfigManager;
import net.minecraft.src.BiomeGenBase;

import java.util.*;

public class CustomSpawnAddon extends BTWAddon {
    private static CustomSpawnAddon instance;

    public CustomSpawnAddon() {
        super();
		instance = this;
    }
	
	public static BTWAddon getInstance() {
		return instance;
	}
	
	public static Map<String, Boolean> suitableBiomesMap = new HashMap<>();
	public static String onlyBiomeS = "none";
	public static boolean affectHR = false;
	public static int range = 2048;
	public static int scanStep = 68;
	
	
	
	
	
	public static ArrayList<BiomeGenBase> spawneableBiomes = new ArrayList<>();
	public static ArrayList<BiomeGenBase> unSpawneableBiomes = new ArrayList<>();
	public static ArrayList<BiomeGenBase> wantedBiomesInSpawn = new ArrayList<>();
	public static ArrayList<BiomeGenBase> unwantedBiomesInSpawn = new ArrayList<>();
	public static ArrayList<BiomeGenBase> allBiomes = new ArrayList<>();
	public static Map<BiomeGenBase, Integer> biomesWithPriority = new HashMap<>();
	public static BiomeGenBase onlyBiome = null;
	
	public static Set<String> allBiomeFound = new TreeSet<>();
	public static Set<String> wantedBiomesFound = new TreeSet<>();
	public static Set<String> unwantedBiomesFound = new TreeSet<>();
	
	public static int loadingProgress = 0;
	
	/*
	@Override
	public void handleConfigProperties(AddonConfig config) {
		//ConfigUtils.reloadConfigs(config);
	}
	
	@Override
	public void registerConfigProperties(AddonConfig config) {
		//ConfigUtils.registerConfigs(config);
	}*/
	
	@Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
		
		DVS_ConfigManager.loadFromFile();
		
		ConfigUtils.registerAllSettings();
		
		DVS_ConfigManager.save();
	}
	
	@Override
	public void preInitialize() {
		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome == null) continue;
			
			allBiomes.add(biome);
			String name = biome.biomeName.replace(" ", "");
			boolean defaultValue = !HardcoreSpawnUtils.blacklistedBiomes.contains(biome);
			
			suitableBiomesMap.put(name, defaultValue);
		}
	}
}
