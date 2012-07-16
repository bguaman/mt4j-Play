package fishes;

import org.mt4j.MTApplication;
import org.mt4j.sceneManagement.AbstractScene;

import scenes.WaterSceneExportObf;

public class StartFishes extends MTApplication{
 
	private static final long serialVersionUID = 1L;
 
	public static void main(String[] args){
        initialize();
    }
 
	@Override
	public void startUp(){
		AbstractScene fishes=new FishScene(this);
		fishes.setName("peces");
		addScene(fishes);
		
		
    }
}
