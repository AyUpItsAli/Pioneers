package ayupitsali.pioneers;

import eu.midnightdust.lib.config.MidnightConfig;

public class PioneersConfig extends MidnightConfig {
    public enum DefaultColour { GREEN, YELLOW, RED }
    @Entry(category = "lives") public static DefaultColour DEFAULT_COLOUR = DefaultColour.GREEN;
    @Entry(category = "lives", min = 1) public static int GREEN_LIVES = 3;
    @Entry(category = "lives", min = 1) public static int YELLOW_LIVES = 3;
    @Entry(category = "lives", min = 1) public static int RED_LIVES = 3;
    @Entry(category = "lives", min = 1) public static int LIVES_LOST_ON_DEATH = 1;
    @Entry(category = "lives", min = 1) public static int LIVES_GAINED_ON_KILL = 1;
}
