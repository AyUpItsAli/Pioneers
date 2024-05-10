package ayupitsali.pioneers.data;

import ayupitsali.pioneers.Pioneers;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.minecraft.util.Identifier;

public class ModComponents implements ScoreboardComponentInitializer {
    public static final ComponentKey<PioneersDataComponent> PIONEERS_DATA = ComponentRegistry.getOrCreate(
            new Identifier(Pioneers.MOD_ID, "pioneers_data"), PioneersDataComponent.class);

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(PIONEERS_DATA, (scoreboard, server) -> new PioneersDataComponent(scoreboard));
    }
}
