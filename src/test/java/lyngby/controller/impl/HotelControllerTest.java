package lyngby.controller.impl;

import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.config.Populate;
import dk.lyngby.dto.HotelDto;
import dk.lyngby.exception.Message;
import dk.lyngby.exception.ValidationMessage;
import dk.lyngby.model.Hotel;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import utility.TestUtility;

import java.util.LinkedHashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotelControllerTest {

    private static Javalin app;
    private static EntityManagerFactory emfTest;
    private static Object adminToken;
    private static final String BASE_URL = "http://localhost:7777/api/v1";

    @BeforeAll
    static void beforeAll() {

        // Setup test database
        HibernateConfig.setTest(true);
        emfTest = HibernateConfig.getEntityManagerFactory();
        TestUtility.createUserTestData(emfTest);

        // Start server
        app = Javalin.create();
        ApplicationConfig.startServer(app, 7777);

        // Get token
        adminToken = TestUtility.getAdminToken();

    }

    @BeforeEach
    void setUp() {
        Populate.populateData(emfTest);
    }

    @AfterAll
    static void tearDown() {
        HibernateConfig.setTest(false);
        ApplicationConfig.stopServer(app);
    }

    @Test
    @DisplayName("Read hotel by id")
    void read() {

        // given
        int hotelId = 3;

        // when
        given()
                .contentType("application/json")
                .when()
                .get(BASE_URL + "/hotels/" + hotelId)
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(hotelId))
                .body("hotelName", equalTo("Motel"))
                .body("hotelAddress", equalTo("Copenhagen"))
                .body("rooms", hasSize(3));

        // then
    }

    @Test
    @DisplayName("Read all hotels")
    void readAll() {
        // given
        int listSize = 3;

        // when
        given()
                .contentType("application/json")
                .header("Authorization", adminToken)
                .when()
                .get(BASE_URL + "/hotels")
                .then()
                .assertThat()
                .statusCode(200)
                // then
                .body("size()", equalTo(listSize));
    }

    @Test
    @DisplayName("Create new hotel without rooms")
    void create() {

        // given
        Hotel h3 = new Hotel("Cab-inn", "Østergade 2", Hotel.HotelType.BUDGET);
        int hotelId = 4;
        System.out.println(adminToken);

        // when
        HotelDto hotel =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", adminToken)
                        .body(h3)
                        .when()
                        .post(BASE_URL + "/hotels")
                        .then()
                        .statusCode(201)
                        .body("id", equalTo(hotelId))
                        .body("hotelName", equalTo("Cab-inn"))
                        .body("hotelAddress", equalTo("Østergade 2"))
                        .body("rooms", hasSize(0))
                        .extract().body().as(HotelDto.class);

        // then
        assertEquals(hotel.getHotelName(), h3.getHotelName());
    }

    @Test
    void getValidateEntityExceptionWhenCreatingHotel() {

        // given
        String jsonHotelWithoutName = "{\"hotelAddress\":\"Østergade 2\",\"hotelType\":\"BUDGET\"}";

        // when
        ValidationMessage error =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", adminToken)
                        .body(jsonHotelWithoutName)
                        .when()
                        .post(BASE_URL + "/hotels")
                        .then()
                        .statusCode(400)
                        .extract().body().as(ValidationMessage.class);

        // then
        assertEquals(error.message(), "Hotel name must be set");
    }

    @Test
    void getConstraintViolationExceptionWhenCreatingHotel() {

        // given
        Hotel hilton = new Hotel("Hilton", "Copenhagen", Hotel.HotelType.STANDARD);

        // when
        Message msg =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", adminToken)
                        .body(hilton)
                        .when()
                        .post(BASE_URL + "/hotels")
                        .then()
                        .statusCode(500)
                        .extract().body().as(Message.class);

        assertEquals(msg.status(), 0);
        assertTrue(msg.message().contains("duplicate key value violates unique constraint"));

    }

    @Test
    @DisplayName("Update hotel address by id")
    void update() {

        // given
        Hotel update = new Hotel("Hotel California", "California Boulevard", Hotel.HotelType.LUXURY);

        // when
        HotelDto updHotel =
                given()
                        .contentType("application/json")
                        .header("Authorization", adminToken)
                        .body(update)
                        .when()
                        .put(BASE_URL + "/hotels/1")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract().body().as(HotelDto.class);

        // then
        assertEquals(updHotel.getHotelAddress(), update.getHotelAddress());
    }

    @Test
    void delete() {

        // given
        int hotelId = 2;

        // when
        given()
                .contentType("application/json")
                .header("Authorization", adminToken)
                .when()
                .delete(BASE_URL + "/hotels/" + hotelId)
                .then()
                .assertThat()
                .statusCode(204);

        // then
        given()
                .contentType("application/json")
                .header("Authorization", adminToken)
                .when()
                .get(BASE_URL + "/hotels/" + hotelId)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    @DisplayName("Call the delete method with a non-existing id")
    void delteWithWNonExistingId() {

        // given
        int hotelId = 200;

        // when
        ValidationMessage error =
                given()
                        .contentType("application/json")
                        .header("Authorization", adminToken)
                        .when()
                        .delete(BASE_URL + "/hotels/" + hotelId)
                        .then()
                        .assertThat()
                        .statusCode(404)
                        .extract().body().as(ValidationMessage.class);

        // then
        assertEquals(error.message(), "Not a valid id");
        assertEquals(error.value(), hotelId);
        assertEquals(error.args(), new LinkedHashMap<>());
    }


}