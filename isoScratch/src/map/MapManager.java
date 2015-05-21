package map;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.Pair;

import mapGenerator.MapGenerator;
import player.Camera;
import player.Player;
import properties.GameProperties;

public class MapManager {
	
	MapGenerator mapGenerator;
	
	//screen size used to determine what needs to be drawn
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int screenWidth = screenSize.width;
	int screenHeight = screenSize.height;

	//First dimension of chunks is X axis, second is Y
	//all relevant region objects
	Iterator<Entry<Pair<Integer, Integer>, Region>> regionIter;
	//tmp map to add to while looping through original
	HashMap<Pair<Integer, Integer>, Region> tmpRegions = new HashMap<Pair<Integer, Integer>, Region>();
	//loaded regions - 2 dimensional map -> x/y Pair, and region
	public HashMap<Pair<Integer, Integer>, Region> loadedRegions = new HashMap<Pair<Integer, Integer>, Region>();
	
	public GameProperties gameProperties;
	
	//number of regions in x/y axes
	public int regionCountX = 1;
	public int regionCountY = 1;
	//region width/height in chunks
	public int regionSize;
	
	//chunk width/height in tiles
	public int chunkSize;
	
	//number to round tile height to, amount to multipy tile height by in pixels
	public int heightRounder;
	public int heightMultiplier;
	
	//minimum ocean height
	public int oceanHeight;
	
	//tile dimensions in pixels
	public int tileWidth;
	public int tileHeight;
	
	//player to determine offset
	Camera camera;
	Player player;
	//seed
	private int seed;
	
	public MapManager(Camera camera, GameProperties gameProperties) {
		this.camera = camera;
		this.player = camera.player;
		this.gameProperties = gameProperties;
		this.tileWidth = gameProperties.tileWidth;
		this.tileHeight = gameProperties.tileHeight;
		//these next 4 are all finals in GameManager
		this.regionSize = gameProperties.REGION_SIZE;
		this.chunkSize = gameProperties.CHUNK_SIZE;
		this.heightRounder = gameProperties.HEIGHT_ROUNDER;
		this.oceanHeight = gameProperties.OCEAN_HEIGHT;
		this.heightMultiplier = gameProperties.HEIGHT_MULTIPLIER;
		initializeRegion();
	}

	public void updateRegions() {
		//update all region positions
		regionIter = loadedRegions.entrySet().iterator();
		while(regionIter.hasNext()) {
			Region curRegion = regionIter.next().getValue();
			curRegion.xOffset = -1*(camera.pixelX);
			curRegion.yOffset = -1*(camera.pixelY);
		}
		setOnScreen();
	}
	
	public void initializeRegion() {
		//build out regions, build out empty tiles into regions
		for(int i = 0; i < regionCountX; i++) {
			for(int j = 0; j < regionCountY; j++) {
				//initialize starting region
				//TODO: remove this and make this more standardized
				Region initialRegion = new Region(i, j, regionSize, chunkSize);
				Pair<Integer, Integer> regionIndexes = new Pair<Integer, Integer>(initialRegion.xIndex, initialRegion.yIndex);
				loadedRegions.put(regionIndexes, initialRegion);
			}
		}
		mapGenerator = new MapGenerator(loadedRegions, regionSize, regionCountX, regionCountY, chunkSize, heightRounder, oceanHeight, heightMultiplier, seed);
		//loop back through and set chunk sizes in each region and region position now that tile size is set
		regionIter = loadedRegions.entrySet().iterator();
		while(regionIter.hasNext()) {
			Entry<Pair<Integer, Integer>, Region> curEntry = regionIter.next();
			Pair<Integer, Integer> curIndex = curEntry.getKey();
			Region curRegion = curEntry.getValue();
			curRegion.setSize(this.tileWidth, this.tileHeight);
			curRegion.setPixelPosFromIndex((Integer) curIndex.left, (Integer) curIndex.right);
		}
	}
	
