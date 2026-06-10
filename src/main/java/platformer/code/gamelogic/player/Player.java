package platformer.code.gamelogic.player;

import java.awt.Color;
import java.awt.Graphics;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.graphics.MyGraphics;
import platformer.code.gameengine.hitbox.RectHitbox;
import platformer.code.gamelogic.Main;
import platformer.code.gamelogic.level.Level;
import platformer.code.gamelogic.tiles.Tile;

public class Player extends PhysicsObject{
	public float walkSpeed = 400;
	public float jumpPower = 1350;

	private boolean isJumping = false;

	public Player(float x, float y, Level level) {
	
		super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
		int offset =(int)(level.getLevelData().getTileSize()*0.1); //hitbox is offset by 10% of the player size.
		this.hitbox = new RectHitbox(this, offset,offset, width -offset, height - offset);
	}

	//@Override
	public void update(float tslf, boolean touchingWater, boolean touchingStar) {
		super.update(tslf);
		
		movementVector.x = 0;
		if(PlayerInput.isLeftKeyDown()) {
			movementVector.x = -walkSpeed;
		}
		if(PlayerInput.isRightKeyDown()) {
			movementVector.x = +walkSpeed;
		}
		if(PlayerInput.isJumpKeyDown() && (!isJumping || touchingWater || touchingStar)) {
			if (touchingWater) {
				movementVector.y = -jumpPower;
				isJumping = true;
				
			} else if (touchingStar) {
				movementVector.y = -2 * jumpPower;
				isJumping = true;
			} else {
				movementVector.y = -jumpPower;
				isJumping = true;
			}
			
		}
		
		isJumping = true;
		if(collisionMatrix[BOT] != null) isJumping = false;

		if (touchingWater) {
			movementVector.y /= 1.5;
			movementVector.y -= 200;
			
		}
	}

	
	public void draw(Graphics g, double timeInGas, double maxTime) {
		double percentage = timeInGas / maxTime * 100.0;

		Color c = new Color(255, 255, 0);

		for (int i = 1; i < 6; i++) {
			if (percentage > 20 * i) {
				c = c.darker();
			}
		}
		g.setColor(c);

	
		MyGraphics.fillRectWithOutline(g, (int)getX(), (int)getY(), width, height);
		
		if(Main.DEBUGGING) {
			for (int i = 0; i < closestMatrix.length; i++) {
				Tile t = closestMatrix[i];
				if(t != null) {
					g.setColor(Color.RED);
					g.drawRect((int)t.getX(), (int)t.getY(), t.getSize(), t.getSize());
				}
			}
		}
		
		hitbox.draw(g);
	}

}
