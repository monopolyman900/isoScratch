package map;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utilities.ImageManipulator;

public class Chunk {
	ImageManipulator im = new ImageManipulator();
	
	public Region parentRegion;
	
	//actual coordinate of top left corner of tile *image*
	int pixelX;
	int pixelY;
	//offset
	int xOffset = 0;
	int yOffset = 0;
	//size of chunk in tiles width and height should match and always be square
	int chunkSize;
	
	//width and height of tile images
	int tileWidth;
	int tileHeight;
	
	//width and height in pixels
	int width;
	int height;
	
	public Integer val = 0;
	
	//decides whether to draw or not
	boolean onScreen;
	
	//two dimensional arraylist [x,y]
	public ArrayList<ArrayList<Tile>> tiles = new ArrayList<ArrayList<Tile>>();
	
	public Chunk(Region parentRegion, int chunkSize) {
		this.parentRegion = parentRegion;
		this.chunkSize = chunkSize;
		createTiles();
	}
	
	//draw all tiles vertically from top to bottom, left to right
	public void draw(Graphics g) {
		for(int i = 0; i < tiles.size(); i++) {
			for(int j = 0; j < tiles.get(i).size(); j++) {
				this.xOffset = parentRegion.xOffset;
				this.yOffset = parentRegion.yOffset;
				if(tiles.get(i).get(j).onScreen()) {
					//pass in x index and y index in relation to chunk
					tiles.get(i).get(j).draw(g);
				}
			}
		}
	}
	
	//draw all tiles vertically from top to bottom, left to right
	public void debugDraw(Graphics g) {
		for(int i = 0; i < tiles.size(); i++) {
			for(int j = 0; j < tiles.get(i).size(); j++) {
				this.xOffset = parentRegion.xOffset;
				this.yOffset = parentRegion.yOffset;
				if(tiles.get(i).get(j).onScreen()) {
					//pass in x index and y index in relation to chunk
					tiles.get(i).get(j).debugDraw(g);
				}
			}
		}
	}
	
	//creates an arraylist of empty tile objects - no image, no size
	public void createTiles() {
		for(int i = 0; i < chunkSize; i++) {
			ArrayList<Tile> tileColumn = new ArrayList<Tile>();
			tiles.add(tileColumn);
			for(int j = 0; j < chunkSize; j++) {
				tiles.get(i).add(new Tile(this));
			}
		}
	}
	
	//sets pixel position X and Y based on x/y index within parent chunk
	public void setPosInRegion(int xIndex, int yIndex) {
		/*
		 * x index runs along northeast/southwest sides, add half width for each X index pos, 
		 * subtract same amount for each Y index pos, because it moved back to the left as it moves down
		 */
		int xPos = (parentRegion.pixelX + (xIndex * (this.width/2)) - (yIndex * (this.width/2)));
		
		/*
		 * y index runs along northwest/southeast sides, add half heigth for each Y index pos to move down,
		 * add half height again for each x index as it X pos moves away from northern corner
		 * we don't want to subtract anything from Y, only add because the starting chunk pos is the northernmost corner
		 */
		int yPos = (parentRegion.pixelY + (yIndex * (this.height/2)) + (xIndex * (this.height/2)));
		this.pixelX = xPos;
		this.pixelY = yPos;
	}
	
	//XOffset for chunk and all tiles in chunk
	public int getXOffset() {
		return this.xOffset;
	}
	
	//YOffset for chunk and all tiles in chunk
	public int getYOffset() {
		return this.yOffset;
	}
	
	//set and get onScreen value
	public void setOnScreen(boolean onScreen) {
		this.onScreen = onScreen;
	}
	
	public boolean onScreen() {
		return onScreen;
	}
	
	//set width and height in pixels
	public void setSize(int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.width = tileWidth*chunkSize;
		this.height = tileHeight*chunkSize;
	}

}
