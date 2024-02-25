package net.endercube.Common.enums;

import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.PlayerHeadMeta;

import java.util.Base64;
import java.util.UUID;

public enum Heads {
    QUESTION_MARK("da99b05b9a1db4d29b5e673d77ae54a77eab66818586035c8a2005aeb810602a"); // https://minecraft-heads.com/custom-heads/head/64805

    public final String mcURL;

    Heads(String mcURL) {
        this.mcURL = mcURL;
    }

    /**
     * The "Minecraft URL" section on minecraft-heads
     *
     * @return The value
     */
    public String getMcURL() {
        return mcURL;
    }

    /**
     * Gets an ItemStack of this head
     *
     * @return The ItemStack
     */
    public ItemStack getItemStack() {
        PlayerSkin playerSkin = new PlayerSkin(Base64.getEncoder().encodeToString(("{textures:{SKIN:{url:\"https://textures.minecraft.net/texture/" + mcURL + "\"}}}").getBytes()), "");
        return ItemStack.of(Material.PLAYER_HEAD).withMeta(PlayerHeadMeta.class, meta -> meta
                .skullOwner(UUID.randomUUID())
                .playerSkin(playerSkin)
                .build());
    }
}
