
package com.eap.pli24.weatherapp.json;

import com.eap.pli24.weatherapp.db.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Κλάση για την επικοινωνία της εφαρμογής μας με το WEB API και την λήψη των επιθυμητών δεδομένων. 
 */
public class MyHttpRequester {
    
    
    //private static final String SEARCH_FORECAST_URL = "https://wttr.in/?format=j1";
        
    private static final String TEMPERATURE_JSON_API_KEY = "temp_C";
    private static final String HUMIDITY_JSON_API_KEY = "humidity";
    private static final String WIND_SPEED_JSON_API_KEY = "windspeedKmph";
    private static final String UV_LEVEL_JSON_API_KEY = "uvIndex";
    private static final String LOCAL_OBSERVATION_DATE_TIME_JSON_API_KEY = "localObsDateTime";
    private static final String WEATHER_DESCRIPTION_JSON_API_KEY = "weatherDesc";
    private static final String ASTRONOMY_JSON_API_KEY = "astronomy";
    private static final String ASTRONOMY_MOON_PHASE_JSON_API_KEY = "moon_phase";
    private static final String ASTRONOMY_SUNRISE_JSON_API_KEY = "sunrise";
    private static final String ASTRONOMY_SUNSET_JSON_API_KEY = "sunset";
    private static final String LATITUDE_JSON_API_KEY = "latitude";
    private static final String LONGITUDE_JSON_API_KEY = "longitude";
    private static final String CITY_AREA_NAME_API_KEY = "areaName";
                 
    //Δημιουργία ενός HashMap που θα έχει όλες τις συλλεχθείσες πληροφορίες (καιρικά δεδομένα)
    private static HashMap<String, Object> weatherDatahashMap = new HashMap<>();
    
    //Δήλωση ενός hashMap που θα κρατάει την ημερομηνία παρατήρησης της πόλης και ως string και ως αντικείμενο date.
    private static Map<String,Object> CityWeatherObsDateHashMap = null;    
    
    //Δημιουργούμε μια Λίστα απο JSONArray αντικείμενα στην οποία θα τοποθετήσουμε τρία JSON Array με τα επιθυμητα δεδομένα
    private static List<JsonArray> getMyJsonArrayListFromWttrAPI (String cityForWeatherSearch) {
        
        List<JsonArray> ArrayListWithJSONArraysWithForecastDataForGivenCity = new ArrayList<>();
        
        /*Ελεγχος του ονόματος της πόλης αν περιέχει μόνο γράμματα και είναι UTF-8*/
        String checkedCityName = "";
        
        //Καλούμε την συνάρτηση checkTheCityName() με όρισμα το όνομα της πόλης που έδωσε ο χρήστης αφού πρώτα
        //το μετατρέψουμε σε μικρά με την toLowerCase().      
        checkedCityName = checkTheCityName(cityForWeatherSearch.toLowerCase());             
       
        String urlToCall = "";    
        String urlEncodedCityParameter = "";
        
        try {
            urlEncodedCityParameter = java.net.URLEncoder.encode(checkedCityName, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Url Encoding Error: " + ex.getMessage());
        }
        urlToCall = "https://wttr.in/" + urlEncodedCityParameter + "?format=j1";
        //urlToCall = "https://wttr.in/" + checkedCityName.trim() + "?format=j1";
        System.out.println("URL to Call: " + urlToCall);
           
        if (!urlToCall.startsWith("http://") && !urlToCall.startsWith("https://")) {
            throw new IllegalArgumentException("Invalid URL: " + urlToCall);
        }
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(urlToCall).build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
               String responseString = "";
                try(ResponseBody body = response.body()){
                    responseString =  body.string();              
                            
                //Δημιουργία αντικειμένου JsonBuilder
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();
              
                //Παίρνουμε το αποτέλεσμα σε JsonObject
                JsonObject json = gson.fromJson(responseString,JsonObject.class);

                // Δήλωση Μεταβλητών τύπου JsonArray στις οποίες θα αποθηκευτούν τα τρία JSON Αντικείμενα με τα κάτωθι κλειδιά οπως φάινονται.
                JsonArray currentConditionJSONArray = json.get("current_condition").getAsJsonArray();
                JsonArray nearestAreaJSONArray = json.get("nearest_area").getAsJsonArray();
                JsonArray weatherJSONArray = json.get("weather").getAsJsonArray();
                
                //Προσθήκη στη Λίστα των τριών JsonArrays που δημιουργήσαμε παραπάνω
                ArrayListWithJSONArraysWithForecastDataForGivenCity.add(currentConditionJSONArray); //index 0
                ArrayListWithJSONArraysWithForecastDataForGivenCity.add(nearestAreaJSONArray); //index 1
                ArrayListWithJSONArraysWithForecastDataForGivenCity.add(weatherJSONArray); //index 2
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            return ArrayListWithJSONArraysWithForecastDataForGivenCity;
            }else{
                System.out.println("Unsuccessful response: " + response.code());
                return null;
            }         
        } catch (Exception e) {
            System.out.println(e.getMessage());    
        }
        return null;        
    }       
    
