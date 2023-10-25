package dk.lyngby.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "hotel")
@NamedQueries({
        @NamedQuery(name = "Hotel.deleteAllRows", query = "DELETE from Hotel"),
})
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "hotel_name", nullable = false, unique = true)
    private String hotelName;

    @Setter
    @Column(name = "hotel_address", nullable = false)
    private String hotelAddress;

    @Enumerated(EnumType.STRING)
    @Setter
    @Column(name = "hotel_type", nullable = false)
    private HotelType hotelType;

    @ToString.Exclude
    @OneToMany(mappedBy = "hotel", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<Room> rooms = new HashSet<>();

    public Hotel(String hotelName, String hotelAddress, HotelType hotelType) {
        this.hotelName = hotelName;
        this.hotelAddress = hotelAddress;
        this.hotelType = hotelType;
    }

    public void setRooms(Set<Room> rooms) {
        if (rooms != null) {
            this.rooms = rooms;
            for (Room room : rooms) {
                room.setHotel(this);
            }
        }
    }

    public void addRoom(Room room) {
        if (room != null) {
            this.rooms.add(room);
            room.setHotel(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return Objects.equals(hotelName, hotel.hotelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelName);
    }

    public enum HotelType {
        BUDGET,
        STANDARD,
        LUXURY
    }
}
