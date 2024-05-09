package ayupitsali.pioneers.data;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum LivesGroup {
    GREEN(3),
    YELLOW(3),
    RED(3),
    GHOST(0);

    private final int lives;

    LivesGroup(int lives) {
        this.lives = lives;
    }

    public int getLives() {
        return this.lives;
    }

    public int getTotalLives() {
        return switch (this) {
            case GREEN -> this.lives + YELLOW.getLives() + RED.getLives();
            case YELLOW -> this.lives + RED.getLives();
            case RED -> this.lives;
            case GHOST -> 0;
        };
    }

    public MutableText getDisplayName() {
        return switch (this) {
            case GREEN -> Text.literal("Green").formatted(Formatting.GREEN);
            case YELLOW -> Text.literal("Yellow").formatted(Formatting.YELLOW);
            case RED -> Text.literal("Red").formatted(Formatting.RED);
            case GHOST -> Text.literal("Ghost").formatted(Formatting.DARK_GRAY);
        };
    }
}
