package advanced.drawing;



import java.io.File;

import processing.core.PImage;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class ETwitter implements Runnable{
	public int cont=0;
	public ETwitter(int numberImage) {
		// TODO Auto-generated constructor stub
		this.cont=numberImage;
	}

	 
	 public void procesarTweet(){
			// Create file
				File picture;
				try{
					//Obtengo el recurso de una imagen desde el computador
					picture = new File("images/test"+this.cont+".jpg");
					if(picture.canRead()){
						System.out.println("Si se pudo leer la imagen");
					}		
					else{
						//no se pudo leer la imagen
						//System.exit(0);
					}
					//configuracion con los datos de la aplicacion creada en Twitter
					ConfigurationBuilder cb = new ConfigurationBuilder();
					cb.setDebugEnabled(true)
					  .setOAuthConsumerKey("PYHp4bhLf4snQilYgf9ywQ")
					  .setOAuthConsumerSecret("7Z99I6bqgJbEJeRkUfTXbxakspzQP3RUuOgIvCnGdM4")
					  .setOAuthAccessToken("729056089-ByM1Gt2c7lqTRQkD23b4EqjQBZrfzhggVx0ggdd8")
					  .setOAuthAccessTokenSecret("rrzjpRcedxzIaBg7SOpxaomTMdehyudocuF23kpDs");
					TwitterFactory tf = new TwitterFactory(cb.build());
					Twitter twitter = tf.getInstance(); 
					//status para para el Tweet
				    StatusUpdate status = new StatusUpdate("Imagen del Canvas #CTI realizadas con un led Infrarrojo");
				   status.setMedia(picture);//coloca el recurso imagen en el estatus
				    twitter.updateStatus(status);// push del estado en Twitter
				} catch (Exception e) {
				    //e.printStackTrace();
				}
			
		 }
	 
	 @Override
	public void run() {
		// TODO Auto-generated method stub
		 procesarTweet();
	}
}
