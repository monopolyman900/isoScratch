package core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

import map.MapManager;
import player.Player;
import player.ControlListener;

public class GamePanel extends JPanel implements Runnable{
	
	//core classes
	GameRenderer renderer;
	public GameManager gameManager;
	GameLoader loader;
	
	//graphics stuff
	private Graphics dbg;
	private Image dbImage = null;
	
	//panel/screen properties
	private int panelWidth;
	private int panelHeight;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int screenHeight = screenSize.height;
	private int screenWidth = screenSize.width;
	
	//game updating stuff
	Thread animator;
	boolean running;
	int sleepPeriod = 20;
	int period = 100; 

	public GamePanel(int w, int h) {
		this.setSize(w, h);
		this.setVisible(true);
		this.setFocusable(true);
		this.panelWidth = w;
		this.panelHeight = h;

		gameManager = new GameManager();
		renderer = new GameRenderer(gameManager);
		loader = new GameLoader();

		startGame();
	}
	
	private void startGame() {
		this.addKeyListener(new ControlListener(gameManager.player, gameManager.camera, gameManager.systemProperties));
		animator = new Thread(this, "Animator");
		running = true;
		animator.start();
	}
	
	public void saveAndQuit() {
		gameManager.saveAndQuit();
	}

	
	public void run() {
			try {
				while(running) {
					gameAction();
					Thread.sleep(sleepPeriod);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
	}
	
	//everything that needs to be updated continuously
	private void gameAction() {
		updateGame();
		gameRender(); // Draw to the double buffer.
		repaint();
	}
	
	public void updateGame() {
		gameManager.player.updatePlayer(period);
		gameManager.camera.updateCamera();
		gameManager.mapManager.updateRegions();
	}
	
	/**
	 * Draws the game image to the buffer.
	 * Idk what this is actually needed for
	 */
	private void gameRender() {
		if(dbImage == null) {
			dbImage = createImage(this.panelWidth, this.panelHeight);
			return;
		}
	    dbg = dbImage.getGraphics();
		renderer.draw((Graphics2D) dbg, screenWidth, screenHeight);
	}
	
	/**
	 * Draws the game image to the screen by drawing the buffer.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (dbImage != null) {
	        g.drawImage(dbImage, 0, 0, this);
	    }
	}
	
}
