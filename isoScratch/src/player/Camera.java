package player;

import java.awt.event.KeyEvent;

import core.GameManager;

public class Camera {

	final int SCROLL_SPEED = 20;
	
	public int pixelX;
	public int pixelY;
	//movement variables
	int dx;
	int dy;
	boolean upPressed;
	boolean downPressed;
	boolean leftPressed;
	boolean rightPressed;
	//player to move camera based on player when player is focus
	public Player player;
	private GameManager gameManager;
	
	public Camera(Player player, GameManager gameManager) {
		this.player = player;
		this.gameManager = gameManager;
	}
	
	//used to update camera position, move everything else with offset
	public void updateCamera() {
		//camera is free floating
		if(!gameManager.systemProperties.PLAYER_FOCUS) {
			if(upPressed) {
				dy = -SCROLL_SPEED;
			}
			if(downPressed) {
				dy = SCROLL_SPEED;
			}
			if(leftPressed) {
				dx = -SCROLL_SPEED;
			}
			if(rightPressed) {
				dx = SCROLL_SPEED;
			}
			pixelX += dx;
			pixelY += dy;
		}
		//camera focus is on player
		else {
			this.pixelX = player.pixelX;
			this.pixelY = player.pixelY;
		}
	}
	
	//Key Events - Only called here when camera focus is true in listener class
	public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_UP) {
        	upPressed = true;
        	dy = -SCROLL_SPEED;
        } else if(key == KeyEvent.VK_DOWN) {
        	downPressed = true;
        	dy = SCROLL_SPEED;
        } else if(key == KeyEvent.VK_LEFT) {
        	leftPressed = true;
        	dx = -SCROLL_SPEED;
        } else if(key == KeyEvent.VK_RIGHT) {
        	rightPressed = true;
        	dx = SCROLL_SPEED;
        }
	}
		
	public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_UP) {
        	upPressed = false;
        	dy = 0;
        } else if(key == KeyEvent.VK_DOWN) {
        	downPressed = false;
        	dy = 0;
        } else if(key == KeyEvent.VK_LEFT) {
        	leftPressed = false;
        	dx = 0;
        } else if(key == KeyEvent.VK_RIGHT) {
        	rightPressed = false;
        	dx = 0;
        }
	}
}
