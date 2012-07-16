package advanced.physics.scenes;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.MultipleDragProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PImage;
import advanced.physics.physicsShapes.IPhysicsComponent;
import advanced.physics.physicsShapes.PhysicsCircle;
import advanced.physics.physicsShapes.PhysicsRectangle;
import advanced.physics.util.PhysicsHelper;
import advanced.physics.util.UpdatePhysicsAction;

public class AirHockeyScene extends AbstractScene {
	private float timeStep = 1.0f / 60.0f;
	private int constraintIterations = 10;
	
	/** THE CANVAS SCALE **/
	private float scale = 20;
	private AbstractMTApplication app;
	private World world;
	
	private MTComponent physicsContainer;

	//CTI BG
	//aqui colocamos un hash para manejar distintas entradas

	private HashMap< InputCursor, MTComponent> cursores;

	private HockeyBall ball;
	
	/*
	private Minim minim;
	private AudioSnippet wallHit;
	private AudioSample paddleBallClash;
	private AudioSnippet goalHit;
	private AudioSnippet paddleHit;
	*/
 
	MTRectangle contenedor ;
	
	private boolean enableSound = true;
	
	
	//TODO sometimes goal not recognized when shot at fast
	//TODO after goal reset puck and make 3 second countdown -> dont use physics until countdown up!
	//TODO get graphics, sounds, effects
	
//	private String imagesPath = System.getProperty("user.dir") + File.separator + "examples" + File.separator +"advanced"+ File.separator +  "physics"  + File.separator + "data" +  File.separator  + "images" +  File.separator;
	private String imagesPath =  "advanced" + AbstractMTApplication.separator +  "physics"  + AbstractMTApplication.separator + "data" +  AbstractMTApplication.separator  + "images" +  AbstractMTApplication.separator;
	
	
	public AirHockeyScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
//		this.setClearColor(new MTColor(120,150,150));
//		this.setClearColor(new MTColor(190, 190, 170, 255));
		this.setClearColor(new MTColor(0, 0, 0, 255));
//		this.setClearColor(new MTColor(40, 40, 40, 255));
		this.registerGlobalInputProcessor(new CursorTracer(app, this));
		
	
		
		float worldOffset = 10; //Make Physics world slightly bigger than screen borders
		//Physics world dimensions
		AABB worldAABB = new AABB(new Vec2(-worldOffset, -worldOffset), new Vec2((app.width)/scale + worldOffset, (app.height)/scale + worldOffset));
		Vec2 gravity = new Vec2(0, 0);
		boolean sleep = true;
		//Create the pyhsics world
		this.world = new World(worldAABB, gravity, sleep);
		
		//Update the positions of the components according the the physics simulation each frame
		this.registerPreDrawAction(new UpdatePhysicsAction(world, timeStep, constraintIterations, scale));
		
		physicsContainer = new MTComponent(app);
		//Scale the physics container. Physics calculations work best when the dimensions are small (about 0.1 - 10 units)
		//So we make the display of the container bigger and add in turn make our physics object smaller
		physicsContainer.scale(scale, scale, 1, Vector3D.ZERO_VECTOR);
		this.getCanvas().addChild(physicsContainer);
		
		//Create borders around the screen
		this.createScreenBorders(physicsContainer);
		
		//Create gamefield marks
		MTLine line = new MTLine(mtApplication, mtApplication.width/2f/scale, 0, mtApplication.width/2f/scale, mtApplication.height/scale);
		line.setPickable(false);
//		line.setStrokeColor(new MTColor(0,0,0));
		line.setStrokeColor(new MTColor(150,150,150,0));
		line.setStrokeWeight(0.5f);
		physicsContainer.addChild(line);
		
		MTEllipse centerCircle = new MTEllipse(mtApplication, new Vector3D(mtApplication.width/2f/scale, mtApplication.height/2f/scale), 80/scale, 80/scale);
		centerCircle.setPickable(false);
		centerCircle.setNoFill(true);
//		centerCircle.setStrokeColor(new MTColor(0,0,0));
		centerCircle.setStrokeColor(new MTColor(150,150,150,0));
		centerCircle.setStrokeWeight(0.5f);
		physicsContainer.addChild(centerCircle);
		
