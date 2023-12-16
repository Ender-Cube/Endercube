package net.endercube.Hub.listeners;

import net.endercube.Common.events.MinigamePlayerJoinEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventListener;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

public class MinigamePlayerJoinEventListener implements EventListener<MinigamePlayerJoinEvent> {
    @Override
    public @NotNull Class<MinigamePlayerJoinEvent> eventType() {
        return MinigamePlayerJoinEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull MinigamePlayerJoinEvent event) {
        EndercubePlayer player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);

        player.playSound(Sound.sound(SoundEvent.ENTITY_FIREWORK_ROCKET_LAUNCH, Sound.Source.AMBIENT, 1f, 1f), player.getPosition());
        return Result.SUCCESS;
    }
}
