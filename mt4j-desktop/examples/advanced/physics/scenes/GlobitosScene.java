package advanced.physics.scenes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.mt4j.AbstractMTApplication;
import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.gestureAction.TapAndHoldVisualizer;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.MultipleDragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;



import processing.core.PApplet;
import processing.core.PImage;
import sun.security.krb5.internal.APRep;

import java.lang.*;

import javax.media.opengl.GL;

import advanced.mtShell.ICreateScene;
import advanced.physics.StartGlobitos;
import advanced.physics.physicsShapes.PhysicsCircle;
import advanced.physics.physicsShapes.PhysicsPolygon;
import advanced.physics.physicsShapes.PhysicsRectangle;
import advanced.physics.scenes.AirHockeyScene.GameFieldHalfDragListener;
import advanced.physics.util.PhysicsHelper;
import advanced.physics.util.UpdatePhysicsAction;
import advanced.touchTail.TouchTailScene;

public class GlobitosScene extends AbstractScene {
	public static final int WIDTH_TIME_DISPLAY = 150;
	public static final int WIDTH_SCORE_PLAYER = 300;
	private float timeStep = 1.0f / 60.0f;
	private int constraintIterations = 10;
	private  MTTextArea t1,t2,t3,t4,tinfo,t5;
	/** THE CANVAS SCALE **/
	private float scale = 20;
	private AbstractMTApplication app;
	MTRectangle  replay ;
	MTRectangle fondo;
	MTComponent uiLayer;
	private World world;
	IFont font,font2,font3,font4,fontscoreafected;
	PImage  blue, green , purple , fondoimg , time , player1 , player2 , nube , explotadoaz , explotadovd , explotadomr , botonreplay;
	Iscene escenacurrent=this;
	boolean gameover = false;
	
	//CTI - BG 
	//aniadimos una variable Ifont

