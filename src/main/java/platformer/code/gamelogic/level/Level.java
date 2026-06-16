package platformer.code.gamelogic.level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.graphics.Camera;
import platformer.code.gameengine.loaders.Mapdata;
import platformer.code.gameengine.loaders.Tileset;
import platformer.code.gamelogic.GameResources;
import platformer.code.gamelogic.Main;
import platformer.code.gamelogic.enemies.Enemy;
import platformer.code.gamelogic.player.Player;
import platformer.code.gamelogic.tiledMap.Map;
import platformer.code.gamelogic.tiles.Flag;
import platformer.code.gamelogic.tiles.Flower;
import platformer.code.gamelogic.tiles.Gas;
import platformer.code.gamelogic.tiles.SolidTile;
import platformer.code.gamelogic.tiles.Spikes;
import platformer.code.gamelogic.tiles.Star;
import platformer.code.gamelogic.tiles.Tile;
import platformer.code.gamelogic.tiles.Water;

public class Level {

	private LevelData leveldata;
	private Map map;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();
	private ArrayList<Water> waterList = new ArrayList<>();
	private ArrayList<Gas> gasList = new ArrayList<>();
	private ArrayList<Star> starList = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;

	private double gasTimer = 0.0;
	private double maxGasTime = 5.0;

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData(){
		return leveldata;
	}

