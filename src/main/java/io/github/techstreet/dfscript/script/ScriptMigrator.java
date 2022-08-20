package io.github.techstreet.dfscript.script;

import io.github.techstreet.dfscript.DFScript;

public class ScriptMigrator {
    public static void migrate(Script script) {
        int previousVer = script.getVersion();

        if (script.getVersion() == 0) {
            script.setServer("None");
            script.setOwner(DFScript.PLAYER_UUID);
            script.setVersion(1);
        }

        if (script.getVersion() == 1) {
            script.setDescription("N/A");
            script.setVersion(2);
        }

        if (previousVer != script.getVersion()) {
            ScriptManager.LOGGER.info("Migrated script '" + script.getName() + "' from version " + previousVer + " to version " + script.getVersion() + "!");
            ScriptManager.getInstance().saveScript(script);
        }
    }
}
