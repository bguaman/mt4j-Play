package advanced.drawing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTColorPicker;
import org.mt4j.components.visibleComponents.widgets.MTSceneTexture;
import org.mt4j.components.visibleComponents.widgets.MTSlider;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GLFBO;

import processing.core.PGraphics;
import processing.core.PImage;

public class MainDrawingScene extends AbstractScene {
	private AbstractMTApplication pa;
	private MTRectangle textureBrush;
	private MTEllipse pencilBrush;
	private DrawSurfaceScene drawingScene;
	int conglobal;
	public MTRoundRectangle frame;
	boolean save=false,bandera;
	public int contadorImagen=0;
	public MTSceneTexture sceneTexture;
	public double tiempo=System.currentTimeMillis();
	
//	private String imagesPath = System.getProperty("user.dir")+File.separator + "examples"+  File.separator +"advanced"+ File.separator + File.separator +"drawing"+ File.separator + File.separator +"data"+ File.separator +  File.separator +"images" + File.separator ;
	private String imagesPath = "advanced" + AbstractMTApplication.separator + "drawing" + AbstractMTApplication.separator + "data" + AbstractMTApplication.separator + "images" + AbstractMTApplication.separator;

	public MainDrawingScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		save=false;
		bandera=true;
		
		this.pa = mtApplication;
		
		  
		if (!(MT4jSettings.getInstance().isOpenGlMode() && GLFBO.isSupported(pa))){
			System.err.println("Drawing example can only be run in OpenGL mode on a gfx card supporting the GL_EXT_framebuffer_object extension!");
			return;
		}
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		//this.getCanvas().
		//Create window frame
        frame = new MTRoundRectangle(pa,-50, -50, 0, pa.width+100, pa.height+100,25, 25);
        frame.setSizeXYGlobal(pa.width-10, pa.height-10);
        frame.setPickable(false);
        this.getCanvas().addChild(frame);
        //Create the scene in which we actually draw
        drawingScene = new DrawSurfaceScene(pa, "DrawSurface Scene");
        drawingScene.setClear(false);
      
       
        //Create texture brush
        PImage brushImage = getMTApplication().loadImage(imagesPath + "brush1.png");
		textureBrush = new MTRectangle(getMTApplication(), brushImage);
		textureBrush.setPickable(false);
		textureBrush.setNoFill(false);
		textureBrush.setNoStroke(true);
		textureBrush.setDrawSmooth(true);
		textureBrush.setFillColor(new MTColor(0,0,0));
		//Set texture brush as default
		drawingScene.setBrush(textureBrush);
		
		//Create pencil brush
		pencilBrush = new MTEllipse(pa, new Vector3D(brushImage.width/2f,brushImage.height/2f,0), brushImage.width/2f, brushImage.width/2f, 60);
		pencilBrush.setPickable(false);
		pencilBrush.setNoFill(false);
		pencilBrush.setNoStroke(false);
		pencilBrush.setDrawSmooth(true);
		pencilBrush.setStrokeColor(new MTColor(0, 0, 0, 255));
		pencilBrush.setFillColor(new MTColor(0, 0, 0, 255));
		
        //Create the frame/window that displays the drawing scene through a FBO
//        final MTSceneTexture sceneWindow = new MTSceneTexture(0,0, pa, drawingScene);
		//We have to create a fullscreen fbo in order to save the image uncompressedc
		sceneTexture = new MTSceneTexture(pa,0, 0, pa.width, pa.height, drawingScene);
        sceneTexture.getFbo().clear(true, 255, 255, 255, 0, true);
        sceneTexture.setStrokeColor(new MTColor(255,255,255));
        frame.addChild(sceneTexture);
        
        PImage imagen_fondo=mtApplication.loadImage(imagesPath +"Inventio-Wall.png");
        
		MTRectangle fondo=new MTRectangle(pa, -50, -50, 0, mtApplication.screenWidth+100, mtApplication.screenHeight+100);
		fondo.setVisible(true);
		fondo.setPickable(false);
		fondo.setTexture(imagen_fondo);
		
