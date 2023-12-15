package net.endercube.Parkour.listeners;

import net.endercube.Common.events.MinigamePlayerJoinEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;

public class MinigamePlayerJoinEventListener implements EventListener<MinigamePlayerJoinEvent> {
    @Override
    public @NotNull Class<MinigamePlayerJoinEvent> eventType() {
        return MinigamePlayerJoinEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull MinigamePlayerJoinEvent event) {
        EndercubePlayer player = event.getPlayer();
        player.sendMessage("Sending you to Easy-1");

        return Result.SUCCESS;
    }
}
