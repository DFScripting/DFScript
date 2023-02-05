package io.github.techstreet.dfscript.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.script.action.ScriptActionType;

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

        if (script.getVersion() == 2) {
            script.replaceAction(ScriptActionType.TEXT_SUBTEXT, ScriptActionType.TEXT_SUBTEXT_V1);
            script.setVersion(3);
        }

        if (script.getVersion() == 3) {
            script.replaceAction(ScriptActionType.SPLIT_TEXT, ScriptActionType.REGEX_SPLIT_TEXT);
            script.replaceAction(ScriptActionType.RANDOM_NUMBER, ScriptActionType.RANDOM_DOUBLE);

            script.setVersion(4);
        }

        if (script.getVersion() == 4) {
            script.setVersion(5);
        }

        if (previousVer != script.getVersion()) {
            ScriptManager.LOGGER.info("Migrated script '" + script.getName() + "' from version " + previousVer + " to version " + script.getVersion() + "!");
            ScriptManager.getInstance().saveScript(script);
        }
    }
}
