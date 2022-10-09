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
import lee.aspect.dev.animationengine.animation.RotateIn;
import lee.aspect.dev.application.Gui.callbackscreen.CallBackController;
import lee.aspect.dev.application.RunLoopManager;

public class LoadingController implements Initializable{

	@FXML
	private ProgressIndicator progress;
	
	@FXML
	private StackPane stackPane;
	
	@FXML
	private AnchorPane anchorRoot;
	
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
					try {
						RunLoopManager.initCallBack();
						RunLoopManager.startUpdate();
					} catch(RuntimeException e) {
						file = "error running callback";
						System.err.println("Error Loading Discord RPC");
						break;
					}

					break;
				case "readyconfig":
					try{
						RunLoopManager.closeCallBack();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
					finally {
						break;
					}
				default:
					Thread.sleep(sleep != null? sleep: 1000);
				}
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						try {
							switch(file) {
							case "callback":
								var loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/CallBack.fxml"));
								Parent root = loader.load();
								stackPane.getChildren().add(root);
								CallBackController callBackController = loader.getController();
								var animation = new RotateIn(stackPane);
								animation.setCycleCount(1).setDelay(Duration.valueOf("100ms"));
								animation.setOnFinished(actionEvent -> {
									stackPane.getChildren().remove(anchorRoot);
									callBackController.updateCurrentDisplay();
								});
								animation.play();

								RunLoopManager.startUpdate();
								break;
								case "readyconfig":
									Parent root1 = FXMLLoader.load(getClass().getResource("/lee/aspect/dev/ReadyConfig.fxml"));
									Scene scene1 = anchorRoot.getScene();

									root1.translateYProperty().set(scene1.getHeight());
									stackPane.getChildren().add(root1);

									Timeline timeline1 = new Timeline();
									KeyValue keyValue1 = new KeyValue(root1.translateYProperty(), 0,Interpolator.EASE_OUT);
									KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(1), keyValue1);
									timeline1.getKeyFrames().add(keyFrame1);
									timeline1.setOnFinished(event1 -> {
										stackPane.getChildren().remove(anchorRoot);
									});
									timeline1.play();
								break;
							default:
								Parent root2 = FXMLLoader.load(getClass().getResource("/lee/aspect/dev/ReadyConfig.fxml"));
								Scene scene2 = anchorRoot.getScene();
								
								root2.translateYProperty().set(scene2.getHeight());
								stackPane.getChildren().add(root2);
								
								Timeline timeline2 = new Timeline();
								KeyValue keyValue2 = new KeyValue(root2.translateYProperty(), 0,Interpolator.EASE_OUT);
								KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(1), keyValue2);
								timeline2.getKeyFrames().add(keyFrame2);
								timeline2.setOnFinished(event1 -> {
									stackPane.getChildren().remove(anchorRoot);
								});
								timeline2.play();
								System.err.println(file);
								
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
