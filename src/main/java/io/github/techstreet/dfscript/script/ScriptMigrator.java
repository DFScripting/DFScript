package io.github.techstreet.dfscript.script;

import io.github.techstreet.dfscript.DFScript;

public class ScriptMigrator {
    public static Script migrate(Script script) {
        if (script.getVersion() == 0) {
            script.setServer(null);
            if (DFScript.MC.player != null) script.setOwner(DFScript.MC.player.getUuid().toString());

            script.setVersion(1);

            ScriptManager.LOGGER.info("Migrated script '" + script.getName() + "' from version " + script.getVersion() + " to version " + script.getVersion());
        }

        return script;
    }
}
