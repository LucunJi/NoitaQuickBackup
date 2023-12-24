package io.github.lucunji.noitaqb.utils;

import java.awt.*;

public class SwingUtils {
    public static void refreshDisplay(Component component) {
        component.revalidate();
        component.repaint();
    }
}