    public static CityForecast getCityForecastFromWttrAPI(String cityName) {
        
        List<JsonArray> jsonArraysListWithCityForecast = new ArrayList<>();        
        JsonArray cityWeatherDescriptionJsonArray = null;
        JsonArray cityAstronomyJsonArray = null;
        JsonArray cityForecastJsonArray = null;
        JsonArray cityAreaNameJsonArray = null;
        
        Date WeatherObsDateObject = null;
        
        int cityTemperature = 0;
        int cityHumidity = 0;
        int cityWindSpeed = 0;
        int cityUvLevel = 0;        
        
        String cityAreaNameString = "";
        String cityWeatherDescriptionStr = "";
        String CityWeatherObservationDateTime = "";        
        String WeatherObsDateString = "";
        String moonPhaseString = "";
        String sunRiseTimeString = "";
        String sunSetTimeString = "";
        String cityLatitudeString = "";
        String cityLongitudeString = "";             
        
        CityForecast forecast = null;
        City city = null;
     
        jsonArraysListWithCityForecast = getMyJsonArrayListFromWttrAPI (cityName);
        
        //Επιστροφή και Αποθήκευση στην μεταβλητή cityForecastJsonArray (Τύπου JsonArray) ενός αντικειμένου JsonArray
        //με τα δεδομένα απο το API σε μορφή JSON
        //cityForecastJsonArray = getMyJsonArrayListFromWttrAPI (cityName);
        
        if (jsonArraysListWithCityForecast.get(0).isJsonArray() 
            && jsonArraysListWithCityForecast.get(1).isJsonArray()
            && jsonArraysListWithCityForecast.get(2).isJsonArray()) {
           
            
            for(JsonArray jsonarray : jsonArraysListWithCityForecast){
                
                for(JsonElement jsonElement : jsonarray){

                    if(jsonElement.isJsonObject()){
                        JsonObject JsonArrayObjectWithCityForecast = jsonElement.getAsJsonObject();

                        //Λήψη του ονόματος της περιοχής                   
                        JsonElement jsonArrayObjectCityAreaName = JsonArrayObjectWithCityForecast.get(CITY_AREA_NAME_API_KEY);                                          
                        if(jsonArrayObjectCityAreaName !=null){
                            cityAreaNameJsonArray = jsonArrayObjectCityAreaName.getAsJsonArray();                       
                            for(JsonElement je : cityAreaNameJsonArray){                                    
                                JsonObject jsonObjectAreaName = je.getAsJsonObject();
                                if(jsonObjectAreaName != null){
                                    cityAreaNameString = jsonObjectAreaName.get("value").getAsString();
                                    //Προσθήκη στο Λεξικό - HashMap της λεκτικής περιγραφής του καιρού παρατήρησης με ονομασία κλειδιού ίδια με αυτήν του JSON
                                    weatherDatahashMap.put(CITY_AREA_NAME_API_KEY,cityAreaNameString);
                                    //System.out.println("City Area Name: " + cityAreaNameString);
                                }else{
                                    cityAreaNameString = cityName;
                                }                                        
                            }   
                        }                                         
                        //Εξαγωγή της πληροφορίας της θερμοκρασίας απο το JSON 
                        JsonElement jsonArrayObjectCityTemperature = JsonArrayObjectWithCityForecast.get(TEMPERATURE_JSON_API_KEY);                                          
                        if(jsonArrayObjectCityTemperature != null){

                            //Τοπική προσωρινή Μεταβλητή για έλεγχο του στοιχείου της θερμοκρασίας
                            String tempCityTemperature = jsonArrayObjectCityTemperature.getAsString(); // Αποθήκευση ως String απο το JSON
                            if(tempCityTemperature.equals("")){ //Αν δεν έχει τιμή άρα θα ειναι το "" string
                                cityTemperature = -273; //θέσε στην μεταβλητή της θερμοκρασίας της πόλης την τιμή -273
                            }else{
                                 //Λήψη ως String της Θερμοκρασίας της Πόλης απο τον πίνακα με τα JSON αντικείμενα και μετατροπή σε Int
                                 //Ελεγχος του κώδικα για πρόκληση εξαίρεσης σε περίπτωση που τα δεδομένα που θα λάβουμε δεν έχουν σωστό format ακεραίου.
                                 try{
                                    cityTemperature = Integer.parseInt(jsonArrayObjectCityTemperature.getAsString());
                                    //System.out.println("City Temperature: " + cityTemperature + "oC" ); //Εκτύπωση στην Κονσόλα.
                                    //Προσθήκη στο Λεξικό - HashMap της θερμοκρασίας με ονομασία κλειδιού ίδια με αυτήν του JSON
                                    weatherDatahashMap.put(TEMPERATURE_JSON_API_KEY,cityTemperature);
                                 }catch(NumberFormatException e){
                                    System.out.println("Error at Parsing the Temperature String: " + e.getMessage());
                                    cityTemperature = -273;
                                 }                            
                            }                                               
                        }  
                        //Ομοίως και για την Υγρασία
                        JsonElement jsonArrayObjectCityHumidity = JsonArrayObjectWithCityForecast.get(HUMIDITY_JSON_API_KEY);                                          
                        if(jsonArrayObjectCityHumidity != null){
                            String tempCityHumidity = jsonArrayObjectCityHumidity.getAsString();
                            if(tempCityHumidity.equals("")){
                                cityHumidity = -1;
                            }else{
                                try{
                                    cityHumidity = Integer.parseInt(jsonArrayObjectCityHumidity.getAsString());
                                    //System.out.println("City Humidity: " + cityHumidity + "%");
                                    //Προσθήκη στο Λεξικό - HashMap της Υγρασίας με ονομασία κλειδιού ίδια με αυτήν του JSON
                                    weatherDatahashMap.put(HUMIDITY_JSON_API_KEY,cityHumidity);
                                }catch(NumberFormatException e){
                                    System.out.println("Error at Parsing the Humidity String: " + e.getMessage());
                                    cityHumidity = -1;
                                 }
                            }                                            
                        }    
                        //Ομοίως και για την Ταχύτητα του Αέρα
                        JsonElement jsonArrayObjectCityWindSpeed = JsonArrayObjectWithCityForecast.get(WIND_SPEED_JSON_API_KEY);                                          
                        if(jsonArrayObjectCityWindSpeed != null){
                            String tempCityWind = jsonArrayObjectCityWindSpeed.getAsString();
                            if(tempCityWind.equals("")){
                                cityWindSpeed = -1;
                            }else{
                                try{
                                    cityWindSpeed = Integer.parseInt(jsonArrayObjectCityWindSpeed.getAsString());
                                    //System.out.println("City Wind Speed: " + cityWindSpeed + "Kph" );
                                    //Προσθήκη στο Λεξικό - HashMap της ταχύτητας του Αέρα με ονομασία κλειδιού ίδια με αυτήν του JSON
                                    weatherDatahashMap.put(WIND_SPEED_JSON_API_KEY,cityWindSpeed);
                                }catch(NumberFormatException e){
                                    System.out.println("Error at Parsing the Wind String: " + e.getMessage());
                                    cityWindSpeed = -1;
                                 }
                            }                
                        }
                        //Ομοίως και για την ακτινοβολία UV
                        JsonElement jsonArrayObjectCityUvIndexLevel = JsonArrayObjectWithCityForecast.get(UV_LEVEL_JSON_API_KEY);                                          
                        if(jsonArrayObjectCityUvIndexLevel != null){

                            String tempCityUvIndex = jsonArrayObjectCityUvIndexLevel.getAsString();
                            if(tempCityUvIndex.equals("")){
                                cityUvLevel = -1;
                            }else{
                                try{
                                    cityUvLevel = Integer.parseInt(jsonArrayObjectCityUvIndexLevel.getAsString());
                                    //System.out.println("City UV Index: " + cityUvLevel);
                                    //Προσθήκη στο Λεξικό - HashMap του επιπέδου της UV ακτινοβοίας με ονομασία κλειδιού ίδια με αυτήν του JSON
                                    weatherDatahashMap.put(UV_LEVEL_JSON_API_KEY,cityUvLevel);
                                }catch(NumberFormatException e){
                                    System.out.println("Error at Parsing the UV Index String: " + e.getMessage());
                                    cityUvLevel = -1;
                                 }
                            }                        
                        }
                        //Λήψη της Ημερομηνίας και Ωρας της Παρατήρησης των Καιρικών Δεδομένων
                        JsonElement jsonArrayObjectCityWeatherObservationDateTime = JsonArrayObjectWithCityForecast.get(LOCAL_OBSERVATION_DATE_TIME_JSON_API_KEY);                                          
                        if(jsonArrayObjectCityWeatherObservationDateTime != null){
                            CityWeatherObservationDateTime = jsonArrayObjectCityWeatherObservationDateTime.getAsString();
                            System.out.println("City Observation Date and Time: " + CityWeatherObservationDateTime);

                            //Μετατροπή της ημερομηνίας σε ελληνικό format και απόρριψη της ώρας και δημιουργία ενός Map Που περιέχει
                            //την Ημερομηνία και σε μορφή String και σε Μορφή Date για αποθήκευση στην ΒΔ ως Date πεδίο.
                            CityWeatherObsDateHashMap = MyHttpRequester.DateFormatConvertion(CityWeatherObservationDateTime);
                            WeatherObsDateObject = (Date) CityWeatherObsDateHashMap.get("dateObject");
                            WeatherObsDateString = (String) CityWeatherObsDateHashMap.get("stringDateValue");
                            //System.out.println("City Observation Desired Format Date : " + WeatherObsDateObject);
                            //System.out.println("City Observation Desired Format Date : " + WeatherObsDateString);
                            //Προσθήκη στο Λεξικό - HashMap της ημερομηνίας παρατήρησης με ονομασία κλειδιού ίδια με αυτήν του JSON
                            weatherDatahashMap.put(LOCAL_OBSERVATION_DATE_TIME_JSON_API_KEY,WeatherObsDateString);
                        }
                        //Λήψη της Λεκτικής Πειγραφής του Καιρού                   
                        JsonElement jsonArrayObjectCityWeatherDescription = JsonArrayObjectWithCityForecast.get(WEATHER_DESCRIPTION_JSON_API_KEY);                                          
                        if(jsonArrayObjectCityWeatherDescription != null){
                            cityWeatherDescriptionJsonArray = jsonArrayObjectCityWeatherDescription.getAsJsonArray();                       
                                for(JsonElement je : cityWeatherDescriptionJsonArray){
                                    JsonObject jsonObjectWeatherDescription = je.getAsJsonObject();
                                    cityWeatherDescriptionStr = jsonObjectWeatherDescription.get("value").getAsString();
                                    //Προσθήκη στο Λεξικό - HashMap της λεκτικής περιγραφής του καιρού παρατήρησης με ονομασία κλειδιού ίδια με αυτήν του JSON
                                    weatherDatahashMap.put(WEATHER_DESCRIPTION_JSON_API_KEY,cityWeatherDescriptionStr);
                                    //System.out.println("Weather Description: " + cityWeatherDescriptionStr);
                                }

                        }
                        //Λήψη της Φάσης της Σελήνης, της Ωρας Ανατολής και της Ωρας Δύσης.         
                        JsonElement jsonArrayObjectCityAstronomy = JsonArrayObjectWithCityForecast.get(ASTRONOMY_JSON_API_KEY);                                          
                        if(jsonArrayObjectCityAstronomy != null){
                            cityAstronomyJsonArray = jsonArrayObjectCityAstronomy.getAsJsonArray();                       
                                for(JsonElement je : cityAstronomyJsonArray){
                                    JsonObject jsonObjectAstronomy = je.getAsJsonObject();
                                    moonPhaseString = jsonObjectAstronomy.get(ASTRONOMY_MOON_PHASE_JSON_API_KEY).getAsString();
                                    sunRiseTimeString = jsonObjectAstronomy.get(ASTRONOMY_SUNRISE_JSON_API_KEY).getAsString();
                                    sunSetTimeString = jsonObjectAstronomy.get(ASTRONOMY_SUNSET_JSON_API_KEY).getAsString();
                                    //Προσθήκη στο Λεξικό - HashMap της Φασης της Σελήνης, Ωρας Δύσης, Ωρας Ανατολής με ονομασίες κλειδιών ίδιες με αυτές του JSON
                                    weatherDatahashMap.put(ASTRONOMY_MOON_PHASE_JSON_API_KEY,moonPhaseString);
                                    weatherDatahashMap.put(ASTRONOMY_SUNRISE_JSON_API_KEY,sunRiseTimeString);
                                    weatherDatahashMap.put(ASTRONOMY_SUNSET_JSON_API_KEY,sunSetTimeString);                                
                                    //System.out.println("Moon Phase: " + moonPhaseString);
                                    //System.out.println("SunRise: " + sunRiseTimeString);
                                    //System.out.println("SunSet: " + sunSetTimeString);                                
                                }                      
                        }
                        //Λήψη των Συντεταγμένων της Περιοχής και μετατροπής τους σε μορφή Μοιρών-Λεπτών-Δευτερολέπτων (Αεροπορική Χρήση)
                        //Λήψη του Γεωγραφικού Πλάτους (Latitude)
                        JsonElement jsonArrayObjectCityLatitude = JsonArrayObjectWithCityForecast.get(LATITUDE_JSON_API_KEY);                                          
                        if(jsonArrayObjectCityLatitude != null){                                              
                            //Λήψη της τιμής του Πλάτους απο το Json αντικείμενο ως String μετατροπή του σε Double και πέρασμα ως παράμετρο στην συνάρτηση
                            //coordinatesConverter() για την μετατροπή του στο επιθυμητό format.

                            double tempDoubleLatitude = Double.parseDouble(jsonArrayObjectCityLatitude.getAsString());
                            cityLatitudeString = coordinatesConverter(tempDoubleLatitude);

                            //Προσθήκη στο Λεξικό - HashMap του Γεωγραφικού Πλάτους (Latitude) με ονομασίες κλειδιών ίδιες με αυτές του JSON
                            weatherDatahashMap.put(LATITUDE_JSON_API_KEY,cityLatitudeString);
                            //System.out.println("City Latitude in DMS Format: " + cityLatitudeString);                      
                        }
                        //Λήψη του Γεωγραφικού Μήκους (Longitude)
                        JsonElement jsonArrayObjectCityLongitude = JsonArrayObjectWithCityForecast.get(LONGITUDE_JSON_API_KEY);                                          
                        if(jsonArrayObjectCityLongitude != null){                        
                            //Λήψη της τιμής του Πλάτους απο το Json αντικείμενο ως String μετατροπή του σε Double και πέρασμα ως παράμετρο στην συνάρτηση
                            //coordinatesConverter() για την μετατροπή του στο επιθυμητό format.
                            cityLongitudeString = coordinatesConverter(Double.parseDouble(jsonArrayObjectCityLongitude.getAsString()));
                            //Προσθήκη στο Λεξικό - HashMap του Γεωγραφικού Μήκους (Longitude) με ονομασίες κλειδιών ίδιες με αυτές του JSON
                            weatherDatahashMap.put(LONGITUDE_JSON_API_KEY,cityLongitudeString);
                            //System.out.println("City Longitude in DMS Format: " + cityLongitudeString);                      
                        }
                        
                        city = new City(cityAreaNameString);
                       
                        forecast = new CityForecast(WeatherObsDateObject, cityTemperature, cityHumidity, cityWindSpeed, cityUvLevel, cityWeatherDescriptionStr, city);
                        break;                            
                    }            
                }
            }
            System.out.println("\nThe Json has parsed and the CityForecast Entity Object has been created : " + forecast.toString());
            System.out.println("\nWeather HashMap: " + weatherDatahashMap );
        }
        //System.out.println(forecast.toString());
        return forecast;
    }
    
