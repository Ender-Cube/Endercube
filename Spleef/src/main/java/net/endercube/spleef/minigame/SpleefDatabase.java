package net.endercube.spleef.minigame;

import net.endercube.Common.database.AbstractDatabase;
import net.minestom.server.entity.Player;
import redis.clients.jedis.JedisPooled;

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
        return Integer.parseInt(jedis.get(nameSpace + "wonGames:" + player.getUuid()));
    }

    public int getLostGames(Player player) {
        return Integer.parseInt(jedis.get(nameSpace + "lostGames:" + player.getUuid()));
    }

    public int getAllGames(Player player) {
        return getWonGames(player) + getLostGames(player);
    }
}
