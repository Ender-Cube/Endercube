package net.endercube.parkour.listeners;

import net.endercube.common.players.EndercubePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerUseItemEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerUseItem implements EventListener<PlayerUseItemEvent> {
    @Override
    public @NotNull Class<PlayerUseItemEvent> eventType() {
        return PlayerUseItemEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerUseItemEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();

        ItemClickHandler.handleClick(event.getItemStack(), player);

        return Result.SUCCESS;
    }


}

