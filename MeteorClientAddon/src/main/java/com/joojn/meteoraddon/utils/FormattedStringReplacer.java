package com.joojn.meteoraddon.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Function;

public class FormattedStringReplacer implements StringVisitable.StyledVisitor<Object> {

    private final MutableText text = Text.empty();
    private final Function<String, String> replacer;

    private FormattedStringReplacer(Function<String, String> replacer) {
        this.replacer = replacer;
    }

    @Override
    public Optional<Object> accept(Style style, String asString) {

        text.append(
                Text.literal(replacer.apply(asString)).fillStyle(style)
        );

        return Optional.empty();
    }

    public static MutableText replaceText(
            Text original,
            Function<String, String> replacer
    ) {
        FormattedStringReplacer visitor = new FormattedStringReplacer(replacer);
        original.visit(visitor, Style.EMPTY);

        return visitor.text;
    }
}
