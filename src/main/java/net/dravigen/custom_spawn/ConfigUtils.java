package net.dravigen.custom_spawn;

import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.minecraft.src.BiomeGenBase;

import java.io.*;
import java.util.Properties;

public class ConfigUtils {
	private static final File file = new File("config/custom_spawn.properties");
	public static final Properties properties = new Properties();
	
	
	public static void save() {
		StringBuilder biomes = new StringBuilder();
		
		try (FileInputStream fis = new FileInputStream(file)) {
			properties.load(fis);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome == null) continue;
			
			String name = biome.biomeName.replace(" ", "_");
			
			if (properties.get(name) != null) {
				biomes.append(name)
						.append("=")
						.append(properties.getProperty(name))
						.append("\n");
			}
			else {
				biomes.append(name)
						.append("=")
						.append(!HardcoreSpawnUtils.blacklistedBiomes.contains(biome))
						.append("\n");
			}
		}
		
		String onlyBiomeKey = "onlyBiome";
		String onlyBiomeValue = "";
		
		if (properties.get(onlyBiomeKey) != null) {
			onlyBiomeValue = properties.getProperty(onlyBiomeKey);
		}
		
		int range = 256;
		
		String rangeValue = properties.getProperty("range");
		
		if (rangeValue != null) {
			int newRange = Integer.parseInt(rangeValue);
			
			if (newRange != range) {
				range = newRange;
			}
		}
		
		try (FileWriter writer = new FileWriter(file)) {
			String defaultContent = "# CUSTOM SPAWN CONFIGURATION\n" +
									"\n" +
									"# 1. WHITELISTED BIOMES\n" +
									"# True= allow spawn ; False= prevent spawn.\n" +
									biomes +
									"\n" +
									"# 2. SPAWN ONLY BIOME\n" +
									"# By adding a biome's name, the game will 'try' to only spawn in this biome. \n" +
									onlyBiomeKey + "=" + onlyBiomeValue + "\n" +
									"\n" +
									"# 3. SPAWN SEARCH RANGE\n" +
									"# Range of valid spawn search (higher values increases the chance of crashes). Default=256 \n" +
									"range=" + range;
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
}
