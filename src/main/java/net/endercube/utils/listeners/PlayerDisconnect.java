package net.endercube.utils.listeners;

import net.endercube.common.players.EndercubePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import org.jetbrains.annotations.NotNull;

import static net.endercube.Main.logger;

public class PlayerDisconnect implements EventListener<PlayerDisconnectEvent> {
    @Override
    public @NotNull Class<PlayerDisconnectEvent> eventType() {
        return PlayerDisconnectEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerDisconnectEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();

        // Tell players, and the log, that someone joined
        Component playerLeaveMessage = player
                .getName()
                .append(Component.text(" left the server"))
                .color(NamedTextColor.YELLOW);

        Audiences.players().sendMessage(playerLeaveMessage);
        logger.info(ANSIComponentSerializer.ansi().serialize(playerLeaveMessage));

        return Result.SUCCESS;
    }
}
