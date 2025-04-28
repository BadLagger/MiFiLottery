package com.example.lottery.mapper;

import com.example.lottery.dto.TicketDto;
import com.example.lottery.dto.TicketStatus;
import com.example.lottery.entity.Ticket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", uses = ObjectMapper.class)
public interface TicketMapper {

  TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);

  @Mapping(source = "data", target = "data", qualifiedByName = "mapDataToJsonNode")
  @Mapping(source = "userId", target = "userId.id")
  @Mapping(source = "drawId", target = "drawId.id")
  @Mapping(source = "status", target = "status", qualifiedByName = "mapStatusToEntity")
  Ticket toEntity(TicketDto ticketDto);

  @Mapping(source = "data", target = "data", qualifiedByName = "mapJsonNodeToData")
  @Mapping(source = "userId.id", target = "userId")
  @Mapping(source = "drawId.id", target = "drawId")
  @Mapping(source = "status", target = "status", qualifiedByName = "mapStatusToDto")
  TicketDto toDto(Ticket ticket);

  @Named("mapDataToJsonNode")
  default JsonNode mapDataToJsonNode(Map<String, Object> data) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.valueToTree(data);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert Map to JsonNode", e);
    }
  }

  @Named("mapJsonNodeToData")
  default Map<String, Object> mapJsonNodeToData(JsonNode data) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.convertValue(data, Map.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert JsonNode to Map", e);
    }
  }

  @Named("mapStatusToEntity")
  default Ticket.Status mapStatusToEntity(TicketStatus status) {
    return status == null ? null : Ticket.Status.valueOf(status.name());
  }

  @Named("mapStatusToDto")
  default TicketStatus mapStatusToDto(Ticket.Status status) {
    return status == null ? null : TicketStatus.valueOf(status.name());
  }
}
