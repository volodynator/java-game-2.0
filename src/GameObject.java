import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GameObject {
    Level level;

    // Movement:
    boolean jump = false, walkingLeft = false, walkingRight = false;
    boolean facingLeft = false;
    Vec2 pos;
    Vec2 posLastFrame;
    Vec2 gravity;
    Vec2 maxSpeed;
    public Vec2 lastValidPosition;
    float movementSpeed;
    float jumpPower = 35.f;

    // Collisions:
    boolean collidesTop = false, collidesDown = false, collidesLeft = false, collidesRight = false, collides = false;
    BoundingBox boundingBox;

    // Animation:
    protected ArrayList<BufferedImage> tilesWalk;
    int numberAnimationStates = 0;
    int displayedAnimationState = 0;
    int moveCounter = 0;

    // Features:
    int hp = 100;

    private void move(int deltaX) {
        if (deltaX < 0) {
            pos.x = pos.x - movementSpeed / 4;
        } else if (deltaX > 0) {
            pos.x = pos.x + movementSpeed / 4;
        }
    }

    public void update() {

        // Check if walking and call move()
        if (walkingLeft){
            move(-1);
            facingLeft = true;
        }
        if (walkingRight){
            move(1);
            facingLeft = false;
        }

        if(jump && collidesDown){
            pos.y -= jumpPower;
        }

        // Save old position
        Vec2 pos_lastFrame_temp = pos;

        // Add gravity and move according to the actual speed
        pos = pos.add(pos.sub(posLastFrame));

        // Get saved old Position back
        posLastFrame = pos_lastFrame_temp;

        //apply gravity
        pos = pos.add(gravity);

        // Calculate difference in X
        float diffX = pos.x - posLastFrame.x;

        // Factor to damp the energy, otherwise the player would glitch threw the world
        float damping = 0.02f;
        if(collides){
            damping = 0.2f;
        }

        // Generate a damped version of the difference
        pos.x = posLastFrame.x + diffX * (1.0f-damping);

        // Check weather speed is under maxSpeed
        if (pos.x - posLastFrame.x > maxSpeed.x)
            pos.x = posLastFrame.x + maxSpeed.x;

        if (pos.x - posLastFrame.x < -maxSpeed.x)
            pos.x = posLastFrame.x - maxSpeed.x;

        if (pos.y - posLastFrame.y > maxSpeed.y)
            pos.y = posLastFrame.y + maxSpeed.y;

        if (pos.y - posLastFrame.y < -maxSpeed.y)
            pos.y = posLastFrame.y - maxSpeed.y;

        // Check window boundaries
        if (pos.x < 0)
            pos.x = 0;

        if (pos.x > level.lvlSize.x-Tile.tileSize)
            pos.x = level.lvlSize.x-Tile.tileSize;

        updateBoundingBox();
    }

    public void updateBoundingBox(){
        // update BoundingBox
        boundingBox.min.x = pos.x;
        boundingBox.min.y = pos.y;

        boundingBox.max.x = pos.x + tilesWalk.get(0).getWidth();
        boundingBox.max.y = pos.y + tilesWalk.get(0).getHeight();
    }

    public BufferedImage getImage() {
        BufferedImage b = getNextTile();
        if (facingLeft) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-b.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            b = op.filter(b, null);
        }
        return b;
    }

    private BufferedImage getNextTile() {
        if ((walkingLeft || walkingRight)) {
            moveCounter++;
            if(moveCounter>=2) {
                displayedAnimationState++;
                moveCounter = 0;
            }
            if (displayedAnimationState > numberAnimationStates - 1) {
                displayedAnimationState = 0;
            }
            return tilesWalk.get(displayedAnimationState);
        }
        return tilesWalk.get(tilesWalk.size()-1);
    }

    public void drawObject(Graphics2D g2d){
        g2d.drawImage(this.getImage(), (int) (this.pos.x-level.offsetX), (int) this.pos.y, null);
    }
    public void checkCollision() {

        this.collidesDown = false;
        this.collidesLeft = false;
        this.collidesRight = false;
        this.collidesTop = false;
        this.collides = false;

        // Collision
        for (int i = 0; i < level.tilesToCheck.size(); i++) {

            Tile tile = level.tilesToCheck.get(i);

            Vec2 overlapSize = tile.bb.OverlapSize(this.boundingBox);

            float epsilon = 8.f; // experiment with this value. If too low,the player might get stuck when walking over the
            // ground. If too high, it can cause glitching inside/through walls


            if (overlapSize.x >= 0 && overlapSize.y >= 0 && Math.abs(overlapSize.x + overlapSize.y) >= epsilon) {

                if(tile.hasRigidCollision) {
                    if (Math.abs(overlapSize.x) > Math.abs(overlapSize.y)) {// Y overlap correction

                        if (this.boundingBox.min.y + this.boundingBox.max.y > tile.bb.min.y + tile.bb.max.y) { // player comes from below
                            this.pos.y += overlapSize.y;
                            this.collidesTop = true;
                        } else { // player comes from above
                            this.pos.y -= overlapSize.y;
                            this.collidesDown = true;
                        }
                    } else { // X overlap correction
                        if (this.boundingBox.min.x + this.boundingBox.max.x > tile.bb.min.x + tile.bb.max.x) { // player comes from right
                            this.pos.x += overlapSize.x;
                            this.collidesLeft = true;
                        } else { // player comes from left
                            this.pos.x -= overlapSize.x;
                            this.collidesRight = true;
                        }
                    }
                }
                this.collides = true;
                tile.onCollision(this);
                this.updateBoundingBox();
            }
        }
    }
}
