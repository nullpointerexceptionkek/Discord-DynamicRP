package lee.aspect.dev.application.Gui.LoadingScreen;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lee.aspect.dev.application.LaunchManager;
import lee.aspect.dev.discordrpc.DiscordRP;
import net.arikia.dev.drpc.DiscordRPC;

public class LoadingController implements Initializable{

	@FXML
	private ProgressIndicator progress;
	
	@FXML
	private StackPane stackpane;
	
	@FXML
	private AnchorPane anchorroot;
	
	private String file;
	
	private Long sleep;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		new SplashScreen().start();
	}
	
	class SplashScreen extends Thread{
		
		@Override
		public void run() {
			if(file == null) {
				file = "null";
			}
			try {
				switch(file) {
				case "callback":
					long time = System.currentTimeMillis();
					while(DiscordRP.discordName == null) {
						DiscordRPC.discordRunCallbacks();
						Thread.sleep(100);
						if(time + 60000 < System.currentTimeMillis()) {
							file = "errorNoSDK";
							System.err.println("fail to connect sdk");
							break;
						}
					}
					break;
				case "readyconfig":
					Thread.sleep(100);
					break;
				default:
					Thread.sleep(sleep != null? sleep: 1000);
				}
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						try {
							switch(file) {
							case "callback":
								Parent root = FXMLLoader.load(getClass().getResource("/application/Gui/callbackscreen/CallBack.fxml"));
								Scene scene = anchorroot.getScene();
								
								root.translateYProperty().set(scene.getHeight());
								stackpane.getChildren().add(root);
								
								Timeline timeline = new Timeline();
								KeyValue keyValue = new KeyValue(root.translateYProperty(), 0,Interpolator.EASE_IN);
								KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), keyValue);
								timeline.getKeyFrames().add(keyFrame);
								timeline.setOnFinished(event1 -> {
									stackpane.getChildren().remove(anchorroot);
								});
								timeline.play();
								LaunchManager.startUpdate();
								break;
							case "readyconfig":
								Parent root1 = FXMLLoader.load(getClass().getResource("/application/Gui/config/ready/ReadyConfig.fxml"));
								Scene scene1 = anchorroot.getScene();
								
								root1.translateYProperty().set(scene1.getHeight());
								stackpane.getChildren().add(root1);
								
								Timeline timeline1 = new Timeline();
								KeyValue keyValue1 = new KeyValue(root1.translateYProperty(), 0,Interpolator.EASE_OUT);
								KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(1), keyValue1);
								timeline1.getKeyFrames().add(keyFrame1);
								timeline1.setOnFinished(event1 -> {
									stackpane.getChildren().remove(anchorroot);
								});
								timeline1.play();
								break;
							default:
								Parent root2 = FXMLLoader.load(getClass().getResource("/application/Gui/config/ready/ReadyConfig.fxml"));
								Scene scene2 = anchorroot.getScene();
								
								root2.translateYProperty().set(scene2.getHeight());
								stackpane.getChildren().add(root2);
								
								Timeline timeline2 = new Timeline();
								KeyValue keyValue2 = new KeyValue(root2.translateYProperty(), 0,Interpolator.EASE_OUT);
								KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(1), keyValue2);
								timeline2.getKeyFrames().add(keyFrame2);
								timeline2.setOnFinished(event1 -> {
									stackpane.getChildren().remove(anchorroot);
								});
								timeline2.play();
								
							}
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				});
				
				
				 
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void toNewScene(long sleep, String file)  {
		this.sleep = sleep;
		this.file = file;
		
	}
	
	public void toNewScene(String file)  {
		this.file = file;
		
	}
	
	
}
