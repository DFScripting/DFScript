package io.github.techstreet.dfscript.script.action;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.text.LiteralText;

public class ScriptActionArgument {

    private final String name;
    private final ScriptActionArgumentType type;
    private boolean optional = false;
    private boolean plural = false;

    public ScriptActionArgument(String name, ScriptActionArgumentType type) {
        this.name = name;
        this.type = type;
    }

    public ScriptActionArgument optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public ScriptActionArgument plural(boolean plural) {
        this.plural = plural;
        return this;
    }

    public String name() {
        return name;
    }

    public boolean optional() {
        return optional;
    }

    public boolean plural() {
        return plural;
    }

    public ScriptActionArgumentType type() {
        return type;
    }

    public Text text() {
        MutableText t = type.text();
        if (plural) {
            t.append(((LiteralText) Text.of("(s)")).fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));
        }

        if (optional) {
            t.append(((LiteralText) Text.of("*")).fillStyle(Style.EMPTY.withItalic(true)));
        }

        return t.append(((LiteralText) Text.of(" - ")).fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY)))
            .append(((LiteralText) Text.of(name)).fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));

    }

    public enum ScriptActionArgumentType {
        VARIABLE,
        NUMBER,
        TEXT,
        LIST,
        DICTIONARY,
        ANY;

        public MutableText text() {
            MutableText val = (MutableText) Text.of(switch (this) {
                case VARIABLE -> "Variable";
                case NUMBER -> "Number";
                case TEXT -> "Text";
                case LIST -> "List";
                case DICTIONARY -> "Dictionary";
                case ANY -> "Any";
            });
            return val.fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE));
        }
        public boolean convertableTo(ScriptActionArgumentType to) {
            return to == ANY
                || to == TEXT
                || this == VARIABLE
                || this == to;
        }
    }
}