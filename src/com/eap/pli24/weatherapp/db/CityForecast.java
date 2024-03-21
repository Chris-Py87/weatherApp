/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eap.pli24.weatherapp.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "CITY_FORECAST")
@NamedQueries({
    @NamedQuery(name = "CityForecast.findAll", query = "SELECT c FROM CityForecast c"),
    @NamedQuery(name = "CityForecast.findById", query = "SELECT c FROM CityForecast c WHERE c.id = :id"),
    @NamedQuery(name = "CityForecast.findByObservationdate", query = "SELECT c FROM CityForecast c WHERE c.observationdate = :observationdate"),
    @NamedQuery(name = "CityForecast.findByTemperature", query = "SELECT c FROM CityForecast c WHERE c.temperature = :temperature"),
    @NamedQuery(name = "CityForecast.findByHumidity", query = "SELECT c FROM CityForecast c WHERE c.humidity = :humidity"),
    @NamedQuery(name = "CityForecast.findByWindspeed", query = "SELECT c FROM CityForecast c WHERE c.windspeed = :windspeed"),
    @NamedQuery(name = "CityForecast.findByUvindex", query = "SELECT c FROM CityForecast c WHERE c.uvindex = :uvindex"),
    @NamedQuery(name = "CityForecast.findByWeatherdescription", query = "SELECT c FROM CityForecast c WHERE c.weatherdescription = :weatherdescription")})
public class CityForecast implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "OBSERVATIONDATE")
    @Temporal(TemporalType.DATE)
    private Date observationdate;
    @Basic(optional = false)
    @Column(name = "TEMPERATURE")
    private int temperature;
    @Basic(optional = false)
    @Column(name = "HUMIDITY")
    private int humidity;
    @Basic(optional = false)
    @Column(name = "WINDSPEED")
    private int windspeed;
    @Basic(optional = false)
    @Column(name = "UVINDEX")
    private int uvindex;
    @Basic(optional = false)
    @Column(name = "WEATHERDESCRIPTION")
    private String weatherdescription;
    @JoinColumn(name = "CITYNAME", referencedColumnName = "NAME")
    @ManyToOne(optional = false)
    private City cityname;

    public CityForecast() {
    }

    public CityForecast(Integer id) {
        this.id = id;
    }

    public CityForecast(Integer id, Date observationdate, int temperature, int humidity, int windspeed, int uvindex, String weatherdescription) {
        this.id = id;
        this.observationdate = observationdate;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windspeed = windspeed;
        this.uvindex = uvindex;
        this.weatherdescription = weatherdescription;
    }

    public CityForecast(Date observationdate, int temperature, int humidity, int windspeed, int uvindex, String weatherdescription, City cityname) {
        this.observationdate = observationdate;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windspeed = windspeed;
        this.uvindex = uvindex;
        this.weatherdescription = weatherdescription;
        this.cityname = cityname;
    }
    
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getObservationdate() {
        return observationdate;
    }

    public void setObservationdate(Date observationdate) {
        this.observationdate = observationdate;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(int windspeed) {
        this.windspeed = windspeed;
    }

    public int getUvindex() {
        return uvindex;
    }

    public void setUvindex(int uvindex) {
        this.uvindex = uvindex;
    }

    public String getWeatherdescription() {
        return weatherdescription;
    }

    public void setWeatherdescription(String weatherdescription) {
        this.weatherdescription = weatherdescription;
    }

    public City getCityname() {
        return cityname;
    }

    public void setCityname(City cityname) {
        this.cityname = cityname;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CityForecast)) {
            return false;
        }
        CityForecast other = (CityForecast) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CityForecast: " + 
                ", id=" + id +
                ", AreaName: " + this.cityname.getName() +
                ", Temperature: " + this.temperature +
                ", Humidity: " + this.humidity + 
                ", UV: " + this.uvindex + 
                ", Weather Description: " + this.getWeatherdescription() + 
                ", Date: " + this.observationdate.toString();
    }

   
    
}
