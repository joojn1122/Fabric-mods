package com.joojn.meteoraddon.commands;

import com.joojn.meteoraddon.modules.HypixelGuessTheBuild;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class GuessTheBuilderCommand {

    public static class Guess extends Command {

        public Guess() {
            super(
                    "guess",
                    "Automatically guesses the build in Hypixel's Guess The Build game, '/guesser help' for help"
            );
        }

        @Override
        public void build(LiteralArgumentBuilder<CommandSource> builder) {
            builder.then(
                    argument("word", StringArgumentType.string())
                            .suggests(this::getSuggestions)
                            .executes((context) -> {

                                String input = context.getArgument("word", String.class);

                                ChatUtils.sendPlayerMsg(input);

                                return SINGLE_SUCCESS;
                            })
            );
        }

        public CompletableFuture<Suggestions> getSuggestions(
                CommandContext<CommandSource> context,
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

    public static class Guesser extends Command{

        public Guesser() {
            super(
                    "guesser",
                    "Guesser info and help"
            );
        }

        private static final Text[] infoTexts = new Text[] {
                Text.empty(),
                Text.literal(HypixelGuessTheBuild.PREFIX.replace(":", "")),
                Text.empty(),
                Text.literal("§6/‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾\\"),
                Text.literal("§6|                       |"),
                Text.literal("§6|  §fVersion  §e1.0.0    §6|"),
                Text.literal("§6|  §fAuthor  §ejoojn   §6|"),
                Text.literal("§6|                       |"),
                Text.literal("§6|        ").append(Text.literal("§a§nGitHub:§r").setStyle(
                        Style.EMPTY.withClickEvent(new ClickEvent(
                                ClickEvent.Action.OPEN_URL, "https://github.com/joojn1122/Fabric-mods/tree/main/Hypixel-GuessTheBuild-Helper"
                        ))).append("       §6|")),
                Text.literal("§6|                       |"),
                Text.literal("§6\\______________/"),
                Text.empty()
        };

        @Override
        public void build(LiteralArgumentBuilder<CommandSource> builder) {
            builder.then(
                            literal("update")
                                    .executes((ctx) -> {
                                        info(
                                                HypixelGuessTheBuild.PREFIX + "Updating words.."
                                        );

                                        HypixelGuessTheBuild.words = HypixelGuessTheBuild.getWords();

                                        if(HypixelGuessTheBuild.words.length > 0)
                                        {
                                            info(
                                                    HypixelGuessTheBuild.PREFIX + "§aSuccessfully updated words!"
                                            );

                                            return 0;
                                        }
                                        else
                                        {
                                            info(
                                                    HypixelGuessTheBuild.PREFIX + "§cSomething went wrong while updating words.."
                                            );

                                            return SINGLE_SUCCESS;
                                        }
                                    })
                    )
                    .then(
                            literal("info")
                                    .executes((ctx) -> {
                                        for(Text text : infoTexts)
                                        {
                                            info(text);
                                        }

                                        return SINGLE_SUCCESS;
                                    })
                    )
                    .then(
                            literal("help")
                                    .executes((ctx) -> {
                                        info("/guess <word> §8- §aIsn't this enough help?");

                                        return SINGLE_SUCCESS;
                                    })
                    );
        }
    }
}
