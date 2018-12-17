package li.entrix.javafx.tree.view;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.concurrent.ArrayBlockingQueue;

public class TreeView {

    private Canvas canvas;

    private Color fontColor;

    ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<>(1);


    {
        queue.offer(new Object());
    }

    public TreeView(Canvas canvas, Color fontColor) {
        this.canvas = canvas;
        this.fontColor = fontColor;
    }

    public void addNode(BaseShape bs1, BaseShape bs2, Color cl) {
        if (cl != null) bs2.setColor(cl);
        if (bs1 != null) {
            fillLine(bs1.centerX, bs1.centerY, bs2.centerX, bs2.centerY);
            bs1.fill();
        }
        bs2.fill();
    }

    public void eraseNode(BaseShape bs1, BaseShape bs2, Color cl) {
        if (bs1 == null) {
            bs2.erase(fontColor);
            return;
        }
        eraseLine(bs1, bs2);
        if (cl != null) bs1.setColor(cl);
        bs1.fill();
        bs2.erase(fontColor);
    }

    public void eraseLine(BaseShape bs1, BaseShape bs2) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(fontColor);
        PixelWriter pw = gc.getPixelWriter();
        int xMin = bs1.centerX, xMax = bs2.centerX;
        double k = (double) (bs1.centerY - bs2.centerY) / (bs1.centerX - bs2.centerX),
                b = bs2.centerY - bs2.centerX * k;
        if (xMax < xMin) {
            int sw = xMax;
            xMax = xMin;
            xMin = sw;
        }
        for (int x = xMin; x <= xMax; ++x) {
            int y = (int)(k*x + b);
            pw.setColor(x, y, fontColor);
            pw.setColor(x, y - 1, fontColor);
            pw.setColor(x, y - 2, fontColor);
            pw.setColor(x - 1, y, fontColor);
            pw.setColor(x - 2, y, fontColor);
            pw.setColor(x, y + 1, fontColor);
            pw.setColor(x, y + 2, fontColor);
            pw.setColor(x + 1, y, fontColor);
            pw.setColor(x + 2, y, fontColor);
        }
    }

    private void fillLine(BaseShape bs1, BaseShape bs2) {
        Point2D start = getPointOnCircle(bs1.centerX, bs1.centerY, bs2.centerX, bs2.centerY),
                end = getPointOnCircle(bs2.centerX, bs2.centerY, bs1.centerX, bs1.centerY);
        canvas.getGraphicsContext2D().strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
    }

    private void fillLine(int x1, int y1, int x2, int y2) {
        canvas.getGraphicsContext2D().strokeLine(x1, y1, x2, y2);
    }

    public Point2D getPointOnCircle(int x1, int y1, int x2, int y2) {
        double len = Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2-y1)),
                coeff = 20.0 / len;
        return new Point2D((x2 - x1)*coeff + x1, (y2 - y1)*coeff + y1);
    }

//    public void swapNodes(BaseShape bs1, BaseShape bs2, boolean first, boolean second) {
//        swapNodes
//    }

    public void swapNodes(BaseShape bs1, BaseShape bs2, Runnable run) {
        try {
            double k = (double) (bs1.centerY - bs2.centerY) / (bs1.centerX - bs2.centerX),
                    b = bs2.centerY - bs2.centerX * k,
                    len = Math.sqrt((bs2.centerX - bs1.centerX)*(bs2.centerX - bs1.centerX) + (bs2.centerY - bs1.centerY)*(bs2.centerY - bs1.centerY));
            int xMax = bs1.centerX, xMin = bs2.centerX;
            final int startX1 = bs1.centerX, startY1 = bs1.centerY, startX2 = bs2.centerX, startY2 = bs2.centerY;
            boolean invert = false;
            if (xMax < xMin) {
                invert = true;
                int sw = xMax;
                xMax = xMin;
                xMin = sw;
            }

            IntegerProperty x  = new SimpleIntegerProperty();

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new KeyValue(x, xMin)
                    ),
                    new KeyFrame(Duration.seconds(1),
                            new KeyValue(x, xMax)
                    )
            );
            timeline.setAutoReverse(true);
            timeline.setCycleCount(xMax - xMin);

            boolean finalInvert = invert;
            int finalXMax = xMax;
            int finalXMin = xMin;
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                     if ((finalInvert && (bs1.centerX == finalXMax || bs1.centerX == finalXMax - 1)) ||
                             (!finalInvert && (bs2.centerX == finalXMax || bs2.centerX == finalXMax - 1))) {
                        fillLine(bs1.centerX, bs1.centerY, bs2.centerX, bs2.centerY);
                        bs1.fill();
                        bs2.fill();
                         if (run != null) run.run();
                         queue.offer(new Object());
                         stop();
                         return;
                    }
                    bs1.erase(fontColor);
                    bs2.erase(fontColor);
                    fillLine(startX1, startY1, startX2, startY2);
                    if (finalInvert) {
                        bs1.centerX = x.intValue();
                        bs1.centerY = (int) (k * bs1.centerX + b);
                        bs1.fill();
                        bs2.centerX = finalXMin + finalXMax - x.intValue();
                        bs2.centerY = (int) (k * bs2.centerX + b);
                        bs2.fill();
                    } else {
                        bs1.centerX = finalXMin + finalXMax - x.intValue();
                        ;
                        bs1.centerY = (int) (k * bs1.centerX + b);
                        bs1.fill();
                        bs2.centerX = x.intValue();
                        ;
                        bs2.centerY = (int) (k * bs2.centerX + b);
                        bs2.fill();
                    }
                }
            };
            timeline.play();
            timer.start();
//            try {
//                for (;;) {
//                    if ((invert && bs1.centerX == xMax) || (!invert && bs2.centerX == xMax)) {
//                        fillLine(bs1.centerX, bs1.centerY, bs2.centerX, bs2.centerY);
//                        bs1.fill();
//                        bs2.fill();
//                        break;
//                    }
//                    fillLine(startX1, startY1, startX2, startY2);
//                    bs1.erase(fontColor);
//                    bs2.erase(fontColor);
//                    if (invert) {
//                        bs1.centerX = bs1.centerX + 1;
//                        bs1.centerY = (int) (k * bs1.centerX + b);
//                        bs1.fill();
//                        bs2.centerX = bs2.centerX - 1;
//                        bs2.centerY = (int) (k * bs2.centerX + b);
//                        bs2.fill();
//                    } else {
//                        bs1.centerX = bs1.centerX - 1;
//                        ;
//                        bs1.centerY = (int) (k * bs1.centerX + b);
//                        bs1.fill();
//                        bs2.centerX = bs2.centerX + 1;
//                        ;
//                        bs2.centerY = (int) (k * bs2.centerX + b);
//                        bs2.fill();
//                    }
////                    if (len > 200)
//                        Thread.sleep(1);
////                    else
////                        Thread.sleep(5);
//                }
//            } catch (Throwable ex) {
//                ex.printStackTrace();
//            }
//            if (run != null) run.run();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void clearScreen() {
        canvas.getGraphicsContext2D().setFill(fontColor);
        canvas.getGraphicsContext2D().fillRect(0,0, canvas.getWidth(), canvas.getHeight());
    }
}
