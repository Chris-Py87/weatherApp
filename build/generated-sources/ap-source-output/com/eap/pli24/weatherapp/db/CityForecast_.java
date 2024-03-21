package com.eap.pli24.weatherapp.db;

import com.eap.pli24.weatherapp.db.City;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2024-03-09T19:40:28")
@StaticMetamodel(CityForecast.class)
public class CityForecast_ { 

    public static volatile SingularAttribute<CityForecast, Integer> uvindex;
    public static volatile SingularAttribute<CityForecast, Date> observationdate;
    public static volatile SingularAttribute<CityForecast, City> cityname;
    public static volatile SingularAttribute<CityForecast, Integer> temperature;
    public static volatile SingularAttribute<CityForecast, Integer> humidity;
    public static volatile SingularAttribute<CityForecast, Integer> windspeed;
    public static volatile SingularAttribute<CityForecast, String> weatherdescription;
    public static volatile SingularAttribute<CityForecast, Integer> id;

}