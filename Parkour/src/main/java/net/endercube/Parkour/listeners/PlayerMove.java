package net.endercube.Parkour.listeners;

import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Parkour.ParkourMinigame;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import static net.endercube.Common.EndercubeMinigame.logger;
import static net.endercube.Common.utils.ComponentUtils.toHumanReadableTime;

/**
 * Most of the logic for the game is in here... I should really clean this code up
 */
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
        Scheduler scheduler = player.scheduler();

        // See if player is below the death barrier and if so, teleport them to current checkpoint
        if (player.getPosition().y() < instance.getTag(Tag.Integer("death-y"))) {
            player.sendMessage("Ya died :("); // TODO: Random death message
            ParkourMinigame.sendToCheckpoint(player);
            logger.debug(player.getUsername() + " died on " + mapName);
            return Result.SUCCESS;
        }

        // Start timer on player move
        if (!player.getTag(Tag.Boolean("parkour_timerStarted"))) {

            // Toggle the timer started and set the start time
            player.setTag(Tag.Boolean("parkour_timerStarted"), true);
            player.setTag(Tag.Long("parkour_startTime"), System.currentTimeMillis());

            // Start the action bar timer task
            player.setTag(Tag.Transient("actionbarTimerTask"),
                    scheduler.submitTask(() -> {
                        long timeTaken = System.currentTimeMillis() - player.getTag(Tag.Long("parkour_startTime"));
                        player.sendActionBar(Component.text(toHumanReadableTime(timeTaken), NamedTextColor.WHITE));
                        return TaskSchedule.millis(15);
                    })
            );


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
                // Calculate time by taking away the tag we set at the beginning from time now
                long timeTakenMS = System.currentTimeMillis() - player.getTag(Tag.Long("parkour_startTime"));

                // Stop the action bar timer
                Task actionbarTimerTask = player.getTag(Tag.Transient("actionbarTimerTask"));
                actionbarTimerTask.cancel();

                player.gotoHub();
                player.sendMessage("Well done! You finished " + mapName + " in " + toHumanReadableTime(timeTakenMS));
                logger.debug(player.getUsername() + " finished " + mapName);

                return Result.SUCCESS;
            }
        }


        return Result.SUCCESS;
    }
}
