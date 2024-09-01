package net.endercube.hub.listeners;

import net.endercube.gamelib.events.MinigamePlayerJoinEvent;
import net.endercube.global.EndercubePlayer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventListener;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import static net.endercube.hub.HubMinigame.hubMinigame;

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
