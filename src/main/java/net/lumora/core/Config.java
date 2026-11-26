package net.lumora.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("lumora.json");

    public Map<String, Boolean> modules = new HashMap<>();
    public int theme = 0;
    public int crosshairStyle = 0;
    /** 0 = off, 1 = Opaline, 2 = Nocturne, 3 = Galaxy */
    public int sky = 0;

    public static Config load() {
        try (Reader r = Files.newBufferedReader(FILE)) {
            Config c = GSON.fromJson(r, Config.class);
            if (c != null) return c;
        } catch (Exception ignored) { }
        return new Config();
    }

    public void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(this, w);
        } catch (Exception e) {
            System.err.println("[Lumora] Could not save config: " + e);
        }
    }
}
