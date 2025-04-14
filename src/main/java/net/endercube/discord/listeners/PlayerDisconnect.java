package net.endercube.discord.listeners;

import net.endercube.discord.Discord;
import net.endercube.global.EndercubePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDisconnect implements EventListener<PlayerDisconnectEvent> {
    @Override
    public @NotNull Class<PlayerDisconnectEvent> eventType() {
        return PlayerDisconnectEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerDisconnectEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        Discord.sendMessage(player, "I just left the server :( (" + MinecraftServer.getConnectionManager().getOnlinePlayerCount() + ")");
        return Result.SUCCESS;
    }
}
