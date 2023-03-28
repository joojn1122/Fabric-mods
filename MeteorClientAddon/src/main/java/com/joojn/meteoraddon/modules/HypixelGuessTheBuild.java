package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HypixelGuessTheBuild extends Module {

    public static final HypixelGuessTheBuild INSTANCE = new HypixelGuessTheBuild();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> autoSolve = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-solve")
            .description("Automatically solves the build")
            .defaultValue(false)
            .build()
    );

    public HypixelGuessTheBuild() {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "hypixel-guess-the-build",
                "Automatically guesses the build in Hypixel's Guess The Build game, '/guesser help' for help"
        );
    }

    private final static Random RANDOM = new Random();
    public static final String PREFIX = "§8[§6Guess The Build Helper§8]: §r";

    // regex
    private static final Pattern ARRAY_PATTERN = Pattern.compile("\\[(.*)]");

    public static boolean inGame = false;
    private static boolean showAllWords = true;

    private static final List<String> filteredWords = new ArrayList<>();
    public static String[] words = getWords();

    static {
        MeteorClientUtils.LOGGER.info("Found words %s".formatted(Arrays.toString(words)));
    }

    public static String[] getWords()
    {
        File file = new File("guesser-words.csv");

        if(file.exists())
        {
            try
            {
                MeteorClientUtils.LOGGER.info("Trying to load words from '%s'".formatted(file.getName()));

                return FileUtils.readFileToString(file, StandardCharsets.UTF_8).split(";");
            }
            catch (IOException e)
            {
                MeteorClientUtils.LOGGER.error("Something went wrong while trying to load data from '%s'".formatted(file.getName()));

                e.printStackTrace();
            }
        }

        try
        {
            URL originalSite = new URL("https://gtb.cyberfla.me/wordlist/wordlist.js");
            MeteorClientUtils.LOGGER.info("Trying to load words from %s".formatted(originalSite.getPath()));

            String data = IOUtils.toString(originalSite, StandardCharsets.UTF_8).replaceAll("[\n\"\t\r]", "");

            Matcher match = ARRAY_PATTERN.matcher(data);
            if(!match.find()) throw new IOException("Something went wrong while searching for array..");

            return match.group(1).split(",");
        }
        catch (IOException e)
        {
            MeteorClientUtils.LOGGER.error("Could not load words from original site..");

            e.printStackTrace();
        }

        try
        {
            URL url = new URL("https://raw.githubusercontent.com/joojn1122/Fabric-mods/main/Hypixel-GuessTheBuild-Helper/words.csv");

            MeteorClientUtils.LOGGER.info("Trying to load words from %s".formatted(url.getPath()));

            return IOUtils.toString(url, StandardCharsets.UTF_8).split(";");
        }
        catch (IOException e)
        {
            MeteorClientUtils.LOGGER.error("Could not load words from github..");

            e.printStackTrace();
        }

        return new String[0];
    }

    private final Pattern GUESS_THE_BUILD_PATTERN = Pattern.compile(
            "GUESS.*THE.*BUILD",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE
    );

    private String lastTitle = "";

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if(mc.world != null)
        {
            Collection<ScoreboardObjective> objectives = mc.world.getScoreboard().getObjectives();

            if(objectives.size() > 0)
            {
                ScoreboardObjective o = objectives.iterator().next();

                String title = o.getDisplayName().getString();

                if(!Objects.equals(lastTitle, title))
                {
                    lastTitle = title;
                    inGame = GUESS_THE_BUILD_PATTERN.matcher(title).find();
                }
            }

            if(autoSolve.get() && inGame && !showAllWords && ++tickDelay >= 70) {
                tickDelay = 0;

                if(filteredWords.isEmpty() || (usedWords.size() >= filteredWords.size())) return;

                // try random number from filter list
                int num = 0;
                /*
                do {
                    num = RANDOM.nextInt(0, filteredWords.size() - 1);
                }
                while (usedWords.contains(num));
                 */

                boolean found = false;
                // rather use for loop instead of while to be safe
                for(int i = 0; i < filteredWords.size(); i++)
                {
                    num = RANDOM.nextInt(0, filteredWords.size() - 1);

                    if(!usedWords.contains(num)){
                        found = true;
                        break;
                    }
                }

                if(found)
                {
                    usedWords.add(num);

                    ChatUtils.sendPlayerMsg(
                            filteredWords.get(num)
                    );
                }
            };
        }
    }

    private static String theme_ = "";

    private static final Set<Integer> usedWords = new HashSet<>();
    private static int tickDelay = 0;

    public static void setTheme(String theme)
    {
        if(!INSTANCE.isActive() || Objects.equals(theme, theme_)) return;

        theme_ = theme;

        if(theme != null)
        {
            filteredWords.clear();
            usedWords.clear();

            showAllWords = false;

            ChatUtils.sendPlayerMsg(PREFIX + "§aSetting current search to '§e%s§a'".formatted(theme));

            Pattern themePattern = Pattern.compile(
                    theme.replace("_", "[^ ]"),
                    Pattern.CASE_INSENSITIVE
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
}
