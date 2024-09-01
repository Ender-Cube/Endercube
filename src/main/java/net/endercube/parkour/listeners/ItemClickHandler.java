package net.endercube.parkour.listeners;

import net.endercube.global.EndercubePlayer;
import net.endercube.parkour.InventoryItems;
import net.endercube.parkour.ParkourMinigame;
import net.endercube.parkour.enums.GrindMode;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;

import static net.endercube.gamelib.EndercubeMinigame.logger;
import static net.endercube.parkour.ParkourMinigame.database;

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
                player.updateViewerRule(playerVisible -> true);
            }
            case "hidePlayers" -> {
                player.playSound(Sound.sound(
                        SoundEvent.UI_BUTTON_CLICK,
                        Sound.Source.PLAYER,
                        1f,
                        1f)
                );

                player.getInventory().setItemStack(4, InventoryItems.VISIBILITY_ITEM_INVISIBLE);
                player.updateViewerRule(playerVisible -> false);
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

    private static void playPling(EndercubePlayer player) {
        player.playSound(Sound.sound(
                SoundEvent.BLOCK_NOTE_BLOCK_PLING,
                Sound.Source.PLAYER,
                1f,
                1f
        ));
    }
}
