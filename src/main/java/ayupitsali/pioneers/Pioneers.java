package ayupitsali.pioneers;

import ayupitsali.pioneers.command.LivesCommand;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pioneers implements ModInitializer {
	public static final String MOD_ID = "pioneers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initialising Pioneers!");
		CommandRegistrationCallback.EVENT.register(LivesCommand::register);
	}
}