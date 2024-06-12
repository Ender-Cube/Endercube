package net.endercube.common.utils;

import net.minestom.server.coordinate.Pos;

public class CoordinateUtils {

    /**
     * Gets the center of a block from a block position
     *
     * @param blockPos The {@link Pos} with the integer block position
     * @return The {@link Pos} with the .5 position
     */
    public static Pos posFromBlockpos(Pos blockPos) {
        double x = blockPos.blockX();
        double z = blockPos.blockZ();

        if (x > 0) {
            x = x - 0.5;
        } else {
            x = x + 0.5;
        }

        if (z > 0) {
            z = z - 0.5;
        } else {
            z = z + 0.5;
        }

        return new Pos(x, blockPos.y(), z, blockPos.yaw(), blockPos.pitch());
    }
}
