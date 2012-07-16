package fishes;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vertex;

import processing.core.*;

/*
Steer behavior class, to control/simulate natural movement
 the idea is to make some behaviors interactive like
 */

class Boid extends Flagellum {

  PVector location;
  PVector velocity;
  PVector acceleration;
  float r;
  float maxForce;
  float maxSpeed;
  float fleeConstant;
  float inv_maxSpeed;
  float wandertheta;
  float rushSpeed;

  boolean timeCheck = false;                 // check if time interval is complete
  int timeCount = 0;                         // time cicle index
  int lastTimeCheck = 0;                     // stores last time check
  int timeCountLimit = 10;                   // max time cicles
  int evadeRange;
  int pursueRange;
  InputCursor objetivo;
  /*MTLine vectorsteer;
  MTLine vectorsteerfinal;
  MTLine vectortarget;
  MTLine vectorlocation;*/
  //private AbstractMTApplication app;

  Boid (String _skin, PVector _location, float _maxSpeed, float _maxForce,AbstractMTApplication _app) {
	super(_skin,_app);
	super.app=_app;
	objetivo=null;
	this.rushSpeed = super.app.random(3.0f, 5.0f); //3 a 6
    location = _location.get();
    velocity = new PVector(0, 0);
    acceleration = new PVector(0, 0);
    maxForce = _maxForce;
    maxSpeed = _maxSpeed;
    this.fleeConstant=1/(maxSpeed*36);//cambiamos de 8 a 12 para reducir su velovidad  al perseguir 
    this.inv_maxSpeed=1/maxSpeed;
    this.evadeRange=100; //100
    this.pursueRange=400;
     /*vectorsteer=new MTLine(super.app,new Vertex(100,100,0), new Vertex(100,100,0));
    vectorsteer.setStrokeColor(MTColor.FUCHSIA);
    vectorsteer.setPickable(false);
    vectorsteerfinal=new MTLine(super.app,new Vertex(100,100,0), new Vertex(100,100,0));
    vectorsteerfinal.setStrokeColor(MTColor.SILVER);
    vectorsteerfinal.setStrokeWeight(5.0f);
   // vectorsteerfinal.setPickable(false);
    vectortarget=new MTLine(super.app,new Vertex(0,0,0), new Vertex(0,0,0));
    vectortarget.setStrokeColor(MTColor.GREEN);
    vectortarget.setPickable(false);
    vectorlocation=new MTLine(super.app,new Vertex(0,0,0), new Vertex(0,0,0));
    vectorlocation.setStrokeColor(MTColor.BLUE);
    vectorlocation.setPickable(false);
    vectorsteer.setVisible(false);
    vectortarget.setVisible(false);
    vectorlocation.setVisible(false);*/
    // System.out.println("Creating Boid: Sp:"+this.rushSpeed+" Loc:"+location+" MF:"+maxForce+" MS:"+maxSpeed);   
  }


