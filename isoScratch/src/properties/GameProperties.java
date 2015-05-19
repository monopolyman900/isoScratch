package properties;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import utilities.ImageManipulator;


/**
 * 
 * game specific properties - seed, etc
 *
 */
public class GameProperties implements Serializable{
	
	ImageManipulator im = new ImageManipulator();
	

	//region width/height in chunks
	public final int REGION_SIZE = 5;
	
	//chunk width/height in tiles
	public final int CHUNK_SIZE = 5;
	
	//number to round tile height to, amount to multipy tile height by in pixels
	public final int HEIGHT_ROUNDER = 1;
	public final int HEIGHT_MULTIPLIER = 1;
	
	//minimum ocean height
	public final int OCEAN_HEIGHT = 10;
	
	//map seed
	public int seed = 0;
	public int playerPosX = 0;
	public int playerPosY = 0;
	
	public Integer hardTileWidth = 100;
	public Integer hardTileHeight = 50;
	
	//tile dimensions
	public int tileWidth;
	public int tileHeight;

	public GameProperties() {
		BufferedImage sampleTile = im.loadImage("Images/Tiles/grassTile.png");
		if(this.hardTileWidth == null) {
			this.tileWidth = this.hardTileWidth;
		} else {
			this.tileWidth = sampleTile.getWidth();
		}
		if(this.hardTileHeight == null) {
			this.tileHeight = this.hardTileHeight;
		} else {
			this.tileHeight = sampleTile.getHeight();
		}
	}
	
	public void setSeed(int seed) {
		this.seed = seed;
	}
	
	public void setPlayerPos(int x, int y) {
		this.playerPosX = x;
		this.playerPosY = y;
	}
}
