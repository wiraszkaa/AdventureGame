package jakubwiraszka.observable;

import jakubwiraszka.gamefiles.Location;

public interface Listener {
    void update(double value1, double value2, int value3, int value4, Location location);
}
