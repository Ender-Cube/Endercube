package net.endercube.gamelib.dimensions;

import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;

public class FullbrightDimension {

    public static final RegistryKey<DimensionType> INSTANCE;

    static {
        INSTANCE = MinecraftServer
                .getDimensionTypeRegistry()
                .register(
                        Key.key("endercube:full_bright"),
                        DimensionType.builder()
                                .ambientLight(2.0f)
                                .build()
                );
    }

}
