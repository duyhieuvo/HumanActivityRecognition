package Utilities;

import java.io.File;

import javax.sound.sampled.*;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



public class SoundAlarm{
	  static MediaPlayer mediaPlayer;
	  
	  public static void playSong() {
		  JFXPanel fxPanel = new JFXPanel();
		  String bip = "audio.mp3";
		  Media hit = new Media(new File(bip).toURI().toString());
		  mediaPlayer = new MediaPlayer(hit);
		  mediaPlayer.play();
	  }
	  public static void stopSong() {
		  if(mediaPlayer != null){
			  mediaPlayer.stop();
			  mediaPlayer=null;
	        }

	  }

}

