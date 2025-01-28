package net.endercube.spleef.minigame.listeners;

import net.endercube.global.EndercubePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import static net.endercube.spleef.minigame.SpleefMinigame.logger;

public class PlayerMove implements EventListener<PlayerMoveEvent> {
    @Override
    public @NotNull Class<PlayerMoveEvent> eventType() {
        return PlayerMoveEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerMoveEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        logger.debug(player.getUsername() + " moved");
//        int deathY = event.getInstance().getTag(Tag.Integer("deathY"));
//        Pos spawnPos = event.getInstance().getTag(Tag.Transient("spawnPos"));
//
//        if (player.getPosition().y() < deathY) {
//            logger.info("Killing " + player.getUsername());
//
//            player.teleport(spawnPos);
//        }
        return Result.SUCCESS;
    }
}
