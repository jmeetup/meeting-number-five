package li.entrix.javafx.shape;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public abstract class FillingShape {

    private static final Color DEFAULT_COLOR = Color.BLACK;

    private Canvas canvas;
    private ReferenceCreator referenceCreator;
    private int height, width;
    private Map<Point2D, Reference<?>> referenceMap = new HashMap<>();

    FillingShape(Canvas canvas, ReferenceCreator referenceCreator) {
        this.canvas = canvas;
        this.referenceCreator = referenceCreator;
        this.height = (int) canvas.getHeight();
        this.width = (int) canvas.getWidth();
    }

    public abstract void fill();

    public boolean isEmpty() {
        return referenceMap.isEmpty();
    }

    public boolean refill() {
        PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();
        if (referenceMap.isEmpty()) return true;
        int sz = referenceMap.size(), limit = Integer.MAX_VALUE, counter = 0;
        List<Point2D> list = new LinkedList<>();
        if (sz >= 100) limit = sz / 2;
        for (Map.Entry<Point2D, Reference<?>> entry : referenceMap.entrySet()) {
            if (entry.getValue().get() == null) {
                counter++;
                pw.setColor((int)entry.getKey().getX(), (int)entry.getKey().getY(), DEFAULT_COLOR);
                list.add(entry.getKey());
            }
            if (counter == limit) break;
        }
        list.forEach(p2d -> referenceMap.remove(p2d));
        return false;
    }

    public abstract void clear();

    public void callClearOnRefs() {
        referenceCreator.cleaAll();
    }

    void fillPixel(int x, int y, Color cl) {
        PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();
        if (x > 0 && x < width && y > 0 && y < height) {
            Point2D key = new Point2D(x, y);
            Reference<?> ref = referenceMap.get(key);
            if (ref == null) {
                referenceMap.put(key, referenceCreator.createReference(() -> pw.setColor(x, y, Color.CYAN)));
                pw.setColor(x, y, cl);
            }
            else if (ref.get() == null) {
                pw.setColor(x, y, DEFAULT_COLOR);
            }
        }
    }
}