	private MTComponent physicsContainer;
	private String imagesPath =  "advanced" + AbstractMTApplication.separator +  "physics"  + AbstractMTApplication.separator + "data" +  AbstractMTApplication.separator  + "images" +  AbstractMTApplication.separator;
	
	
	public GlobitosScene(AbstractMTApplication mtApplication, String name, boolean first) {
		super(mtApplication, name);
		this.app = mtApplication;
		if(first) this.app.width=mtApplication.width-150;
		//this.app.setBounds(0, 0, mtApplication.width-100, mtApplication.height);
		//this.app.setBounds(x, y, width, height)
		//CTI - BG
		//inicializamos la =variable font
		//font = FontManager.getInstance().createFont(app, "arial.ttf", 60, MTColor.WHITE);
		
		float worldOffset = 10;
		float worldOffsetalto = app.height/scale+50; //Make Physics world slightly bigger than screen borders
		//Physics world dimensions
		AABB worldAABB = new AABB(new Vec2(-worldOffset, -worldOffsetalto), new Vec2((app.width)/scale + worldOffset, (app.height)/scale + worldOffsetalto));
		//CTI - BG 
		//Aqui cambiamos la gravedad 
		//Vec2 gravity = new Vec2(0, 10);
		
		Vec2 gravity = new Vec2(0,6);
//		Vec2 gravity = new Vec2(0, 0);
		boolean sleep = true;
		//Create the pyhsics world
		this.world = new World(worldAABB, gravity, sleep);
		
		this.registerGlobalInputProcessor(new CursorTracer(app, this));
		
		//Update the positions of the components according the the physics simulation each frame
		this.registerPreDrawAction(new UpdatePhysicsAction(world, timeStep, constraintIterations, scale));
	
		physicsContainer = new MTComponent(app);
		//Scale the physics container. Physics calculations work best when the dimensions are small (about 0.1 - 10 units)
		//So we make the display of the container bigger and add in turn make our physics object smaller
		physicsContainer.scale(scale, scale, 1, Vector3D.ZERO_VECTOR);
		this.getCanvas().addChild(physicsContainer);
		 fondo =new MTRectangle(app,  -50,-50,-25, app.width+150, app.height+100);
		fondo.setNoFill(false);
		fondo.setPickable(false);
		fondo.setFillColor(new MTColor(216,255 ,255));
		fondo.setVisible(true);
		this.getCanvas().addChild(fondo);
	
			fondoimg = app.loadImage(imagesPath+"fondo.png");
		  blue = app.loadImage(imagesPath+"azul1.png");
		  green = app.loadImage(imagesPath+"verde1.png");
		  purple = app.loadImage(imagesPath+"morado1.png");
		  time = app.loadImage(imagesPath+"time.png");
		  player1 = app.loadImage(imagesPath+"player1.png");
		  player2 = app.loadImage(imagesPath+"player2.png");
		  nube= app.loadImage(imagesPath+"nubes.png");
		  explotadoaz= app.loadImage(imagesPath+"azul1ex.png");
		  explotadovd= app.loadImage(imagesPath+"verde1ex.png");
		  explotadomr= app.loadImage(imagesPath+"morado1ex.png");
		  botonreplay= app.loadImage(imagesPath+"replay.png");
		  
		/*  nube nubecita=new nube(app,fondo,nube,0,2*mtApplication.height/5,app.width/2,app.width/6);
		  Thread nubet=new Thread(nubecita);
		  nubet.start();
		  nube nubecita2=new nube(app,fondo,nube,mtApplication.width/2+50,2*mtApplication.height/5,app.width/2,app.width/6);
		  nubecita2.canvas.rotateY(nubecita2.getcenter(), 180);
		  Thread nubet2=new Thread(nubecita2);
		  nubet2.start();
		  nube nubecita0=new nube(app,fondo,nube,-mtApplication.width/2,2*mtApplication.height/5,app.width/2,app.width/6);
		  nubecita0.canvas.rotateY(nubecita2.getcenter(), 180);
		  Thread nube0=new Thread(nubecita0);
		  nube0.start();*/
		  //nube nubecita2=new nube(app,fondo,nube,(mtApplication.width - mtApplication.width/8),mtApplication.height/5,app.width/4,app.width/6);
		 // nube nubecita3=new nube(app,fondo,nube,(mtApplication.width-mtApplication.width/3),mtApplication.height/5,app.width/4,app.width/6);
		 // nube nubecita4=new nube(app,fondo,nube,mtApplication.width/3,mtApplication.height/5,app.width/4,app.width/6);
		  //nube nubecita5=new nube(app,fondo,nube,0,mtApplication.height/5,app.width/4,app.width/6);
		 // nubecita5.canvas.rotateY(nubecita5.getcenter(), 180);
		  //nubecita3.canvas.rotateY(nubecita3.getcenter(), 180);
		  
		  fondo.setTexture(fondoimg);
			
		//Create borders around the screen
		this.createScreenBorders(physicsContainer);
		
		 uiLayer = new MTComponent(app, new MTCamera(app));
		uiLayer.setDepthBufferDisabled(true);
		getCanvas().addChild(uiLayer);
		 font = FontManager.getInstance().createFont(app, "arial.ttf", 60,  new MTColor(255,255,255,150));
		 font2 = FontManager.getInstance().createFont(app, "Winks.ttf", 50, new MTColor(59,159,158,150));
		 font3 = FontManager.getInstance().createFont(app, "Winks.ttf", 70, new MTColor(54,123,12,150));
		 font4 = FontManager.getInstance().createFont(app, "arial.ttf", 60,  new MTColor(59,159,158,150));
		 fontscoreafected = FontManager.getInstance().createFont(app, "arial.ttf", 2,  new MTColor(59,159,158,150));
			
		
			
			 MTRectangle player1=new MTRectangle(app,app.width/2-WIDTH_SCORE_PLAYER,0 ,WIDTH_SCORE_PLAYER, 100
			 );
			 player1.setTexture(this.player1);
			 player1.setNoFill(false);
			 player1.setNoStroke(true);
			 player1.setPickable(false);
			 player1.setFillColor(new MTColor(210,191 ,180));
			 player1.setVisible(true);
			 player1.sendToFront();
			uiLayer.addChild(player1);
		
			 MTRectangle player2=new MTRectangle(app,app.width/2,0 ,WIDTH_SCORE_PLAYER, 100
			 );
			 player2.setTexture(this.player2 );
			 player2.setNoFill(false);
			 player2.setNoStroke(true);
			 player2.setPickable(false);
			 player2.setFillColor(new MTColor(210,191 ,180));
			 player2.setVisible(true);
			 player2.sendToFront();
			 uiLayer.addChild(player2);
			 
			 MTRectangle upscore=new MTRectangle(app,app.width/2-WIDTH_TIME_DISPLAY/2,0 ,WIDTH_TIME_DISPLAY, 100
			 );
	 upscore.setTexture(time );
	upscore.setDrawSmooth(true);
	 upscore.setNoFill(false);
	 upscore.setNoStroke(true);
	 upscore.setPickable(false);
	 upscore.setFillColor(new MTColor(210,191 ,180));
	 upscore.setVisible(true);
	 	uiLayer.addChild(upscore);
		
		t1 = new MTTextArea(app, font);
		t1.setPickable(false);
		t1.setKerning(false);
		t1.setNoFill(true);
		t1.setNoStroke(true);
		t1.setPositionGlobal(new Vector3D(app.width/2-50,app.height/16,20));
		   t1.setText("");
		getCanvas().addChild(t1);
		
		t5 = new MTTextArea(app, font);
		t5.setPickable(false);
		t5.setKerning(false);
		t5.setNoFill(true);
		t5.setNoStroke(true);
		t5.setPositionGlobal(new Vector3D(player2.getCenterPointGlobal().x,app.height/16,20));
		t5.setText("0");
		getCanvas().addChild(t5);
		//t5.setPositionRelativeToParent(new Vector3D(0,0,0));
		
		t2 = new MTTextArea(app, font);
		t2.setPickable(false);
		t2.setKerning(false);
		t2.setNoFill(true);
		t2.setNoStroke(true);
		t2.setPositionGlobal(new Vector3D(player1.getCenterPointGlobal().x-WIDTH_SCORE_PLAYER/3,app.height/16,20));
	        t2.setText("0");
	        getCanvas().addChild(t2);
		//t2.setPositionRelativeToParent(new Vector3D(0,0,0));
		
		tinfo = new MTTextArea(app, font2);
		tinfo.setPickable(false);
		tinfo.setKerning(false);
		tinfo.setNoFill(true);
		tinfo.setNoStroke(true);
		tinfo.setPositionGlobal(new Vector3D(app.width/4,2*app.height/5,20));
		
	        tinfo.setText("");
		getCanvas().addChild(tinfo);
		
		
		JuegoGlobos juego= new JuegoGlobos(15,2);
		

	}
	
	
	
