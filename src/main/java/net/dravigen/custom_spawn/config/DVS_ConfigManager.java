package net.dravigen.custom_spawn.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class DVS_ConfigManager {
	private static final File file = new File("config/custom_spawn.properties");
	private static final Properties properties = new Properties();
		private static final List<BaseSetting> settings = new ArrayList<>();
	private static final Map<String, Object> configValues = new HashMap<>();
	private static boolean isDirty = false;
	
	public static <T> ConfigValue<T> registerBool(String id, String name, T defaultValue, String description,
			String category) {
		return register(id, Type.BOOLEAN, name, defaultValue, 0, 0, description, category);
	}
	
	public static <T> ConfigValue<T> registerDouble(String id, String name, T defaultValue, String description,
			String category) {
		return register(id, Type.DOUBLE, name, defaultValue, 0, 8, description, category);
	}
	
	public static <T> ConfigValue<T> registerDouble(String id, String name, T defaultValue, double min, double max,
			String description, String category) {
		return register(id, Type.DOUBLE, name, defaultValue, min, max, description, category);
	}
	
	public static <T> ConfigValue<T> registerInt(String id, String name, T defaultValue, String description,
			String category) {
		return register(id, Type.INT, name, defaultValue, 0, 8, description, category);
	}
	
	public static <T> ConfigValue<T> registerInt(String id, String name, T defaultValue, double min, double max,
			String description, String category) {
		return register(id, Type.INT, name, defaultValue, min, max, description, category);
	}
	
	public static <T> ConfigValue<T> registerString(String id, String name, T defaultValue, String description, String category) {
		return register(id, Type.STRING, name, defaultValue, 0, 8, description, category);
	}
	
	public static <T> ConfigValue<T> register(String id, Type type, String name, T defaultValue, double min, double max,
			String description, String category) {
		BaseSetting setting = new BaseSetting(id, type, name, defaultValue, min, max, description, category);
		settings.add(setting);
		
		String stringValue = properties.getProperty(id);
		Object valueToStore = defaultValue;
		
		if (stringValue != null) {
			try {
				valueToStore = parse(type, stringValue);
			} catch (Exception e) {
				System.err.println("Failed to parse '" + id + "', resetting to default.");
				isDirty = true;
			}
		}
		else {
			isDirty = true;
		}
		
		configValues.put(id, valueToStore);
		
		return new ConfigValue<>(id);
	}
	
	private static Object parse(Type type, String value) {
		return switch (type) {
			case BOOLEAN -> Boolean.parseBoolean(value);
			case INT -> Integer.parseInt(value);
			case DOUBLE -> Double.parseDouble(value);
			case STRING -> value;
		};
	}
	
	public static void save() {
		if (!isDirty && file.exists()) return;
		
		for (BaseSetting setting : settings) {
			Object val = configValues.get(setting.id());
			properties.setProperty(setting.id(), String.valueOf(val));
		}
		
		try (FileOutputStream fos = new FileOutputStream(file)) {
			properties.store(fos, "Custom Spawn Settings");
			isDirty = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadFromFile() {
		if (!file.exists()) return;
		try (FileInputStream fis = new FileInputStream(file)) {
			properties.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<BaseSetting> getSettings() {
		return Collections.unmodifiableList(settings);
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
			isDirty = true;
		}
	}
	
	public Object getValue(String id) {
		return configValues.get(id);
	}
	
	public enum Type {
		BOOLEAN,
		INT,
		DOUBLE,
		STRING
	}
	
	public static class ConfigValue<T> {
		private final String id;
		
		private ConfigValue(String id) {
			this.id = id;
		}
		
		@SuppressWarnings("unchecked")
		public T get() {
			return (T) configValues.get(id);
		}
		
		public boolean getBool() {
			return (boolean) configValues.get(id);
		}
		
		public int getInt() {
			return (int) configValues.get(id);
		}
		
		public double getDouble() {
			return (double) configValues.get(id);
		}
		
		public String getString() {
			return (String) configValues.get(id);
		}
		
		public void set(T value) {
			configValues.put(id, value);
			isDirty = true;
		}
	}
}