package io.github.lucunji.noitaqb.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

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

    public SwingUIBuilder<T> child(JComponent child) {
        this.component.add(child);
        return this;
    }

    public SwingUIBuilder<T> child(JComponent child, Object constraints) {
        this.component.add(child, constraints);
        return this;
    }

    public SwingUIBuilder<T> child(String title, JComponent child) {
        this.component.add(title, child);
        return this;
    }

    public SwingUIBuilder<T> children(JComponent... children) {
        for (var child :  children) this.component.add(child);
        return this;
    }

    public SwingUIBuilder<T> layout(LayoutManager layoutManager) {
        this.component.setLayout(layoutManager);
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
