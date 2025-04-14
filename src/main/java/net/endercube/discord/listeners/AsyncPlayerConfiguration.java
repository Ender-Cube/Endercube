package net.endercube.discord.listeners;

import net.endercube.discord.Discord;
import net.endercube.global.EndercubePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerConfiguration implements EventListener<AsyncPlayerConfigurationEvent> {
    @Override
    public @NotNull Class<AsyncPlayerConfigurationEvent> eventType() {
        return AsyncPlayerConfigurationEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull AsyncPlayerConfigurationEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        Discord.sendMessage(player, "I just joined the server! (" + (MinecraftServer.getConnectionManager().getOnlinePlayerCount() + 1) + ")");
        return Result.SUCCESS;
    }
}
