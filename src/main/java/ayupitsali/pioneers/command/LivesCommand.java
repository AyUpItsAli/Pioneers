package ayupitsali.pioneers.command;

import ayupitsali.pioneers.data.ModComponents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class LivesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("pioneers").then(CommandManager.literal("lives")
                .then(CommandManager.literal("get").then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes((commandContext) -> execute(commandContext, EntityArgumentType.getPlayer(commandContext, "player")))))));
    }

    public static int execute(CommandContext<ServerCommandSource> context, ServerPlayerEntity playerEntity) throws CommandSyntaxException {
        int lives = ModComponents.PIONEER_DATA.get(playerEntity).getLives();
        context.getSource().sendFeedback(() -> Text.literal(playerEntity.getName().getString() + " has " + lives + " lives"), false);
        return 1;
    }
}
