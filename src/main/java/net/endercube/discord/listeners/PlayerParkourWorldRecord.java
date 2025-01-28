package net.endercube.discord.listeners;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.endercube.gamelib.utils.ComponentUtils;
import net.endercube.global.EndercubePlayer;
import net.endercube.parkour.events.PlayerParkourWorldRecordEvent;
import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;

import static net.endercube.discord.Discord.webhookClient;

public class PlayerParkourWorldRecord implements EventListener<PlayerParkourWorldRecordEvent> {
    @Override
    public @NotNull Class<PlayerParkourWorldRecordEvent> eventType() {
        return PlayerParkourWorldRecordEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerParkourWorldRecordEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        webhookClient.send(new WebhookMessageBuilder()
                .setUsername(player.getUsername()) // use this username
                .setAvatarUrl("https://mc-heads.net/avatar/" + player.getUuid()) // use this avatar
                .setContent(player.getUsername() + " Just got a new world record of " + ComponentUtils.toHumanReadableTime(event.time()) + " on " + event.map() + " \uD83C\uDF89")
                .build());
        return Result.SUCCESS;
    }
}
