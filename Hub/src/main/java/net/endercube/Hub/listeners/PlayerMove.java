package net.endercube.Hub.listeners;

import net.endercube.Common.players.EndercubePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public class PlayerMove implements EventListener<PlayerMoveEvent> {
    @Override
    public @NotNull Class<PlayerMoveEvent> eventType() {
        return PlayerMoveEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerMoveEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        int deathY = player.getInstance().getTag(Tag.Integer("deathY"));

        // Send to hub spawn if below death barrier
        if (player.getPosition().y() < deathY) {
            player.gotoHub();
        }
        return Result.SUCCESS;
    }
}
