package net.endercube.global.blocks;

import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class Skull implements BlockHandler {
    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return List.of(
                Tag.String("ExtraType"),
                Tag.NBT("SkullOwner")
        );
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("minecraft:skull");
    }

}