	private void createScreenBorders(MTComponent parent){
		//Left border 
		float borderWidth = 50f;
		float borderHeight = app.height;
		Vector3D pos = new Vector3D(-(borderWidth/2f) , app.height/2f);
		PhysicsRectangle borderLeft = new PhysicsRectangle(pos, borderWidth, 3* borderHeight, app, world, 0,0,0, scale);
		borderLeft.setFillColor(MTColor.BLACK);
		borderLeft.setNoStroke(true);
		borderLeft.setName("borderLeft");
		parent.addChild(borderLeft);
		
		//Right border
		pos = new Vector3D(app.width + (borderWidth/2), app.height/2);
		PhysicsRectangle borderRight = new PhysicsRectangle(pos, borderWidth,3* borderHeight, app, world, 0,0,0, scale);
		borderRight.setFillColor(MTColor.BLACK);
		borderRight.setName("borderRight");
		borderRight.setNoStroke(true);
		parent.addChild(borderRight);
		borderRight.setVisible(false);
		//Top border
		borderWidth = app.width;
		borderHeight = 50f;
		pos = new Vector3D(app.width/2, -(app.height));
		PhysicsRectangle borderTop = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderTop.setFillColor(new MTColor(255, 0, 0));
		borderTop.setName("borderTop");
		parent.addChild(borderTop);
		//Bottom border
		//Bottom border
		pos = new Vector3D(app.width/2 , 2*app.height + (borderHeight/2));
		PhysicsRectangle borderBottom = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderBottom.setFillColor(new MTColor(255, 0, 0));
		borderBottom.setName("borderBottom");
		parent.addChild(borderBottom);
		
	}

	public void onEnter() {
	}
	
	public void onLeave() {	
	}

	private class JuegoGlobos implements Runnable{
		public static final int MAX_ELLAPSED_TIME = 5;
		
