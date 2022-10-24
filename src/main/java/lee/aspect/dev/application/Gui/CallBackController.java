package lee.aspect.dev.application.Gui;

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
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import lee.aspect.dev.animationengine.animation.*;
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
	private Button switchToConfig;

	private final Label display1 = new Label();

	private final Label display2 = new Label();

	private final Label display3 = new Label();

	private BounceOutRight pMoveUp;

	private BounceInLeft afterIn;

	public void switchToConfig(ActionEvent event) throws IOException {
		switchToConfig.setDisable(true);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/Scenes/LoadingScreen.fxml"));
		Parent root = loader.load();
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = stage.getScene();
		LoadingController lc = loader.getController();
		lc.toNewScene(LoadingController.Load.ConfigScreen);
		root.translateYProperty().set(scene.getHeight());
		stackPane.getChildren().add(root);

		Timeline timeline = new Timeline();
		KeyValue keyValue = new KeyValue(root.translateYProperty(),0,Interpolator.EASE_OUT);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.3), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.setOnFinished(event1 -> stackPane.getChildren().remove(anchorRoot));
		timeline.play();


	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		switchToConfig.setDisable(false);
		Welcome.setText("Welcome " + DiscordRP.discordName + "!!!");
		Platform.runLater(() -> {
			setDefault(display1,-45);
			setDefault(display2,0);
			setDefault(display3,45);

			if(Script.getTotalupdates().size() == 1) {
				display2.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()).getFl()
						+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()).getSl());
			} else {
				if(RunLoopManager.getCURRENTDISPLAY() > 0)
					display1.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()-1).getFl()
							+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()-1).getSl());
				display1.opacityProperty().set(0.3);
				display2.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()).getFl()
						+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY()).getSl());
				display3.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY() >= Script.getTotalupdates().size()-1 ? 0 :RunLoopManager.getCURRENTDISPLAY()+1).getFl()
						+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY() >= Script.getTotalupdates().size()-1 ? 0 :RunLoopManager.getCURRENTDISPLAY()+1).getSl());
				display3.opacityProperty().set(0.8);
			}



			anchorRoot.getChildren().addAll(display1, display2, display3);
			pMoveUp = new BounceOutRight(display1);
			afterIn = new BounceInLeft(display3,0.8);

		});
	}

	public void updateCurrentDisplay() {
		if((display1.getLayoutY() + display1.getTranslateY()) <= 230) {
			updateDisplayLabel(display1,display2,display3);
		}
		else if((display2.getLayoutY() + display2.getTranslateY()) <= 230) {
			updateDisplayLabel(display2,display3,display1);
		}
		else if((display3.getLayoutY() + display3.getTranslateY()) <= 230) {
			updateDisplayLabel(display3,display1,display2);
		}
		else{
			System.err.println("Animation have encounter an error, this should not be occur");
		}


	}

	private void setDefault(Label display, int transY){
		display.setPrefWidth(150);
		display.setPrefHeight(45);
		display.setLayoutX(anchorRoot.getScene().getWidth() / 2 - display1.getPrefWidth() / 2);
		display.setLayoutY(anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2);
		display.setTranslateY(transY);
		display.setTextAlignment(TextAlignment.CENTER);
	}

	private void updateDisplayLabel(Label Display1, Label Display2, Label Display3){
		SlideAndFade CurrentUp, AfterUp;
		pMoveUp.setNode(Display1);
		pMoveUp.setOnFinished((actionEvent -> {
			Display1.setTranslateY(45);
			afterIn.setNode(Display1);
			Display1.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY() >= Script.getTotalupdates().size()-1 ? 0 :RunLoopManager.getCURRENTDISPLAY()+1).getFl()
					+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY() >= Script.getTotalupdates().size()-1 ? 0 :RunLoopManager.getCURRENTDISPLAY()+1).getSl());
			afterIn.play();
		}));
		pMoveUp.play();
		CurrentUp = new SlideAndFade(Display2, -45,0.3);
		CurrentUp.play();
		AfterUp = new SlideAndFade(Display3, 0,1);
		AfterUp.play();
	}
	
}
