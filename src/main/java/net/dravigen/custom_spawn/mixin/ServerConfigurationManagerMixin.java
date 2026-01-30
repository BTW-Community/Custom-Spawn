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

import static net.dravigen.custom_spawn.CustomSpawnAddon.*;

@Mixin(ServerConfigurationManager.class)
public abstract class ServerConfigurationManagerMixin {
	
	@Inject(method = "initializeConnectionToPlayer", at = @At(value = "INVOKE", target = "Lapi/AddonHandler;serverPlayerConnectionInitialized(Lnet/minecraft/src/NetServerHandler;Lnet/minecraft/src/EntityPlayerMP;)V", shift = At.Shift.AFTER))
	private void sendFoundBiomesInSpawn(INetworkManager par1INetworkManager, EntityPlayerMP mp, CallbackInfo ci) {
		if (allBiomeFound.isEmpty() && wantedBiomesFound.isEmpty() && unwantedBiomesFound.isEmpty()) return;
		
		List<String> uwBiomes = new ArrayList<>(CustomSpawnAddon.unwantedBiomesInSpawn);
		List<String> wBiomes = new ArrayList<>(CustomSpawnAddon.wantedBiomesInSpawn);
		
		if (!wBiomes.isEmpty()) {
			wBiomes.remove(0);
		}
		if (!uwBiomes.isEmpty()) {
			uwBiomes.remove(0);
		}
		
		ChatMessageComponent leftBracket = ChatMessageComponent.createFromText("§f[");
		ChatMessageComponent rightBracket = ChatMessageComponent.createFromText("§f]");
		ChatMessageComponent comma = ChatMessageComponent.createFromText("§f, ");
		
		ChatMessageComponent wantedFoundNum = ChatMessageComponent.createFromText(String.valueOf(wantedBiomesFound.size()))
				.setColor(EnumChatFormatting.GREEN);
		ChatMessageComponent wantedTotalNum = ChatMessageComponent.createFromText(String.valueOf(wBiomes.size()))
				.setColor(EnumChatFormatting.WHITE);
		
		mp.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("customspawn.msg.wantedFound",
																						wantedFoundNum,
																						wantedTotalNum)
									.setColor(EnumChatFormatting.GREEN));
		
		ChatMessageComponent wantedBiomeLine = new ChatMessageComponent(leftBracket);
		if (wBiomes.isEmpty()) {
			ChatMessageComponent none = ChatMessageComponent.createFromTranslationKey("customspawn.biome.none");
			none.setColor(EnumChatFormatting.WHITE);
			wantedBiomeLine.appendComponent(none);
		}
		else {
			boolean first = true;
			for (String biomeName : wBiomes) {
				if (!first) wantedBiomeLine.appendComponent(comma);
				ChatMessageComponent comp = ChatMessageComponent.createFromTranslationKey("customspawn.biome." +
																								  biomeName);
				comp.setColor(wantedBiomesFound.contains(biomeName)
							  ? EnumChatFormatting.GREEN
							  : EnumChatFormatting.WHITE);
				wantedBiomeLine.appendComponent(comp);
				first = false;
			}
		}
		wantedBiomeLine.appendComponent(rightBracket);
		mp.sendChatToPlayer(wantedBiomeLine);
		
		sendMsg("", mp);
		
		ChatMessageComponent unwantedFoundNum = ChatMessageComponent.createFromText(String.valueOf(unwantedBiomesFound.size()))
				.setColor(EnumChatFormatting.RED);
		ChatMessageComponent unwantedTotalNum = ChatMessageComponent.createFromText(String.valueOf(uwBiomes.size()))
				.setColor(EnumChatFormatting.WHITE);
		
		mp.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("customspawn.msg.unwantedFound",
																						unwantedFoundNum,
																						unwantedTotalNum)
									.setColor(EnumChatFormatting.RED));
		
		ChatMessageComponent unwantedBiomeLine = new ChatMessageComponent(leftBracket);
		
		if (uwBiomes.isEmpty()) {
			ChatMessageComponent none = ChatMessageComponent.createFromTranslationKey("customspawn.biome.none");
			none.setColor(EnumChatFormatting.WHITE);
			unwantedBiomeLine.appendComponent(none);
		}
		else {
			boolean first = true;
			for (String biomeName : uwBiomes) {
				if (!first) unwantedBiomeLine.appendComponent(comma);
				ChatMessageComponent comp = ChatMessageComponent.createFromTranslationKey("customspawn.biome." +
																								  biomeName);
				comp.setColor(unwantedBiomesFound.contains(biomeName)
							  ? EnumChatFormatting.DARK_RED
							  : EnumChatFormatting.WHITE);
				unwantedBiomeLine.appendComponent(comp);
				first = false;
			}
		}
		unwantedBiomeLine.appendComponent(rightBracket);
		mp.sendChatToPlayer(unwantedBiomeLine);
		sendMsg("", mp);
		
		ChatMessageComponent otherFoundNum = ChatMessageComponent.createFromText(String.valueOf(allBiomeFound.size()))
				.setColor(EnumChatFormatting.AQUA);
		
		mp.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("customspawn.msg.otherFound",
																						otherFoundNum)
									.setColor(EnumChatFormatting.AQUA));
		ChatMessageComponent otherBiomesLine = new ChatMessageComponent(leftBracket);
		
		if (allBiomeFound.isEmpty()) {
			ChatMessageComponent none = ChatMessageComponent.createFromTranslationKey("customspawn.biome.none");
			none.setColor(EnumChatFormatting.WHITE);
			otherBiomesLine.appendComponent(none);
		}
		else {
			boolean first = true;
			for (String biomeName : allBiomeFound) {
				if (!first) otherBiomesLine.appendComponent(comma);
				ChatMessageComponent comp = ChatMessageComponent.createFromTranslationKey("customspawn.biome." +
																								  biomeName);
				otherBiomesLine.appendComponent(comp);
				first = false;
			}
		}
		
		otherBiomesLine.appendComponent(rightBracket);
		mp.sendChatToPlayer(otherBiomesLine);
		
		allBiomeFound.clear();
		wantedBiomesFound.clear();
		unwantedBiomesFound.clear();
	}
	
	@Unique
	private void sendMsg(String msg, EntityPlayerMP mp) {
		mp.sendChatToPlayer(ChatMessageComponent.createFromText(msg));
	}
}
