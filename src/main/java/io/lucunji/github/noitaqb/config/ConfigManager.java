package io.lucunji.github.noitaqb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.lucunji.github.noitaqb.utils.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class ConfigManager {
    private final Gson loader;
    private final Gson saver;
    private final Path cfgFile;
    private Configs cfg = null;

    public ConfigManager(Path cfgFile) {
        this.loader = new Gson();
        this.saver = new GsonBuilder().setPrettyPrinting().create();
        this.cfgFile = cfgFile;
    }

    public void loadOrCreate() {
        if (!cfgFile.toFile().exists()) {
            System.out.println("Could not find config file at " + cfgFile + ", creating a default one");
            FileUtils.ensureDir(cfgFile.getParent());
            try (var writer = new BufferedWriter(new FileWriter(cfgFile.toFile()))) {
                saver.toJson(new Configs(), writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (var reader = new BufferedReader(new FileReader(cfgFile.toFile()))) {
            cfg = loader.fromJson(reader, Configs.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Configs getConfigs() {
        if (cfg == null) throw new NoSuchElementException("No config loaded");
        return cfg;
    }
}
