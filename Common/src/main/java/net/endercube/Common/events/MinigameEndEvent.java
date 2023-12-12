package net.endercube.Common.events;

import net.minestom.server.event.Event;

/**
 * Called when a minigame ends
 */
public record MinigameEndEvent(String minigameName) implements Event {
}
