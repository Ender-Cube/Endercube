package net.endercube.spleef.activeGame.listeners;


import net.endercube.common.players.EndercubePlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class PlayerStartDigging implements EventListener<PlayerStartDiggingEvent> {
    @Override
    public @NotNull Class<PlayerStartDiggingEvent> eventType() {
        return PlayerStartDiggingEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerStartDiggingEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        Point blockPos = event.getBlockPosition();
        Instance instance = event.getInstance();

        instance.setBlock(blockPos, Block.AIR);
        Sound snowBreak = Sound.sound(Key.key("block.snow.break"), Sound.Source.BLOCK, 1f, 1f);
        player.playSound(snowBreak);
        return Result.SUCCESS;
    }
}
