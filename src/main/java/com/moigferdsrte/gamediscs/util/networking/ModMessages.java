package com.moigferdsrte.gamediscs.util.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import com.moigferdsrte.gamediscs.item.custom.GamingConsoleItem;
import com.moigferdsrte.gamediscs.util.networking.payload.SetBestScoreC2SPayload;

public final class ModMessages {
    private ModMessages() {
    }

    public static void registerC2SPackets() {
        PayloadTypeRegistry.serverboundPlay().register(SetBestScoreC2SPayload.TYPE, SetBestScoreC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetBestScoreC2SPayload.TYPE,
                (payload, context) -> {
                    ItemStack stack = context.player().getMainHandItem();
                    if (!(stack.getItem() instanceof GamingConsoleItem)) {
                        stack = context.player().getItemInHand(InteractionHand.OFF_HAND);
                    }
                    if (stack.getItem() instanceof GamingConsoleItem console) {
                        console.setBestScore(stack, payload.game(), payload.score(), context.player());
                    }
        });
    }
}
