package net.dravigen.custom_spawn;

import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.minecraft.src.BiomeGenBase;

import java.io.*;
import java.util.Properties;

public class ConfigUtils {
	private static final File file = new File("config/custom_spawn.properties");
	public static final Properties properties = new Properties();
	
	public static final String onlyBiomeKey = "spawnOnlyInBiome";
	public static final String rangeKey = "range";
	public static final String wantedBiomesInSpawnKey = "wantedBiomesInSpawn";
	public static final String unwantedBiomesInSpawnKey = "unwantedBiomesInSpawn";
	public static final String affectHRKey = "affectHR";
	
	
	public static void save() {
		StringBuilder biomes = new StringBuilder();
		
		try (FileInputStream fis = new FileInputStream(file)) {
			properties.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (properties.containsKey("onlyBiome")) {
			properties.clear();
		}
		
		String onlyBiome = properties.getProperty(onlyBiomeKey, "");
		int range = Integer.parseInt(properties.getProperty(rangeKey, "2048"));
		String wantedBiomesInSpawn = properties.getProperty(wantedBiomesInSpawnKey, "");
		String unwantedBiomesInSpawn = properties.getProperty(unwantedBiomesInSpawnKey, "");
		boolean affectHR = Boolean.parseBoolean(properties.getProperty(affectHRKey, "false"));
		
		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome == null) continue;
			
			String name = biome.biomeName.replace(" ", "_");
			
			if (properties.get(name) != null) {
				biomes.append(name).append("=").append(properties.getProperty(name)).append("\n");
			}
			else {
				biomes.append(name)
						.append("=")
						.append(!HardcoreSpawnUtils.blacklistedBiomes.contains(biome))
						.append("\n");
			}
		}
		
		try (FileWriter writer = new FileWriter(file)) {
			String defaultContent = "### CUSTOM SPAWN CONFIGURATION\n" +
					"\n" +
					"## 1. WHITELISTED BIOMES\n" +
					"# List of biomes allowed to be used as a spawning location.\n" +
					"# True= allow spawn ; False= prevent spawn.\n" +
					biomes +
					"\n" +
					"## 2. SPAWN ONLY IN BIOME\n" +
					"# By adding a biome's name, the game will 'try' to only spawn in this biome.\n" +
					onlyBiomeKey +
					"=" +
					onlyBiome +
					"\n" +
					"\n" +
					"## 3. SPAWN'S CHUNKS BIOMES CRITERIA\n" +
					"# List of every biome you would want in spawn's chunks with associated score (higher score mean that potential spawn is more likely to get picked over another).\n" +
					"# Example: wantedBiomesInSpawn=Jungle:10,Swampland:10,Forest:5\n" +
					wantedBiomesInSpawnKey +
					"=" +
					wantedBiomesInSpawn +
					"\n" +
					"\n" +
					"# List of every biome you wouldn't want in spawn's chunks (higher score means that the presence of that biome will make that potential spawn less likely to get picked).\n" +
					"# Example: unwantedBiomesInSpawn=Ice_Plains:5,Ocean:10\n" +
					unwantedBiomesInSpawnKey +
					"=" +
					unwantedBiomesInSpawn +
					"\n" +
					"\n" +
					"## 4. MISC OPTIONS\n" +
					"# Range of valid spawn search. Default=2048 \n" +
					rangeKey +
					"=" +
					range +
					"\n" +
					"\n" +
					"# Should the biome whitelist affect hardcore respawn.\n" +
					affectHRKey +
					"=" +
					affectHR;
			writer.write(defaultContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadFromFile() {
		save();
		
		try (FileInputStream fis = new FileInputStream(file)) {
			properties.load(fis);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static boolean getBool(String key) {
		return Boolean.parseBoolean(properties.getProperty(key));
	}
	
	public static int getInt(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}
	
	public static String getString(String key) {
		return properties.getProperty(key);
	}
}
