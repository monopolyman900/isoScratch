package mapGenerator;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.Pair;

import utilities.ImageManipulator;
import map.Region;
import map.Tile;
import mapGenerator.Noise;

public class MapGenerator {
	
	ImageManipulator im = new ImageManipulator();

	//all of these variables are from the RegionManager
	HashMap<Pair<Integer, Integer>, Region> regions;
	//size of region x and y axes in chunks (ex 20 -> 20x20 regions)
	int regionSize;
	//number of regions in map
	int regionCountX;
	int regionCountY;
	//size of chunk x and y axes in tiles (ex 20 -> 20x20 chunks)
	int chunkSize;
	
	//total number of x/y tiles in a region
	int totalTilesX;
	int totalTilesY;
	
	//round each tile's height to a multiple of this
	int heightRounder;
	
	//multiply each tile's height value by this - to increase or decrease height differences
	int heightMultiplier;
	
	//minimum ocean height
	int oceanHeight;
	
	//map's seed
	int seed;
	Noise noise;
	
	//current tile image
	BufferedImage tileImg = null;
	
	//initialize and generate map into existing chunks
	public MapGenerator(HashMap<Pair<Integer, Integer>, Region> regions, int regionSize, int regionCountX, int regionCountY, int chunkSize, int heightRounder, int oceanHeight, int heightMultiplier, int seed) {
		this.regions = regions;
		this.regionSize = regionSize;
		this.regionCountX = regionCountX;
		this.regionCountY = regionCountY;
		this.chunkSize = chunkSize;
		this.heightRounder = heightRounder;
		this.oceanHeight = oceanHeight;
		this.heightMultiplier = heightMultiplier;
		this.seed = seed;
		generateMap();
	}
	
