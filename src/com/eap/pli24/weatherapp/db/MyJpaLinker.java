/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eap.pli24.weatherapp.db;

import com.eap.pli24.weatherapp.db.exceptions.NonexistentEntityException;
import com.eap.pli24.weatherapp.json.MyHttpRequester;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;


public class MyJpaLinker {
      
    private Map<String, List<String>> cityMap = new HashMap<>();
    
    
    //Δήλωση Entity Manager και Controller και λοιπών πεδίων απαραίτητων για την διαχείριση της ΒΔ
    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("WeatherAppPU");
    private EntityManager entityManager = factory.createEntityManager();
    private EntityTransaction transaction = entityManager.getTransaction();
    private CityJpaController cityEntityJpaController = new CityJpaController(factory);
    private CityForecastJpaController cityForecastController = new CityForecastJpaController(factory);
    
    //Default Κατασκευαστής
    public MyJpaLinker(){
        
    }
    
    //Getters Section
    public EntityManagerFactory getFactory() {
        return factory;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public EntityTransaction getTransaction() {
        return transaction;
    }

    public CityJpaController getCityController() {
        return cityEntityJpaController;
    }

    public CityForecastJpaController getCityForecastController() {
        return cityForecastController;
    }
    
    //Custom Functions Section
    
    /**
     * Κατα την Αναζήτηση κάποια πόλης καλείται η εν λόγω μέθοδος η οποία εκτελεί
     * ενημέρωση του Πίνακα City στην ΒΔ με προσθήκη πόλης και αύξηση των προβολών σε περίπτωση που υπάρχει ήδη.
     * @param controller
     * @param cityNameStr 
     */
    public void updateCityTableInDatabase(CityJpaController controller, CityForecast cf){
        
        transaction.begin();
        
        try{
            
            Query aCityNameQuery = this.entityManager.createNamedQuery("City.findByName");

            aCityNameQuery.setParameter("name",cf.getCityname().getName());
            if(aCityNameQuery.getResultList().isEmpty()){
                try {
                   //Δημιουγία ενός αντικειμένου POJO της Entity Κλάσης City.
                    City city = new City(cf.getCityname().getName());
                    //Αυξάνουμε τον μετρητή των αναζητήσεων για την εν λόγω πόλη.
                    city.incrementCityForecastSearchViews();
                   //Εγγραφή της Πόλης στην ΒΔ στον Πίνακα City
                    controller.create(city);
               } catch (Exception ex) {
                   System.out.println("Error in Saving tuple at DB: " + ex.getMessage());
               }
            //Αν η πόλη υπάρχει ήδη στην ΒΔ τότε θα αυξήσουμε μόνο τον μετρητή των αναζητήσεων
            }else{               
               try {
                    City city = (City)aCityNameQuery.getSingleResult();
                    city.incrementCityForecastSearchViews();
                    controller.edit(city);
                } catch (NonexistentEntityException ex) {
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }           

        transaction.commit();
        }catch(Exception e){
            transaction.rollback();
            System.out.println("Σφάλμα κατα την Ενημέρωση του Πίνακα City: " + e.getMessage());
        }
        
    }

    /**
     * Παίρνει ως ορίσματα ενα αντικείμενο CityForecast και ένα string με το όνομα της πόλης
     * @param cf
     */
    public void saveCityForecastInDb(CityForecast cf) { 
        //Εναρξη της δοσοληψίας με την ΒΔ.
        transaction.begin();

        try{
            
            City cityEntityObj = null;
            //Δημιουργούμε ενα query για την λήψη απο την ΒΔ των εγγραφών με το όνομα της πόλης που δίνει ο χρήστης στο πλαίσιο 
            //κειμένου της αναζήτησης

            cityEntityObj = cf.getCityname();

            Query searchCityInDbQuery = entityManager.createNamedQuery("City.findByName");
            searchCityInDbQuery.setParameter("name", cf.getCityname().getName());
            try{
                //Εκτελείτε το ερώτημα και αποθηκεύεται το αποτέλεσμα σε μία μεταβλητη τύπου City. 
                //Επειδή η getSingleResult() επιστρέφει αντικείμενο τύπου Object γίνεται type casting.
                cityEntityObj = (City) searchCityInDbQuery.getSingleResult();
            }catch(Exception e){
                //Σε περίπτωση που η getSingleResult() δεν επιστρέψει κάποια εγγραφή
                //προκαλείται noResult Exception  
                System.out.println("Error at retrieving the resultListfrom DB: " + e.getMessage());
            }        

            //Δημιουργούμε ενα custom SQL query για την ανάληψη των δεδομένων απο την ΒΔ με κριτήριο το όνομα της πόλης και την ημερομηνία
            //Αν δεν βρεθουν αποτελέσματα θα αποθηκεύσουμε τα τρέχοντα δεδομένα αλλίως θα εμφανιστεί ενημερωτικό μύνημα με Dialog 
            //για την ύπαρξη των δεδομένων της τρέχουσας ημέρας ήδη στην ΒΔ
            Query searchByCityNameAndDateQuery = this.entityManager.createQuery("SELECT c FROM CityForecast c WHERE c.cityname = :cityname AND c.observationdate = :observationdate");
            //Θέτουμε τις παραμέτρους του query που ειναι το αντικείμενο τύπου City με τα αποτ και η τρέχουσα ημερομηνία
            searchByCityNameAndDateQuery.setParameter("cityname", cityEntityObj);
            searchByCityNameAndDateQuery.setParameter("observationdate", cf.getObservationdate());

            //Ελέγχουμε εάν υπάρχουν δεδομένα στην Database
            if (searchByCityNameAndDateQuery.getResultList().isEmpty()) {
                //Πέρνουμε τα δεδομένα από την φόρμα
                int tempC = cf.getTemperature();
                int humidity = cf.getHumidity();
                int windspeedkmph = cf.getWindspeed();
                int uvindex = cf.getUvindex();
                String weatherdesc = cf.getWeatherdescription();

                //Δημιουργούμε ένα αντικείμενο CityForecast με τα επιθυμητά προς αποθήκευση δεδομένα στην ΒΔ
                CityForecast cityForecast = new CityForecast(cf.getObservationdate(), tempC, humidity, windspeedkmph, uvindex, weatherdesc, cityEntityObj);

                //Με τον JPA  Controller της Entity Κλασης CityForecast στέλνουμε προς αποθήκευση τα επιθυμητά δεδομένα στην ΒΔ (weatherDB).
                this.cityForecastController.create(cityForecast);
                System.out.println("cf: " + cf);    
                //Δημιουργούμε ενα μορφοποιημένο μύνημα για την εμφάνιση στον χρήστη της αποθήκευσης των δεδομένων στην ΒΔ μέσω ενός MessageDialog.
                    String saveDbMessage = "<html><body>"
                        + "<p style='font-size:10px; font-weight:bold; color:blue;'>Αποθήκευση Δεδομένων Καιρού στην βάση δεδομένων.</p>"                   
                        + "<ul>"
                        + "<li>Τα καιρικά δεδομένα της: "  
                        +    cf.getCityname().getName()                        
                        +    " για τις " 
                        +    MyHttpRequester.getCityObservationDateMap().get("stringDateValue")                             
                        +     " αποθηκεύτηκαν.</li>"                    
                        + "</ul>"                  
                        + "</body></html>";
                JOptionPane.showMessageDialog(
                                null
                                ,saveDbMessage
                                , "Αποθήκευση"
                                , JOptionPane.INFORMATION_MESSAGE);
                //System.out.println(MyHttpRequester.getCityObservationDateMap().get("stringDateValue")); 
            } else {
                //Ομοίως δημιουργούμε ενα μορφοποιημένο με HTML μύνημα για την εμφάνιση στον χρήστη σε περίπτωση που για την τρέχουσα ημερομηνία έχουμε
                //ήδη αποθηκεύσει ενα δελτίο καιρού.
                String alreadySavedDbMessage = "<html><body>"
                        + "<p style='font-size:10px; font-weight:bold; color:black;'>Αποθήκευση Δεδομένων Καιρού στην βάση δεδομένων.</p>"                   
                        + "<ul>"
                        + "<li>Τα καιρικά δεδομένα της: " 
                        +    cf.getCityname().getName() 
                        +    " για τις " 
                        +    MyHttpRequester.getCityObservationDateMap().get("stringDateValue") 
                        +    " υπάρχουν ήδη αποθηκευμένα.</li>"                    
                        + "</ul>"                  
                        + "</body></html>";
                JOptionPane.showMessageDialog(null
                        , alreadySavedDbMessage
                        ,"Αποθήκευση"
                        , JOptionPane.WARNING_MESSAGE);
            }
            //Γίνεται Τερματισμός της Δοσοληψίας με την ΒΔ και αν ειναι επιτυχής η δοσοληψία αποθηκεύονται οι αλλαγές στην ΒΔ.
            transaction.commit();
        }catch(Exception e){
            //Αν προκληθεί εξαίρεση τότε αναιρούνται οι αλλαγές που έγιναν και κλείνει η δοσοληψία με την ΒΔ
            transaction.rollback();
            System.out.println("Σφάλμα στην Αποθήκευση στον Πίνακα City_Forecast: " + e.getMessage());
        }    
    }     
    /**
     * Διαγραφή Εγγραφών απο τον Πίνακα CITYFORECAST με τα καιρικά δεδομένα των πόλεων
     * και ακολούθως επιλογή απο τον χρήστη για Διαγραφή και της εγγραφής της πόλης απο τον
     * Πίνακα CITY.
     * @param cf 
     */  
    public void deleteCityForecastFromDb(CityForecast cf){
      
        City cityEntityObj = null;
        try{
            Object[] options = {"Ναι", "Ακύρωση"};
            int userSelection = JOptionPane.showOptionDialog(null,
                    "<html><body>" 
                            + "<p style='font-size:12px; font-weight:bold; color:red; padding: 5px; margin: 5px;'>Θέλετε σίγουρα να διαγράψετε τα δεδομένα της πόλης: " 
                            + cf.getCityname().getName() + 
                            " ;</p>" +
                    "</body></html>",
                    "Διαγραφή Δεδομένων",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);

            if (userSelection == JOptionPane.OK_OPTION) {
                try{
                         
                    //Εναρξη της δοσοληψίας με την ΒΔ.
                    this.transaction.begin();
                    //Λαμβάνουμε απο τον Πίνακα CITY την εγγραφή με το όνομα της πόλης που έχει αναζητήσει ο Χρήστης
                    // και το αναθέτουμε σε ενα αντικείμενο τύπου City.
                    cityEntityObj = this.cityEntityJpaController.findCity(cf.getCityname().getName());
                    
                    //Δημιουργούμε ενα αντικείμενο της Κλάσης Query και του αναθέτουμε το custom query που δημιουργούμε με την μέθοδο createQuery().
                    //Αρχικά θα αναζητήσουμε αν υπάρχει αποθηκευμένο forecast στην ΒΔ για την δεδομένη πόλη 
                    //Δημιουργούμε το query αναζήτησης και επιλόγης
                    String searchQueryStringByCityName = "SELECT cf FROM CityForecast cf WHERE cf.cityname = :cityname";
                    Query checkForExistingForecastForGivenCityInDbQuery = this.entityManager.createQuery(searchQueryStringByCityName);                    
                    
                    //Θέτουμε την παράμετρο του ερωτήματος
                    checkForExistingForecastForGivenCityInDbQuery.setParameter("cityname", cityEntityObj);
                    //Εκτελούμε το ερώτημα και απο αποθηκεύομε σε μία λίστα όλα τα δελτία καιρού για την ίδια πόλη (1 για κάθε ημερομηνία)
                    List <CityForecast> forecastsList = checkForExistingForecastForGivenCityInDbQuery.getResultList();
                    
                    //Κλείσιμο της Δοσοληψίας και επικύρωση των Αλλαγών στην ΒΔ
                    this.transaction.commit();
                    System.out.println("forecastsList --> " + forecastsList); //Εκτύπωση στην κονσόλα της λίστας με τα δελτία καιρού για την πόλη για debugging
                    if(!forecastsList.isEmpty()){ //Αν η λίστα περιέχει δελτία
                        
                        this.transaction.begin();
                        
                        //Με το SQL Query διαγράφουμε τα δεδομένα της πόλης.
                        Query deleteFromCityForecastTableQuery = this.entityManager.createQuery("DELETE FROM CityForecast cf WHERE cf.cityname = :cityname");

                        //Θέτουμε τις παραμέτρους του query που ειναι το αντικείμενο τύπου City και η τρέχουσα ημερομηνία
                        //και με την executeUpdate() εκτελείται η διαγραφή της εγγραφής για την δεδομένη πόλη.                    
                        deleteFromCityForecastTableQuery.setParameter("cityname", cityEntityObj).executeUpdate();
                        //Οριστικοποίηση της διαγραφής των καιρικών δεδομένων για την εν λόγω πόλη στην ΒΔ
                        transaction.commit();                                            

                        String deleteForecastFromDbMessage = "<html><body>"
                            + "<p style='font-size:12px; font-weight:bold; color:black; padding: 5px; margin: 5px;'>Τα Καιρικά Δεδομένα της "                   
                            + cf.getCityname().getName() +                         
                              " διαγράφηκαν"                              
                            + "</p></body></html>";
                        //Εμφάνιση του μυνήματος επιτυχούς διαγραφής της εγγραφής απο την ΒΔ.
                        JOptionPane.showMessageDialog(null
                            , deleteForecastFromDbMessage
                            ,"Διαγραφή Δεδομένων"
                            , JOptionPane.WARNING_MESSAGE);

                        //Ακολουθως ο Χρήστης μπορεί να επιλέξει να διαγράψει και την πόλη απο τον Πίνακα City
                        //Εχει προηγηθεί η οριστικοποίηση της διαγραφής του Forecast της πόλης απο τον αντίστοιχο πίνακα
                        //για την αποφυγή πρόκλησης εξαιρέσεων.             
                        //Δημιουργία ενός Πίνακα Object για την εμφάνιση δύο επιλογών στα ελληνικά
                        Object[] cityDeleteOptions = {"Ναι", "Όχι"};
                        //Εμφάνιση του Dialog για να επιλέξει ο Χρήστης Ναι ή Οχι.
                        String deleteSellectionMessage = "<html><body>" 
                                + "<p style='font-size:12px; font-weight:bold; color:red; padding: 5px; margin: 5px;'>Θέλετε να διαγράψετε και την πόλη " 
                                + cf.getCityname().getName() + 
                                " ;</p> " +
                        "</body></html>";
                        int userSelectionForCityDelete = JOptionPane.showOptionDialog(null,deleteSellectionMessage, "Διαγραφή Πόλης",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,cityDeleteOptions,cityDeleteOptions[0]);                                       

                        //Αν επιλέξει Ναί τότε εκτελείται ο κάτωθι κώδικας και διαγράφεται και η Πόλη απο την ΒΔ.
                        if (userSelectionForCityDelete == JOptionPane.YES_OPTION) {

                            //Εναρξη Δοσοληψίας με την ΒΔ
                            transaction.begin();

                            Query deleteFromCityTableQuery = this.entityManager.createQuery("DELETE FROM City c WHERE c.name = :name");                     
                            deleteFromCityTableQuery.setParameter("name", cityEntityObj.getName()).executeUpdate();

                            //Γίνεται Τερματισμός της Δοσοληψίας με την ΒΔ και αν ειναι επιτυχής η δοσοληψία αποθηκεύονται οι αλλαγές στην ΒΔ.  
                            transaction.commit(); 

                            String deleteCityFromDbMessage = "<html><body>"
                                            + "<p style='font-size:12px; font-weight:normal; color:black; padding: 5px; margin: 5px;'>Η πόλη "                   
                                            + cf.getCityname().getName()
                                            + " διεγράφηκε απο την ΒΔ"                              
                                            + "</p> "
                                            + "</body></html>";
                            JOptionPane.showMessageDialog(null, deleteCityFromDbMessage,"Διαγραφή Πόλης", JOptionPane.WARNING_MESSAGE);                                                                     
                        }
                    }else{
                        System.out.println("Δεν υπάρχουν Αποθηκευμένα Δελτία Καιρού για την εν λόγω Πόλη, Αποθηκεύεστε πρώτα κάποιο.");
                        String message = "Δεν υπάρχουν Αποθηκευμένα Δελτία Καιρού για την εν λόγω Πόλη, Αποθηκεύεστε πρώτα κάποιο.";
                        JOptionPane.showMessageDialog(null, message,"Διαγραφή Πόλης", JOptionPane.WARNING_MESSAGE);
                    }
                } catch(Exception e) {
                    //Αν προκληθεί εξαίρεση τότε αναιρούνται οι αλλαγές που έγιναν και κλείνει η δοσοληψία με την ΒΔ
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }
                    String formattedMessage = "<html>"
                            + "<body>" 
                            + "<p style='font-size:12px; font-weight:bold; color:red; padding: 5px; margin: 5px;'> "
                            + "Σφάλμα κατα την Διαγραφή των Δεδομένων στην Βάση"                            
                            + "</p>"
                            + "</body>"
                            + "</html>";
                    System.out.println("Σφάλμα κατα την Διαγραφή των Δεδομένων στην Βάση: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, formattedMessage,"Διαγραφή Πόλης", JOptionPane.ERROR_MESSAGE);
                }
            }else{                 
                System.out.println("Η διαγραφή των καιρικών δεδομένων για την " + cf.getCityname().getName() + " ακυρώθηκε απο τον Χρήστη");
            }
        
        }catch(Exception e){
             
            String formattedMessage = "<html>" 
                            + "<body>" 
                            + "<p style='font-size:12px; font-weight:bold; color:black; padding: 5px; margin: 5px;'> "
                            + "Σφάλμα κατα την Διαγραφή των Δεδομένων στην Βάση"                             
                            + "</p>"
                            + "</body>"
                            + "</html>";
            System.out.println("Σφάλμα κατα την Διαγραφή των Δεδομένων στην Βάση" + e.getMessage());
            //String formattedMessage = "<html>" + e.getMessage().replaceAll(",", "<br>") + "</html>";
            JOptionPane.showMessageDialog(null
                        , formattedMessage
                        ,"Διαγραφή Πόλης"
                        , JOptionPane.ERROR_MESSAGE);
            //Αν προκληθεί εξαίρεση τότε αναιρούνται οι αλλαγές που έγιναν και κλείνει η δοσοληψία με την ΒΔ
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }            
        }      
    }
    
    /**
     * Κατα την Αναζήτηση κάποια πόλης καλείται η εν λόγω μέθοδος η οποία εκτελεί
     * ενημέρωση του Πίνακα City στην ΒΔ με προσθήκη πόλης και αύξηση των προβολών σε περίπτωση που υπάρχει ήδη.
     * @param controller 
     * @param forecast 
     */
    public void updateForecastTableInDatabase(CityForecastJpaController controller, CityForecast forecast){
        
        transaction.begin();        
                
        Object[] options = {"Ναι", "Ακύρωση"};
            int userSelection = JOptionPane.showOptionDialog(null,
                    "<html><body>" 
                            + "<p style='font-size:12px; font-weight:bold; color:blue; padding: 5px; margin: 5px;'>Θέλετε σίγουρα να αποθηκεύσετε τα δεδομένα της πόλης: " 
                            + forecast.getCityname().getName() + 
                            " ;</p>" +
                    "</body></html>",
                    "Επεξεργασία Δεδομένων",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (userSelection == JOptionPane.OK_OPTION) {
                
                try{                    
                    controller.edit(forecast);               
                }catch(Exception e) {
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }                  
                    System.out.println(e.getMessage());
                    System.out.println("Σφάλμα κατα την Ενημέρωση του Πίνακα CityForecast: " + e.getMessage());
                    e.printStackTrace();
                }
                transaction.commit();                                            
                                                     
                String deleteForecastFromDbMessage = "<html><body>"
                    + "<p style='font-size:12px; font-weight:normal; color:black; padding: 5px; margin: 5px;'>Τα Καιρικά Δεδομένα της: "                   
                    + forecast.getCityname().getName() +                         
                      " Αποθηκεύτηκαν"                              
                    + "</p></body></html>";
                //Εμφάνιση του μυνήματος επιτυχούς διαγραφής της εγγραφής απο την ΒΔ.
                JOptionPane.showMessageDialog(null
                    , deleteForecastFromDbMessage
                    ,"Επεξεργασία Δεδομένων"
                    , JOptionPane.WARNING_MESSAGE);
            }else if (userSelection == JOptionPane.CANCEL_OPTION){
                //Αλλιώς αν ο χρήστης πατήσει το κουμπί "Ακύρωση" τότε ακυρώνεται η αποθήκευση, κλείνει η Δοσοληψία, Αναιρούνται όλες οι Αλλαγές στην ΒΔ.
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }   
                System.out.println("Η διαγραφή των καιρικών δεδομένων για την " + forecast.getCityname().getName() + " ακυρώθηκε απο τον Χρήστη");                
            }
    }
            
