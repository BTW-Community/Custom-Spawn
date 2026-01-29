package net.dravigen.custom_spawn.config;

public record BaseSetting(String id, DVS_ConfigManager.Type type, String name, Object defaultValue, double min,
						  double max, String description, String category) {}

