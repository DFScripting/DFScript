package io.github.techstreet.dfscript.script;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptConfigArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptScopeVariables;
import io.github.techstreet.dfscript.script.execution.ScriptTask;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ScriptComment implements ScriptPart {

    private String comment;

    public ScriptComment(String comment) {
        this.comment = comment;
    }

    public ScriptComment setComment(String comment) {
        this.comment = comment;

        return this;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public ScriptGroup getGroup() {
        return ScriptGroup.COMMENT;
    }

    public static class Serializer implements JsonSerializer<ScriptComment> {

        @Override
        public JsonElement serialize(ScriptComment src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "comment");
            obj.addProperty("comment", src.getComment());
            return obj;
        }
    }
}