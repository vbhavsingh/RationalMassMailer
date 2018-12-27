/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.ui.data;

import java.util.List;

/**
 *
 * @author Vaibhav Singh
 */
public class DynamicContactsFromCsv {

    private String headers[];

    private List<String[]> recepients;

    private String emailColumnHeaderName;

    private int emailColumnHeaderPosition;

    public DynamicContactsFromCsv(String[] headers, List<String[]> recepients) {
        this.headers = headers;
        this.recepients = recepients;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public List<String[]> getRecepients() {
        return recepients;
    }

    public void setRecepients(List<String[]> recepients) {
        this.recepients = recepients;
    }

    public String getEmailColumnHeaderName() {
        return emailColumnHeaderName;
    }

    public void setEmailColumnHeaderName(String emailColumnHeaderName) {
        this.emailColumnHeaderName = emailColumnHeaderName;
    }

    public int getEmailColumnHeaderPosition() {
        return emailColumnHeaderPosition;
    }

    public void setEmailColumnHeaderPosition(int emailColumnHeaderPosition) {
        this.emailColumnHeaderPosition = emailColumnHeaderPosition;
    }

}
