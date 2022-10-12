package lee.aspect.dev.application.Gui.callbackscreen;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import lee.aspect.dev.animationengine.animation.BounceInDown;
import lee.aspect.dev.animationengine.animation.BounceInUp;
import lee.aspect.dev.animationengine.animation.BounceOutUp;
import lee.aspect.dev.animationengine.animation.SlideAndFade;
import lee.aspect.dev.application.Gui.LoadingScreen.LoadingController;
import lee.aspect.dev.application.RunLoopManager;
import lee.aspect.dev.discordrpc.DiscordRP;
import lee.aspect.dev.discordrpc.Script;

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

	private Label prev;

	private Label current;

	private Label after;

	private BounceOutUp pmoveUp;

	private SlideAndFade currentUp;

	public void displayStatus(String fl) {
		Playing.setText(fl);
	}
	public void switchToConfig(ActionEvent event) throws IOException, InterruptedException {
		switchtoconfig.setDisable(true);
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
		prev = new Label();
		current = new Label();
		after = new Label();
		Platform.runLater(()-> {
			anchorRoot.getChildren().addAll(prev, current, after);
			// defines the animation for the prev moving out of screen
			prev.setPrefWidth(100);
			prev.setPrefHeight(250);
			prev.setLayoutX(anchorRoot.getScene().getWidth() / 2 - prev.getPrefWidth() / 2);
			prev.setLayoutY(anchorRoot.getScene().getHeight() / 2 - prev.getPrefHeight() / 2 + 25);
			prev.setTextAlignment(TextAlignment.CENTER);
			pmoveUp = new BounceOutUp(prev);
			pmoveUp.setOnFinished((actionEvent) -> {
				if(RunLoopManager.getCURRENTDISPLAY()<1) return;
				prev.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()).getFl()
						+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()).getSl());
				currentUp.play();
				});
			});
			// defines the animation when current moves up
			current.setPrefWidth(100);
			current.setPrefHeight(250);
			current.setLayoutX(anchorRoot.getScene().getWidth() / 2 - prev.getPrefWidth() / 2);
			current.setLayoutY(anchorRoot.getScene().getHeight() / 2 - prev.getPrefHeight() / 2);
			currentUp = new SlideAndFade(current,(anchorRoot.getScene().getHeight() / 2 - prev.getPrefHeight() / 2 + 25));
		}

	public void updateCurrentDisplay(){





	}

	
}
