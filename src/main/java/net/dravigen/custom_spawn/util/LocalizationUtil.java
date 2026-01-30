// java
package net.dravigen.custom_spawn.util;

import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.dravigen.custom_spawn.config.BaseSetting;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.I18n;

public final class LocalizationUtil {
    private LocalizationUtil() {}

    public static String localizeBiome(String key) {

        if (key == null) return "";

        if (key.equalsIgnoreCase("none")) {

            String string = I18n.getString("customspawn.biome.none");

            if (string != null && !string.equals("customspawn.biome.none")) return string;

            return "none";
        }

        String fullKey = "customspawn.biome." + key;
        String localized = I18n.getString(fullKey);

        if (localized == null || localized.equals(fullKey)) return key;

        return localized;
    }

    public static String localizeSettingName(BaseSetting setting) {

        if (setting == null) return "";

        String name = setting.name();

        if (name == null) return "";

        if (name.startsWith("customspawn.")) {
            return I18n.getString(name);
        }

        for (BiomeGenBase biome : CustomSpawnAddon.allBiomes) {

            if (biome == null) continue;

            if (biome.biomeName.replace(" ", "").equalsIgnoreCase(name)) {
                return biome.biomeName;
            }
        }

        return name;
    }
}
