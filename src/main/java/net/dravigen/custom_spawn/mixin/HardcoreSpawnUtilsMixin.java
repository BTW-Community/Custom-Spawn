package net.dravigen.custom_spawn.mixin;

import btw.util.hardcorespawn.HardcoreSpawnUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HardcoreSpawnUtils.class)
public class HardcoreSpawnUtilsMixin {
	/*
	@ModifyConstant(method = "assignNewHardcoreSpawnLocation", constant = @Constant(intValue = 20))
	private static int moreAttempts(int constant) {
		return (int) (constant * Math.pow(256d / CustomSpawnAddon.range, 2));
	}*/
}
