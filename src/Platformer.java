import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Platformer extends JFrame {
	@Serial
	private static final long serialVersionUID = 5736902251450559962L;
	private Level l = null;
	private boolean isFullScreen = false;
	BufferStrategy bufferStrategy;

	Timer gameStateUpdateTrigger;
	List<GameObject> objects = new ArrayList<>();

	public Platformer() {
		//exit program when window is closed
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
					System.exit(0);
			}
		});

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("./"));
		fc.setDialogTitle("Select input image");
		FileFilter filter = new FileNameExtensionFilter("Level image (.bmp)","bmp");
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(this);
		File selectedFile = new File("");
		addKeyListener(new AL(this));
		createBufferStrategy(2);
		bufferStrategy = this.getBufferStrategy();


		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		} else {
			dispose();
			System.exit(0);
		}

		try {
			l = new Level(selectedFile.getAbsolutePath(), "background0.png");
			l.player = new Player(l);
			objects.add(l.player);
			objects.add(new Enemy(l));

			this.setBounds(0, 0, 1000, 12 * 70);
			this.setVisible(true);
			gameStateUpdateTrigger = new Timer();
			gameStateUpdateTrigger.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					updateGameStateAndRepaint();
				}

			}, 0, 10);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void restart() throws IOException {
		l.player.pos.x = 0;
		l.player.pos.y = 0;
		l.offsetX = 0;
		l.initLevel();
	}

	private void updateGameStateAndRepaint() {
		l.update();
		for (GameObject object: objects) {
			object.update();
			object.checkCollision();
		}
		repaint();
	}


	private void gameOver() throws IOException {
		restart();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = null;

		try {
			g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
			draw(g2);

		} finally {
			g2.dispose();
		}
		bufferStrategy.show();
	}

	private void draw(Graphics2D g2d) {
		BufferedImage level = (BufferedImage) l.getResultingImage();
		if (l.offsetX > level.getWidth() - 1000)
			l.offsetX = level.getWidth() - 1000;
		BufferedImage bi = level.getSubimage((int) l.offsetX, 0, 1000, level.getHeight());
		g2d.drawImage(l.backgroundImage, 0, 0, this);
		g2d.drawImage(bi, 0, 0, this);

		for (int i = 0; i< l.tiles.size(); i++) {
			l.tiles.get(i).draw(g2d,l.offsetX,0);
		}
		for (GameObject object: objects) {
			object.drawObject(g2d);
		}
		for (Bullet bullet: l.bullets) {
			g2d.drawImage(bullet.image, (int) (bullet.x - l.offsetX), (int) bullet.y, null);
		}
		g2d.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 28));
		g2d.setColor(Color.WHITE);
		g2d.drawString(l.player.skill, 100, 100);

	}

	public Level getLevel() {
		return this.l;
	}

	public void setFullScreenMode(boolean b) {
		this.isFullScreen = b;
	}

	public boolean getFullScreenMode() {
		return this.isFullScreen;
	}

	public class AL extends KeyAdapter {
		Platformer p;

		public AL(Platformer p) {
			super();
			this.p = p;
		}

		@Override
		public void keyPressed(KeyEvent event) {
			int keyCode = event.getKeyCode();
			Player player = l.player;

			if (keyCode == KeyEvent.VK_ESCAPE) {
				dispose();
			}

			if (keyCode == KeyEvent.VK_UP) {
			}

			if (keyCode == KeyEvent.VK_DOWN) {
			}

			if (keyCode == KeyEvent.VK_A) {
				player.walkingLeft = true;
			}

			if (keyCode == KeyEvent.VK_D) {
				player.walkingRight = true;
			}

			if (keyCode == KeyEvent.VK_SPACE) {
				player.jump = true;
			}

//			if (keyCode == KeyEvent.VK_R) {
//				try {
//					restart();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			if (keyCode >=73 && keyCode<=76){
				try {
					player.listenToKey(keyCode);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			if (keyCode==KeyEvent.VK_BACK_SPACE){
				player.resetKeyListener();
			}
		}

		@Override
		public void keyReleased(KeyEvent event) {
			int keyCode = event.getKeyCode();
			Player player = l.player;

			if (keyCode == KeyEvent.VK_UP) {
			}

			if (keyCode == KeyEvent.VK_DOWN) {
			}

			if (keyCode == KeyEvent.VK_A) {
				player.walkingLeft = false;
			}

			if (keyCode == KeyEvent.VK_D) {
				player.walkingRight = false;
			}

			if (keyCode == KeyEvent.VK_SPACE) {
				player.jump = false;
			}
		}
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