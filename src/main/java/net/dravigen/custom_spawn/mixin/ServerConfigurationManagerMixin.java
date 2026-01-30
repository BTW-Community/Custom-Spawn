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
    private void sendFoundBiomesInSpawn(INetworkManager par1INetworkManager, EntityPlayerMP mp,
                                        CallbackInfo ci) {
        if (allBiomeFound.isEmpty() && wantedBiomesFound.isEmpty() && unwantedBiomesFound.isEmpty()) return;

        List<String> uwBiomes = new ArrayList<>(CustomSpawnAddon.unwantedBiomesInSpawn);
        List<String> wBiomes = new ArrayList<>(CustomSpawnAddon.wantedBiomesInSpawn);

        if (!wBiomes.isEmpty()) {
            wBiomes.remove(0);
        }
        if (!uwBiomes.isEmpty()) {
            uwBiomes.remove(0);
        }

        ChatMessageComponent wantedFoundNum = ChatMessageComponent.createFromText(String.valueOf(wantedBiomesFound.size())).setColor(EnumChatFormatting.GREEN);
        ChatMessageComponent wantedTotalNum = ChatMessageComponent.createFromText(String.valueOf(wBiomes.size())).setColor(EnumChatFormatting.WHITE);
        ChatMessageComponent wantedLine = ChatMessageComponent.createFromTranslationWithSubstitutions("customspawn.msg.wantedFound", wantedFoundNum, wantedTotalNum);
        wantedLine.setColor(EnumChatFormatting.GREEN);

        wantedLine.appendComponent(ChatMessageComponent.createFromText(" "));
        if (wBiomes.isEmpty()) {
            ChatMessageComponent none = ChatMessageComponent.createFromTranslationKey("customspawn.biome.none");
            none.setColor(EnumChatFormatting.WHITE);
            wantedLine.appendComponent(none);
        } else {
            boolean first = true;
            for (String biomeName : wBiomes) {
                if (!first) wantedLine.appendComponent(ChatMessageComponent.createFromText(", "));
                ChatMessageComponent comp = ChatMessageComponent.createFromTranslationKey("customspawn.biome." + biomeName);
                comp.setColor(wantedBiomesFound.contains(biomeName) ? EnumChatFormatting.GREEN : EnumChatFormatting.WHITE);
                wantedLine.appendComponent(comp);
                first = false;
            }
        }
        mp.sendChatToPlayer(wantedLine);

        sendMsg("", mp);

        ChatMessageComponent unwantedFoundNum = ChatMessageComponent.createFromText(String.valueOf(unwantedBiomesFound.size())).setColor(EnumChatFormatting.RED);
        ChatMessageComponent unwantedTotalNum = ChatMessageComponent.createFromText(String.valueOf(uwBiomes.size())).setColor(EnumChatFormatting.WHITE);
        ChatMessageComponent unwantedLine = ChatMessageComponent.createFromTranslationWithSubstitutions("customspawn.msg.unwantedFound", unwantedFoundNum, unwantedTotalNum);
        unwantedLine.setColor(EnumChatFormatting.RED);

        unwantedLine.appendComponent(ChatMessageComponent.createFromText(" "));
        if (uwBiomes.isEmpty()) {
            ChatMessageComponent none = ChatMessageComponent.createFromTranslationKey("customspawn.biome.none");
            none.setColor(EnumChatFormatting.WHITE);
            unwantedLine.appendComponent(none);
        } else {
            boolean first = true;
            for (String biomeName : uwBiomes) {
                if (!first) unwantedLine.appendComponent(ChatMessageComponent.createFromText(", "));
                ChatMessageComponent comp = ChatMessageComponent.createFromTranslationKey("customspawn.biome." + biomeName);
                comp.setColor(unwantedBiomesFound.contains(biomeName) ? EnumChatFormatting.DARK_RED : EnumChatFormatting.WHITE);
                unwantedLine.appendComponent(comp);
                first = false;
            }
        }
        mp.sendChatToPlayer(unwantedLine);
        sendMsg("", mp);

        ChatMessageComponent otherFoundNum = ChatMessageComponent.createFromText(String.valueOf(allBiomeFound.size())).setColor(EnumChatFormatting.AQUA);
        ChatMessageComponent otherLine = ChatMessageComponent.createFromTranslationWithSubstitutions("customspawn.msg.otherFound", otherFoundNum);
        otherLine.setColor(EnumChatFormatting.AQUA);

        otherLine.appendComponent(ChatMessageComponent.createFromText(" "));
        if (allBiomeFound.isEmpty()) {
            ChatMessageComponent none = ChatMessageComponent.createFromTranslationKey("customspawn.biome.none");
            none.setColor(EnumChatFormatting.WHITE);
            otherLine.appendComponent(none);
        } else {
            boolean first = true;
            for (String biomeName : allBiomeFound) {
                if (!first) otherLine.appendComponent(ChatMessageComponent.createFromText(", "));
                ChatMessageComponent comp = ChatMessageComponent.createFromTranslationKey("customspawn.biome." + biomeName);
                comp.setColor(EnumChatFormatting.AQUA);
                otherLine.appendComponent(comp);
                first = false;
            }
        }
        mp.sendChatToPlayer(otherLine);

        allBiomeFound.clear();
        wantedBiomesFound.clear();
        unwantedBiomesFound.clear();
    }

    @Unique
    private void sendMsg(String msg, EntityPlayerMP mp) {
        mp.sendChatToPlayer(ChatMessageComponent.createFromText(msg));
    }
}
