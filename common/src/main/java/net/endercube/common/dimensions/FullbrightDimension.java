package net.endercube.common.dimensions;

import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

public class FullbrightDimension {

    public static final DynamicRegistry.Key<DimensionType> INSTANCE;

    static {
        INSTANCE = MinecraftServer.getDimensionTypeRegistry().register(DimensionType.builder(NamespaceID.from("dragonescape:full_bright"))
                .ambientLight(2.0f)
                .build());
    }

}
