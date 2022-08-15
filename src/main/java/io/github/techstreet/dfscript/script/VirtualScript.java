package io.github.techstreet.dfscript.script;

public class VirtualScript {

    private final String name;
    private final String id;

    public VirtualScript(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
