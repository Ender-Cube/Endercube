package net.endercube.discord.listeners;

import net.endercube.discord.Discord;
import net.endercube.global.EndercubePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerChat implements EventListener<PlayerChatEvent> {
    @Override
    public @NotNull Class<PlayerChatEvent> eventType() {
        return PlayerChatEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerChatEvent event) {
        String message = event.getRawMessage();
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        Discord.discordWebhook.sendMessage(player, message);

        return Result.SUCCESS;
    }
}
