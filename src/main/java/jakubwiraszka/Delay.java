package jakubwiraszka;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class Delay {

    public static void createDelay(int seconds, EventHandler<ActionEvent> eventHandler) {
        Timeline timeLine = new Timeline(
                new KeyFrame(Duration.seconds(seconds), eventHandler)
        );
        timeLine.play();
    }
}
