package net.endercube.parkour.events;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public record PlayerParkourWorldRecordEvent(Player player, String map, long record) implements PlayerEvent {
    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Player getEntity() {
        return PlayerEvent.super.getEntity();
    }
}
