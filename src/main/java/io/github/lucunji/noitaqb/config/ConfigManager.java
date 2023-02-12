package io.github.lucunji.noitaqb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.lucunji.noitaqb.utils.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.NoSuchElementException;

/**
 * Do not handle exceptions here early
 */
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

    public void loadOrCreate() throws IOException {
        if (!cfgFile.toFile().exists()) {
            System.out.println("Could not find config file at " + cfgFile + ", creating a default one");
            FileUtils.ensureDir(cfgFile.getParent(), true);
            try (var writer = new BufferedWriter(new FileWriter(cfgFile.toFile()))) {
                saver.toJson(new Configs(), writer);
            }
        }

        try (var reader = new BufferedReader(new FileReader(cfgFile.toFile()))) {
            cfg = loader.fromJson(reader, Configs.class);
        }
    }

    public Configs getConfigs() {
        if (cfg == null) throw new NoSuchElementException("No config loaded");
        return cfg;
    }

    public void save() throws IOException {
        FileUtils.ensureDir(cfgFile.getParent(), true);
        try (var writer = new BufferedWriter(new FileWriter(cfgFile.toFile()))) {
            saver.toJson(cfg, writer);
        }
    }
}
