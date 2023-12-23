package net.endercube.Parkour.listeners;

import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Parkour.InventoryItems;
import net.endercube.Parkour.ParkourMinigame;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import static net.endercube.Common.EndercubeMinigame.logger;

public class PlayerUseItem implements EventListener<PlayerUseItemEvent> {
    @Override
    public @NotNull Class<PlayerUseItemEvent> eventType() {
        return PlayerUseItemEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerUseItemEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();

        switch (event.getItemStack().getTag(Tag.String("action"))) {
            // TODO: restart confirm toggle option?
            case "restart" -> {
                player.teleport(player.getInstance().getTag(Tag.Transient("spawnPos")));
                player.setTag(Tag.Integer("parkour_checkpoint"), -1);
                player.setTag(Tag.Long("parkour_startTime"), System.currentTimeMillis()); // Reset timer
            }
            case "checkpoint" -> {
                ParkourMinigame.sendToCheckpoint(player);
            }
            case "hub" -> {
                player.gotoHub();
            }
            case "showPlayers" -> {
                player.playSound(Sound.sound(
                        SoundEvent.UI_BUTTON_CLICK,
                        Sound.Source.PLAYER,
                        1f,
                        1f)
                );

                player.getInventory().setItemStack(4, InventoryItems.VISIBILITY_ITEM_VISIBLE);
                this.showPlayers(player);

            }
            case "hidePlayers" -> {
                player.playSound(Sound.sound(
                        SoundEvent.UI_BUTTON_CLICK,
                        Sound.Source.PLAYER,
                        1f,
                        1f)
                );

                player.getInventory().setItemStack(4, InventoryItems.VISIBILITY_ITEM_INVISIBLE);
                this.hidePlayers(player);

            }
            default -> logger.error("A player used an item with an invalid tag");

        }

        return Result.SUCCESS;
    }

    private void showPlayers(EndercubePlayer player) {
        player.updateViewerRule(playerVisible -> true);
    }

    private void hidePlayers(EndercubePlayer player) {
        player.updateViewerRule(playerVisible -> false);
    }
}

