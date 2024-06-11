package net.endercube.parkour;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;

import java.util.List;

public class InventoryItems {
    public static final ItemStack CHECKPOINT_ITEM = ItemStack.of(Material.ARROW)
            .with(ItemComponent.CUSTOM_NAME, Component.text("Teleport to current checkpoint"))
            .withTag(Tag.String("action"), "checkpoint");
    
    public static final ItemStack RESTART_ITEM = ItemStack.of(Material.BLAZE_POWDER)
            .with(ItemComponent.CUSTOM_NAME, Component.text("Restart the whole map!"))
            .withTag(Tag.String("action"), "restart");

    public static final ItemStack HUB_ITEM = ItemStack.of(Material.RED_BED)
            .with(ItemComponent.CUSTOM_NAME, Component.text("Teleport back to hub"))
            .withTag(Tag.String("action"), "hub");

    public static final ItemStack VISIBILITY_ITEM_VISIBLE = ItemStack.of(Material.ENDER_EYE)
            .with(ItemComponent.CUSTOM_NAME, Component.text("Click to hide players"))
            .withTag(Tag.String("action"), "hidePlayers");

    public static final ItemStack VISIBILITY_ITEM_INVISIBLE = ItemStack.of(Material.ENDER_PEARL)
            .with(ItemComponent.CUSTOM_NAME, (Component.text("Click to show players")))
            .withTag(Tag.String("action"), "showPlayers");

    public static final ItemStack GRIND_MODE_HUB = ItemStack.of(Material.YELLOW_BED)
            .with(ItemComponent.CUSTOM_NAME, Component.text("Click to toggle grind mode"))
            .with(ItemComponent.LORE,
                    List.of(
                            Component.text("→ Go To Hub").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD).asComponent(),
                            Component.text("  Open map menu").color(NamedTextColor.GRAY).asComponent(),
                            Component.text("  Restart map").color(NamedTextColor.GRAY).asComponent()
                    )
            )
            .withTag(Tag.String("action"), "set_grindMode_menu");

    public static final ItemStack GRIND_MODE_MENU = ItemStack.of(Material.PAINTING)
            .with(ItemComponent.CUSTOM_NAME, Component.text("Click to toggle grind mode"))
            .with(ItemComponent.LORE,
                    List.of(
                            Component.text("  Go To Hub").color(NamedTextColor.GRAY),
                            Component.text("→ Open map menu").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD),
                            Component.text("  Restart map").color(NamedTextColor.GRAY)
                    )
            )
            .withTag(Tag.String("action"), "set_grindMode_restart");

    public static final ItemStack GRIND_MODE_RESTART = ItemStack.of(Material.BLAZE_POWDER)
            .with(ItemComponent.CUSTOM_NAME, Component.text("Click to toggle grind mode"))
            .with(ItemComponent.LORE,
                    List.of(
                            Component.text("  Go To Hub").color(NamedTextColor.GRAY),
                            Component.text("  Open map menu").color(NamedTextColor.GRAY),
                            Component.text("→ Restart map").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD)
                    )
            )
            .withTag(Tag.String("action"), "set_grindMode_hub");
}