	//generates map from hollow chunks - meaning chunk and tile structure defined but no attributes/images are created
	public void generateMap() {
		//total x size is number of chunks on x axis * number of tiles in x axis of chunk
		totalTilesX = regionCountX * regionSize * chunkSize;
		//total y size is number of chunks on y axis * number of tiles in y axis of chunk
		totalTilesY = regionCountY * regionSize * chunkSize;
		
		Random random = new Random();
		//seed should be 0 for new maps
		if(seed == 0) {
			seed = random.nextInt();
		}
		// Create a seeded noise generator
		noise = new Noise(seed);
		
		// Prepare two arrays to fill with tile image values
		int[][] tilesArr = new int[totalTilesX][totalTilesY];
		
		for(int x = 0; x < tilesArr.length; x++) {
			for(int y = 0; y < tilesArr[0].length; y++) {
				// Generate a noise value for the coordinate (x,y)
				float noiseValue = 0;
				noiseValue += scale256(noise.interpolatedNoise(x * 0.01f, y * 0.01f));
				noiseValue += scale256(noise.interpolatedNoise(x * 0.01f, y * 0.01f));
				noiseValue += scale256(noise.interpolatedNoise(x * 0.02f, y * 0.02f));
				noiseValue += scale256(noise.interpolatedNoise(x * 0.04f, y * 0.04f));
				int roundedValue = Math.round(noiseValue / 4f);
				
				//get current region/chunk based of x/y values of array position
				int curRegionX = (int) Math.floor(x/(regionSize*chunkSize));
				int curRegionY = (int) Math.floor(y/(regionSize*chunkSize));
				int curChunkX = (int) Math.floor((x % (regionSize * chunkSize)) / chunkSize);
				int curChunkY = (int) Math.floor((y % (regionSize * chunkSize)) / chunkSize);
				int curTileX = x % chunkSize;
				int curTileY = y % chunkSize;
				
				Pair<Integer, Integer> curIndex = new Pair<Integer, Integer>(curRegionX, curRegionY);
				Tile curTile = regions.get(curIndex).chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY);
				//debugging values
				int doubleRoundedValue = (int) (Math.rint((double) roundedValue / heightRounder) * heightRounder) - 100;
				regions.get(curIndex).chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY).val = (doubleRoundedValue * heightMultiplier);
				regions.get(curIndex).chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY).absTileX = x;
				regions.get(curIndex).chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY).absTileY = y;
				regions.get(curIndex).chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY).chunkX = curChunkX;
				regions.get(curIndex).chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY).chunkY = curChunkY;
				regions.get(curIndex).chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY).regionX = curIndex.left;
				regions.get(curIndex).chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY).regionY = curIndex.right;
				
				//update chunk val
				if(regions.get(curIndex) != null && regions.get(curIndex).chunks.get(curChunkX).get(curChunkY).val < curTile.val) {
					regions.get(curIndex).chunks.get(curChunkX).get(curChunkY).val = curTile.val;
				}
				//update region val
				if(regions.get(curIndex).val < curTile.val) {
					regions.get(curIndex).val = curTile.val;
				}

				setTileImage(doubleRoundedValue);
				curTile.setImage(this.tileImg);
			}
			int percentComp = (x*100/tilesArr.length);
			System.out.println("Generating: " + percentComp + "%");
		}
	}
	
	//generate region from region position
	@Deprecated
	public Region generateRegion(int xIndex, int yIndex) {
		
		Region newRegion = new Region(xIndex, yIndex, regionSize, chunkSize);
		
		int startingX = xIndex * totalTilesX;
		int startingY = yIndex * totalTilesY;
		
		// Prepare two arrays to fill with tile image values
		int[][] tilesArr = new int[totalTilesX][totalTilesY];
		
		for(int x = 0; x < totalTilesX; x++) {
			for(int y = 0; y < totalTilesY; y++) {
				// Generate a noise value for the coordinate (x,y)
				float noiseValue = 0;
				noiseValue += scale256(noise.interpolatedNoise((x + startingX) * 0.01f, (y + startingY) * 0.01f));
				noiseValue += scale256(noise.interpolatedNoise((x + startingX) * 0.01f, (y + startingY) * 0.01f));
				noiseValue += scale256(noise.interpolatedNoise((x + startingX) * 0.02f, (y + startingY) * 0.02f));
				noiseValue += scale256(noise.interpolatedNoise((x + startingX) * 0.04f, (y + startingY) * 0.04f));
				int roundedValue = Math.round(noiseValue / 4f);
				
				//get current chunk based of x/y values of array position
				int curChunkX = (int) Math.floor((x % (regionSize * chunkSize)) / chunkSize);
				int curChunkY = (int) Math.floor((y % (regionSize * chunkSize)) / chunkSize);
				int curTileX = x % chunkSize;
				int curTileY = y % chunkSize;
				
				Tile curTile = newRegion.chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY);
				//debugging values
				int doubleRoundedValue = (int) (Math.rint((double) roundedValue / heightRounder) * heightRounder) - 100;
				newRegion.chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY).val = doubleRoundedValue;
				newRegion.chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY).absTileX = x + startingX;
				newRegion.chunks.get(curChunkX).get(curChunkY).tiles.get(curTileX).get(curTileY).absTileY = y + startingY;
				
				setTileImage(roundedValue);
				curTile.setImage(this.tileImg);
			}
			int percentComp = (x*100/tilesArr.length);
			System.out.println("Generating New Region: " + percentComp + "%");
		}
		newRegion.setLoaded(true);
		return newRegion;
	}
	
	//parameters of Tile Object, indexes: TileX, TileY, ChunkX, ChunkY, RegionX, RegionY
	public void generateTile(Tile tile, int tileX, int tileY, int chunkX, int chunkY, int regionX, int regionY) {
		
		int x = (regionX*(regionSize*chunkSize)) + (chunkX*chunkSize) + tileX;
		int y = (regionY*(regionSize*chunkSize)) + (chunkY*chunkSize) + tileY;
		
		float noiseValue = 0;
		noiseValue += scale256(noise.interpolatedNoise(x * 0.01f, y * 0.01f));
		noiseValue += scale256(noise.interpolatedNoise(x * 0.01f, y * 0.01f));
		noiseValue += scale256(noise.interpolatedNoise(x * 0.02f, y * 0.02f));
		noiseValue += scale256(noise.interpolatedNoise(x * 0.04f, y * 0.04f));
		int roundedValue = Math.round(noiseValue / 4f);
		int doubleRoundedValue = (int) (Math.rint((double) roundedValue / heightRounder) * heightRounder) - 100;
		
		//ocean min height
		if(doubleRoundedValue < oceanHeight) doubleRoundedValue = oceanHeight;
		
		tile.val = (doubleRoundedValue * heightMultiplier);
		//set chunk val
		if(tile.parentChunk.val < tile.val) {
			tile.parentChunk.val = tile.val;
		}
		//set region val
		if(tile.parentChunk.parentRegion.val < tile.val) {
			tile.parentChunk.parentRegion.val = tile.val;
		}
		//debugging values
		tile.absTileX = x;
		tile.absTileY = y;
		tile.chunkX = chunkX;
		tile.chunkY = chunkY;
		tile.regionX = regionX;
		tile.regionY = regionY;
		
		setTileImage(doubleRoundedValue);
		tile.setImage(this.tileImg);
	}
	
	//generates image
	public void setTileImage(int value) {
		
		if(value > 50) {
			// light grey
			this.tileImg = im.loadImage("Images/Tiles/mountainTile3.png");
		} else if(value > 40) {
			// medium grey
			this.tileImg = im.loadImage("Images/Tiles/mountainTile2.png");
		} else if(value > 30) {
			// dark grey
			this.tileImg = im.loadImage("Images/Tiles/mountainTile1.png");
		} else if(value > 20) {
			// light green
			this.tileImg = im.loadImage("Images/Tiles/grassTile.png");
		} else if(value > 10) {
			// sandy yellow
			this.tileImg = im.loadImage("Images/Tiles/sandTile.png");
		} else {
			// ocean blue
			this.tileImg = im.loadImage("Images/Tiles/waterTile.png");
		}
	}
	
	// x is a number in the range min..max
	// converts x to a number in the range [a,b]
	public float scaleRange(float min, float max, float a, float b, float x) {
	    return (b - a) * (x - min) / (max - min) + a;
	}

	// converts x from a number in the range [-1, 1] to a number in the range [0, 255]
	public float scale256(float x) {
	    return scaleRange(-1, 1, 0, 255, x);
	}
	
	//used for systemProperties
	public int getSeed() {
		return seed;
	}
	
}
