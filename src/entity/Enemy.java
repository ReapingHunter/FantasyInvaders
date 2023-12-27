package entity;

import java.awt.image.BufferedImage;
import main.GamePanel;
import main.GamePanel.Sprite;

public abstract class Enemy extends Entity {
	private GamePanel gp;
	private int health;
	private EnemyAttack attack;
	
	public Enemy(GamePanel gp, float x, float y) {
		this.gp = gp;
		setX(x);
		setY(y);
		attack = new EnemyAttack(x, y);
		setSpeed(0.6f);
		setDirection("left");
	}
	
	public GamePanel getGp() {
		return gp;
	}

	public void setGp(GamePanel gp) {
		this.gp = gp;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public EnemyAttack attack() {
		return attack;
	}


	public void move() {
		if(getDirection() == "left") {
			setAnimation(getWalkingLeft());
			setImage(getAnimation().getSprite());
			getAnimation().start();
			getAnimation().update();
			setX(getX() - getSpeed());
		} else if(getDirection() == "right") {
			setAnimation(getWalkingRight());
			setImage(getAnimation().getSprite());
			getAnimation().start();
			getAnimation().update();
			setX(getX() + getSpeed());
		}
	}
	
	public void moveLose(Player player, int screenWidth, int tileSize) {
		if (getY() < player.getY()) {
			setY(getY() + 8);
		} else if (getX() < (screenWidth - tileSize) / 2 - 8) {
			setX(getX() + 8);
		} else if (getX() > (screenWidth - tileSize) / 2 + 8) {
			setX(getX() - 8);
		} else {
			die();
		}
		getAnimation().setFrameDelay(1);
		setAnimation(getWalkingLeft());
		setImage(getAnimation().getSprite());
		getAnimation().start();
		getAnimation().update();
		setAnimation(getWalkingRight());
		setImage(getAnimation().getSprite());
		getAnimation().start();
		getAnimation().update();
		
	}

	public static class Skeleton extends Enemy {
		public Skeleton(GamePanel gp, float x, float y) {
			super(gp, x, y);
			Sprite.setSpriteSheet(Sprite.loadSprite("enemy/Skeleton"));
			BufferedImage[] stand = {Sprite.getSprite(0, 0)};
			BufferedImage[] walkLeft = {Sprite.getSprite(32, 0), Sprite.getSprite(16, 0)};
			BufferedImage[] walkRight = {Sprite.getSprite(16, 0), Sprite.getSprite(32, 0)};
			setStanding(gp.new Animation(stand, 12));
			setWalkingLeft(gp.new Animation(walkLeft, 12));
			setWalkingRight(gp.new Animation(walkRight, 12));
			setAnimation(getStanding());
			setImage(getAnimation().getSprite());
			setHealth(1);
		}

	}

	public static class ArmoredSkeleton extends Skeleton {
		public ArmoredSkeleton(GamePanel gp, float x, float y) {
			super(gp, x, y);
			Sprite.setSpriteSheet(Sprite.loadSprite("enemy/Skeleton"));
			BufferedImage[] stand = {Sprite.getSprite(0, 0)};
			BufferedImage[] walkLeft = {Sprite.getSprite(32, 32), Sprite.getSprite(16, 32)};
			BufferedImage[] walkRight = {Sprite.getSprite(16, 32), Sprite.getSprite(32, 32)};
			setStanding(gp.new Animation(stand, 12));
			setWalkingLeft(gp.new Animation(walkLeft, 12));
			setWalkingRight(gp.new Animation(walkRight, 12));
			setAnimation(getStanding());
			setImage(getAnimation().getSprite());
			setHealth(2);
		}
	}
	
	public static class DemolisherSkeleton extends Skeleton {
		public DemolisherSkeleton(GamePanel gp, float x, float y) {
			super(gp, x, y);
			Sprite.setSpriteSheet(Sprite.loadSprite("enemy/Skeleton"));
			BufferedImage[] stand = {Sprite.getSprite(0, 0)};
			BufferedImage[] walkLeft = {Sprite.getSprite(32, 64), Sprite.getSprite(16, 64)};
			BufferedImage[] walkRight = {Sprite.getSprite(16, 64), Sprite.getSprite(32, 64)};
			setStanding(gp.new Animation(stand, 12));
			setWalkingLeft(gp.new Animation(walkLeft, 12));
			setWalkingRight(gp.new Animation(walkRight, 12));
			setAnimation(getStanding());
			setImage(getAnimation().getSprite());
			setHealth(1);
		}
	}
	
	public static class TrackerSkeleton extends Skeleton {
		public TrackerSkeleton(GamePanel gp, float x, float y) {
			super(gp, x, y);
			Sprite.setSpriteSheet(Sprite.loadSprite("enemy/Skeleton"));
			BufferedImage[] stand = {Sprite.getSprite(0, 0)};
			BufferedImage[] walkLeft = {Sprite.getSprite(32, 96), Sprite.getSprite(16, 96)};
			BufferedImage[] walkRight = {Sprite.getSprite(16, 96), Sprite.getSprite(32, 96)};
			setStanding(gp.new Animation(stand, 12));
			setWalkingLeft(gp.new Animation(walkLeft, 12));
			setWalkingRight(gp.new Animation(walkRight, 12));
			setAnimation(getStanding());
			setImage(getAnimation().getSprite());
			setHealth(1);
		}
	}
}
