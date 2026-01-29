package net.dravigen.custom_spawn.config;

public record BaseSetting(String id, ConfigUtils.Type type, String name, Object defaultValue, double min,
						  double max, String description, String category) {}

