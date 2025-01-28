package net.endercube.gamelib.events.eventTypes;

import net.endercube.global.EndercubePlayer;
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
