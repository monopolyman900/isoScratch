package map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import animation.CollidableObject;

public class Tile extends CollidableObject{

	boolean debugMode = false;
	
	public Chunk parentChunk;
	BufferedImage tileImg;
	
	//width and height in pixels
	int width;
	int height;
	
	//x and y values based on tile grid - z based on height up
	int tileX;
	int tileY;
	//actual coordinate of top left corner of tile *image*
	int pixelX;
	int pixelY;
	//absolute position
	public int absTileX;
	public int absTileY;
	public int chunkX;
	public int chunkY;
	public int regionX;
	public int regionY;
	
	public Integer val;
	
	//onscreen or not - determine whether to draw
	boolean onScreen;
	
	public Tile(Chunk parentChunk) {
		this.parentChunk = parentChunk;
	}
	
	public void setImage(BufferedImage img) {
		this.tileImg = img;
	}
	
	public void draw(Graphics g) {
		g.drawImage(tileImg, pixelX + parentChunk.getXOffset(), pixelY + parentChunk.getYOffset() - val, null);
	}
	
	public void debugDraw(Graphics g) {
		g.setFont(new Font("Lucida Console", Font.PLAIN, 10));
		g.drawImage(tileImg, pixelX + parentChunk.getXOffset(), pixelY + parentChunk.getYOffset() - val, null);
		g.setColor(Color.WHITE);
		if(this.val != null) {
			g.drawString(this.val.toString(), pixelX + parentChunk.getXOffset() + this.width/2 - 15, pixelY + parentChunk.getYOffset() + this.height/2 - 8 - val);
		}
		//absolute position
		g.drawString("X: " + this.absTileX, pixelX + parentChunk.getXOffset() + this.width/2 - 15, pixelY + parentChunk.getYOffset() + this.height/2 + 0 - val);
		g.drawString("Y: " + this.absTileY, pixelX + parentChunk.getXOffset() + this.width/2 - 15, pixelY + parentChunk.getYOffset() + this.height/2 + 8 - val);
		//g.drawString("ChunkX: " + this.chunkX, pixelX + parentChunk.getXOffset() + this.width/2 - 15, pixelY + parentChunk.getYOffset() + this.height/2 + 10);
	}
	
	//sets pixel position X and Y based on x/y index within parent chunk
	public void setPosInChunk(int xIndex, int yIndex, int tileWidth, int tileHeight) {
		this.width = tileWidth;
		this.height = tileHeight;
		/*
		 * x index runs along northeast/southwest sides, add half width for each X index pos, 
		 * subtract same amount for each Y index pos, because it moved back to the left as it moves down
		 */
		int xPos = (parentChunk.pixelX + (xIndex * (this.width/2)) - (yIndex * (this.width/2)));
		
		/*
		 * y index runs along northwest/southeast sides, add half heigth for each Y index pos to move down,
		 * add half height again for each x index as it X pos moves away from northern corner
		 * we don't want to subtract anything from Y, only add because the starting chunk pos is the northernmost corner
		 */
		int yPos = (parentChunk.pixelY + (yIndex * (this.height/2)) + (xIndex * (this.height/2)));
		this.pixelX = xPos;
		this.pixelY = yPos;
	}
	
	public void setOnScreen(boolean onScreen) {
		this.onScreen = onScreen;
	}
	
	public boolean onScreen() {
		return onScreen;
	}
	
}
