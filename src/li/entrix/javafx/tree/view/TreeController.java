package li.entrix.javafx.tree.view;

import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TreeController {

    private BaseShape root;
    private Map<Integer, BaseShape> treeModel = new HashMap<>();
    private TreeView treeView;
    private ShapeCreator shapeCreator;
    private final int startX, startY;
    private ConcurrentLinkedDeque<Integer[]> deque = new ConcurrentLinkedDeque<>();


    public TreeController(TreeView treeView, ShapeCreator shapeCreator, int startX, int startY) {
        this.treeView = treeView;
        this.shapeCreator = shapeCreator;
        this.startX = startX;
        this.startY = startY;
    }

    public void redrawTree(List<FillingEntry> entries) {
        treeView.clearScreen();
        treeModel.clear();
        entries.forEach(e -> addNode(e.keyOne, e.keyTwo, e.cl == NodeColor.RED ? Color.RED : Color.BLACK));
    }


    public void swapShapes(List<FillingEntry> swapEntries, List<FillingEntry> redrawEntries) {
        swapEntries.forEach(e -> swapNodes(e.keyOne, e.keyTwo, () -> { redrawTree(redrawEntries); }));
    }

    public void addNode(Object keyOne, Object keyTwo, Color cl) {
        BaseShape bs1 = treeModel.get(keyOne),
                bs2 = createNode(bs1, keyTwo.toString());
        treeModel.put((Integer) keyTwo, bs2);
        treeView.addNode(bs1, bs2, cl);
    }

    private BaseShape createNode(BaseShape bs1, String keyTwo) {
        if (bs1 == null) {
            root = shapeCreator.createNode(startX, startY, keyTwo, null);
            return root;
        }
        int startX, startY = bs1.centerY + 100;
        int val1 = Integer.valueOf(bs1.text), val2 = Integer.valueOf(keyTwo);
        BaseShape bs = null;
        if (val1 > val2) {
            startX = bs1.parent == null ?
                    bs1.centerX - bs1.centerX / 2 :
                    bs1.centerX - Math.abs(bs1.parent.centerX - bs1.centerX) / 2;
            bs = shapeCreator.createNode(startX, startY, keyTwo, bs1);
            bs1.rightChild(bs);
        }
        else {
            startX = bs1.parent == null ?
                    bs1.centerX + bs1.centerX / 2 :
                    bs1.centerX + Math.abs(bs1.parent.centerX - bs1.centerX) / 2;
            bs = shapeCreator.createNode(startX, startY, keyTwo, bs1);
            bs1.leftChild(bs);
        }
        return bs;
    }

    public boolean removeNode(Object keyOne, Object keyTwo, Color cl) {
        BaseShape bs1 = treeModel.get(keyOne), bs2 = treeModel.get(keyTwo);
        LinkedList<BaseShape> doubleQueue = new LinkedList<>();
        LinkedList<BaseShape> queue = new LinkedList<>();
        if (bs1 != null) {
            if (bs1.children[0] == bs2) bs1.children[0] = null;
            if (bs1.children[1] == bs2) bs1.children[1] = null;
        }
        if (bs2.children[0] != null) {
            treeView.eraseNode(bs2, bs2.children[0], cl);
//            bs2.children[0].centerX = bs2.centerX;
//            bs2.children[0].centerY = bs2.centerY;
        }
        else if (bs2.children[1] != null) {
            treeView.eraseNode(bs2, bs2.children[1], cl);
//            bs2.children[1].centerX = bs2.centerX;
//            bs2.children[1].centerY = bs2.centerY;
        }
        treeView.eraseNode(bs1, bs2, cl);
        if (bs2.children[0] != null) {
            doubleQueue.add(bs1);
            doubleQueue.add(bs2.children[0]);
            treeView.eraseLine(bs2, bs2.children[0]);
            queue.add(bs2.children[0]);
        }
        if (bs2.children[1] != null) {
            doubleQueue.add(bs1);
            doubleQueue.add(bs2.children[1]);
            treeView.eraseLine(bs2, bs2.children[1]);
            queue.add(bs2.children[1]);
        }
        while (!queue.isEmpty()) {
            BaseShape bs = queue.poll();
            if (bs.children[0] != null) {
                doubleQueue.add(bs);
                doubleQueue.add(bs.children[0]);
                queue.add(bs2.children[0]);
            }
            if (bs.children[1] != null) {
                doubleQueue.add(bs);
                doubleQueue.add(bs.children[1]);
                queue.add(bs2.children[1]);
            }
        }
        LinkedList<BaseShape> tripleQueue = (LinkedList<BaseShape>) doubleQueue.clone();
        while (!doubleQueue.isEmpty()) {
            BaseShape rbs1 = doubleQueue.poll(),
                    rbs2 = doubleQueue.poll();
            treeView.eraseNode(rbs1, rbs2, null);
        }
        while (!tripleQueue.isEmpty()) {
            BaseShape rbs1 = tripleQueue.pop(),
                    rbs2 = tripleQueue.pop();
            BaseShape newRb2 = createNode(bs1, rbs2.getText());
            newRb2.setColor(rbs2.getColor());
            treeModel.put(Integer.valueOf(rbs2.getText()), newRb2);
            treeView.addNode(rbs1, newRb2, rbs2.getColor());
        }
        return treeModel.remove(keyTwo) == null;
    }

    public void swapNodes(Object keyOne, Object keyTwo, Runnable run) {
        deque.add(new Integer[]{(Integer) keyOne, (Integer) keyTwo});
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " before");
                treeView.queue.take();
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + " after");
                Integer[] arr = deque.poll();
                BaseShape bs1 = treeModel.get(arr[0]), bs2 = treeModel.get(arr[1]);
//                if (bs1.children[0] == bs2) {
//                    if (bs1.parent != null) {
//                        if (bs1.parent.children[0] == bs1)
//                            bs1.parent.children[0] = bs2;
//                        else
//                            bs1.parent.children[1] = bs2;
//                    }
//                    BaseShape sw = bs1.children[1];
//                    bs1.children[1] = bs2.children[1];
//                    bs2.children[1] = sw;
//                    bs1.children[0] = bs2.children[0];
//                    bs2.children[0] = bs1;
//                }
//                else {
//                    if (bs1.parent != null) {
//                        if (bs1.parent.children[0] == bs1)
//                            bs1.parent.children[0] = bs2;
//                        else
//                            bs1.parent.children[1] = bs2;
//                    }
//                    BaseShape sw = bs1.children[0];
//                    bs1.children[0] = bs2.children[0];
//                    bs2.children[0] = sw;
//                    bs1.children[1] = bs2.children[1];
//                    bs2.children[1] = bs1;
//                }
                treeView.swapNodes(bs1, bs2, run);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        service.shutdown();

    }
}
