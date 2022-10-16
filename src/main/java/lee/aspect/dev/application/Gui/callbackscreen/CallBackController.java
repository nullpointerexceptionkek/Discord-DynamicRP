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
import lee.aspect.dev.animationengine.animation.*;
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

	private Label display1;

	private Label display2;

	private Label display3;

	private BounceOutUp pmoveUp;

	private SlideAndFade currentUp;

	private SlideAndFade afterUp;

	private BounceInLeft afterIn;

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
		Platform.runLater(() -> {
			display1 = new Label();
			display1.setPrefWidth(100);
			display1.setPrefHeight(250);
			display1.setLayoutX(anchorRoot.getScene().getWidth() / 2 - display1.getPrefWidth() / 2);
			display1.setLayoutY(anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2 - 45);
			display1.setTextAlignment(TextAlignment.CENTER);

			display2 = new Label();
			display2.setPrefWidth(100);
			display2.setPrefHeight(250);
			display2.setLayoutX(anchorRoot.getScene().getWidth() / 2 - display1.getPrefWidth() / 2);
			display2.setLayoutY(anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2);
			display2.setTextAlignment(TextAlignment.CENTER);

			display3 = new Label();
			display3.setPrefWidth(100);
			display3.setPrefHeight(250);
			display3.setLayoutX(anchorRoot.getScene().getWidth() / 2 - display1.getPrefWidth() / 2);
			display3.setLayoutY(anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2 + 45);
			display3.setTextAlignment(TextAlignment.CENTER);

			//prev.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()).getFl()
			//		+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()).getSl());
			//display2.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()).getFl()
			//		+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()).getSl());
			//display3.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()+1).getFl()
			//		+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()+1).getSl());

			display1.setText("1");
			display2.setText("2");
			display3.setText("3");

			anchorRoot.getChildren().addAll(display1, display2, display3);
			pmoveUp = new BounceOutUp(display1);
			currentUp = new SlideAndFade(display2, display2.getLayoutBounds().getMaxY()-45);
			afterUp = new SlideAndFade(display3, display3.getLayoutBounds().getMaxY()-45);
			afterIn = new BounceInLeft(display3);

			//pmoveUp.play();
			//currentUp.play();
			//afterUp.play();
			//afterIn.play();
		});
	}

	public void updateCurrentDisplay() {
		//pmoveUp.play();

		System.out.println((anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2 - 45));
		System.out.println("d1 " + display1.getLayoutY());
		System.out.println("d2 " +display2.getLayoutY());
		System.out.println("d3 " +display3.getLayoutY());
		if(display1.getLayoutY() <= (anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2 - 45)) {
			currentUp.setOnFinished((actionEvent -> {
				display2.setLayoutY((anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2 - 45));
				currentUp.setNode(display3);
			}));
			currentUp.play();
			afterUp.setOnFinished((actionEvent -> {
				display3.setLayoutY((anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2));
				afterUp.setNode(display1);
			}));
			afterUp.play();
			display1.setLayoutY(anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2 + 45);
			afterIn.setNode(display1);
			display1.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()+1).getFl()
					+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()+1).getSl());
			afterIn.play();
		}
		else if(display2.getLayoutY() <= (anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2 - 45)) {
			currentUp.setOnFinished((actionEvent -> {
				currentUp.setNode(display1);
			}));
			currentUp.play();
			afterUp.setOnFinished((actionEvent -> {
				afterUp.setNode(display2);
			}));
			afterUp.play();
			display2.setLayoutY(anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2 + 45);
			afterIn.setNode(display2);
			display2.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()+1).getFl()
					+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()+1).getSl());
			afterIn.play();
		}
		else if(display3.getLayoutY() <= (anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2 - 45)) {
			currentUp.setOnFinished((actionEvent -> {
				currentUp.setNode(display2);
			}));
			currentUp.play();
			afterUp.setOnFinished((actionEvent -> {
				afterUp.setNode(display3);
			}));
			afterUp.play();
			display3.setLayoutY(anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2 + 45);
			afterIn.setNode(display3);
			display3.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()+1).getFl()
					+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()+1).getSl());
			afterIn.play();
		}
		else{
			System.err.println("Animation have encounter an error, this should not be happend");
		}


	}
	
}
