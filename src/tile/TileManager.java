package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;
import java.awt.Color;

public class TileManager {
	// FINAL SCREEN SETTINGS
	private final int ORIGINAL_TILE_SIZE = 16; // 16x16 tile
	private final int SCALE = 4;
	private final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE; // 64x64 tile
	private final int MAX_SCREEN_COL = 24; // original = 24
	private final int MAX_SCREEN_ROW = 13; // original = 13
	private final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
	private final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;

	private GamePanel gp;
	private Tile[] tile;
	private int mapTileNum[][];
	
	public TileManager(GamePanel gp) {
		this.gp = gp;
		tile = new Tile[70];
		mapTileNum = new int[MAX_SCREEN_COL][SCREEN_WIDTH];

		getTileImage();
		loadMap();
	}

	public int getTileSize() {
		return TILE_SIZE;
	}

	public int getScale() {
		return SCALE;
	}

	public int getOriginalTileSize() {
		return ORIGINAL_TILE_SIZE;
	}

	public int getMaxScreenCol() {
		return MAX_SCREEN_COL;
	}

	public int getMaxScreenRow() {
		return MAX_SCREEN_ROW;
	}

	public int getScreenWidth() {
		return SCREEN_WIDTH;
	}

	public int getScreenHeight() {
		return SCREEN_HEIGHT;
	}	

	public Tile[] getTile() {
		return tile;
	}

	public void setTile(Tile[] tile) {
		this.tile = tile;
	}

	public int[][] getMapTileNum() {
		return mapTileNum;
	}

	public void setMapTileNum(int[][] mapTileNum) {
		this.mapTileNum = mapTileNum;
	}

	public void getTileImage() {
		try {
			System.out.println("Resource path: " + getClass().getResource("/tiles/Outside.png"));
			System.out.println("Classpath: " + System.getProperty("java.class.path"));

			tile[0] = new Tile();
			tile[0].setImage(ImageIO.read(getClass().getResourceAsStream("/tiles/Outside.png")));

			for (int i = 1; i < 56; i++) {
				tile[i] = new Tile();
				tile[i].setImage(ImageIO.read(getClass().getResourceAsStream("/tiles/joegrass.png")));
				if (i == 34 || (i >= 62 && i <= 67)) {
					tile[i].setCollision(true);
				}
			}

			for (int i = 56; i <= 67; i++) {
				tile[i] = new Tile();
				try {
					String imageName = "/tiles/fort" + i + ".png";
					tile[i].setImage(ImageIO.read(getClass().getResourceAsStream(imageName)));
					if ((i >= 62 && i <= 67)) {
						tile[i].setCollision(true);
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Failed to load image at path: /tiles/fort" + (i - 55) + ".png");
				}
			}
			tile[1].setCollision(true);
			tile[2].setCollision(true);
			tile[68] = new Tile();
			tile[69] = new Tile();
			tile[68].setImage(ImageIO.read(getClass().getResourceAsStream("/tiles/fort56.png")));
			tile[68].setCollision(true);
			tile[69].setImage(ImageIO.read(getClass().getResourceAsStream("/tiles/fort61.png")));
			tile[69].setCollision(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadMap() {
		try {
			InputStream is = getClass().getResourceAsStream("/maps/world01.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			int col = 0;
			int row = 0;

			while (col < MAX_SCREEN_COL && row < MAX_SCREEN_ROW) {

				String line = br.readLine();

				while (col < MAX_SCREEN_COL) {

					String numbers[] = line.split(" ");
					int num = Integer.parseInt(numbers[col]);

					mapTileNum[col][row] = num;
					col++;
				}
				if (col == MAX_SCREEN_COL) {
					col = 0;
					row++;
				}
			}
			br.close();

		} catch (Exception e) {

		}
	}

	public void draw(Graphics2D g2) {
		int worldCol = 0;
		int worldRow = 0;

		while (worldCol < MAX_SCREEN_COL && worldRow < MAX_SCREEN_ROW) {
			int tileNum = mapTileNum[worldCol][worldRow];

			int worldX = worldCol * TILE_SIZE;
			int worldY = worldRow * TILE_SIZE;
			int screenX = worldX - (int) gp.getPlayer().getX() + gp.getPlayer().getScreenX();
			int screenY = worldY - (int) gp.getPlayer().getY() + gp.getPlayer().getScreenY();
			// Draw the tile image
			g2.drawImage(tile[tileNum].getImage(), screenX, screenY, TILE_SIZE, TILE_SIZE, null);

			// Draw the corresponding number on top of the tile
			g2.setColor(Color.WHITE);

			worldCol++;

			if (worldCol == MAX_SCREEN_COL) {
				worldCol = 0;
				worldRow++;
			}
		}
	}

}
