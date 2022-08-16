package io.github.techstreet.dfscript;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.techstreet.dfscript.commands.CommandManager;
import io.github.techstreet.dfscript.features.AuthHandler;
import io.github.techstreet.dfscript.features.UpdateAlerts;
import io.github.techstreet.dfscript.loader.Loader;
import io.github.techstreet.dfscript.screen.script.ScriptListScreen;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.util.AuthcodeResponse;
import io.github.techstreet.dfscript.script.util.ServercodeResponse;
import io.github.techstreet.dfscript.script.util.UploadResponse;
import io.github.techstreet.dfscript.util.Scheduler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import io.github.techstreet.dfscript.util.chat.ChatType;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.jmx.Server;

public class DFScript implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    public static final MinecraftClient MC = MinecraftClient.getInstance();

    public static final String MOD_NAME = "DFScript";
    public static final String MOD_ID = "dfscript";
    public static String MOD_VERSION;

    public static String PLAYER_UUID = null;
    public static String PLAYER_NAME = null;

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing");
        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));

        // allows FileDialog class to open without a HeadlessException
        System.setProperty("java.awt.headless", "false");

        PLAYER_UUID = MC.getSession().getUuid();
        PLAYER_NAME = MC.getSession().getUsername();

        MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString();

        Loader loader = Loader.getInstance();
        loader.load(new CommandManager());
        loader.load(new AuthHandler());
        loader.load(new ScriptManager());
        loader.load(new Scheduler());
        loader.load(new UpdateAlerts());

        LOGGER.info("Initialized");
    }

    public void onClose() {
        LOGGER.info("Closing...");

        LOGGER.info("Closed.");
    }
}
