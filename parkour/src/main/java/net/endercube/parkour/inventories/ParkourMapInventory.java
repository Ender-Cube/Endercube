package net.endercube.parkour.inventories;

import net.endercube.common.events.MinigamePlayerJoinEvent;
import net.endercube.common.players.EndercubePlayer;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.*;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.endercube.common.EndercubeMinigame.logger;
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
                .withDisplayName(
                        Component.text("Easy Maps")
                                .color(NamedTextColor.GREEN)
                );

        itemStacks[4] = ItemStack.of(Material.ORANGE_CONCRETE)
                .withDisplayName(
                        Component.text("Medium Maps")
                                .color(NamedTextColor.GREEN)
                );

        itemStacks[5] = ItemStack.of(Material.RED_CONCRETE)
                .withDisplayName(
                        Component.text("Hard Maps")
                                .color(NamedTextColor.GREEN)
                );

        // Add the hub button if it's configured
        if (hubButton) {
            itemStacks[44] = ItemStack.of(Material.RED_BED)
                    .withDisplayName(
                            Component.text("Back to hub")
                                    .color(NamedTextColor.RED)
                    );
        }


        // put the contents of the itemStack in to the inventory
        inventory.copyContents(itemStacks);

        // Add the inventory condition for all the events of this inventory
        inventory.addInventoryCondition(this::inventoryCondition);

        this.inventory = inventory;

        // Set the state to easy by default
        setState("easy", null);
    }

    private void inventoryCondition(Player badPlayer, int slot, ClickType clickType, InventoryConditionResult inventoryConditionResult) {
        EndercubePlayer player = (EndercubePlayer) badPlayer;
        // Stop items from being moved around
        inventoryConditionResult.setCancel(true);

        if (inventoryConditionResult.getClickedItem() == ItemStack.AIR) return;

        // Deal with sending a player to a map
        if (Arrays.stream(mapSlots).anyMatch(i -> i == slot)) {
            String map = inventory.getItemStack(slot).getTag(Tag.String("map"));
            sendToMap(player, map);
            player.sendMessage(parkourMinigame.getChatPrefix().append(Component.text("Sending you to " + map)));
        }

        switch (slot) {
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
            pre_maps.add(ItemStack
                    .of(Objects.requireNonNull(Material.fromNamespaceId(mapInstance.getTag(Tag.String("UI_material")))))
                    .withDisplayName(MiniMessage.miniMessage().deserialize(mapInstance.getTag(Tag.String("UI_name"))))
                    .withTag(Tag.String("map"), mapInstance.getTag(Tag.String("name")))
                    .withTag(Tag.Integer("order"), mapInstance.getTag(Tag.Integer("order")))
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
            inventory.setItemStack(slot,
                    inventory
                            .getItemStack(slot)
                            .withMeta(builder -> builder
                                    .enchantment(Enchantment.KNOCKBACK, (short) 1)
                                    .hideFlag(ItemHideFlag.HIDE_ENCHANTS)
                            )
            );
        } else {
            inventory.setItemStack(slot,
                    inventory
                            .getItemStack(slot)
                            .withMeta(ItemMeta.Builder::clearEnchantment)
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