package li.entrix.javafx.tree.view;

public interface ShapeCreator {

    BaseShape createNode(int centerX, int centerY, String text, BaseShape parent);
}
