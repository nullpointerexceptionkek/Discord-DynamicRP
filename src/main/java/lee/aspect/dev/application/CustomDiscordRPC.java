package lee.aspect.dev.application;
	
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lee.aspect.dev.discordrpc.settings.Settings;

import javax.swing.*;

/**
 * This class manages the default interface option and System Tray
 */
public class CustomDiscordRPC extends Application {
	public static Stage primaryStage;

	/**
	 * Launches the config interface
	 * This method should be only called by javaFX
	 * inits {@link RunLoopManager}
	 * @param pstage
	 */
	@Override
	public void start(Stage pstage) {
		try {
			primaryStage= pstage;
			RunLoopManager.init();
			Parent root = FXMLLoader.load(getClass().getResource("/lee/aspect/dev/ReadyConfig.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource(Settings.getTheme().Themepass()).toExternalForm());
			primaryStage.setTitle("Custom Discord RPC");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setOnCloseRequest((event)->{
				event.consume();
				onclose();
			});
			primaryStage.show();
		} catch(Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			System.out.println(sStackTrace);
		    String message = "\"Custom Discord RP\"\n"
		            + "it looks like we cannot connect to javaFX\n"
		            + "Please check if javaFX is installed";
		        JOptionPane.showMessageDialog(new JFrame(), message, "Error",
		            JOptionPane.ERROR_MESSAGE);
		    System.exit(1);
		       
		}
		
		
	}

	/**
	 * This is the "main" function of the program, this method is getting called on start up
	 * Sets the basic property of JavaFX and calls {@link #start(Stage)} to Launch the application interface
	 * @param args
	 */
	public static void Launch(String[] args) {
		Platform.setImplicitExit(false);
		new ApplicationTray();
		System.out.println(Arrays.toString(args));
		launch(args);
		//check if already running
		/*
		ServerSocket ss;
		 ss = null;
		    try {
		        ss = new ServerSocket(1044);
		        launch(args);
		    } catch (IOException e) {
		        System.err.println("Application already running!");
			    String message = "\"Custom Discord RP\"\n"
			            + "It looks like you are trying to create\n"
			            + "mutiple instance of the program";
			        JOptionPane.showMessageDialog(new JFrame(), message, "Application Running",
			            JOptionPane.ERROR_MESSAGE);
			    System.exit(1);
		    }

		 */
	}
	@Override
	public void stop() throws Exception {
		RunLoopManager.onClose();
	}

	/**
	 * This method is the method that the program calls when the exit button is hit
	 * it will detect whether if System Tray is supported on the system and gives you
	 * an option to minimize to System Tray when it is supported
	 * System Tray is defined in {@link ApplicationTray}
	 */
	public void onclose() {
		if(!SystemTray.isSupported()) {
			RunLoopManager.onClose();
			return;
		}
		var alert = new Alert(Alert.AlertType.CONFIRMATION);
		ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
		alert.getButtonTypes().setAll(yesButton, noButton);
		alert.setTitle("Close");
		alert.setHeaderText("SystemTray is supported");
		alert.setContentText("minimize to System tray instead of being closed?");
		ButtonType result = alert.showAndWait().get();

		if(result.equals(yesButton)) {
			primaryStage.close();
		} else if(result.equals(noButton)){
			RunLoopManager.onClose();
	}
		}


}
