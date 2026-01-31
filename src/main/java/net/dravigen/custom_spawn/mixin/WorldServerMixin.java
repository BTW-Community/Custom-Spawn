package net.dravigen.custom_spawn.mixin;

import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Random;

import static net.dravigen.custom_spawn.CustomSpawnAddon.createCustomSpawn;
import static net.dravigen.custom_spawn.CustomSpawnAddon.customSpawnCoord;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends World {
	@Shadow
	public ChunkProviderServer theChunkProviderServer;
	
	public WorldServerMixin(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider,
			WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent) {
		super(par1ISaveHandler, par2Str, par3WorldProvider, par4WorldSettings, par5Profiler, par6ILogAgent);
	}
	
	@Redirect(method = "createSpawnPosition", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
	private boolean forceSpawn1(List instance, Object o) {
		return true;
	}
	
	
	@Redirect(method = "createSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldProvider;canCoordinateBeSpawn(II)Z"))
	private boolean forceSpawn2(WorldProvider instance, int par1, int par2) {
		return true;
	}
	
	@Redirect(method = "createSpawnPositionLocked", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
	private boolean forceSpawn3(List instance, Object o) {
		return true;
	}
	
	
	@Redirect(method = "createSpawnPositionLocked", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldProvider;canCoordinateBeSpawn(II)Z"))
	private boolean forceSpawn4(WorldProvider instance, int par1, int par2) {
		return true;
	}
	
	@Redirect(method = "createSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldChunkManager;findBiomePosition(IIILjava/util/List;Ljava/util/Random;)Lnet/minecraft/src/ChunkPosition;"))
	private ChunkPosition customSpawn(WorldChunkManager instance, int var1, int var2, int var3, List list,
			Random par1) {
		return getSpawnSpot(instance);
	}
	
	@Redirect(method = "createSpawnPositionLocked", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldChunkManager;findBiomePosition(IIILjava/util/List;Ljava/util/Random;)Lnet/minecraft/src/ChunkPosition;"))
	private ChunkPosition customSpawnLocked(WorldChunkManager instance, int var1, int var2, int var3, List list,
			Random par1) {
		return getSpawnSpot(instance);
	}
	
	@Unique
	private @NotNull ChunkPosition getSpawnSpot(WorldChunkManager instance) {
		customSpawnCoord = createCustomSpawn((WorldServer) (Object) this, instance);
		
		MapGenStronghold stronghold = ((ChunkProviderGenerate) this.theChunkProviderServer.getCurrentChunkProvider()).getStrongholdGenerator();
		stronghold.ranBiomeCheck = false;
		stronghold.canSpawnStructureAtCoords(0, 0);
		
		return customSpawnCoord;
		//return findValidSpawnSpot(this, customSpawnCoord.x, customSpawnCoord.z);
	}
}
