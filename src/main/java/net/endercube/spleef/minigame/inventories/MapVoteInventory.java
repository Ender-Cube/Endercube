package net.endercube.spleef.minigame.inventories;

import net.endercube.gamelib.enums.Heads;
import net.goldenstack.window.InventoryView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.component.DataComponents;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class MapVoteInventory {
    private final Inventory inventory;

    public MapVoteInventory() {
        Inventory inventory = new Inventory(InventoryType.CHEST_5_ROW, "Vote!");
        int size = inventory.getSize();

        InventoryView mainView = InventoryView.contiguous(0, size);
        InventoryView maps = mainView.forkRange(0, size - 9);
        InventoryView actions = mainView.forkRange(size - 9, size);

        mainView.fill(inventory, slot -> ItemStack.of(Material.BLACK_STAINED_GLASS_PANE).with(DataComponents.CUSTOM_NAME, Component.empty()));
        maps.fill(inventory, slot -> ItemStack.of(Material.MAP)); // just for mockup

        // Init action bar
        actions.set(inventory, 0,
                ItemStack.of(Material.SEA_PICKLE).with(DataComponents.CUSTOM_NAME, Component.text("Pick all").color(NamedTextColor.GREEN))
        );
        actions.set(inventory, 4,
                ItemStack.of(Material.BARRIER).with(DataComponents.CUSTOM_NAME, Component.text("Close").color(NamedTextColor.RED))
        );
        actions.set(inventory, 8,
                Heads.QUESTION_MARK.getItemStack().with(DataComponents.CUSTOM_NAME, Component.text("Help!").color(NamedTextColor.GREEN))
        );

        this.inventory = inventory;
    }

    public static Inventory getInventory() {
        return new MapVoteInventory().inventory;
    }
}