	//passes seed back to game manager
	public int getSeed() {
		return this.mapGenerator.getSeed();
	}
	
	//see if chunk parameter needs to be drawn or not
	public void setOnScreen() {
		
		//copy regions map to tmp regions map - alter tmp regions, replace loadedRegions with tmp
		tmpRegions.clear();
		tmpRegions.putAll(loadedRegions);
		
		//loop through regions
		regionIter = loadedRegions.entrySet().iterator();
		while(regionIter.hasNext()) {
			Entry<Pair<Integer, Integer>, Region> curEntry = regionIter.next();
			Region curRegion = curEntry.getValue();
				
			ArrayList<ArrayList<Chunk>> chunks = curRegion.chunks;
			boolean onScreen = true;
			
			//biggest chunk value in array list for each axis
			int xMax = chunks.size() - 1;
			int yMax = chunks.get(0).size() - 1;
			//all chunks should be same height, get dimensions from first one
			int chunkWidth = curRegion.chunkWidth;
			int chunkHeight = curRegion.chunkHeight;
			
			//absolute positions on the screen
			//northernY is base region position + YOffset
			//southernY region position + YOffset + y distance to the west + then y distance south + chunk height for southernmost chunk
			//westernX is base region position + XOffset - distance to the west
			//easternX is base region position + XOffset + distance to the east + 1 more chunk for easternmost chunk - I'm not sure why but this isn't needed for west
			int northernY = curRegion.pixelY + curRegion.getYOffset() - 500;
			int southernY = curRegion.pixelY + curRegion.getYOffset() + (yMax*(chunkHeight/2)) + (xMax*(chunkHeight/2)) + chunkHeight;
			if(curRegion.val != null) southernY -= curRegion.val;
			int westernX = curRegion.pixelX + curRegion.getXOffset() - (yMax*(chunkWidth/2));
			int easternX = curRegion.pixelX + curRegion.getXOffset() + (xMax*(chunkWidth/2) + (chunkWidth));
			
			//run through chunk corners.. 
			//if bottom of screen y val < northernChunk y val -> onscreen is false 			->northern tip of map is below bottom of screen
			//if top of screen y val > southernChunk y val + chunkHeight -> onscreen = false	->southern tip of map is above top of screen
			//if right of screen x val < westernChunk x val -> onscreen = false				->western tip of map is to the right of right edge of screen
			//if left of screen x val > eastern chunk-> onscreen = false						->eastern tip of map is to the right of left edge of screen
			if(northernY > screenHeight) {
				onScreen = false;
			}
			else if(southernY < 0) {
				onScreen = false;
			}
			else if(westernX > screenWidth) {
				onScreen = false;
			}
			else if(easternX < 0) {
				onScreen = false;
			}
			if(onScreen) {
				//set onScreen chunks for currently onscreen regions
				for(int k = 0; k < chunks.size(); k++) {
					for(int l = 0; l < chunks.get(k).size(); l++) {
						Chunk curChunk = chunks.get(k).get(l);
						//set pixelX, pixelY of chunk before evaluating onscreen
						curChunk.setPosInRegion(k, l);
						boolean chunkOnScreen = true;
						int northernChunkY = curChunk.pixelY + curRegion.yOffset - 500;
						int southernChunkY = curChunk.pixelY + curRegion.yOffset + chunkHeight;
						if(curChunk.val != null) southernChunkY -= curChunk.val;
						//TODO: in theory the next line shouldn't need the "- chunkWidth" part. But displays weird without it. - remove
						int westernChunkX = curChunk.pixelX + curRegion.xOffset - chunkWidth;
						int easternChunkX = curChunk.pixelX + curRegion.xOffset + chunkWidth;
						if(northernChunkY > screenHeight) {
							chunkOnScreen = false;
						}
						else if(southernChunkY < 0) {
							chunkOnScreen = false;
						}
						else if(westernChunkX > screenWidth) {
							chunkOnScreen = false;
						}
						else if(easternChunkX < 0) {
							chunkOnScreen = false;
						}
						curChunk.setOnScreen(chunkOnScreen);
						//set onscreen tiles for currently onscreen chunks
						if(curChunk.onScreen) {
							//check to see if more regions need to be loaded
							setLoadedRegions(k, l, curRegion.xIndex, curRegion.yIndex);
							ArrayList<ArrayList<Tile>> tiles = curChunk.tiles;
							for(int m = 0; m < tiles.size(); m++) {
								for(int n = 0; n < tiles.get(m).size(); n++) {
									Tile curTile = tiles.get(m).get(n);
									//set pixelX, pixelY of tile before evaluating onscreen
									curTile.setPosInChunk(m, n, this.tileWidth, this.tileHeight);
									boolean tileOnScreen = true;
									int northernTileY = curTile.pixelY + curChunk.yOffset - 500;
									//TODO: I shouldn't need the curTile.height added twice here but it renders weird without it
									int southernTileY = curTile.pixelY + curChunk.yOffset + curTile.height + curTile.height;
									int westernTileX = curTile.pixelX + curChunk.xOffset;
									//TODO: same kind of issue as above
									int easternTileX = curTile.pixelX + curChunk.xOffset + curTile.width + curTile.width;
									if(northernTileY > screenHeight) {
										tileOnScreen = false;
									}
									else if(southernTileY < 0) {
										tileOnScreen = false;
									}
									else if(westernTileX > screenWidth) {
										tileOnScreen = false;
									}
									else if(easternTileX < 0) {
										tileOnScreen = false;
									}
									curTile.setOnScreen(tileOnScreen);
									//generate new tile images when needed
									if(curTile.onScreen && curTile.tileImg == null) {
										//passing in tile object, indexes: TileX, TileY, ChunkX, ChunkY, RegionX, RegionY
										mapGenerator.generateTile(curTile, m, n, k, l, curEntry.getKey().left, curEntry.getKey().right);
									}
								}
							}
						}
					}
				}
		
			} else {
				tmpRegions.remove(curEntry.getKey());
			}
		}
		loadedRegions.clear();
		loadedRegions.putAll(tmpRegions);
	}
	
