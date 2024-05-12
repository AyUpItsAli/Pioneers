package ayupitsali.pioneers;

import ayupitsali.pioneers.command.LivesCommand;
import ayupitsali.pioneers.data.PioneersData;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pioneers implements ModInitializer, ScoreboardComponentInitializer {
	public static final String MOD_ID = "pioneers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ComponentKey<PioneersData> PIONEERS_DATA = ComponentRegistry.getOrCreate(
			new Identifier(Pioneers.MOD_ID, "pioneers_data"), PioneersData.class);

	@Override
	public void onInitialize() {
		LOGGER.info("Initialising Pioneers!");
		CommandRegistrationCallback.EVENT.register(LivesCommand::register);
	}

	@Override
	public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
		registry.registerScoreboardComponent(Pioneers.PIONEERS_DATA, (scoreboard, server) -> new PioneersData(scoreboard));
	}
}