package net.dravigen.custom_spawn;

import api.AddonHandler;
import api.BTWAddon;
import api.config.AddonConfig;
import net.dravigen.custom_spawn.config.ConfigUtils;
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
	
	public static ArrayList<BiomeGenBase> spawneableBiomes = new ArrayList<>();
	public static ArrayList<BiomeGenBase> unSpawneableBiomes = new ArrayList<>();
	public static ArrayList<BiomeGenBase> wantedBiomesInSpawn = new ArrayList<>();
	public static ArrayList<BiomeGenBase> unwantedBiomesInSpawn = new ArrayList<>();
	public static ArrayList<BiomeGenBase> allBiomes = new ArrayList<>();
	public static Map<BiomeGenBase, Integer> biomesWithPriority = new HashMap<>();
	public static BiomeGenBase onlyBiome = null;
	public static int range;
	public static int scanStep;
	
	public static Set<String> allBiomeFound = new TreeSet<>();
	public static Set<String> wantedBiomesFound = new TreeSet<>();
	public static Set<String> unwantedBiomesFound = new TreeSet<>();
	
	public static int loadingProgress = 0;
	
	@Override
	public void handleConfigProperties(AddonConfig config) {
		ConfigUtils.reloadConfigs(config);
	}
	
	@Override
	public void registerConfigProperties(AddonConfig config) {
		ConfigUtils.registerConfigs(config);
	}
	
	@Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
	}
	
	@Override
	public void preInitialize() {
		for (BiomeGenBase biomeGenBase : BiomeGenBase.biomeList) {
			if (biomeGenBase == null) continue;
			
			allBiomes.add(biomeGenBase);
		}
	}
}