    /**
     * Η μέθοδος παίρνει ως όριμα ενα JcomboBox το οποίο
     * αποικίζεται με τις ημερομηνίες που υπάρχουν στην ΒΔ
     * @param comboBox 
     */     
    public void fillWithDatesJcomboBox(JComboBox<String> comboBox) {
        //Εκκαθάριση του JComboBox απο προυπάρχουσες Ημερομηνίες
        comboBox.removeAllItems();

        //Αποθήκευση σε Μία Λίστα με αντικείμενα Τυπου Date των 
        //ημερομηνιών που υπάρχουν στην ΒΔ.
        List<Date> observationDates = getWeatherObservationDatesFromDB();

        // Μετατροπή των Ημερομηνιών της Λίστας σε String και προσθήκη με την addItem()
        //στο JComboBox
        for (Date date : observationDates) {
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = dateFormat.format(date);
            comboBox.addItem(formattedDate);
            //comboBox.addItem(date.toString());
        }
    }

    /**
     * Η μέθοδος αυτή εκτελεί ένα ερώτημα για την επιλογή των διακριτών ημερομηνιώ
     * που υπάρχουν αποθηκευμένες στην ΒΔ
     * @return Λίστα Ημερομηνιών
     */
    private List<Date> getWeatherObservationDatesFromDB() {
        Query query = this.entityManager.createQuery("SELECT DISTINCT cf.observationdate FROM CityForecast cf");
        return query.getResultList();
    }

