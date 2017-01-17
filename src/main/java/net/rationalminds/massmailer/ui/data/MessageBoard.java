/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.ui.data;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.rationalminds.massmailer.utils.Constants;
import net.rationalminds.massmailer.utils.Utilities;

/**
 *
 * @author Vaibhav Singh
 */
public class MessageBoard {

    private static final StringProperty message = new SimpleStringProperty("");

    private static final StringBuffer stringBuffer = new StringBuffer();

    private static long lastUpdateTime = System.currentTimeMillis();

    private static int lineCount = 0;

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
        lineCount++;
        stringBuffer.append(Utilities.currentTimestamp() + message);
        stringBuffer.append(System.lineSeparator());
        if (System.currentTimeMillis() - lastUpdateTime > 100) {
            String currentMsg = this.message.get();
            if (lineCount > Constants.MSG_BOARD_MAX_LINES) {
                currentMsg = currentMsg.substring(Utilities.nthOccurrence(currentMsg, (char) 10, lineCount));
            }
            String text = currentMsg + stringBuffer.toString();
            Platform.runLater(() -> this.message.setValue(text));
            stringBuffer.setLength(0);
            lastUpdateTime = System.currentTimeMillis();
        }
    }

}
