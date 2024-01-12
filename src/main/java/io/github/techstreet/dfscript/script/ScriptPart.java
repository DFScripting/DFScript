package io.github.techstreet.dfscript.script;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.action.ScriptBuiltinAction;
import io.github.techstreet.dfscript.script.action.ScriptFunctionCall;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.conditions.ScriptBooleanSet;
import io.github.techstreet.dfscript.script.conditions.ScriptBranch;
import io.github.techstreet.dfscript.script.conditions.ScriptCondition;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.repetitions.ScriptBuiltinRepetition;
import io.github.techstreet.dfscript.script.repetitions.ScriptRepetitionType;
import io.github.techstreet.dfscript.script.repetitions.ScriptWhile;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class ScriptPart implements ScriptRunnable {

    public abstract void create(ScriptPartRender render, Script script);

    public boolean isDeprecated() {
        return false;
    }

    public List<ContextMenuButton> getContextMenu() {
        return new ArrayList<>();
    }

    public abstract ItemStack getIcon();

    public abstract String getName();

    public static class Serializer implements JsonDeserializer<ScriptPart> {

        @Override
        public ScriptPart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String type = obj.get("type").getAsString();
            switch (type) {
                case "action" -> {
                    String action = obj.get("action").getAsString();
                    List<ScriptArgument> args = new ArrayList<>();
                    for (JsonElement arg : obj.get("arguments").getAsJsonArray()) {
                        args.add(context.deserialize(arg, ScriptArgument.class));
                    }
                    return new ScriptBuiltinAction(ScriptActionType.valueOf(action), args);
                }
                case "functionCall" -> {
                    String action = obj.get("functionCall").getAsString();
                    List<ScriptArgument> args = new ArrayList<>();
                    for (JsonElement arg : obj.get("arguments").getAsJsonArray()) {
                        args.add(context.deserialize(arg, ScriptArgument.class));
                    }
                    return new ScriptFunctionCall(null, action, args);
                }
                case "branch" -> {
                    boolean hasElse = obj.get("hasElse").getAsBoolean();
                    List<ScriptArgument> args = new ArrayList<>();
                    for (JsonElement arg : obj.get("arguments").getAsJsonArray()) {
                        args.add(context.deserialize(arg, ScriptArgument.class));
                    }
                    ScriptCondition condition = context.deserialize(obj.get("condition"), ScriptCondition.class);

                    ScriptBranch part = new ScriptBranch(args, condition);
                    if(hasElse) part.setHasElse();

                    part.container().setSnippet(0, context.deserialize(obj.getAsJsonObject("true"), ScriptSnippet.class));
                    part.container().setSnippet(1, context.deserialize(obj.getAsJsonObject("false"), ScriptSnippet.class));

                    return part;
                }
                case "booleanSet" -> {
                    List<ScriptArgument> args = new ArrayList<>();
                    for (JsonElement arg : obj.get("arguments").getAsJsonArray()) {
                        args.add(context.deserialize(arg, ScriptArgument.class));
                    }
                    ScriptCondition condition = context.deserialize(obj.get("condition"), ScriptCondition.class);

                    ScriptBooleanSet part = new ScriptBooleanSet(args, condition);

                    return part;
                }
                case "repetition" -> {
                    String action = obj.get("repetition").getAsString();
                    List<ScriptArgument> args = new ArrayList<>();
                    for (JsonElement arg : obj.get("arguments").getAsJsonArray()) {
                        args.add(context.deserialize(arg, ScriptArgument.class));
                    }
                    ScriptBuiltinRepetition part = new ScriptBuiltinRepetition(args, ScriptRepetitionType.valueOf(action));

                    part.container().setSnippet(0, context.deserialize(obj.getAsJsonObject("snippet"), ScriptSnippet.class));

                    return part;
                }
                case "while" -> {
                    ScriptCondition condition = context.deserialize(obj.get("condition"), ScriptCondition.class);
                    List<ScriptArgument> args = new ArrayList<>();
                    for (JsonElement arg : obj.get("arguments").getAsJsonArray()) {
                        args.add(context.deserialize(arg, ScriptArgument.class));
                    }
                    ScriptWhile part = new ScriptWhile(args, condition);

                    part.container().setSnippet(0, context.deserialize(obj.getAsJsonObject("snippet"), ScriptSnippet.class));

                    return part;
                }
                case "comment" -> {
                    String comment = obj.get("comment").getAsString();
                    return new ScriptComment(comment);
                }
                default -> throw new JsonParseException("Unknown script part type: " + type);
            }
        }
    }
}
