package net.endercube.Hub.listeners;

import net.endercube.Common.events.MinigamePlayerJoinEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventListener;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import static net.endercube.Hub.HubMinigame.hubMinigame;

public class MinigamePlayerJoin implements EventListener<MinigamePlayerJoinEvent> {
    @Override
    public @NotNull Class<MinigamePlayerJoinEvent> eventType() {
        return MinigamePlayerJoinEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull MinigamePlayerJoinEvent event) {
        Instance hubInstance = hubMinigame.getInstances().get(0);
        Pos spawnPosition = hubInstance.getTag(Tag.Transient("spawnPos"));
        EndercubePlayer player = event.getPlayer();
        player.setInstance(hubInstance, spawnPosition);
        return Result.SUCCESS;
    }
}
