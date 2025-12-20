package net.dravigen.custom_spawn.mixin;

import net.dravigen.custom_spawn.CustomSpawnAddon;
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
		if (CustomSpawnAddon.spawneableBiomes.contains(this.worldObj.getBiomeGenForCoords(par1, par2))) {
			int id = this.worldObj.getFirstUncoveredBlock(par1, par2);
			
			cir.setReturnValue(id == Block.grass.blockID ||
									   id == Block.waterStill.blockID &&
											   (CustomSpawnAddon.onlyBiome == BiomeGenBase.ocean ||
													   CustomSpawnAddon.onlyBiome == BiomeGenBase.river) ||
									   id == Block.ice.blockID &&
											   (CustomSpawnAddon.onlyBiome == BiomeGenBase.frozenRiver));
		}
	}
}
