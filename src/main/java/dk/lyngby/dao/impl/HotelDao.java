package dk.lyngby.dao.impl;

import dk.lyngby.model.Hotel;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HotelDao implements dk.lyngby.dao.IDao<Hotel, Integer> {

    private static HotelDao instance;
    private static EntityManagerFactory emf;

    public static HotelDao getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HotelDao();
        }
        return instance;
    }

    @Override
    public Hotel read(Integer integer) {
       try (var em = emf.createEntityManager())
       {
           return em.find(Hotel.class, integer);
       }
    }

    @Override
    public List<Hotel> readAll() {
        try (var em = emf.createEntityManager())
        {
            var query = em.createQuery("SELECT h FROM Hotel h", Hotel.class);
            return query.getResultList();
        }
    }

    @Override
    public Hotel create(Hotel hotel) {
        try (var em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(hotel);
            em.getTransaction().commit();
            return hotel;
        }
    }

    @Override
    public Hotel update(Integer integer, Hotel hotel) {
        try(var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            var h = em.find(Hotel.class, integer);
            h.setHotelAddress(hotel.getHotelAddress());

            Hotel merge = em.merge(h);
            em.getTransaction().commit();
            return merge;
        }
    }

    @Override
    public void delete(Integer integer) {
        try(var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var hotel = em.find(Hotel.class, integer);
            em.remove(hotel);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try(var em = emf.createEntityManager()) {
            var person = em.find(Hotel.class, integer);
            return person != null;
        }
    }
}
