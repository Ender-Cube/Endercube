package net.endercube.spleef.listeners;

import net.endercube.Common.events.MinigamePlayerJoinEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.endercube.spleef.SpleefActiveGame;
import net.endercube.spleef.SpleefMinigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventListener;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static net.endercube.Common.EndercubeMinigame.logger;

public class MinigamePlayerJoin implements EventListener<MinigamePlayerJoinEvent> {

    private EndercubePlayer player;
    private Instance hub;
    private final TextComponent chatPrefix = SpleefMinigame.spleefMinigame.getChatPrefix();
    private final int minimumPlayers = SpleefMinigame.spleefMinigame.getMinimumPlayers();
    private final int maximumPlayers = SpleefMinigame.spleefMinigame.getMaximumPlayers();

    @Override
    public @NotNull Class<MinigamePlayerJoinEvent> eventType() {
        return MinigamePlayerJoinEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull MinigamePlayerJoinEvent event) {
        player = event.getPlayer();
        hub = SpleefMinigame.spleefMinigame.spleefHub;

        Pos spawnPos = hub.getTag(Tag.Transient("spawnPos"));

        if (spawnPos == null) {
            player.sendMessage("Cannot send you to spleef, check console");
            logger.error("No spawn position set for the spleef hub in config.");
            return Result.EXCEPTION;
        }

        logger.info("Spawning " + player.getUsername() + " at " + spawnPos);
        player.setInstance(hub, spawnPos);
        player.sendMessage(chatPrefix.append(Component.text("Welcome to the spleef hub! A game will start soon")));

        if (hub.getPlayers().size() == maximumPlayers) {
            startGame();
        }


        return Result.SUCCESS;
    }

    private void startGame() {
        // String selectedMapName = hub.getTag(Tag.String("selectedMap"));
        String selectedMapName = "spleef-1";
        
        Instance selectedMap = SpleefMinigame.spleefMinigame.getInstances().stream()
                .filter((map) -> map.getTag(Tag.String("name")).equals(selectedMapName))
                .findFirst()
                .orElse(null);

        new SpleefActiveGame(
                selectedMap,
                hub.getPlayers()
                        .stream()
                        .map((player) -> (EndercubePlayer) player)
                        .collect(Collectors.toSet())
        );

        hub.setTag(Tag.String("selectedMap"), null);
    }
}
