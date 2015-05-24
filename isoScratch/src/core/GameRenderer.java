package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;
import java.util.Iterator;

import map.Region;

import org.apache.commons.lang3.Pair;

import utilities.ImageManipulator;

public class GameRenderer {
	
	//game objects
	ImageManipulator im = new ImageManipulator();
	GameManager gameManager;
	
	//background image
	BufferedImage background = im.loadImage("Images/Backgrounds/background.png");
	
	public GameRenderer(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	/**
	 * Actually draw everything that needs to be drawn
	 * Layering also happens here 
	 */
	public void draw(Graphics2D g, int screenWidth, int screenHeight) {
		//draw background on first layer
		g.drawImage(background, 0, 0, null);
		//draw all items in GameManager

		if(!gameManager.systemProperties.DEBUG_MAP) {
			if(gameManager.mapManager != null) {
				Iterator<Entry<Pair<Integer, Integer>, Region>> regionIter  = gameManager.mapManager.loadedRegions.entrySet().iterator();
				while(regionIter.hasNext()) {
					Entry<Pair<Integer,Integer>, Region> curRecord = regionIter.next();
					Pair<Integer,Integer> curIndex = curRecord.getKey();
					Region curRegion = curRecord.getValue();
					System.out.println(curIndex);
					curRegion.draw(g);
				}
			}
		} else {
			debugScreen(g, screenWidth, screenHeight);
		}
		//don't draw the player if player focus is false
		if(gameManager.systemProperties.PLAYER_FOCUS) {
			if(gameManager.player != null) {
				gameManager.player.draw(g, (int) gameManager.player.getX(), (int) gameManager.player.getY());
			}
		}
	}
	
	//using this as a misc function to debug screen stuff
	public void debugScreen(Graphics2D g, int screenWidth, int screenHeight) {
		if(gameManager.mapManager != null) {
			Iterator<Entry<Pair<Integer, Integer>, Region>> regionIter  = gameManager.mapManager.loadedRegions.entrySet().iterator();
			while(regionIter.hasNext()) {
				Entry<Pair<Integer,Integer>, Region> curRecord = regionIter.next();
				Pair<Integer,Integer> curIndex = curRecord.getKey();
				Region curRegion = curRecord.getValue();
				curRegion.debugDraw(g);
			}
		}
		//find currently-calculated screen edges
		//misc values used to draw solid lines
		/*
		g.setColor(Color.RED);
		//horizontal line
		g.drawLine(0, (int)(screenHeight/2), 5000, (int)(screenHeight/2));
		//vertical line
		g.drawLine((int)(screenWidth/2), 0, (int)(screenWidth/2), 5000);
		*/
	}
	
}
