package io.github.techstreet.dfscript.script.action;

import com.google.gson.*;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import net.minecraft.registry.Registries;

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
        if (arg.optional()) {
            generatePossibilities(possibilities, new ScriptActionArgumentList(current), pos + 1);
        }
        current.add(arg);
        generatePossibilities(possibilities, current, pos + 1);
    }

    public List<ScriptActionArgumentList> generatePossibilities() {
        List<ScriptActionArgumentList> possibilities = new ArrayList<>();

        generatePossibilities(possibilities, new ScriptActionArgumentList(), 0);

        return possibilities;
    }

    public void getArgMap(ScriptActionContext ctx) {
        List<ScriptActionArgumentList> possibilities = generatePossibilities();

        search:
        for (List<ScriptActionArgument> possibility : possibilities) {
            int pos = 0;
            ctx.argMap().clear();
            ctx.actionArgMap().clear();
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
                ctx.setArg(arg.name(), args);
                ctx.actionArgMap().put(arg.name(), arg);
            }
            if (pos == ctx.arguments().size()) {
                return;
            }
        }
        ctx.argMap().clear();
        ctx.actionArgMap().clear();
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

    public boolean argumentExists(String functionName) {
        for (ScriptActionArgument arg : this) {
            if(arg.name().equals(functionName)) {
                return true;
            }
        }
        return false;
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
