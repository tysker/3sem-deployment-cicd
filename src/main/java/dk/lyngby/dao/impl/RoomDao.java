package dk.lyngby.dao.impl;


import dk.lyngby.model.Hotel;
import dk.lyngby.model.Room;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RoomDao implements dk.lyngby.dao.IDao<Room, Integer> {

    private static RoomDao instance;
    private static EntityManagerFactory emf;

    public static RoomDao getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new RoomDao();
        }
        return instance;
    }

    public Hotel addRoomToHotel(Integer hotelId, Room room ) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var hotel = em.find(Hotel.class, hotelId);
            hotel.addRoom(room);
            em.persist(room);
            Hotel merge = em.merge(hotel);
            em.getTransaction().commit();
            return merge;
        }
    }

    @Override
    public Room read(Integer integer) {
        try (var em = emf.createEntityManager()) {
            return em.find(Room.class, integer);
        }
    }

    @Override
    public List<Room> readAll() {
        try (var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT r FROM Room r", Room.class);
            return query.getResultList();
        }
    }

    public List<Room> readAll(int hotelId) {
        try (var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT r FROM Room r WHERE r.hotel.id = :hotelId", Room.class)
                    .setParameter("hotelId", hotelId);
            return query.getResultList();
        }
    }

    @Override
    public Room create(Room room) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(room);
            em.getTransaction().commit();
            return room;
        }
    }

    @Override
    public Room update(Integer integer, Room room) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            var r = em.find(Room.class, integer);
            r.setRoomType(room.getRoomType());
            r.setRoomPrice(room.getRoomPrice());

            Room merge = em.merge(r);
            em.getTransaction().commit();
            return merge;
        }
    }

    @Override
    public void delete(Integer integer) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var room = em.find(Room.class, integer);
            em.remove(room);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (var em = emf.createEntityManager()) {
            var room = em.find(Room.class, integer);
            return room != null;
        }
    }

    public Function<Integer, Boolean> validateHotelRoomNumber = (roomNumber) -> {
        try (var em = emf.createEntityManager()) {
            var room = em.find(Room.class, roomNumber);
            return room != null;
        }
    };

    public Boolean validateHotelRoomNumber(Integer roomNumber, Integer hotelId) {
        try (var em = emf.createEntityManager()) {
            var hotel = em.find(Hotel.class, hotelId);
            return hotel.getRooms().stream().anyMatch(r -> r.getRoomNumber().equals(roomNumber));
        }
    }

}
