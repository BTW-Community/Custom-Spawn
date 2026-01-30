package net.dravigen.custom_spawn.mixin;

import net.minecraft.src.ChunkPosition;
import net.minecraft.src.MapGenStronghold;
import net.minecraft.src.MapGenStructure;
import net.minecraft.src.WorldChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Random;

import static net.dravigen.custom_spawn.CustomSpawnAddon.*;

@Mixin(MapGenStronghold.class)
public abstract class MapGenStrongholdMixin extends MapGenStructure {
	@Redirect(method = "canSpawnStructureAtCoords", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldChunkManager;findBiomePosition(IIILjava/util/List;Ljava/util/Random;)Lnet/minecraft/src/ChunkPosition;"))
	private ChunkPosition spawnAroundCustomSpawn(WorldChunkManager instance, int x, int z, int range, List list,
			Random rand) {
		if (!createdSpawn) {
			customSpawnCoord = createCustomSpawn(instance);
			
			createdSpawn = true;
		}
		
		return instance.findBiomePosition(x + customSpawnCoord.x, z + customSpawnCoord.z, range, list, rand);
	}
}
