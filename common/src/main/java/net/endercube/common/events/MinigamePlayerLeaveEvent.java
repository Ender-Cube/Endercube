package net.endercube.common.events;

import net.endercube.common.events.eventTypes.PlayerMinigameEvent;
import net.endercube.common.players.EndercubePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player leaves this minigame
 */
public class MinigamePlayerLeaveEvent implements PlayerMinigameEvent {
    private final String minigame;
    private final EndercubePlayer player;

    public MinigamePlayerLeaveEvent(String minigame, EndercubePlayer player) {
        this.minigame = minigame;
        this.player = player;

        player.setCurrentMinigame("none");
    }

    @Override
    public @NotNull String getMinigame() {
        return minigame;
    }

    public @NotNull EndercubePlayer getPlayer() {
        return player;
    }
}
