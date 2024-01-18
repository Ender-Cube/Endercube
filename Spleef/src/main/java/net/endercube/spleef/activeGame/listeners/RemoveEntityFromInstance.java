package net.endercube.spleef.activeGame.listeners;

import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Common.utils.ComponentUtils;
import net.endercube.spleef.minigame.SpleefMinigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import static net.endercube.Common.EndercubeMinigame.logger;

public class RemoveEntityFromInstance implements EventListener<RemoveEntityFromInstanceEvent> {
    private final Instance gameInstance;

    public RemoveEntityFromInstance(Instance gameInstance) {
        this.gameInstance = gameInstance;
    }

    @Override
    public @NotNull Class<RemoveEntityFromInstanceEvent> eventType() {
        return RemoveEntityFromInstanceEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull RemoveEntityFromInstanceEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return Result.INVALID;
        }
        EndercubePlayer player = (EndercubePlayer) event.getEntity();
        logger.debug(player.getUsername() + " just left an active game");

        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().setItemStack(0, ItemStack.AIR);

        // Right, done all the cleanup. Now for stuff that happens if you actually get the game to start
        if (!gameInstance.getTag(Tag.Boolean("activeGameStarted"))) {
            return Result.SUCCESS;
        }

        int placement = gameInstance.getPlayers().size();
        player.sendMessage(SpleefMinigame.spleefMinigame.getChatPrefix()
                .append(Component.text("You got "))
                .append(ComponentUtils.addOrdinals(placement).decorate(TextDecoration.BOLD))
                .append(Component.text(" place!"))
        );

        return Result.SUCCESS;
    }
}
