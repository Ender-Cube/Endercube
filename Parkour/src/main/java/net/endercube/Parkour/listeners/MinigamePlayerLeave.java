package net.endercube.Parkour.listeners;

import net.endercube.Common.events.MinigamePlayerLeaveEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;

public class MinigamePlayerLeave implements EventListener<MinigamePlayerLeaveEvent> {
    @Override
    public @NotNull Class<MinigamePlayerLeaveEvent> eventType() {
        return MinigamePlayerLeaveEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull MinigamePlayerLeaveEvent event) {
        EndercubePlayer player = event.getPlayer();
        player.getInventory().clear();
        return Result.SUCCESS;
    }
}

