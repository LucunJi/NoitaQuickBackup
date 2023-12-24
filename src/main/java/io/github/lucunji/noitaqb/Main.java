package io.github.lucunji.noitaqb;

import com.formdev.flatlaf.FlatDarkLaf;
import io.github.lucunji.noitaqb.config.ConfigManager;
import io.github.lucunji.noitaqb.gui.MainWindow;
import io.github.lucunji.noitaqb.utils.FileUtils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {
    public static JFrame mainFrame;
    public static ConfigManager cfgManager;

    public static void main(String[] args) {
        System.out.println("Loading configs");
        cfgManager = new ConfigManager(FileUtils.getExecutablePath().getParent().resolve("configs.json"));
        try {
            cfgManager.loadOrCreate(); // no lazy-loading because we want to identify errors early
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Could not load configs", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("Configs loaded");

        System.out.println("Launching main window");
        FlatDarkLaf.setup();

        mainFrame = new JFrame("QuickBackup");
        mainFrame.setContentPane(new MainWindow().getRootPane());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var general = cfgManager.getConfigs().getGeneral();
        mainFrame.setBounds(general.getWindowX(), general.getWindowY(), general.getWindowWidth(), general.getWindowHeight());
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                var general = cfgManager.getConfigs().getGeneral();
                var window = e.getWindow();
                general.setWindowX(window.getX());
                general.setWindowY(window.getY());
                general.setWindowWidth(window.getWidth());
                general.setWindowHeight(window.getHeight());
                try {
                    cfgManager.save();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Could not save configs", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        mainFrame.setVisible(true);
        System.out.println("Main window launched");
    }
}