    /**
     * Γεμίζουμε την Jlist με τα ονόμα των πόλεων που υπάρχουν στην ΒΔ
     * @param list
     * @param selectedDate 
     */
    public void populateCityList(JList<String> list, String selectedDate) {
        //Γίνεται εκκαθθαριση της jList
        list.setModel(new DefaultListModel<>()); 

        //Αποθηκέουμε σε μία Λίστα τα ονόματα των πόλεων που έχουμε στην ΒΔ.
        List <String> cities = getCitiesForEveryDateFromDB(selectedDate);

        // Προσθέτουμε τα ονόματα των Πόλεων στο στοιχείο Jlist
        DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
        for (String city : cities) {
            model.addElement(city);
        }
    }

    /**
     * Παίνει ως όρισμα μία επιλεχθείσα ημερομηνία και επιστρέφει τα διακριτά ονόματα των πόλεων 
     * για αυτη την ημερομηνία που υπάρχουν στην ΒΔ
     * @param selectedDate
     * @return Μία Λίστα με τα ονόματα των Πόλεων που υπάρχουν στον Πίνακα CITY
     */
    private List<String> getCitiesForEveryDateFromDB(String mSelectedDate) {
        
        List<String> citiesNamesList = new ArrayList<>();
        
        // Μετατροπή της Ημερομηνίας σε format φιλικό για τον Χρήστη στην Ελλάδα.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); 
        Date date = null;
        try {
            date = dateFormat.parse(mSelectedDate);
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());           
        }
        try{
            this.transaction.begin();
            // Δημιουργία του query που θα εκτελέσουμε για την επιλογή των διακριτών ονομάτων 
            String distinctCityNamesQuery = "SELECT DISTINCT cf.cityname.name FROM CityForecast cf WHERE cf.observationdate = :date";
            Query query = entityManager.createQuery(distinctCityNamesQuery);
            query.setParameter("date", date);

            // Αποθήκευση στην Λίστα cities της λίστας με τα ονόματα των πόλεων που επιστρέφει η getResultList()
            citiesNamesList = query.getResultList();
            this.transaction.commit(); 
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        
        return citiesNamesList;
    }
    
    
    