	//x and y of current chunk - xIndex, yIndex of current region
	public void setLoadedRegions(int x, int y, int xIndex, int yIndex) {
		
		Direction direction = Direction.NONE;
		Pair<Integer, Integer> curPair = null;
		
		if(x <= (regionSize/2)) {
			direction = Direction.WEST;
		} else if(x > (regionSize/2)) {
			direction = Direction.EAST;
		}
		//NORTH
		if(y <= (regionSize/2)) {
			if(direction == Direction.WEST) {
				direction = Direction.NORTHWEST;
				curPair = new Pair<Integer, Integer>(xIndex - 1, yIndex);
			} else if(direction == Direction.EAST) {
				direction = Direction.NORTHEAST;
				curPair = new Pair<Integer, Integer>(xIndex, yIndex - 1);
			}
		//SOUTH
		} else if(y > (regionSize/2)) {
			if(direction == Direction.WEST) {
				direction = Direction.SOUTHWEST;
				curPair = new Pair<Integer, Integer>(xIndex, yIndex + 1);
			} else if(direction == Direction.EAST) {
				direction = Direction.SOUTHEAST;
				curPair = new Pair<Integer, Integer>(xIndex + 1, yIndex);
			}
		}
		//generate new pair and add to loaded regions
		if(curPair != null && loadedRegions.get(curPair) == null) {
			Region newRegion = new Region(curPair.left, curPair.right, regionSize, chunkSize);
			//Region newRegion = mapGenerator.generateRegion(curPair.left, curPair.right);
			//TODO: Create function to set chunk sizes and set pixel pos at the same time
			newRegion.setSize(this.tileWidth, this.tileHeight);
			newRegion.setPixelPosFromIndex((Integer) curPair.left, (Integer) curPair.right);
			tmpRegions.put(curPair, newRegion);
		}
	}
}
