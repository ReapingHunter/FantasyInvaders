package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import entity.Player;
import entity.Player.PlayerAttack;
import tile.TileManager;
import entity.Enemy;
import entity.Enemy.ArmoredSkeleton;
import entity.Enemy.DemolisherSkeleton;
import entity.Enemy.Skeleton;
import entity.Enemy.TrackerSkeleton;
import entity.EnemyAttack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable{
	// FPS
	private int FPS = 60;
	
	private TileManager tileM = new TileManager(this);
	private KeyHandler keyHandler = new KeyHandler();
	private Thread gameThread;
	private Player player;
	private PlayerAttack attack;
	private List<Enemy> enemies;
	private int enemyFireInterval;
	private final float ORIGINAL_INTERVAL = 5000;
	private int enemyKillCount;
	private boolean hasInvaded;

	public boolean hasInvaded() {
		return hasInvaded;
	}

	public TileManager getTileM() {
		return tileM;
	}

	public void setTileM(TileManager tileM) {
		this.tileM = tileM;
	}

	public KeyHandler getKeyHandler() {
		return keyHandler;
	}

	public void setKeyHandler(KeyHandler keyHandler) {
		this.keyHandler = keyHandler;
	}

	public Thread getGameThread() {
		return gameThread;
	}

	public void setGameThread(Thread gameThread) {
		this.gameThread = gameThread;
	}

	public void setInvaded(boolean invaded) {
		this.hasInvaded = invaded;
	}

	public GamePanel(FantasyInvaders main) {

		setLayout(null);
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyHandler);
		this.setFocusable(true);
		
		this.requestFocusInWindow();
		// Set the initial camera position to the center of the game world
		initGame();

	}

	public void initGame() {
		enemies = new ArrayList<>();
		enemyKillCount = 0;
		enemyFireInterval = (int)ORIGINAL_INTERVAL;
		hasInvaded = false;
		for (int y = 0; y < 6; y++) {
			for (int x = 5; x < 19; x++) {
				Enemy enemy;
				switch(y) {
					case 0:
						enemy = new TrackerSkeleton(this, tileM.getTileSize() * x, tileM.getTileSize() * y);
						break;
					case 1:
						enemy = new DemolisherSkeleton(this, tileM.getTileSize() * x, tileM.getTileSize() * y);
						break;
					case 4:
						enemy = new ArmoredSkeleton(this, tileM.getTileSize() * x, tileM.getTileSize() * y);
						break;
					case 5:
						enemy = new ArmoredSkeleton(this, tileM.getTileSize() * x, tileM.getTileSize() * y);
						break;
					default:
						enemy = new Skeleton(this, tileM.getTileSize() * x, tileM.getTileSize() * y);
						break;
				}
				enemies.add(enemy);
			}
		}

		player = new Player(this, keyHandler);
		attack = new PlayerAttack();
		player.setScreenX((int) player.getX());
		player.setScreenY((int) player.getY());
	}

	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

