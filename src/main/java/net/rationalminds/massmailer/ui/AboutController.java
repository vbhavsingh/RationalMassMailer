/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.ui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import net.rationalminds.massmailer.utils.Constants;

/**
 *
 * @author Vaibhav Singh
 */
public class AboutController implements Initializable {
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    protected void openLicenseLink(ActionEvent e) {
    	LOGGER.info("accessing link : http://www.binpress.com/license/view/l/b6b277ef9686a305a054919afab9ac18");
        Constants.THIS_APP.getHostServices().showDocument("http://www.binpress.com/license/view/l/b6b277ef9686a305a054919afab9ac18");
    }

    @FXML
    protected void openRationalMindsLink(ActionEvent e) {
    	LOGGER.info("accessing link : http://www.rationalminds.net");
        Constants.THIS_APP.getHostServices().showDocument("http://www.rationalminds.net");
    }

    @FXML
    protected void openDonateLink(ActionEvent e) {
    	LOGGER.info("accessing link : http://rationalminds.net/#/donate");
        Constants.THIS_APP.getHostServices().showDocument("http://rationalminds.net/#/donate");
    }

    @FXML
    protected void openSourceCodeLink(ActionEvent e) {
    	LOGGER.info("accessing link : https://github.com/vbhavsingh/RationalMassMailer");
        Constants.THIS_APP.getHostServices().showDocument("https://github.com/vbhavsingh/RationalMassMailer");
    }
}
