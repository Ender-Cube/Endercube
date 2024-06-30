package net.endercube.spleef.activeGame.listeners;

import net.endercube.common.players.EndercubePlayer;
import net.endercube.spleef.minigame.SpleefMinigame;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import static net.endercube.common.EndercubeMinigame.logger;

public class ActivePlayerMove implements EventListener<PlayerMoveEvent> {
    @Override
    public @NotNull Class<PlayerMoveEvent> eventType() {
        return PlayerMoveEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerMoveEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        int deathY = event.getInstance().getTag(Tag.Integer("deathY"));

        if (player.getPosition().y() < deathY) {
            logger.info("Killing " + player.getUsername());

            player.teleport(new Pos(0, 100, 0));
            // Send the player back to the hub. The rest of the game end logic is handled already
            Instance hubInstance = SpleefMinigame.spleefMinigame.getEndercubeServer().getMinigameByName("hub").getInstances().getFirst();
            player.setInstance(hubInstance);
        }
        return Result.SUCCESS;
    }
}
