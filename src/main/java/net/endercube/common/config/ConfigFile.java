package net.endercube.common.config;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigFile {

    private HoconConfigurationLoader configLoader;

    private CommentedConfigurationNode config;
    private static final Logger logger;

    static {
        logger = LoggerFactory.getLogger(ConfigFile.class);
    }

    /**
     * Creates a config file
     *
     * @param name    The name of the file (excluding .conf)
     * @param comment The header for the file
     */
    public ConfigFile(String name, String comment) {
        // Create config
        String fileName = name + ".conf";

        configLoader = HoconConfigurationLoader.builder()
                .path(Paths.get("./config/" + fileName))
                .defaultOptions(configurationOptions -> configurationOptions.header(comment))
                .build();


        try {
            config = configLoader.load();
        } catch (ConfigurateException e) {
            logger.error("An error occurred while loading \"" + fileName + "\": " + e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            MinecraftServer.stopCleanly();
        }

        // Required to create the config file
        this.saveConfig();
    }

    public HoconConfigurationLoader getConfigLoader() {
        return configLoader;
    }

    public CommentedConfigurationNode getConfig() {
        return config;
    }

    /**
     * Saves unwritten changes to config
     */
    public void saveConfig() {
        try {
            configLoader.save(config);
        } catch (final ConfigurateException e) {
            logger.error("Unable to save your messages configuration! Sorry! " + e.getMessage());
            MinecraftServer.stopCleanly();
        }
    }

    /**
     * @param node  The {@link ConfigurationNode} to write to
     * @param value The value to write to that node
     * @return a {@link String} containing what the value currently is
     */
    public String getOrSetDefault(ConfigurationNode node, String value) {
        if (node.getString() == null) {
            node.raw(value);
            logger.info("Config node: " + node.path().toString() + " Does not exist, setting it to \"" + value + "\"");
            saveConfig();
            return value;
        }

        return node.getString();
    }

    /**
     * Reads an {@link ItemStack} from config
     *
     * @param configNode The {@link ConfigurationNode} to read from
     * @return {@link ItemStack}
     */
    @Nullable
    public ItemStack getItemStackFromConfig(ConfigurationNode configNode) {
        String materialString = configNode.node("material").getString();
        String name = configNode.node("name").getString();

        if (materialString == null) {
            materialString = "minecraft:barrier";
            logger.warn("Please set a material for the map above");
        }
        if (name == null) {
            name = "Please set a name in config for this";
            logger.warn("Please set a name for the map above");
        }

        Material material = Material.fromNamespaceId(materialString);

        if (material == null) {
            logger.warn("The material, " + materialString + " in config of the map above is invalid");
            return null;
        }

        return ItemStack.of(material).with(ItemComponent.CUSTOM_NAME, MiniMessage.miniMessage().deserialize(name));
    }

    @Nullable
    public Float[] getFloatListFromConfig(ConfigurationNode configNode) {
        Float[] floats;
        try {
            floats = configNode.get(new TypeToken<>() {
            });
        } catch (SerializationException e) {
            logger.warn("Failed to get a float list from config");
            throw new RuntimeException(e);
        }

        return floats;
    }

    /**
     * Gets a single {@link Pos} from config
     *
     * @param configNode The {@link ConfigurationNode} to read from
     * @return {@link Pos}
     */
    @Nullable
    public Pos getPosFromConfig(ConfigurationNode configNode) {
        Float[] pointList = getFloatListFromConfig(configNode);

        if (pointList.length == 3) {
            return new Pos(pointList[0], pointList[1], pointList[2]);
        } else if (pointList.length == 5) {
            return new Pos(pointList[0], pointList[1], pointList[2], pointList[3], pointList[4]);
        }

        logger.warn("Reading position value: \"" + configNode.path().toString() + "\" failed for some reason");
        return null;
    }

    /**
     * Returns a list of {@link Pos} from config
     *
     * @param configNode The {@link ConfigurationNode} to read from
     * @return A list of {@link Pos}
     */
    @Nullable
    public Pos[] getPosListFromConfig(ConfigurationNode configNode) {
        List<Pos> outArrayList = new ArrayList<>();

        // Loop through the list at the specific node and add it to our out array list
        for (ConfigurationNode currentNode : configNode.childrenList()) {
            outArrayList.add(getPosFromConfig(currentNode));
        }
        return outArrayList.toArray(new Pos[0]);
    }

    @Nullable
    public Vec getVecFromConfig(ConfigurationNode configNode) {
        Float[] pointList = getFloatListFromConfig(configNode);

        if (pointList.length == 3) {
            return new Vec(pointList[0], pointList[1], pointList[2]);
        }

        logger.warn("Vector value in config's length is out of bounds or blank");
        return null;
    }

    @Nullable
    public Vec[] getVecListFromConfig(ConfigurationNode configNode) {
        List<Vec> outArrayList = new ArrayList<>();

        // Loop through the list at the specific node and add it to our out array list
        for (ConfigurationNode currentNode : configNode.childrenList()) {
            outArrayList.add(getVecFromConfig(currentNode));
        }
        return outArrayList.toArray(new Vec[0]);
    }
}
