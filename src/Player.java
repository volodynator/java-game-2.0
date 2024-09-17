import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Player extends GameObject{

	Player(Level l) throws IOException {
		this.pos = new Vec2(0, 0);
		this.posLastFrame = new Vec2(0, 0);
		this.gravity = new Vec2(0, 0.35f);
		this.maxSpeed = new Vec2(5, 10);
		this.movementSpeed = 3.5f;

		this.level = l;
		tilesWalk = new ArrayList<BufferedImage>();
		try {

			// Tiles for movement animation
			BufferedImage imageWalk;
			BufferedImage imageEmpty;
			BufferedImage imageHalf;
			imageWalk = ImageIO.read(new File("assets/walk/1.0.png"));
			tilesWalk.add(imageWalk);
			imageHalf = ImageIO.read(new File("assets/walk/2.0.png"));
			tilesWalk.add(imageHalf);
			imageEmpty = ImageIO.read(new File("assets/walk/3.0.png"));
			tilesWalk.add(imageEmpty);

		} catch (IOException e) {
			e.printStackTrace();
		}

		boundingBox = new BoundingBox(pos.x, pos.y, tilesWalk.get(0).getWidth(), tilesWalk.get(0).getHeight());
		numberAnimationStates = tilesWalk.size();

	}
	public void playSound(String path){
		File lol = new File(path);

		try{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(lol));
			clip.start();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
