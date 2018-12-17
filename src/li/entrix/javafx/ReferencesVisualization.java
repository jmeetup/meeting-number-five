/*
 * Copyright (c) 2011, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package li.entrix.javafx;

import li.entrix.javafx.shape.FillingShape;
import li.entrix.javafx.shape.ShapeFacade;
import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ReferencesVisualization extends Application {

    private static final AtomicInteger refType = new AtomicInteger(0);

    private static ScheduledService scheduledService;

    private static List<FillingShape> shapes = new LinkedList<>();

    private static Point2D currentPos = new Point2D(200, 200);

    public static void main(String[] args) {
        launch(args);
    }

    private static void fillRedCross(Canvas canvas) {
        fillCross(canvas, Color.RED);
    }

    private static void eraseRedCross(Canvas canvas) {
        fillCross(canvas, Color.BLACK);
    }

    private static void fillCross(Canvas canvas, Color cl) {
        int x = (int) currentPos.getX(), y = (int) currentPos.getY();
        PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();

        fillPixel(x, y, canvas, pw, cl);
        fillPixel(x + 1, y, canvas, pw, cl);
        fillPixel(x + 2, y, canvas, pw, cl);
        fillPixel(x + 3, y, canvas, pw, cl);
        fillPixel(x + 1, y + 1, canvas, pw, cl);
        fillPixel(x + 2, y + 1, canvas, pw, cl);
        fillPixel(x + 3, y + 1, canvas, pw, cl);
        fillPixel(x + 1, y - 1, canvas, pw, cl);
        fillPixel(x + 2, y - 1, canvas, pw, cl);
        fillPixel(x + 3, y - 1, canvas, pw, cl);

        fillPixel(x, y + 1, canvas, pw, cl);
        fillPixel(x, y + 2, canvas, pw, cl);
        fillPixel(x, y + 3, canvas, pw, cl);
        fillPixel(x - 1, y + 1, canvas, pw, cl);
        fillPixel(x - 1, y + 2, canvas, pw, cl);
        fillPixel(x - 1, y + 3, canvas, pw, cl);
        fillPixel(x + 1, y + 1, canvas, pw, cl);
        fillPixel(x + 1, y + 2, canvas, pw, cl);
        fillPixel(x + 1, y + 3, canvas, pw, cl);

        fillPixel(x - 1, y, canvas, pw, cl);
        fillPixel(x - 2, y, canvas, pw, cl);
        fillPixel(x - 3, y, canvas, pw, cl);
        fillPixel(x - 1, y + 1, canvas, pw, cl);
        fillPixel(x - 2, y + 1, canvas, pw, cl);
        fillPixel(x - 3, y + 1, canvas, pw, cl);
        fillPixel(x - 1, y - 1, canvas, pw, cl);
        fillPixel(x - 2, y - 1, canvas, pw, cl);
        fillPixel(x - 3, y - 1, canvas, pw, cl);

        fillPixel(x, y - 1, canvas, pw, cl);
        fillPixel(x, y - 2, canvas, pw, cl);
        fillPixel(x, y - 3, canvas, pw, cl);
        fillPixel(x + 1, y - 1, canvas, pw, cl);
        fillPixel(x + 1, y - 2, canvas, pw, cl);
        fillPixel(x + 1, y - 3, canvas, pw, cl);
        fillPixel(x - 1, y - 1, canvas, pw, cl);
        fillPixel(x - 1, y - 2, canvas, pw, cl);
        fillPixel(x - 1, y - 3, canvas, pw, cl);

        fillPixel(x + 1, y + 1, canvas, pw, cl);
        fillPixel(x + 1, y - 1, canvas, pw, cl);
        fillPixel(x - 1, y + 1, canvas, pw, cl);
        fillPixel(x - 1, y - 1, canvas, pw, cl);
    }


    private static void fillPixel(int x, int y, Canvas canvas, PixelWriter pw, Color cl) {
        if (x > 0 && x < canvas.getWidth() && y > 0 && y < canvas.getHeight()) {
            pw.setColor(x, y, cl);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        current(primaryStage);
    }

    private static void current(Stage primaryStage) {
        VBox rootVBox = new VBox();
        Group rootGroup = new Group();
        Scene scene = new Scene(rootVBox, 800, 600, Color.BLACK);
        primaryStage.setScene(scene);

        Canvas canvas = new Canvas(scene.getWidth(), scene.getHeight());

        MenuBar menuBar = new MenuBar();

        Menu menu = new Menu("References");
        MenuItem menuSoft = new MenuItem("SoftReference");
        menuSoft.setOnAction(t -> refType.set(0));
        MenuItem menuWeak = new MenuItem("WeakReference");
        menuWeak.setOnAction(t -> refType.set(1));
        MenuItem menuPhantom = new MenuItem("PhantomReference");
        menuPhantom.setOnAction(t -> refType.set(2));
        MenuItem menuClearPhantom = new MenuItem("Clear phantom references");
        menuClearPhantom.setOnAction(t -> {
            shapes.forEach(FillingShape::callClearOnRefs);
            fillRedCross(canvas);
        });
        MenuItem menuClearScreen = new MenuItem("Clear all screen");
        menuClearScreen.setOnAction(t -> {
            synchronized(scene) {
                canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }
        });
        menu.getItems().addAll(menuSoft, menuWeak, menuPhantom, menuClearPhantom, menuClearScreen);
        menuBar.getMenus().addAll(menu);

        ((VBox) scene.getRoot()).getChildren().addAll(menuBar);
        ((VBox) scene.getRoot()).getChildren().addAll(rootGroup);

        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        fillRedCross(canvas);
        rootGroup.getChildren().add(canvas);

        scene.setOnKeyPressed(event -> {
            FillingShape fs2 = null;
            synchronized (scene) {
                eraseRedCross(canvas);
                switch (event.getCode()) {
                    case LEFT:
                        if ((currentPos.getX() - 10) >= 0)
                            currentPos = new Point2D(currentPos.getX() - 8, currentPos.getY());
                        break;
                    case RIGHT:
                        if ((currentPos.getX() + 10) <= scene.getWidth())
                            currentPos = new Point2D(currentPos.getX() + 8, currentPos.getY());
                        break;
                    case UP:
                        if ((currentPos.getY() - 10) >= 0)
                            currentPos = new Point2D(currentPos.getX(), currentPos.getY() - 8);
                        break;
                    case DOWN:
                        if ((currentPos.getY() + 10) <= scene.getHeight())
                            currentPos = new Point2D(currentPos.getX(), currentPos.getY() + 8);
                        break;
                    case P:
                        shapes.forEach(FillingShape::callClearOnRefs);
                        fillRedCross(canvas);
                        return;
                    default:
                        return;
                }
                switch (refType.get()) {
                    case 0:
                        fs2 = ShapeFacade.drawCircleSoft(currentPos.getX(), currentPos.getY(), 10, canvas);
                        break;
                    case 1:
                        fs2 = ShapeFacade.drawCircleWeak(currentPos.getX(), currentPos.getY(), 10, canvas);
                        break;
                    case 2:
                        fs2 = ShapeFacade.drawCirclePhantom(currentPos.getX(), currentPos.getY(), 10, canvas);
                        break;
                }
                shapes.add(fs2);
                fs2.fill();
                fillRedCross(canvas);
            }
        });

        primaryStage.show();

        scheduledService = new ScheduledService() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        synchronized (scene) {
                            List<FillingShape> localShapes = new LinkedList<>();
                            System.gc();
                            for (FillingShape fs : shapes) {
                                if (!fs.isEmpty()) {
                                    if (fs.refill()) localShapes.add(fs);
                                }
                            }
                            shapes.removeAll(localShapes);
                            return null;
                        }
                    }
                };
            }
        };
        scheduledService.setPeriod(Duration.millis(3000));
        scheduledService.start();
    }
}
