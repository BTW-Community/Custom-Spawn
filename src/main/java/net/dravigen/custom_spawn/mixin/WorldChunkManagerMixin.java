package net.dravigen.custom_spawn.mixin;

import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.World;
import net.minecraft.src.WorldChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(WorldChunkManager.class)
public abstract class WorldChunkManagerMixin {
	@Shadow
	private List biomesToSpawnIn;
	
	@Shadow
	public abstract ChunkPosition findBiomePosition(int par1, int par2, int par3, List par4List, Random par5Random);
	
	@Inject(method = "<init>(Lnet/minecraft/src/World;)V", at = @At("TAIL"))
	private void customAllowedBiomeList(World par1World, CallbackInfo ci) {
		if (CustomSpawnAddon.onlyBiome == null) {
			this.biomesToSpawnIn = CustomSpawnAddon.spawneableBiomes;
		}
		else {
			this.biomesToSpawnIn.clear();
			this.biomesToSpawnIn.add(CustomSpawnAddon.onlyBiome);
		}
	}
	
	@Inject(method = "findBiomePosition", at = @At("RETURN"), cancellable = true)
	private void useListIfOnlyBiomeUnvalid(int par1, int par2, int par3, List par4List, Random par5Random,
			CallbackInfoReturnable<ChunkPosition> cir) {
		if (cir.getReturnValue() == null && par4List != CustomSpawnAddon.spawneableBiomes) {
			cir.setReturnValue(this.findBiomePosition(par1, par2, par3, CustomSpawnAddon.spawneableBiomes, par5Random));
		}
	}
}
