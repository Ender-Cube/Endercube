package net.endercube.spleef.minigame;

import net.endercube.common.database.AbstractDatabase;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.JedisPooled;

import java.util.UUID;

public class SpleefDatabase extends AbstractDatabase {
    /**
     * An Endercube database
     *
     * @param jedis     A {@code JedisPooled} to get jedis instances from
     * @param nameSpace The prefix for all keys, does not need a colon on the end
     */
    public SpleefDatabase(JedisPooled jedis, String nameSpace) {
        super(jedis, nameSpace);
    }

    public void addWonGame(Player player) {
        jedis.incr(nameSpace + "wonGames:" + player.getUuid());
    }

    public void addLostGame(Player player) {
        jedis.incr(nameSpace + "lostGames:" + player.getUuid());
    }

    public int getWonGames(Player player) {
        return getWonGames(player.getUuid());
    }

    public int getWonGames(UUID playerUUID) {
        @Nullable String wonGames = jedis.get(nameSpace + "wonGames:" + playerUUID.toString());
        if (wonGames == null) {
            return 0;
        }
        return Integer.parseInt(wonGames);
    }

    public int getLostGames(Player player) {
        return getLostGames(player.getUuid());
    }

    public int getLostGames(UUID playerUUID) {
        @Nullable String lostGames = jedis.get(nameSpace + "lostGames:" + playerUUID.toString());
        if (lostGames == null) {
            return 0;
        }
        return Integer.parseInt(lostGames);
    }

    public int getAllGames(Player player) {
        return getWonGames(player) + getLostGames(player);
    }

    public int getAllGames(UUID playerUUID) {
        return getWonGames(playerUUID) + getLostGames(playerUUID);
    }
}
