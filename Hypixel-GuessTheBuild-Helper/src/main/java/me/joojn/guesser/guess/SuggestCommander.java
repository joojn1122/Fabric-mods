package me.joojn.guesser.guess;

import me.joojn.guesser.GuesserMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import java.util.concurrent.CompletableFuture;

public class SuggestCommander implements SuggestionProvider<FabricClientCommandSource> {

    public static void register(
            CommandDispatcher<FabricClientCommandSource> dispatcher,
            CommandRegistryAccess commandRegistryAccess
    )
    {
        dispatcher.register(
                ClientCommandManager.literal(
                        "guesser"
                )
                        .then(
                                ClientCommandManager.literal("update")
                                        .executes(GuessTheBuild::updateWords)
                        )
                        .then(
                              ClientCommandManager.literal("info")
                                      .executes(GuessTheBuild::printInfo)
                        )
                        .then(
                                ClientCommandManager.literal("help")
                                        .executes(GuessTheBuild::printHelp)
                        )
        );

        dispatcher.register(
                ClientCommandManager.literal(
                        "guess"
                ).then(
                        ClientCommandManager.argument("word", StringArgumentType.string())
                                .suggests(new SuggestCommander())
                                .executes(SuggestCommander::execute)
                )
        );
    }

    private static int execute(CommandContext<FabricClientCommandSource> context)
    {
        String input = context.getArgument("word", String.class);

        GuesserMod.sendChatMessage(input);

        return 0;
    }


    @Override
    public CompletableFuture<Suggestions> getSuggestions(
            CommandContext<FabricClientCommandSource> context,
            SuggestionsBuilder builder
    )
    {
        String input = context.getInput().substring(
                context.getInput().indexOf(" ") + 1
                ).replace("\"", "").toLowerCase();

        for(String s : GuessTheBuild.getFilteredWords())
        {
            if(!(
                    s.toLowerCase().startsWith(input)
                    || (s.contains(" ") && s.split(" ")[1].startsWith(input))
            )) continue;

            if(s.contains(" "))
            {
                builder.suggest('"' + s + '"');
            }
            else builder.suggest(s);
        }

        return builder.buildFuture();
    }

}
