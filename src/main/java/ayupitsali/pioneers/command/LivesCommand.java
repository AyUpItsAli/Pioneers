package ayupitsali.pioneers.command;

import ayupitsali.pioneers.Pioneers;
import ayupitsali.pioneers.data.LivesGroup;
import ayupitsali.pioneers.data.Pioneer;
import ayupitsali.pioneers.data.PioneersData;
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
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LivesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("lives").executes(LivesCommand::executeLives).then(CommandManager.literal("list").executes(LivesCommand::executeList))
                .then(CommandManager.literal("set").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.argument("player", GameProfileArgumentType.gameProfile()).suggests((context, builder) ->
                        CommandSource.suggestMatching(Pioneers.PIONEERS_DATA.get(context.getSource().getWorld().getScoreboard()).getPioneers().stream().map(Pioneer::getName), builder)
                ).then(CommandManager.argument("lives", IntegerArgumentType.integer(0, LivesGroup.GREEN.getMaxLives())).executes(context ->
                        executeSet(context, GameProfileArgumentType.getProfileArgument(context, "player").iterator().next(), IntegerArgumentType.getInteger(context, "lives"))
                )))).then(CommandManager.literal("reset").requires(source -> source.hasPermissionLevel(2)).executes(context ->
                        executeReset(context, LivesGroup.GREEN.getMaxLives())
                ).then(CommandManager.argument("lives", IntegerArgumentType.integer(0, LivesGroup.GREEN.getMaxLives())).executes(context ->
                        executeReset(context, IntegerArgumentType.getInteger(context, "lives"))
                ))));
    }

    public static int executeLives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Pioneer pioneer = PioneersData.getPioneer(context.getSource().getPlayerOrThrow());
        context.getSource().sendFeedback(() -> Text.translatable("commands.lives.query.success", new Object[]{pioneer.getLivesDisplay()}), false);
        return 1;
    }

    public static int executeList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<Pioneer> pioneers = Pioneers.PIONEERS_DATA.get(context.getSource().getWorld().getScoreboard()).getPioneers();
        Arrays.stream(LivesGroup.values()).forEach(livesGroup -> {
            List<Pioneer> groupPioneers = pioneers.stream().filter(pioneerData -> pioneerData.getLivesGroup().equals(livesGroup)).toList();
            context.getSource().sendFeedback(livesGroup::getListTitle, false);
            if (groupPioneers.isEmpty()) {
                context.getSource().sendFeedback(() -> Text.translatable("commands.lives.list.success.item", Text.translatable("commands.lives.list.success.item.empty").formatted(Formatting.GRAY).formatted(Formatting.ITALIC)), false);
            } else {
                groupPioneers.forEach(pioneer -> {
                    context.getSource().sendFeedback(() -> Text.translatable("commands.lives.list.success.item", Text.translatable("commands.lives.list.success.item.pioneer", new Object[]{pioneer.getDisplayName(), pioneer.getLivesDisplay()})), false);
                });
            }
        });
        return 1;
    }

    public static int executeSet(CommandContext<ServerCommandSource> context, GameProfile profile, int lives) throws CommandSyntaxException {
        PioneersData pioneersData = Pioneers.PIONEERS_DATA.get(context.getSource().getWorld().getScoreboard());
        if (!pioneersData.pioneerExists(profile)) {
            context.getSource().sendError(Text.translatable("commands.lives.set.failure"));
            return 0;
        }
        Pioneer pioneer = pioneersData.getPioneer(profile);
        pioneer.setLives(lives);
        if (context.getSource().isExecutedByPlayer() && context.getSource().getPlayerOrThrow().getGameProfile().equals(profile))
            context.getSource().sendFeedback(() -> Text.translatable("commands.lives.set.success.self", new Object[]{pioneer.getLivesDisplay()}), false);
        else
            context.getSource().sendFeedback(() -> Text.translatable("commands.lives.set.success.single", new Object[]{pioneer.getDisplayName(), pioneer.getLivesDisplay()}), false);
        return 1;
    }

    public static int executeReset(CommandContext<ServerCommandSource> context, int lives) throws CommandSyntaxException {
        Pioneers.PIONEERS_DATA.get(context.getSource().getWorld().getScoreboard()).getPioneers().forEach(pioneer -> pioneer.setLives(lives));
        Formatting formatting = LivesGroup.getGroupForLives(lives).getColourFormatting();
        context.getSource().sendFeedback(() -> Text.translatable("commands.lives.reset.success", Pioneer.getLivesText(lives, formatting)), false);
        return 1;
    }
}
