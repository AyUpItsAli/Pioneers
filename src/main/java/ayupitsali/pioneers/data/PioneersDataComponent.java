package ayupitsali.pioneers.data;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class PioneersDataComponent implements AutoSyncedComponent {
    private final Scoreboard provider;
    private final Map<String, PioneerData> pioneers = new HashMap<>();

    public PioneersDataComponent(Scoreboard provider) {
        this.provider = provider;
    }

    public void sync() {
        ModComponents.PIONEERS_DATA.sync(provider);
    }

    public PioneerData getPioneerData(String id) {
        if (!pioneers.containsKey(id)) {
            pioneers.put(id, new PioneerData(this));
            sync();
        }
        return pioneers.get(id);
    }

    // Must accept player as LivingEntity instead of PlayerEntity, so that MixinPlayerEntity can be passed
    public static PioneerData getPioneerData(LivingEntity player) {
        if (player instanceof PlayerEntity playerEntity) {
            PioneersDataComponent component = ModComponents.PIONEERS_DATA.get(playerEntity.getScoreboard());
            return component.getPioneerData(playerEntity.getGameProfile().getId().toString());
        }
        return null;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        pioneers.clear();
        tag.getList("pioneers", NbtElement.COMPOUND_TYPE).forEach((element -> {
            if (element instanceof NbtCompound compound) {
                String id = compound.getString("id");
                LivesGroup livesGroup = LivesGroup.values()[compound.getInt("livesGroup")];
                int lives = compound.getInt("lives");
                pioneers.put(id, new PioneerData(this, livesGroup, lives));
            }
        }));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList list = new NbtList();
        pioneers.forEach((id, pioneerData) -> {
            NbtCompound compound = new NbtCompound();
            compound.putString("id", id);
            compound.putInt("livesGroup", pioneerData.getLivesGroup().ordinal());
            compound.putInt("lives", pioneerData.getLives());
            list.add(compound);
        });
        tag.put("pioneers", list);
    }
}
