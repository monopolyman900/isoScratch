package map;

import java.awt.Graphics;
import java.util.ArrayList;

import utilities.ImageManipulator;

public class Region {
	ImageManipulator im = new ImageManipulator();
	//actual coordinate of top left corner of chunk's *image*
	int pixelX;
	int pixelY;
	//offset
	int xOffset = 0;
	int yOffset = 0;
	//size of region in chunks width and height should match and always be square
	int regionSize;
	
	//size of chunk in tiles
	int chunkSize;
	//width and height of chunk in pixels
	int chunkWidth;
	int chunkHeight;
	//width and height of region in pixels
	int height;
	int width;
	
	//decides whether to draw or not
	boolean onScreen;
	boolean isLoaded = false;
	
	//x,y index of region within entire map - starting region should be 0,0 and go from there
	public int xIndex;
	public int yIndex;
	
	//two dimensional arraylist [x,y]
	public ArrayList<ArrayList<Chunk>> chunks = new ArrayList<ArrayList<Chunk>>();
	
	public Region(int xIndex, int yIndex, int regionSize, int chunkSize) {
		this.regionSize = regionSize;
		this.chunkSize = chunkSize;
		this.xIndex = xIndex;
		this.yIndex = yIndex;
		createChunks();
	}
	
	//creates an arraylist of empty tile objects - no image, no size
	public void createChunks() {
		for(int i = 0; i < regionSize; i++) {
			ArrayList<Chunk> chunkColumn = new ArrayList<Chunk>();
			chunks.add(chunkColumn);
			for(int j = 0; j < regionSize; j++) {
				chunks.get(i).add(new Chunk(this, chunkSize));
			}
		}
	}
	
	//draw all tiles vertically from top to bottom, left to right
	public void draw(Graphics g) {
		for(int i = 0; i < chunks.size(); i++) {
			for(int j = 0; j < chunks.get(i).size(); j++) {
				if(chunks.get(i).get(j).onScreen()) {
					//pass in x index and y index in relation to chunk
					chunks.get(i).get(j).draw(g);
				}
			}
		}
	}
	
	//draw all tiles vertically from top to bottom, left to right
	public void debugDraw(Graphics g) {
		for(int i = 0; i < chunks.size(); i++) {
			for(int j = 0; j < chunks.get(i).size(); j++) {
				if(chunks.get(i).get(j).onScreen()) {
					//pass in x index and y index in relation to chunk
					chunks.get(i).get(j).debugDraw(g);
				}
			}
		}
	}
	
	//set chunk width/height based off of chunk sizes
	public void setSize(int tileWidth2, int tileHeight2) {
		//this should get called after tiles/chunks have been initialized
		for(int i = 0; i < chunks.size(); i ++) {
			for(int j = 0; j < chunks.get(i).size(); j++) {
				chunks.get(i).get(j).setSize(tileWidth2, tileHeight2);
			}
		}
		this.chunkWidth = chunks.get(0).get(0).width;
		this.chunkHeight = chunks.get(0).get(0).height;
		this.width = chunkWidth * regionSize;
		this.height = chunkHeight * regionSize;
	}
	
	//set pixel position of each region based on it's index
	public void setPixelPosFromIndex(int xIndex, int yIndex) {
		/*
		 * x index runs along northeast/southwest sides, add half width for each X index pos, 
		 * subtract same amount for each Y index pos, because it moved back to the left as it moves down
		 */
		int xPos = (xIndex * (this.width/2)) - (yIndex * (this.width/2));
		
		/*
		 * y index runs along northwest/southeast sides, add half heigth for each Y index pos to move down,
		 * add half height again for each x index as it X pos moves away from northern corner
		 * we don't want to subtract anything from Y, only add because the starting chunk pos is the northernmost corner
		 */
		int yPos = (yIndex * (this.height/2)) + (xIndex * (this.height/2));
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
	
	public boolean isLoaded() {
		return this.isLoaded;
	}
	
	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}
}
