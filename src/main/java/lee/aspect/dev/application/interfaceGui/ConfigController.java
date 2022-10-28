package lee.aspect.dev.application.interfaceGui;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lee.aspect.dev.animationengine.animation.*;
import lee.aspect.dev.application.RunLoopManager;
import lee.aspect.dev.discordrpc.Script;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.discordrpc.settings.Settings;
import lee.aspect.dev.discordrpc.settings.options.TimeStampMode;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {

    public ToggleGroup timeStampMode;
    @FXML
    protected ListView<String> displayUpdates;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField appID;
    @FXML
    private Button callbackButton;

    @FXML
    private Button settingButton;

    @FXML
    private RadioButton appLaunch, none, local, custom;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane stackPane;

    private ImageView invalidIndex;

    private ImageView invalidAppID;

    private int currentCount;

    public void getTimeStampMode() {
        if (appLaunch.isSelected()) {
            Script.setTimestampmode(TimeStampMode.applaunch);
        } else if (none.isSelected()) {
            Script.setTimestampmode(TimeStampMode.none);
        } else if (local.isSelected()) {
            Script.setTimestampmode(TimeStampMode.current);
        } else if (custom.isSelected()) {
            Script.setTimestampmode(TimeStampMode.CDFromDayEnd);
        }
    }

    public void switchToCallBack() throws IOException {
        String DiscordAppID = appID.getText();
        if (displayUpdates.getItems().size() < 1) {
            displayUpdates.setBackground(new Background(new BackgroundFill(Color.rgb(204,51,0,0.9), new CornerRadii(5), Insets.EMPTY)));
            if(!anchorRoot.getChildren().contains(invalidIndex)) {
                invalidIndex = WarningManager.setWarning(displayUpdates,16,"Index must be greater than one", WarningManager.Mode.Left);
                anchorRoot.getChildren().add(invalidIndex);
            }
            new Shake(anchorRoot).play();
            return;
        }
        if(DiscordAppID.isEmpty() || DiscordAppID.isBlank()){
            invalidDiscordAppID("Invalid Application ID");
            return;
        }

        callbackButton.setDisable(true);
        Settings.setDiscordAPIKey(DiscordAppID);
        SettingManager.saveSettingToFile();
        RunLoopManager.saveScripToFile();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/LoadingScreen.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Settings.getTheme().getThemepass())).toExternalForm());
        var fadeOut = new FadeOut(anchorRoot);
        fadeOut.setOnFinished((actionEvent -> {
            stackPane.getChildren().remove(anchorRoot);
            var fadeIn = new FadeIn(root);
            fadeIn.setOnFinished((actionEvent1) -> {
                LoadingController lc = loader.getController();
                lc.toNewScene(LoadingController.Load.CallBackScreen);
            });
            root.setOpacity(0);
            stackPane.getChildren().add(root);
            fadeIn.play();
        }));
        fadeOut.setSpeed(5);
        fadeOut.play();

    }

    public void switchToSetting() throws IOException {
        settingButton.setDisable(true);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/Settings.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Settings.getTheme().getThemepass())).toExternalForm());
        stackPane.getChildren().add(root);
        var animation = new SlideInLeft(root);
        animation.setOnFinished((actionEvent) -> stackPane.getChildren().remove(anchorRoot));
        animation.play();

    }

    public void addNewItem() {
        if(anchorRoot.getChildren().contains(invalidIndex)) {
            anchorRoot.getChildren().remove(invalidIndex);
            displayUpdates.setBackground(null);
        }
        currentCount++;
        if (Script.getTotalupdates().size() > 0)
            Script.addUpdates(new Updates((Script.getTotalupdates().get(currentCount - 2).getWait()),
                    String.valueOf(currentCount),
                    Script.getTotalupdates().get(currentCount - 2).getImagetext(),
                    Script.getTotalupdates().get(currentCount - 2).getSmallimage()
                    , Script.getTotalupdates().get(currentCount - 2).getSmalltext(),
                    "First line " + currentCount, "Second line " + currentCount,
                    Script.getTotalupdates().get(currentCount - 2).getButton1Text(),
                    Script.getTotalupdates().get(currentCount - 2).getButton1Url(),
                    Script.getTotalupdates().get(currentCount - 2).getButton2Text(),
                    Script.getTotalupdates().get(currentCount - 2).getButton2Url()));

        else
            Script.addUpdates(new Updates(16000, String.valueOf(currentCount), "" + currentCount, "", "", "First line ", "Second line " + currentCount));
        displayUpdates.getItems().add("Fl: \"" + Script.getTotalupdates().get(Script.getTotalupdates().size() - 1).getFl() + "\" Sl: \"" + Script.getTotalupdates().get(Script.getTotalupdates().size() - 1).getSl() + "\"");
    }


    //this file will init and set the Listview to the total updates that was read from Json
    //it will also set the appid to only accept numbers and if loaded is not null, it will leave it empty
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ImageView imageView = new ImageView(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/icon/settingsImage.png")).toExternalForm());
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
        appID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (!newValue.matches("\\d*")) {
                appID.setText(newValue.replaceAll("\\D", ""));
                return;
            }
            if(anchorRoot.getChildren().contains(invalidAppID)) anchorRoot.getChildren().remove(invalidAppID);
        });
        appID.setText(Settings.getDiscordAPIKey());


        try {
            //set the timestamp mode
            switch (Script.getTimestampmode()) {
                case applaunch:
                    appLaunch.setSelected(true);
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
                    appLaunch.setSelected(true);
                    Script.setTimestampmode(TimeStampMode.applaunch);

            }
            displayUpdates.getItems().clear();
            for (int i = 0; i < Script.getTotalupdates().size(); i++) {
                displayUpdates.getItems().add("Fl: \"" + Script.getTotalupdates().get(i).getFl() + "\" Sl: \"" + Script.getTotalupdates().get(i).getSl() + "\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        displayUpdates.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //check if the list is double-clicked
        displayUpdates.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    if (!((displayUpdates.getSelectionModel().getSelectedIndex()) == -1)) {
                        showListConfig(displayUpdates.getSelectionModel().getSelectedIndex(), displayUpdates.getScene().getWindow().getX(), displayUpdates.getScene().getWindow().getY());
                    }
                }
            }
        });
    }

    //this will open up a new window and edit the arraylist
    private void showListConfig(int numberInList, double x, double y) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/EditListScript.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Settings.getTheme().getThemepass())).toExternalForm());
            EditListController ec = loader.getController();
            ec.setnumberInList(numberInList);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/lee/aspect/dev/icon/settingsImage.png"))));
            stage.setTitle("Config Editor - index: " + (numberInList + 1));
            stage.setScene(new Scene(root));
            stage.setX(x);
            stage.setY(y);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void invalidDiscordAppID(String msg){
        appID.setBackground(new Background(new BackgroundFill(Color.rgb(204,51,0,0.9), new CornerRadii(5), Insets.EMPTY)));
        if(!anchorRoot.getChildren().contains(invalidAppID)){
            invalidAppID = WarningManager.setWarning(appID,16,msg, WarningManager.Mode.Left);
            anchorRoot.getChildren().add(invalidAppID);
        }
        new Shake(anchorRoot).play();
    }


}