  PVector steer(PVector target, boolean slowdown) {
    PVector steer;
    this.muscleFreq=0.06f;
    PVector desired = PVector.sub(target, location);//hace substraccion entre los dos vectores 
    float d = desired.mag();//calcula la magnitud del vector 
      //System.out.println("Magnitud desired"+d);
     /* 
      //vectorsteer 
      Vertex nuevosvertices[]=new Vertex[2];
      nuevosvertices[0]=new Vertex(target.x,target.y,0);
      nuevosvertices[1]=new Vertex(location.x,location.y,0);
     vectorsteer.setVertices(nuevosvertices);
     //vector target
     Vertex nuevosvertices2[]=new Vertex[2];
     nuevosvertices2[0]=new Vertex(0,0,0);
     nuevosvertices2[1]=new Vertex(target.x,target.y,0);
    vectortarget.setVertices(nuevosvertices2);
    //vectorlacotion
    Vertex nuevosvertices3[]=new Vertex[2];
    nuevosvertices3[0]=new Vertex(0,0,0);
    nuevosvertices3[1]=new Vertex(location.x,location.y,0);
   vectorlocation.setVertices(nuevosvertices3);
     
     
      //vectorsteer.setVisible(true);
      super.app.getScene("peces").getCanvas().addChild(vectorsteer);
      super.app.getScene("peces").getCanvas().addChild(vectortarget);
      super.app.getScene("peces").getCanvas().addChild(vectorlocation);
      */
    if (d > 0) {
      desired.normalize();

      if (slowdown && d <= 75) {//cambiamos este valor para que los peces tengan un menor rango para empezar a hacer nada..
       //desired.mult(maxSpeed /24);//cambiamos de 100 a 1000 para hafcerlo mas lento  
    	 steer = new PVector(0, 0); //se pone vector 0 pa que no hagan nada
    	  //steer = PVector.sub(desired, velocity);
        //.limit(maxForce/8);
          
      }
      else if (slowdown && d < 200 && d>75) {//cambiamos este valor para que los peces tengan un menor rango para empezar a hacer nada..
         desired.mult(maxSpeed * (d/200));//cambiamos de 100 a 1000 para hafcerlo mas lento  
      	 // steer = new PVector(0, 0); //se pone vector 0 pa que no hagan nada
          steer = PVector.sub(desired, velocity);
     
          //super.app.
        steer.limit(maxForce/1.5f);
        
        }
      else {
       desired.mult(maxSpeed * (d/200));
        //se puso estas 2 lineas antes de la llave para que no afecte al if de arriba y se queden inmoviles 
        steer = PVector.sub(desired, velocity);
        steer.limit(maxForce);
     
      }

     
    }
    else {
      steer = new PVector(0, 0);
    }
   /* Vertex nuevosvertices4[]=new Vertex[2];
    nuevosvertices4[0]=new Vertex(0,0,0);
    nuevosvertices4[1]=new Vertex(velocity.x,velocity.y,0);
    vectorsteerfinal.setVertices(nuevosvertices4);
    System.out.println("X:"+velocity.x+"Y:"+velocity.y);
    System.out.println("D:"+d);*/
    return steer;
  }
  //creamos una funcion alterna a steer para que no interfiera con el metodor wander que tambien usaba seek y steer]
  
  PVector steerwander(PVector target, boolean slowdown) {
	    PVector steer;
	    PVector desired = PVector.sub(target, location);//hace substraccion entre los dos vectores 
	    float d = desired.mag();//calcula la magnitud del vector 

	    if (d > 0) {
	      desired.normalize();

	      if (slowdown && d < 100) {//no cambiar este valor ...
	        desired.mult(maxSpeed * (d / 1000));//cambiamos de 100 a 1000 para hafcerlo mas lento  
	      }
	      else {
	        desired.mult(maxSpeed);
	      }

	      steer = PVector.sub(desired, velocity);
	      steer.limit(maxForce);
	    }
	    else {
	      steer = new PVector(0, 0);
	    }

	    return steer;
	  }


  /*  SEEK - FLEE  */
  void seek(PVector target) {
    acceleration.add(steer(target, true));//cambiamos steer(target, false) para que baje la velocidad cuando ya esten muy cerca
    
  }
  //creamos un metodo propiio para que no compartan el mismo metodo con pursue 
  void seekwander(PVector target) {
	    acceleration.add(steerwander(target, true));//cambiamos steer(target, false) para que baje la velocidad cuando ya esten muy cerca
	    
	  }

  void arrive(PVector target) {
    acceleration.add(steer(target, false));
  }

  void flee(PVector target) {
    acceleration.sub(steer(target, false));
  }

  /*  PURSUE - EVADE  */
  void pursue(PVector target) {
	  timeCheck = true;
	  if (PApplet.dist(target.x, target.y, location.x, location.y) < this.pursueRange) {
	    float lookAhead = location.dist(target)*this.fleeConstant;// / (maxSpeed * 2);
	    PVector predictedTarget = new PVector(target.x - lookAhead, target.y - lookAhead);
	    seek(predictedTarget);
	  } 
	  
	  
	/*  
	  float wanderR = 5;
	  float wanderD = 100;
	  float change = 0.05f;//0.05f;

	  wandertheta += super.app.random(-change, change);

	  PVector circleLocation = velocity.get();
	  circleLocation.normalize();
	  circleLocation.mult(wanderD);
	  circleLocation.add(location);

	  PVector circleOffset = new PVector(wanderR * PApplet.cos(wandertheta), wanderR * PApplet.sin(wandertheta));
	  PVector target2 = PVector.add(circleLocation, circleOffset);
	    seek(target2);
	  
	  
	  
	  */
	  
    //float lookAhead = location.dist(target) *this.inv_maxSpeed;
    //PVector predictedTarget = new PVector(target.x + lookAhead, target.y + lookAhead);
    //seek(predictedTarget);
  }

