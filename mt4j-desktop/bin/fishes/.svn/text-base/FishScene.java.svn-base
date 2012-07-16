package fishes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
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
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import advanced.physics.util.PhysicsHelper;
import java.util.Collections;
import processing.core.*;
//import processing.opengl.PGraphicsOpenGL;



public class FishScene extends AbstractScene{
	

	//For fishes animation
	//CTI se redujo el numero de fishes a 15 
	private static int NUM_BOIDS = 15;
	private int lastBirthTimecheck = 0;                // birth time interval
	private int addKoiCounter = 0;
//MTEllipse
	 MTEllipse ellipse ;
	private ArrayList<Boid> wanderers = new ArrayList<Boid>();     // stores wander behavior objects
	private PVector mouseAvoidTarget;                  // use mouse location as object to evade
	//private boolean press = false;                     // check is mouse is press
	private int mouseAvoidScope = 100;  //120  
	private int mouseArriveScope = 400;//cambiamos de 200 a 400 para que perciba desde mas distancia y siga 
    private int radioobjetivo= 50;
	private String[] skin = new String[10];
	private ArrayList<InputCursor> Cursores;

	//private PImage canvas;
	//private Ripple ripples;
	//private boolean isRipplesActive = false;
	private MTComponent physicsContainer;
	private PImage rocks;
	private PImage vidrio; //Ponermos efecto de vidrrio para el estanque
	//private PImage innerShadow;
	
	//AbstractMTApplication is an instance of PApplet
	public FishScene(AbstractMTApplication  mtapp){
		super(mtapp,"Coward Fishes");
		
		if (!MT4jSettings.getInstance().isOpenGlMode()){
			System.err.println("Scene only usable when using the OpenGL renderer! - See settings.txt");
        	return;
        }
		else
			System.out.println("Using OpenGL renderer");		
	
	
		Cursores=new ArrayList<InputCursor>();
	}

