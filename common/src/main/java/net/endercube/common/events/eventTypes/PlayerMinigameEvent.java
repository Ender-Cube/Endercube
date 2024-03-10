package net.endercube.common.events.eventTypes;

import net.endercube.common.players.EndercubePlayer;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public interface PlayerMinigameEvent extends PlayerEvent {

    /**
     * Get the minigame
     *
     * @return The minigame
     */
    @NotNull String getMinigame();

    /**
     * Gets the player.
     *
     * @return The player
     */
    @NotNull EndercubePlayer getPlayer();

    /**
     * Returns {@link #getPlayer()}.
     */
    @Override
    default @NotNull EndercubePlayer getEntity() {
        return getPlayer();
    }
}