  void evade(PVector target) {
    timeCheck = true;
    if (PApplet.dist(target.x, target.y, location.x, location.y) < this.evadeRange) {
      float lookAhead = location.dist(target)*this.fleeConstant;// / (maxSpeed * 2);
      PVector predictedTarget = new PVector(target.x - lookAhead, target.y - lookAhead);
      flee(predictedTarget);
    }
  }


  /*  WANDER  */
  void wander() {
    float wanderR = 5;
    float wanderD = 100;
    float change = 0.05f;//0.05f;

    wandertheta += super.app.random(-change, change);

    PVector circleLocation = velocity.get();
    circleLocation.normalize();
    circleLocation.mult(wanderD);
    circleLocation.add(location);

    PVector circleOffset = new PVector(wanderR * PApplet.cos(wandertheta), wanderR * PApplet.sin(wandertheta));
    PVector target = PVector.add(circleLocation, circleOffset);
    seekwander(target);
  }


  void run2() {
    update2();
    borders();
    display();
  }  


  void update2() {
  velocity.add(acceleration);
   // velocity.limit(maxSpeed);
  // location.add(velocity);
    acceleration.mult(0);

  }
    void run() {
        update();
        borders();
        display();
      }  


      void update() {
        velocity.add(acceleration);
        velocity.limit(maxSpeed);
        location.add(velocity);
        acceleration.mult(0);

    // sets flagellum muscleFreq in relation to velocity
   //super.muscleRange = norm(velocity.mag(), 0, 1) * 2.5;
    super.muscleFreq = (float) (PApplet.norm(velocity.mag(), 0, 1) * 0.045);//cambiamos el multiplicador de 0.06 a 0.04 para que no se serpenteen demasiado 
    super.move();

    if (timeCheck) {
      if (super.app.millis() > lastTimeCheck + 200) {
        lastTimeCheck = super.app.millis();

        if (timeCount <= timeCountLimit) {
          // derease maxSpeed in relation with time cicles
          // this formula needs a proper look
          maxSpeed = rushSpeed - (PApplet.norm(timeCount, 0, timeCountLimit) * 3);
          timeCount++;
        }
        else// if (timeCount >= timeCountLimit) {
        {  // once the time cicle is complete
          // resets timer variables,
          timeCount = 0;
          timeCheck = false;

          // set default speed values
          maxSpeed = super.app.random(0.8f, 1.9f);
          maxForce = 0.2f;
        }
      }
    }
  }


  // control skin tint, for now it picks a random dark grey color
  //Cambiamos la opacidad para que aparezca muy oscuro al principio 
  int opacity = 0;
  int maxOpacity = 0;

  void display() {
    if (opacity < 255) opacity += 1;
    else opacity = 255;
    super.app.tint(maxOpacity, maxOpacity, maxOpacity, opacity);

    // update location and direction
    float theta = velocity.heading2D() + PApplet.radians(180);//direccion del pez ...NO CAMBIAR 
    super.app.pushMatrix();
    super.app.translate(location.x, location.y);
    //rotate(theta);
    super.display();
    super.app.popMatrix();
    super.app.noTint();

    // update flagellum body rotation
    super.theta = PApplet.degrees(theta);
    super.theta += 180;
  }

  // wrapper, appear opposit side
  void borders() {
    if (location.x < -super.skin.width) location.x = super.app.width;
    if (location.x > super.app.width + skin.width) location.x = 0;
    if (location.y < -super.skin.width) location.y = super.app.height;
    if (location.y > super.app.height + skin.width) location.y = 0;
  }

}




