package li.entrix.javafx.shape;

import javafx.scene.canvas.Canvas;

public class ShapeFacade {

    public static FillingShape drawCircleWeak(double centerX, double centerY, int radius, Canvas canvas) {
        ReferenceCreator referenceCreator = new WeakReferenceCreator();
        return new CircleShape((int)centerX, (int)centerY, radius, canvas, referenceCreator);
    }

    public static FillingShape drawCircleSoft(double centerX, double centerY, int radius, Canvas canvas) {
        ReferenceCreator referenceCreator = new SoftReferenceCreator();
        return new CircleShape((int)centerX, (int)centerY, radius, canvas, referenceCreator);
    }

    public static FillingShape drawCirclePhantom(double centerX, double centerY, int radius, Canvas canvas) {
        ReferenceCreator referenceCreator = new PhantomReferenceCreator();
        return new CircleShape((int)centerX, (int)centerY, radius, canvas, referenceCreator);
    }
}
