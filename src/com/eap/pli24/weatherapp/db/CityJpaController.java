/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eap.pli24.weatherapp.db;

import com.eap.pli24.weatherapp.db.exceptions.IllegalOrphanException;
import com.eap.pli24.weatherapp.db.exceptions.NonexistentEntityException;
import com.eap.pli24.weatherapp.db.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Christos
 */
public class CityJpaController implements Serializable {

    public CityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(City city) throws PreexistingEntityException, Exception {
        if (city.getCityForecastList() == null) {
            city.setCityForecastList(new ArrayList<CityForecast>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CityForecast> attachedCityForecastList = new ArrayList<CityForecast>();
            for (CityForecast cityForecastListCityForecastToAttach : city.getCityForecastList()) {
                cityForecastListCityForecastToAttach = em.getReference(cityForecastListCityForecastToAttach.getClass(), cityForecastListCityForecastToAttach.getId());
                attachedCityForecastList.add(cityForecastListCityForecastToAttach);
            }
            city.setCityForecastList(attachedCityForecastList);
            em.persist(city);
            for (CityForecast cityForecastListCityForecast : city.getCityForecastList()) {
                City oldCitynameOfCityForecastListCityForecast = cityForecastListCityForecast.getCityname();
                cityForecastListCityForecast.setCityname(city);
                cityForecastListCityForecast = em.merge(cityForecastListCityForecast);
                if (oldCitynameOfCityForecastListCityForecast != null) {
                    oldCitynameOfCityForecastListCityForecast.getCityForecastList().remove(cityForecastListCityForecast);
                    oldCitynameOfCityForecastListCityForecast = em.merge(oldCitynameOfCityForecastListCityForecast);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCity(city.getName()) != null) {
                throw new PreexistingEntityException("City " + city + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(City city) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            City persistentCity = em.find(City.class, city.getName());
            List<CityForecast> cityForecastListOld = persistentCity.getCityForecastList();
            List<CityForecast> cityForecastListNew = city.getCityForecastList();
            List<String> illegalOrphanMessages = null;
            for (CityForecast cityForecastListOldCityForecast : cityForecastListOld) {
                if (!cityForecastListNew.contains(cityForecastListOldCityForecast)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CityForecast " + cityForecastListOldCityForecast + " since its cityname field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<CityForecast> attachedCityForecastListNew = new ArrayList<CityForecast>();
            for (CityForecast cityForecastListNewCityForecastToAttach : cityForecastListNew) {
                cityForecastListNewCityForecastToAttach = em.getReference(cityForecastListNewCityForecastToAttach.getClass(), cityForecastListNewCityForecastToAttach.getId());
                attachedCityForecastListNew.add(cityForecastListNewCityForecastToAttach);
            }
            cityForecastListNew = attachedCityForecastListNew;
            city.setCityForecastList(cityForecastListNew);
            city = em.merge(city);
            for (CityForecast cityForecastListNewCityForecast : cityForecastListNew) {
                if (!cityForecastListOld.contains(cityForecastListNewCityForecast)) {
                    City oldCitynameOfCityForecastListNewCityForecast = cityForecastListNewCityForecast.getCityname();
                    cityForecastListNewCityForecast.setCityname(city);
                    cityForecastListNewCityForecast = em.merge(cityForecastListNewCityForecast);
                    if (oldCitynameOfCityForecastListNewCityForecast != null && !oldCitynameOfCityForecastListNewCityForecast.equals(city)) {
                        oldCitynameOfCityForecastListNewCityForecast.getCityForecastList().remove(cityForecastListNewCityForecast);
                        oldCitynameOfCityForecastListNewCityForecast = em.merge(oldCitynameOfCityForecastListNewCityForecast);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = city.getName();
                if (findCity(id) == null) {
                    throw new NonexistentEntityException("The city with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            City city;
            try {
                city = em.getReference(City.class, id);
                city.getName();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The city with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<CityForecast> cityForecastListOrphanCheck = city.getCityForecastList();
            for (CityForecast cityForecastListOrphanCheckCityForecast : cityForecastListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This City (" + city + ") cannot be destroyed since the CityForecast " + cityForecastListOrphanCheckCityForecast + " in its cityForecastList field has a non-nullable cityname field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(city);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<City> findCityEntities() {
        return findCityEntities(true, -1, -1);
    }

    public List<City> findCityEntities(int maxResults, int firstResult) {
        return findCityEntities(false, maxResults, firstResult);
    }

    private List<City> findCityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(City.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public City findCity(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(City.class, id);
        } finally {
            em.close();
        }
    }

    public int getCityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<City> rt = cq.from(City.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
