package com.joojn.meteoraddon;

import com.joojn.meteoraddon.commands.GuessTheBuilderCommand;
import com.joojn.meteoraddon.hud.CodeViewer;
import com.joojn.meteoraddon.modules.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mojang.brigadier.arguments.StringArgumentType.string;


public class MeteorClientUtils extends MeteorAddon {

    // public static final Category BLATANT_CATEGORY = new Category("Blatant", Items.BARRIER.getDefaultStack());
    public static final Category FUN_CATEGORY = new Category("Fun", Items.TNT.getDefaultStack());
    public static final Logger LOGGER = LoggerFactory.getLogger("MeteorClientUtils");

    @Override
    public void onInitialize() {
        Modules.get().add(new PacketLogger());
        Modules.get().add(new ChatReaction());
        Modules.get().add(new FastBreak());
        Modules.get().add(new AutoMinecart());

        Modules.get().add(Misplace.INSTANCE);
        Modules.get().add(ClientOffset.INSTANCE);
        Modules.get().add(HypixelGuessTheBuild.INSTANCE);
        Modules.get().add(PlayerHider.INSTANCE);
        Modules.get().add(Sidebar.INSTANCE);

        Commands.get().add(new GuessTheBuilderCommand.Guess());
        Commands.get().add(new GuessTheBuilderCommand.Guesser());

        Commands.get().add(new Command("viewcode", "Opens the code viewer.") {
            @Override
            public void build(LiteralArgumentBuilder<CommandSource> builder) {
                builder.then(argument("class", string())
                        .suggests((context, sb) -> CommandSource.suggestMatching(new String[] {
                                "com.joojn.meteoraddon.hud.CodeViewer"
                        }, sb))
                        .executes(context -> {

                    String className = context.getArgument("class", String.class);

                    try {
                        CodeViewer codeViewer = new CodeViewer(className);

                        mc.send(() -> mc.setScreen(codeViewer));

                        info("§aOpened code viewer for class: " + className);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        info("§cCould not find class: " + className);

                        return -1;
                    }

                    return 1;
                }));
            }
        });

        // Hud.get().register(CodeViewer.INFO);
    }

    @Override
    public String getPackage() {
        return "com.joojn.meteoraddon";
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(FUN_CATEGORY);
    }
}
