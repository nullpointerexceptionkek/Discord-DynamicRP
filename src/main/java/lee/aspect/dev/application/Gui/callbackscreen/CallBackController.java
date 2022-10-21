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
import javafx.scene.layout.*;
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

	private BounceOutRight pmoveUp;

	private SlideAndFade currentUp;

	private SlideAndFade afterUp;

	private BounceInLeft afterIn;

	public void displayStatus(String fl) {
		Playing.setText(fl);
	}
	public void switchToConfig(ActionEvent event) throws IOException, InterruptedException {
		switchtoconfig.setDisable(true);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/Scenes/LoadingScreen.fxml"));
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
			display1.setPrefHeight(30);
			display1.setLayoutX(anchorRoot.getScene().getWidth() / 2 - display1.getPrefWidth() / 2);
			display1.setLayoutY(anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2);
			display1.setTranslateY(-45);
			display1.setTextAlignment(TextAlignment.CENTER);
			//display1.setBorder(new Border(new BorderStroke(Color.RED,
			//		BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

			display2 = new Label();
			display2.setPrefWidth(100);
			display2.setPrefHeight(30);
			display2.setLayoutX(anchorRoot.getScene().getWidth() / 2 - display1.getPrefWidth() / 2);
			display2.setLayoutY(anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2);
			display2.setTextAlignment(TextAlignment.CENTER);
			//display2.setBorder(new Border(new BorderStroke(Color.GREEN,
			//		BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

			display3 = new Label();
			display3.setPrefWidth(100);
			display3.setPrefHeight(30);
			display3.setLayoutX(anchorRoot.getScene().getWidth() / 2 - display1.getPrefWidth() / 2);
			display3.setLayoutY(anchorRoot.getScene().getHeight() / 2 - display1.getPrefHeight() / 2);
			display3.setTranslateY(45);
			display3.setTextAlignment(TextAlignment.CENTER);
			//display3.setBorder(new Border(new BorderStroke(Color.ORANGE,
			//		BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

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
			pmoveUp = new BounceOutRight(display1);
			afterIn = new BounceInLeft(display3,0.8);

		});
	}

	public void updateCurrentDisplay() {

		if((display1.getLayoutY() + display1.getTranslateY()) <= 230) {
			pmoveUp.setNode(display1);
			pmoveUp.setOnFinished((actionEvent -> {
				display1.setTranslateY(45);
				afterIn.setNode(display1);
				display1.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY() >= Script.getTotalupdates().size()-1 ? 0 :RunLoopManager.getCURRENTDISPLAY()+1).getFl()
						+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY() >= Script.getTotalupdates().size()-1 ? 0 :RunLoopManager.getCURRENTDISPLAY()+1).getSl());
				afterIn.play();
			}));
			pmoveUp.play();
			currentUp = new SlideAndFade(display2, -45,0.3);
			currentUp.play();
			afterUp = new SlideAndFade(display3, 0,1);
			afterUp.play();
		}
		else if((display2.getLayoutY() + display2.getTranslateY()) <= 230) {
			pmoveUp.setNode(display2);
			pmoveUp.setOnFinished((actionEvent -> {
				display2.setTranslateY(45);
				afterIn.setNode(display2);
				display2.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY() >= Script.getTotalupdates().size()-1 ? 0 :RunLoopManager.getCURRENTDISPLAY()+1).getFl()
						+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY() >= Script.getTotalupdates().size()-1 ? 0 :RunLoopManager.getCURRENTDISPLAY()+1).getSl());
				afterIn.play();
			}));
			pmoveUp.play();
			currentUp = new SlideAndFade(display3,-45,0.3);
			currentUp.play();
			afterUp = new SlideAndFade(display1,0,1);
			afterUp.play();
		}
		else if((display3.getLayoutY() + display3.getTranslateY()) <= 230) {
			pmoveUp.setNode(display3);
			pmoveUp.setOnFinished((actionEvent -> {
				display3.setTranslateY(45);
				afterIn.setNode(display3);
				display3.setText(Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY() >= Script.getTotalupdates().size()-1 ? 0 :RunLoopManager.getCURRENTDISPLAY()+1).getFl()
						+ '\n' + Script.getTotalupdates().get(RunLoopManager.getCURRENTDISPLAY() >= Script.getTotalupdates().size()-1 ? 0 :RunLoopManager.getCURRENTDISPLAY()+1).getSl());
				afterIn.play();
			}));
			pmoveUp.play();
			currentUp = new SlideAndFade(display1, -45,0.3);
			currentUp.play();
			afterUp = new SlideAndFade(display2, 0,1);
			afterUp.play();
		}
		else{
			System.err.println("Animation have encounter an error, this should not be happend");
		}


	}
	
}
