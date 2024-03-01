package net.endercube.Endercube.listeners;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import org.jetbrains.annotations.NotNull;

public class ServerTickMonitor implements EventListener<ServerTickMonitorEvent> {
    public static double RAW_MSPT;

    @Override
    public @NotNull Class<ServerTickMonitorEvent> eventType() {
        return ServerTickMonitorEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull ServerTickMonitorEvent event) {
        RAW_MSPT = event.getTickMonitor().getTickTime();
        return Result.SUCCESS;
    }
}
