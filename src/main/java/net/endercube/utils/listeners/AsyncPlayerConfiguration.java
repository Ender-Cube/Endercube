package net.endercube.utils.listeners;

import net.endercube.common.EndercubeMinigame;
import net.endercube.common.players.EndercubePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.permission.Permission;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

import static net.endercube.Main.endercubeServer;
import static net.endercube.Main.logger;

public class AsyncPlayerConfiguration implements EventListener<AsyncPlayerConfigurationEvent> {
    @Override
    public @NotNull Class<AsyncPlayerConfigurationEvent> eventType() {
        return AsyncPlayerConfigurationEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull AsyncPlayerConfigurationEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();

        if (endercubeServer == null) {
            logger.warn(player.getUsername() + " Tried to log in before endercubeServer was initialised");
            player.kick("Please try again");
            return Result.EXCEPTION;
        }

        @Nullable EndercubeMinigame hubMinigame = endercubeServer.getMinigameByName("hub");

        if (hubMinigame == null) {
            logger.error("hub minigame does not exist. Please create a minigame called \"hub\"");
            MinecraftServer.stopCleanly();
            return Result.SUCCESS;
        }

        // Tell players, and the log, that someone joined
        Component playerJoinMessage = player
                .getName()
                .append(Component.text(" joined the server"))
                .color(NamedTextColor.YELLOW);

        Audiences.players().sendMessage(playerJoinMessage);
        logger.info(ANSIComponentSerializer.ansi().serialize(playerJoinMessage));

        // Set the spawning instance and position
        event.setSpawningInstance(hubMinigame.getInstances().getFirst());

        // Set the respawn point
        Pos respawnPoint = hubMinigame.getInstances().getFirst().getTag(Tag.Transient("spawnPos"));
        player.setRespawnPoint(respawnPoint);

        // Init the current minigame
        player.setCurrentMinigame("hub");

        // Make them an op if they're op
        initOperator(player);

        player.setGameMode(GameMode.ADVENTURE);

        return Result.SUCCESS;
    }

    private void initOperator(EndercubePlayer player) {
        // Just Zax71 for now
        ArrayList<UUID> operators = new ArrayList<>();
        operators.add(UUID.fromString("aa64173b-924d-42d0-a8fc-611a46a70258"));

        if (operators.contains(player.getUuid())) {
            player.addPermission(new Permission("operator"));
        }
    }
}
