package li.entrix.javafx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import li.entrix.javafx.tree.view.TreeCircleCreator;
import li.entrix.javafx.tree.view.TreeController;
import li.entrix.javafx.tree.view.TreeView;
import li.entrix.javafx.tree.AVLTree;
import li.entrix.javafx.tree.TreeUtils;


public class TreeVisualization  extends Application {

    private AVLTree<Integer, String> tree = new AVLTree<>();

    public static void main(String[] args) {
        launch(args);
    }

    private static StringBuffer sb = new StringBuffer();
    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 1280, 700, Color.WHITE);
        primaryStage.setScene(scene);
        Canvas canvas = new Canvas(scene.getWidth(), scene.getHeight());
        root.getChildren().addAll(canvas);
        TreeController controller =
                new TreeController(new TreeView(canvas, Color.WHITE),
                        new TreeCircleCreator(canvas, 20),
                        640,
                        30);
        TreeUtils.setController(controller);
//        controller.addNode(null, 12, Color.RED);
//        controller.addNode(12, 9, Color.RED);
//        controller.addNode(9, 11, Color.BLACK);
//        controller.addNode(11, 13, Color.BLACK);
//        controller.addNode(11, 10, Color.BLACK);
//        controller.addNode(10, 14, Color.BLACK);
//        controller.addNode(13, 16, Color.BLACK);
//        controller.addNode(13, 5, Color.BLACK);
//        controller.addNode(12, 25, Color.BLACK);
//        controller.addNode(25, 24, Color.BLACK);
//        controller.addNode(25, 26, Color.BLACK);
//        controller.addNode(26, 21, Color.BLACK);
//        controller.addNode(24, 23, Color.RED);
//        controller.addNode(23, 20, Color.BLACK);
        primaryStage.show();
//        Thread.sleep(100);
//        controller.swapNodes(23, 20, null);
//        controller.swapNodes(24, 20, null);
//        controller.swapNodes(25, 20, null);
//        controller.swapNodes(12, 20, null);
        scene.setOnKeyPressed(event -> {
            synchronized (scene) {
                switch (event.getCode()) {
                    case DIGIT1:
                    case DIGIT2:
                    case DIGIT3:
                    case DIGIT4:
                    case DIGIT5:
                    case DIGIT6:
                    case DIGIT7:
                    case DIGIT8:
                    case DIGIT9:
                    case DIGIT0:
                        sb.append(event.getText());
                        break;
                    case ENTER:
                        if (sb.length() == 0) return;
                        tree.add(Integer.valueOf(sb.toString()), sb.toString());
//                        controller.addNode(21, Integer.valueOf(sb.toString()), Color.BLACK);
                        sb = new StringBuffer();
                        break;
                    case DELETE:
                        if (sb.length() == 0) return;
                        tree.remove(Integer.valueOf(sb.toString()));
                        sb = new StringBuffer();
                        break;
                    case E:
                        sb = new StringBuffer();
                        break;
                }
            }
//            ExecutorService service = Executors.newSingleThreadExecutor();
//            service.submit(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });
//            service.shutdown();

        });
    }
}

