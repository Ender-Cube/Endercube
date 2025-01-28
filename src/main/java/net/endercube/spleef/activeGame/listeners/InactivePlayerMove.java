package net.endercube.spleef.activeGame.listeners;

import net.endercube.global.EndercubePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public class InactivePlayerMove implements EventListener<PlayerMoveEvent> {
    @Override
    public @NotNull Class<PlayerMoveEvent> eventType() {
        return PlayerMoveEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerMoveEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        Instance instance = event.getInstance();
        int deathY = instance.getTag(Tag.Integer("deathY"));

        if (player.getPosition().y() < deathY) {
            player.teleport(instance.getTag(Tag.Transient("spawnPos")));
        }
        return Result.SUCCESS;
    }
}
