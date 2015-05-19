package core;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;

public class GameFrame extends JFrame{
	
	GamePanel panel;
	
	int w = 1300;
	int h = 740;
	
	public GameFrame() {
		
		this.setSize(w, h);
		setTitle("Frame");
		panel = new GamePanel(w, h);
		this.add(panel);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//save on close
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				saveAndQuit();
			}
		});
	}
	
	public static void main(String[] args) {
		GameFrame gameFrame = new GameFrame();
	}
	
	public void saveAndQuit() {
		panel.saveAndQuit();
	}
}
