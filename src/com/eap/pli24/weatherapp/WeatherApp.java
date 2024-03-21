/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.eap.pli24.weatherapp;

import com.eap.pli24.weatherapp.gui.WeatherAppMainScreenForm;

public class WeatherApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Δημιουργία ενος αντικειμένου της κλασης WeatherAppMainScreenForm
        WeatherAppMainScreenForm menuScreen = new WeatherAppMainScreenForm();
        //Το θέλουμε να είναι εμφανίσιμο
        //Καθε φορά που θα ξεκινάει η εφαρμογή θα εμφανίζεται αυτό το παράθυρο.
        menuScreen.setVisible(true);

    }
    
}
