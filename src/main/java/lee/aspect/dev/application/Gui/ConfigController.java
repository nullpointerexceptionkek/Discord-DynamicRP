package lee.aspect.dev.application.Gui;



import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
import lee.aspect.dev.animationengine.animation.FadeIn;
import lee.aspect.dev.application.RunLoopManager;
import lee.aspect.dev.discordrpc.Script;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.discordrpc.settings.Settings;
import lee.aspect.dev.discordrpc.settings.options.TimeStampMode;

public class ConfigController implements Initializable{
	
	@FXML
	private Label titleLabel;
	
	@FXML
	private TextField appID;
	
	@FXML
	protected ListView<String> displayUpdates;
	
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
			Script.setTimestampmode(TimeStampMode.applaunch);
		} 
		else if(none.isSelected()){
			Script.setTimestampmode(TimeStampMode.none);
		}
		else if(local.isSelected()){
			Script.setTimestampmode(TimeStampMode.current);
		}
		else if(custom.isSelected()){
			Script.setTimestampmode(TimeStampMode.CDFromDayEnd);
		}
	}
	
	public void switchToCallBack(ActionEvent event) throws IOException, InterruptedException{
		//update DiscordRP app id and save it to the file
		if(displayUpdates.getItems().size() < 1) return;
		String DiscordAppID = appID.getText();
		Settings.setDiscordAPIKey(DiscordAppID);
		SettingManager.saveSettingToFile();
		RunLoopManager.saveScripToFile();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/Scenes/LoadingScreen.fxml"));
		Parent root = loader.load();
		root.getStylesheets().add(getClass().getResource(Settings.getTheme().getThemepass()).toExternalForm());
		LoadingController lc = loader.getController();
		lc.toNewScene(1000,"callback");
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = callbackButton.getScene();
		stackPane.getChildren().add(root);

		FadeIn animation = new FadeIn(stackPane);
		animation.setSpeed(2.00);
		stackPane.getChildren().remove(anchorRoot);
		animation.play();

		
		

	}
	
	public void switchToSetting(ActionEvent event) throws IOException, InterruptedException {
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		stage.close();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/Scenes/Settings.fxml"));
		Parent root = loader.load();
		root.getStylesheets().add(getClass().getResource(Settings.getTheme().getThemepass()).toExternalForm());
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
		
		else Script.addUpdates(new Updates(16000, String.valueOf(currentCount), "" + currentCount,"","","First line ", "Second line " + currentCount));
		displayUpdates.getItems().add("Fl: \"" + Script.getTotalupdates().get(Script.getTotalupdates().size()-1).getFl() + "\" Sl: \"" + Script.getTotalupdates().get(Script.getTotalupdates().size()-1).getSl()+"\"");
	}

	
	//this file will init and set the Listview to the total updates that was read from Json
	//it will also set the appid to only accept numbers and if loaded is not null, it will leave it empty
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ImageView imageView = new ImageView(getClass().getResource("/lee/aspect/dev/icon/settingsImage.png").toExternalForm());
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
				case applaunch:
					applaunch.setSelected(true);
					break;
				
				case none:
					none.setSelected(true);
					break;
					
				case current:
					local.setSelected(true);
					break;
				
				case CDFromDayEnd:
					custom.setSelected(true);
					break;
				default:
					applaunch.setSelected(true);
					Script.setTimestampmode(TimeStampMode.applaunch);
			
			}
			displayUpdates.getItems().clear();
			for(int i = 0;i < Script.getTotalupdates().size();i++) {
				displayUpdates.getItems().add("Fl: \"" + Script.getTotalupdates().get(i).getFl() + "\" Sl: \"" + Script.getTotalupdates().get(i).getSl()+"\"");
			}
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
                    		//Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
							//stage.close();
                    	}
                    }
                }
				
			}

        });
	}
	//this will open up a new window and edit the arraylist
	private void showListConfig(int numberInList, double x, double y) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/Scenes/EditListScript.fxml"));
			Parent root = loader.load();
			root.getStylesheets().add(getClass().getResource(Settings.getTheme().getThemepass()).toExternalForm());
			EditListController ec = loader.getController();
			//loader.setController(ec);
			ec.setnumberInList(numberInList);
	        Stage stage = new Stage();
	        stage.setTitle("Config Editor - index: " + (numberInList+1));
	        stage.setScene(new Scene(root));
	        stage.setX(x);stage.setY(y);
	        stage.setResizable(false);
	        stage.show();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	
	
	
	
	
}
