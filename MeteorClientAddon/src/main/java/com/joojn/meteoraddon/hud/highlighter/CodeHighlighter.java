package com.joojn.meteoraddon.hud.highlighter;

import com.joojn.meteoraddon.hud.highlighter.theme.ITheme;
import net.minecraft.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeHighlighter {

    // These numbers are numbers of each group
    public static final int DEFAULT   = 0;
    public static final int COMMENTS  = 1;
    public static final int STRINGS   = 2;
    public static final int CLASSES2  = 5;
    public static final int KEYWORDS  = 6;
    public static final int METHODS   = 7;
    public static final int FIELDS    = 9;
    public static final int CLASSES   = 12;
    public static final int LOCALS    = 14;

    // sorted by priority
    private static final List<Integer> KEYS = Arrays.asList(
            COMMENTS,
            STRINGS,
            CLASSES2,
            KEYWORDS,
            METHODS,
            FIELDS,
            CLASSES,
            LOCALS
    );

    private static final Pattern CODE_PATTERN = Pattern.compile("(//.*$|/\\*(?s:.)*?\\*/)|((\".*\")|('.*'))|(?<=package|import)[\\r ]+([^;]+)|(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while)(?=\\W)|(\\w+)(?=\\()|\\b(boolean|byte|char|double|float|int|long|short|void)\\b\\s+\\w+|(\\w+)(?=\\s*[=|;])(?!\\S*(\\b(boolean|byte|char|double|float|int|long|short|void)\\b))|(?<=\\W)([A-Z]\\w*)|(@\\w+)|([a-zA-Z]\\w+)",
            Pattern.MULTILINE
    );

    public static ArrayList<Pair<Integer, String>> highlightCode(
            String code
    ) {
        Matcher matcher = CODE_PATTERN.matcher(code);

        final ArrayList<Pair<Integer, String>> codeParts = new ArrayList<>();
        int currentIndex = 0;

        while (matcher.find()) {
            for(int key : KEYS) {
                if(matcher.group(key) != null) {

                    String previous = code.substring(currentIndex, matcher.start(key));
                    String next = code.substring(matcher.start(), matcher.end(key));

                    if(!previous.isBlank()) {
                        codeParts.add(new Pair<>(
                                DEFAULT, previous
                        ));
                    }

                    codeParts.add(new Pair<>(
                            key, next
                    ));

                    // code = code.substring(matcher.end(key));
                    currentIndex = matcher.end();

                    break;
                }
            }
        }

        return codeParts;
    }
}