	@Override
	public void init(){
		System.out.println("Starting scene");
		
		//this.size(900, 450, OPENGL);    // publish size
		//this.size(640, 480, OPENGL);      // blog size
	    getMTApplication().smooth();
	    getMTApplication().background(0);
	    getMTApplication().frameRate(25);
	    physicsContainer = new MTComponent(getMTApplication());
        this.rocks = getMTApplication().loadImage("data/rocks.jpg");
        this.vidrio=getMTApplication().loadImage("data/vidrio.png");
       this.rocks.resize(1920,1080);
       this.vidrio.resize(1920,1080);
       //cosas del ellipse
       ellipse = new MTEllipse(mtApplication, new Vector3D(0,0,0), radioobjetivo, radioobjetivo);
 	  ellipse.setNoFill(true);
      ellipse.setStrokeColor(MTColor.FUCHSIA);
      ellipse.setVisible(false);
      this.getCanvas().addChild(ellipse);
  	//this.registerGlobalInputProcessor(new CursorTracer(getMTApplication(), this));
     // getCanvas().registerInputProcessor(new CursorTracer(getMTApplication(), this));
  
		//Scale the physics container. Physics calculations work best when the dimensions are small (about 0.1 - 10 units)
		//So we make the display of the container bigger and add in turn make our physics object smaller
	//	physicsContainer.scale(scale, scale, 1, Vector3D.ZERO_VECTOR);
		//this.getCanvas().addChild(physicsContainer);
		//this.innerShadow = getMTApplication().loadImage("data/pond.png");
			  
		// load and init skin array images
	    for (int n = 0; n < 10; n++) 
		    skin[n] = "data/skin-" + n + ".png";
	    	//skin[n] = "data/skin-0.png";

	     // this is the ripples code
		//canvas = getMTApplication().createImage(getMTApplication().width, getMTApplication().height, PApplet.RGB);
		//ripples = new Ripple(canvas,getMTApplication().width, getMTApplication().height,getMTApplication());
	    /*ellipse.unregisterAllInputProcessors();
	    ellipse.removeAllGestureEventListeners(DragProcessor.class);
	    ellipse.registerInputProcessor(new DragProcessor(getMTApplication()));
	    
	    ellipse.addGestureListener(DragProcessor.class, new IGestureEventListener(){

	    	 
			public boolean processGestureEvent(MTGestureEvent ge) {
				System.out.println("Started sdfgsdf");
				DragEvent de = (DragEvent)ge;
				try{
					Vector3D hacia =de.getTo();
					 
					switch (de.getId()) {
					case DragEvent.GESTURE_STARTED:
						
						pursue((int)hacia.x,(int)hacia.y,50,50);
						 System.out.println("Started");
						break;
					case DragEvent.GESTURE_UPDATED:
						
						pursue((int)hacia.x,(int)hacia.y,50,50);
						System.out.println("Updated");
						break;
					case DragEvent.GESTURE_ENDED:
						pursue((int)hacia.x,(int)hacia.y,50,50);
						System.out.println("Ended");
						break;
						
					default:
						System.out.println("nada");
						break;
					}
				}catch (Exception e) {
					
					System.err.println(e.getMessage());
				}
				return false;
			}
	    	
	    	
	    	
	    	
	    });*/
		
	    getCanvas().addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) {
					AbstractCursorInputEvt ce = (AbstractCursorInputEvt) inEvt;
					
					
					//pursue((int)ce.getX(),(int)ce.getY(),50,50); //Radio de tamanio 25 
					switch(ce.getId()){
					  case AbstractCursorInputEvt.INPUT_STARTED:
						if(ce.getCursor()==null) System.out.println("cursor es null");
						  
						 Cursores.add(ce.getCursor());
						 
						 //Buscar el cursor adecuado
						 //pursue();
						  //System.out.println("Started");
						  break;
					  case AbstractCursorInputEvt.INPUT_UPDATED:
						  
						
					  pursue();
						 
						  //System.out.println("Updated");
						  break;
					  case AbstractCursorInputEvt.INPUT_ENDED:
						
						 // pursue();
						  Cursores.remove(ce.getCursor());
						  QuitarObjetivo(ce.getCursor());
						  //System.out.println("Ended");
						  break;
					  default:
						  break;
				
					}
					
					
					
				    //System.out.println("X:"+ce.getX()+" Y:"+ce.getY());
				    //evade((int)ce.getX(),(int)ce.getY());
				    //if (isRipplesActive == true) ripples.makeTurbulence((int)ce.getX(),(int)ce.getY());
					//evade((int)ce.getX(),(int)ce.getY());
				}
				return false;
			}
		});
		
		
	}
    
	
	private void addKois(){
		//adds new koi on a interval of time
		if (addKoiCounter <  NUM_BOIDS) 
			if (getMTApplication().millis() > lastBirthTimecheck + 500) {
			    lastBirthTimecheck = getMTApplication().millis();
			    if (addKoiCounter <  NUM_BOIDS) 
				   	addKoi(); //Add Koi fish
			}
	}
	
	void pursue(){
		
	        for (int n = 0; n < wanderers.size(); n++) {
		    Boid wanderBoid = wanderers.get(n);
		  // ArrayList<InputCursor> distcur=new ArrayList<InputCursor>();
		  // ArrayList <Float> distancias =new ArrayList<Float>();
		    InputCursor mincur;
		    if(wanderBoid.objetivo!=null){
		    	
		    	
		 mincur=wanderBoid.objetivo;
		    	
		    }else{
		    
		   Iterator<InputCursor> curite=Cursores.iterator();
		   TreeMap<Float,InputCursor> arbol= new TreeMap<Float, InputCursor>();
		   while(curite.hasNext()){
			   InputCursor curtemp= curite.next();
			   float postempx=curtemp.getCurrentEvtPosX();
			   float postempy=curtemp.getCurrentEvtPosY();
			   float distanciaactual=PApplet.dist(postempx, postempy, wanderBoid.location.x, wanderBoid.location.y);
			  
			   if (distanciaactual<=this.mouseArriveScope){
				 //  distancias.add(distanciaactual);
				   //distcur.add(curtemp);
				   arbol.put(distanciaactual,curtemp);
			   }
			   
		   }
		   if(arbol.size()!=0){
		Map.Entry<Float,InputCursor> resultado=arbol.firstEntry();
			   
		
		//float mindist=Collections.min(distancias);
		  // int index=distancias.indexOf(mindist);
		   
		//InputCursor mincur=(InputCursor)distcur.get(index);
		 mincur=resultado.getValue();
		  wanderBoid.objetivo=mincur;
		   }else{
			   continue;
			   
		   }
		    }
		   //encontrar al mas cercano
		    
		    
		    
		    
		    
		  //  if (PApplet.dist(mincur.getCurrentEvtPosX(), mincur.getCurrentEvtPosY(), wanderBoid.location.x, wanderBoid.location.y) <= this.mouseArriveScope) {
		        wanderBoid.timeCount = 0;
		        //aleatorio dentyro del area
		        //se intento hacer que los peces no se junten usando un random de un circulo pero con esto se mivian en todas direcciones     
		        // int indice =(int) getMTApplication().random(tamaniocontorno);
		        //System.out.println("indice"+indice);
		       // Vertex resultante=contorno[contador];
		     
		        mouseAvoidTarget = new PVector(mincur.getCurrentEvtPosX(),mincur.getCurrentEvtPosY());
		         
		        //aumentamos en step 
		        //contador=contador+step;
		        wanderBoid.pursue(mouseAvoidTarget);  //Evade
		        wanderBoid.run2();
		   // }
		    
		   
		   		    
		 }
	}
	void QuitarObjetivo(InputCursor obj){
		  for (int n = 0; n < wanderers.size(); n++) {
			  Boid wanderBoid = wanderers.get(n);
			  if(wanderBoid.objetivo!=null){
				  
				  if(wanderBoid.objetivo.equals(obj)){
					  
					  wanderBoid.objetivo=null;
				  }
			  }
		  
		  
		  }
		
	}
	
	
	
	//Evade behaviour
	void evade(int mouseX, int mouseY){
		for (int n = 0; n < wanderers.size(); n++) {
		    Boid wanderBoid = wanderers.get(n);
		    if (PApplet.dist(mouseX, mouseY, wanderBoid.location.x, wanderBoid.location.y) <= mouseAvoidScope) {
		        wanderBoid.timeCount = 0;
		        mouseAvoidTarget = new PVector(mouseX, mouseY);
		        wanderBoid.evade(mouseAvoidTarget);  //Evade
		    }		    
		 }
	}
	
	// Add wander behavior to the fishes
	private void addWanderBehavior(){
		for (int n = 0; n < wanderers.size(); n++) {
			Boid wanderBoid = wanderers.get(n);
		    wanderBoid.wander();
			wanderBoid.run();
		}	
	}
	
	@Override
	//Calling each time, similar to draw() in Processing
	public void drawAndUpdate(PGraphics graphics, long timeDelta) {
		super.drawAndUpdate(graphics, timeDelta);
		getMTApplication().background(0);
		getMTApplication().image(this.rocks, 0, 0);
		
		//Add Kois to Scene
		addKois();
		//Add motion wander behavior
		addWanderBehavior();
		getMTApplication().image(this.vidrio, 0, 0);
		 //ellipse = new MTEllipse(mtApplication, new Vector3D(mouseX,mouseY,0), radiox, radioy);
		//getCanvas().addChild(ellipse);
		// ripples code
		/*if (isRipplesActive == true) {
		    refreshCanvas();
		    ripples.update();
		}*/
		  
		//getMTApplication().image(innerShadow, 0, 0);
		
	}
	
	
	// increments number of koi by 1
	void addKoi() {
		  int id = (int)(getMTApplication().random(1, 11)) - 1; 
		  
		  wanderers.add(new Boid(skin[id],
		               new PVector(getMTApplication().random(100, getMTApplication().width - 100), 
		             		       getMTApplication().random(100f, getMTApplication().height - 100)),
		               getMTApplication().random(0.8f, 1.0f), //max speed .random(0.8f, 1.2f)
		               0.2f, //0.2 max force
		               getMTApplication()));
		  Boid wanderBoid = (Boid)wanderers.get(addKoiCounter);
		  // sets opacity to simulate depth
		  //cambiamos la opacidad inicial a 50
		  wanderBoid.maxOpacity = (int)(PApplet.map(addKoiCounter, 0, NUM_BOIDS - 1, 200, 230));
	
		  addKoiCounter++;	  
	}
	
	
	//Use for the ripple effect to refresh the canvas
	//void refreshCanvas() {
	//  getMTApplication().loadPixels();
	//  System.arraycopy(getMTApplication().pixels, 0, canvas.pixels, 0, getMTApplication().pixels.length);
	//  getMTApplication().updatePixels();
	//}

	
	@Override
	public void shutDown(){
		
	}
	
	
	
	

}

