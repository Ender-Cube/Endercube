package net.endercube.Common.events;

import net.endercube.Common.players.EndercubePlayer;
import net.minestom.server.event.Event;

/**
 * Called when a player joins the minigame
 */
public record MinigamePlayerJoinEvent(String minigameName, EndercubePlayer player) implements Event {
}
