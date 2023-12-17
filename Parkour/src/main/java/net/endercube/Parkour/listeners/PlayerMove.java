package net.endercube.Parkour.listeners;

import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Parkour.ParkourMinigame;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import static net.endercube.Common.EndercubeMinigame.logger;

public class PlayerMove implements EventListener<PlayerMoveEvent> {
    @Override
    public @NotNull Class<PlayerMoveEvent> eventType() {
        return PlayerMoveEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerMoveEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        Pos playerPosition = player.getPosition();
        Instance instance = player.getInstance();
        Pos[] checkpoints = instance.getTag(Tag.Transient("checkpointsPosArray"));
        int currentCheckpoint = player.getTag(Tag.Integer("parkour_checkpoint"));
        String mapName = instance.getTag(Tag.String("name"));

        // See if player is below the death barrier and if so, teleport them to current checkpoint
        if (player.getPosition().y() < instance.getTag(Tag.Integer("death-y"))) {
            player.sendMessage("Ya died :("); // TODO: Random death message
            ParkourMinigame.sendToCheckpoint(player);
            logger.debug(player.getUsername() + " died on " + mapName);
            return Result.SUCCESS;
        }


        // Deal with completing checkpoints
        if (currentCheckpoint < checkpoints.length - 1) { // Stop IndexOutOfBounds errors
            if (player.getPosition().sameBlock(checkpoints[currentCheckpoint + 1])) { // Actually check the checkpoint

                player.setTag(Tag.Integer("parkour_checkpoint"), currentCheckpoint + 1);
                currentCheckpoint = currentCheckpoint + 1; // Increment currentCheckpoint as that hasn't updated and we need it soon

                player.sendMessage("Checkpoint " + (currentCheckpoint + 1) + " completed!");
                logger.debug(player.getUsername() + " finished checkpoint " + (currentCheckpoint + 1) + "/" + checkpoints.length);

                player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1f, 1f));
                return Result.SUCCESS;
            }
        }

        // Deal with finish
        if (playerPosition.sameBlock(instance.getTag(Tag.Transient("finishPos")))) {
            if (currentCheckpoint == checkpoints.length - 1) {
                player.gotoHub();
                player.sendMessage("Well done! You finished " + mapName + " in // TODO: timer seconds");
                logger.debug(player.getUsername() + " finished " + mapName);
                return Result.SUCCESS;
            }
        }


        return Result.SUCCESS;
    }
}
