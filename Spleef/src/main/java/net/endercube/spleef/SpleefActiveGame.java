package net.endercube.spleef;

import net.endercube.Common.EndercubeActiveGame;
import net.endercube.Common.players.EndercubePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.TitlePart;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.TaskSchedule;

import java.util.Set;

public class SpleefActiveGame extends EndercubeActiveGame {
    public SpleefActiveGame(Instance instance, Set<EndercubePlayer> players) {
        super(instance, players);

        players.forEach((player) -> player.setInstance(instance, instance.getTag(Tag.Transient("spawnPos"))));

        // Init tags
        instance.setTag(Tag.Integer("nextStartingCountdown"), 5);
        instance.setTag(Tag.String("gameState"), "STARTING");

        // Countdown
        instance.scheduler().submitTask(() -> {
            for (EndercubePlayer player : players) {
                int secondsLeft = instance.getTag(Tag.Integer("nextStartingCountdown"));

                if (secondsLeft == 0) {
                    player.sendTitlePart(TitlePart.TITLE, Component.text("GO!").color(NamedTextColor.GOLD));
                    startGame();
                    // Stop schedule after that
                    return TaskSchedule.stop();
                }

                // Send standard countdown title
                player.sendTitlePart(TitlePart.TITLE, Component.text(secondsLeft).color(NamedTextColor.GOLD));

                // Update tag
                instance.setTag(Tag.Integer("nextStartingCountdown"), secondsLeft - 1);
            }
            return TaskSchedule.seconds(1);
        });
    }

    @Override
    public void onPlayerLeave() {

    }

    private void startGame() {

    }
}