    /**
     * Μέθοδος χωρίς Ορίσματα για την επιστροφή απο την ΒΔ όλων των Ονομάτων των Πόλεων
     * Παίνει ως όρισμα μία επιλεχθείσα ημερομηνία και επιστρέφει τα διακριτά ονόματα των πόλεων 
     * για αυτη την ημερομηνία που υπάρχουν στην ΒΔ
     * @return Μία Λίστα με τα ονόματα των Πόλεων που υπάρχουν στον Πίνακα CITY
     */
    public List<String> getCitiesFromForecastTable() {
        
        List<String> citiesNamesList = new ArrayList<>();
                
        try{
            this.transaction.begin();
            // Δημιουργία του query που θα εκτελέσουμε για την επιλογή των διακριτών ονομάτων 
            String distinctCityNamesQuery = "SELECT DISTINCT cf.cityname.name FROM CityForecast cf";
            Query query = entityManager.createQuery(distinctCityNamesQuery);
            
            // Αποθήκευση στην Λίστα cities της λίστας με τα ονόματα των πόλεων που επιστρέφει η getResultList()
            citiesNamesList = query.getResultList();
            this.transaction.commit(); 
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        
        return citiesNamesList;
    }
    
    
    
    
    /**
     * Επιστρέφει τα καιρικά δεδομένα για την επιλεχθείσα ημερομηνία απο το jcombobox και την επιλεχθείσα 
     * πόλη απο το Jlist.
     * @param mSelectedDate
     * @param mSelectedCity
     * @return Ενα POJO αντικείμενο CityForecast
     */
    public CityForecast getForecastFromDB(String mSelectedDate, String mSelectedCity) {
         
        List<CityForecast> CityForecastList = null;
        //Μετατροπή της Ημερομηνίας σε κατάλληλο format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(mSelectedDate);
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        try{
            this.transaction.begin();
            //Δημιουργία query για την λήψη του forecast απο την ΒΔ για την επιλεγμένη ημερομηνία και πόλη
            String forecastQuery = "SELECT DISTINCT cf FROM CityForecast cf WHERE cf.observationdate = :observationdate AND cf.cityname.name = :cityname";
            Query query = entityManager.createQuery(forecastQuery);
            //Θέτουμε τις παραμέτρους του ερωτήματος ωστε να αποφύγουμε τυχούσα SQL Ingection
            query.setParameter("observationdate", date);
            query.setParameter("cityname", mSelectedCity);            
                  
            // Εκτέλεση του ερωτήματος και επιστροφή μίας λίστας με τα αποτελέσματα την οποία αποθηκεύομαι στην λίστα CityForecastList.
            CityForecastList = query.getResultList();
        
            this.transaction.commit();
        
        }catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
        // Check if the result list is not empty and return the first result
        if (!CityForecastList.isEmpty()) {
            System.out.println("CityForecastList: " + CityForecastList); //για debugging
            return CityForecastList.get(0);
        } else {
            return null;
        }
        
        
    }
    /**
     * Τροποποιημένη Μέθοδος για την λήψη απο την ΒΔ όλων των καιρικών δελίτων για 
     * κάποια επιλεχθείσα πόλη
     * @param mSelectedCity
     * @return 
     */    
    public List<CityForecast> getForecastFromDBinList(String mSelectedCity) {
         
        List<CityForecast> CityForecastList = null;
        
        try{
            this.transaction.begin();
            //Δημιουργία query για την λήψη του forecast απο την ΒΔ για την επιλεγμένη ημερομηνία και πόλη
            String forecastQuery = "SELECT DISTINCT cf FROM CityForecast cf WHERE cf.cityname.name = :cityname";
            Query query = entityManager.createQuery(forecastQuery);
            //Θέτουμε ως Παράμετρο την επιλεχθείσα Πόλη
            query.setParameter("cityname", mSelectedCity);                   
        
            // Εκτέλεση του ερωτήματος και επιστροφή μίας λίστας με τα αποτελέσματα την οποία αποθηκεύομαι στην λίστα CityForecastList.
            CityForecastList = query.getResultList();
        
            this.transaction.commit();
        
        }catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
        // Check if the result list is not empty and return the first result
        if (CityForecastList!= null && !CityForecastList.isEmpty()) {
            System.out.println("CityForecastList: " + CityForecastList); //για debugging
            return  CityForecastList;
        } else {
            return null;
        }                
    }
       
    
    public List<City> getCitiesViews() {
         List<City> cities = null;
        try{
            this.transaction.begin();
            String citiesQuery = "SELECT DISTINCT c FROM City c ORDER BY c.views DESC ";
            cities = entityManager.createQuery(citiesQuery, City.class).getResultList();
            this.transaction.commit();
        }catch(Exception e){
            System.out.println("Error at retrieving tuples of City Table: " + e.getMessage());
            this.transaction.rollback();            
        }      
        return cities;
    }
        
    
}
