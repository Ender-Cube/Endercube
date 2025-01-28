package net.endercube.global.listeners;

import net.endercube.global.EndercubePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

import static net.endercube.Main.jedis;

public class AsyncPlayerPreLogin implements EventListener<AsyncPlayerPreLoginEvent> {
    @Override
    public @NotNull Class<AsyncPlayerPreLoginEvent> eventType() {
        return AsyncPlayerPreLoginEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull AsyncPlayerPreLoginEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getConnection().getPlayer();
        if (isBanned(player)) {
            player.kick(getBanMessage(player));
            return Result.SUCCESS;
        }

        return Result.SUCCESS;
    }

    private boolean isBanned(EndercubePlayer player) {
        return jedis.exists("banned:" + player.getUuid());
    }

    private String getBanMessage(EndercubePlayer player) {
        return jedis.get("banned:" + player.getUuid());
    }
}
