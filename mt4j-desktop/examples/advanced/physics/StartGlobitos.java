package advanced.physics;

import org.mt4j.MTApplication;

import advanced.physics.scenes.GlobitosScene;

public class StartGlobitos extends MTApplication {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		initialize();
	}
	
	@Override
	public void startUp() {
		addScene(new GlobitosScene(this, "Physics Example Scene", true));
	}

}
