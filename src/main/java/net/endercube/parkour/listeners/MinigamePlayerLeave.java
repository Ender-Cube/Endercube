package net.endercube.parkour.listeners;

import net.endercube.gamelib.events.MinigamePlayerLeaveEvent;
import net.endercube.global.EndercubePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Task;
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

        // Stop the action bar timer
        Task actionbarTimerTask = player.getTag(Tag.Transient("actionbarTimerTask"));
        actionbarTimerTask.cancel();

        // Clean up team
        player.setTeam(null);

        // Make sure the player can see hub NPCs
        player.updateViewerRule(playerVisible -> true);

        return Result.SUCCESS;
    }
}

