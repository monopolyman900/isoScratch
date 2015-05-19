package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import properties.GameProperties;
import properties.SystemProperties;

public class GameLoader {

	public GameLoader() {
		
	}
	
	public SystemProperties loadSystemProperties() {
		SystemProperties systemProperties = new SystemProperties();
		return systemProperties;
	}
	
	public GameProperties loadGameProperties(SystemProperties systemProperties) {
		//load properties
		File file = new File(systemProperties.PROPERTIES_FULL_PATH);
		//read file
		GameProperties gameProperties = null;
		try{
			if(file != null){
				System.out.println("Loading Map...");
				FileInputStream fis= new FileInputStream(file);
				ObjectInputStream ois= new ObjectInputStream(fis);
				gameProperties = (GameProperties) ois.readObject();
			}
		System.out.println("Loading Successful");
		} catch(Exception e) {
				System.out.println("Loading Failed, creating new map");
				gameProperties = new GameProperties();
		}
		return gameProperties;
	}
	
}
