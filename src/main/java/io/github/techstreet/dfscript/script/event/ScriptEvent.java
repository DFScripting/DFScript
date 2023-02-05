package io.github.techstreet.dfscript.script.event;

import com.google.gson.*;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptGroup;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import net.minecraft.text.Text;

import java.lang.reflect.Type;

public class ScriptEvent extends ScriptHeader {

    private final ScriptEventType type;

    public ScriptEvent(ScriptEventType type) {
        this.type = type;
    }

    public ScriptEventType getType() {
        return type;
    }

    public static class Serializer implements JsonSerializer<ScriptEvent> {

        @Override
        public JsonElement serialize(ScriptEvent src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "event");
            obj.addProperty("event", src.getType().name());
            obj.add("snippet", context.serialize(src.container().getSnippet(0)));
            return obj;
        }
    }

    public int create(CScrollPanel panel, int y, int index, Script script) {
        panel.add(new CItem(5, y, getType().getIcon()));
        panel.add(new CText(15, y + 2, Text.literal(getType().getName())));

        return super.create(panel, y, index, script);
    }
}
