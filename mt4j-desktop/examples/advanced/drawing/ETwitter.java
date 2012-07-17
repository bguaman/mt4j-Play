package advanced.drawing;



import java.io.File;

import processing.core.PImage;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class ETwitter {
	 public static void procesarTweet(int cont){
		// Create file
			File picture;
			try{
				//Obtengo el recurso de una imagen desde el computador
				picture = new File("images/test"+cont+".jpg");
				if(picture.canRead()){
					System.out.println("Si se pudo leer la imagen");
				}		
				else{
					//no se pudo leer la imagen
					System.exit(0);
				}
				//configuracion con los datos de la aplicacion creada en Twitter
				ConfigurationBuilder cb = new ConfigurationBuilder();
				cb.setDebugEnabled(true)
				  .setOAuthConsumerKey("zfpAP9XMSgMj6YUbYhy5Q")
				  .setOAuthConsumerSecret("7YdcDHty4kwpNqHuuF3KvKkhTMzGiDGkDOYxz9w2Y")
				  .setOAuthAccessToken("292725248-T4mBc8Ls6SDetf8h8UYCNzft7COWUHXtZGfzeSBJ")
				  .setOAuthAccessTokenSecret("C3zXz3htpwQ89C4BFrels5KwcYtmqlZWkq5a6ZaBA");
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
}
