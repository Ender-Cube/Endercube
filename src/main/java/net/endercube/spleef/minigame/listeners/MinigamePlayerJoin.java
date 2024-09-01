package net.endercube.spleef.minigame.listeners;

import net.endercube.gamelib.events.MinigamePlayerJoinEvent;
import net.endercube.gamelib.utils.ChunkLoader;
import net.endercube.global.EndercubePlayer;
import net.endercube.spleef.activeGame.SpleefActiveGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.EventListener;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

import static net.endercube.gamelib.EndercubeMinigame.logger;
import static net.endercube.spleef.minigame.SpleefMinigame.spleefMinigame;

public class MinigamePlayerJoin implements EventListener<MinigamePlayerJoinEvent> {

    private EndercubePlayer player;
    private Instance hub;
    private final TextComponent chatPrefix = spleefMinigame.getChatPrefix();
    private final int minimumPlayers = spleefMinigame.getMinimumPlayers();
    private final int maximumPlayers = spleefMinigame.getMaximumPlayers();

    @Override
    public @NotNull Class<MinigamePlayerJoinEvent> eventType() {
        return MinigamePlayerJoinEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull MinigamePlayerJoinEvent event) {
        player = event.getPlayer();
        hub = spleefMinigame.spleefHub;

        Pos spawnPos = hub.getTag(Tag.Transient("spawnPos"));

        if (spawnPos == null) {
            player.sendMessage("Cannot send you to spleef, check console");
            logger.error("No spawn position set for the spleef hub in config.");
            return Result.EXCEPTION;
        }

        logger.info("Spawning " + player.getUsername() + " at " + spawnPos);
        player.setInstance(hub, spawnPos);
        player.sendMessage(chatPrefix.append(Component.text("Welcome to the spleef hub! A game will start soon")));

        if (hub.getPlayers().size() >= maximumPlayers) {
            @Nullable final Task startingTask = hub.getTag(Tag.Transient("startingTask"));
            if (startingTask != null) {
                startingTask.cancel();
            }
            startGame();
            return Result.SUCCESS;
        }

        if (hub.getPlayers().size() >= minimumPlayers) {
            int startDelay = spleefMinigame.config.getConfig().node("startDelay").getInt();
            if (startDelay == 0) {
                logger.error("The start delay must be greater than 0 or it is not defined in config");
                MinecraftServer.stopCleanly();
            }

            hub.sendMessage(chatPrefix.append(Component.text("Minimum player count reached! Starting a game in " + startDelay + " seconds")));
            hub.setTag(Tag.Integer("secondsLeft"), startDelay);

            Task startingTask = hub.scheduler().submitTask(() -> {
                int secondsLeft = hub.getTag(Tag.Integer("secondsLeft"));

                // Stop the countdown if there are less than the minimum player count players
                if (hub.getPlayers().size() < minimumPlayers) {
                    hub.sendMessage(chatPrefix.append(Component.text("Stopping the countdown, we are no longer at the minimum player count")));
                    return TaskSchedule.stop();
                }

                // Announce every 5 seconds or every second when less than 5
                if (secondsLeft % 5 == 0 || secondsLeft <= 5) {
                    hub.sendMessage(chatPrefix
                            .append(Component.text("Starting in "))
                            .append(Component.text(secondsLeft)
                                    .color(NamedTextColor.GOLD)
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.text(" seconds")));
                }

                if (secondsLeft == 1) {
                    hub.sendMessage(chatPrefix.append(Component.text("Starting the game!")));
                    startGame();
                    return TaskSchedule.stop();
                }

                hub.setTag(Tag.Integer("secondsLeft"), secondsLeft - 1);

                return TaskSchedule.seconds(1);
            });

            hub.setTag(Tag.Transient("startingTask"), startingTask);
        }


        return Result.SUCCESS;
    }

    private void startGame() {
        // String selectedMapName = hub.getTag(Tag.String("selectedMap"));
        // TODO: implement map voting
        String selectedMapName = "spleef-1";

        InstanceContainer selectedMap = spleefMinigame.getInstances().stream()
                .filter((map) -> map.getTag(Tag.String("name")).equals(selectedMapName))
                .findFirst()
                .orElse(null);

        if (selectedMap == null) {
            logger.error("The selected map's name does not exist. Something has really gone wrong!");
            return;
        }

        // Load the map so the chunks actually get copied
        int loadRadius = selectedMap.getTag(Tag.Integer("chunkLoadRadius"));
        ChunkLoader.loadRadius(selectedMap, new Vec(0, 0, 0), loadRadius);

        // Copy and register the instance
        InstanceContainer gameMap = selectedMap.copy();
        MinecraftServer.getInstanceManager().registerInstance(gameMap);

        new SpleefActiveGame(
                gameMap,
                hub.getPlayers()
                        .stream()
                        .map((player) -> (EndercubePlayer) player)
                        .collect(Collectors.toSet())
        );

        hub.setTag(Tag.String("selectedMap"), null);
    }
}
