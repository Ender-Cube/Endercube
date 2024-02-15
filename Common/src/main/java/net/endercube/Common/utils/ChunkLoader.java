package net.endercube.Common.utils;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;


public class ChunkLoader {

    /**
     * @param instance The instance to load the chunks in
     * @param center   A position in the center chunk
     * @param radius   The radius to load with 0 loading 1 chunk and 1 loading a 3x3
     */
    public static void loadRadius(Instance instance, Point center, int radius) {

        for (int x = center.chunkX() - radius; x <= center.chunkX() + radius; x++) {
            for (int z = center.chunkZ() - radius; z <= center.chunkZ() + radius; z++) {
                instance.loadChunk(x, z);
            }
        }
    }
}
