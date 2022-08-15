package io.github.techstreet.dfscript.config.internal;

import io.github.techstreet.dfscript.config.types.IConfigEnum;

public enum DfButtonTextLocations implements IConfigEnum {
    NONE(),
    TOP_LEFT(),
    BOTTOM_RIGHT();

    @Override
    public String getKey() {
        return "dfButtonsLocation";
    }
}
