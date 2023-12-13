package net.endercube.Common.events;

import net.endercube.Common.events.eventTypes.PlayerMinigameEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.minestom.server.event.Event;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player leaves this minigame
 */
public class MinigamePlayerLeaveEvent implements PlayerMinigameEvent {
    private String minigame;
    private EndercubePlayer player;

    public MinigamePlayerLeaveEvent(String minigame, EndercubePlayer player) {
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