	public void restartLevel() {
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition*tileSize, yPosition*tileSize, this)); // TODO: objects vs tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15){
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
					gasList.add((Gas) tiles[x][y]);
				}
				else if (values[x][y] == 16){
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
					gasList.add((Gas) tiles[x][y]);
				}
				else if (values[x][y] == 17){
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
					gasList.add((Gas) tiles[x][y]);
				}
				else if (values[x][y] == 18) {
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
					waterList.add((Water) tiles[x][y]);
				}
				else if (values[x][y] == 19){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
					waterList.add((Water) tiles[x][y]);
				}
				else if (values[x][y] == 20) {
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
					waterList.add((Water) tiles[x][y]);
				}
				else if (values[x][y] == 21) {
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
					waterList.add((Water) tiles[x][y]);
				}
				else if (values[x][y] == 22) {
					tiles[x][y] = new Star(xPosition, yPosition, tileSize, tileset.getImage("Star"), this);
					starList.add((Star) tiles[x][y]);
				}
				
			} 
		}
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		while (waterList.size() != 0) {
			waterList.remove(0);
		}
		while (gasList.size() != 0) {
			gasList.remove(0);
		}
		while (enemiesList.size() != 0) {
			enemiesList.remove(0);
		}


		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Water 
			boolean touchingWater = false;
			for (int i = 0; i < waterList.size(); i++) {
				if (waterList.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					touchingWater = true;
				}
			}

			// Gas
			boolean touchingGas = false;
			for (int i = 0; i < gasList.size(); i++) {
				if (gasList.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					touchingGas = true;
				}
			}
			if (touchingGas) {
				gasTimer += tslf;

				if (gasTimer > maxGasTime) onPlayerDeath();
			} else {
				gasTimer = 0.0;
			}

			// Star
			boolean touchingStar = false;
			for (int i = 0; i < starList.size(); i++) {
				if (starList.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					touchingStar = true;
				}
			}

			// Update the player
			player.update(tslf, touchingWater, touchingStar);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			else if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			else if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			else if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			else if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if(flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					else
						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}

			


			// Update the enemies
			for (int i = 0; i < enemiesList.size(); i++) {
				try {
					Enemy e = enemiesList.get(i);

					// Touching water
					boolean touchingW = false;
					for (int j = 0; j < waterList.size(); j++) {
						if (waterList.get(j).getHitbox().isIntersecting(e.getHitbox())) {
							touchingW = true;
						}
					}

					// Update enemy
					enemiesList.get(i).update(tslf, touchingW);

					// Player death
					if (player.getHitbox().isIntersecting(enemiesList.get(i).getHitbox())) {
						onPlayerDeath();
					}

					// Enemy falls out of map
					if (map.getFullHeight() + 100 < enemiesList.get(i).getY()){
						enemiesList.remove(i);
						i--;
					}
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Out of bounds exception. ");
					System.out.println("This was caused by enemiesList being cleared due to player death before this code was executed");
				}
				
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}
	
	//Adds gas tiles until the requisite number of squares are filled or there is no more room 
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
		final int[] vOffset = {-1 , -1, -1, 0, 0, 1, 1, 1};
		final int[] hOffset = {0, -1, 1, -1, 1, 0, -1 , 1};

		Tile[][] tiles = map.getTiles();

		Gas g1 = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 1);
		map.addTile(col, row, g1);
		placedThisRound.add(g1);
		gasList.add(g1);

		int j = 0;
		while (placedThisRound.size() < numSquaresToFill) {
			if (j == placedThisRound.size()) break;

			for (int i = 0; i < 8; i++) {
				if (placedThisRound.size() >= numSquaresToFill) break;
      				   
				int thisCol = (placedThisRound.get(j)).getCol() + hOffset[i];
				int thisRow = (placedThisRound.get(j)).getRow() + vOffset[i];

				Tile t = tiles[thisCol][thisRow];

				if (!(t instanceof Gas || t instanceof Water || t instanceof SolidTile || t instanceof Spikes || t instanceof Star)) {
					Gas g2 = new Gas(thisCol, thisRow, tileSize, tileset.getImage("GasOne"), this, 1);
					map.addTile(thisCol, thisRow, g2);
					placedThisRound.add(g2);
					gasList.add(g2);
				}
			}

			j++;
		}
	}	


	//#############################################################################################################
	//Your code goes here! 
	//Please make sure you read the rubric/directions carefully and implement the solution recursively!
	private void water(int col, int row, Map map, int fullness) {
		Tile[][] t = map.getTiles();

		int actualFullness = fullness;

		if (actualFullness == 0 && row + 1 < t[0].length && (t[col][row+1] instanceof SolidTile)) {
			actualFullness = 3;
		}

		if (!(actualFullness > 3 || actualFullness < 0)) {

		String[] waterTypes = {"Falling_water", "Quarter_water", "Half_water", "Full_water"};

		Water w = new Water(col, row, tileSize, tileset.getImage(waterTypes[actualFullness]), this, actualFullness);
		
		map.addTile(col, row, w);
		waterList.add(w);

		
		if (row + 1 < t[0].length && !(t[col][row+1] instanceof SolidTile)) { // Down
			water(col, row + 1, map, 0);

		} else { // Left & Right (too lazy to determine which of the following is left and which is right)
			if (actualFullness == 1) actualFullness = 2;

			if (col - 1 >= 0) {
				if (!(t[col - 1][row] instanceof SolidTile) && !(t[col - 1][row] instanceof Water)) {
					water(col - 1, row, map, actualFullness - 1);
				}
			}

			if (col + 1 < t.length) {
				if (!(t[col + 1][row] instanceof SolidTile) && !(t[col + 1][row] instanceof Water)) {
					water(col + 1, row, map, actualFullness - 1);
				}
			}
		}

		}
	}



	public void draw(Graphics g) {
	   	 g.translate((int) -camera.getX(), (int) -camera.getY());
	   	 // Draw the map
	   	 for (int x = 0; x < map.getWidth(); x++) {
	   		 for (int y = 0; y < map.getHeight(); y++) {
	   			 Tile tile = map.getTiles()[x][y];
	   			 if (tile == null)
	   				 continue;
	   			 if(tile instanceof Gas) {
	   				
	   				 int adjacencyCount =0;
	   				 for(int i=-1; i<2; i++) {
	   					 for(int j =-1; j<2; j++) {
	   						 if(j!=0 || i!=0) {
	   							 if((x+i)>=0 && (x+i)<map.getTiles().length && (y+j)>=0 && (y+j)<map.getTiles()[x].length) {
	   								 if(map.getTiles()[x+i][y+j] instanceof Gas) {
	   									 adjacencyCount++;
	   								 }
	   							 }
	   						 }
	   					 }
	   				 }
	   				 if(adjacencyCount == 8) {
	   					 ((Gas)(tile)).setIntensity(2);
	   					 tile.setImage(tileset.getImage("GasThree"));
	   				 }
	   				 else if(adjacencyCount >5) {
	   					 ((Gas)(tile)).setIntensity(1);
	   					tile.setImage(tileset.getImage("GasTwo"));
	   				 }
	   				 else {
	   					 ((Gas)(tile)).setIntensity(0);
	   					tile.setImage(tileset.getImage("GasOne"));
	   				 }
	   			 }
	   			 if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
	   				 tile.draw(g);
	   		 }
	   	 }


	   	 // Draw the enemies
	   	 for (int i = 0; i < enemiesList.size(); i++) {
	   		 enemiesList.get(i).draw(g);
	   	 }
 

	   	 // Draw the player
	   	 player.draw(g, gasTimer, maxGasTime);




	   	 // used for debugging
	   	 if (Camera.SHOW_CAMERA)
	   		 camera.draw(g);
	   	 g.translate((int) +camera.getX(), (int) +camera.getY());
	    }


	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}
}