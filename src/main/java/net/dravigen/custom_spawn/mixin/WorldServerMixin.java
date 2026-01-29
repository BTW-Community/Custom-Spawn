package net.dravigen.custom_spawn.mixin;

import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin {
	@Redirect(method = "createSpawnPosition", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
	private boolean forceSpawn1(List instance, Object o) {
		return true;
	}
	
	/*
	@Redirect(method = "createSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldProvider;canCoordinateBeSpawn(II)Z"))
	private boolean forceSpawn2(WorldProvider instance, int par1, int par2) {
		return true;
	}*/
	
	@Redirect(method = "createSpawnPositionLocked", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
	private boolean forceSpawn3(List instance, Object o) {
		return true;
	}
	
	/*
	@Redirect(method = "createSpawnPositionLocked", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldProvider;canCoordinateBeSpawn(II)Z"))
	private boolean forceSpawn4(WorldProvider instance, int par1, int par2) {
		return true;
	}*/
}
