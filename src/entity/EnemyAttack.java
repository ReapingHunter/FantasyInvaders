package entity;

public class EnemyAttack extends Entity {
	private boolean isDestroyed;
	public EnemyAttack(float x, float y) {
		initAttack(x, y);
	}

	public boolean isDestroyed() {
		return isDestroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.isDestroyed = destroyed;
	}

	public void initAttack(float x, float y) {
		setDestroyed(true);
		setX(x);
		setY(y);
	}
}
