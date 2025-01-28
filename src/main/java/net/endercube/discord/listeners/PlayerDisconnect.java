package net.endercube.discord.listeners;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.endercube.global.EndercubePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import org.jetbrains.annotations.NotNull;

import static net.endercube.discord.Discord.webhookClient;

public class PlayerDisconnect implements EventListener<PlayerDisconnectEvent> {
    @Override
    public @NotNull Class<PlayerDisconnectEvent> eventType() {
        return PlayerDisconnectEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerDisconnectEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        webhookClient.send(new WebhookMessageBuilder()
                .setUsername(player.getUsername()) // use this username
                .setAvatarUrl("https://mc-heads.net/avatar/" + player.getUuid()) // use this avatar
                .setContent("I just left the server :( (" + MinecraftServer.getConnectionManager().getOnlinePlayerCount() + ")")
                .build());
        return Result.SUCCESS;
    }
}
