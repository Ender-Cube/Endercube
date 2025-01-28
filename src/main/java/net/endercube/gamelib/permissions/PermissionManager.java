package net.endercube.gamelib.permissions;

import net.endercube.global.EndercubePlayer;

import java.util.HashMap;
import java.util.UUID;

public class PermissionManager {
    private final HashMap<UUID, PermissionLevel> player_permissions = new HashMap<>();

    public PermissionManager() {

    }

    public void setPermission(EndercubePlayer player, PermissionLevel permission) {
        setPermission(player.getUuid(), permission);
    }

    public void setPermission(UUID uuid, PermissionLevel permission) {
        player_permissions.put(uuid, permission);
    }

    public boolean hasPermission(EndercubePlayer player, PermissionLevel permission) {
        if (permission == PermissionLevel.DEFAULT) {
            return true;
        }
        return player_permissions.containsKey(player.getUuid());
    }

    public PermissionLevel getPermission(EndercubePlayer player) {
        PermissionLevel level = player_permissions.get(player.getUuid());

        if (level == null) {
            return PermissionLevel.DEFAULT;
        }
        return level;
    }
}
