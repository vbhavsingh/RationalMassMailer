/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import net.rationalminds.massmailer.ui.data.MessageBoard;

/**
 *
 * @author Vaibhav Singh
 */
public class MessageBoardController implements Initializable {

    private final MessageBoard messageBoard = MessageBoard.getMessageBoard();

    @FXML
    TextArea message;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        message.textProperty().bindBidirectional(messageBoard.getMessageProperty());
        messageBoard.getMessageProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                message.positionCaret(message.getText().length());
            }

        });
    }

}
