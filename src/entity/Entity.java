package entity;

import java.awt.image.BufferedImage;

import main.GamePanel.Animation;

public abstract class Entity {
	private float x;
	private float y;
	private float speed;
	private BufferedImage image;
	private String direction;
	private boolean visible;
	
	// Sprites and Animations for players and enemies
	private Animation standing;
	private Animation walkingLeft;
	private Animation walkingRight;
	
	// Actual animation
	private Animation animation;

	public Entity() {
		visible = true;
	}

	public void die() {
		visible = false;
	}

	public boolean isVisible() {

		return visible;
	}

	public void setVisible(boolean visible) {

		this.visible = visible;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Animation getStanding() {
		return standing;
	}

	public void setStanding(Animation standing) {
		this.standing = standing;
	}

	public Animation getWalkingLeft() {
		return walkingLeft;
	}

	public void setWalkingLeft(Animation walkingLeft) {
		this.walkingLeft = walkingLeft;
	}

	public Animation getWalkingRight() {
		return walkingRight;
	}

	public void setWalkingRight(Animation walkingRight) {
		this.walkingRight = walkingRight;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
	
}
