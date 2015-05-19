package player;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import core.GameManager;
import utilities.ImageManipulator;
import animation.Animation;
import animation.CollidableObject;

//Player methods and all in-game controls
public class Player extends CollidableObject{

	ImageManipulator im = new ImageManipulator();
	
	//final/static variables
	private static final int SCROLL_SPEED = 20;
	private static final int ANIM_TIME = 250;
	
	public int pixelX;
	public int pixelY;
	int dx;
	int dy;
	boolean upPressed;
	boolean downPressed;
	boolean leftPressed;
	boolean rightPressed;
	
	/* Animation variables. */
	private BufferedImage[] animImgs;
	private Animation faceUp, faceDown, faceLeft, faceRight, walkUp, walkDown, walkLeft, walkRight;
	
	public Player(int startingX, int startingY, int pixelX, int pixelY) {
		super(startingX, startingY);
		loadAnimations();
		this.pixelX = pixelX;
		this.pixelY = pixelY;
	}

	public void loadAnimations() {
		this.animImgs = new BufferedImage[]{ 
			im.loadImage("Images/Player/player_facing_up.png"), 
			im.loadImage("Images/Player/player_walking_up1.png"), im.loadImage("Images/Player/player_walking_up2.png"),
			im.loadImage("Images/Player/player_facing_down.png"), 
			im.loadImage("Images/Player/player_walking_down1.png"), im.loadImage("Images/Player/player_walking_down2.png"),
			im.loadImage("Images/Player/player_facing_left.png"), 
			im.loadImage("Images/Player/player_walking_left1.png"), im.loadImage("Images/Player/player_walking_left2.png"), im.loadImage("Images/Player/player_walking_left3.png"), im.loadImage("Images/Player/player_walking_left4.png"), 
			im.loadImage("Images/Player/player_facing_right.png"), 
			im.loadImage("Images/Player/player_walking_right1.png"), im.loadImage("Images/Player/player_walking_right2.png"), im.loadImage("Images/Player/player_walking_right3.png"), im.loadImage("Images/Player/player_walking_right4.png")
		};
		
		faceUp = new Animation(ANIM_TIME).addFrame(animImgs[0]);
		faceDown = new Animation(ANIM_TIME).addFrame(animImgs[3]);
		faceLeft = new Animation(ANIM_TIME).addFrame(animImgs[6]);
		faceRight = new Animation(ANIM_TIME).addFrame(animImgs[3]);
		walkUp = new Animation(ANIM_TIME).addFrame(animImgs[11]);
		
		walkUp = new Animation(ANIM_TIME).addFrame(animImgs[1]).addFrame(animImgs[2]);
		walkDown = new Animation(ANIM_TIME).addFrame(animImgs[4]).addFrame(animImgs[5]);
		walkLeft = new Animation(ANIM_TIME).addFrame(animImgs[7]).addFrame(animImgs[8]).addFrame(animImgs[9]).addFrame(animImgs[10]);
		walkRight = new Animation(ANIM_TIME).addFrame(animImgs[11]).addFrame(animImgs[12]).addFrame(animImgs[13]).addFrame(animImgs[14]);
		
		setAnimation(faceDown);
	}
	
	public void updatePlayer(int time) {
		if(upPressed && !downPressed) {
			dy = -SCROLL_SPEED;
			setAnimation(walkUp);
		}
		if(downPressed && !upPressed) {
			dy = SCROLL_SPEED;
			setAnimation(walkDown);
		}
		if(leftPressed && !rightPressed) {
			dx = -SCROLL_SPEED;
			setAnimation(walkLeft);
		}
		if(rightPressed && !leftPressed) {
			dx = SCROLL_SPEED;
			setAnimation(walkRight);
		}
		super.update(time);
		pixelX += dx;
		pixelY += dy;
		//System.out.println("x: " + pixelX + " y: " + pixelY);
	}
	
	//Key Events - Only called here when player focus is true in listener class
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
			setAnimation(faceUp);
        } else if(key == KeyEvent.VK_DOWN) {
        	downPressed = false;
        	dy = 0;
			setAnimation(faceDown);
        } else if(key == KeyEvent.VK_LEFT) {
        	leftPressed = false;
        	dx = 0;
			setAnimation(faceLeft);
        } else if(key == KeyEvent.VK_RIGHT) {
        	rightPressed = false;
        	dx = 0;
			setAnimation(faceRight);
        }
	}
}
