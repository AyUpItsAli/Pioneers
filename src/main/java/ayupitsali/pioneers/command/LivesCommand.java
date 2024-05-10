package ayupitsali.pioneers.command;

import ayupitsali.pioneers.data.LivesGroup;
import ayupitsali.pioneers.data.ModComponents;
import ayupitsali.pioneers.data.PioneerData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class LivesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("lives").executes(context ->
                executeGet(context, context.getSource().getPlayerOrThrow())
        ).then(CommandManager.literal("get").executes(context ->
                executeGet(context, context.getSource().getPlayerOrThrow())
        ).then(CommandManager.argument("player", EntityArgumentType.player()).executes(context ->
                executeGet(context, EntityArgumentType.getPlayer(context, "player"))
        ))).then(CommandManager.literal("set").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.argument("players", EntityArgumentType.players()).then(CommandManager.argument("lives", IntegerArgumentType.integer(0, LivesGroup.GREEN.getMaxLives())).executes(context ->
                executeSet(context, IntegerArgumentType.getInteger(context, "lives"), EntityArgumentType.getPlayers(context, "players"))
        )))).then(CommandManager.literal("reset").executes(LivesCommand::executeReset)));
    }

    public static int executeGet(CommandContext<ServerCommandSource> context, ServerPlayerEntity playerEntity) throws CommandSyntaxException {
        PioneerData playerData = ModComponents.PIONEER_DATA.get(playerEntity);
        if (context.getSource().getPlayerOrThrow().equals(playerEntity))
            context.getSource().sendFeedback(() -> Text.translatable("commands.lives.get.success.self", new Object[]{playerData.getLivesDisplay()}), false);
        else
            context.getSource().sendFeedback(() -> Text.translatable("commands.lives.get.success.single", new Object[]{playerEntity.getDisplayName(), playerData.getLivesDisplay()}), false);
        return 1;
    }

    public static int executeSet(CommandContext<ServerCommandSource> context, int lives, Collection<ServerPlayerEntity> playerEntities) throws CommandSyntaxException {
        if (playerEntities.size() == 1) {
            ServerPlayerEntity playerEntity = playerEntities.iterator().next();
            PioneerData playerData = ModComponents.PIONEER_DATA.get(playerEntity);
            playerData.setLives(lives);
            if (context.getSource().getPlayerOrThrow().equals(playerEntity))
                context.getSource().sendFeedback(() -> Text.translatable("commands.lives.set.success.self", new Object[]{playerData.getLivesDisplay()}), false);
            else
                context.getSource().sendFeedback(() -> Text.translatable("commands.lives.set.success.single", new Object[]{playerEntity.getDisplayName(), playerData.getLivesDisplay()}), false);
            return 1;
        } else {
            for (ServerPlayerEntity playerEntity : playerEntities)
                ModComponents.PIONEER_DATA.get(playerEntity).setLives(lives);
            PioneerData playerData = ModComponents.PIONEER_DATA.get(playerEntities.iterator().next());
            context.getSource().sendFeedback(() -> Text.translatable("commands.lives.set.success.multiple", new Object[]{playerEntities.size(), playerData.getLivesDisplay()}), false);
            return playerEntities.size();
        }
    }

    public static int executeReset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(() -> Text.literal("Reset command"), false);
        return 1;
    }
}
