package net.endercube.spleef.minigame.listeners;

import net.endercube.gamelib.events.MinigamePlayerLeaveEvent;
import net.endercube.global.EndercubePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.endercube.gamelib.EndercubeMinigame.logger;
import static net.endercube.spleef.minigame.SpleefMinigame.spleefMinigame;

public class MinigamePlayerLeave implements EventListener<MinigamePlayerLeaveEvent> {

    @Override
    public @NotNull Class<MinigamePlayerLeaveEvent> eventType() {
        return MinigamePlayerLeaveEvent.class;
    }

    @Override
    public @NotNull net.minestom.server.event.EventListener.Result run(@NotNull MinigamePlayerLeaveEvent event) {
        EndercubePlayer player = event.getPlayer();
        Instance instance = spleefMinigame.getInstances().getFirst();

        // Stop the start if we dip below the minimum player count
        logger.debug("There are " + instance.getPlayers().size() + " players in the spleef lobby and the minimum player count is " + spleefMinigame.getMinimumPlayers());
        if (instance.getPlayers().size() < spleefMinigame.getMinimumPlayers()) {
            @Nullable final Task startingTask = instance.getTag(Tag.Transient("startingTask"));
            if (startingTask != null) {
                startingTask.cancel();
            }
        }
        logger.debug(player.getUsername() + " just left the spleef lobby");


        return Result.SUCCESS;
    }
}