/*


import processing.opengl.PGraphicsOpenGL;

int NUM_BOIDS = 60;
int lastBirthTimecheck = 0;                // birth time interval
int addKoiCounter = 0;

ArrayList wanderers = new ArrayList();     // stores wander behavior objects
PVector mouseAvoidTarget;                  // use mouse location as object to evade
boolean press = false;                     // check is mouse is press
int mouseAvoidScope = 100;    

String[] skin = new String[10];

PImage canvas;
Ripple ripples;
boolean isRipplesActive = false;

PImage rocks;
PImage innerShadow;

void setup() {
  size(900, 450, OPENGL);    // publish size
  //size(640, 480, OPENGL);      // blog size
  
  smooth();
  background(0);
  //frameRate(30);
  
  rocks = loadImage("rocks.jpg");
  innerShadow = loadImage("pond.png");
  
  // init skin array images
  for (int n = 0; n < 10; n++) skin[n] = "skin-" + n + ".png";

  // this is the ripples code
  canvas = createImage(width, height, RGB);
  //ripples = new Ripple(canvas);
}


void draw() {
  background(0);
  
  image(rocks, 0, 0);
  
  // adds new koi on a interval of time
  if (millis() > lastBirthTimecheck + 500) {
    lastBirthTimecheck = millis();
    if (addKoiCounter <  NUM_BOIDS) addKoi();
  }

  // fish motion wander behavior
  for (int n = 0; n < wanderers.size(); n++) {
    Boid wanderBoid = (Boid)wanderers.get(n);

    // if mouse is press pick objects inside the mouseAvoidScope
    // and convert them in evaders
    if (press) {
      if (dist(mouseX, mouseY, wanderBoid.location.x, wanderBoid.location.y) <= mouseAvoidScope) {
        wanderBoid.timeCount = 0;
        wanderBoid.evade(mouseAvoidTarget);
      }
    }
    else {
      wanderBoid.wander();
    }
    wanderBoid.run();
  }


  // ripples code
  if (isRipplesActive == true) {
    refreshCanvas();
    ripples.update();
  }
  
  image(innerShadow, 0, 0);
  
  //println("fps: " + frameRate);
}


// increments number of koi by 1
void addKoi() {
  int id = int(random(1, 11)) - 1;
  wanderers.add(new Boid(skin[id],
  new PVector(random(100, width - 100), random(100, height - 100)),
  random(0.8, 1.9), 0.2));
  Boid wanderBoid = (Boid)wanderers.get(addKoiCounter);
  // sets opacity to simulate deepth
  wanderBoid.maxOpacity = int(map(addKoiCounter, 0, NUM_BOIDS - 1, 10, 170));

  addKoiCounter++;
}


// use for the ripple effect to refresh the canvas
void refreshCanvas() {
  loadPixels();
  System.arraycopy(pixels, 0, canvas.pixels, 0, pixels.length);
  updatePixels();
}



void mousePressed() {
  press = true;
  mouseAvoidTarget = new PVector(mouseX, mouseY);

  if (isRipplesActive == true) ripples.makeTurbulence(mouseX, mouseY);
}

void mouseDragged() {
  mouseAvoidTarget.x = mouseX;
  mouseAvoidTarget.y = mouseY;

  if (isRipplesActive == true) ripples.makeTurbulence(mouseX, mouseY);
}

void mouseReleased() {
  press = false;
}


 */
