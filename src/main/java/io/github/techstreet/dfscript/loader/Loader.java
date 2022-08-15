package io.github.techstreet.dfscript.loader;

public class Loader {
    private static Loader instance;

    private Loader() {
        instance = this;
    }

    public static Loader getInstance() {
        if (instance == null) {
            new Loader();
        }
        return instance;
    }

    public void load(Loadable... l) {
        for (Loadable loadable : l) {
            loadable.load();
        }
    }
}