		Thread juego;
		int contadortiempo=59;
		long delay=1*1000;
		int utipo;
		List<Globo> globosbien=new CopyOnWriteArrayList<Globo>();
		Player player1,player2;
		int ellapsedtime;
		int texttime=0;
		boolean enabled;
		Thread t;
		nube nubecita,nubecita0,nubecita2;
		
		

		public JuegoGlobos(int nglobosbien,int nglobosmal) {
			juego=new Thread(this);
			enabled=false;
			ellapsedtime=0;
			
			player1=new Player();
			player2=new Player();
			
			juego.start();
			utipo=-1;
			texttime=0;
			
			t2.setText("0");
			t5.setText("0");
			
			
			
		
			
			
		System.out.println("Tiempo:"+contadortiempo+ "Puntaje P1"+ player1.Puntaje+"Puntaje P2:"+player2.Puntaje+"Numero de Globos en globosbien:"+globosbien.size()+"Nro globos en physics:" +
				physicsContainer.getChildCount());	
		}
		public void ComenzarJuego(int tipo){
			
			
			
			
		    try{
		    	
		    	
			final Globo glo = new Globo(app, new Vector3D(ToolsMath.getRandom(60, app.width-60),ToolsMath.getRandom(-200,-75),0), ToolsMath.getRandom(50,75), world, 1.0f, 0.3f, 0.2f, scale,tipo);
	
			globosbien.add(glo);
			physicsContainer.addChild(glo);
			//final Globo glo = new Globo(app, new Vector3D((float) (60 + Math.random()*(((mtApplication.width-60)-60)+1)),ToolsMath.getRandom(60, mtApplication.height/4),z), 50, world, 1.0f, 0.3f, 0.4f, scale,tipo);
			
		
				
				glo.unregisterAllInputProcessors();
				glo.registerInputProcessor(new TapProcessor(app));
				glo.addGestureListener(TapProcessor.class, new IGestureEventListener() {
					public boolean processGestureEvent(MTGestureEvent ge) {
						TapEvent te = (TapEvent)ge;
						Vector3D location=te.getLocationOnScreen();
						//si la coordenada y del globo esta mas abajo de la mitad de la pantalla entonces se puefe explotar 
						if(location.y > app.height/2){
							
						switch (te.getId()) {
						case MTGestureEvent.GESTURE_STARTED:
							//System.out.println("start");
							//glo.setFillColor(new MTColor(255,0,0));
							glo.setTexture(glo.texturaexplotada);
							glo.setTextureEnabled(true);
							player1.explotados++;
							if(glo.tipo==utipo){
								if(glo.getCenterPointGlobal().x <= app.width/2){
								//glo.sumarpuntaje(player1);
								player1.Puntaje=player1.Puntaje+10;
								t2.setText(Integer.toString(player1.Puntaje));
								}else{
									player2.Puntaje=player2.Puntaje+10;
									t5.setText(Integer.toString(player2.Puntaje));
									
								}
								glo.addChild(glo.scoreafected);
								glo.scoreafected.setText("+10");
								glo.scoreafected.getFont().setFillColor(new MTColor(59,159,158,255));
								
								
							}else{
								if(glo.getCenterPointGlobal().x <= app.width/2){
									//glo.sumarpuntaje(player1);
									if (player1.Puntaje>=10)	player1.Puntaje=player1.Puntaje-10;
									t2.setText(Integer.toString(player1.Puntaje));
									}else{
									if (player2.Puntaje>=10)	player2.Puntaje=player2.Puntaje-10;
									t5.setText(Integer.toString(player2.Puntaje));
									}
								glo.addChild(glo.scoreafected);
								glo.scoreafected.setText("-10");
								glo.scoreafected.getFont().setFillColor(new MTColor(255,86,86,255));
								
							}
							
							glo.scoreafected.sendToFront();
							glo.scoreafected.setPositionGlobal((new Vector3D(glo.getCenterPointGlobal().x,glo.getCenterPointGlobal().y,glo.getCenterPointGlobal().z)));
							glo.scoreafected.setVisible(true);
							Thread explosion =new Thread(glo);
							explosion.start();
							
							//physicsContainer.removeChild(glo);
							globosbien.remove(glo);
							
					
							
							break;
						case MTGestureEvent.GESTURE_UPDATED:
							//System.out.println("update");
							break;
						case MTGestureEvent.GESTURE_ENDED:
							
							break;
						}
						
						
						}
						return false;
					}
				});
				
	    }catch(Exception e){
		    	e.printStackTrace();
		    	System.out.println("se cayo creando un globo");
		    	
		    }
			
			
			
			
			
			
		}
	
		
		
