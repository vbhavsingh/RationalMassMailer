/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.utils;

import net.rationalminds.massmailer.ui.MassSenderUI;

/**
 *
 * @author Vaibhav Singh
 */
public class Constants {

    public static final String PERMITTED_EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@([Gg][Mm][Aa][Ii][Ll]|[Yy][Aa][Hh][Oo][Oo])(\\.[A-Za-z]{2,})$";

    public static final String EMAIL_REGEX_PATERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    public static MassSenderUI THIS_APP;

}
