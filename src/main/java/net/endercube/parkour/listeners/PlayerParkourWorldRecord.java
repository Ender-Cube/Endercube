package net.endercube.parkour.listeners;

import net.endercube.gamelib.utils.ComponentUtils;
import net.endercube.global.EndercubePlayer;
import net.endercube.parkour.events.PlayerParkourWorldRecordEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.event.EventListener;
import net.minestom.server.network.packet.server.play.EntityStatusPacket;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static net.endercube.parkour.ParkourMinigame.parkourMinigame;

public class PlayerParkourWorldRecord implements EventListener<PlayerParkourWorldRecordEvent> {
    @Override
    public @NotNull Class<PlayerParkourWorldRecordEvent> eventType() {
        return PlayerParkourWorldRecordEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerParkourWorldRecordEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();

        showWRAnimation(player);
        Audiences.players().sendMessage(parkourMinigame.getChatPrefix()
                .append(Component.text(player.getUsername()))
                .append(Component.text(" Just got a "))
                .append(
                        Component.text("world record ")
                                .color(NamedTextColor.GOLD)
                                .decorate(TextDecoration.BOLD)
                )
                .append(Component.text("on " + event.getMap()))
                .append(Component.text(" with a time of "))
                .append(
                        Component.text(ComponentUtils.toHumanReadableTime(event.getTime()))
                                .color(NamedTextColor.GOLD)
                                .decorate(TextDecoration.BOLD)
                )
                .append(Component.text("!"))
        );

        return Result.SUCCESS;
    }

    private void showWRAnimation(EndercubePlayer player) {
        byte totemEffect = (byte) 35;
        player.sendPacket(new EntityStatusPacket(player.getEntityId(), totemEffect));

        final Title.Times times = Title.Times.times(Duration.ofMillis(100), Duration.ofMillis(1500), Duration.ofMillis(500));
        final Title title = Title.title(Component.text("New world record!"), Component.empty(), times);
        player.showTitle(title);


    }
}


