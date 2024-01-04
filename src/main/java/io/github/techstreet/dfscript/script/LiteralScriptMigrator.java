package io.github.techstreet.dfscript.script;

import com.google.gson.*;

public class LiteralScriptMigrator {

    public static String migrate(String content) {
        JsonObject parsed = new JsonObject();
        JsonObject object = JsonParser.parseString(content).getAsJsonObject();
        parsed.add("name", object.get("name"));
        parsed.add("owner", object.get("owner"));
        parsed.add("server", object.get("server"));
        parsed.add("description", object.get("description"));
        parsed.add("config", object.get("config"));
        parsed.add("disabled", object.get("disabled"));
        parsed.add("version", object.get("version"));
        JsonArray parsedHeaders = new JsonArray();
        for (JsonElement el : object.get("headers").getAsJsonArray()) {
            JsonObject header = el.getAsJsonObject();
            JsonObject parsedHeader = new JsonObject();
            parsedHeader.add("type", header.get("type"));
            parsedHeader.add("event", header.get("event"));
            JsonObject parsedSnippet = new JsonObject();
            parsedSnippet.add("hidden", header.get("snippet").getAsJsonObject().get("hidden"));
            JsonArray parsedParts = new JsonArray();
            for (JsonElement part_el : header.get("snippet").getAsJsonObject().get("parts").getAsJsonArray()) {
                JsonObject part = part_el.getAsJsonObject();
                migrate_snippet(part);
                parsedParts.add(part);
            }
            parsedSnippet.add("parts", parsedParts);
            parsedHeader.add("snippet", parsedSnippet);
            parsedHeaders.add(parsedHeader);
        }
        parsed.add("headers", parsedHeaders);
        return new GsonBuilder().create().toJson(parsed);
    }

    public static void migrate_snippet(JsonObject snippet) {
        if (snippet.get("type").getAsString().equals("branch")) {
            migrate_branch(snippet);
        } else if (snippet.get("type").getAsString().equals("action")) {
            migrate_part(snippet);
        } else if (snippet.get("type").getAsString().equals("booleanSet")) {
            migrate_booleanSet(snippet);
        } else if (snippet.get("type").getAsString().equals("repetition")) {
            migrate_repetition(snippet);
        }
    }

    public static void migrate_repetition(JsonObject part) {
        String action = part.get("repetition").getAsString();
        part.remove("repetition");
        part.add("repetition", new JsonPrimitive(migrate_action(action)));
        JsonArray parsedParts = new JsonArray();
        for (JsonElement el : part.get("snippet").getAsJsonObject().get("parts").getAsJsonArray()) {
            JsonObject el_json = el.getAsJsonObject();
            migrate_snippet(el_json);
            parsedParts.add(el_json);
        }
        JsonObject parsedSnippets = new JsonObject();
        parsedSnippets.add("hidden", part.get("snippet").getAsJsonObject().get("hidden"));
        parsedSnippets.add("parts", parsedParts);
        part.remove("snippet");
        part.add("snippet", parsedSnippets);
    }

    public static void migrate_booleanSet(JsonObject part) {
        JsonObject condition = part.get("condition").getAsJsonObject();
        String conditionString = condition.get("condition").getAsString();
        condition.remove("condition");
        condition.add("condition", new JsonPrimitive(migrate_action(conditionString)));
        part.remove("condition");
        part.add("condition", condition);
    }

    public static void migrate_branch(JsonObject part) {
        JsonObject condition = part.get("condition").getAsJsonObject();
        String conditionString = condition.get("condition").getAsString();
        condition.remove("condition");
        condition.add("condition", new JsonPrimitive(migrate_action(conditionString)));
        part.remove("condition");
        part.add("condition", condition);
        JsonObject parsedTrue = new JsonObject();
        parsedTrue.add("hidden", part.get("true").getAsJsonObject().get("hidden"));
        JsonObject parsedFalse = new JsonObject();
        parsedFalse.add("hidden", part.get("false").getAsJsonObject().get("false"));

        JsonArray parsedTrueParts = new JsonArray();
        for (JsonElement true_el : part.get("true").getAsJsonObject().get("parts").getAsJsonArray()) {
            JsonObject true_el_json = true_el.getAsJsonObject();
            migrate_snippet(true_el_json);
            parsedTrueParts.add(true_el_json);
        }
        parsedTrue.add("parts", parsedTrueParts);

        JsonArray parsedFalseParts = new JsonArray();
        for (JsonElement false_el : part.get("false").getAsJsonObject().get("parts").getAsJsonArray()) {
            JsonObject false_el_json = false_el.getAsJsonObject();
            migrate_snippet(false_el_json);
            parsedFalseParts.add(false_el_json);
        }
        parsedFalse.add("parts", parsedFalseParts);

        part.remove("true");
        part.remove("false");
        part.add("true", parsedTrue);
        part.add("false", parsedFalse);
    }

    public static void migrate_part(JsonObject part) {
        String action = part.get("action").getAsString();
        part.remove("action");
        part.add("action", new JsonPrimitive(migrate_action(action)));
    }

    public static String migrate_action(String action) {
        return switch (action) {
            case "JOIN_TEXT" -> "JOIN_STRING";
            case "COPY_TEXT" -> "COPY_STRING";
            case "SPLIT_TEXT" -> "SPLIT_STRING";
            case "REGEX_SPLIT_TEXT" -> "REGEX_SPLIT_STRING";
            case "JOIN_LIST_TO_TEXT" -> "JOIN_LIST_TO_STRING";
            case "TEXT_INDEX_OF" -> "STRING_INDEX_OF";
            case "TEXT_SUBTEXT" -> "GET_SUBSTRING";
            case "TEXT_LENGTH" -> "STRING_LENGTH";
            case "REPLACE_TEXT" -> "REPLACE_STRING";
            case "REGEX_REPLACE_TEXT" -> "REGEX_REPLACE_STRING";
            case "REMOVE_TEXT" -> "REMOVE_STRING";
            case "REPEAT_TEXT" -> "REPEAT_STRING";
            case "POP_AND_INDEX" -> "POP_AT_INDEX";
            case "SHOW_OPTIONS" -> "MESSAGE_BOX";
            default -> action;
        };
    }

}
