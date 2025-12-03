package net.dravigen.custom_spawn;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.minecraft.src.BiomeGenBase;

import java.util.*;

import static net.dravigen.custom_spawn.ConfigUtils.*;

public class CustomSpawnAddon extends BTWAddon {
    private static CustomSpawnAddon instance;

    public CustomSpawnAddon() {
        super();
    }
	
	public static ArrayList<BiomeGenBase> spawneableBiomes = new ArrayList<>();
	public static ArrayList<BiomeGenBase> unSpawneableBiomes = new ArrayList<>();
	public static ArrayList<BiomeGenBase> wantedBiomesInSpawn = new ArrayList<>();
	public static ArrayList<BiomeGenBase> unwantedBiomesInSpawn = new ArrayList<>();
	public static Map<BiomeGenBase, Integer> biomesWithPriority = new HashMap<>();
	public static BiomeGenBase onlyBiome = null;
	public static int range;
	
	public static Set<String> allBiomeFound = new TreeSet<>();
	public static Set<String> wantedBiomesFound = new TreeSet<>();
	public static Set<String> unwantedBiomesFound = new TreeSet<>();
	
	public static int loadingProgress = 0;
	
	@Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    
		loadFromFile();
		
		String onlyBiomeName = getString(onlyBiomeKey);
		String[] wantedBiomes = properties.getProperty(wantedBiomesInSpawnKey, "").split(",");
		String[] unwantedBiomes = properties.getProperty(unwantedBiomesInSpawnKey, "").split(",");
		
		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome == null) continue;
			
			if (!onlyBiomeName.isEmpty()) {
				if (biome.biomeName.equals(onlyBiomeName)) {
					onlyBiome = biome;
				}
			}
			
			for (String wantedBiome : wantedBiomes) {
				String name = wantedBiome;
				String score = "1";
				
				if (wantedBiome.split(":").length == 2) {
					name = wantedBiome.split(":")[0];
					score = wantedBiome.split(":")[1];
				}

				if (biome.biomeName.replace(" ", "_").equals(name)) {
					wantedBiomesInSpawn.add(biome);
					biomesWithPriority.put(biome, Integer.parseInt(score));
				}
			}
			
			for (String unwantedBiome : unwantedBiomes) {
				String name = unwantedBiome;
				String score = "1";
				
				if (unwantedBiome.split(":").length == 2) {
					name = unwantedBiome.split(":")[0];
					score = unwantedBiome.split(":")[1];
				}
				
				if (biome.biomeName.replace(" ", "_").equals(name)) {
					unwantedBiomesInSpawn.add(biome);
					biomesWithPriority.put(biome, -Integer.parseInt(score));
				}
			}
			
			if (getBool(biome.biomeName.replace(" ", "_"))) {
				spawneableBiomes.add(biome);
			}
			else {
				unSpawneableBiomes.add(biome);
			}
		}
		
		range = getInt(rangeKey);
		
		if (getBool(affectHRKey)) {
			HardcoreSpawnUtils.blacklistedBiomes = unSpawneableBiomes;
		}
	}
}
