package ayupitsali.pioneers.data;

import ayupitsali.pioneers.PioneersConfig;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Pioneer {
    private final PioneerData component;
    private final String name;
    private LivesGroup livesGroup;
    private int lives;

    public Pioneer(PioneerData component, String name, LivesGroup livesGroup, int lives) {
        this.component = component;
        this.name = name;
        this.livesGroup = livesGroup;
        this.lives = lives;
    }

    public Pioneer(PioneerData component, String name, LivesGroup livesGroup) {
        this(component, name, livesGroup, livesGroup.getMaxLives());
    }

    public Pioneer(PioneerData component, String name) {
        this(component, name, LivesGroup.getDefaultGroup());
    }

    public String getName() {
        return name;
    }

    public MutableText getDisplayName() {
        return Text.literal(name).formatted(livesGroup.getColourFormatting());
    }

    public LivesGroup getLivesGroup() {
        return livesGroup;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.min(Math.max(lives, 0), LivesGroup.GREEN.getMaxLives());
        livesGroup = LivesGroup.getGroupForLives(this.lives);
        component.sync();
    }

    public void addLives(int amount) {
        setLives(lives + amount);
    }

    public MutableText getLivesDisplay() {
        return Pioneer.getLivesText(lives, livesGroup.getColourFormatting());
    }

    public static MutableText getLivesText(int lives, Formatting livesFormatting) {
        MutableText livesText = Text.literal(Integer.toString(lives)).formatted(livesFormatting);
        return lives == 1 ? Text.translatable("lives.display.single", new Object[]{livesText}) : Text.translatable("lives.display.multiple", new Object[]{livesText});
    }
}
