package net.dravigen.custom_spawn.mixin;

import net.dravigen.custom_spawn.gui.GuiButtonCustom;
import net.dravigen.custom_spawn.gui.GuiCustomSpawn;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiCreateWorld;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCreateWorld.class)
public abstract class GuiCreateWorldMixin extends GuiScreen {
	@Unique
	private static final int ID = 750;
	
	@Inject(method = "initGui", at = @At("RETURN"))
	private void addCustomButton(CallbackInfo ci) {
		this.buttonList.add(new GuiButtonCustom(ID,
												this.width / 2 - 100 + 200 + 4,
												60,
												20,
												20,
												0,
												0,
												"",
												new ResourceLocation("custom_spawn:textures/gui/texture.png")));
	}
	
	@Inject(method = "actionPerformed", at = @At("HEAD"))
	private void onActionPerformed(GuiButton button, CallbackInfo ci) {
		if (button.id == ID) {
			this.mc.displayGuiScreen(new GuiCustomSpawn(this));
		}
	}
}
