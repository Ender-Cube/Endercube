package net.endercube.parkour.inventories;

import net.endercube.gamelib.events.MinigamePlayerJoinEvent;
import net.endercube.global.EndercubePlayer;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.endercube.gamelib.EndercubeMinigame.logger;
import static net.endercube.parkour.ParkourMinigame.parkourMinigame;

public class ParkourMapInventory {
    private final Inventory inventory;
    private final boolean hubButton;

    /**
     * The slots that maps are allowed to be put in
     */
    private final int[] mapSlots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

    public ParkourMapInventory(boolean hubButton) {
        this.hubButton = hubButton;

        Inventory inventory = new Inventory(InventoryType.CHEST_5_ROW, "Select a map");

        // Create an ItemStack the size of the Inventory and fill it with black stained glass panes
        ItemStack[] itemStacks = new ItemStack[inventory.getSize()];
        Arrays.fill(itemStacks, ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE).build());

        // Add the top buttons
        itemStacks[3] = ItemStack.of(Material.GREEN_CONCRETE)
                .with(DataComponents.CUSTOM_NAME,
                        Component.text("Easy Maps")
                                .color(NamedTextColor.GREEN)
                );

        itemStacks[4] = ItemStack.of(Material.ORANGE_CONCRETE)
                .with(DataComponents.CUSTOM_NAME,
                        Component.text("Medium Maps")
                                .color(NamedTextColor.GREEN)
                );

        itemStacks[5] = ItemStack.of(Material.RED_CONCRETE)
                .with(DataComponents.CUSTOM_NAME,
                        Component.text("Hard Maps")
                                .color(NamedTextColor.GREEN)
                );

        // Add the hub button if it's configured
        if (hubButton) {
            itemStacks[44] = ItemStack.of(Material.RED_BED)
                    .with(DataComponents.CUSTOM_NAME,
                            Component.text("Back to hub")
                                    .color(NamedTextColor.RED)
                    );
        }


        // put the contents of the itemStack in to the inventory
        inventory.copyContents(itemStacks);

        // Add the handler for all the click events of this inventory
        MinecraftServer.getGlobalEventHandler().addListener(InventoryPreClickEvent.class, event -> {
            if (event.getInventory() == inventory) {
                this.inventoryHandler(event);
            }
        });

        this.inventory = inventory;

        // Set the state to easy by default
        setState("easy", null);
    }

    /**
     * Event handler for InventoryPreClickEvent
     */
    private void inventoryHandler(InventoryPreClickEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        // Stop items from being moved around
        event.setCancelled(true);

        if (event.getClickedItem() == ItemStack.AIR) return;

        // Deal with sending a player to a map
        if (Arrays.stream(mapSlots).anyMatch(i -> i == event.getSlot())) {
            String map = inventory.getItemStack(event.getSlot()).getTag(Tag.String("map"));
            sendToMap(player, map);
            player.sendMessage(parkourMinigame.getChatPrefix().append(Component.text("Sending you to " + map)));
        }

        switch (event.getSlot()) {
            case 3 -> setState("easy", player);
            case 4 -> setState("medium", player);
            case 5 -> setState("hard", player);
            case 44 -> {
                if (hubButton) {
                    player.gotoHub();
                }
            }
        }
    }

    private ItemStack getMapItem(String difficulty, int i) {

        ArrayList<ItemStack> pre_maps = new ArrayList<>();

        // Gather all data from instances and put it in to an item
        parkourMinigame.getInstances().forEach((mapInstance) -> {
            if (!Objects.equals(mapInstance.getTag(Tag.String("difficulty")), difficulty)) {
                return;
            }
            String mapName = mapInstance.getTag(Tag.String("name"));
            int mapOrder = mapInstance.getTag(Tag.Integer("order"));

            Material UIMaterial = Material.fromKey(mapInstance.getTag(Tag.String("UI_material")));
            if (UIMaterial == null) {
                logger.error("The material for " + mapName + " is incorrect");
                return;
            }

            pre_maps.add(
                    ItemStack.of(UIMaterial)
                            .with(DataComponents.CUSTOM_NAME, MiniMessage.miniMessage().deserialize(mapInstance.getTag(Tag.String("UI_name"))))
                            .withTag(Tag.String("map"), mapName)
                            .withTag(Tag.Integer("order"), mapOrder)
            );
        });

        // Order maps correctly
        List<ItemStack> maps = pre_maps.stream().sorted(Comparator.comparing((ItemStack item) -> item.getTag(Tag.Integer("order")))).toList();


        // Return the ItemStack if i is in bounds, else return AIR
        if (i < maps.size()) {
            return maps.get(i);
        } else {
            return ItemStack.AIR;
        }
    }

    private void setGlowing(int slot, boolean state) {
        if (state) {
            inventory.setItemStack(
                    slot,
                    inventory.getItemStack(slot).with(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
            );
        } else {
            inventory.setItemStack(
                    slot,
                    inventory.getItemStack(slot).with(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false)
            );
        }

    }

    private void setState(String state, @Nullable Player player) {


        switch (state) {
            case "easy" -> {
                // Set the buttons to glow appropriately
                setGlowing(3, true);
                setGlowing(4, false);
                setGlowing(5, false);

                // Add the maps to the slots they have a space in
                int mapI = 0;
                for (int slot : mapSlots) {
                    inventory.setItemStack(slot, getMapItem("easy", mapI));
                    mapI++;
                }
            }
            case "medium" -> {
                // Set the buttons to glow appropriately
                setGlowing(3, false);
                setGlowing(4, true);
                setGlowing(5, false);

                // Add the maps to the slots they have a space in
                int mapI = 0;
                for (int slot : mapSlots) {
                    inventory.setItemStack(slot, getMapItem("medium", mapI));
                    mapI++;
                }
            }
            case "hard" -> {
                // Set the buttons to glow appropriately
                setGlowing(3, false);
                setGlowing(4, false);
                setGlowing(5, true);

                // Add the maps to the slots they have a space in
                int mapI = 0;
                for (int slot : mapSlots) {
                    inventory.setItemStack(slot, getMapItem("hard", mapI));
                    mapI++;
                }
            }
            default -> logger.error("State: " + state + " is not allowed in MapInventory#setState()");
        }

        if (player != null) {
            player.playSound(Sound.sound(
                    SoundEvent.UI_BUTTON_CLICK,
                    Sound.Source.PLAYER,
                    1f,
                    1f)
            );
        }

    }

    private void sendToMap(EndercubePlayer player, String mapName) {
        player.closeInventory();
        player.playSound(Sound.sound(
                SoundEvent.BLOCK_NOTE_BLOCK_PLING,
                Sound.Source.PLAYER,
                1f,
                1f)
        );

        // Call the event to send our player to parkour
        MinecraftServer.getGlobalEventHandler().call(new MinigamePlayerJoinEvent("parkour", player, mapName));
    }

    public static Inventory getInventory(boolean hubButton) {
        return new ParkourMapInventory(hubButton).inventory;
    }
}