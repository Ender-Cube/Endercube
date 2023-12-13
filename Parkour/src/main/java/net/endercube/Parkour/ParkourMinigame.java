package net.endercube.Parkour;

import net.endercube.Common.EndercubeMinigame;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;

/**
 * This is the entrypoint for Parkour
 */
public class ParkourMinigame extends EndercubeMinigame {

    @Override
    public String getName() {
        return "parkour";
    }

    @Override
    public ArrayList<InstanceContainer> getInstances() {
        return null;
    }

    // TODO: implement properly
    @Override
    public Pos[] getSpawnPositions() {
        return new Pos[0];
    }
}