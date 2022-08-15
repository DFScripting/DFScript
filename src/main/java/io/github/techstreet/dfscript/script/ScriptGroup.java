package io.github.techstreet.dfscript.script;

public enum ScriptGroup {
    //This class specifies whether an action type is an action, a condition or a repetition
    //(important to SKIP_ITERATION, STOP_REPETITION and ELSE)
    ACTION,
    CONDITION,
    REPETITION,
    EVENT
}
