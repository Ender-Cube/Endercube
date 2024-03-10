package net.endercube.common;

import net.endercube.common.events.MinigameStartEvent;
import net.endercube.common.players.EndercubePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

public abstract class EndercubeActiveGame {

    protected static final Logger logger;
    private final Instance instance;
    private final Set<EndercubePlayer> players;
    private final EventNode<InstanceEvent> eventNode;
    private final EventNode<InstanceEvent> activeEventNode;
    private final EventNode<InstanceEvent> inactiveEventNode;

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


        // The instance should not contain players
        if (!instance.getPlayers().isEmpty()) {
            logger.error("The provided instance for an active game contains players! " + instance.getPlayers() + " are currently in the instance");
        }

        // Init tags
        instance.setTag(Tag.Boolean("activeGameStarted"), false);

        eventNode = instance.eventNode();

        // EventNode that will only call when the game has started
        activeEventNode = EventNode.event(
                "ActiveGameActiveEventNode-" + UUID.randomUUID(),
                EventFilter.INSTANCE,
                (InstanceEvent event) -> {
                    if (!event.getInstance().equals(instance)) {
                        return false;
                    }

                    return event.getInstance().getTag(Tag.Boolean("activeGameStarted"));
                }
        );

        // EventNode that will only call when the game has not started
        inactiveEventNode = EventNode.event(
                "ActiveGameActiveEventNode-" + UUID.randomUUID(),
                EventFilter.INSTANCE,
                (InstanceEvent event) -> {
                    if (!event.getInstance().equals(instance)) {
                        return false;
                    }

                    return !event.getInstance().getTag(Tag.Boolean("activeGameStarted"));
                }
        );
        MinecraftServer.getGlobalEventHandler().addChild(activeEventNode);
        MinecraftServer.getGlobalEventHandler().addChild(inactiveEventNode);

        this.registerDefaultEvents();
    }

    private void registerDefaultEvents() {
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

        // Automatically set "activeGameStarted" tag on minigame start
        eventNode.addListener(MinigameStartEvent.class, event -> {
            event.getInstance().setTag(Tag.Boolean("activeGameStarted"), true);
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
     * Get the event node for once the game has started, does not include minigameEvents as these should be handled elsewhere
     *
     * @return The eventNode
     */
    public EventNode<InstanceEvent> getActiveEventNode() {
        return activeEventNode;
    }

    /**
     * Get the event node for when tje game has not started, does not include minigameEvents as these should be handled elsewhere
     *
     * @return The eventNode
     */
    public EventNode<InstanceEvent> getInactiveEventNode() {
        return inactiveEventNode;
    }

    /**
     * Called when a player leaves the active game
     */
    public abstract void onPlayerLeave();
}
