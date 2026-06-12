package platformer.code.gamelogic.enemies;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.hitbox.RectHitbox;
import platformer.code.gamelogic.GameResources;
import platformer.code.gamelogic.level.Level;

public class Enemy extends PhysicsObject{

	private float walkSpeed = 80;
	private BufferedImage image;
	
	public Enemy(float x, float y, Level level) {
		super(x, y,(int)(level.getLevelData().getTileSize()*1.5), (int)(level.getLevelData().getTileSize()*1.5), level);
		movementVector.x = walkSpeed;
		this.hitbox = new RectHitbox(this, 10, 10, width - 10, height - 10);
		this.image = GameResources.enemy;
	}
	
	public void update(float tslf, boolean touchingWater) {
		super.update(tslf);
		
		if(collisionMatrix[LEF] != null) {
			movementVector.x = walkSpeed;
		} else if(collisionMatrix[RIG] != null) {
			movementVector.x = -walkSpeed;
		}

		if (touchingWater) {
			movementVector.y /= 1.5;
			movementVector.y -= 200;
		}
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(image, (int)position.x, (int)position.y, width, height, null);
		
		hitbox.draw(g);
	}
	
}