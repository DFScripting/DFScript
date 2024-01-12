package io.github.techstreet.dfscript.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import net.minecraft.command.CommandSource;

public class StringFuncArgumentType implements ArgumentType<String> {

    StringFuncArgumentFunctions func;
    boolean greedy;

    public StringFuncArgumentType(StringFuncArgumentFunctions func, boolean greedy) {
        this.func = func;
        this.greedy = greedy;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
        SuggestionsBuilder builder) {

        List<String> suggestions = func.getFunction().apply(null);

        if (context.getSource() instanceof CommandSource) {
            return CommandSource.suggestMatching(suggestions, builder);
        } else {
            return Suggestions.empty();
        }
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();

        while (reader.canRead()) {
            if (this.greedy) reader.skip();
            else if (reader.peek() != ' ') reader.skip();
            else break;
        }

        return reader.getString().substring(i, reader.getCursor());
    }

    public StringFuncArgumentFunctions getFunction()
    {
        return func;
    }

    public boolean isGreedy() {
        return greedy;
    }
}