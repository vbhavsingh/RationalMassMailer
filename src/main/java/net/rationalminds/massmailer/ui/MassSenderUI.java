/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.rationalminds.massmailer.utils.Constants;

/**
 *
 * @author Vaibhav Singh
 */
public class MassSenderUI extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        Constants.THIS_APP = this;
        Parent root = null;
        try {
            URL url;
            url = getClass().getResource("../../../../fxml/application.fxml");
            if (url == null) {
                url = getClass().getResource("/fxml/application.fxml");
            }
            root = FXMLLoader.load(url);
        } catch (IOException ex) {
            System.out.println(ex.getCause());
            System.exit(0);
        }
        Scene scene = new Scene(root, 800, 500);
        stage.setTitle("RationalMassMailer v1.1 - Send individual mails in mass with Gmail and Yahoo");
        stage.setScene(scene);
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.show();
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                stage.close();
                System.exit(0);
            }
        });
    }
    
}
