package io.github.techstreet.dfscript.script;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.event.ScriptEvent;
import io.github.techstreet.dfscript.script.execution.*;
import io.github.techstreet.dfscript.util.chat.ChatType;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Script {
    public static int scriptVersion = 1;

    private final String name;
    private String owner;
    private int version = 0;
    private String server;
    private final List<ScriptPart> parts;
    private final Logger LOGGER;
    private final ScriptContext context = new ScriptContext();
    private File file;
    private boolean disabled;

    public Script(String name, String owner, String server, List<ScriptPart> parts, boolean disabled, int version) {
        this.name = name;
        this.owner = owner;
        this.server = server;
        this.parts = parts;
        this.disabled = disabled;
        this.version = version;

        LOGGER = LogManager.getLogger("Script." + name);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void invoke(Event event) {
        int pos = 0;
        for (ScriptPart part : parts) {
            if (part instanceof ScriptEvent se) {
                if (se.getType().getCodeutilitiesEvent().equals(event.getClass())) {
                    try {
                        this.execute(new ScriptTask(new ScriptPosStack(pos+1), event,this));
                    } catch (Exception err) {
                        ChatUtil.sendMessage("Error while invoking event " + se.getType().getName() + " in script " + name + ": " + err.getMessage(), ChatType.FAIL);
                        LOGGER.error("Error while invoking event " + se.getType().getName(), err);
                        err.printStackTrace();
                    }
                }
            }
            pos++;
        }
    }

    public void execute(ScriptTask task) {
        if (disabled) { // don't run the code if it's disabled obviously
            return;
        }
        while (task.stack().peek() < parts.size()) { // check if there is still code to be run
            ScriptPart part = parts.get(task.stack().peek()); // get the script part (action or event who cares)
            if (part instanceof ScriptEvent) { // well maybe we do care?
                return;
            } else if (part instanceof ScriptAction sa) { // only run ScriptActions (possibly being able to implement comments?)
                Consumer<ScriptScopeVariables> inner = null;
                if (sa.getType().hasChildren()) {
                    int posCopy = task.stack().peek(); // get the current position for later
                    inner = (scriptScopeVariables) -> task.schedule(posCopy, scriptScopeVariables); // schedule the configurable code
                    int depth = 0;
                    while (task.stack().peek() < parts.size()) { // loop through all the script parts
                        ScriptPart nextPart = parts.get(task.stack().peek());
                        if (nextPart instanceof ScriptEvent) { // so we can see whether it's an event or an action
                            task.stack().clear();
                            return;
                        } else if (nextPart instanceof ScriptAction sa2) {
                            if (sa2.getType().hasChildren()) { // we increase the depth if it has children
                                depth++;
                            } else if (sa2.getType() == ScriptActionType.CLOSE_BRACKET) { // or we decrease it if we get to the end of the inner code
                                depth--;
                                if (depth == 0) { // stop when we reach the same depth as the original script action
                                    break;
                                }
                            }
                        } else {
                            throw new IllegalStateException("Unexpected script part type: " + nextPart.getClass().getName());
                        }
                        if (!task.stack().isEmpty()) { // are we done with code yet?
                            task.stack().increase();
                        } else {
                            return;
                        }
                    }
                }
                if(sa.getGroup() == ScriptGroup.CONDITION) { // if it's a condition
                    if(sa.getType() != ScriptActionType.ELSE) { // and not an else
                        task.stack().peekElement().setVariable("lastIfResult", false); // set the last result to false
                    }
                }
                else {
                    task.stack().peekElement().setVariable("lastIfResult", true); //does this detect close brackets or no (no it doesn't, good)
                }
                sa.invoke(task.event(), context, inner,task, this); // execute the script action
                if (!task.isRunning()) { // is the script still running?
                    return;
                }
                if(sa.getGroup() == ScriptGroup.CONDITION) { // if it's a condition
                    if(task.stack().peekElement().getVariable("lastIfResult").equals(true)) { // and it's last if result worked
                        inner.accept(null);
                    }
                }
                if (sa.getType() == ScriptActionType.CLOSE_BRACKET) { // is this the end of the scope?
                    if(endScope(task))
                    {
                        return;
                    }
                }
                while(context.isForcedToEndScope()) { // are we forced to end the scope? (aka was skip iteration used?)
                    context.forceEndScope(-1);
                    if(endScope(task))
                    {
                        return;
                    }
                }
                if(context.isLoopBroken()) { // are we forced to break the loop? (aka was stop repetition used?)
                    context.breakLoop(-1);
                    task.stack().pop(); // don't use endScope() because of the fact that endScope runs the condition to see if it is false before ending the scope
                }
            } else {
                throw new IllegalArgumentException("Invalid script part");
            }
            if (!task.stack().isEmpty()) { // did we finish executing the code?
                task.stack().increase();
            } else {
                return;
            }
        }
    }

    private boolean endScope(ScriptTask task) {
        if(task.stack().peekElement().checkCondition()) {
            if(!task.stack().peekElement().hasVariable("LagslayerCounter")) {
                task.stack().peekElement().setVariable("LagslayerCounter", 0);
            }

            int lagslayerCounter = (Integer)task.stack().peekElement().getVariable("LagslayerCounter")+1;

            task.stack().peekElement().setVariable("LagslayerCounter", lagslayerCounter);

            if(lagslayerCounter >= 100000) {
                task.stack().peekElement().setVariable("LagslayerCounter", 0);
                task.stop();//Lagslayer be like:
            }

            task.stack().peekElement().setPos(task.stack().peekElement().getOriginalPos());
            return false;
        }

        if (task.stack().isEmpty()) {
            return true;
        } else {
            task.stack().pop();
        }

        return false;
    }
    public List<ScriptPart> getParts() {
        return parts;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public int getVersion() {
        return version;
    }

    public String getServer() {
        return server;
    }

    public boolean disabled() {
        return disabled;
    }

    public void setDisabled(boolean b) {
        disabled = b;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public ScriptContext getContext() {
        return context;
    }

    public static class Serializer implements JsonSerializer<Script>, JsonDeserializer<Script> {
        @Override
        public Script deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String name = object.get("name").getAsString();

            String owner = null;
            if (object.get("owner") != null) owner = object.get("owner").getAsString();

            String serverId = object.get("server").getAsString();

            List<ScriptPart> parts = new ArrayList<>();
            for (JsonElement element : object.get("actions").getAsJsonArray()) {
                ScriptPart part = context.deserialize(element, ScriptPart.class);
                parts.add(part);
            }

            boolean disabled = object.has("disabled") && object.get("disabled").getAsBoolean();
            int version = object.get("version").getAsInt();

            return new Script(name, owner, serverId, parts, disabled, version);
        }

        @Override
        public JsonElement serialize(Script src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("name", src.name);
            object.addProperty("owner", src.owner);
            object.addProperty("server", src.server);

            JsonArray array = new JsonArray();
            for (ScriptPart part : src.getParts()) {
                array.add(context.serialize(part));
            }

            object.add("actions", array);
            object.addProperty("disabled", src.disabled);
            object.addProperty("version", src.version);
            return object;
        }
    }
}
