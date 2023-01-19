package io.lucunji.github.noitaqb;

import io.lucunji.github.noitaqb.config.Configs;

import javax.swing.*;

public class Main {
    private static  final int DEFAULT_WIDTH = 480;
    private static final int DEFAULT_HEIGHT = 640;
    public static Configs configs;

    public static void main(String[] args) {
        configs = new Configs();

        JFrame frame = new JFrame("QuickBackup");
        var qb = new MainWindow();
        frame.setContentPane(qb.rootPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        frame.setVisible(true);
    }
}