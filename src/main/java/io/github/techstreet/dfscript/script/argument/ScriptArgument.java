package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public interface ScriptArgument {

    ScriptValue getValue(ScriptTask task);

    boolean convertableTo(ScriptActionArgumentType type);

    ItemStack getArgIcon();

    String getArgText();

    default List<ContextMenuButton> getContextMenu() {
        return new ArrayList<>();
    }

    default Text getArgIconText() { return null; }

    class Serializer implements JsonDeserializer<ScriptArgument> {

        @Override
        public ScriptArgument deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String type = object.get("type").getAsString();
            return switch (type) {
                case "TEXT" -> new ScriptTextArgument(object.get("value").getAsString());
                case "NUMBER" -> new ScriptNumberArgument(object.get("value").getAsDouble());
                case "VARIABLE" -> context.deserialize(object, ScriptVariableArgument.class);
                case "CLIENT_VALUE" -> ScriptClientValueArgument.valueOf(object.get("value").getAsString());
                case "CONFIG_VALUE" -> new ScriptConfigArgument(object.get("value").getAsString(), null);
                default -> throw new JsonParseException("Unknown argument type: " + type);
            };
        }
    }
}
