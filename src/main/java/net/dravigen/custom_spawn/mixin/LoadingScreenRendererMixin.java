package net.dravigen.custom_spawn.mixin;

import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.minecraft.src.LoadingScreenRenderer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingScreenRenderer.class)
public abstract class LoadingScreenRendererMixin {
	@Shadow
	private Minecraft mc;
	
	@Shadow
	private String field_73727_a;
	
	@Inject(method = "setLoadingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I", ordinal = 1))
	private void drawProgress(int par1, CallbackInfo ci) {
		ScaledResolution var4 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		int var5 = var4.getScaledWidth();
		int var6 = var4.getScaledHeight();
		
		if (CustomSpawnAddon.loadingProgress != 0 && !this.field_73727_a.isEmpty()) {
			String progress = CustomSpawnAddon.loadingProgress + "%";
			this.mc.fontRenderer.drawStringWithShadow(progress,
													  (var5 - this.mc.fontRenderer.getStringWidth(progress)) /
															  2,
													  var6 / 2 - 4 + 32,
													  0xFFFFFF);
		}
		else {
			CustomSpawnAddon.loadingProgress = 0;
		}
	}
}
