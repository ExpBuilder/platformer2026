package platformer.code.gamelogic.tiles;

import platformer.code.gameengine.hitbox.RectHitbox;

import java.awt.image.BufferedImage;
import platformer.code.gamelogic.level.Level;

public class Star extends Tile {
    public Star(float x, float y, int size, BufferedImage image, Level level) {
		super(x, y, size, image, false, level);
		this.hitbox = new RectHitbox(x*size , y*size, 0, 10, size, size);
	}

}
