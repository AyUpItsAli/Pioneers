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
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Collection;

public class LivesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("lives").executes(context ->
                executeGet(context, context.getSource().getPlayerOrThrow().getGameProfile())
        ).then(CommandManager.literal("get").then(CommandManager.argument("player", GameProfileArgumentType.gameProfile()).suggests(((context, builder) ->
                CommandSource.suggestMatching(Pioneers.PIONEERS_DATA.get(context.getSource().getWorld().getScoreboard()).getPioneerNames(), builder)
        )).executes(context ->
                executeGet(context, GameProfileArgumentType.getProfileArgument(context, "player").iterator().next())
        ))).then(CommandManager.literal("set").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.argument("players", EntityArgumentType.players()).then(CommandManager.argument("lives", IntegerArgumentType.integer(0, LivesGroup.GREEN.getMaxLives())).executes(context ->
                executeSet(context, IntegerArgumentType.getInteger(context, "lives"), EntityArgumentType.getPlayers(context, "players"))
        )))).then(CommandManager.literal("reset").executes(LivesCommand::executeReset)));
    }

    public static int executeGet(CommandContext<ServerCommandSource> context, GameProfile profile) throws CommandSyntaxException {
        PioneersDataComponent component = Pioneers.PIONEERS_DATA.get(context.getSource().getWorld().getScoreboard());
        if (!component.pioneerExists(profile)){
            context.getSource().sendError(Text.translatable("commands.lives.get.failure"));
            return 0;
        }
        PioneerData pioneerData = component.getPioneerData(profile);
        if (context.getSource().getPlayerOrThrow().getGameProfile().equals(profile))
            context.getSource().sendFeedback(() -> Text.translatable("commands.lives.get.success.self", new Object[]{pioneerData.getLivesDisplay()}), false);
        else {
            MutableText displayName = Text.literal(profile.getName()).formatted(pioneerData.getLivesGroup().getColourFormatting());
            context.getSource().sendFeedback(() -> Text.translatable("commands.lives.get.success.single", new Object[]{displayName, pioneerData.getLivesDisplay()}), false);
        }
        return 1;
    }

    public static int executeSet(CommandContext<ServerCommandSource> context, int lives, Collection<ServerPlayerEntity> playerEntities) throws CommandSyntaxException {
        if (playerEntities.size() == 1) {
            ServerPlayerEntity playerEntity = playerEntities.iterator().next();
            PioneerData pioneerData = PioneersDataComponent.getPioneerData(playerEntity);
            pioneerData.setLives(lives);
            if (context.getSource().getPlayerOrThrow().equals(playerEntity))
                context.getSource().sendFeedback(() -> Text.translatable("commands.lives.set.success.self", new Object[]{pioneerData.getLivesDisplay()}), false);
            else
                context.getSource().sendFeedback(() -> Text.translatable("commands.lives.set.success.single", new Object[]{playerEntity.getDisplayName(), pioneerData.getLivesDisplay()}), false);
            return 1;
        } else {
            for (ServerPlayerEntity playerEntity : playerEntities)
                PioneersDataComponent.getPioneerData(playerEntity).setLives(lives);
            PioneerData pioneerData = PioneersDataComponent.getPioneerData(playerEntities.iterator().next());
            context.getSource().sendFeedback(() -> Text.translatable("commands.lives.set.success.multiple", new Object[]{playerEntities.size(), pioneerData.getLivesDisplay()}), false);
            return playerEntities.size();
        }
    }

    public static int executeReset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(() -> Text.literal("Reset command"), false);
        return 1;
    }
}
