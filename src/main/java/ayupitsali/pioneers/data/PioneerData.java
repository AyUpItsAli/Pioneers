package ayupitsali.pioneers.data;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PioneerData {
    private final PioneersDataComponent component;
    private LivesGroup livesGroup;
    private int lives;

    public PioneerData(PioneersDataComponent component, LivesGroup livesGroup, int lives) {
        this.component = component;
        this.livesGroup = livesGroup;
        this.lives = lives;
    }

    public PioneerData(PioneersDataComponent component, LivesGroup livesGroup) {
        this(component, livesGroup, livesGroup.getMaxLives());
    }

    public PioneerData(PioneersDataComponent component) {
        this(component, LivesGroup.GREEN);
    }

    public LivesGroup getLivesGroup() {
        return livesGroup;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.min(Math.max(lives, 0), LivesGroup.GREEN.getMaxLives());
        if (this.lives >= LivesGroup.GREEN.getMinLives()) {
            livesGroup = LivesGroup.GREEN;
        } else if (this.lives >= LivesGroup.YELLOW.getMinLives()) {
            livesGroup = LivesGroup.YELLOW;
        } else if (this.lives >= LivesGroup.RED.getMinLives()) {
            livesGroup = LivesGroup.RED;
        } else {
            livesGroup = LivesGroup.GHOST;
        }
        ModComponents.PIONEERS_DATA.sync(this.component.getProvider());
    }

    public void addLives(int amount) {
        setLives(lives + amount);
    }

    public MutableText getLivesDisplay() {
        return PioneerData.getLivesText(lives, livesGroup.getColourFormatting());
    }

    public static MutableText getLivesText(int lives, Formatting livesFormatting) {
        MutableText livesText = Text.literal(Integer.toString(lives)).formatted(livesFormatting);
        return lives == 1 ? Text.translatable("lives.display.single", new Object[]{livesText}) : Text.translatable("lives.display.multiple", new Object[]{livesText});
    }
}
