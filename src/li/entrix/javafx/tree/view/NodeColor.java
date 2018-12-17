package li.entrix.javafx.tree.view;

import javafx.scene.paint.Color;

public enum NodeColor {
    RED(Color.RED), BLACK(Color.BLACK);

    private Color color;

    NodeColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
