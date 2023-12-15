package net.endercube.Common;

import net.endercube.Common.events.eventTypes.PlayerMinigameEvent;
import net.endercube.Common.utils.ConfigUtils;
import net.minestom.server.MinecraftServer;
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
    protected ArrayList<InstanceContainer> instances;

    // Create an instance of the logger
    static {
        logger = LoggerFactory.getLogger(EndercubeMinigame.class);
    }

    protected EndercubeMinigame() {
        createConfig();

        // Initialise instances
        instances = new ArrayList<>();
        instances = initInstances();

        // Create the eventNode
        // Filter if the event is an instanceEvent happening in our instances or a minigameEvent on this minigame
        eventNode = EventNode.event(getName(), EventFilter.ALL, (Event event) -> {
            if (event instanceof InstanceEvent instanceEvent) {
                return getInstances().contains(instanceEvent.getInstance());
            }

            if (event instanceof PlayerMinigameEvent minigameEvent) {
                return minigameEvent.getMinigame().equals(getName());
            }

            return false;
        });

        // Register the event node
        MinecraftServer.getGlobalEventHandler().addChild(eventNode);
    }


    /**
     * The name of this minigame. must be unique
     * @return The name
     */
    public abstract String getName();

    /**
     * Loads all the instances
     * @return An ArrayList of InstanceContainer's
     */
    protected abstract ArrayList<InstanceContainer> initInstances();

    /**
     * Get the instances associated with this minigame
     * @return The instances
     */
    public ArrayList<InstanceContainer> getInstances() {
        return instances;
    }

    private void createConfig() {
        // Create config and configUtils
        String fileName = getName() + ".conf";


        configLoader = HoconConfigurationLoader.builder()
                .path(Paths.get("./config/" + fileName))
                .defaultOptions(configurationOptions -> configurationOptions.header("This is the configuration file for the " + getName() + " minigame"))
                .build();


        try {
            config = configLoader.load();
        } catch (ConfigurateException e) {
            logger.error("An error occurred while loading \"" + fileName + "\": " + e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            MinecraftServer.stopCleanly();
        }

        configUtils = new ConfigUtils(configLoader, config);

        // Required to create the config file
        configUtils.saveConfig();
    }
}
