package net.rationalminds.massmailer.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import net.rationalminds.massmailer.utils.Utilities;

/**
 * Backs the Help tab's clipboard-icon button, which copies just the
 * template/image-bundle building instructions as plain text, so they can be
 * pasted elsewhere - e.g. into an AI assistant to ask questions about
 * building a template.
 */
public class HelpController implements Initializable {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String CLIPBOARD_ICON = "📋";

    @FXML
    Button copyToClipboardButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    protected void copyToClipboard(ActionEvent event) {
        try {
            String instructions = Utilities.readClasspathResource(getClass(), "/help/template-instructions.txt");
            ClipboardContent content = new ClipboardContent();
            content.putString(instructions);
            Clipboard.getSystemClipboard().setContent(content);

            copyToClipboardButton.setText("✓");
            javafx.animation.PauseTransition reset = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
            reset.setOnFinished(e -> copyToClipboardButton.setText(CLIPBOARD_ICON));
            reset.play();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Could not copy template instructions to clipboard: " + ex.getMessage());
            copyToClipboardButton.setText("✕");
        }
    }
}
