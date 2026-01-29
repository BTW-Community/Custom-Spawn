package net.dravigen.custom_spawn.mixin;

import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
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
		List<String> uwBiomes = new ArrayList<>(CustomSpawnAddon.unwantedBiomesInSpawn);
		List<String> wBiomes = new ArrayList<>(CustomSpawnAddon.wantedBiomesInSpawn);
		
		if (!wBiomes.isEmpty()) {
			wBiomes.remove(0);
		}
		if (!uwBiomes.isEmpty()) {
			uwBiomes.remove(0);
		}
		
		for (String biomeName : uwBiomes) {
			unwantedList.add((unwantedBiomesFound.contains(biomeName) ? "§4" : "§7") + biomeName + "§f");
		}
		for (String biomeName : wBiomes) {
			wantedList.add((wantedBiomesFound.contains(biomeName) ? "§2" : "§7") + biomeName + "§f");
		}
		
		sendMsg("", mp);
		sendMsg("Wanted Biomes Found: (" + wantedBiomesFound.size() + "/" + wBiomes.size() + ")",
				mp,
				EnumChatFormatting.GREEN);
		sendMsg(wantedList.toString(), mp);
		
		sendMsg("", mp);
		sendMsg("Unwanted Biomes Found: (" + unwantedBiomesFound.size() + "/" + uwBiomes.size() + ")",
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
