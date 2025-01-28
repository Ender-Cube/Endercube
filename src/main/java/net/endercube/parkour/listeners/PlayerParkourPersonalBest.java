package net.endercube.parkour.listeners;

import net.endercube.global.EndercubePlayer;
import net.endercube.parkour.events.PlayerParkourPersonalBestEvent;
import net.kyori.adventure.text.Component;
import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;

import static net.endercube.parkour.ParkourMinigame.parkourMinigame;

public class PlayerParkourPersonalBest implements EventListener<PlayerParkourPersonalBestEvent> {
    @Override
    public @NotNull Class<PlayerParkourPersonalBestEvent> eventType() {
        return PlayerParkourPersonalBestEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerParkourPersonalBestEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        player.sendMessage(parkourMinigame.getChatPrefix().append(Component.text("That run was a PB! Nice job")));
        return Result.SUCCESS;
    }
}
