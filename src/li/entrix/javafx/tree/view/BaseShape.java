package li.entrix.javafx.tree.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public abstract class BaseShape {

    final String text;
    int centerX;
    int centerY;
    private Color cl;
    BaseShape parent;
    BaseShape children[] = new BaseShape[2];

    public String getText() {
        return text;
    }

    private Canvas canvas;
    private int height, width;

    BaseShape(int centerX, int centerY, String text, Canvas canvas, BaseShape parent) {
        if (text == null) throw new NullPointerException("Text cannot be null");
        this.canvas = canvas;
        this.height = (int) canvas.getHeight();
        this.width = (int) canvas.getWidth();
        this.text = text;
        this.centerX = centerX;
        this.centerY = centerY;
        this.parent = parent;
    }

    public Color getColor() {
        return cl;
    }

    public void setColor(Color cl) {
        this.cl = cl;
    }

    public void leftChild(BaseShape bs) {
        children[0] = bs;
    }

    public void rightChild(BaseShape bs) {
        children[1] = bs;
    }

    public abstract void fill();

    public abstract void erase(Color cl);

    void fillText(int x, int y, int maxWidth, String text) {
        canvas.getGraphicsContext2D().setFill(Color.WHITE);
        Font font = Font.getDefault();
        canvas.getGraphicsContext2D().setFont(Font.font(font.getFamily(), FontWeight.BOLD, font.getSize()));
        canvas.getGraphicsContext2D().fillText(text, x, y, maxWidth);
    }

    void fillPixel(int x, int y, Color cl) {
        PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();
        if (x > 0 && x < width && y > 0 && y < height) {
            pw.setColor(x, y, cl);
        }
    }
}
