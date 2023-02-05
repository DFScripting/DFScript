package io.github.techstreet.dfscript.script.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderIconElement;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.List;

public class ScriptBuiltinAction extends ScriptAction {

    private ScriptActionType type;

    public ScriptBuiltinAction(ScriptActionType type, List<ScriptArgument> arguments) {
        super(arguments);
        this.type = type;
    }

    public ScriptBuiltinAction setType(ScriptActionType newType) {
        type = newType;

        return this;
    }

    @Override
    public void run(ScriptTask task) {
        type.run(new ScriptActionContext(task, getArguments()));
    }

    public ScriptActionType getType() {
        return type;
    }

    @Override
    public boolean isDeprecated() {
        return type.isDeprecated();
    }

    @Override
    public ItemStack getIcon() {
        return type.getIcon();
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        render.addElement(new ScriptPartRenderIconElement(getType().getName(), getType().getIcon()));

        super.create(render, script);
    }

    public static class Serializer implements JsonSerializer<ScriptBuiltinAction> {

        @Override
        public JsonElement serialize(ScriptBuiltinAction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "action");
            obj.addProperty("action", src.getType().name());
            obj.add("arguments", context.serialize(src.getArguments()));
            return obj;
        }
    }
}