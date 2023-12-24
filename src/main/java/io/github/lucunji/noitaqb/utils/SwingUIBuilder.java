package io.github.lucunji.noitaqb.utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * A factory class for constructing Swing UI with more natural semantics
 *
 * @param <T> type of the current component
 */
public class SwingUIBuilder<T extends JComponent> {
    private final T component;

    private SwingUIBuilder(T component) {
        this.component = component;
    }

    public static <T extends JComponent> SwingUIBuilder<T> create(T root) {
        return new SwingUIBuilder<>(root);
    }

    public SwingUIBuilder<T> child(Component child) {
        this.component.add(child);
        return this;
    }

    public SwingUIBuilder<T> child(Component child, Object constraints) {
        this.component.add(child, constraints);
        return this;
    }

    public SwingUIBuilder<T> child(String title, Component child) {
        this.component.add(title, child);
        return this;
    }

    public SwingUIBuilder<T> children(Component... children) {
        for (var child :  children) this.component.add(child);
        return this;
    }

    public SwingUIBuilder<T> layout(LayoutManager layoutManager) {
        this.component.setLayout(layoutManager);
        return this;
    }

    public SwingUIBuilder<T> boxLayout(int axis) {
        this.component.setLayout(new BoxLayout(this.component, axis));
        return this;
    }

    public SwingUIBuilder<T> margin(int unified) {
        this.component.setBorder(BorderFactory.createEmptyBorder(unified, unified, unified, unified));
        return this;
    }

    public SwingUIBuilder<T> margin(int topBottom, int leftRight) {
        this.component.setBorder(BorderFactory.createEmptyBorder(topBottom, leftRight, topBottom, leftRight));
        return this;
    }

    public SwingUIBuilder<T> margin(int top, int left, int bottom, int right) {
        this.component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return this;
    }

    public SwingUIBuilder<T> border(Border border) {
        this.component.setBorder(border);
        return this;
    }

    public SwingUIBuilder<T> disable() {
        this.component.setEnabled(false);
        return this;
    }

    public SwingUIBuilder<T> run(Consumer<T> func) {
        func.accept(this.component);
        return this;
    }

    public SwingUIBuilder<T> onAction(ActionListener listener) {
        if (this.component instanceof AbstractButton) ((AbstractButton) this.component).addActionListener(listener);
        else throw new UnsupportedOperationException("Not an instance of AbstractButton");
        return this;
    }

    public T finish() {
        return this.component;
    }
}
