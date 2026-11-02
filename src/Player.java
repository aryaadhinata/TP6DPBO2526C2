import java.awt.*;

/** Stores the player's position, dimensions, image, and vertical velocity. */
public class Player {
    private int posX;
    private int posY;
    private int width;
    private int height;
    private Image image;
    private int velocityY = -0;

    /** Creates a player with the supplied initial state. */
    public Player(int posX, int posY, int width, int height, Image image, int velocityY) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.image = image;
        this.velocityY = velocityY;
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
        return  image;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    public int getVelocityY() {
        return velocityY;
    }
}