// TIME SHIFT TO SET FPS
	public void run() {
		double drawInterval = 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;
		int drawCount = 0;

		while (gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;

			if (delta >= 1) {
				// Call update here instead of in the constructor
				update();
				repaint();
				delta--;
				drawCount++;
			}

			if (timer >= 1000000000) {
				System.out.println("FPS: " + drawCount);
				drawCount = 0;
				timer = 0;
			}
		}
	}

	// UPDATES GAME EVENTS
	public void update() {
		// Game end
		if (player.getLives() <= 0 || hasInvaded == true || enemyKillCount == enemies.size()) {
			if (player.getLives() <= 0 || hasInvaded == true) {
				player.die();
				for (Enemy enemy : enemies) {
					enemy.moveLose(player, tileM.getScreenWidth(), tileM.getTileSize());
				}
			} else {
				gameThread = null;
			}
			attack.die();
			for (Enemy enemy : enemies) {
				if (!enemy.attack().isDestroyed()) {
					enemy.attack().setDestroyed(true);
				}
			}
		} else {
			// Player
			player.act();

			// Attack
			if (player.getKeyH().spacePressed == true && !attack.isVisible()) {
				attack = new PlayerAttack(player.getX(), player.getY());
			}
			if (attack.isVisible()) {
				int atkX = (int) attack.getX();
				int atkY = (int) attack.getY();
				for (Enemy enemy : enemies) {
					int enemyX = (int) enemy.getX();
					int enemyY = (int) enemy.getY();

					if (enemy.isVisible() && attack.isVisible()) {
						if (atkX >= enemyX && atkX <= (enemyX + tileM.getTileSize() / 2) && atkY >= enemyY
								&& atkY <= (enemyY + tileM.getTileSize())) {
							attack.die();
							enemy.setHealth(enemy.getHealth() - 1);
							if(enemy.getHealth() == 1) {
								Sprite.setSpriteSheet(Sprite.loadSprite("enemy/Skeleton"));
								BufferedImage[] stand = {Sprite.getSprite(0, 0)};
								BufferedImage[] walkLeft = {Sprite.getSprite(32, 0), Sprite.getSprite(16, 0)};
								BufferedImage[] walkRight = {Sprite.getSprite(16, 0), Sprite.getSprite(32, 0)};
								enemy.setStanding(new Animation(stand, enemy.getStanding().getFrameDelay()));
								enemy.setWalkingLeft(new Animation(walkLeft, enemy.getWalkingLeft().getFrameDelay()));
								enemy.setWalkingRight(new Animation(walkRight, enemy.getWalkingRight().getFrameDelay()));
							}
							if (enemy.getHealth() <= 0) {
								enemy.die();
								enemyKillCount++;
							}
						}
					}
				}
				int y = (int) attack.getY();
				y -= 10;

				if (y < 0) {
					attack.die();
				} else {
					attack.setY(y);
				}
			}
			// Enemy
			for (Enemy enemy : enemies) {
				if (enemy.getX() >= tileM.getScreenWidth() - tileM.getTileSize() && enemy.getDirection() == "right") {
					Iterator<Enemy> i1 = enemies.iterator();
					while (i1.hasNext()) {
						Enemy e1 = i1.next();
						e1.setY(e1.getY() + tileM.getTileSize() / 2);
						e1.setDirection("left");
					}

				}
				if (enemy.getX() <= 0 && enemy.getDirection() == "left") {
					Iterator<Enemy> i2 = enemies.iterator();
					while (i2.hasNext()) {
						Enemy e2 = i2.next();
						e2.setY(e2.getY() + tileM.getTileSize() / 2);
						e2.setDirection("right");
					}

				}
			}

			Iterator<Enemy> it = enemies.iterator();

			while (it.hasNext()) {
				Enemy enemy = it.next();
				if (enemy.isVisible()) {
					int y = (int) enemy.getY();
					if (y >= player.getY() - tileM.getTileSize() / 2) {
						hasInvaded = true;
					}
					enemy.move();
				}
			}

			// Enemy Attack
			var generator = new Random();
			for (Enemy enemy : enemies) {
				int shoot = generator.nextInt(enemyFireInterval);
				EnemyAttack attack = enemy.attack();
				if (shoot == enemyFireInterval - 3 && enemy.isVisible() && attack.isDestroyed()) {
					attack.setDestroyed(false);
					attack.setX(enemy.getX());
					attack.setY(enemy.getY());
				}

				int atkX = (int) attack.getX();
				int atkY = (int) attack.getY();
				int playerX = (int) player.getX();
				int playerY = (int) player.getY();
				if (player.isVisible() && !attack.isDestroyed()) {
					if(enemy.getClass() == Skeleton.class || enemy.getClass() == ArmoredSkeleton.class) {
						Sprite.setSpriteSheet(Sprite.loadSprite("enemy/ArrowSpriteSheet"));
						attack.setImage(Sprite.getSprite(0, 0));
						attack.setY(attack.getY() + 5);
						if (atkY >= playerY + tileM.getTileSize()) {
							attack.setDestroyed(true);
						}
						if (atkX >= (playerX - tileM.getTileSize() / 4) && atkX <= (playerX + tileM.getTileSize() / 2) && atkY >= playerY
								&& atkY <= (playerY + tileM.getTileSize())) {
							player.setLives(player.getLives() - 1);
							player.setDirection("hit");
							attack.setDestroyed(true);
						}
					} else if(enemy.getClass() == DemolisherSkeleton.class) {
						Sprite.setSpriteSheet(Sprite.loadSprite("enemy/ArrowSpriteSheet"));
						attack.setImage(Sprite.getSprite(0, 32));
						attack.setY(attack.getY() + 2);
						if (atkY >= playerY + tileM.getTileSize()) {
							
							// Indirect hits near the arrows still cause damage
							if(atkX >= playerX - tileM.getTileSize() && atkX <= playerX + tileM.getTileSize()) {
								player.setLives(player.getLives() - 1);
							}
							Sprite.setSpriteSheet(Sprite.loadSprite("enemy/Explosion"));
							BufferedImage[] explosionSprite = {Sprite.getSprite(0, 0), Sprite.getSprite(16, 0),
														 Sprite.getSprite(32, 0), Sprite.getSprite(48, 0),
														 Sprite.getSprite(64, 0), Sprite.getSprite(80, 0),
														 Sprite.getSprite(96, 0), Sprite.getSprite(112, 0),
														 Sprite.getSprite(128, 0)};
							attack.setAnimation(new Animation(explosionSprite, 1200));
							while(attack.getAnimation().getCurrentFrame() < attack.getAnimation().getTotalFrames() - 1) {
								attack.setImage(attack.getAnimation().getSprite());
								attack.getAnimation().start();
								attack.getAnimation().update();
							}
							attack.setDestroyed(true);
						}
						if ((atkX >= (playerX - tileM.getTileSize() / 4) && atkX <= (playerX + tileM.getTileSize() / 2) && atkY >= playerY
								&& atkY <= (playerY + tileM.getTileSize() * 2))) {
							player.setLives(player.getLives() - 1);
							player.setDirection("hit");
							attack.setDestroyed(true);
						}
					} else if(enemy.getClass() == TrackerSkeleton.class) {
						Sprite.setSpriteSheet(Sprite.loadSprite("enemy/ArrowSpriteSheet"));
						if(attack.getX() <= player.getX() + tileM.getTileSize() / 2 && attack.getX() >= player.getX() - tileM.getTileSize() / 8){
							attack.setImage(Sprite.getSprite(0, 64));
							attack.setX(attack.getX());
						} else if(attack.getX() < player.getX()) {
							attack.setImage(Sprite.getSprite(32, 64));
							attack.setX(attack.getX() + 3);
						} else if(attack.getX() > player.getX()) {
							attack.setImage(Sprite.getSprite(16, 64));
							attack.setX(attack.getX() - 3);
						} 
						
						attack.setY(attack.getY() + 4);
						if (atkY >= playerY + tileM.getTileSize()) {
							attack.setDestroyed(true);
						}
						if (atkX >= (playerX - tileM.getTileSize() / 4) && atkX <= (playerX + tileM.getTileSize() / 2) && atkY >= playerY
								&& atkY <= (playerY + tileM.getTileSize())) {
							player.setLives(player.getLives() - 1);
							player.setDirection("hit");
							attack.setDestroyed(true);
						}
					}
					
				}
			}

			// Dynamic Difficulty
			for (Enemy enemy : enemies) {
				if (enemyKillCount >= Math.floor(enemies.size() * 0.99)) {
					enemy.setSpeed(9.0f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.04f);
					enemy.getStanding().setFrameDelay(1);
					enemy.getWalkingLeft().setFrameDelay(1);
					enemy.getWalkingRight().setFrameDelay(1);
				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.96)) {
					enemy.setSpeed(4.5f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.08f);
					enemy.getStanding().setFrameDelay(3);
					enemy.getWalkingLeft().setFrameDelay(3);
					enemy.getWalkingRight().setFrameDelay(3);
				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.88)) {
					enemy.setSpeed(4.0f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.16f);
					enemy.getStanding().setFrameDelay(4);
					enemy.getWalkingLeft().setFrameDelay(4);
					enemy.getWalkingRight().setFrameDelay(4);
				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.80)) {
					enemy.setSpeed(3.5f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.24f);
					enemy.getStanding().setFrameDelay(5);
					enemy.getWalkingLeft().setFrameDelay(5);
					enemy.getWalkingRight().setFrameDelay(5);
				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.72)) {
					enemy.setSpeed(3.1f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.32f);
					enemy.getStanding().setFrameDelay(6);
					enemy.getWalkingLeft().setFrameDelay(6);
					enemy.getWalkingRight().setFrameDelay(6);
				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.64)) {
					enemy.setSpeed(2.7f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.38f);
					enemy.getStanding().setFrameDelay(7);
					enemy.getWalkingLeft().setFrameDelay(7);
					enemy.getWalkingRight().setFrameDelay(7);
				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.56)) {
					enemy.setSpeed(2.3f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.44f);
					enemy.getStanding().setFrameDelay(8);
					enemy.getWalkingLeft().setFrameDelay(8);
					enemy.getWalkingRight().setFrameDelay(8);
				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.48)) {
					enemy.setSpeed(2.0f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.52f);
					enemy.getStanding().setFrameDelay(9);
					enemy.getWalkingLeft().setFrameDelay(9);
					enemy.getWalkingRight().setFrameDelay(9);
				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.40)) {
					enemy.setSpeed(1.7f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.60f);

				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.32)) {
					enemy.setSpeed(1.4f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.68f);
					enemy.getStanding().setFrameDelay(10);
					enemy.getWalkingLeft().setFrameDelay(10);
					enemy.getWalkingRight().setFrameDelay(10);
				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.24)) {
					enemy.setSpeed(1.2f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.76f);

				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.16)) {
					enemy.setSpeed(1.0f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.84f);
					enemy.getStanding().setFrameDelay(11);
					enemy.getWalkingLeft().setFrameDelay(11);
					enemy.getWalkingRight().setFrameDelay(11);
				} else if (enemyKillCount >= Math.floor(enemies.size() * 0.08)) {
					enemy.setSpeed(0.8f);
					enemyFireInterval = (int) (ORIGINAL_INTERVAL * 0.92f);
				}
			}
		}
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		doDrawing(g2);
		String text = "You Win";
		String warning = "Warning: Invasion Imminent";
		String guide = "Press SPACE to shoot";
		String guide2 = "Press LEFT or RIGHT arrow keys to move";
		Font font = new Font("Arial", Font.BOLD, tileM.getTileSize());
		g2.setFont(font);
		if (player.getLives() <= 0) {
			text = "You lost by dying";
		} else if (hasInvaded == true) {
			text = "You lost by invasion";
		}
		FontMetrics metrics = g2.getFontMetrics(font);
        int stringWidth = metrics.stringWidth(text);
        int stringHeight = metrics.getHeight();

        // Calculate the position to center the text horizontally and vertically
        int x = (getWidth() - stringWidth) / 2;
        int y = (getHeight() - stringHeight) / 2 + metrics.getAscent();
		if (player.getLives() <= 0 || enemies.size() == enemyKillCount || hasInvaded == true) {
			g2.setColor(new Color(0, 0, 0, 150));
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.setColor(Color.white);
			g2.setFont(new Font("Helvetica", Font.BOLD, tileM.getTileSize()));
	        g2.drawString(text, x, y / 2);
		} else {
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, tileM.getTileSize() / 3));
			g2.setColor(Color.black);
			g2.drawString(guide, 0, tileM.getScreenHeight() - tileM.getTileSize() / 3);
			g2.drawString(guide2, 0, tileM.getScreenHeight());
			// Warns if invaders are getting closer
			for (Enemy enemy : enemies) {
				if (enemy.isVisible() && enemy.getY() >= player.getY() - (tileM.getTileSize() * 2)) {
					g2.setFont(g2.getFont().deriveFont(Font.BOLD, tileM.getTileSize() / 3));
					g2.setColor(Color.red);
					g2.drawString(warning, x, tileM.getTileSize() / 2);
				}
			}
		}
		g2.dispose();
	}
	
	public void doDrawing(Graphics2D g2) {
		tileM.draw(g2);
		drawPlayer(g2);
		drawEnemy(g2);
		drawAttack(g2);
		drawEnemyAttack(g2);
		drawLives(g2);
	}

	public void drawPlayer(Graphics2D g2) {
		if (player.isVisible()) {
			g2.drawImage(player.getImage(), player.getScreenX(), player.getScreenY(), tileM.getTileSize(), tileM.getTileSize() * 2, null);
		}
	}

	public void drawEnemy(Graphics2D g2) {
		for (Enemy enemy : enemies) {
			if (enemy.isVisible()) {
				g2.drawImage(enemy.getImage(), (int) enemy.getX(), (int) enemy.getY(), tileM.getTileSize(), tileM.getTileSize() * 2, this);
			}
		}
	}

	public void drawLives(Graphics2D g2) {
		try {
			Image img = ImageIO.read(getClass().getResourceAsStream("/player/heart.png"));
			for (int lives = 0; lives < player.getLives(); lives++) {
				g2.drawImage(img, lives * 50, lives, tileM.getTileSize(), tileM.getTileSize(), this);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void drawAttack(Graphics2D g2) {
		if (attack.isVisible()) {
			g2.drawImage(attack.getImage(), (int) attack.getX(), (int) attack.getY(), tileM.getTileSize() / 2, tileM.getTileSize(), this);
		}

	}

	public void drawEnemyAttack(Graphics2D g2) {
		for (Enemy enemy : enemies) {
			EnemyAttack e = enemy.attack();
			if (!e.isDestroyed()) {
				g2.drawImage(e.getImage(), (int) e.getX(), (int) e.getY(), tileM.getTileSize() / 2, tileM.getTileSize(), this);
			}
		}
	}
	
	public static class Sprite {
		private static BufferedImage spriteSheet;
		private final static int TILE_SIZE = 16;
		
		public static BufferedImage getSpriteSheet() {
			return spriteSheet;
		}

		public static void setSpriteSheet(BufferedImage spriteSheet) {
			Sprite.spriteSheet = spriteSheet;
		}

		public static BufferedImage loadSprite(String file) {

	        BufferedImage sprite = null;

	        try {
	            sprite = ImageIO.read(new File("res/" + file + ".png"));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return sprite;
	    }
		
		public static BufferedImage getSprite(int xGrid, int yGrid) {
	        if (spriteSheet == null) {
	            spriteSheet = loadSprite("");
	        }

	        return spriteSheet.getSubimage(xGrid, yGrid, TILE_SIZE, TILE_SIZE * 2);
	    }
		
	}
	
	public class Animation {
		private int frameCount;                 // Counts ticks for change
	    private int frameDelay;                 // frame delay 1-12 (You will have to play around with this)
	    private int currentFrame;               // animations current frame
	    private int animationDirection;         // animation direction (i.e counting forward or backward)
	    private int totalFrames;                // total amount of frames for your animation

	    private boolean stopped;                // has animations stopped

	    private List<Frame> frames = new ArrayList<Frame>();    // Arraylist of frames 

	    public Animation(BufferedImage[] frames, int frameDelay) {
	        this.frameDelay = frameDelay;
	        this.stopped = true;

	        for (int i = 0; i < frames.length; i++) {
	            addFrame(frames[i], frameDelay);
	        }

	        this.frameCount = 0;
	        this.frameDelay = frameDelay;
	        this.currentFrame = 0;
	        this.animationDirection = 1;
	        this.totalFrames = this.frames.size();

	    }
	    
	    public List<Frame> getFrames() {
			return frames;
		}

		public void setFrames(List<Frame> frames) {
			this.frames = frames;
		}

		public int getFrameDelay() {
			return frameDelay;
		}
		
		public void setFrameDelay(int frameDelay) {
			this.frameDelay = frameDelay;
		}
		
		public int getFrameCount() {
			return frameCount;
		}

		public void setFrameCount(int frameCount) {
			this.frameCount = frameCount;
		}

		public int getCurrentFrame() {
			return currentFrame;
		}

		public void setCurrentFrame(int currentFrame) {
			this.currentFrame = currentFrame;
		}

		public int getTotalFrames() {
			return totalFrames;
		}

		public void setTotalFrames(int totalFrames) {
			this.totalFrames = totalFrames;
		}

		public void start() {
	    	if(stopped && frames.size() != 0) {
	    		stopped = false;
	    	}
	    }
	    
	    public void stop() {
	    	if(frames.size() != 0) {
	    		stopped = true;
	    	}
	    }
	    
	    public void restart() {
	        if (frames.size() != 0) {
	        	stopped = false;
		        currentFrame = 0;
	        }
	    }

	    public void reset() {
	        this.stopped = true;
	        this.frameCount = 0;
	        this.currentFrame = 0;
	    }
	    
	    private void addFrame(BufferedImage frame, int duration) {
	        if (duration <= 0) {
	            System.err.println("Invalid duration: " + duration);
	            throw new RuntimeException("Invalid duration: " + duration);
	        }

	        frames.add(new Frame(frame, duration));
	        currentFrame = 0;
	    }

	    public BufferedImage getSprite() {
	        return frames.get(currentFrame).getFrame();
	    }

	    public void update() {
	        if (!stopped) {
	            frameCount++;

	            if (frameCount > frameDelay) {
	                frameCount = 0;
	                currentFrame += animationDirection;

	                if (currentFrame > totalFrames - 1) {
	                    currentFrame = 0;
	                }
	                else if (currentFrame < 0) {
	                    currentFrame = totalFrames - 1;
	                }
	            }
	        }

	    }
	    
	    public class Frame {

	        private BufferedImage frame;
	        private int duration;

	        public Frame(BufferedImage frame, int duration) {
	            this.frame = frame;
	            this.duration = duration;
	        }

	        public BufferedImage getFrame() {
	            return frame;
	        }

	        public void setFrame(BufferedImage frame) {
	            this.frame = frame;
	        }

	        public int getDuration() {
	            return duration;
	        }

	        public void setDuration(int duration) {
	            this.duration = duration;
	        }

	    }
	}
}
