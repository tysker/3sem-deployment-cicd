package dk.lyngby.dto;

import dk.lyngby.model.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class RoomDto {
    private Integer roomNumber;
    private Integer hotelId;
    private BigDecimal roomPrice;

    public RoomDto(Room room) {
        this.roomNumber = room.getRoomNumber();
        this.roomPrice = BigDecimal.valueOf(room.getRoomPrice().intValue());
        this.hotelId = room.getHotel().getId() != null ? room.getHotel().getId() : null;
    }

    public static List<RoomDto> toRoomDTOList(List<Room> rooms) {
        return List.of(rooms.stream().map(RoomDto::new).toArray(RoomDto[]::new));
    }
}
