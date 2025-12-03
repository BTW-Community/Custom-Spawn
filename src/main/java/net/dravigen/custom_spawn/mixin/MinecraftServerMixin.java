package net.dravigen.custom_spawn.mixin;

import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Shadow
	protected abstract void outputPercentRemaining(String par1Str, int par2);
	
	@Redirect(method = "initialWorldChunkLoad", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;outputPercentRemaining(Ljava/lang/String;I)V"))
	private void getProgress(MinecraftServer instance, String par1Str, int par2) {
		this.outputPercentRemaining(par1Str, par2);
		CustomSpawnAddon.loadingProgress=par2;
	}
}