		MTEllipse centerCircleInner = new MTEllipse(mtApplication, new Vector3D(mtApplication.width/2f/scale, mtApplication.height/2f/scale), 10/scale, 10/scale);
		centerCircleInner.setPickable(false);
		centerCircleInner.setFillColor(new MTColor(160,160,160,0));
//		centerCircleInner.setStrokeColor(new MTColor(150,150,150));
//		centerCircleInner.setStrokeColor(new MTColor(0,0,0));
		centerCircleInner.setStrokeColor(new MTColor(150,150,150,0));
		centerCircleInner.setStrokeWeight(0.5f);
		physicsContainer.addChild(centerCircleInner);
		
		
		//CTI BG
		//aqui instanciamos el hash conla informacion de cada bola con cada cursor 
		this.cursores = new HashMap<InputCursor, MTComponent>();
		

		//CTI BG
		//Creamos las bolas con los nombres como texturas 
		//
		 for (int i=1;i<13;i++){
		//ball = new HockeyBall(app, new Vector3D(mtApplication.width/2f, mtApplication.height/2f), 38, world, 0.5f, 0.005f, 0.70f, scale);
			 ball = new HockeyBall(app, new Vector3D(mtApplication.width/2f, mtApplication.height/2f), 60, world, 0.3f, 0.7f, 0.70f, scale);
		PImage ballTex = mtApplication.loadImage(imagesPath + "image"+i+".png");
		ball.setTexture(ballTex);
		//ball.setFillColor(new MTColor(255,255,255,0));
		//MTColor ballCol =  new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255));
		//ball.setFillColor(ballCol);
		//ball.setNoFill(true);
		ball.setNoStroke(true);
		ball.setName("ball");
		physicsContainer.addChild(ball);
		ball.getBody().applyImpulse(new Vec2(ToolsMath.getRandom(-8f, 8),ToolsMath.getRandom(-8, 8)), ball.getBody().getWorldCenter());
		
	
	
	}

		
		
		//Make two components for both game field sides to drag the puks upon
		 contenedor = new MTRectangle(
				app, PhysicsHelper.scaleDown(0, scale), 
				PhysicsHelper.scaleDown(0, scale), PhysicsHelper.scaleDown(app.width, scale)
				, PhysicsHelper.scaleDown(app.height, scale));
		contenedor.setName("left side");
		contenedor.setNoFill(true); //Make it invisible -> only used for dragging
		contenedor.setNoStroke(true);
		contenedor.unregisterAllInputProcessors();
		contenedor.removeAllGestureEventListeners(MultipleDragProcessor.class);
		contenedor.registerInputProcessor(new MultipleDragProcessor(app));
		
		contenedor.addGestureListener(MultipleDragProcessor.class, new GameFieldHalfDragListener());
		
		physicsContainer.addChild(contenedor);
		
		
		//Set up check for collisions between objects
		this.addWorldContactListener(world);

