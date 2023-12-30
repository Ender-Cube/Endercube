package net.endercube.Parkour.listeners;

import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Parkour.ParkourMinigame;
import net.endercube.Parkour.inventories.ParkourMapInventory;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import static net.endercube.Parkour.ParkourMinigame.database;
import static net.endercube.Parkour.ParkourMinigame.parkourMinigame;

/**
 * Most of the logic for the game is in here... I should really clean this code up
 */
public class PlayerMove implements EventListener<PlayerMoveEvent> {

    private EndercubePlayer player;
    private Pos playerPosition;
    private Instance instance;
    private Pos[] checkpoints;
    private int currentCheckpoint;
    private String mapName;
    private Scheduler scheduler;
    @Override
    public @NotNull Class<PlayerMoveEvent> eventType() {
        return PlayerMoveEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerMoveEvent event) {
        player = (EndercubePlayer) event.getPlayer();
        playerPosition = player.getPosition();
        instance = player.getInstance();
        checkpoints = instance.getTag(Tag.Transient("checkpointsPosArray"));
        currentCheckpoint = player.getTag(Tag.Integer("parkour_checkpoint"));
        mapName = instance.getTag(Tag.String("name"));
        scheduler = player.scheduler();

        // See if player is below the death barrier and if so, teleport them to current checkpoint
        if (player.getPosition().y() < instance.getTag(Tag.Integer("death-y"))) {
            this.handleDeath();
            return Result.SUCCESS;
        }

        // Start timer on player move
        if (!player.getTag(Tag.Boolean("parkour_timerStarted"))) {
            startTimer();
            return Result.SUCCESS;
        }


        // Deal with completing checkpoints
        if (currentCheckpoint < checkpoints.length - 1) { // Stop IndexOutOfBounds errors
            if (player.getPosition().sameBlock(checkpoints[currentCheckpoint + 1])) { // Actually check the checkpoint
                handleCheckpoint();
                return Result.SUCCESS;
            }
        }

        // Deal with finish
        if (playerPosition.sameBlock(instance.getTag(Tag.Transient("finishPos")))) {
            if (currentCheckpoint == checkpoints.length - 1) {
                handleFinish();

            } else {
                player.sendMessage(parkourMinigame.getChatPrefix()
                        .append(Component.text("You've missed some checkpoints! Go back and grab them or use the"))
                        .append(Component.text(" blaze powder ")
                                .hoverEvent(HoverEvent.showItem(HoverEvent.ShowItem.showItem(Key.key("minecraft:blaze_powder"), 1)))
                                .decorate(TextDecoration.BOLD)
                        )
                        .append(Component.text("to restart the course"))
                );
            }
            return Result.SUCCESS;
        }


        return Result.SUCCESS;
    }

    private void handleDeath() {
        player.sendMessage(parkourMinigame.getChatPrefix().append(Component.text("Ya died :("))); // TODO: Random death message
        ParkourMinigame.sendToCheckpoint(player);
        logger.debug(player.getUsername() + " died on " + mapName);
    }

    private void startTimer() {
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
    }

    private void handleCheckpoint() {
        player.setTag(Tag.Integer("parkour_checkpoint"), currentCheckpoint + 1);
        currentCheckpoint = currentCheckpoint + 1; // Increment currentCheckpoint as that hasn't updated and we need it soon

        player.sendMessage(parkourMinigame.getChatPrefix().append(Component.text("Checkpoint " + (currentCheckpoint + 1) + " completed!")));
        logger.debug(player.getUsername() + " finished checkpoint " + (currentCheckpoint + 1) + "/" + checkpoints.length);

        player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1f, 1f));
    }

    private void handleFinish() {
        // Calculate time by taking away the tag we set at the beginning from time now
        long timeTakenMS = System.currentTimeMillis() - player.getTag(Tag.Long("parkour_startTime"));

        // Stop the action bar timer
        Task actionbarTimerTask = player.getTag(Tag.Transient("actionbarTimerTask"));
        actionbarTimerTask.cancel();

        // Add the player's time to the database
        boolean newPB = database.addTime(player, mapName, timeTakenMS);

        // Do what the player's grind mode says
        switch (database.getGrindMode(player)) {
            case HUB -> player.gotoHub();
            case RESTART -> ParkourMinigame.restartMap(player);
            case MENU -> {
                player.gotoHub();
                player.openInventory(ParkourMapInventory.getInventory(false));
            }
        }

        player.sendMessage(Component.text("")
                .append(parkourMinigame.getChatPrefix())
                .append(Component.text("Well done! You finished " + mapName + " in " + toHumanReadableTime(timeTakenMS) + ". Use "))
                .append(Component.text("/parkour leaderboard")
                        .decorate(TextDecoration.ITALIC)
                        .clickEvent(ClickEvent.suggestCommand("/parkour leaderboard "))
                        .hoverEvent(HoverEvent.showText(Component.text("/parkour leaderboard <map>"))))
                .append(Component.text(" to view other's times"))
        );

        if (newPB) {
            player.sendMessage(parkourMinigame.getChatPrefix().append(Component.text("That run was a PB! Nice job")));
        }
        logger.debug(player.getUsername() + " finished " + mapName);
    }
}
