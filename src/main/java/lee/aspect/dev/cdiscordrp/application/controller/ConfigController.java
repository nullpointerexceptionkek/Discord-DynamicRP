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

package lee.aspect.dev.cdiscordrp.application.controller;


import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lee.aspect.dev.cdiscordrp.Launch;
import lee.aspect.dev.cdiscordrp.manager.UndoRedoManager;
import lee.aspect.dev.cdiscordrp.animatefx.FadeIn;
import lee.aspect.dev.cdiscordrp.animatefx.FadeOut;
import lee.aspect.dev.cdiscordrp.animatefx.Shake;
import lee.aspect.dev.cdiscordrp.animatefx.SlideInLeft;
import lee.aspect.dev.cdiscordrp.application.core.RunLoopManager;
import lee.aspect.dev.cdiscordrp.application.core.Script;
import lee.aspect.dev.cdiscordrp.application.core.Settings;
import lee.aspect.dev.cdiscordrp.application.core.Updates;
import lee.aspect.dev.cdiscordrp.language.LanguageManager;
import lee.aspect.dev.cdiscordrp.json.loader.FileManager;
import lee.aspect.dev.cdiscordrp.util.WarningManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
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
    private Button callbackButton;
    @FXML
    private Button settingButton;
    @FXML
    private RadioButton appLaunch, local, cdFomEndDay, sinceUpdate, none, custom;
    @FXML
    private TextField CustomTimeInput;
    @FXML
    private AnchorPane anchorRoot;
    @FXML
    private StackPane stackPane;
    @FXML
    private Label Application_IDLabel, TimeStampMethodLabel, UpdateModeLabel;
    @FXML
    private Button AddNewItemButton;
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
            invalidDiscordAppID("Invalid Application ID");
            return;
        }
        Script.getINSTANCE().setCustomTimestamp(CustomTimeInput.getText());
        Script.getINSTANCE().setUpdateType(updateMode.getValue());
        callbackButton.setDisable(true);
        Script.getINSTANCE().setDiscordAPIKey(DiscordAppID);
        Settings.saveSettingToFile();
        RunLoopManager.saveScripToFile();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/cdiscordrp/scenes/LoadingScreen.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Settings.getINSTANCE().getTheme().getPath())).toExternalForm());
        FadeOut fadeOut = new FadeOut(anchorRoot);
        fadeOut.setOnFinished((actionEvent -> {
            stackPane.getChildren().remove(anchorRoot);
            FadeIn fadeIn = new FadeIn(root);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/cdiscordrp/scenes/Settings.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Settings.getINSTANCE().getTheme().getPath())).toExternalForm());
        stackPane.getChildren().add(root);
        SlideInLeft animation = new SlideInLeft(root);
        animation.setOnFinished((actionEvent) -> stackPane.getChildren().remove(anchorRoot));
        animation.play();

    }

    public void addNewItem() {
        if (anchorRoot.getChildren().contains(invalidIndex)) {
            anchorRoot.getChildren().remove(invalidIndex);
            displayUpdates.setBackground(null);
        }
        int index = Script.getINSTANCE().getTotalupdates().size() - 1;
        if (displayUpdates.getItems().size() > 0)
            Script.getINSTANCE().addUpdates(new Updates((Script.getINSTANCE().getTotalupdates().get(index).getWait()),
                    String.valueOf(index),
                    Script.getINSTANCE().getTotalupdates().get(index).getImagetext(),
                    Script.getINSTANCE().getTotalupdates().get(index).getSmallimage()
                    , Script.getINSTANCE().getTotalupdates().get(index).getSmalltext(),
                    "First line " + index, "Second line " + index,
                    Script.getINSTANCE().getTotalupdates().get(index).getButton1Text(),
                    Script.getINSTANCE().getTotalupdates().get(index).getButton1Url(),
                    Script.getINSTANCE().getTotalupdates().get(index).getButton2Text(),
                    Script.getINSTANCE().getTotalupdates().get(index).getButton2Url()));

        else
            Script.getINSTANCE().addUpdates(new Updates(16000, String.valueOf(index), "" + index, "", "", "First line ", "Second line " + index));
        refreshList();
        undoRedoManager.modifyList(Script.getINSTANCE().getTotalupdates());
    }


    //this file will init and set the Listview to the total updates that was read from Json
    //it will also set the appid to only accept numbers and if loaded is not null, it will leave it empty
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        //apply languages
        Application_IDLabel.setText(LanguageManager.getLang().getString("Application_ID"));
        TimeStampMethodLabel.setText(LanguageManager.getLang().getString("TimeStampMethod"));
        UpdateModeLabel.setText(LanguageManager.getLang().getString("UpdateMode"));
        //radio buttons
        appLaunch.setText(LanguageManager.getLang().getString("appLaunch"));
        local.setText(LanguageManager.getLang().getString("local"));
        cdFomEndDay.setText(LanguageManager.getLang().getString("cdFomEndDay"));
        sinceUpdate.setText(LanguageManager.getLang().getString("sinceUpdate"));
        none.setText(LanguageManager.getLang().getString("none"));
        custom.setText(LanguageManager.getLang().getString("custom"));
        //buttons
        AddNewItemButton.setText(LanguageManager.getLang().getString("addNewItemButton"));
        callbackButton.setText(LanguageManager.getLang().getString("callbackButton"));
        settingButton.setText(LanguageManager.getLang().getString("settingButton"));
        //text-fields
        appIDTextField.setPromptText(LanguageManager.getLang().getString("DiscordAppIDInput"));
        CustomTimeInput.setPromptText(LanguageManager.getLang().getString("CustomTimeInput"));


        ImageView imageView = new ImageView(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/cdiscordrp/icon/settingsImage.png")).toExternalForm());
        imageView.setFitHeight(25);
        imageView.setPreserveRatio(true);
        settingButton.setGraphic(imageView);
        settingButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
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
            refreshList();
        });
        redoItem.setOnAction((actionEvent) -> {

            undoRedoManager.redo();
            refreshList();
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
                    refreshList();
                    undoRedoManager.modifyList(Script.getINSTANCE().getTotalupdates());
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        deleteItem.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                ObservableList<Integer> selectedIndices = displayUpdates.getSelectionModel().getSelectedIndices();
                for (int i = 0; i < selectedIndices.size(); i++) {
                    Script.getINSTANCE().getTotalupdates().remove(selectedIndices.get(i) - i);
                }
            }
            refreshList();
            undoRedoManager.modifyList(Script.getINSTANCE().getTotalupdates());
        });
        insertRowBelow.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                int index = displayUpdates.getSelectionModel().getSelectedIndex();
                Script.getINSTANCE().addUpdates(index + 1, new Updates(16000, String.valueOf(index), "" + index, "", "", "First line ", "Second line " + index));
            }
            refreshList();
            undoRedoManager.modifyList(Script.getINSTANCE().getTotalupdates());
        });
        insertRowAbove.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                int index = displayUpdates.getSelectionModel().getSelectedIndex();
                Script.getINSTANCE().addUpdates(index, new Updates(16000, String.valueOf(index), "" + index, "", "", "First line ", "Second line " + index));
            }
            refreshList();
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
        refreshList();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/cdiscordrp/scenes/EditListScript.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Settings.getINSTANCE().getTheme().getPath())).toExternalForm());
            EditListController ec = loader.getController();
            ec.setnumberInList(numberInList);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/lee/aspect/dev/cdiscordrp/icon/settingsImage.png"))));
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

    private void refreshList() {
        displayUpdates.getItems().clear();
        displayUpdates.getItems().addAll(Script.getINSTANCE().getTotalupdates());
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