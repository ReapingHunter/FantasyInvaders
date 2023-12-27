package entity;

import java.awt.image.BufferedImage;
import main.GamePanel;
import main.GamePanel.Sprite;
import main.KeyHandler;
import tile.TileManager;

public class Player extends Entity {
	private TileManager tileM;
	private GamePanel gp;
	private KeyHandler keyH;
	private int lives;
	private int screenX;
	private int screenY;

	public Player(GamePanel gp, KeyHandler keyH) {
		this.gp = gp;
		this.keyH = keyH;
		this.tileM = new TileManager(gp);
		setX(tileM.getTileSize() * 9);
		setY(tileM.getTileSize() * 10);
		lives = 3;
		setSpeed(0);
		setDirection("right");
		Sprite.setSpriteSheet(Sprite.loadSprite("player/Knight"));
		BufferedImage[] stand = {Sprite.getSprite(0, 0)};
		BufferedImage[] walkLeft = {Sprite.getSprite(16, 0), Sprite.getSprite(32, 0)};
		BufferedImage[] walkRight = {Sprite.getSprite(32, 0), Sprite.getSprite(16, 0)};
		setStanding(gp.new Animation(stand, 6));
		setWalkingLeft(gp.new Animation(walkLeft, 6));
		setWalkingRight(gp.new Animation(walkRight, 6));
		setAnimation(getStanding());
		setImage(getAnimation().getSprite());
	}

	public GamePanel getGp() {
		return gp;
	}

	public void setGp(GamePanel gp) {
		this.gp = gp;
	}

	public KeyHandler getKeyH() {
		return keyH;
	}

	public void setKeyH(KeyHandler keyH) {
		this.keyH = keyH;
	}

	public int getScreenX() {
		return screenX;
	}

	public void setScreenX(int screenX) {
		this.screenX = screenX;
	}

	public int getScreenY() {
		return screenY;
	}

	public void setScreenY(int screenY) {
		this.screenY = screenY;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public void act() {
		if(!keyH.leftPressed && !keyH.rightPressed) {
			getAnimation().stop();
			getAnimation().reset();
			setAnimation(getStanding());
		    setImage(getAnimation().getSprite());
		}else {
			if (keyH.leftPressed) {
				setAnimation(getWalkingLeft());
				setImage(getAnimation().getSprite());
				getAnimation().start();
				getAnimation().update();
				setX(getX() - getSpeed());
			}
			if (keyH.rightPressed) {
				setAnimation(getWalkingRight());
				setImage(getAnimation().getSprite());
				getAnimation().start();
				getAnimation().update();
				setX(getX() + getSpeed());
			}
			if(getX() <= 0) {
				setX(0);
			}
			if(getX() >= tileM.getScreenWidth() - tileM.getTileSize()) {
				setX(tileM.getScreenWidth() - tileM.getTileSize());
			}
			screenX = (int)getX();
			screenY = (int)getY();
			if (keyH.shiftPressed == false) {
				setSpeed(6);
				getAnimation().setFrameDelay(6);
			} else {
				setSpeed(12);
				getAnimation().setFrameDelay(3);
			}
		}
		
	}

	public static class PlayerAttack extends Entity {
		public PlayerAttack() {}

		public PlayerAttack(float x, float y) {
			attack(x, y);
		}

		private void attack(float x, float y) {
			setImage(Sprite.loadSprite("player/arrowSprite"));
			setX(x + 17);
			setY(y - 1);
		}
	}
}
