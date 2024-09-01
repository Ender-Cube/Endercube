package net.endercube.parkour.events;

import net.endercube.gamelib.events.eventTypes.PlayerMinigameEvent;
import net.endercube.global.EndercubePlayer;
import org.jetbrains.annotations.NotNull;

public record PlayerParkourPersonalBestEvent(
        EndercubePlayer player,
        String map,
        long time
) implements PlayerMinigameEvent {
    @Override
    public @NotNull String getMinigame() {
        return "parkour";
    }

    @Override
    public @NotNull EndercubePlayer getPlayer() {
        return player;
    }

    @Override
    public @NotNull EndercubePlayer getEntity() {
        return player;
    }

    public @NotNull String getMap() {
        return map;
    }

    public long getTime() {
        return time;
    }
}
