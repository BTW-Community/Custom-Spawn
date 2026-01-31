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
		int var3 = 63;
		while (!this.worldObj.isAirBlock(par1, var3 + 1, par2)) {
			++var3;
		}
		int id = this.worldObj.getBlockId(par1, var3, par2);
		
		String onlyBiome = CustomSpawnAddon.onlyBiome;
		cir.setReturnValue((id == Block.grass.blockID ||
				id == Block.stone.blockID ||
				id == Block.dirt.blockID ||
				id == Block.gravel.blockID ||
				id == Block.sand.blockID) && worldObj.getBlockId(par1, var3 + 1, par2) != Block.waterStill.blockID ||
								   id == Block.waterStill.blockID &&
										   (onlyBiome.equalsIgnoreCase(BiomeGenBase.ocean.biomeName.replace(" ", "")) ||
												   onlyBiome.equalsIgnoreCase(BiomeGenBase.river.biomeName.replace(" ",
																												   ""))) ||
								   id == Block.ice.blockID &&
										   (onlyBiome.equalsIgnoreCase(BiomeGenBase.frozenRiver.biomeName.replace(" ",
																												  ""))));
	}
}
