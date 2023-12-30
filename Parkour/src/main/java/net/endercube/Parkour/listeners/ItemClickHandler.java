package net.endercube.Parkour.listeners;

import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Parkour.InventoryItems;
import net.endercube.Parkour.ParkourMinigame;
import net.endercube.Parkour.enums.GrindMode;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;

import static net.endercube.Common.EndercubeMinigame.logger;
import static net.endercube.Parkour.ParkourMinigame.database;

public class ItemClickHandler {

    public static void handleClick(ItemStack item, EndercubePlayer player) {
        switch (item.getTag(Tag.String("action"))) {
            // TODO: restart confirm toggle option?
            case "restart" -> ParkourMinigame.restartMap(player);
            case "checkpoint" -> ParkourMinigame.sendToCheckpoint(player);
            case "hub" -> player.gotoHub();
            case "showPlayers" -> {
                player.playSound(Sound.sound(
                        SoundEvent.UI_BUTTON_CLICK,
                        Sound.Source.PLAYER,
                        1f,
                        1f)
                );

                player.getInventory().setItemStack(4, InventoryItems.VISIBILITY_ITEM_VISIBLE);
                showPlayers(player);
            }
            case "hidePlayers" -> {
                player.playSound(Sound.sound(
                        SoundEvent.UI_BUTTON_CLICK,
                        Sound.Source.PLAYER,
                        1f,
                        1f)
                );

                player.getInventory().setItemStack(4, InventoryItems.VISIBILITY_ITEM_INVISIBLE);
                hidePlayers(player);

            }
            case "set_grindMode_menu" -> {
                player.getInventory().setItemStack(17, InventoryItems.GRIND_MODE_MENU);
                database.setGrindMode(player, GrindMode.MENU);
                playPling(player);
            }
            case "set_grindMode_restart" -> {
                player.getInventory().setItemStack(17, InventoryItems.GRIND_MODE_RESTART);
                database.setGrindMode(player, GrindMode.RESTART);
                playPling(player);
            }
            case "set_grindMode_hub" -> {
                player.getInventory().setItemStack(17, InventoryItems.GRIND_MODE_HUB);
                database.setGrindMode(player, GrindMode.HUB);
                playPling(player);
            }
            default -> logger.error(player.getUsername() + " used an item with an invalid tag");

        }
    }

    private static void showPlayers(EndercubePlayer player) {
        player.updateViewerRule(playerVisible -> true);
    }

    private static void hidePlayers(EndercubePlayer player) {
        player.updateViewerRule(playerVisible -> false);
    }

    private static void playPling(EndercubePlayer player) {
        player.playSound(Sound.sound(
                SoundEvent.BLOCK_NOTE_BLOCK_PLING,
                Sound.Source.PLAYER,
                1f,
                1f
        ));
    }
}
