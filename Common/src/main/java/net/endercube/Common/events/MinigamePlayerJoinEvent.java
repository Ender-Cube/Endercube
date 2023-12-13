package net.endercube.Common.events;

import net.endercube.Common.events.eventTypes.PlayerMinigameEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.minestom.server.event.Event;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player requests to join the minigame
 */
public class MinigamePlayerJoinEvent implements PlayerMinigameEvent {

    private String minigame;
    private EndercubePlayer player;

    public MinigamePlayerJoinEvent(String minigame, EndercubePlayer player) {
        player.setCurrentMinigame(minigame);
    }

    @Override
    public @NotNull String getMinigame() {
        return minigame;
    }

    public @NotNull EndercubePlayer getPlayer() {
        return player;
    }
}
