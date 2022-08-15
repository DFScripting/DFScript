package io.github.techstreet.dfscript.commands.arguments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileArgumentType {
    public static StringFuncArgumentType folder(File folder, boolean greedy) {
        return new StringFuncArgumentType(v -> {
            List<String> files = new ArrayList<>();

            if (folder.exists()) {
                for (File f : folder.listFiles()) {
                    files.add(f.getName());
                }
            }

            return files;
        }, true);
    }
}