package cz.coffee.skjson.api;

import com.google.gson.*;
import cz.coffee.skjson.utils.LoggingUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;

import static cz.coffee.skjson.api.ConfigRecords.LOGGING_LEVEL;
import static cz.coffee.skjson.api.ConfigRecords.PROJECT_DEBUG;

/**
 * The type File wrapper.
 */
public class FileWrapper {

    private static final File aRoot = new File("./");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().enableComplexMapKeySerialization().create();

    /**
     * The type Json file.
     */
    public static class JsonFile {
        private final File file;
        private final Reader reader;

        /**
         * Instantiates a new Json file.
         *
         * @param file   the file
         * @param reader the reader
         */
        public JsonFile(File file, Reader reader) {
            this.file = file;
            this.reader = reader;
        }

        @Override
        public String toString() {
            return "JsonFile{file=" + file + "}";
        }

        /**
         * Get json element.
         *
         * @return the json element
         */
        public JsonElement get() {
            if (file.getName().endsWith(".json")) {
                try {
                    return JsonParser.parseReader(reader);
                } catch (JsonParseException e) {
                    if (PROJECT_DEBUG) LoggingUtil.error(e.getMessage());
                }
            } else if (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
                try {
                    Yaml yaml = new Yaml();
                    Object yamlMap = yaml.loadAll(reader);
                    return new GsonBuilder().serializeNulls().create().toJsonTree(yamlMap);
                } catch (Exception e) {
                    if (PROJECT_DEBUG) LoggingUtil.error(e.getMessage());
                }
            }
            try {
                reader.close();
            } catch (Exception e) {
                if (PROJECT_DEBUG) LoggingUtil.error(e.getMessage());
                return null;
            }
            return null;
        }
    }


    /**
     * From completable future.
     *
     * @param file the file
     * @return the completable future
     */
    public static CompletableFuture<JsonFile> from(File file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Reader reader = new BufferedReader(new FileReader(file));
                return new JsonFile(file, reader);
            } catch (IOException exception) {
                if (PROJECT_DEBUG) LoggingUtil.error(exception.getMessage());
                return null;
            }
        });
    }

    public static JsonFile fromNormal(File file) {
        try {
            Reader reader = new BufferedReader(new FileReader(file));
            return new JsonFile(file, reader);
        } catch (IOException exception) {
            if (PROJECT_DEBUG) LoggingUtil.error(exception.getMessage());
            return null;
        }
    }

    /**
     * New file completable future.
     *
     * @param fileString the file string
     * @param json       the json
     */
    public static void newFile(String fileString, JsonElement json) {
        if (json == null || json instanceof JsonNull) json = new JsonObject();
        JsonElement finalJson = json;
        try {
            File file = new File(fileString);
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                LoggingUtil.error("Cannot create directory " + file.getParentFile().getAbsolutePath());
            }

            if (!file.exists() && !file.createNewFile()) return;
            if (!file.exists()) return;
            Files.writeString(file.toPath(), gson.toJson(finalJson));
        } catch (Exception ex) {
            LoggingUtil.error(ex.getMessage());
        }
    }

    /**
     * Write.
     *
     * @param fileString the file string
     * @param json       the json
     */
    public static void write(String fileString, JsonElement json) {
        if (json == null || json instanceof JsonNull) json = new JsonObject();
        final String data = gson.toJson(json);
        try {
            File file = new File(fileString);
            if (!file.exists() && !file.isFile()) return;
            Files.writeString(file.toPath(), data);
        } catch (Exception ex) {
            if (LOGGING_LEVEL >= 1) LoggingUtil.error(ex.getLocalizedMessage());
        }
    }

    public static File serchFile(String filename) {
        return serchFile(filename, aRoot);
    }

    public static File serchFile(String filename, File directory) {
        Deque<File> fileDeque = new ArrayDeque<>();
        File current;
        fileDeque.add(directory);

        while ((current = fileDeque.pollFirst()) != null) {
            File[] files = current.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        fileDeque.offerLast(file);
                    } else if (file.getName().equals(filename)) {
                        return file;
                    }
                }
            }
        }
        return null;
    }

    public static String[] fromDirectory(final String directoryPath) {
        try {
            File folder = new File(directoryPath);
            if (folder.isDirectory() && folder.canRead()) {
                var files = folder.listFiles((File::isFile));
                if (files != null) {
                    return Arrays.stream(files)
                            .map(File::getPath)
                            .filter(file -> file.endsWith(".json"))
                            .toArray(String[]::new);
                }
            }
        } catch (Exception ex) {
            LoggingUtil.enchantedError(ex, ex.getStackTrace(), "FromDirectory");
        }
        return new String[0];
    }
}
