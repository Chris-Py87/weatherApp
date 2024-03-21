/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eap.pli24.weatherapp.db;

import com.eap.pli24.weatherapp.db.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


public class CityForecastJpaController implements Serializable {

    public CityForecastJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CityForecast cityForecast) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            City cityname = cityForecast.getCityname();
            if (cityname != null) {
                cityname = em.getReference(cityname.getClass(), cityname.getName());
                cityForecast.setCityname(cityname);
            }
            em.persist(cityForecast);
            if (cityname != null) {
                cityname.getCityForecastList().add(cityForecast);
                cityname = em.merge(cityname);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CityForecast cityForecast) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CityForecast persistentCityForecast = em.find(CityForecast.class, cityForecast.getId());
            City citynameOld = persistentCityForecast.getCityname();
            City citynameNew = cityForecast.getCityname();
            if (citynameNew != null) {
                citynameNew = em.getReference(citynameNew.getClass(), citynameNew.getName());
                cityForecast.setCityname(citynameNew);
            }
            cityForecast = em.merge(cityForecast);
            if (citynameOld != null && !citynameOld.equals(citynameNew)) {
                citynameOld.getCityForecastList().remove(cityForecast);
                citynameOld = em.merge(citynameOld);
            }
            if (citynameNew != null && !citynameNew.equals(citynameOld)) {
                citynameNew.getCityForecastList().add(cityForecast);
                citynameNew = em.merge(citynameNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cityForecast.getId();
                if (findCityForecast(id) == null) {
                    throw new NonexistentEntityException("The cityForecast with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CityForecast cityForecast;
            try {
                cityForecast = em.getReference(CityForecast.class, id);
                cityForecast.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cityForecast with id " + id + " no longer exists.", enfe);
            }
            City cityname = cityForecast.getCityname();
            if (cityname != null) {
                cityname.getCityForecastList().remove(cityForecast);
                cityname = em.merge(cityname);
            }
            em.remove(cityForecast);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CityForecast> findCityForecastEntities() {
        return findCityForecastEntities(true, -1, -1);
    }

    public List<CityForecast> findCityForecastEntities(int maxResults, int firstResult) {
        return findCityForecastEntities(false, maxResults, firstResult);
    }

    private List<CityForecast> findCityForecastEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CityForecast.class));
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

    public CityForecast findCityForecast(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CityForecast.class, id);
        } finally {
            em.close();
        }
    }

    public int getCityForecastCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CityForecast> rt = cq.from(CityForecast.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
