/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.gmailgreetingssender.ui.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.rationalminds.gmailgreetingssender.utils.Utilities;

/**
 *
 * @author Vaibhav Singh
 */
public class MessageBoard {

    private static final StringProperty message = new SimpleStringProperty("");

    public MessageBoard() {
    }

    public StringProperty getMessageProperty() {
        return message;
    }

    public String getMessage() {
        return message.get();
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public void appendMessage(String message) {
        message=Utilities.currentTimestamp() + message;
        String msg = this.message.get() +System.lineSeparator()+message;
        this.message.set(msg);
    }

}
