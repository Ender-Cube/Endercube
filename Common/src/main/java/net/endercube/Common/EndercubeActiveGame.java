package net.endercube.Common;

import net.endercube.Common.players.EndercubePlayer;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public abstract class EndercubeActiveGame {

    private static final Logger logger;


    private final Instance instance;


    private final Set<EndercubePlayer> players;
    private final EventNode<InstanceEvent> eventNode;

    static {
        logger = LoggerFactory.getLogger(EndercubeActiveGame.class);
    }

    /**
     * Players will be teleported to the instance automatically
     *
     * @param instance The instance the active game takes place in
     * @param players  The players involved, updated on leave
     */
    public EndercubeActiveGame(@NotNull Instance instance, @NotNull Set<EndercubePlayer> players) {
        this.instance = instance;
        this.players = players;
        this.eventNode = instance.eventNode();

        // The instance should not contain players
        if (!instance.getPlayers().isEmpty()) {
            logger.error("The provided instance for an active game contains players! " + instance.getPlayers() + " are currently in the instance");
        }

        // Automatically call minigameLeave on player leave
        eventNode.addListener(RemoveEntityFromInstanceEvent.class, event -> {
            if (event.getEntity().getEntityType() != EntityType.PLAYER) {
                return;
            }

            EndercubePlayer player = (EndercubePlayer) event.getEntity();

            players.remove(player);
            logger.debug(player.getUsername() + " Just left an active minigame, removing from the players and calling custom tasks");
            this.onPlayerLeave();
        });
    }

    protected Instance getInstance() {
        return instance;
    }

    /**
     * @return The players in the minigame
     */
    protected Set<EndercubePlayer> getPlayers() {
        return players;
    }

    /**
     * Get the event node for this instance, does not include minigameEvents as these should be handled elsewhere
     *
     * @return The eventNode
     */
    protected EventNode<InstanceEvent> getEventNode() {
        return eventNode;
    }

    /**
     * Called when a player leaves the active game
     */
    public abstract void onPlayerLeave();
}
