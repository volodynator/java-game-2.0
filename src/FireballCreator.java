import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FireballCreator implements Skill{
    Level level;
    GameObject object;
    BufferedImage image = ImageIO.read(new File("assets/bullets/fireball.png"));
    int damage;
    int mana;
    float range;
    float speed;


    public FireballCreator(Level level, GameObject object, int damage, int mana, float range, float speed) throws IOException {
        this.level = level;
        this.object = object;
        this.damage = damage;
        this.mana = mana;
        this.range = range;
        this.speed = speed;
    }

    @Override
    public void use() throws IOException {
        Bullet bullet = new Bullet(object, damage, mana, range, speed, image);
        if (object.facingLeft){
            bullet.movingLeft=true;
        }
        level.bullets.add(bullet);
    }
}
