package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import player.Camera;
import player.Player;
import properties.GameProperties;
import properties.SystemProperties;
import map.MapManager;

//manages all game objects
public class GameManager {

	//properties
	public SystemProperties systemProperties;
	public GameProperties gameProperties; 
	public GameLoader gameLoader = new GameLoader();
	
	//game objects
	public Player player;
	public Camera camera;
	public MapManager mapManager;
	
	public GameManager() {
		
		loadGame();
		
		//initialize major game objects
		player = new Player(systemProperties.PLAYER_ABS_X, systemProperties.PLAYER_ABS_Y, gameProperties.playerPosX, gameProperties.playerPosY);
		camera = new Camera(player, this);
		mapManager = new MapManager(camera, gameProperties);
		gameProperties.setSeed(mapManager.getSeed());
	}
	
	public void loadGame() {
		//initialize properties
		systemProperties = gameLoader.loadSystemProperties();
		gameProperties = gameLoader.loadGameProperties(this.systemProperties);
	}
	
	public void saveAndQuit() {
		//message
    	System.out.println("Saving...");
		try {
			//create new map every time the game is loaded
			if(systemProperties.REGEN_MAP) {
				File mapFile = new File(systemProperties.PROPERTIES_FULL_PATH);
				if(mapFile.exists()) {
					mapFile.delete();
				}
			} else {
				//update properties
				gameProperties.setPlayerPos(player.pixelX, player.pixelY);
				//save file
	    		FileOutputStream fos = new FileOutputStream(systemProperties.PROPERTIES_FULL_PATH);
	            ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(gameProperties);
				oos.close();
				System.out.println("Save Successful");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Save Failed");
		}
	}
}
