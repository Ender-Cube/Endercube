package net.endercube.gamelib.events;

import net.endercube.gamelib.events.eventTypes.PlayerMinigameEvent;
import net.endercube.global.EndercubePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player requests to join the minigame
 */
public class MinigamePlayerJoinEvent implements PlayerMinigameEvent {

    private final String minigame;
    private final EndercubePlayer player;
    private final String map;

    public MinigamePlayerJoinEvent(String minigame, EndercubePlayer player, String map) {
        this.minigame = minigame;
        this.player = player;
        this.map = map;

        player.setCurrentMinigame(minigame);
    }

    @Override
    public @NotNull String getMinigame() {
        return minigame;
    }

    public @NotNull EndercubePlayer getPlayer() {
        return player;
    }

    public @NotNull String getMap() {
        return map;
    }
}
