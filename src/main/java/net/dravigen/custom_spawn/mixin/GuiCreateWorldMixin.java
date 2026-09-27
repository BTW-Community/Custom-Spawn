package net.dravigen.custom_spawn.mixin;

import net.dravigen.custom_spawn.config.ConfigUtils;
import net.dravigen.custom_spawn.gui.GuiButtonCustom;
import net.dravigen.custom_spawn.gui.GuiCustomSpawn;
import net.dravigen.custom_spawn.gui.GuiSeedSearching;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(GuiCreateWorld.class)
public abstract class GuiCreateWorldMixin extends GuiScreen {
	@Unique
	private static final int ID = 750;
	@Shadow
	public String generatorOptionsToUse;
	@Shadow
	private int worldTypeId;
	@Shadow
	private boolean createClicked;
	@Shadow
	private GuiTextField textboxSeed;
	@Shadow
	private String gameMode;
	@Shadow
	private int difficultyID;
	@Shadow
	private boolean lockDifficulty;
	@Shadow
	private boolean generateStructures;
	@Shadow
	private boolean isHardcore;
	@Shadow
	private boolean bonusItems;
	@Shadow
	private boolean commandsAllowed;
	@Shadow
	private String folderName;
	@Shadow
	private GuiTextField textboxWorldName;
	
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
	
	@Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
	private void onActionPerformed(GuiButton button, CallbackInfo ci) {
		if (button.id == ID) {
			this.mc.displayGuiScreen(new GuiCustomSpawn(this));
		}
		else if (button.id == 0 && !ConfigUtils.getBoolean(ConfigUtils.disableCustomSpawnKey)) {
			if (this.createClicked) {
				return;
			}
			this.createClicked = true;
			
			long seed = new Random().nextLong();
			String var4 = this.textboxSeed.getText();
			if (!MathHelper.stringNullOrLengthZero(var4)) {
				try {
					long var5 = Long.parseLong(var4);
					if (var5 != 0L) {
						seed = var5;
					}
				} catch (NumberFormatException var7) {
					seed = var4.hashCode();
				}
			}
			
			GuiSeedSearching searching = new GuiSeedSearching(seed,
															  gameMode,
															  difficultyID,
															  lockDifficulty,
															  generateStructures,
															  isHardcore,
															  worldTypeId,
															  generatorOptionsToUse,
															  folderName,
															  textboxWorldName.getText().trim(),
															  bonusItems,
															  commandsAllowed);
			this.mc.displayGuiScreen(searching);
			
			ci.cancel();
		}
	}
}
