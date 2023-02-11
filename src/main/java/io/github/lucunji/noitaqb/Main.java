package io.github.lucunji.noitaqb;

import io.github.lucunji.noitaqb.utils.FileUtils;
import io.github.lucunji.noitaqb.config.ConfigManager;
import io.github.lucunji.noitaqb.gui.MainWindow;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading configs");
        String executablePath;
        try {
            executablePath = FileUtils.getExecutablePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        var cfgManager = new ConfigManager(Path.of(executablePath).getParent().resolve("configs.json"));
        cfgManager.loadOrCreate(); // no lazy-loading because we want to identify errors early
        System.out.println("Configs loaded");

        System.out.println("Launching main window");
        JFrame frame = new JFrame("QuickBackup");
        frame.setContentPane(new MainWindow(cfgManager).getRootPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var general = cfgManager.getConfigs().getGeneral();
        frame.setBounds(general.getWindowX(), general.getWindowY(), general.getWindowWidth(), general.getWindowHeight());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                var general = cfgManager.getConfigs().getGeneral();
                var window = e.getWindow();
                general.setWindowX(window.getX());
                general.setWindowY(window.getY());
                general.setWindowWidth(window.getWidth());
                general.setWindowHeight(window.getHeight());
                cfgManager.save();
            }
        });
        frame.setVisible(true);
        System.out.println("Main window launched");
    }
}