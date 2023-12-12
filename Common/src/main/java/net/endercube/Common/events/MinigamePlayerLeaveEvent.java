package net.endercube.Common.events;

import net.endercube.Common.players.EndercubePlayer;
import net.minestom.server.event.Event;

/**
 * Called when a player leaves
 */
public record MinigamePlayerLeaveEvent(String minigameName, EndercubePlayer player) implements Event {
}
