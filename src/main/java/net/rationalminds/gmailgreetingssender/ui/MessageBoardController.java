/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.gmailgreetingssender.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import net.rationalminds.gmailgreetingssender.ui.data.MessageBoard;

/**
 *
 * @author Vaibhav Singh
 */
public class MessageBoardController implements Initializable {
    
    private final MessageBoard messageBoard = new MessageBoard();
    
    @FXML
    TextArea message;
    
    @Override    
    public void initialize(URL location, ResourceBundle resources) {
        message.textProperty().bindBidirectional(messageBoard.getMessageProperty());
    }
    
}
