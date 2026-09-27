package net.dravigen.custom_spawn.mixin;

import com.prupe.mcpatcher.mal.biome.BiomeAPI;
import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.dravigen.custom_spawn.config.ConfigUtils;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProvider.class)
public class WorldProviderMixin {
	@Shadow
	public World worldObj;
	
	@Inject(method = "canCoordinateBeSpawn", at = @At("RETURN"), cancellable = true)
	private void canSpawnHereFromList(int par1, int par2, CallbackInfoReturnable<Boolean> cir) {
		if (ConfigUtils.getBoolean(ConfigUtils.disableCustomSpawnKey)) return;
		
		int id = this.worldObj.getFirstUncoveredBlock(par1, par2);
		
		String onlyBiome = CustomSpawnAddon.onlyBiome;
		BiomeGenBase biome = BiomeAPI.findBiomeByName(onlyBiome);
		
		cir.setReturnValue(id == Block.grass.blockID ||
								   (id == Block.waterStill.blockID &&
										   (biome == BiomeGenBase.ocean || biome == BiomeGenBase.river) ||
								   id == Block.ice.blockID &&
										   (biome == BiomeGenBase.frozenRiver || biome == BiomeGenBase.frozenOcean)) ||
								   (id == Block.sand.blockID && biome == BiomeGenBase.desert || biome == BiomeGenBase.beach));
	}
}
