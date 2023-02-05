package io.github.techstreet.dfscript.script.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;

import java.lang.reflect.Type;
import java.util.List;

public class ScriptAction extends ScriptParametrizedPart {

    public ScriptAction(List<ScriptArgument> arguments) {
        super(arguments);
    }

    @Override
    public void run(ScriptTask task) {

    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public void create(ScriptPartRender render, Script script) {}
}