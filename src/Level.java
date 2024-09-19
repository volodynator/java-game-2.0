import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;

public class Level {
	BufferedImage levelImg, resultingLevelImg, backgroundImage, cloudImage1;
	public Player player;
	Vec2 lvlSize;
	float offsetX;
	ArrayList<Tile> tiles;
	List<Tile> tilesToCheck;
	List<Bullet> bullets = new CopyOnWriteArrayList<>();

	public Level(String levelMapPath, String levelBackgroundMapPath) {
		try {
//			backgroundImage = ImageIO.read(new File(levelBackgroundMapPath));
			tiles = new ArrayList<Tile>();
			lvlSize = new Vec2(0, 0);
			offsetX = 0.0f;

			try {
				// Level image
				levelImg = ImageIO.read(new File(levelMapPath));

				// Tile images
				Tile.images.add(ImageIO.read(new File("assets/tiles/lavaImage.png")));
				Tile.images.add(ImageIO.read(new File("assets/tiles/ground.png")));
				Tile.images.add(ImageIO.read(new File("assets/tiles/margin.png")));
			} catch (IOException e) {

			}
			initLevel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {

		//update camera offset
		float diff = (player.boundingBox.max.x + player.boundingBox.min.x) * 0.5f - 500 - offsetX;

		int noMoveZone = 100;

		if (Math.abs(diff) > noMoveZone) {
			if (diff < 0)
				diff += noMoveZone;
			else
				diff -= noMoveZone;
			offsetX += diff;
		}


		if (offsetX < 0)
			offsetX = 0;

		if (offsetX > resultingLevelImg.getWidth() - 1000)
			offsetX = resultingLevelImg.getWidth() - 1000;

		tilesToCheck = tiles.stream().filter(tile -> (tile.hasRigidCollision) && (tile.bb.max.x < player.pos.x + 1000) && (tile.bb.min.x > player.pos.x - 1000)).toList();
		bullets.stream().filter(Bullet::isInRange).forEach(Bullet::update);
	}

	public void initLevel() {
		lvlSize.x = Tile.tileSize * levelImg.getWidth(null);
		lvlSize.y = Tile.tileSize * levelImg.getHeight(null);

		resultingLevelImg = new BufferedImage((int) lvlSize.x, (int) lvlSize.y, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = null;
		g2d = (Graphics2D) resultingLevelImg.getGraphics();

//		for (int x = 0; x < resultingLevelImg.getWidth(null); x += backgroundImage.getWidth()) {
//
//			g2d.drawImage((BufferedImage) backgroundImage, null, x, 0);
//
//			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
//			tx.translate(-backgroundImage.getWidth(null), 0);
//			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//			backgroundImage = op.filter(backgroundImage, null);
//		}

//		for (int x = 0; x < resultingLevelImg.getWidth(null); x += cloudImage1.getWidth() * 2) {
//			Random r = new Random();
//			g2d.drawImage((BufferedImage) cloudImage1, null, x + r.nextInt(250), r.nextInt(250) + 50);
//		}

		tiles.clear();

		for (int y = 0; y < levelImg.getHeight(null); y++) {
			for (int x = 0; x < levelImg.getWidth(null); x++) {
				Color color = new Color(levelImg.getRGB(x, y));

				 Tile t = null;

				// Compare color of pixels in order to select the corresponding tiles

				if (color.equals(Color.BLUE))
					t = new TileWater(0,x*Tile.tileSize,y*Tile.tileSize);
				if (color.equals(Color.GREEN))
					// decorative -> no rigid collision
					t = new Tile(1,x*Tile.tileSize,y*Tile.tileSize, false);
				if (color.equals(Color.BLACK)){
					t = new Tile(2, x*Tile.tileSize,y*Tile.tileSize);
				}

				if(t!=null) {
					tiles.add(t);
					// Get graphics context
					g2d = (Graphics2D) resultingLevelImg.getGraphics();

					// Draw tile into data of image
					t.drawStatic(g2d,0,0);
				}
			}
		}
		g2d.dispose();
	}

	public Image getResultingImage() {
		return resultingLevelImg;
	}

	public int getSizeX() {
		return resultingLevelImg.getWidth();
	}

	public int getSizeY() {
		return resultingLevelImg.getHeight();
	}
}
