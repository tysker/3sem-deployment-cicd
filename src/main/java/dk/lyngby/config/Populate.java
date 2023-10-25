package dk.lyngby.config;


import dk.lyngby.model.Hotel;
import dk.lyngby.model.Room;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Populate {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        populateData(emf);
    }

    public static List<Room> populateData(EntityManagerFactory emf) {

        Set<Room> calRooms = getCalRooms();
        Set<Room> hilRooms = getHilRooms();
        Set<Room> motelRooms = getMotelRooms();

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.createNamedQuery("Room.deleteAllRows").executeUpdate();
            em.createNamedQuery("Hotel.deleteAllRows").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE room_room_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE hotel_hotel_id_seq RESTART WITH 1").executeUpdate();

            Hotel california = new Hotel("Hotel California", "California", Hotel.HotelType.LUXURY);
            Hotel hilton = new Hotel("Hilton", "Copenhagen", Hotel.HotelType.STANDARD);
            Hotel motel = new Hotel("Motel", "Copenhagen", Hotel.HotelType.BUDGET);
            california.setRooms(calRooms);
            hilton.setRooms(hilRooms);
            motel.setRooms(motelRooms);
            em.persist(california);
            em.persist(hilton);
            em.persist(motel);
            em.getTransaction().commit();

            return new ArrayList<>(calRooms);
        }
    }

    @NotNull
    private static Set<Room> getCalRooms() {
        Room r100 = new Room(100, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r101 = new Room(101, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r102 = new Room(102, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r103 = new Room(103, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r104 = new Room(104, new BigDecimal(3200), Room.RoomType.DOUBLE);
        Room r105 = new Room(105, new BigDecimal(4500), Room.RoomType.SUITE);

        Room[] roomArray = {r100, r101, r102, r103, r104, r105};
        return Set.of(roomArray);
    }

    @NotNull
    private static Set<Room> getHilRooms() {
        Room r111 = new Room(111, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r112 = new Room(112, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r113 = new Room(113, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r114 = new Room(114, new BigDecimal(2520), Room.RoomType.DOUBLE);
        Room r115 = new Room(115, new BigDecimal(3200), Room.RoomType.DOUBLE);
        Room r116 = new Room(116, new BigDecimal(4500), Room.RoomType.SUITE);

        Room[] roomArray = {r111, r112, r113, r114, r115, r116};
        return Set.of(roomArray);
    }

    @NotNull
    private static Set<Room> getMotelRooms() {
        Room r200 = new Room(200, new BigDecimal(1250), Room.RoomType.DOUBLE);
        Room r201 = new Room(201, new BigDecimal(1250), Room.RoomType.DOUBLE);
        Room r202 = new Room(202, new BigDecimal(1000), Room.RoomType.SINGLE);

        Room[] roomArray = {r200, r201, r202};
        return Set.of(roomArray);
    }
}
