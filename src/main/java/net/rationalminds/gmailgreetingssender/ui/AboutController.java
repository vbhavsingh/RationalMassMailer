/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.gmailgreetingssender.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import net.rationalminds.gmailgreetingssender.utils.Constants;

/**
 *
 * @author Vaibhav Singh
 */
public class AboutController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    protected void openLicenseLink(ActionEvent e) {
        Constants.THIS_APP.getHostServices().showDocument("http://www.binpress.com/license/view/l/b6b277ef9686a305a054919afab9ac18");
    }

    @FXML
    protected void openRationalMindsLink(ActionEvent e) {
        Constants.THIS_APP.getHostServices().showDocument("http://www.rationalminds.net");
    }

    @FXML
    protected void openDonateLink(ActionEvent e) {
        Constants.THIS_APP.getHostServices().showDocument("http://rationalminds.net/#/donate");
    }

    @FXML
    protected void openSourceCodeLink(ActionEvent e) {
        Constants.THIS_APP.getHostServices().showDocument("https://github.com/vbhavsingh/RationalMassMailer");
    }
}
