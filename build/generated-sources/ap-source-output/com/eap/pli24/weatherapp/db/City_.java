package com.eap.pli24.weatherapp.db;

import com.eap.pli24.weatherapp.db.CityForecast;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2024-03-09T19:40:28")
@StaticMetamodel(City.class)
public class City_ { 

    public static volatile SingularAttribute<City, String> name;
    public static volatile SingularAttribute<City, Integer> views;
    public static volatile ListAttribute<City, CityForecast> cityForecastList;

}