package properties;

import java.io.File;

/**
 * 
 * used to store global game properties - what kind of focus, base save/load paths, etc
 * properties that aren't specific to the game
 *
 */
public class SystemProperties {
	
	//currently only shows abs value of tile position
	public final boolean DEBUG_MAP = false;
	
	//deletes map and regenerates every time the program is run if true
	public final boolean REGEN_MAP = true;

	//player focus vs free floating camera
	public final boolean PLAYER_FOCUS = false;
	
	//player attributes
	//absolute screen position in pixels of the player - may want to make this more variable based on screen/panel size?
	public final int PLAYER_ABS_X = 653;
	public final int PLAYER_ABS_Y = 320;
	
	//saving/loading properties
	public final String GAME_DIRECTORY_STRING = "saved_games";
	//only loading/saving 1 game right now
	public final String PROPERTIES_FILE_STRING = "game1";
	public final String PROPERTIES_FULL_PATH = GAME_DIRECTORY_STRING + "\\" + PROPERTIES_FILE_STRING;
	public final File GAME_DIRECTORY;
	
	public SystemProperties() {
		GAME_DIRECTORY = new File(GAME_DIRECTORY_STRING);
		if(!(GAME_DIRECTORY.exists() && GAME_DIRECTORY.isDirectory())) {
			GAME_DIRECTORY.mkdirs();
		}
	}
}
