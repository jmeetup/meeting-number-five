package li.entrix.javafx.tree.view;

import javafx.scene.canvas.Canvas;


public class TreeCircleCreator implements ShapeCreator {

    final private Canvas canvas;

    final private int radius;

    public TreeCircleCreator(Canvas canvas, int radius) {
        this.canvas = canvas;
        this.radius = radius;
    }

    @Override
    public BaseShape createNode(int centerX, int centerY, String text, BaseShape parent) {
        return new TreeCircle(centerX, centerY, radius, text, canvas, parent);
    }
}
