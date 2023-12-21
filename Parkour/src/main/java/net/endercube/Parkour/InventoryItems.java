package net.endercube.Parkour;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;

public class InventoryItems {
    public static final ItemStack CHECKPOINT_ITEM = ItemStack.builder(Material.ARROW)
            .displayName(Component.text("Teleport to current checkpoint"))
            .build()
            .withTag(Tag.String("action"), "checkpoint");
    public static final ItemStack RESTART_ITEM = ItemStack.builder(Material.BLAZE_POWDER)
            .displayName(Component.text("Restart the whole map!"))
            .build()
            .withTag(Tag.String("action"), "restart");
    public static final ItemStack HUB_ITEM = ItemStack.builder(Material.RED_BED)
            .displayName(Component.text("Teleport back to hub"))
            .build()
            .withTag(Tag.String("action"), "hub");
    public static final ItemStack VISIBILITY_ITEM_INVISIBLE = ItemStack.builder(Material.ENDER_PEARL)
            .displayName(Component.text("Click to make players ghosts"))
            .build()
            .withTag(Tag.String("action"), "showPlayers");
    public static final ItemStack VISIBILITY_ITEM_VISIBLE = ItemStack.builder(Material.ENDER_EYE)
            .displayName(Component.text("Click to hide players"))
            .build()
            .withTag(Tag.String("action"), "hidePlayers");
}