		sceneTexture.addChild(fondo);
        //Eraser button
        PImage eraser = pa.loadImage(imagesPath + "Kde_crystalsvg_eraser.png");
        MTImageButton b = new MTImageButton(pa, eraser);
        b.setNoStroke(true);
        b.translate(new Vector3D(-50,400,0));
        b.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isTapped()){
//					//As we are messing with opengl here, we make sure it happens in the rendering thread
					pa.invokeLater(new Runnable() {
						public void run() {
							sceneTexture.getFbo().clear(true, 255, 255, 255, 0, true);						
						}
					});
				}
				return true;
			}
        });
        frame.addChild(b); 
        
        //Pen brush selector button
      /*  PImage penIcon = pa.loadImage(imagesPath + "pen.png");
        final MTImageButton penButton = new MTImageButton(pa, penIcon);
        //frame.addChild(penButton);
        penButton.translate(new Vector3D(-50f, 65,0));
        penButton.setNoStroke(true);
        penButton.setStrokeColor(new MTColor(0,0,0));
        
        //Texture brush selector button
        PImage brushIcon = pa.loadImage(imagesPath + "paintbrush.png");
        final MTImageButton brushButton = new MTImageButton(pa, brushIcon);
        //frame.addChild(brushButton);
        brushButton.translate(new Vector3D(-50f, 130,0));
        brushButton.setStrokeColor(new MTColor(0,0,0));
        brushButton.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isTapped()){
					drawingScene.setBrush(textureBrush);
					brushButton.setNoStroke(false);
					penButton.setNoStroke(true);
				}
				return true;
			}
        });
        
        penButton.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isTapped()){
					drawingScene.setBrush(pencilBrush);
					penButton.setNoStroke(false);
					brushButton.setNoStroke(true);
				}
				return true;
			}
        });*/
        
        //Save to file button
        PImage floppyIcon = pa.loadImage(imagesPath + "t.png");
        final MTImageButton floppyButton = new MTImageButton(pa, floppyIcon);
        frame.addChild(floppyButton);
        floppyButton.translate(new Vector3D(-50f, 460,0));
        floppyButton.setNoStroke(true);
        floppyButton.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isTapped()){
					pa.invokeLater(new Runnable() {
						public void run() {
							drawingScene.getCanvas().drawAndUpdateCanvas(pa.g, 0);
							pa.saveFrame("images/test"+contadorImagen+".jpg");
							clear(pa.g);
						}
					});
					//pa.saveFrame("images/test"+contadorImagen+".jpg");
					ETwitter tweet=new ETwitter(contadorImagen);
					Thread hThread=new Thread(tweet);
					hThread.start();
					sceneTexture.getFbo().clear(true, 255, 255, 255, 0, true);
			/*		drawingScene.registerPreDrawAction(new IPreDrawAction() {
						public void processAction() {
							//drawingScene.getCanvas().drawAndUpdateCanvas(pa.g, 0);
							pa.saveFrame();
						}
						public boolean isLoop() {
							return false;
						}
					});*/
					contadorImagen++;
				}
				return true;
			}
        });
        
        /////////////////////////
        //ColorPicker and colorpicker button
      /*  PImage colPick = pa.loadImage(imagesPath + "colorcircle.png");
//        final MTColorPicker colorWidget = new MTColorPicker(0, pa.height-colPick.height, colPick, pa);
        final MTColorPicker colorWidget = new MTColorPicker(pa, 0, 0, colPick);
        colorWidget.translate(new Vector3D(0f, 135,0));
        colorWidget.setStrokeColor(new MTColor(0,0,0));
        colorWidget.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				if (ge.getId()== MTGestureEvent.GESTURE_ENDED){
					if (colorWidget.isVisible()){
						colorWidget.setVisible(false);
					}
				}else{
					drawingScene.setBrushColor(colorWidget.getSelectedColor());
				}
				return false;
			}
		});
       // frame.addChild(colorWidget);
        colorWidget.setVisible(false);
        
    /*    PImage colPickIcon = pa.loadImage(imagesPath + "ColorPickerIcon.png");
        MTImageButton colPickButton = new MTImageButton(pa, colPickIcon);
        //frame.addChild(colPickButton);
        colPickButton.translate(new Vector3D(-50f, 195,0));
        colPickButton.setNoStroke(true);
        colPickButton.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isTapped()){
					if (colorWidget.isVisible()){
						colorWidget.setVisible(false);
					}else{
						colorWidget.setVisible(true);
						colorWidget.sendToFront();
					}				
				}
				return true;
			}
        });*/
        
        //Add a slider to set the brush width
   /*     MTSlider slider = new MTSlider(pa, 0, 0, 200, 38, 0.05f, 2.0f);
        slider.setValue(1.0f);
       // frame.addChild(slider);
        slider.rotateZ(new Vector3D(), 90, TransformSpace.LOCAL);
        slider.translate(new Vector3D(-7, 325));
        slider.setStrokeColor(new MTColor(0,0,0));
        slider.setFillColor(new MTColor(220,220,220));
        slider.getKnob().setFillColor(new MTColor(70,70,70));
        slider.getKnob().setStrokeColor(new MTColor(70,70,70));
        slider.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				drawingScene.setBrushScale((Float)p.getNewValue());
			}
		});
        //Add triangle in slider to indicate brush width
        MTPolygon p = new MTPolygon(pa, 
        		new Vertex[]{
        		new Vertex(2 + slider.getKnob().getWidthXY(TransformSpace.LOCAL), slider.getHeightXY(TransformSpace.LOCAL)/2f, 0),
        		new Vertex(slider.getWidthXY(TransformSpace.LOCAL)-3, slider.getHeightXY(TransformSpace.LOCAL)/4f +2, 0),
        		new Vertex(slider.getWidthXY(TransformSpace.LOCAL)-1, slider.getHeightXY(TransformSpace.LOCAL)/2f, 0),
        		new Vertex(slider.getWidthXY(TransformSpace.LOCAL)-3, -slider.getHeightXY(TransformSpace.LOCAL)/4f -2 + slider.getHeightXY(TransformSpace.LOCAL), 0),
        		new Vertex(2, slider.getHeightXY(TransformSpace.LOCAL)/2f, 0),
        });
        p.setFillColor(new MTColor(150,150,150, 150));
        p.setStrokeColor(new MTColor(160,160,160, 190));
        p.unregisterAllInputProcessors();
        p.setPickable(false);
        slider.getOuterShape().addChild(p);
        slider.getKnob().sendToFront();*/
        
	    contadortiempo c = new contadortiempo();
		Thread hilo=new Thread(c);
		hilo.start();
		
	
	}
	public class contadortiempo implements Runnable{

		public contadortiempo(){
			//temporizador que mira si es tiempo de exportar la imagen y guardarla
		}
		@Override
		public void run() {
			while(true){
			
				double tiempoFinal=System.currentTimeMillis();
				double d=tiempoFinal-tiempo;
				if(d>360000){
					tiempo=tiempoFinal;
					save=true;
					bandera=true;
				}			
			}
			
		}
		
	}

	public void onEnter() {}
	
	public void onLeave() {	}
	
	@Override
	public boolean destroy() {
		boolean destroyed = super.destroy();
		if (destroyed){
			drawingScene.destroy(); //Destroy the scene manually since it isnt destroyed in the MTSceneTexture atm!
		}
		return destroyed;
	}
	@Override
	public void drawAndUpdate(PGraphics graphics, long timeDelta){
		
		//Process preDrawActions
		synchronized (this.preDrawActions) {
			for (Iterator<IPreDrawAction> iter = preDrawActions.iterator(); iter.hasNext();) {
				IPreDrawAction action = iter.next();
				action.processAction();
				if (!action.isLoop()){
					iter.remove();
				}
			}
		}
		
		//Clear the background
		if (this.clearBeforeDraw){ 
			this.clear(graphics);
		}
		
		//Draw and update canvas
		this.getCanvas().drawAndUpdateCanvas(graphics, timeDelta);
		if(save){
			
			bandera=false;
			this.mtApplication.saveFrame("images/test"+this.contadorImagen+".jpg");
			//Publico el Frame capturado del Canvas 
			//y lo publico en Twitter
			/*ETwitter tweet=new ETwitter(this.contadorImagen);
			Thread hThread=new Thread(tweet);
			hThread.start();*/
			//ETwitter.procesarTweet(this.contadorImagen);
			this.contadorImagen++;
			save=false;
			//sceneTexture.getFbo().clear(true, 255, 255, 255, 0, true);	
			
		}
	}
}
