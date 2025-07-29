package com.oheers.fish.items.configs;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public class ItemModelConfig extends ItemConfig<@NotNull String> {

    public ItemModelConfig(@NotNull Section section) {
        super(section);
    }

    @Override
    @NotNull
    public String getConfiguredValue() {
        return section.getString("item.item-model");
    }

    @Override
    protected BiConsumer<ItemStack, @NotNull String> applyToItem(@Nullable Map<String, ?> replacements) {
        return (item, value) -> {
            if (value.contains(":")) {
                String[] splitValue = value.split(":");

                String namespace = splitValue[0];
                String key = splitValue[1];

                item.editMeta(meta -> meta.setItemModel(new NamespacedKey(namespace, key)));
            }
        };
    }

}
