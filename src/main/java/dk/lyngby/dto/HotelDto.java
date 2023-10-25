package dk.lyngby.dto;

import dk.lyngby.model.Hotel;
import dk.lyngby.model.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class HotelDto {

    private Integer id;
    private String hotelName;
    private String hotelAddress;
    private Set<Integer> rooms;

    public HotelDto(Hotel hotel) {
        this.id = hotel.getId();
        this.hotelName = hotel.getHotelName();
        this.hotelAddress = hotel.getHotelAddress();
        this.rooms = hotel.getRooms() != null ? getRooms(hotel.getRooms()) : new HashSet<>();
    }

    public static List<HotelDto> toHotelDTOList(List<Hotel> hotels) {
        return hotels.stream().map(HotelDto::new).collect(Collectors.toList());
    }

    public Set<Integer> getRooms(Set<Room> rooms) {
        return rooms.stream().map(Room::getRoomNumber).collect(Collectors.toSet());
    }

}
