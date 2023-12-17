package net.endercube.Parkour.listeners;

import net.endercube.Common.events.MinigamePlayerJoinEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import static net.endercube.Common.EndercubeMinigame.logger;
import static net.endercube.Parkour.ParkourMinigame.parkourMinigame;

public class MinigamePlayerJoin implements EventListener<MinigamePlayerJoinEvent> {
    @Override
    public @NotNull Class<MinigamePlayerJoinEvent> eventType() {
        return MinigamePlayerJoinEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull MinigamePlayerJoinEvent event) {

        EndercubePlayer player = event.getPlayer();
        logger.info("Sending " + player.getUsername() + " To a parkour map");
        player.sendMessage("Sending you to Easy-1");

        InstanceContainer instance = parkourMinigame.getEndercubeServer().getMinigameByName("parkour").getInstances().get(0);
        player.setInstance(instance);
        player.teleport(instance.getTag(Tag.Transient("spawnPos")));

        // Init tags
        player.setTag(Tag.Integer("parkour_checkpoint"), -1);

        return Result.SUCCESS;
    }
}
