package player;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import properties.SystemProperties;

public class ControlListener extends KeyAdapter{
	
	SystemProperties systemProperties;
	Player player;
	Camera camera;
	
	//initialize with player or camera, we don't want both player and camera to be true
	public ControlListener(Player player, Camera camera, SystemProperties systemProperties) {
		this.camera = camera;
		this.player = player;
		this.systemProperties = systemProperties;
	}
	
	// do while key is released
    public void keyReleased(KeyEvent e) {
    	if(systemProperties.PLAYER_FOCUS) { player.keyReleased(e); }
    	else { camera.keyReleased(e); }
    }

    // do while key is pressed down
    public void keyPressed(KeyEvent e) {
    	if(systemProperties.PLAYER_FOCUS) { player.keyPressed(e); }
    	else { camera.keyPressed(e); }
    }
	
}
