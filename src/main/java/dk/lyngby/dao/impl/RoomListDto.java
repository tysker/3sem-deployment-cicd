package dk.lyngby.dao.impl;

import dk.lyngby.dto.RoomDto;

import java.util.List;

public record RoomListDto(int listSize, List<RoomDto> roomDtos) {}
