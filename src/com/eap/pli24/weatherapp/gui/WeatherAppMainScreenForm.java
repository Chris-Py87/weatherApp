package com.eap.pli24.weatherapp.gui;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JOptionPane;


public class WeatherAppMainScreenForm extends javax.swing.JFrame {

    /**
     * Creates new form MainScreenForm
     */
    public WeatherAppMainScreenForm() {
        setTitle("Εφαρμογή Καιρού - Κεντρικό Μενού");
        initComponents();
        
        this.setLocationRelativeTo(null);
        
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("WeatherAppPU");
        EntityManager entityManager = factory.createEntityManager();

        //Ελεγχος της Σύνδεσης με την ΒΔ κατα την Έναρξη της Εφαρμογής
        try {
            Connection connection = entityManager.unwrap(Connection.class);
        } catch (Exception e) {

            String errorMessage = "<html><body>"
                    + "<p style='font-size:10px; font-weight:bold; color:red;'>Παρουσιάστηκε σφάλμα κατα την σύνδεση στην βάση δεδομένων.</p>"
                    + "<ul>"
                    + "<li>Βεβαιωθείτε ότι ο Derby Server τρέχει.</li>"
                    + "<li><p style='font-size:10px;'>Λεπτομέρειες σφάλματος:<br>" + e.getMessage() + "</p>"+"</li>"
                    + "</ul>"
                    + "<p style='font-size:12px;font-weight:bold; color:blue;'><u>Η Εφαρμογή θα Κλείσει!</u><br>" + "</p>"
                    + "</body></html>";
            //Εμφάνιση ενός Dialog με το Λάθος που προκληθηκε/
            JOptionPane.showMessageDialog(null, 
                                        errorMessage,
                                        "Πρόβλημα στην σύνδεση με την ΒΔ", 
                                         JOptionPane.ERROR_MESSAGE);
        
        }      
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        myMainScreenPanel = new javax.swing.JPanel();
        statisticsScreenButton = new javax.swing.JButton();
        historySearchScreenButton = new javax.swing.JButton();
        exitApplicationButton = new javax.swing.JButton();
        prognosisSearchScreenButton = new javax.swing.JButton();
        logoLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WeatherApp");
        setBackground(new java.awt.Color(102, 102, 255));

        myMainScreenPanel.setBackground(new java.awt.Color(231, 230, 230));
        myMainScreenPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        statisticsScreenButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        statisticsScreenButton.setText("Προβολή Στατιστικών");
        statisticsScreenButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        statisticsScreenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statisticsScreenButtonActionPerformed(evt);
            }
        });

        historySearchScreenButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        historySearchScreenButton.setText("Προβολή Ιστορικού Αναζητήσεων");
        historySearchScreenButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        historySearchScreenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historySearchScreenButtonActionPerformed(evt);
            }
        });

        exitApplicationButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        exitApplicationButton.setText("Έξοδος");
        exitApplicationButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        exitApplicationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitApplicationButtonActionPerformed(evt);
            }
        });

        prognosisSearchScreenButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        prognosisSearchScreenButton.setText("Αναζήτηση Πρόγνωσης");
        prognosisSearchScreenButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        prognosisSearchScreenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prognosisSearchScreenButtonActionPerformed(evt);
            }
        });

        logoLabel.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eap/pli24/weatherapp/resources/sun-day-weather-73146.png"))); // NOI18N
        logoLabel.setText("WeatherApp");

        javax.swing.GroupLayout myMainScreenPanelLayout = new javax.swing.GroupLayout(myMainScreenPanel);
        myMainScreenPanel.setLayout(myMainScreenPanelLayout);
        myMainScreenPanelLayout.setHorizontalGroup(
            myMainScreenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statisticsScreenButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(historySearchScreenButton, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
            .addComponent(exitApplicationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(prognosisSearchScreenButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(myMainScreenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        myMainScreenPanelLayout.setVerticalGroup(
            myMainScreenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, myMainScreenPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(logoLabel)
                .addGap(28, 28, 28)
                .addComponent(prognosisSearchScreenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(historySearchScreenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statisticsScreenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exitApplicationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(myMainScreenPanel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void exitApplicationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitApplicationButtonActionPerformed
        closeWeatherApplication();
    }//GEN-LAST:event_exitApplicationButtonActionPerformed

    
    /**
     * Με το πάτημα του κουμπιού μεταβαίνουμε στην οθόνη για την αναζήτηση του καιρού σε κάποια πόλη
     * @param evt 
     */
    private void prognosisSearchScreenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prognosisSearchScreenButtonActionPerformed
        
        //Δημιουργία ενός αντικειμένου της οθόνης αναζήτησης πρόγνωσης καιρού
        CitySearchScreenForm prognosisSearch = new CitySearchScreenForm();
        //Εμφανίζουμε την οθόνη πρόγνωσης καιρού
        prognosisSearch.setVisible(true);
        
    }//GEN-LAST:event_prognosisSearchScreenButtonActionPerformed

    private void historySearchScreenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historySearchScreenButtonActionPerformed
        //Δημιουργία ενός αντικειμένου της οθόνης Προβολής Ιστορικού
        CityForecastHistoryForm historyForm = new CityForecastHistoryForm();
        //Εμφανίζουμε την οθόνη Προβολής Ιστορικού
        historyForm.setVisible(true);
    }//GEN-LAST:event_historySearchScreenButtonActionPerformed

    private void statisticsScreenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statisticsScreenButtonActionPerformed
        //Δημιουργία ενός αντικειμένου της οθόνης Προβολής Ιστορικού
        CityForecastStatisticsForm statsForm = new CityForecastStatisticsForm();
        //Εμφανίζουμε την οθόνη Προβολής Ιστορικού
        statsForm.setVisible(true);
    }//GEN-LAST:event_statisticsScreenButtonActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exitApplicationButton;
    private javax.swing.JButton historySearchScreenButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel myMainScreenPanel;
    private javax.swing.JButton prognosisSearchScreenButton;
    private javax.swing.JButton statisticsScreenButton;
    // End of variables declaration//GEN-END:variables


    
    /**
     * Κλείσιμο Εφαρμογής με Dialog Επιλογής και κλείσιμο της Βάσης
     */  
    private void closeWeatherApplication(){            
        Object[] options = {"Όχι", "Ναι"};
        int userSelection = JOptionPane.showOptionDialog(null,
                "<html><body><p style='font-size:14px; font-weight:normal; color:black;padding: 5px; margin: 5px;'>Θέλετε σίγουρα να κλείσετε την εφαρμογή; </p></body></html>",
                "weatherApp",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (userSelection == 1) {
            try {
                //Stop DerbyDB engine
                Process process = Runtime.getRuntime().exec("cmd /c start /b stopNetworkServer.bat", null, new File("db"));
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }finally {            
                System.exit(0);
            }                        
        }        
    }


}
