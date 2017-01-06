/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.gmailgreetingssender.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rationalminds.gmailgreetingssender.utils.Constants;

/**
 *
 * @author Vaibhav Singh
 */
public class MassSenderUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Constants.THIS_APP = this;
        Parent root = FXMLLoader.load(getClass().getResource("../../../../fxml/application.fxml"));
        Scene scene = new Scene(root, 800, 500);
        stage.setTitle("RationalMassMailer - Send individual mails in mass with Gmail");
        stage.setScene(scene);
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.show();
    }

}