		void PresentarMensajeTrans(int time,int tipo){
			String text="no text here"; 
			
			
			if(tipo==0){ 
				text="AZULES";
				tinfo.getFont().setFillColor(new MTColor(43,137,253,0));
				
				}
			else if(tipo==1){
				text="VERDES";
				tinfo.getFont().setFillColor(new MTColor(54,123,12,0));
				
				}
			else if(tipo==2){
				text="MORADOS";
				tinfo.getFont().setFillColor(new MTColor(255,0,255,0));
				
				}
		
			tinfo.setVisible(true);
			
			tinfo.setText("EXPLOTA LOS " + text);
			
			
			
			
		}
		
		//para mapear rango de valores desde left hacia right con value calculamos el valor 
		float CalcularGravedad(float value , int left_min, int left_max, int right_min, int right_max ){
			float leftspan =  left_max - left_min ;
			float rightspan =  right_max - right_min ;
			
			float scalefactor=(float)rightspan/(float)leftspan;
			
			float resultado= right_min + (value-left_min)*scalefactor;
			
			
			
			
			
			return resultado ;
			
		}

		@Override
		public void run() {
			
			
			 nubecita=new nube(app,fondo,nube,0,2*app.height/5,app.width/2,app.width/5);
			  Thread nubet=new Thread(nubecita);
			  nubet.start();
			   nubecita2=new nube(app,fondo,nube,app.width/2+50,2*app.height/5,app.width/2,app.width/5);
			  nubecita2.canvas.rotateY(nubecita2.getcenter(), 180);
			  Thread nubet2=new Thread(nubecita2);
			  nubet2.start();
			   nubecita0=new nube(app,fondo,nube,-app.width/2,2*app.height/5,app.width/2,app.width/5);
			  nubecita0.canvas.rotateY(nubecita2.getcenter(), 180);
			  Thread nube0=new Thread(nubecita0);
			  nube0.start();
		
			
			
			
			
		    try {
		    	
		    	// TODO Auto-generated method stub
		    	Random r=new Random();
		    	Random r2=new Random();
		        while (contadortiempo>=0) {
		        	
		        	//calcula la gravedad con la que caen los globos 
		        	float gravedad=CalcularGravedad(contadortiempo, 59, 1, 3, 6);
		        	world.setGravity(new Vec2(0,gravedad));
		        	
		        	//Actualiza el tiempo en pantalla
		        	 t1.setText(Integer.toString(contadortiempo));
		  		        
	  		        	
	  		        	
		         int tipo = r.nextInt(3);
		         
		         
		         //verifica el tiempo transcurrido desde la ultima orden de explotar si ya le toca envia una nueva orden 
		          if (ellapsedtime > MAX_ELLAPSED_TIME || contadortiempo==54){
		        	
					  do{
						  
						  tipo=r.nextInt(3);
						  
					  }while(tipo==utipo);
					  enabled=true;
					  PresentarMensajeTrans(45, tipo);	  
					  try{
							texto tex = new texto(tinfo);
							t=new Thread(tex);
							t.start();
					  }catch(Exception e){
						  e.printStackTrace();
					  }
					  
					  utipo=tipo;
					  ellapsedtime=0;
		          }
		          
		        
	
		    	  //Numero de globos a crear por segundo
		    	  int globosxsec=2;
		    	  while(globosxsec>0){
		    		  
		    	  int tipoglobo=r2.nextInt(3);
		          ComenzarJuego(tipoglobo);
		    	  globosxsec--;
		    	  
		    	  }
		    	  
		    	  //aumenta el tiempotranscurrido
		          ellapsedtime++;
		          if(enabled) texttime++;
		          
		          
		          //verifica cada segundo los globos que esten fuera del rango y los elimina 
		       //MTComponent[] globos=physicsContainer.getChildren();
		         Iterator<Globo> selected= globosbien.iterator();
		         
		          while(selected.hasNext()){
		        	  
		        	//  if (globosbien[i] instanceof Globo){
		        	  
		        	  Globo glob=selected.next();
		        	  if (glob.getCenterPointGlobal().getY() > app.height+100){
		        		  //globosbien.remove(glob);
		        		 // System.out.println("size "+globosbien.size());
		        		  physicsContainer.removeChild(glob);
		        		  globosbien.remove(glob);
		        		  //getCanvas().removeChild(glob);
		        		 if(glob!=null )
		        		  
		        			 glob.destroy();
		        		  //System.out.println("Explotado por que se salio ");
		        		  //  globosbien.remove(glob);
		        	  }
		        	  
		        		
		        		  
		          //}
		          } 
		         // System.out.println("Explotados:"+player1.explotados);
		          Thread.sleep(1000);
		
		  		   
		          contadortiempo--;
		          
		        }
		    		

		    	
		    	
		        		physicsContainer.removeAllChildren();
		        		globosbien.clear();
		        		//t2.setVisible(false);
		        		//t5.setVisible(false);
		        		tinfo.setVisible(false);
		        		
		        		t3 = new MTTextArea(app, font3);
		        		t3.setPickable(false);
		        		t3.setText("GAME OVER");
		        		t3.setNoFill(true);
		        		t3.setNoStroke(true);
		        		t3.setPositionGlobal(new Vector3D(app.width/2,app.height/3,0));
		        		t3.setVisible(true);
		        		
		        		t4 = new MTTextArea(app, font4);
		        		t4.setPickable(false);
		        		String textowinner="EMPATE";
		        		if(player1.Puntaje > player2.Puntaje) textowinner="PLAYER 1 WINNER";
		        		if(player2.Puntaje > player1.Puntaje) textowinner="PLAYER 2 WINNER";
		        		
		        		t4.setText(textowinner);
		        		t4.setNoFill(true);
		        		t4.setNoStroke(true);
		        		t4.setPositionGlobal(new Vector3D(app.width/2,(app.height/2),0));
		        		t4.setVisible(true);
		        		uiLayer.addChild(t3);
		        		uiLayer.addChild(t4);
		        		gameover=true;
		        		
		        		replay=new MTRectangle(app,2*app.width/5,app.height-300 ,150, 150);
		        		replay.setVisible(true);
		        		replay.setNoStroke(true);
		        		replay.setTexture(botonreplay);
		        		replay.unregisterAllInputProcessors();
		        		replay.registerInputProcessor(new TapAndHoldProcessor(app, 125));
		        		replay.addGestureListener(TapAndHoldProcessor.class, new TapAndHoldVisualizer(app, getCanvas()));
		        		replay.addGestureListener(TapAndHoldProcessor.class, new IGestureEventListener() {
		        			
		        			public boolean processGestureEvent(MTGestureEvent ge) {
		        				TapAndHoldEvent th = (TapAndHoldEvent)ge;
		        				switch (th.getId()) {
		        				case TapAndHoldEvent.GESTURE_STARTED:
		        					break;
		        				case TapAndHoldEvent.GESTURE_UPDATED:
		        					break;
		        				case TapAndHoldEvent.GESTURE_ENDED:
		        					if (th.isHoldComplete()){
		        						fondo.removeAllChildren();
		        					//	t3.setVisible(false);
		        						//t4.setVisible(false);
		        					//	t3.setText("0");
		        						//t4.setText("0");
		        						//gameover=false;
		        						//uiLayer.removeChild(replay);
		        						//createScreenBorders(physicsContainer);
		        						//JuegoGlobos juego= new JuegoGlobos(15,2);
		        					//	System.out.println("complete");
		        						//physicsContainer.removeAllChildren();
		        						//app.removeScene(escenacurrent);
		        						app.changeScene(new GlobitosScene(app, "Physics Example Scene",false));
		        						
		        					
		        					}
		        					break;
		        				default:
		        					break;
		        				}
		        				return false;
		        			}
		        		});
		        		
		        		
		        		
		        		
		        		uiLayer.addChild(replay);
		        		
		        		
		        		
		      } catch (Exception iex) {
		    	  
		    	 iex.printStackTrace();
		    	  System.out.println("ERROR : --Revisar--");
		      }
			
		}
		
	}
	private class Player{
		int Puntaje;
		int explotados;
		public Player(){
			this.Puntaje=0;
			this.explotados=0;
			
		}
		
	}

