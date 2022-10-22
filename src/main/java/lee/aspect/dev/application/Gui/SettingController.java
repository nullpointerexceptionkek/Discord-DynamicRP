package lee.aspect.dev.application.Gui;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lee.aspect.dev.application.CustomDiscordRPC;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.discordrpc.settings.Settings;
import lee.aspect.dev.discordrpc.settings.options.MinimizeMode;
import lee.aspect.dev.discordrpc.settings.options.Theme;

public class SettingController implements Initializable{

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
	private AnchorPane anchorRoot;
	
	@FXML
	private StackPane stackPane;
	
	public void switchBack(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		stage.close();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/Scenes/ReadyConfig.fxml"));
		Parent root = loader.load();
		root.getStylesheets().add(getClass().getResource(Settings.getTheme().getThemepass()).toExternalForm());
		ConfigController ec = loader.getController();
		//loader.setController(ec);
        stage = CustomDiscordRPC.primaryStage;
        stage.setTitle("Custom Discord RP");
        stage.setScene(new Scene(root));
        stage.setX(goBack.getScene().getWindow().getX());stage.setY(goBack.getScene().getWindow().getY());
        stage.setResizable(false);
        stage.show();
		
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
				if(themeChoiceBox.getValue().equals(theme.getDisplayName())){
					Settings.setTheme(theme);
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
	
}
