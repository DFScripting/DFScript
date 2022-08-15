package io.github.techstreet.dfscript.config.internal;

import io.github.techstreet.dfscript.config.types.IConfigEnum;

public enum QueueMessages implements IConfigEnum {
    MAIN_CHAT(),
    SIDE_CHAT(),
    TOAST(),
    HIDDEN();

    @Override
    public String getKey() {
        return "support_queuemessages";
    }
}
