package net.endercube.Common.players;

import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EndercubePlayer extends Player {
    public EndercubePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
    }

    /**
     * Get the current minigame
     * @return the name of the minigame or "hub" if in the hub
     */
    public String getCurrentMinigame() {
        // TODO: implement
        return "To be implemented";
    }

    /**
     * Teleport the player to the hub
     */
    public void gotoHub() {
        // TODO: implement
    }
}
