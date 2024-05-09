package ayupitsali.pioneers.data;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

public class PioneerData implements Component {
    private int lives = LivesGroup.GREEN.getTotalLives();

    public int getLives() {
        return lives;
    }

    public int setLives(int lives) {
        this.lives = Math.min(Math.max(lives, 0), LivesGroup.GREEN.getTotalLives());
        return this.lives;
    }

    public void addLives(int amount) {
        setLives(lives + amount);
    }

    public LivesGroup getLivesGroup() {
        if (lives <= 0) return LivesGroup.GHOST;
        if (lives <= LivesGroup.RED.getTotalLives()) return LivesGroup.RED;
        if (lives <= LivesGroup.YELLOW.getTotalLives()) return LivesGroup.YELLOW;
        return LivesGroup.GREEN;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        lives = tag.getInt("lives");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("lives", lives);
    }
}
