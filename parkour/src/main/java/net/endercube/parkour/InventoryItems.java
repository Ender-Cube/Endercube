package net.endercube.parkour;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
    public static final ItemStack VISIBILITY_ITEM_VISIBLE = ItemStack.builder(Material.ENDER_EYE)
            .displayName(Component.text("Click to hide players"))
            .build()
            .withTag(Tag.String("action"), "hidePlayers");
    public static final ItemStack VISIBILITY_ITEM_INVISIBLE = ItemStack.builder(Material.ENDER_PEARL)
            .displayName(Component.text("Click to show players"))
            .build()
            .withTag(Tag.String("action"), "showPlayers");
    public static final ItemStack GRIND_MODE_HUB = ItemStack.builder(Material.YELLOW_BED)
            .displayName(Component.text("Click to toggle grind mode"))
            .lore(
                    Component.text("→ Go To Hub").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD),
                    Component.text("  Open map menu").color(NamedTextColor.GRAY),
                    Component.text("  Restart map").color(NamedTextColor.GRAY)
            )
            .build()
            .withTag(Tag.String("action"), "set_grindMode_menu");

    public static final ItemStack GRIND_MODE_MENU = ItemStack.builder(Material.PAINTING)
            .displayName(Component.text("Click to toggle grind mode"))
            .lore(
                    Component.text("  Go To Hub").color(NamedTextColor.GRAY),
                    Component.text("→ Open map menu").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD),
                    Component.text("  Restart map").color(NamedTextColor.GRAY)
            )
            .build()
            .withTag(Tag.String("action"), "set_grindMode_restart");

    public static final ItemStack GRIND_MODE_RESTART = ItemStack.builder(Material.BLAZE_POWDER)
            .displayName(Component.text("Click to toggle grind mode"))
            .lore(
                    Component.text("  Go To Hub").color(NamedTextColor.GRAY),
                    Component.text("  Open map menu").color(NamedTextColor.GRAY),
                    Component.text("→ Restart map").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD)
            )
            .build()
            .withTag(Tag.String("action"), "set_grindMode_hub");
}
