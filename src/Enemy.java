import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Enemy extends GameObject{
    public Enemy(Level l){
        this.pos = new Vec2(50, 50);
        this.posLastFrame = new Vec2(50, 50);
        this.gravity = new Vec2(0, 0.35f);
        this.maxSpeed = new Vec2(5, 10);
        this.movementSpeed = 3.5f;

        this.level = l;
        tilesWalk = new ArrayList<BufferedImage>();
        try {

            // Tiles for movement animation
            BufferedImage imageWalk;
            BufferedImage imageHalf;
            imageWalk = ImageIO.read(new File("assets/enemy/export 10.png"));
            tilesWalk.add(imageWalk);
            imageHalf = ImageIO.read(new File("assets/enemy/export 11.png"));
            tilesWalk.add(imageHalf);

        } catch (IOException e) {
            e.printStackTrace();
        }

        boundingBox = new BoundingBox(pos.x, pos.y, tilesWalk.get(0).getWidth(), tilesWalk.get(0).getHeight());
        numberAnimationStates = tilesWalk.size();
    }

    @Override
    public void update() {
        Random random = new Random();
        double r = random.nextDouble();
        double e1 = 0.004;
        double e2 = 0.008;
        double e3 = 0.009;
        if (r<e1){
            this.walkingRight = true;
        }
        else if (r>e1 && r<e2){
            this.walkingLeft = true;
        }
        else if (r>e2 && r<e3){
            this.jump=true;
        }
        super.update();
        walkingLeft = false;
        walkingRight = false;
        jump = false;
    }
}
