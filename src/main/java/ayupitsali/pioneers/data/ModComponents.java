package ayupitsali.pioneers.data;

import ayupitsali.pioneers.Pioneers;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

public class ModComponents implements EntityComponentInitializer {
    public static final ComponentKey<PioneerData> PIONEER_DATA = ComponentRegistry.getOrCreate(
            new Identifier(Pioneers.MOD_ID, "pioneer_data"), PioneerData.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(PIONEER_DATA, PioneerData::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
