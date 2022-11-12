package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.options.ScriptNamedOption;
import io.github.techstreet.dfscript.script.values.ScriptValue;

import java.lang.reflect.Type;
import java.util.Objects;

public final class ScriptConfigArgument implements ScriptArgument {

    private String option;
    private transient Script script;

    public ScriptConfigArgument(String option, Script script) {
        this.option = option;
        this.script = script;
    }

    @Override
    public ScriptValue getValue(Event event, ScriptContext context) {
        return script.getOption(option);
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType type) {
        return script.getNamedOption(option).getOption().convertableTo(type);
    }

    public ScriptNamedOption getOption() {
        return script.getNamedOption(option);
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public String getName() {
        return option;
    }

    public void setOption(String newOption) {
        option = newOption;
    }

    public static class Serializer implements JsonSerializer<ScriptConfigArgument> {

        @Override
        public JsonElement serialize(ScriptConfigArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type", "CONFIG_VALUE");
            object.addProperty("value", src.getName());
            return object;
        }
    }
}