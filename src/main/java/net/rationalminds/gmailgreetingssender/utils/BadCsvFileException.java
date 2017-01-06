/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.gmailgreetingssender.utils;

/**
 *
 * @author Vaibhav Singh
 */
public class BadCsvFileException extends Exception {

    public BadCsvFileException() {
        super();
    }

    public BadCsvFileException(String string) {
        super(string);
    }

    public BadCsvFileException(Throwable thrwbl) {
        super(thrwbl);
    }

    public BadCsvFileException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

}
