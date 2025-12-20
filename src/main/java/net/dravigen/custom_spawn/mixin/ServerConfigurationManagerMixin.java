package net.dravigen.custom_spawn.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.TreeSet;

import static net.dravigen.custom_spawn.CustomSpawnAddon.*;

@Mixin(ServerConfigurationManager.class)
public abstract class ServerConfigurationManagerMixin {
	
	@Inject(method = "initializeConnectionToPlayer", at = @At(value = "INVOKE", target = "Lapi/AddonHandler;serverPlayerConnectionInitialized(Lnet/minecraft/src/NetServerHandler;Lnet/minecraft/src/EntityPlayerMP;)V", shift = At.Shift.AFTER))
	private void sendFoundBiomesInSpawn(INetworkManager par1INetworkManager, EntityPlayerMP mp,
			CallbackInfo ci) {
		if (allBiomeFound.isEmpty() && wantedBiomesFound.isEmpty() && unwantedBiomesFound.isEmpty()) return;
		
		Set<String> wantedList = new TreeSet<>();
		Set<String> unwantedList = new TreeSet<>();
		
		for (BiomeGenBase unwantedBiome : unwantedBiomesInSpawn) {
			unwantedList.add((unwantedBiomesFound.contains(unwantedBiome.biomeName.replace(" ", "")) ? "§4" : "§7") + unwantedBiome.biomeName + "§f");
		}
		for (BiomeGenBase wantedBiome : wantedBiomesInSpawn) {
			wantedList.add((wantedBiomesFound.contains(wantedBiome.biomeName.replace(" ", "")) ? "§2" : "§7") + wantedBiome.biomeName + "§f");
		}
		
		sendMsg("", mp);
		sendMsg("Wanted Biomes Found: (" + wantedBiomesFound.size() + "/" + wantedBiomesInSpawn.size() + ")",
				mp,
				EnumChatFormatting.GREEN);
		sendMsg(wantedList.toString(), mp);
		
		sendMsg("", mp);
		sendMsg("Unwanted Biomes Found: (" + unwantedBiomesFound.size() + "/" + unwantedBiomesInSpawn.size() + ")",
				mp,
				EnumChatFormatting.RED);
	
		sendMsg(unwantedList.toString(), mp);
		
		sendMsg("", mp);
		sendMsg("Other Biomes Found: (" +
						allBiomeFound.size() +
						")", mp, EnumChatFormatting.AQUA);
		sendMsg(allBiomeFound.toString(), mp);
		
		allBiomeFound.clear();
		wantedBiomesFound.clear();
		unwantedBiomesFound.clear();
	}
	
	@Unique
	private void sendMsg(String msg, EntityPlayerMP mp, EnumChatFormatting color) {
		mp.sendChatToPlayer(ChatMessageComponent.createFromText(msg).setColor(color));
	}
	
	@Unique
	private void sendMsg(String msg, EntityPlayerMP mp) {
		mp.sendChatToPlayer(ChatMessageComponent.createFromText(msg));
	}
}
