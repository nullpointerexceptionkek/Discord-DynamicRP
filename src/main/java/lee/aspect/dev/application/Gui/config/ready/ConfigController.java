package lee.aspect.dev.application.Gui.config.ready;



import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lee.aspect.dev.application.Gui.LoadingScreen.LoadingController;
import lee.aspect.dev.application.Gui.Settings.SettingController;
import lee.aspect.dev.application.Gui.config.EditListController;
import lee.aspect.dev.application.LaunchManager;
import lee.aspect.dev.discordrpc.Script;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.discordrpc.settings.Settings;

public class ConfigController implements Initializable{
	
	@FXML
	private Label titleLabel;
	
	@FXML
	private TextField appID;
	
	@FXML
	protected ListView<Updates> displayUpdates;
	
	@FXML
	private Button callbackButton;
	
	@FXML
	private Button settingButton;
	
	@FXML
	private RadioButton applaunch, none, local, custom;
	
	@FXML
	private AnchorPane anchorRoot;
	
	@FXML
	private StackPane stackPane;
	
	private int currentCount;
	
	public void getTimeStampMode(ActionEvent event){
		if(applaunch.isSelected()) {
			Script.setTimestampmode("Default");
		} 
		else if(none.isSelected()){
			Script.setTimestampmode("None");
		}
		else if(local.isSelected()){
			Script.setTimestampmode("Local time");
		}
		else if(custom.isSelected()){
			Script.setTimestampmode("Custom");
		}
	}
	
	public void switchToCallBack(ActionEvent event) throws IOException, InterruptedException{
		//update DiscordRP app id and save it to the file
		if(displayUpdates.getItems().size() < 1) return;
		String DiscordAppID = appID.getText();
		Settings.setDiscordAPIKey(DiscordAppID);
		SettingManager.saveSettingToFile();
		
		System.out.println(displayUpdates.getItems());
		ArrayList<Updates> u = new ArrayList<>(displayUpdates.getItems());
		Script.setTotalupdates(u); 
		LaunchManager.saveScripToFile();
		//Parent root = FXMLLoader.load(getClass().getResource("/application/Gui/callbackscreen/CallBack.fxml"));
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/LoadingScreen.fxml"));
		Parent root = loader.load();
		LoadingController lc = loader.getController();
		lc.toNewScene(1000,"callback");
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = callbackButton.getScene();
		
		
		root.translateYProperty().set(scene.getHeight());
		stackPane.getChildren().add(root);
		
		Timeline timeline = new Timeline();
		KeyValue keyValue = new KeyValue(root.translateYProperty(), 0,Interpolator.EASE_IN);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.3), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.setOnFinished(event1 -> {
			stackPane.getChildren().remove(anchorRoot);
		});
		timeline.play();

		
		

	}
	
	public void switchToSetting(ActionEvent event) throws IOException, InterruptedException {
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		stage.close();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/Settings.fxml"));
		Parent root = loader.load();
		root.getStylesheets().add(getClass().getResource(Settings.getTheme().Themepass()).toExternalForm());
		SettingController ec = loader.getController();
		//loader.setController(ec);
        stage = new Stage();
        stage.setTitle("Settings");
        stage.setScene(new Scene(root));
        stage.setX(displayUpdates.getScene().getWindow().getX());stage.setY(displayUpdates.getScene().getWindow().getY());
        stage.setResizable(false);
        stage.show();
		
	}
	
	public void addnewitem() {
		currentCount++;
		System.out.println(currentCount);
		if(Script.getTotalupdates().size()>0)
			Script.addUpdates(new Updates((Script.getTotalupdates().get(currentCount-2).getWait()), 
					String.valueOf(currentCount),
					Script.getTotalupdates().get(currentCount-2).getImagetext(),
					Script.getTotalupdates().get(currentCount-2).getSmallimage()
					,Script.getTotalupdates().get(currentCount-2).getSmalltext(), 
					"First line " + currentCount, "Second line " + currentCount,
					Script.getTotalupdates().get(currentCount-2).getButton1Text(),
					Script.getTotalupdates().get(currentCount-2).getButton1Url(),
					Script.getTotalupdates().get(currentCount-2).getButton2Text(),
					Script.getTotalupdates().get(currentCount-2).getButton2Url()));
		
		else Script.addUpdates(new Updates(16000, String.valueOf(currentCount), "First line " + currentCount,"","","", "Second line " + currentCount));
		displayUpdates.getItems().clear();		
		displayUpdates.getItems().addAll(Script.getTotalupdates());	
	}

	
	//this file will init and set the Listview to the total updates that was read from Json
	//it will also set the appid to only accept numbers and if loaded is not null, it will leave it empty
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ImageView imageView = new ImageView(getClass().getResource("/lee/aspect/dev/settingsImage.png").toExternalForm());
		imageView.setFitHeight(25);
		imageView.setPreserveRatio(true);
		settingButton.setGraphic(imageView);
		settingButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.getItems().addAll(
				new MenuItem("Copy"),
				new MenuItem("Paste"),
				new MenuItem("Delete")
				);
        //TilePane tilePane = new TilePane(displayUpdates);
        displayUpdates.setContextMenu(contextMenu);

        
		currentCount = Script.getTotalupdates().size();
		appID.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		    	if(newValue == null) return;
		        if (!newValue.matches("\\d*")) {
		            appID.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		    
		});
		appID.setText(Settings.getDiscordAPIKey());
		
		
		
		try {
			//set the timestamp mode
			switch(Script.getTimestampmode()) {
				case "Default":
					applaunch.setSelected(true);
					break;
				
				case "None":
					none.setSelected(true);
					break;
					
				case "Local time":
					local.setSelected(true);
					break;
				
				case "Custom":
					custom.setSelected(true);
					break;
				default:
					applaunch.setSelected(true);
					Script.setTimestampmode("Default");
			
			}
			displayUpdates.getItems().addAll(Script.getTotalupdates());	
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		displayUpdates.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		//check if the list is double clicked
		displayUpdates.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)){
                    if(event.getClickCount() == 2){
                    	if(!((displayUpdates.getSelectionModel().getSelectedIndex()) == -1)) {
                    		showListConfig(displayUpdates.getSelectionModel().getSelectedIndex(),displayUpdates.getScene().getWindow().getX(),displayUpdates.getScene().getWindow().getY());
                    		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                    		stage.close();
                    	}
                    }
                }
				
			}

        });
	}
	//this will open up a new window and edit the arraylist
	private void showListConfig(int numberInList, double x, double y) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/EditListScript.fxml"));
			Parent root = loader.load();
			root.getStylesheets().add(getClass().getResource(Settings.getTheme().Themepass()).toExternalForm());
			EditListController ec = loader.getController();
			//loader.setController(ec);
			ec.setnumberInList(numberInList);
	        Stage stage = new Stage();
	        stage.setTitle("Config Editor");
	        stage.setScene(new Scene(root));
	        stage.setX(x);stage.setY(y);
	        stage.setResizable(false);
	        stage.show();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	
	
	
	
	
}
