package com.moigferdsrte.gamediscs.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class GameDiscItem extends Item {
    private final Component name;

    public GameDiscItem(Item.Properties properties, Component name) {
        super(properties.stacksTo(1));
        this.name = name;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(
            @NonNull ItemStack stack,
            @NonNull TooltipContext context,
            @NonNull TooltipDisplay display,
            Consumer<Component> tooltip,
            @NonNull TooltipFlag type
    ) {
        tooltip.accept(name);
    }
}
