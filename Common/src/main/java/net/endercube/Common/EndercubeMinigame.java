package net.endercube.Common;

import net.endercube.Common.utils.ConfigUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Override this class to add minigames
 */
public abstract class EndercubeMinigame {

    public static final Logger logger;
    private HoconConfigurationLoader configLoader;
    protected CommentedConfigurationNode config;
    protected ConfigUtils configUtils;
    protected @NotNull EventNode<Event> eventNode;

    static {
        logger = LoggerFactory.getLogger(EndercubeMinigame.class);
    }

    protected EndercubeMinigame() {
        // Create config and configUtils
        String fileName = getName() + ".conf";


        configLoader = HoconConfigurationLoader.builder()
                .path(Paths.get("./config/" + fileName))
                .defaultOptions(configurationOptions -> configurationOptions.header("This is the configuration file for the " + getName() + " minigame"))
                .build();


        try {
            config = configLoader.load();
        } catch (ConfigurateException e) {
            logger.error("An error occurred while loading " + fileName + ": " + e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            MinecraftServer.stopCleanly();
        }

        configUtils = new ConfigUtils(configLoader, config);

        // Required to create the config file
        configUtils.saveConfig();
    }

    public abstract String getName();

    public abstract ArrayList<InstanceContainer> getInstances();

    public abstract Pos[] getSpawnPositions();

    public void registerEventNode() {
        MinecraftServer.getGlobalEventHandler().addChild(eventNode);
    }
}
