package ayupitsali.pioneers.data;

import net.minecraft.nbt.NbtCompound;

public class PioneerData implements PioneerDataComponent {
    private int lives = 9;

    @Override
    public int getLives() {
        return this.lives;
    }

    @Override
    public void setLives(int lives) {
        this.lives = lives;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {

    }

    @Override
    public void writeToNbt(NbtCompound tag) {

    }
}
