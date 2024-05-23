//package lyngby.dao.impl;
//
//import dk.lyngby.config.HibernateConfig;
//import dk.lyngby.util.PopulateData;
//import dk.lyngby.dao.impl.RoomDao;
//import dk.lyngby.model.Hotel;
//import dk.lyngby.model.Room;
//import jakarta.persistence.EntityManagerFactory;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class RoomDaoTest {
//
//    private static RoomDao roomDao;
//    private static List<Room> rooms;
//    private static EntityManagerFactory emfTest;
//
//    @BeforeAll
//    static void setUpAll() {
//        emfTest = HibernateConfig.getEntityManagerFactory(true);
//        roomDao = RoomDao.getInstance(emfTest);
//    }
//
//    @BeforeEach
//    void setUp() {
//        rooms = PopulateData.populateData(emfTest);
//    }
//
//
//    @Test
//    void addRoomToHotel() {
//
//        // given
//        Room expected = new Room(5000, new BigDecimal(5555), Room.RoomType.SINGLE);
//
//        // when
//        Hotel actually = roomDao.addRoomToHotel(1, expected);
//
//        // then
//        assertTrue(actually.getRooms().contains(expected));
//    }
//
//    @Test
//    void read() {
//
//        // given
//        int roomId = 1;
//
//        // when
//        Room actually = roomDao.read(roomId);
//
//        // then
//        assertEquals(roomId, actually.getRoomId());
//    }
//
//    @Test
//    void readAll() {
//
//        // given
//        int expected = 15;
//
//        // when
//        List<Room> actually = roomDao.readAll();
//
//        // then
//        assertEquals(expected, actually.size());
//    }
//
//
//    @Test
//    void update() {
//
//        // given
//        Room expected = rooms.get(0);
//
//        // when
//        Room actually = roomDao.update(expected.getRoomId(), expected);
//
//        // then
//        assertEquals(expected, actually);
//    }
//
//    @Test
//    void delete() {
//
//        // given
//        int roomId = 1;
//
//        // when
//        roomDao.delete(roomId);
//
//        // then
//        assertNull(roomDao.read(roomId));
//    }
//
//    @Test
//    void validatePrimaryKey() {
//
//            // given
//            int roomId = 1;
//
//            // when
//            boolean actually = roomDao.validatePrimaryKey(roomId);
//
//            // then
//            assertTrue(actually);
//    }
//
//    @Test
//    void validateHotelRoomNumber() {
//
//        // given
//        int roomNumber = 100;
//        int hotelId = 1;
//        boolean expected = true;
//
//        // when
//        Boolean actually = roomDao.validateHotelRoomNumber(roomNumber, hotelId);
//
//        // then
//        assertEquals(expected, actually);
//    }
//}