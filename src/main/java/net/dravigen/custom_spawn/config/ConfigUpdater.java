package net.dravigen.custom_spawn.config;

import api.config.AddonConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ConfigUpdater {
	
	/**
	 * Updates a config value at runtime and saves it to the file.
	 * Bypasses internal API validation logic.
	 * * @param addonConfig The config instance to modify
	 *
	 * @param path     The config path (e.g., "general.toggle")
	 * @param newValue The new value (Integer, Boolean, Double, String, etc.)
	 */
	public static void updateValue(AddonConfig addonConfig, String path, Object newValue) {
		try {
			if (!addonConfig.getExists(path)) {
				System.err.println("[ConfigUpdater] Path does not exist: " + path);
				
				return;
			}
			
			Field configField = AddonConfig.class.getDeclaredField("currentConfig");
			configField.setAccessible(true);
			Config currentConfig = (Config) configField.get(addonConfig);
			
			String stringValue = String.valueOf(newValue);
			
			ConfigValue oldValue = currentConfig.getValue(path);
			ConfigValue newValueWithOrigin = ConfigValueFactory.fromAnyRef(stringValue)
					.withOrigin(oldValue.origin());
			
			Config updatedConfig = currentConfig.withValue(path, newValueWithOrigin);
			configField.set(addonConfig, updatedConfig);
			
			saveConfig(addonConfig);
			
		}
		catch (Exception e) {
			System.err.println("[ConfigUpdater] Critical error updating " + path);
			e.printStackTrace();
		}
	}
	
	public static void saveConfig(AddonConfig addonConfig) {
		File configFile = addonConfig.getConfigFilePath().toFile();
		
		try {
			if (!configFile.exists()) {
				Files.createFile(configFile.toPath());
			}
			
			BufferedWriter writer = Files.newBufferedWriter(configFile.toPath(), StandardOpenOption.TRUNCATE_EXISTING);
			writer.write(addonConfig.render());
			writer.close();
		}
		catch (IOException e) {
			try {
				Field changedField = AddonConfig.class.getDeclaredField("hasChanged");
				changedField.setAccessible(true);
				changedField.setBoolean(addonConfig, true);
				
				addonConfig.readAndWriteConfig();
			}
			catch (Exception ex) {
				System.err.println("[ConfigUpdater] Failed to save config to disk.");
			}
		}
	}
}