package ayupitsali.pioneers.command;

import ayupitsali.pioneers.Pioneers;
import ayupitsali.pioneers.data.LivesGroup;
import ayupitsali.pioneers.data.PioneerData;
import ayupitsali.pioneers.data.PioneersDataComponent;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class LivesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("lives").executes(LivesCommand::executeLives).then(CommandManager.literal("list").executes(LivesCommand::executeList))
                .then(CommandManager.literal("set").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.argument("player", GameProfileArgumentType.gameProfile()).suggests((context, builder) ->
                        CommandSource.suggestMatching(Pioneers.PIONEERS_DATA.get(context.getSource().getWorld().getScoreboard()).getPioneerNames(), builder)
                ).then(CommandManager.argument("lives", IntegerArgumentType.integer(0, LivesGroup.GREEN.getMaxLives())).executes(context ->
                        executeSet(context, GameProfileArgumentType.getProfileArgument(context, "player").iterator().next(), IntegerArgumentType.getInteger(context, "lives"))
                )))).then(CommandManager.literal("reset").executes(LivesCommand::executeReset)));
    }

    public static int executeLives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PioneerData pioneerData = PioneersDataComponent.getPioneerData(context.getSource().getPlayerOrThrow());
        context.getSource().sendFeedback(() -> Text.translatable("commands.lives.success", new Object[]{pioneerData.getLivesDisplay()}), false);
        return 1;
    }

    public static int executeList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(() -> Text.literal("List command"), false);
        return 1;
    }

    public static int executeSet(CommandContext<ServerCommandSource> context, GameProfile profile, int lives) throws CommandSyntaxException {
        PioneerData pioneerData = Pioneers.PIONEERS_DATA.get(context.getSource().getWorld().getScoreboard()).getPioneerData(profile);
        pioneerData.setLives(lives);
        if (context.getSource().getPlayerOrThrow().getGameProfile().equals(profile))
            context.getSource().sendFeedback(() -> Text.translatable("commands.lives.set.success.self", new Object[]{pioneerData.getLivesDisplay()}), false);
        else
            context.getSource().sendFeedback(() -> Text.translatable("commands.lives.set.success.single", new Object[]{pioneerData.getDisplayName(), pioneerData.getLivesDisplay()}), false);
        return 1;
    }

    public static int executeReset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(() -> Text.literal("Reset command"), false);
        return 1;
    }
}