		/*
		//Sound
		if (enableSound){
			minim = new Minim(mtApplication);
			wallHit = minim.loadSnippet(MT4jSettings.getInstance().getDataFolderPath() + "sound" + File.separator + "paddleBallHit.wav");
//			paddleBallClash = minim.loadSample(MT4jSettings.getInstance().getDataFolderPath() + "sound" + File.separator + "paddleBallHit.wav", 2048);
//			goalHit = minim.loadSnippet(MT4jSettings.getInstance().getDataFolderPath() + "sound" + File.separator + "goal.wav");
//			goalHit.play();
			paddleHit = minim.loadSnippet(MT4jSettings.getInstance().getDataFolderPath() + "sound" + File.separator + "wallHit.wav");
		}
		*/
	}
	
	
	
	public class GameFieldHalfDragListener implements IGestureEventListener{
		
		PImage paddleTex;
		public GameFieldHalfDragListener(){
			 paddleTex = mtApplication.loadImage(imagesPath + "paddle.png");
				
			//if (comp.getUserData("box2d") == null){
				//throw new RuntimeException("GameFieldHalfDragListener has to be given a physics object!");
		//	}
		}
		
		public boolean processGestureEvent(MTGestureEvent ge) {
			DragEvent de = (DragEvent)ge;
	
	       
			//PImage paddleTex = mtApplication.loadImage(imagesPath + "paddle.png");
	
			Body	 body = null;
			
			
			
			
			try{
				
				MouseJoint mouseJoint;
				Vector3D to = new Vector3D(de.getTo());
				//Un-scale position from mt4j to box2d
				PhysicsHelper.scaleDown(to, scale);
				switch (de.getId()) {
				case DragEvent.GESTURE_STARTED:
					
					
				
				Paddle	comp = new Paddle(app, de.getTo(), 20, world, 1.0f, 0.3f, 0.4f, scale);
				//comp2=comp;	
				// CTI BG hacemos transparente el paddle
				comp.setTexture(paddleTex);
					comp.setFillColor(new MTColor(50,255,50,0));
					comp.setNoStroke(true);
					comp.setName("red");
					comp.setPickable(false);
					// CTI BG
					//Asoviamos solo un circulo con cada dragcursor 
				    cursores.put(de.getDragCursor(), comp);
					//this.comp = greenCircle;
					physicsContainer.addChild(comp);
				//body=	(Body)comp.getUserData("box2d");
					body=comp.getBody();
					// Hcaemos que este componente se vaya al ulitma de la lista de hijos de su padre para que se dinuje primero 
					comp.sendToFront();
					//Sale de su estado de sleep
					body.wakeUp();
					body.setXForm(new Vec2(to.x,  to.y), body.getAngle());
					mouseJoint = PhysicsHelper.createDragJoint(world, body, to.x, to.y);
					
					comp.setMJ(mouseJoint);
					//comp.setUserData(comp.getID(), mouseJoint);
					break;
				case DragEvent.GESTURE_UPDATED:
					
					comp=(Paddle)cursores.get(de.getDragCursor());
					mouseJoint=comp.getMJ();
					//mouseJoint = (MouseJoint) comp.getUserData(comp.getID());
					if (mouseJoint != null){
						
							mouseJoint.setTarget(new Vec2(to.x, to.y));	
				
					}
			
					break;
				case DragEvent.GESTURE_ENDED:
					comp=(Paddle)cursores.get(de.getDragCursor());
					mouseJoint=comp.getMJ();
					//mouseJoint = (MouseJoint) comp.getUserData(comp.getID());
					
					if (mouseJoint != null){
						comp.setMJ(null);
						//comp.setUserData(comp.getID(), null);
					
						physicsContainer.removeChild(comp);
						
						//temp.destroy();
						cursores.remove(de.getDragCursor());
						comp.destroy();
						//System.out.println("tamanio del hash "+cursorToLastDrawnPoint.size());
						//Only destroy the joint if it isnt already (go through joint list and check)
						/*for (Joint joint = world.getJointList(); joint != null; joint = joint.getNext()) {
							JointType type = joint.getType();
							switch (type) {
							case MOUSE_JOINT:
								MouseJoint mj = (MouseJoint)joint;
								if (body.equals(mj.getBody1()) || body.equals(mj.getBody2())){
									if (mj.equals(mouseJoint)) {
										world.destroyJoint(mj);
									}
								}
								break;
							default:
								break;
							}
						}*/
					}
					mouseJoint = null;
					break;
				default:
					break;
				}
			}catch (Exception e) {
				
				System.err.println(e.getMessage());
			}
			return false;
		}
	}
	
	
	private class Paddle extends PhysicsCircle{
		
		MouseJoint mj;
		public Paddle(PApplet applet, Vector3D centerPoint, float radius,
				World world, float density, float friction, float restitution, float worldScale) {
			super(applet, centerPoint, radius, world, density, friction, restitution, worldScale);
		} 
		@Override
		protected void bodyDefB4CreationCallback(BodyDef def) {
			super.bodyDefB4CreationCallback(def);
			def.fixedRotation = true;
			def.linearDamping = 0.5f;
		}
		public MouseJoint getMJ(){
			return this.mj;
			
			
		}
		public void setMJ(MouseJoint m){
			 this.mj=m;
			
			
		}
	}
	
	private class HockeyBall extends PhysicsCircle{
		public HockeyBall(PApplet applet, Vector3D centerPoint, float radius,
				World world, float density, float friction, float restitution, float worldScale) {
			super(applet, centerPoint, radius, world, density, friction, restitution, worldScale);
		} 
		
		@Override
		protected void circleDefB4CreationCallback(CircleDef def) {
			super.circleDefB4CreationCallback(def);
			def.radius = def.radius -5/scale;
		}
		@Override
		protected void bodyDefB4CreationCallback(BodyDef def) {
			super.bodyDefB4CreationCallback(def);
//			def.linearDamping = 0.15f;
			def.linearDamping = 0.25f;
			def.isBullet = true;
			def.angularDamping = 0.9f;
			
//			def.fixedRotation = true;
		}
	}
	
	
	
	private void addWorldContactListener(World world){
		world.setContactListener(new ContactListener() {
			public void result(ContactResult point) {
//				System.out.println("Result contact");
			}
			//@Override
			public void remove(ContactPoint point) {
//				System.out.println("remove contact");
			}
			//@Override
			public void persist(ContactPoint point) {
//				System.out.println("persist contact");
			}
			//@Override
			public void add(ContactPoint point) {
//				/*
				Shape shape1 = point.shape1;
				Shape shape2 = point.shape2;
				final Body body1 = shape1.getBody();
				final Body body2 = shape2.getBody();
				Object userData1 = body1.getUserData();
				Object userData2 = body2.getUserData();
				
		
//				*/
			}
		});
	}
	
	/*
	private void triggerSound(AudioSnippet snippet){
		if (!snippet.isPlaying()){
			snippet.pause();
			snippet.rewind();
			snippet.play();
		}
	}
	*/
	
	private MTComponent isHit(String componentName, MTComponent comp1, MTComponent comp2){
		MTComponent hitComp = null;
		if (comp1.getName() != null && comp1.getName().equalsIgnoreCase(componentName)){
			hitComp = comp1;
		}else if (comp2.getName() != null && comp2.getName().equalsIgnoreCase(componentName)){
			hitComp = comp2;
		}
		return hitComp;
	}
	

	

	
	
	private void createScreenBorders(MTComponent parent){
		//Left border 
		float borderWidth = 50f;
		float borderHeight = app.height;
		Vector3D pos = new Vector3D(-(borderWidth/2f) , app.height/2f);
		PhysicsRectangle borderLeft = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderLeft.setName("borderLeft");
		parent.addChild(borderLeft);
		//Right border
		pos = new Vector3D(app.width + (borderWidth/2), app.height/2);
		PhysicsRectangle borderRight = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderRight.setName("borderRight");
		parent.addChild(borderRight);
		//Top border
		borderWidth = app.width;
		borderHeight = 50f;
		pos = new Vector3D(app.width/2, -(borderHeight/2));
		PhysicsRectangle borderTop = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderTop.setName("borderTop");
		parent.addChild(borderTop);
		//Bottom border
		pos = new Vector3D(app.width/2 , app.height + (borderHeight/2));
		PhysicsRectangle borderBottom = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderBottom.setName("borderBottom");
		parent.addChild(borderBottom);
	}
	

	public void onEnter() {
		getMTApplication().registerKeyEvent(this);
	}
	
	public void onLeave() {	
		getMTApplication().unregisterKeyEvent(this);
	}
	
	public void keyEvent(KeyEvent e){
		int evtID = e.getID();
		if (evtID != KeyEvent.KEY_PRESSED)
			return;
		switch (e.getKeyCode()){
		case KeyEvent.VK_SPACE:
			//this.reset();
			break;
			default:
				break;
		}
	}

}
