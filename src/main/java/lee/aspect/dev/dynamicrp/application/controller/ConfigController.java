/*
 * 2022-
 * MIT License
 *
 * Copyright (c) 2023 lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lee.aspect.dev.dynamicrp.application.controller;


import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lee.aspect.dev.dynamicrp.Launch;
import lee.aspect.dev.dynamicrp.animatefx.*;
import lee.aspect.dev.dynamicrp.application.core.*;
import lee.aspect.dev.dynamicrp.json.loader.FileManager;
import lee.aspect.dev.dynamicrp.manager.SceneManager;
import lee.aspect.dev.dynamicrp.manager.SearchManager;
import lee.aspect.dev.dynamicrp.manager.UndoRedoManager;
import lee.aspect.dev.dynamicrp.util.WarningManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
    private final UndoRedoManager undoRedoManager = new UndoRedoManager(Script.getINSTANCE().getTotalupdates());
    @FXML
    protected ListView<Updates> displayUpdates;
    @FXML
    private ToggleGroup timeStampMode;
    @FXML
    private ChoiceBox<Script.UpdateType> updateMode;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField appIDTextField;
    @FXML
    private Button callbackButton, searchButton;
    @FXML
    private Button settingButton;
    @FXML
    private RadioButton appLaunch, local, cdFomEndDay, sinceUpdate, none, custom;
    @FXML
    private TextField CustomTimeInput;
    @FXML
    private AnchorPane anchorRoot;
    @FXML
    private Label Application_IDLabel, TimeStampMethodLabel, UpdateModeLabel;
    @FXML
    private Button AddNewItemButton;

    @FXML
    private StackPane stackPane;

    @FXML
    private VBox content;
    private ImageView invalidIndex;
    private ImageView invalidAppID;

    //private int index = -1;

    public void getTimeStampMode() {
        RadioButton selectedRadioButton = (RadioButton) timeStampMode.getSelectedToggle();
        String toggleGroupValue = selectedRadioButton.getId();
        switch (toggleGroupValue) {
            case "appLaunch":
                Script.getINSTANCE().setTimestampmode(Script.TimeStampMode.appLaunch);
                break;
            case "local":
                Script.getINSTANCE().setTimestampmode(Script.TimeStampMode.localTime);
                break;
            case "sinceUpdate":
                Script.getINSTANCE().setTimestampmode(Script.TimeStampMode.sinceUpdate);
                break;
            case "cdFomEndDay":
                Script.getINSTANCE().setTimestampmode(Script.TimeStampMode.cdFromDayEnd);
                break;
            case "custom":
                Script.getINSTANCE().setTimestampmode(Script.TimeStampMode.custom);
                break;
            case "none":
                Script.getINSTANCE().setTimestampmode(Script.TimeStampMode.none);
                break;
        }
    }

    public void saveCurrentScript(){
        Script.getINSTANCE().setCustomTimestamp(CustomTimeInput.getText());
        Script.getINSTANCE().setUpdateType(updateMode.getValue());
        Script.getINSTANCE().setDiscordAPIKey(appIDTextField.getText());
        Script.saveScriptToFile();
    }

    public void showSearch() {
        SearchManager.showDialog();
    }

    public void switchToCallBack() throws IOException {
        String DiscordAppID = appIDTextField.getText();
        if (displayUpdates.getItems().size() < 1) {
            displayUpdates.setBackground(new Background(new BackgroundFill(Color.rgb(204, 51, 0, 0.9), new CornerRadii(5), Insets.EMPTY)));
            if (!anchorRoot.getChildren().contains(invalidIndex)) {
                invalidIndex = WarningManager.setWarning(displayUpdates, 16, "Index must be greater than one", WarningManager.Mode.Left);
                anchorRoot.getChildren().add(invalidIndex);
            }
            new Shake(anchorRoot).play();
            return;
        }
        if (DiscordAppID == null || DiscordAppID.isEmpty()) {
            invalidDiscordAppID("Application ID is empty");
            return;
        }
        if (!(DiscordAppID.length() >= 16 && DiscordAppID.length() <= 32)) {
            //;)
            if (DiscordAppID.equals("123456789")) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            invalidDiscordAppID("Application ID is invalid");
            return;
        }
        Script.getINSTANCE().setCustomTimestamp(CustomTimeInput.getText());
        Script.getINSTANCE().setUpdateType(updateMode.getValue());
        callbackButton.setDisable(true);
        Script.getINSTANCE().setDiscordAPIKey(DiscordAppID);
        Settings.saveSettingToFile();
        Script.saveScriptToFile();
        SceneManager.SceneData sceneData = SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/dynamicrp/scenes/LoadingScreen.fxml");

        FadeOut fadeOut = new FadeOut(anchorRoot);
        fadeOut.setOnFinished((actionEvent -> {
            stackPane.getChildren().remove(anchorRoot);
            FadeIn fadeIn = new FadeIn(sceneData.getRoot());
            fadeIn.setOnFinished((actionEvent1) -> ((LoadingController) sceneData.getController()).toNewScene(LoadingController.Load.CallBackScreen));
            sceneData.getRoot().setOpacity(0);
            stackPane.getChildren().add(sceneData.getRoot());
            fadeIn.play();
        }));
        fadeOut.setSpeed(5);
        fadeOut.play();
    }

    public void switchToSetting() {
        settingButton.setDisable(true);
        saveCurrentScript();
        if(Settings.getINSTANCE().isAutoSwitch()){
            Parent root = SceneManager.getConfigParent();
            stackPane.getChildren().add(0,root);
            SlideOutUp animation = new SlideOutUp(anchorRoot);
            animation.setOnFinished((actionEvent) -> stackPane.getChildren().remove(anchorRoot));
            animation.play();
            return;
        }
        Parent root = SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/dynamicrp/scenes/Settings.fxml").getRoot();
        stackPane.getChildren().add(root);
        SlideInUp animation = new SlideInUp(root);
        animation.setOnFinished((actionEvent) -> stackPane.getChildren().remove(anchorRoot));
        animation.play();


    }

    public void addNewItem() {
        if (anchorRoot.getChildren().contains(invalidIndex)) {
            anchorRoot.getChildren().remove(invalidIndex);
            displayUpdates.setBackground(null);
        }

        int index = Script.getINSTANCE().getTotalupdates().size() - 1;
        Updates newItem;

        if (displayUpdates.getItems().size() > 0) {
            Updates lastUpdate = Script.getINSTANCE().getTotalupdates().get(index);
            newItem = new Updates(
                    lastUpdate.getWait(),
                    String.valueOf(index),
                    lastUpdate.getImagetext(),
                    lastUpdate.getSmallimage(),
                    lastUpdate.getSmalltext(),
                    "First line " + index,
                    "Second line " + index,
                    lastUpdate.getButton1Text(),
                    lastUpdate.getButton1Url(),
                    lastUpdate.getButton2Text(),
                    lastUpdate.getButton2Url()
            );
        } else {
            newItem = new Updates(16000, String.valueOf(index), "" + index, "", "", "First line ", "Second line " + index);
        }

        Script.getINSTANCE().addUpdates(newItem);
        undoRedoManager.modifyList(Script.getINSTANCE().getTotalupdates());
    }



    //this file will initManagers and set the Listview to the total updates that was read from Json
    //it will also set the appid to only accept numbers and if loaded is not null, it will leave it empty
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        titleLabel.setText(Launch.NAME);
        ImageView settingimg;
        if(Settings.getINSTANCE().isAutoSwitch()){
            settingimg = new ImageView(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/dynamicrp/icon/back.png")).toExternalForm());
        } else{
            settingimg = new ImageView(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/dynamicrp/icon/settingsImage.png")).toExternalForm());
        }
        settingimg.setFitHeight(16);
        settingimg.setPreserveRatio(true);
        settingButton.setGraphic(settingimg);
        settingButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        ImageView searchimg = new ImageView(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/dynamicrp/icon/search.png")).toExternalForm());
        searchimg.setFitHeight(16);
        searchimg.setPreserveRatio(true);
        searchButton.setGraphic(searchimg);
        searchButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);


        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem insertRowAbove = new MenuItem("Insert New Above");
        MenuItem insertRowBelow = new MenuItem("Insert New Below");
        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");

        updateMode.getItems().addAll(EnumSet.allOf(Script.UpdateType.class));
        updateMode.setValue(Script.getINSTANCE().getUpdateType());

        undoItem.setOnAction((actionEvent) -> {
            undoRedoManager.undo();
        });
        redoItem.setOnAction((actionEvent) -> {

            undoRedoManager.redo();
        });

        copyItem.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                ObservableList<Integer> selectedIndices = displayUpdates.getSelectionModel().getSelectedIndices();
                Updates[] copiedItem = new Updates[selectedIndices.size()];
                for (int i = 0; i < selectedIndices.size(); i++) {
                    copiedItem[i] = Script.getINSTANCE().getTotalupdates().get(selectedIndices.get(i));
                }
                StringSelection stringSelection = new StringSelection(FileManager.toGson(copiedItem));
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);

            }
        });
        pasteItem.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                int selectedIndex = displayUpdates.getSelectionModel().getSelectedIndex();
                try {
                    String data = (String) Toolkit.getDefaultToolkit()
                            .getSystemClipboard().getData(DataFlavor.stringFlavor);
                    FileManager.readFromJson(data, Updates[].class);
                    Script.getINSTANCE().getTotalupdates().addAll(selectedIndex, Arrays.asList(FileManager.readFromJson(data, Updates[].class)));
                    undoRedoManager.modifyList(Script.getINSTANCE().getTotalupdates());
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        deleteItem.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                ObservableList<Integer> selectedIndices = displayUpdates.getSelectionModel().getSelectedIndices();
                for (int i = selectedIndices.size() - 1; i >= 0; i--) {
                    Script.getINSTANCE().getTotalupdates().remove(selectedIndices.get(i).intValue());
                }
            }
            undoRedoManager.modifyList(Script.getINSTANCE().getTotalupdates());
        });

        insertRowBelow.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                int index = displayUpdates.getSelectionModel().getSelectedIndex();
                Script.getINSTANCE().addUpdates(index + 1, new Updates(16000, String.valueOf(index), "" + index, "", "", "First line ", "Second line " + index));
            }
            undoRedoManager.modifyList(Script.getINSTANCE().getTotalupdates());
        });
        insertRowAbove.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                int index = displayUpdates.getSelectionModel().getSelectedIndex();
                Script.getINSTANCE().addUpdates(index, new Updates(16000, String.valueOf(index), "" + index, "", "", "First line ", "Second line " + index));
            }
            undoRedoManager.modifyList(Script.getINSTANCE().getTotalupdates());
        });


        contextMenu.getItems().addAll(copyItem, pasteItem, deleteItem);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(insertRowAbove, insertRowBelow);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(undoItem, redoItem);


        //TilePane tilePane = new TilePane(displayUpdates);
        displayUpdates.setContextMenu(contextMenu);


        appIDTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (!newValue.matches("\\d*")) {
                appIDTextField.setText(newValue.replaceAll("\\D", ""));
                return;
            }
            anchorRoot.getChildren().remove(invalidAppID);
        });
        appIDTextField.setText(Script.getINSTANCE().getDiscordAPIKey());

        CustomTimeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (!newValue.matches("\\d*")) {
                CustomTimeInput.setText(newValue.replaceAll("\\D", ""));
            }
        });
        CustomTimeInput.setText(Script.getINSTANCE().getCustomTimestamp());


        try {
            //set the timestamp mode
            switch (Script.getINSTANCE().getTimestampmode()) {
                case appLaunch:
                    appLaunch.setSelected(true);
                    break;

                case none:
                    none.setSelected(true);
                    break;

                case localTime:
                    local.setSelected(true);
                    break;

                case cdFromDayEnd:
                    cdFomEndDay.setSelected(true);
                    break;

                case sinceUpdate:
                    sinceUpdate.setSelected(true);
                    break;

                case custom:
                    custom.setSelected(true);
                    break;
                default:
                    appLaunch.setSelected(true);
                    Script.getINSTANCE().setTimestampmode(Script.TimeStampMode.appLaunch);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        displayUpdates.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Launch.LOGGER.debug(Script.getINSTANCE().getTotalupdates().toString());
        displayUpdates.setItems(Script.getINSTANCE().getTotalupdates());
        //check if the list is double-clicked
        displayUpdates.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    if (!((displayUpdates.getSelectionModel().getSelectedIndex()) == -1)) {
                        EditListController.showListConfig(displayUpdates.getSelectionModel().getSelectedIndex(), displayUpdates.getScene().getWindow().getX(), displayUpdates.getScene().getWindow().getY());
                    }
                }
            }
        });
    }

    public void invalidDiscordAppID(String msg) {
        appIDTextField.setBackground(new Background(new BackgroundFill(Color.rgb(204, 51, 0, 0.9), new CornerRadii(5), Insets.EMPTY)));
        if (!anchorRoot.getChildren().contains(invalidAppID)) {
            invalidAppID = WarningManager.setWarning(appIDTextField, 16, msg, WarningManager.Mode.Left);
            anchorRoot.getChildren().add(invalidAppID);
        }
        new Shake(anchorRoot).play();
    }

}