	private class Globo extends PhysicsCircle implements Runnable {
	
		int tipo=-1;
		MTColor col;
		String color;
		boolean activo;
		int tiempoexplota;
		PImage texturaexplotada;
		MTTextArea scoreafected;
		public Globo(PApplet applet, Vector3D centerPoint, float radius,
				World world, float density, float friction, float restitution, float worldScale,int tipo) {
			super(applet, centerPoint, radius, world, density, friction, restitution, worldScale);
			//this.setVisible(true);
			
			scoreafected=new MTTextArea(app, fontscoreafected);
			scoreafected.setPickable(false);
			scoreafected.setNoFill(true);
			scoreafected.setText("");
			scoreafected.setNoStroke(true);
			scoreafected.setVisible(true);
		
			
			activo=true;
			this.tipo=tipo;
			if(this.tipo==0) {
				//0 azul
				//this.col= new MTColor(108,188,219);
				this.setTexture(blue);
				texturaexplotada=explotadoaz;
			}
			else if(this.tipo==1) {
				//1 verde
				//this.col= new MTColor(252,255,87);
				this.setTexture(green);
				texturaexplotada=explotadovd;
				
				}// morado
			else {//this.col= new MTColor(196,115,149);
					this.setTexture(purple);
					texturaexplotada=explotadomr;
					
			}	

			this.setDrawSmooth(true);
		
			tiempoexplota=2;
			

		
		} 
		@Override
		protected void bodyDefB4CreationCallback(BodyDef def) {
			super.bodyDefB4CreationCallback(def);
			def.fixedRotation = true;
			def.linearDamping = 0.5f;
		}
		//@Override
		/*public void run() {
	
			while(activo){
			if(this.getCenterPointGlobal().getY()> app.height+100){
				activo=false;
			
				
				try{
				physicsContainer.removeChild(this);
				this.destroy();
				}catch(Exception e ){
					
					System.out.println("no se pudo destruir en el run de globo");
					e.printStackTrace();
				}
				
					
		
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		}*/
		@Override
		public void run() {
			
			//System.out.println("entro en el run del globo");
			while(tiempoexplota >1){
			try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			tiempoexplota--;
		}
			
			if( this!=null) {
				
				this.destroy();
				
			}	
		}
		

		
	}
	public class texto implements Runnable {
		int contador;
		MTTextArea textarea;
		int alfa = 255;
		int step= 10;
		public texto(MTTextArea textarea){
			contador=32;
			this.textarea=textarea;
			
		}
	
		
		@Override
		public void run() {
		
			while(contador >0){
				
		
				alfa =alfa - step ;
				
				
				
			
				MTColor colortemp=tinfo.getFont().getFillColor();
				colortemp.setAlpha(alfa);
				tinfo.getFont().setFillColor(colortemp);
				//tinfo.setText(""+"hi"+alfa);
				//System.out.println("Child Thread !!"+alfa);
				try {
					Thread.sleep(125);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				contador --;
			
			
		}
			tinfo.setVisible(false);
			
		}
		
		
		
	}
	public class nube implements Runnable{
		AbstractMTApplication application;
		MTComponent parent;
		MTRectangle canvas;
		int x , y , w, h;
		public nube(AbstractMTApplication app, MTComponent parent, PImage nubetexture, int x , int y , int w , int h ){
			this.application=app;
			canvas = new MTRectangle(this.application,x,y,10,w,h);
			canvas.setVisible(true);
			canvas.setPickable(false);
			canvas.setNoStroke(true);
			//canvas.setFillColor(new MTColor(0,0,255,125));
			canvas.setTexture(nubetexture);
			canvas.setTextureEnabled(true);
			parent.addChild(canvas);
			this.x=x;
			this.y=y;
			this.w=w;
			this.h=h;
		}
		public Vector3D getcenter(){
			return canvas.getCenterPointGlobal();
			
			
		}
		
		@Override
		public void run() {
			while(!gameover){
				if(canvas.getCenterPointGlobal().getX()>this.application.width){
					
					canvas.setPositionGlobal(new Vector3D(-application.width/3,y,0));	
					
				}else{
					canvas.setPositionGlobal(new Vector3D(canvas.getCenterPointGlobal().x+1,y,0));	
				
				}
				
				
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
		
		
	}

	
	
	

}