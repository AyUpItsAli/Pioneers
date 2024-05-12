package ayupitsali.pioneers.data;

import ayupitsali.pioneers.Pioneers;
import com.mojang.authlib.GameProfile;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class PioneersDataComponent implements AutoSyncedComponent {
    private final Scoreboard provider;
    private final Map<String, PioneerData> pioneers = new HashMap<>();

    public PioneersDataComponent(Scoreboard provider) {
        this.provider = provider;
    }

    public void sync() {
        Pioneers.PIONEERS_DATA.sync(provider);
    }

    public boolean pioneerExists(GameProfile profile) {
        return pioneers.containsKey(profile.getId().toString());
    }

    public PioneerData getPioneerData(GameProfile profile) {
        String id = profile.getId().toString();
        if (!pioneerExists(profile)) {
            pioneers.put(id, new PioneerData(this, profile.getName()));
            sync();
        }
        return pioneers.get(id);
    }

    public Stream<String> getPioneerNames() {
        return pioneers.values().stream().map(PioneerData::getName);
    }

    // Must accept player as LivingEntity instead of PlayerEntity, so that MixinPlayerEntity can be passed
    public static PioneerData getPioneerData(LivingEntity player) {
        if (player instanceof PlayerEntity playerEntity) {
            return Pioneers.PIONEERS_DATA.get(playerEntity.getScoreboard()).getPioneerData(playerEntity.getGameProfile());
        }
        return null;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        pioneers.clear();
        tag.getList("pioneers", NbtElement.COMPOUND_TYPE).forEach((element -> {
            if (element instanceof NbtCompound compound) {
                String id = compound.getString("id");
                String name = compound.getString("name");
                LivesGroup livesGroup = LivesGroup.values()[compound.getInt("livesGroup")];
                int lives = compound.getInt("lives");
                pioneers.put(id, new PioneerData(this, name, livesGroup, lives));
            }
        }));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList list = new NbtList();
        pioneers.forEach((id, pioneerData) -> {
            NbtCompound compound = new NbtCompound();
            compound.putString("id", id);
            compound.putString("name", pioneerData.getName());
            compound.putInt("livesGroup", pioneerData.getLivesGroup().ordinal());
            compound.putInt("lives", pioneerData.getLives());
            list.add(compound);
        });
        tag.put("pioneers", list);
    }
}
