package net.endercube.global;

import net.endercube.gamelib.events.MinigamePlayerJoinEvent;
import net.endercube.gamelib.events.MinigamePlayerLeaveEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EndercubePlayer extends Player {
    public EndercubePlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
    }

    /**
     * Get the current minigame
     *
     * @return the name of the minigame or "hub" if in the hub
     */
    public String getCurrentMinigame() {
        return this.getTag(Tag.String("minigame"));
    }

    /**
     * Sets the player's current minigame
     */
    public void setCurrentMinigame(String minigame) {
        this.setTag(Tag.String("minigame"), minigame);
    }

    /**
     * Teleport the player to the hub
     */
    public void gotoHub() {
        if (Objects.equals(this.getCurrentMinigame(), "hub")) {
            this.sendMessage("Sending you to the hub spawn");
            this.teleport(this.getInstance().getTag(Tag.Transient("spawnPos")));
            return;
        }
        this.sendMessage("Sending you to the hub");
        MinecraftServer.getGlobalEventHandler().call(new MinigamePlayerLeaveEvent(this.getCurrentMinigame(), this));
        MinecraftServer.getGlobalEventHandler().call(new MinigamePlayerJoinEvent("hub", this, null));
    }
}
