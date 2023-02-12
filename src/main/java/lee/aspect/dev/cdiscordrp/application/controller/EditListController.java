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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lee.aspect.dev.cdiscordrp.manager.ConfigSceneManager;
import lee.aspect.dev.cdiscordrp.application.core.CustomDiscordRPC;
import lee.aspect.dev.cdiscordrp.application.core.Script;
import lee.aspect.dev.cdiscordrp.application.core.Updates;
import lee.aspect.dev.cdiscordrp.language.LanguageManager;
import lee.aspect.dev.cdiscordrp.util.WarningManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditListController extends ConfigController implements Initializable {

    Stage stage;
    @FXML
    private TextField Wait;
    @FXML
    private TextField image;
    @FXML
    private TextField imagetext;
    @FXML
    private TextField smallimage;
    @FXML
    private TextField smalltext;
    @FXML
    private TextField firstline;
    @FXML
    private TextField secondline;
    @FXML
    private TextField button1Text;
    @FXML
    private TextField button1Url;
    @FXML
    private TextField button2Text;
    @FXML
    private TextField button2Url;
    @FXML
    private Button CancelButton;
    @FXML
    private Button SaveButton;
    @FXML
    private Button DeleteButton;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label EditConfiLabel, FirstLineLabel, SecondLineLabel, DelayLabel, LargeImgLabel, SmallImgLabel,
            SmallImgTxtLabel, LargeImgTxtLabel, Button1Label, Button1LinkLabel, Button2TxtLabel, Button2LinkLabel;

    private ImageView delayTooSmall;

    private int numberInList = -1;

    public void cancelSaves() throws IOException {
        stage = (Stage) anchorPane.getScene().getWindow();
        gobacktoConfig();
    }

    public void saveChanges() throws IOException {
        Script.getINSTANCE().setUpdates(numberInList, new Updates(Long.parseLong(Wait.getText()), image.getText(), imagetext.getText(), smallimage.getText(),
                smalltext.getText(), firstline.getText(), secondline.getText(), button1Text.getText(),
                button1Url.getText(), button2Text.getText(), button2Url.getText()));
        gobacktoConfig();
    }

    public void deleteThisItem() throws IOException {
        Script.getINSTANCE().getTotalupdates().remove(numberInList);
        gobacktoConfig();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        //set up the languages
        EditConfiLabel.setText(LanguageManager.getLang().getString("EditConfig"));
        FirstLineLabel.setText(LanguageManager.getLang().getString("FirstLine"));
        SecondLineLabel.setText(LanguageManager.getLang().getString("SecondLine"));
        DelayLabel.setText(LanguageManager.getLang().getString("Delay"));
        LargeImgLabel.setText(LanguageManager.getLang().getString("LargeImage"));
        SmallImgLabel.setText(LanguageManager.getLang().getString("SmallImage"));
        SmallImgTxtLabel.setText(LanguageManager.getLang().getString("SmallImageText"));
        LargeImgTxtLabel.setText(LanguageManager.getLang().getString("LargeImageText"));
        Button1Label.setText(LanguageManager.getLang().getString("Button1Text"));
        Button1LinkLabel.setText(LanguageManager.getLang().getString("Button1Link"));
        Button2TxtLabel.setText(LanguageManager.getLang().getString("Button2Text"));
        Button2LinkLabel.setText(LanguageManager.getLang().getString("Button2Link"));
        CancelButton.setText(LanguageManager.getLang().getString("Cancel"));
        SaveButton.setText(LanguageManager.getLang().getString("Save"));
        DeleteButton.setText(LanguageManager.getLang().getString("Delete"));


        Wait.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                Wait.setText(newValue.replaceAll("\\D", ""));
            if (Wait.getText().isEmpty()) return;
            if (Long.parseLong(Wait.getText()) < 16000) {
                if (!anchorPane.getChildren().contains(delayTooSmall)) {
                    delayTooSmall =
                            WarningManager.setWarning(Wait, 12, "It is recommended to set the delay above 16 second", WarningManager.Mode.Up, 60, 2);
                    anchorPane.getChildren().add(delayTooSmall);
                }
            } else anchorPane.getChildren().remove(delayTooSmall);


        });
    }

    public void setnumberInList(int numberInList) {
        this.numberInList = numberInList;
        Wait.setText(String.valueOf(Script.getINSTANCE().getTotalupdates().get(numberInList).getWait()));
        image.setText(Script.getINSTANCE().getTotalupdates().get(numberInList).getImage());
        imagetext.setText(Script.getINSTANCE().getTotalupdates().get(numberInList).getImagetext());
        smallimage.setText(Script.getINSTANCE().getTotalupdates().get(numberInList).getSmallimage());
        smalltext.setText(Script.getINSTANCE().getTotalupdates().get(numberInList).getSmalltext());
        firstline.setText(Script.getINSTANCE().getTotalupdates().get(numberInList).getFl());
        secondline.setText(Script.getINSTANCE().getTotalupdates().get(numberInList).getSl());
        button1Text.setText(Script.getINSTANCE().getTotalupdates().get(numberInList).getButton1Text());
        button1Url.setText(Script.getINSTANCE().getTotalupdates().get(numberInList).getButton1Url());
        button2Text.setText(Script.getINSTANCE().getTotalupdates().get(numberInList).getButton2Text());
        button2Url.setText(Script.getINSTANCE().getTotalupdates().get(numberInList).getButton2Url());

    }

    private void gobacktoConfig() throws IOException {
        stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
        CustomDiscordRPC.primaryStage.setScene(new Scene(ConfigSceneManager.getDefaultConfigParent()));
        numberInList = -1;
    }


}