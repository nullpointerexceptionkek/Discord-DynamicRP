package lee.aspect.dev.application.Gui;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lee.aspect.dev.SystemUtil.Exceptions.CannotRestartException;
import lee.aspect.dev.SystemUtil.RestartApplication;
import lee.aspect.dev.animationengine.animation.SlideOutLeft;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.discordrpc.settings.Settings;
import lee.aspect.dev.discordrpc.settings.options.MinimizeMode;
import lee.aspect.dev.discordrpc.settings.options.Theme;

public class SettingController implements Initializable {

	@FXML
	private Button goBack;

	@FXML
	private ChoiceBox<String> themeChoiceBox;

	@FXML
	private ChoiceBox<MinimizeMode> minimizeModeChoiceBox;

	@FXML
	private CheckBox shutDownInterfaceCheckBox;

	@FXML
	private CheckBox noAnimationCheckBox;

	@FXML
	private CheckBox lowResourceCheckBox;

	@FXML
	private CheckBox startLaunchCheckBox;

	@FXML
	private AnchorPane settingsAnchorPane;

	@FXML
	private StackPane stackPane;

	public void switchBack(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/Scenes/ReadyConfig.fxml"));
		Parent root = loader.load();
		root.getStylesheets().add(getClass().getResource(Settings.getTheme().getThemepass()).toExternalForm());
		stackPane.getChildren().add(0, root);
		var animation = new SlideOutLeft(settingsAnchorPane);
		animation.setOnFinished((actionEvent) -> {
			stackPane.getChildren().remove(settingsAnchorPane);
		});
		animation.play();

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//add theme to theme choice box
		EnumSet.allOf(Theme.class).forEach((theme) -> {
			themeChoiceBox.getItems().add(theme.getDisplayName());
		});
		themeChoiceBox.setValue((Settings.getTheme().getDisplayName()));
		themeChoiceBox.setOnAction((event) -> {
			EnumSet.allOf(Theme.class).forEach((theme) -> {
				if (themeChoiceBox.getValue().equals(theme.getDisplayName())) {
					Settings.setTheme(theme);
					Parent root = settingsAnchorPane.getParent();
					root.getStylesheets().removeAll();
					root.getStylesheets().add(getClass().getResource(Settings.getTheme().getThemepass()).toExternalForm());
					applyChange();
					return;
				}
			});
		});
		//add min to min choice box
		minimizeModeChoiceBox.setDisable(!SystemTray.isSupported());
		minimizeModeChoiceBox.getItems().addAll(EnumSet.allOf(MinimizeMode.class));
		minimizeModeChoiceBox.setValue((Settings.getMinimizeMode()));
		minimizeModeChoiceBox.setOnAction((event) -> {
			Settings.setMinimizeMode(minimizeModeChoiceBox.getValue());
		});
		//add for booleans
		shutDownInterfaceCheckBox.setDisable(!SystemTray.isSupported());
		shutDownInterfaceCheckBox.setSelected(Settings.isShutDownInterfaceWhenTray());
		shutDownInterfaceCheckBox.setOnAction((actionEvent -> {
			Settings.setShutDownInterfaceWhenTray(shutDownInterfaceCheckBox.isSelected());
		}));

		noAnimationCheckBox.setSelected(Settings.isNoAnimation());
		noAnimationCheckBox.setOnAction((actionEvent -> {
			Settings.setNoAnimation(noAnimationCheckBox.isSelected());
		}));

		lowResourceCheckBox.setSelected(Settings.isLowResourceMode());
		lowResourceCheckBox.setOnAction((actionEvent -> {
			Settings.setLowResourceMode(lowResourceCheckBox.isSelected());
		}));

		startLaunchCheckBox.setSelected(Settings.isStartLaunch());
		startLaunchCheckBox.setOnAction((actionEvent -> {
			Settings.setStartLaunch(startLaunchCheckBox.isSelected());
		}));


		SettingManager.saveSettingToFile();

	}

	private void applyChange() {
		var alert = new Alert(Alert.AlertType.INFORMATION);
		ButtonType yesButton = new ButtonType("Yes restart now", ButtonBar.ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
		alert.getButtonTypes().setAll(yesButton, noButton);
		alert.setTitle("Apply changes");
		alert.setHeaderText("Some changes need the application \n to restart inorder to apply");
		alert.setContentText("would you like to restart now?");
		ButtonType result = alert.showAndWait().get();
		if (result.equals(yesButton)) {
			try {
				RestartApplication.FullRestart();
			} catch (URISyntaxException | IOException | CannotRestartException e) {
				var alertException = new Alert(Alert.AlertType.ERROR);
				alertException.setTitle("Exception");
				alertException.setHeaderText("We have encounter an exception");
				alertException.setContentText("CannotRestartException");
				alertException.show();
			}
		}

	}
}
