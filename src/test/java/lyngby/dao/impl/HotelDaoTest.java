package lyngby.dao.impl;

import dk.lyngby.config.HibernateConfig;
import dk.lyngby.util.PopulateData;
import dk.lyngby.dao.impl.HotelDao;
import dk.lyngby.model.Hotel;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HotelDaoTest {

    private static HotelDao hotelDao;
    private static EntityManagerFactory emfTest;

    @BeforeAll
    static void setUpAll() {
        emfTest = HibernateConfig.getEntityManagerFactory(true);
        hotelDao = HotelDao.getInstance(emfTest);
    }

    @BeforeEach
    void setUp() {
        PopulateData.populateData(emfTest);
    }


    @Test
    void read() {

        // given
        Hotel expected = new Hotel("Hotel California", "California", Hotel.HotelType.LUXURY);

        // when
        Hotel actually = hotelDao.read(1);

        // then
        assertEquals(expected, actually);
    }

    @Test
    void readAll() {

        // given
        int expected = 3;

        // when
        List<Hotel> actually = hotelDao.readAll();

        // then
        assertEquals(expected, actually.size());
    }

    @Test
    void create() {

        // given
        Hotel expected = new Hotel("Radisson", "Stuttgart", Hotel.HotelType.STANDARD);

        // when
        Hotel actually = hotelDao.create(expected);

        // then
        assertEquals(expected, actually);
    }

    @Test
    void update() {

        // given
        Hotel expected = new Hotel("Hotel California", "California Boulevard", Hotel.HotelType.LUXURY);

        // when
        Hotel actually = hotelDao.update(1, expected);

        // then
        assertEquals(expected, actually);
    }

    @Test
    void delete() {

        // given
        int hotelId = 1;

        // when
        hotelDao.delete(hotelId);

        // then
        assertNull(hotelDao.read(hotelId));
    }

    @Test
    void validatePrimaryKey() {

        // given
        boolean expected = true;

        // when
        boolean actually = hotelDao.validatePrimaryKey(1);

        // then
        assertEquals(expected, actually);
    }
}