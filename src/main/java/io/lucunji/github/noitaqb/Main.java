package io.lucunji.github.noitaqb;

import io.lucunji.github.noitaqb.config.ConfigManager;
import io.lucunji.github.noitaqb.utils.FileUtils;

import javax.swing.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Main {
    private static  final int DEFAULT_WIDTH = 480;
    private static final int DEFAULT_HEIGHT = 640;

    public static void main(String[] args) {
        System.out.println("Loading configs");
        String executablePath;
        try {
            executablePath = FileUtils.getExecutablePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        var cfgManager = new ConfigManager(Paths.get(executablePath, "configs.json"));
        cfgManager.loadOrCreate(); // no lazy-loading because we want to identify errors early
        System.out.println("Configs loaded");

        System.out.println("Launching main window");
        JFrame frame = new JFrame("QuickBackup");
        var qb = new MainWindow(cfgManager);
        frame.setContentPane(qb.rootPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        frame.setVisible(true);
        System.out.println("Main window launched");
    }
}