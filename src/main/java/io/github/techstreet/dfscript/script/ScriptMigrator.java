package io.github.techstreet.dfscript.script;

import io.github.techstreet.dfscript.DFScript;

public class ScriptMigrator {
    public static void migrate(Script script) {
        int previousVer = 0;

        if (script.getVersion() == 0) {
            script.setServer("None");
            if (DFScript.MC.player != null) script.setOwner(DFScript.MC.player.getUuid().toString());

            script.setVersion(1);
        }

        if (script.getVersion() == 1) {
            script.setDescription("N/A");
            script.setVersion(2);
        }

        if (previousVer != script.getVersion()) {
            ScriptManager.LOGGER.info("Migrated script '" + script.getName() + "' from version " + previousVer +" to version " + script.getVersion() + "!");
            ScriptManager.getInstance().saveScript(script);
        }
    }
}
