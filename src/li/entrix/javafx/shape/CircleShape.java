package li.entrix.javafx.shape;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class CircleShape extends FillingShape {

    private int centerX, centerY, radius;

    public CircleShape(int centerX, int centerY, int radius, Canvas canvas, ReferenceCreator referenceCreator) {
        super(canvas, referenceCreator);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    @Override
    public void fill() {
        fillCircle(Color.WHITE);
    }

    @Override
    public void clear() {
        fillCircle(Color.BLACK);
    }

    private void fillCircle(Color cl) {
        int startX = centerX - radius, startY = centerY - radius, lenX = startX + radius * 2, lenY = startY + radius * 2;
        for (int x = startX; x < lenX; ++x)
            for (int y = startY; y < lenY; ++y) {
                int xVal = Math.abs(x - centerX), yVal = Math.abs(y - centerY);
                double hypo = Math.sqrt(xVal * xVal + yVal * yVal);
                if (hypo <= radius) {
                    fillPixel(x, y, cl);
                }
            }
    }
}
