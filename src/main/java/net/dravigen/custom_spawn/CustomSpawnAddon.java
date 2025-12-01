package net.dravigen.custom_spawn;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.minecraft.src.BiomeGenBase;

import java.util.ArrayList;

public class CustomSpawnAddon extends BTWAddon {
    private static CustomSpawnAddon instance;

    public CustomSpawnAddon() {
        super();
    }
	
	public static ArrayList<BiomeGenBase> spawneableBiomes = new ArrayList<>();
	public static ArrayList<BiomeGenBase> unSpawneableBiomes = new ArrayList<>();
	public static BiomeGenBase onlyBiome = null;
	public static int range;

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    
		ConfigUtils.loadFromFile();
		
		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome == null) continue;
			
			if (!Boolean.parseBoolean((String) ConfigUtils.properties.get(biome.biomeName.replace(" ", "_")))) {
				spawneableBiomes.add(biome);
			}
			else {
				unSpawneableBiomes.add(biome);
			}
		}
		
		String onlyBiomeName = ConfigUtils.properties.getProperty("onlyBiome");
		
		if (!onlyBiomeName.isEmpty()) {
			for (BiomeGenBase biomeGenBase : BiomeGenBase.biomeList) {
				if (biomeGenBase != null && biomeGenBase.biomeName.equals(onlyBiomeName)) {
					onlyBiome = biomeGenBase;
				}
			}
		}
		
		range = Integer.parseInt(ConfigUtils.properties.getProperty("range"));
		
		HardcoreSpawnUtils.blacklistedBiomes = unSpawneableBiomes;
	}
}