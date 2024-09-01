package net.endercube.gamelib.dimensions;

import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;

public class FullbrightDimension {

    public static final DynamicRegistry.Key<DimensionType> INSTANCE;

    static {
        INSTANCE = MinecraftServer.getDimensionTypeRegistry().register("endercube:full_bright", DimensionType.builder()
                .ambientLight(2.0f)
                .build());
    }

}
