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
		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome == null) continue;
			
			allBiomesName.add(biome.biomeName.replace(" ", ""));
			allBiomes.add(biome);
		}
	}
}
