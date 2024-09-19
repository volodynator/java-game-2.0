import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bullet {
    GameObject object;

    // position:
    float startX;
    float startY;
    float x;
    float y;
    float range;

    // movement:
    boolean movingLeft;
    float speed;

    // features:
    int damage;
    int mana;

    // collision:
    BoundingBox boundingBox;
    boolean hasCollided = false;
    BufferedImage image;
    int width;
    int height;


    public Bullet(GameObject object,int damage, int mana, float range, float speed, BufferedImage image) throws IOException {
        this.object = object;
        this.startX = object.pos.x;
        this.startY = object.pos.y;
        this.x = startX;
        this.y = startY;
        this.range = range;
        this.speed = speed;
        this.damage = damage;
        this.mana = mana;
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        boundingBox = new BoundingBox(x, y, x + width, y + height);
    }

    public void update() {
        if (movingLeft) {
            x -= speed;
        } else {
            x += speed;
        }
        updateBoundingBox();
    }
    public void updateBoundingBox() {
        if (movingLeft) {

            boundingBox.min.x = x;
            boundingBox.max.x = x + width;
        } else {

            boundingBox.min.x = x;
            boundingBox.max.x = x + width;
        }

        boundingBox.min.y = y;
        boundingBox.max.y = y + height;
    }
    public boolean isInRange(){
        return Math.abs(x - startX) < range || x > 0;
    }
}
