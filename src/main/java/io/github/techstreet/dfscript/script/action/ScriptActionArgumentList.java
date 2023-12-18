package io.github.techstreet.dfscript.script.action;

import com.google.gson.*;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScriptActionArgumentList extends ArrayList<ScriptActionArgument> {
    public ScriptActionArgumentList(ScriptActionArgumentList current) {
        this.addAll(current);
    }

    public ScriptActionArgumentList() {

    }

    private void generatePossibilities(List<ScriptActionArgumentList> possibilities, ScriptActionArgumentList current, int pos) {
        if (pos >= size()) {
            possibilities.add(new ScriptActionArgumentList(current));
            return;
        }

        ScriptActionArgument arg = get(pos);

        ScriptActionArgumentList newCurrent = new ScriptActionArgumentList(current);

        if (arg.rightOptional()) {
            generatePossibilities(possibilities, new ScriptActionArgumentList(current), pos + 1);
        }

        current.add(arg);

        generatePossibilities(possibilities, current, pos + 1);

        if (arg.optional()) {
            generatePossibilities(possibilities, newCurrent, pos + 1);
        }
    }

    public List<ScriptActionArgumentList> generatePossibilities() {
        List<ScriptActionArgumentList> possibilities = new ArrayList<>();

        generatePossibilities(possibilities, new ScriptActionArgumentList(), 0);

        return possibilities;
    }

    public void getArgMap(ScriptActionContext ctx) {
        List<ScriptActionArgumentList> possibilities = generatePossibilities();

        for (ScriptActionArgument arg : this) {
            ctx.putActionArg(arg);
        }

        search:
        for (List<ScriptActionArgument> possibility : possibilities) {
            int pos = 0;
            ctx.argMap().clear();
            for (ScriptActionArgument arg : possibility) {
                List<ScriptArgument> args = new ArrayList<>();
                if (pos >= ctx.arguments().size()) {
                    continue search;
                }
                if (ctx.arguments().get(pos).convertableTo(arg.type())) {
                    args.add(ctx.arguments().get(pos));
                    pos++;
                }
                if (arg.plural()) {
                    while (pos < ctx.arguments().size()) {
                        if (ctx.arguments().get(pos).convertableTo(arg.type())) {
                            args.add(ctx.arguments().get(pos));
                            pos++;
                        } else {
                            break;
                        }
                    }
                }
                ctx.setArg(arg, args);
            }
            if (pos == ctx.arguments().size()) {
                return;
            }
        }
        ctx.argMap().clear();
        throw new IllegalArgumentException();
    }

    public String getUnnamedArgument() {
        for(int i = 1; ; i++) {

            String name = "Argument";

            if(i != 1) {
                name = name + " " + i;
            }

            if(!argumentExists(name)) {
                return name;
            }
        }
    }

    public boolean argumentExists(String functionArg) {
        for (ScriptActionArgument arg : this) {
            if(arg.name().equals(functionArg)) {
                return true;
            }
        }
        return false;
    }

    public ScriptActionArgument getByName(String functionArg) {
        for (ScriptActionArgument arg : this) {
            if(arg.name().equals(functionArg)) {
                return arg;
            }
        }
        return null;
    }

    public static class Serializer implements JsonSerializer<ScriptActionArgumentList>, JsonDeserializer<ScriptActionArgumentList> {

        @Override
        public JsonElement serialize(ScriptActionArgumentList src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            JsonArray args = new JsonArray();

            for (ScriptActionArgument arg : src) {
                args.add(context.serialize(arg));
            }

            obj.add("args", args);

            return obj;
        }

        @Override
        public ScriptActionArgumentList deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            ScriptActionArgumentList list = new ScriptActionArgumentList();

            JsonObject obj = json.getAsJsonObject();
            JsonArray argList = obj.getAsJsonArray("args");

            for (JsonElement arg : argList) {
                list.add(context.deserialize(arg, ScriptActionArgument.class));
            }

            return list;
        }
    }
}
