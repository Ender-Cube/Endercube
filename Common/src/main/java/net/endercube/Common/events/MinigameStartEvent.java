package net.endercube.Common.events;

import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a minigame's game starts, used internally in minigames
 */
public record MinigameStartEvent(Instance instance) implements InstanceEvent {
    @Override
    public @NotNull Instance getInstance() {
        return instance;
    }
}
