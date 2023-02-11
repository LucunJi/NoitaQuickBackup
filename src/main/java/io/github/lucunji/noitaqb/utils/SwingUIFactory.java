package io.github.lucunji.noitaqb.utils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * A factory class for constructing Swing UI with more natural semantics
 *
 * @param <T> type of the current component
 */
public class SwingUIFactory<T extends JComponent> {
    private final T component;

    private SwingUIFactory(T component) {
        this.component = component;
    }

    public static <T extends JComponent> SwingUIFactory<T> create(T root) {
        return new SwingUIFactory<>(root);
    }

    public SwingUIFactory<T> child(JComponent child) {
        this.component.add(child);
        return this;
    }

    public SwingUIFactory<T> child(JComponent child, Object constraints) {
        this.component.add(child, constraints);
        return this;
    }

    public SwingUIFactory<T> child(String title, JComponent child) {
        this.component.add(title, child);
        return this;
    }

    public SwingUIFactory<T> children(JComponent... children) {
        for (var child :  children) this.component.add(child);
        return this;
    }

    public SwingUIFactory<T> layout(LayoutManager layoutManager) {
        this.component.setLayout(layoutManager);
        return this;
    }

    public SwingUIFactory<T> run(Consumer<T> func) {
        func.accept(this.component);
        return this;
    }

    public T finish() {
        return this.component;
    }
}