import java.awt.*;

/** Stores the position, dimensions, image, and scoring state of a pipe. */
public class Pipe {
    private int posX;
    private int posY;
    private int width;
    private int height;
    private Image image;
    private int VelocityX;
    boolean passed;

    /** Creates a pipe with the supplied position, dimensions, and image. */
    public Pipe(int posX, int posY, int width, int height, Image image) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.image = image;

        this.VelocityX = 0;
        this.passed = false;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getPosY() {
        return posY;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setImage(String path) {
        this.image = Toolkit.getDefaultToolkit().getImage(path);
    }

    public Image getImage() {
        return image;
    }

    public void setVelocityX(int velocityX) {
        this.VelocityX = velocityX;
    }

    public int getVelocityX() {
        return VelocityX;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public boolean getPassed() {
        return passed;
    }
}
