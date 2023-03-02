package com.joojn.meteoraddon.commands;

import com.joojn.meteoraddon.modules.HypixelGuessTheBuild;
import com.joojn.meteoraddon.utils.PlayerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import java.util.concurrent.CompletableFuture;

public class GuessTheBuilderCommand {


    public static void registerCommands(
            CommandDispatcher<FabricClientCommandSource> dispatcher,
            CommandRegistryAccess access
    )
    {
        dispatcher.register(
                ClientCommandManager.literal(
                                "guesser"
                        )
                        .then(
                                ClientCommandManager.literal("update")
                                        .executes(HypixelGuessTheBuild::updateWords)
                        )
                        .then(
                                ClientCommandManager.literal("info")
                                        .executes(HypixelGuessTheBuild::printInfo)
                        )
                        .then(
                                ClientCommandManager.literal("help")
                                        .executes(HypixelGuessTheBuild::printHelp)
                        )
        );

        dispatcher.register(
                ClientCommandManager.literal(
                        "guess"
                ).then(
                        ClientCommandManager.argument("word", StringArgumentType.string())
                                .suggests(GuessTheBuilderCommand::getSuggestions)
                                .executes((context) -> {

                                    String input = context.getArgument("word", String.class);

                                    PlayerUtil.sendChatMessage(input);

                                    return 0;
                                })
                )
        );
    }

    public static CompletableFuture<Suggestions> getSuggestions(
            CommandContext<FabricClientCommandSource> context,
            SuggestionsBuilder builder
    )
    {
        String input = context.getInput().substring(
                context.getInput().indexOf(" ") + 1
        ).replace("\"", "").toLowerCase();

        for(String s : HypixelGuessTheBuild.getFilteredWords())
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
