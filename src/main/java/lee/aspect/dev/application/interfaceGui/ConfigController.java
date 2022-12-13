/*
 *
 * MIT License
 *
 * Copyright (c) 2022 lee
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

package lee.aspect.dev.application.interfaceGui;


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
import lee.aspect.dev.animationengine.animation.FadeIn;
import lee.aspect.dev.animationengine.animation.FadeOut;
import lee.aspect.dev.animationengine.animation.Shake;
import lee.aspect.dev.animationengine.animation.SlideInLeft;
import lee.aspect.dev.application.RunLoopManager;
import lee.aspect.dev.discordrpc.Script;
import lee.aspect.dev.discordrpc.UpdateManager;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.jsonreader.FileManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

public class ConfigController implements Initializable {
    @FXML
    private ToggleGroup timeStampMode;

    @FXML
    private ChoiceBox<Script.UpdateType> updateMode;
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
    private RadioButton appLaunch,local, cdFomEndDay, sinceUpdate, none, custom;

    @FXML
    private TextField CustomTimeInput;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane stackPane;

    private ImageView invalidIndex;

    private ImageView invalidAppID;

    //private int index = -1;

    public void getTimeStampMode() {
        RadioButton selectedRadioButton = (RadioButton) timeStampMode.getSelectedToggle();
        String toggleGroupValue = selectedRadioButton.getId();
        switch (toggleGroupValue){
            case "appLaunch":
                UpdateManager.SCRIPT.setTimestampmode(Script.TimeStampMode.appLaunch);
                break;
            case "local":
                UpdateManager.SCRIPT.setTimestampmode(Script.TimeStampMode.localTime);
                break;
            case "sinceUpdate":
                UpdateManager.SCRIPT.setTimestampmode(Script.TimeStampMode.sinceUpdate);
                break;
            case "cdFomEndDay":
                UpdateManager.SCRIPT.setTimestampmode(Script.TimeStampMode.cdFromDayEnd);
                break;
            case "custom":
                UpdateManager.SCRIPT.setTimestampmode(Script.TimeStampMode.custom);
                break;
            case "none":
                UpdateManager.SCRIPT.setTimestampmode(Script.TimeStampMode.none);
                break;
        }
    }

    public void switchToCallBack() throws IOException {
        String DiscordAppID = appID.getText();
        if (displayUpdates.getItems().size() < 1) {
            displayUpdates.setBackground(new Background(new BackgroundFill(Color.rgb(204, 51, 0, 0.9), new CornerRadii(5), Insets.EMPTY)));
            if (!anchorRoot.getChildren().contains(invalidIndex)) {
                invalidIndex = WarningManager.setWarning(displayUpdates, 16, "Index must be greater than one", WarningManager.Mode.Left);
                anchorRoot.getChildren().add(invalidIndex);
            }
            new Shake(anchorRoot).play();
            return;
        }
        if (DiscordAppID.isEmpty()) {
            invalidDiscordAppID("Invalid Application ID");
            return;
        }
        UpdateManager.SCRIPT.setCustomTimestamp(CustomTimeInput.getText());
        UpdateManager.SCRIPT.setUpdateType(updateMode.getValue());
        callbackButton.setDisable(true);
        SettingManager.SETTINGS.setDiscordAPIKey(DiscordAppID);
        SettingManager.saveSettingToFile();
        RunLoopManager.saveScripToFile();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/LoadingScreen.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(SettingManager.SETTINGS.getTheme().getThemepass())).toExternalForm());
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/Settings.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(SettingManager.SETTINGS.getTheme().getThemepass())).toExternalForm());
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
        int index = UpdateManager.SCRIPT.getTotalupdates().size() - 1;
        if (UpdateManager.SCRIPT.getTotalupdates().size() > 0)
            UpdateManager.SCRIPT.addUpdates(new Updates((UpdateManager.SCRIPT.getTotalupdates().get(index).getWait()),
                    String.valueOf(index),
                    UpdateManager.SCRIPT.getTotalupdates().get(index).getImagetext(),
                    UpdateManager.SCRIPT.getTotalupdates().get(index).getSmallimage()
                    , UpdateManager.SCRIPT.getTotalupdates().get(index).getSmalltext(),
                    "First line " + index, "Second line " + index,
                    UpdateManager.SCRIPT.getTotalupdates().get(index).getButton1Text(),
                    UpdateManager.SCRIPT.getTotalupdates().get(index).getButton1Url(),
                    UpdateManager.SCRIPT.getTotalupdates().get(index).getButton2Text(),
                    UpdateManager.SCRIPT.getTotalupdates().get(index).getButton2Url()));

        else
            UpdateManager.SCRIPT.addUpdates(new Updates(16000, String.valueOf(index), "" + index, "", "", "First line ", "Second line " + index));
        displayUpdates.getItems().add("Fl: \"" + UpdateManager.SCRIPT.getTotalupdates().get(UpdateManager.SCRIPT.getTotalupdates().size() - 1).getFl() + "\" Sl: \"" + UpdateManager.SCRIPT.getTotalupdates().get(UpdateManager.SCRIPT.getTotalupdates().size() - 1).getSl() + "\"");
        UndoRedoManager.addUndo(new UndoRedoManager.UndoRedo(UndoRedoManager.Type.add, new Updates[]{UpdateManager.SCRIPT.getTotalupdates().get(index)}));
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
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem insertRowAbove = new MenuItem("Insert New Above");
        MenuItem insertRowBelow = new MenuItem("Insert New Below");
        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");

        updateMode.getItems().addAll(EnumSet.allOf(Script.UpdateType.class));
        updateMode.setValue(UpdateManager.SCRIPT.getUpdateType());

        copyItem.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                ObservableList<Integer> selectedIndices = displayUpdates.getSelectionModel().getSelectedIndices();
                Updates[] copiedItem = new Updates[selectedIndices.size()];
                for (int i = 0; i < selectedIndices.size(); i++) {
                    copiedItem[i] = UpdateManager.SCRIPT.getTotalupdates().get(selectedIndices.get(i));
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
                    UpdateManager.SCRIPT.getTotalupdates().addAll(selectedIndex, Arrays.asList(FileManager.readFromJson(data, Updates[].class)));
                    refreshDisplayUpdates();

                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        deleteItem.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                ArrayList<Updates> undoUpdates = new ArrayList<>();
                ObservableList<Integer> selectedIndices = displayUpdates.getSelectionModel().getSelectedIndices();
                for (int i = 0; i < selectedIndices.size(); i++) {
                    undoUpdates.add(UpdateManager.SCRIPT.getTotalupdates().get(selectedIndices.get(i) - i));
                    UpdateManager.SCRIPT.getTotalupdates().remove(selectedIndices.get(i) - i);
                }
                refreshDisplayUpdates();
                UndoRedoManager.addUndo(new UndoRedoManager.UndoRedo(UndoRedoManager.Type.remove, undoUpdates.toArray(new Updates[0])));
            }
        });
        insertRowBelow.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                int index = displayUpdates.getSelectionModel().getSelectedIndex();
                UpdateManager.SCRIPT.addUpdates(index + 1, new Updates(16000, String.valueOf(index), "" + index, "", "", "First line ", "Second line " + index));
                displayUpdates.getItems().add(index + 1, "Fl: \"" + UpdateManager.SCRIPT.getTotalupdates().get(index + 1).getFl() + "\" Sl: \"" + UpdateManager.SCRIPT.getTotalupdates().get(index + 1).getSl() + "\"");
                UndoRedoManager.addUndo(new UndoRedoManager.UndoRedo(UndoRedoManager.Type.add, new Updates[]{UpdateManager.SCRIPT.getTotalupdates().get(index)}));
            }
        });
        insertRowAbove.setOnAction((actionEvent) -> {
            if (displayUpdates.getSelectionModel().getSelectedIndex() != -1) {
                int index = displayUpdates.getSelectionModel().getSelectedIndex();
                UpdateManager.SCRIPT.addUpdates(index, new Updates(16000, String.valueOf(index), "" + index, "", "", "First line ", "Second line " + index));
                displayUpdates.getItems().add(index, "Fl: \"" + UpdateManager.SCRIPT.getTotalupdates().get(index).getFl() + "\" Sl: \"" + UpdateManager.SCRIPT.getTotalupdates().get(index).getSl() + "\"");
                UndoRedoManager.addUndo(new UndoRedoManager.UndoRedo(UndoRedoManager.Type.add, new Updates[]{UpdateManager.SCRIPT.getTotalupdates().get(index)}));
            }
        });

        undoItem.setOnAction((actionEvent) -> applyActionUndoRedo(false));
        redoItem.setOnAction((actionEvent) -> applyActionUndoRedo(true));


        contextMenu.getItems().addAll(copyItem, pasteItem, deleteItem);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(insertRowAbove, insertRowBelow);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(undoItem, redoItem);


        //TilePane tilePane = new TilePane(displayUpdates);
        displayUpdates.setContextMenu(contextMenu);


        appID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (!newValue.matches("\\d*")) {
                appID.setText(newValue.replaceAll("\\D", ""));
                return;
            }
            anchorRoot.getChildren().remove(invalidAppID);
        });
        appID.setText(SettingManager.SETTINGS.getDiscordAPIKey());

        CustomTimeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (!newValue.matches("\\d*")) {
                CustomTimeInput.setText(newValue.replaceAll("\\D", ""));
            }
        });
        CustomTimeInput.setText(UpdateManager.SCRIPT.getCustomTimestamp());


        try {
            //set the timestamp mode
            switch (UpdateManager.SCRIPT.getTimestampmode()) {
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
                    UpdateManager.SCRIPT.setTimestampmode(Script.TimeStampMode.appLaunch);

            }
            refreshDisplayUpdates();
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
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(SettingManager.SETTINGS.getTheme().getThemepass())).toExternalForm());
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

    private void refreshDisplayUpdates() {
        displayUpdates.getItems().clear();
        for (int i = 0; i < UpdateManager.SCRIPT.getTotalupdates().size(); i++) {
            displayUpdates.getItems().add("Fl: \"" + UpdateManager.SCRIPT.getTotalupdates().get(i).getFl() + "\" Sl: \"" + UpdateManager.SCRIPT.getTotalupdates().get(i).getSl() + "\"");
        }
    }

    public void invalidDiscordAppID(String msg) {
        appID.setBackground(new Background(new BackgroundFill(Color.rgb(204, 51, 0, 0.9), new CornerRadii(5), Insets.EMPTY)));
        if (!anchorRoot.getChildren().contains(invalidAppID)) {
            invalidAppID = WarningManager.setWarning(appID, 16, msg, WarningManager.Mode.Left);
            anchorRoot.getChildren().add(invalidAppID);
        }
        new Shake(anchorRoot).play();
    }

    private void applyActionUndoRedo(boolean type) {
        UndoRedoManager.UndoRedo action = type ? UndoRedoManager.getRedo() : UndoRedoManager.getUndo();
        if (action == null) return;
        if (action.type.equals(UndoRedoManager.Type.remove)) {
            for (Updates updates : action.updates) {
                UpdateManager.SCRIPT.getTotalupdates().remove(updates);
            }
        } else {
            for (Updates updates : action.updates) {
                UpdateManager.SCRIPT.getTotalupdates().add(updates);
            }
        }
        refreshDisplayUpdates();
    }

    private static class UndoRedoManager {
        private static final UndoRedo[] undo = new UndoRedo[10];
        private static final UndoRedo[] redo = new UndoRedo[10];
        private static int undoIndex = 0;
        private static int redoIndex = 0;

        public static void addUndo(UndoRedo undo) {
            UndoRedoManager.undo[undoIndex] = undo;
            undoIndex++;
            if (undoIndex == 10) {
                undoIndex = 0;
            }
        }

        public static void addRedo(UndoRedo redo) {
            UndoRedoManager.redo[redoIndex] = redo;
            redoIndex++;
            if (redoIndex == 10) {
                redoIndex = 0;
            }
        }

        public static UndoRedo getUndo() {
            undoIndex--;
            if (undoIndex < 0) {
                undoIndex = 9;
            }
            UndoRedo add = undo[undoIndex];
            if (add!=null) {
                add.type = add.type == Type.add ? Type.remove : Type.add;
                addRedo(add);
            }
            //System.out.println("Undo: " + undo[undoIndex].type + " " + Arrays.toString(undo[undoIndex].updates));
            return undo[undoIndex];
        }

        public static UndoRedo getRedo() {
            redoIndex--;
            if (redoIndex < 0) {
                redoIndex = 9;
            }
            UndoRedo add = undo[undoIndex];
            if (add != null) {
                add.type = add.type == Type.add ? Type.remove : Type.add;
                addUndo(add);
            }
            //System.out.println("Redo: " + redo[redoIndex].type + " " + Arrays.toString(redo[redoIndex].updates));
            return redo[redoIndex];
        }

        public enum Type {
            add, remove
        }

        private static class UndoRedo {
            public Type type;
            public final Updates[] updates;

            public UndoRedo(Type type, Updates[] updates) {
                this.type = type;
                this.updates = updates;
            }

        }
    }
}
