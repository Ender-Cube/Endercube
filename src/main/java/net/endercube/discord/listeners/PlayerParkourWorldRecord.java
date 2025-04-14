package net.endercube.discord.listeners;

import net.endercube.discord.Discord;
import net.endercube.gamelib.utils.ComponentUtils;
import net.endercube.global.EndercubePlayer;
import net.endercube.parkour.events.PlayerParkourWorldRecordEvent;
import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;

public class PlayerParkourWorldRecord implements EventListener<PlayerParkourWorldRecordEvent> {
    @Override
    public @NotNull Class<PlayerParkourWorldRecordEvent> eventType() {
        return PlayerParkourWorldRecordEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerParkourWorldRecordEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        Discord.sendMessage(
                player,
                player.getUsername()
                        + " Just got a new world record of "
                        + ComponentUtils.toHumanReadableTime(event.time())
                        + " on "
                        + event.map()
                        + " \uD83C\uDF89" // Some emojis
        );
        return Result.SUCCESS;
    }
}
