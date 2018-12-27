package net.rationalminds.massmailer.ui;

import javafx.stage.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.web.*;
import javafx.event.Event;
import javafx.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;

import javax.swing.event.DocumentEvent.EventType;

import javafx.geometry.Orientation;

public class MassEmailHtmlEditor extends HTMLEditor{
	public static final String TOP_TOOLBAR = ".top-toolbar";
    public static final String BOTTOM_TOOLBAR = ".bottom-toolbar";
    public static final String WEB_VIEW = ".web-view";
    private static final String IMPORT_BUTTON_GENERAL = "/camera-flat.png";

    private final WebView mWebView;
    private final ToolBar mTopToolBar;
    private final ToolBar mBottomToolBar;
    private Button mImportFileButton;

    public MassEmailHtmlEditor() {
        mWebView = (WebView) this.lookup(WEB_VIEW);
        mTopToolBar = (ToolBar) this.lookup(TOP_TOOLBAR);
        mBottomToolBar = (ToolBar) this.lookup(BOTTOM_TOOLBAR);

        createCustomButtons();
        this.setHtmlText("<html />");
        
    }

    /**
     * Inserts HTML data after the current cursor. If anything is selected, they
     * get replaced with new HTML data.
     *
     * @param html HTML data to insert.
     */
    public void insertHtmlAfterCursor(String html) {
        //replace invalid chars
        html = html.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
        //get script
        String script = String.format(
                "(function(html) {"
                + "  var sel, range;"
                + "  if (window.getSelection) {"
                + "    sel = window.getSelection();"
                + "    if (sel.getRangeAt && sel.rangeCount) {"
                + "      range = sel.getRangeAt(0);"
                + "      range.deleteContents();"
                + "      var el = document.createElement(\"div\");"
                + "      el.innerHTML = html;"
                + "      var frag = document.createDocumentFragment(),"
                + "        node, lastNode;"
                + "      while ((node = el.firstChild)) {"
                + "        lastNode = frag.appendChild(node);"
                + "      }"
                + "      range.insertNode(frag);"
                + "      if (lastNode) {"
                + "        range = range.cloneRange();"
                + "        range.setStartAfter(lastNode);"
                + "        range.collapse(true);"
                + "        sel.removeAllRanges();"
                + "        sel.addRange(range);"
                + "      }"
                + "    }"
                + "  }"
                + "  else if (document.selection && "
                + "           document.selection.type != \"Control\") {"
                + "    document.selection.createRange().pasteHTML(html);"
                + "  }"
                + "})(\"%s\");", html);
        //execute script
        mWebView.getEngine().executeScript(script);
    }

    /**
     * Creates Custom ToolBar buttons and other controls
     */
    private void createCustomButtons() {
        //add embed file button  
        ImageView graphic = new ImageView(new Image(
                getClass().getResourceAsStream(IMPORT_BUTTON_GENERAL)));
        graphic.setFitWidth(18);
        graphic.setFitHeight(18);
        mImportFileButton = new Button("", graphic);
        mImportFileButton.setTooltip(new Tooltip("Insert Image"));
        mImportFileButton.setOnAction((event) -> onImportFileButtonAction());

        //add to top toolbar         
        mTopToolBar.getItems().add(mImportFileButton);
        mTopToolBar.getItems().add(new Separator(Orientation.VERTICAL));
    }

    /**
     * Action to do on Import Image button click
     */
    private void onImportFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file to import");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
        if (selectedFile != null) {
            importDataFile(selectedFile);
        }
    }

    /**
     * Imports an image file.
     *
     * @param file Image file.
     */
    private void importDataFile(File file) {
        try {
            //check if file is too big
            if (file.length() > 1024 * 1024) {
                throw new VerifyError("File is too big.");
            }
            //get mime type of the file
            String type = URLConnection.guessContentTypeFromName(file.getName());
            //get html content
            byte[] data = Files.readAllBytes(file.toPath());
           // byte[] data = org.apache.commons.io.FileUtils.readFileToByteArray(file);
            String base64data = java.util.Base64.getEncoder().encodeToString(data);
            String htmlData = String.format(
                    "<img src=\"data:%s;base64, %s\"/>",
                    type, base64data);
            //insert html
            insertHtmlAfterCursor(htmlData);
        } catch (IOException ex) {
           // Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
}
