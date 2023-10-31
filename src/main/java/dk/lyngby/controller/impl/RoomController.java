package dk.lyngby.controller.impl;

import dk.lyngby.config.HibernateConfig;
import dk.lyngby.controller.IController;
import dk.lyngby.dao.impl.RoomDao;
import dk.lyngby.dao.impl.RoomListDto;
import dk.lyngby.dto.HotelDto;
import dk.lyngby.dto.RoomDto;
import dk.lyngby.exception.Message;
import dk.lyngby.model.Hotel;
import dk.lyngby.model.Room;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class RoomController implements IController<Room, Integer> {

    private RoomDao dao;

    public RoomController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory(false);
        this.dao = RoomDao.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // request
        int id = getId(ctx);

        // entity
        Room room = dao.read(id);
        // dto
        RoomDto roomDto = new RoomDto(room);
        // response
        ctx.res().setStatus(200);
        ctx.json(roomDto, RoomDto.class);

    }

    @Override
    public void readAll(Context ctx) {

        int hotelId = getId(ctx);

        Map<String, List<String>> queryParamMap = ctx.queryParamMap();

        // entity
        List<Room> rooms = dao.readAll(hotelId);

        if(!queryParamMap.isEmpty()) {
            Set<String> keySet = new HashSet<>(queryParamMap.keySet());

            if(keySet.contains("min-price") && keySet.contains("max-price")) {
                double minPrice = Double.parseDouble(queryParamMap.get("min-price").get(0));
                double maxPrice = Double.parseDouble(queryParamMap.get("max-price").get(0));
                rooms = rooms.stream().filter(r -> r.getRoomPrice().doubleValue() >= minPrice && r.getRoomPrice().doubleValue() <= maxPrice).toList();
            }
        }

        // dto
        List<RoomDto> roomDtos = RoomDto.toRoomDTOList(rooms);
        RoomListDto roomListDto = new RoomListDto(roomDtos.size(), roomDtos);

        // response
        ctx.res().setStatus(200);
        ctx.json(roomListDto, RoomListDto.class);

    }

    @Override
    public void create(Context ctx) {
        // request
        Room jsonRequest = validateEntity(ctx);

        int hotelId = getId(ctx);
        Boolean hasRoom = validateHotelRoomNumber.apply(jsonRequest.getRoomNumber(), hotelId);

        if (hasRoom) {
            ctx.res().setStatus(400);
            ctx.json(new Message(400, "Room number already in use by hotel"));
            return;
        }

        // entity
        Hotel hotel = dao.addRoomToHotel(hotelId, jsonRequest);
        // dto
        HotelDto hotelDto = new HotelDto(hotel);
        // response
        ctx.res().setStatus(201);
        ctx.json(hotelDto, HotelDto.class);
    }

    @Override
    public void update(Context ctx) {
        // request
        int id = getId(ctx);
        // entity
        Room update = dao.update(id, validateEntity(ctx));
        // dto
        RoomDto roomDto = new RoomDto(update);
        // response
        ctx.res().setStatus(200);
        ctx.json(roomDto, RoomDto.class);
    }

    @Override
    public void delete(Context ctx) {
        // request
        int id = getId(ctx);
        // entity
        dao.delete(id);
        // response
        ctx.res().setStatus(204);
    }

    @NotNull
    private Integer getId(Context ctx) {
        return ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {return dao.validatePrimaryKey(integer);}

    // Checks if the room number is already in use by the hotel
    BiFunction<Integer, Integer, Boolean> validateHotelRoomNumber = (roomNumber, hotelId) -> dao.validateHotelRoomNumber(roomNumber, hotelId);

    @Override
    public Room validateEntity(Context ctx) {
        return ctx.bodyValidator(Room.class)
                .check(r -> r.getRoomNumber() != null && r.getRoomNumber() > 0, "Not a valid room number")
                .check(r -> r.getRoomType() != null, "Not a valid room type")
                .check(r -> r.getRoomPrice() != null , "Not a valid price")
                .get();
    }
}
