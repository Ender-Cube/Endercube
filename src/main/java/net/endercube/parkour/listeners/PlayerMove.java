package net.endercube.parkour.listeners;

import net.endercube.global.EndercubePlayer;
import net.endercube.parkour.ParkourMinigame;
import net.endercube.parkour.events.PlayerParkourPersonalBestEvent;
import net.endercube.parkour.events.PlayerParkourWorldRecordEvent;
import net.endercube.parkour.inventories.ParkourMapInventory;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
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
import redis.clients.jedis.resps.Tuple;

import java.util.List;
import java.util.Objects;

import static net.endercube.gamelib.EndercubeMinigame.logger;
import static net.endercube.gamelib.utils.ComponentUtils.toHumanReadableTime;
import static net.endercube.parkour.ParkourMinigame.database;
import static net.endercube.parkour.ParkourMinigame.parkourMinigame;

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
                        .append(Component.text("You've missed some checkpoints ☹ Sending you back to the start"))
                );
                ParkourMinigame.restartMap(player);
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

        logger.debug(player.getUsername() + " finished " + mapName);

        // Get the first place time and ensure it is not null
        List<Tuple> leaderboard = database.getLeaderboard(mapName, 1);
        if (leaderboard == null) {
            return;
        }
        Tuple firstPlace = leaderboard.getFirst();

        // If this run is a world record then call the event
        logger.debug("First place score is: " + firstPlace.getScore() + " by " + firstPlace.getElement());
        logger.debug("time taken is: " + timeTakenMS);
        if (Objects.equals(firstPlace.getElement(), player.getUsername()) && firstPlace.getScore() >= timeTakenMS) {
            logger.debug("New WR");
            MinecraftServer.getGlobalEventHandler().call(new PlayerParkourWorldRecordEvent(player, mapName, timeTakenMS));
            return;
        }
        if (newPB) {
            logger.debug("New PB");
            MinecraftServer.getGlobalEventHandler().call(new PlayerParkourPersonalBestEvent(player, mapName, timeTakenMS));

        }
    }
}
