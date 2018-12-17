package li.entrix.javafx.tree.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class TreeCircle extends BaseShape {

    int radius;

    NodeColor nodeColor = NodeColor.BLACK;

    public TreeCircle(int centerX, int centerY, int radius, String text, Canvas canvas, BaseShape parent) {
        super(centerX, centerY, text, canvas, parent);
        this.radius = radius;
    }

    @Override
    public void setColor(Color cl) {
        super.setColor(cl);
        if (cl == Color.RED)
            nodeColor = NodeColor.RED;
        else

            nodeColor = NodeColor.BLACK;
    }

    public void setNodeColor(NodeColor nodeColor) {
        this.nodeColor = nodeColor;
    }

    @Override
    public void fill() {
        fill(null);
    }

    private void fill(Color eraseColor) {
        Color cl = eraseColor == null ?
                nodeColor.getColor() : eraseColor;
        int startX = centerX - radius, startY = centerY - radius, lenX = startX + radius * 2, lenY = startY + radius * 2;
        for (int x = startX; x < lenX; ++x) {
            for (int y = startY; y < lenY; ++y) {
                int xVal = Math.abs(x - centerX), yVal = Math.abs(y - centerY);
                double hypo = Math.sqrt(xVal * xVal + yVal * yVal);
                if (hypo < radius) {
                    fillPixel(x, y, cl);
                }
            }
        }
        if (eraseColor == null)
            fillText(text.length() == 1 ? centerX - 5 : centerX - 10, centerY + 5, radius, text);
    }

    @Override
    public void erase(Color cl) {
        fill(cl);
    }
}
