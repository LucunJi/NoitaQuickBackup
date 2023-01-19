package io.lucunji.github.noitaqb;

import javax.swing.*;

public class Main {
    private static  final int DEFAULT_WIDTH = 480;
    private static final int DEFAULT_HEIGHT = 640;

    public static void main(String[] args) {
        JFrame frame = new JFrame("QuickBackup");
        var qb = new QuickBackup();
        frame.setContentPane(qb.rootPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        frame.setVisible(true);
    }
}