    private static String checkTheCityName(String cityName) {
        
        if(cityName.isBlank()){
            //Δημιουργία ενός Μορφοποιημένου Μυνήματος
            String warningMessage = "<html><body>"
                    + "<p style='font-size:10px; font-weight:bold; color:blue;'>Προσοχή Δεν έχετε Συμπληρώσει Τό Όνομα της Πόλης.</p>"                   
                    + "<ul>"
                    + "<li>Εμφανίζονται τα Μετεωρολογικά Δεδεμένα της περιοχής με βάση την IP σας.</li>"                    
                    + "</ul>"                    
                    + "</body></html>";
            //Δημιουργία ενός στοιχείου swing για εμφάνιση πληροφοριών στον χρήστη
            //σε περίπτωση που δεν καταγράψει κάποια επιθυμητή πόλη.
            JOptionPane.showMessageDialog(null, 
                    warningMessage, 
                    "Warning: Blank City Text Field!!! ", 
                    JOptionPane.INFORMATION_MESSAGE);
            return "";
        }
        
        String correctedCityString = "";
        correctedCityString = cityName.toLowerCase().trim();        
        /*Αφαίρεση των Αριθμών μέσα απο την λέξη*/
        correctedCityString = correctedCityString.replaceAll("\\d", "");
        /*Αφαίρεση όλων των whitespaces απο την λέξη*/
        correctedCityString = correctedCityString.replaceAll("\\s", "");
        //Μετατροπή του ελληνικού ονόματος σε λατινικό
        correctedCityString = convertToLatin(cityName);
        
        /*Ελέγχουμε με Κανονική Έκφραση άν η λέξη ειναι γραμμένη με Λατινικούς Χαρακτήρες*/
        /*Αν όχι εμφανίζεται ένα παράθυρο διαλόγου με το λάθος που συνέβη*/
        if (correctedCityString.matches("[a-z]+")) {                       
            return correctedCityString;            
        } else {            
            JOptionPane.showMessageDialog(null, "Πληκτρολογήστε Ξανά Τό Όνομα της Πόλης με Λατινικούς Χακτήρες", " Error : Invalid City Name!!! ", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
              
    /**
     * Μέθοδος για μετατροπή του format της ημερομηνίας που παίρνουμε απο το JSON API στο
     * κλασσικό format που χρησιμοποειται στην Ελλάδα καθώς και επεξεργασία των δεδομένων για την
     * απαλοιφή της ώρας που λαμβάνεται ώς μέρος του String.
     * @param dateToBeConverted
     * @return Ενα HashMap με την ημερομηνία στο επιθυμητό format τόσο σαν String όσο και σαν Αντικείμενο Date.
     */    
    private static Map<String,Object> DateFormatConvertion (String dateTimeToBeModified) {
        
        Map<String, Object> resultDateHashMap = new HashMap<>();
        
        try {         
            /*Καθορισμός του επιθυμητού Format για την Ημ/νια*/
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");

            /*Διαπέραση των δεδομένων και διατήρηση μόνο της Ημερομηνίας*/
            Date modifiedDate = oldFormat.parse(dateTimeToBeModified.substring(0, 10));

            /*Μετατροπή της Ημερομηνίας στο επιθυμητό Format*/
            String newDateFormatString = newFormat.format(modifiedDate);
            /* Μετατροπή της Νέας Ημερομηνίας απο Συμβολοσειρά σε Αντικείμενο Date και επιστροφή του*/            
            resultDateHashMap.put("stringDateValue", newDateFormatString);
            resultDateHashMap.put("dateObject", modifiedDate);
            return  resultDateHashMap ;
        } catch (ParseException ex) {
            /*Εκτύπωση του μυνηματος λάθους στην κονσόλα σε περίπτωση που προκληθεί εξαίρεση*/
            System.out.println("Error At Date Convertion: " + ex.getMessage());
        }
        return null;            
    }
    
    /**
     * Μέθοδος για την μετρατροπή των Συντεταγμένων απο Δεκαδικές Μοίρες σε 
     * Μοίρες Λεπτά και Δευτερόλεπτα για εύκολη χρήση σε οποιονδήποτε χάρτη.
     * @param coordinate
     * @return Ενα String με τις Συντεταγμένες σην επιθυμητή μορφή
     */
    private static String coordinatesConverter(double coordinate) {
        int degrees = (int) coordinate;
        double minutesDecimal = (coordinate - degrees) * 60;
        int minutes = (int) minutesDecimal;
        int seconds = (int) ((minutesDecimal - minutes) * 60);
        return degrees + "° " + minutes + "' " + seconds + "\"";
    }
    
    /**
     * Η μέθοδος αυτή επιστρέφει το hashMap με τα καιρικά δεδομένα 
     * το συγκεκριμένο Map εχει και έξτρα δεδομένα
     * @return ενα HashMap με τα επιθυμητά δεδομένα.
     */
    public static Map<String,Object> getMyWeatherDataMap (){   
                        
        return MyHttpRequester.weatherDatahashMap;       
    }  
    /**
     * Η μέθοδος επιστρέφει ενα HashMap με την ημερομηνία παρουσίασης και ως Συμβολοσείρα 
     * και ως Αντικείμενο τύπου Date
     * @return 
     */
    public static Map<String,Object> getCityObservationDateMap (){   
                        
        return MyHttpRequester.CityWeatherObsDateHashMap;       
    }  
    
    private static String convertToLatin(String cityNameInGreek) {
        
        HashMap<Character, String> toLatinMap = createTransliterationMap();
        //Δημιουργία ενός αντικειμένου stringBuilder για την ονομασία στα λατινικά
        StringBuilder cityNameInLatin = new StringBuilder();
        //iteration της ελληνικής ονομασίας
        for (char c : cityNameInGreek.toCharArray()) {
            //μετατροπή του κάθε ελληνικού χαρακτήρα σε lowerCase και χρήση του ως κλειδί
            //για την λήψη απο το Map της τιμής του κλειδιού που είναι ο εν λόγω χαρακτήρας στα λατινικά
            String latinChar = toLatinMap.get(Character.toLowerCase(c));
            //Προσθήκη του λατινικού γράμματος στο αντικείμενο stringBuilder αν 
            //γίνει λήψη τιμής απο το map αλλιώς προσθέτουμε τον τρέχοντα χαρακτήρα της αρχικής λέξης
            //με χρήση του τριαδικού τελεστή
            cityNameInLatin.append((latinChar != null) ? latinChar : c);
        }
        
        return cityNameInLatin.toString();
    }

    /**
     * Δημιουργούμε μία αντιστοίχιση για τα ελληνικά γράμματα στα αντίστοιχα λατινικά
     * ωστε να μετατρέπεται τυχούσα είσοδος του χρήστη στα ελληνικά στα λατινικά
     * @return Επιστρέφει ένα HashMap με τις αντιστοιχίσεις των γραμμάτων όπου 
     * κλειδιά το ελληνικό αλφάβητο και τιμές το λατινικό
     */
    private static HashMap<Character, String> createTransliterationMap() {
        
        final HashMap<Character, String> toLatinMap = new HashMap<Character, String>(){{
       
        put('\u03B1', "a");     //  put('α', "a");
        put('\u03B2', "v");     //  put('β', "v");
        put('\u03B3', "g");     //  put('γ', "g");
        put('\u03B4', "d");     //  put('δ', "d");
        put('\u03B5', "e");     //  put('ε', "e");
        put('\u03B6', "z");     //  put('ζ', "z");
        put('\u03B7', "i");     //  put('η', "i");
        put('\u03B8', "th");    //  put('θ', "th");
        put('\u03B9', "i");     //  put('ι', "i");
        put('\u03BA', "k");     //  put('κ', "k");
        put('\u03BB', "l");     //  put('λ', "l");
        put('\u03BC', "m");     //  put('μ', "m");
        put('\u03BD', "n");     //  put('ν', "n");
        put('\u03BE', "x");     //  put('ξ', "x");
        put('\u03BF', "o");     //  put('ο', "o");
        put('\u03C0', "p");     //  put('π', "p");
        put('\u03C1', "r");     //  put('ρ', "r");
        put('\u03C3', "s");     //  put('σ', "s");
        put('\u03C4', "t");     //  put('τ', "t");
        put('\u03C5', "y");     //  put('υ', "y");
        put('\u03C6', "f");     //  put('φ', "f");
        put('\u03C7', "ch");    //  put('χ', "ch");
        put('\u03C8', "ps");    //  put('ψ', "ps");
        put('\u03C9', "o");     //  put('ω', "o");
        put('\u03AC', "a");     //  put('ά', "a");
        put('\u03AD', "e");     //  put('έ', "e");
        put('\u03AF', "i");     //  put('ί', "i");
        put('\u03CC', "o");     //  put('ό', "o");
        put('\u03CD', "y");     //  put('ύ', "y");
        put('\u03AE', "i");     //  put('ή', "i");
        put('\u03CE', "o");     //  put('ώ', "o");
        put('\u03C2', "s");     //  put('ς', "s");
        }};
        return toLatinMap;
    }
         
}
