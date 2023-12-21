package net.endercube.Parkour.listeners;

import net.endercube.Common.events.MinigamePlayerJoinEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.endercube.Parkour.InventoryItems;
import net.minestom.server.event.EventListener;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import static net.endercube.Common.EndercubeMinigame.logger;
import static net.endercube.Parkour.ParkourMinigame.parkourMinigame;

public class MinigamePlayerJoin implements EventListener<MinigamePlayerJoinEvent> {




    @Override
    public @NotNull Class<MinigamePlayerJoinEvent> eventType() {
        return MinigamePlayerJoinEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull MinigamePlayerJoinEvent event) {
        EndercubePlayer player = event.getPlayer();
        String mapName = event.getMap();

        logger.info("Sending " + player.getUsername() + " To a parkour map");
        player.sendMessage("Sending you to Easy-1");

        InstanceContainer instance = parkourMinigame
                .getInstances()
                .stream()
                .filter(
                        (mapInstance) -> mapInstance.getTag(Tag.String("name")).equals(mapName)
                )
                .findFirst()
                .orElse(null);

        if (instance == null) {
            logger.error("Parkour was given a map name that does not exist. something really broke good...");
            return Result.INVALID;
        }

        player.setInstance(instance);
        player.teleport(instance.getTag(Tag.Transient("spawnPos")));

        // Init tags
        player.setTag(Tag.Integer("parkour_checkpoint"), -1);
        player.setTag(Tag.Boolean("parkour_timerStarted"), false);

        addInventoryButtons(player);

        return Result.SUCCESS;
    }

    private void addInventoryButtons(EndercubePlayer player) {
        player.getInventory().setItemStack(0, InventoryItems.CHECKPOINT_ITEM);
        player.getInventory().setItemStack(1, InventoryItems.RESTART_ITEM);
        player.getInventory().setItemStack(4, InventoryItems.VISIBILITY_ITEM_INVISIBLE);
        player.getInventory().setItemStack(8, InventoryItems.HUB_ITEM);
    }
}
