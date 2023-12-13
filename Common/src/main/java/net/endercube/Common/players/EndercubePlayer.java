package net.endercube.Common.players;

import net.endercube.Common.events.MinigamePlayerJoinEvent;
import net.endercube.Common.events.MinigamePlayerLeaveEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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
     * Sets the player's current minigame
     */
    public void setCurrentMinigame(String minigame) {
        // TODO: implement
    }

    /**
     * Teleport the player to the hub
     */
    public void gotoHub() {
        if (Objects.equals(this.getCurrentMinigame(), "hub")) {
            this.sendMessage("You are already in the hub! Going to the hub failed");
        }
        MinecraftServer.getGlobalEventHandler().call(new MinigamePlayerLeaveEvent(this.getCurrentMinigame(), this));
        MinecraftServer.getGlobalEventHandler().call(new MinigamePlayerJoinEvent("hub", this));
    }
}
