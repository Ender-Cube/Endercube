package net.endercube.Common.events;

import net.minestom.server.event.Event;

/**
 * Called when a minigame starts
 */
public record MinigameStartEvent(String minigameName) implements Event {
}
