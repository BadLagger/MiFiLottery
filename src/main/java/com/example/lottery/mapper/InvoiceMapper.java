package com.example.lottery.mapper;

import com.example.lottery.dto.InvoiceCreateDto;
import com.example.lottery.dto.InvoiceResponseDto;
import com.example.lottery.entity.Invoice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    @Mapping(target = "ticketData", expression = "java(mapTicketDataToJson(invoiceCreateDto.getDrawId(), invoiceCreateDto.getNumbers()))")
    Invoice toEntity(InvoiceCreateDto invoiceCreateDto);

    @Mapping(target = "numbers", expression = "java(mapJsonToNumbers(invoice.getTicketData()))")
    @Mapping(target = "drawId", expression = "java(extractDrawIdFromJson(invoice.getTicketData()))")
    @Mapping(source = "user.id", target = "userId")
    InvoiceResponseDto toDto(Invoice invoice);


    default String mapTicketDataToJson(@NotNull Long drawId, List<Integer> numbers) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> result = new LinkedHashMap<>(); //LinkedHashMap для сохранения порядка
            result.put("drawId", drawId);
            result.put("numbers", numbers);
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации: drawId=" + drawId + ", numbers=" + numbers, e);
        }
    }

    default List<Integer> mapJsonToNumbers(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.readValue(
                    json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            List<?> numbersRaw = (List<?>) map.get("numbers");
            if (numbersRaw == null) return new ArrayList<>();
            List<Integer> numbers = new ArrayList<>();
            for (Object obj : numbersRaw) {
                numbers.add(((Number)obj).intValue());
            }
            return numbers;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка парсинга чисел: " + json, e);
        }
    }

    default Long extractDrawIdFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.readValue(
                    json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            Object drawIdObj = map.get("drawId");
            if (drawIdObj instanceof Number) {
                return ((Number) drawIdObj).longValue();
            }
            if (drawIdObj instanceof String) {
                return Long.parseLong((String) drawIdObj);
            }
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка парсинга drawId: " + json, e);
        }
    }

}


