package net.dravigen.custom_spawn.mixin;

import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin {
	@ModifyConstant(method = "createSpawnPosition", constant = @Constant(intValue = 256))
	private int increasedPossibleSpawnRange(int constant) {
		return CustomSpawnAddon.range;
	}
}
