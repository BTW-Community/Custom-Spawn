package net.dravigen.custom_spawn.mixin;

import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.dravigen.custom_spawn.config.ConfigUtils;
import net.minecraft.src.IntCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntCache.class)
public class IntCacheMx {
	@Inject(method = "getIntCache", at = @At("HEAD"), cancellable = true)
	private static void bypassGetIntCache(int i, CallbackInfoReturnable<int[]> cir) {
		if (ConfigUtils.getBoolean(ConfigUtils.disableCustomSpawnKey) || !CustomSpawnAddon.isSearching) return;
		
		cir.setReturnValue(new int[i]);
	}
	
	@Inject(method = "resetIntCache", at = @At("HEAD"), cancellable = true)
	private static void bypassResetIntCache(CallbackInfo ci) {
		if (ConfigUtils.getBoolean(ConfigUtils.disableCustomSpawnKey) || !CustomSpawnAddon.isSearching) return;
		
		ci.cancel();
	}
}
