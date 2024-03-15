package net.endercube.common.utils;

import com.google.gson.JsonObject;
import net.endercube.common.exceptions.ServiceNotAvailableException;
import net.endercube.common.exceptions.UsernameDoesNotExistException;
import net.minestom.server.utils.mojang.MojangUtils;

import java.util.UUID;

public class UUIDUtils {

    public static UUID getUUID(String playerUsername) throws ServiceNotAvailableException, UsernameDoesNotExistException {
        JsonObject response = MojangUtils.fromUsername(playerUsername);

        if (response == null) {
            throw new ServiceNotAvailableException("The Mojang API is down");
        }

        if (response.get("errorMessage") != null) {
            throw new UsernameDoesNotExistException("The username " + playerUsername + " does not exist");
        }

        // Thanks stackoverflow: https://stackoverflow.com/a/19399768/13247146
        return UUID.fromString(
                response.get("id")
                        .getAsString()
                        .replaceFirst(
                                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                                "$1-$2-$3-$4-$5"
                        )
        );
    }
}
