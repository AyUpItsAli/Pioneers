package ayupitsali.pioneers.data;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum LivesGroup {
    GREEN(3, Formatting.GREEN),
    YELLOW(3, Formatting.YELLOW),
    RED(3, Formatting.RED),
    GHOST(0, Formatting.GRAY);

    private final int lives;
    private final Formatting colourFormatting;

    LivesGroup(int lives, Formatting colourFormatting) {
        this.lives = lives;
        this.colourFormatting = colourFormatting;
    }

    public int getLives() {
        return lives;
    }

    public Formatting getColourFormatting() {
        return colourFormatting;
    }

    public int getMaxLives() {
        return switch (this) {
            case GREEN -> RED.getLives() + YELLOW.getLives() + GREEN.getLives();
            case YELLOW -> RED.getLives() + YELLOW.getLives();
            case RED -> RED.getLives();
            case GHOST -> 0;
        };
    }

    public int getMinLives() {
        return switch (this) {
            case GREEN -> RED.getLives() + YELLOW.getLives() + 1;
            case YELLOW -> RED.getLives() + 1;
            case RED -> 1;
            case GHOST -> 0;
        };
    }

    public MutableText getDisplayName() {
        return switch (this) {
            case GREEN -> Text.literal("Green").formatted(colourFormatting).formatted(Formatting.BOLD);
            case YELLOW -> Text.literal("Yellow").formatted(colourFormatting).formatted(Formatting.BOLD);
            case RED -> Text.literal("Red").formatted(colourFormatting).formatted(Formatting.BOLD);
            case GHOST -> Text.literal("Ghost").formatted(colourFormatting).formatted(Formatting.BOLD);
        };
    }
}
