package io.github.techstreet.dfscript.util;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

    private final String regex;
    private final Pattern pattern;
    private static final HashMap<String, Pattern> regexes = new HashMap<>();
    private final Matcher matcher;

    public Regex(String regex) {
        this.regex = regex;
        this.pattern = get(regex);
        this.matcher = pattern.matcher("");
    }

    public static Regex of(String regex) {
        return new Regex(regex);
    }

    public static Pattern get(String regex) {
        if (regexes.containsKey(regex)) {
            return regexes.get(regex);
        } else {
            Pattern pattern = Pattern.compile(regex);
            regexes.put(regex, pattern);
            return pattern;
        }
    }

    public boolean matches() {
        return matcher.matches();
    }

    public boolean matches(String input) {
        return getMatcher(input).matches();
    }

    public void matches(Consumer<Matcher> exec) {
        while(matches()) {
            exec.accept(getMatcher());
        }
    }

    public void matches(String input, Consumer<Matcher> exec) {
        getMatcher(input);
        while(matches()) {
            exec.accept(getMatcher());
        }
    }

    public boolean find() {
        return matcher.find();
    }

    public boolean find(String input) {
        return getMatcher(input).find();
    }

    public void find(Consumer<Matcher> exec) {
        while(find()) {
            exec.accept(getMatcher());
        }
    }

    public void find(String input, Consumer<Matcher> exec) {
        getMatcher(input);
        while(find()) {
            exec.accept(getMatcher());
        }
    }

    public String getRegex() {
        return regex;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Matcher getMatcher(String input) {
        return matcher.reset(input);
    }

    public Matcher getMatcher() {
        return matcher;
    }
}
