package net.endercube.spleef.minigame.listeners;

import net.endercube.Common.events.MinigamePlayerJoinEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Common.utils.ChunkLoader;
import net.endercube.spleef.activeGame.SpleefActiveGame;
import net.endercube.spleef.minigame.SpleefMinigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.EventListener;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
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

        // TODO: Do proper join checking
        if (hub.getPlayers().size() == maximumPlayers) {
            startGame();
        }


        return Result.SUCCESS;
    }

    private void startGame() {
        // String selectedMapName = hub.getTag(Tag.String("selectedMap"));
        // TODO: implement map voting
        String selectedMapName = "spleef-1";

        InstanceContainer selectedMap = SpleefMinigame.spleefMinigame.getInstances().stream()
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
