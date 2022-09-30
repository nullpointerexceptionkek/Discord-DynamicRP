package lee.aspect.dev.application.Gui.callbackscreen;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lee.aspect.dev.application.Gui.LoadingScreen.LoadingController;
import lee.aspect.dev.application.LaunchManager;
import lee.aspect.dev.discordrpc.DiscordRP;

public class CallBackController implements Initializable{
	@FXML
	Label Playing;
	
	@FXML
	Label Welcome;
	
	@FXML
	private StackPane stackPane;
	
	@FXML
	private AnchorPane anchorRoot;
	
	@FXML
	private Button switchtoconfig;
	
	public void displayStatus(String fl) {
		Playing.setText(fl);
	}
	public void switchToConfig(ActionEvent event) throws IOException, InterruptedException {
		switchtoconfig.setDisable(true);
		LaunchManager.closeCallBack();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/LoadingScreen.fxml"));
		Parent root = loader.load();
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = stage.getScene();
		LoadingController lc = loader.getController();
		lc.toNewScene("readyconfig");
		root.translateYProperty().set(scene.getHeight());
		stackPane.getChildren().add(root);
		
		Timeline timeline = new Timeline();
		KeyValue keyValue = new KeyValue(root.translateYProperty(),0,Interpolator.EASE_OUT);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.3), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.setOnFinished(event1 -> {
			stackPane.getChildren().remove(anchorRoot);
		});
		timeline.play();
		
		
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		switchtoconfig.setDisable(false);
		Welcome.setText("Welcome " + DiscordRP.discordName + "!!!");
	}
	
}
