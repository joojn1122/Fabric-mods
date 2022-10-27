package me.joojn.guesser.guess;

import me.joojn.guesser.GuesserMod;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuessTheBuild {

    public static final String PREFIX = "§8[§6Guess The Builder Helper§8]: §f";

    // regex
    private static final Pattern originalSizePattern = Pattern.compile("(?<=\\[).*(?=])");

    public static boolean inGame = false;
    private static boolean showAllWords = true;

    private static final List<String> filteredWords = new ArrayList<>();
    public static String[] words = getWords();


    static{
        System.out.printf("Found words %s\n", Arrays.toString(words));
    }

    private static String[] getWords()
    {
        File file = new File("guesser-words.csv");

        if(file.exists())
        {
            try
            {
                GuesserMod.LOGGER.info("Trying to load words from '%s'".formatted(file.getName()));

                return FileUtils.readFileToString(file, StandardCharsets.UTF_8).split(";");
            }
            catch (IOException e)
            {
                GuesserMod.LOGGER.error("Something went wrong while trying to load data from '%s'".formatted(file.getName()));

                e.printStackTrace();
            }
        }

        try
        {
            URL originalSite = new URL("https://gtb.cyberfla.me/wordlist/wordlist.js");
            GuesserMod.LOGGER.info("Trying to load words from %s".formatted(originalSite.getPath()));

            String data = IOUtils.toString(originalSite, StandardCharsets.UTF_8)
                    .replace("\n", "")
                    .replace("\"", "")
                    .replace("\t", "")
                    .replace("\r", "");

            Matcher match = originalSizePattern.matcher(data);
            if(!match.find()) throw new IOException("Something went wrong while searching for array..");

            return match.group(0).split(",");
        }
        catch (IOException e)
        {
            GuesserMod.LOGGER.error("Could not load words from original site..");

            e.printStackTrace();
        }

        try
        {
            URL url = new URL("https://raw.githubusercontent.com/joojn1122/Fabric-mods/main/Hypixel-GuessTheBuild-Helper/words.csv");

            GuesserMod.LOGGER.info("Trying to load words from %s".formatted(url.getPath()));

            return IOUtils.toString(url, StandardCharsets.UTF_8).split(";");
        }
        catch (IOException e)
        {
            GuesserMod.LOGGER.error("Could not load words from github..");

            e.printStackTrace();
        }

        return new String[0];
    }

    private static String theme_ = "";

    public static void setTheme(String theme)
    {
        if(Objects.equals(theme, theme_)) return;

        theme_ = theme;

        if(theme != null)
        {
            filteredWords.clear();
            showAllWords = false;

            GuesserMod.addChatMessage("§aSetting current search to '§e%s§a'".formatted(theme));

            Pattern themePattern = Pattern.compile(
                    "(?i)" + theme.replace("_", "[^ ]")
            );

            for(String s : words)
            {
                if(themePattern.matcher(s).find() 
                        && s.length() == theme.length())
                {
                    filteredWords.add(s);
                }
            }
        }
        else
        {
            showAllWords = true;
        }
    }

    public static String[] getFilteredWords()
    {
        return showAllWords ? words : filteredWords.toArray(new String[0]);
    }

    public static int updateWords(CommandContext<FabricClientCommandSource> context)
    {
        context.getSource().sendFeedback(
                Text.literal(PREFIX + "§aUpdating words..")
        );

        words = getWords();

        return 1;
    }
}
