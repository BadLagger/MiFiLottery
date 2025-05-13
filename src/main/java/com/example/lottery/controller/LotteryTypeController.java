package com.example.lottery.controller;

import com.example.lottery.dto.LotteryTypeCreateDto;
import com.example.lottery.dto.LotteryTypeResponseDto;
import com.example.lottery.mapper.LotteryTypeMapper;
import com.example.lottery.repository.LotteryTypeRepository;
import com.example.lottery.service.LotteryTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/lottery-types")
@RequiredArgsConstructor
@Tag(name = "Admin - Типы лотерей", description = "Управление типами лотерей. Доступно только администраторам.")
public class LotteryTypeController {
  private final LotteryTypeMapper mapper;
  private final LotteryTypeRepository repository;
  private final LotteryTypeService service;

  @Operation(
          summary = "Создать новый тип лотереи",
          description = "Доступно только для пользователей с ролью ADMIN",
          security = @SecurityRequirement(name = "bearerAuth"),
          responses = {
                  @ApiResponse(responseCode = "201", description = "Тип успешно создан"),
                  @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content(schema = @Schema(hidden = true))),
                  @ApiResponse(responseCode = "409", description = "Тип с таким описанием уже существует", content = @Content(schema = @Schema(hidden = true)))
          }
  )
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<LotteryTypeResponseDto> create(@Valid @RequestBody LotteryTypeCreateDto dto) {
    LotteryTypeResponseDto response = service.createLotteryType(dto);
    return ResponseEntity.created(URI.create("/" + response.getId())).body(response);
  }

  @Operation(
          summary = "Получить список всех типов лотерей",
          description = "Доступно только для пользователей с ролью ADMIN",
          security = @SecurityRequirement(name = "bearerAuth")
  )
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<LotteryTypeResponseDto>> getAll() {
    return ResponseEntity.ok(service.getAll());
  }

  @Operation(
          summary = "Получить тип лотереи по ID",
          description = "Доступно только для пользователей с ролью ADMIN",
          security = @SecurityRequirement(name = "bearerAuth")
  )
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<LotteryTypeResponseDto> getLotteryTypeById(
          @Parameter(name = "id", description = "ID типа лотереи", in = ParameterIn.PATH, required = true)
          @PathVariable Long id) {
    return ResponseEntity.ok(mapper.toDto(repository.getReferenceById(id)));
  }

  @Operation(
          summary = "Удалить тип лотереи по ID",
          description = "Доступно только для пользователей с ролью ADMIN",
          security = @SecurityRequirement(name = "bearerAuth")
  )
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteById(
          @PathVariable @Parameter(name = "id", description = "ID типа лотереи", in = ParameterIn.PATH, required = true) Long id) {

    service.deleteById(id);
    return ResponseEntity.noContent().build();
  }

//  @PreAuthorize("hasRole('ADMIN')")
//  @PostMapping
//  public ResponseEntity<LotteryTypeResponseDto> createLotteryType() {
//    LotteryTypeCreateDto dto = new LotteryTypeCreateDto();
//    dto.setTicketPrice(new BigDecimal("150.00"));
//    dto.setPrizePoolPercentage(0.6);
//    dto.setMinTicket(3);
//
//    dto.setDescription("6 из 45 выбор пользователя");
//    UserSelectedRules rules = new UserSelectedRules();
//    rules.setNumberCount(6);
//    rules.setMinNumber(1);
//    rules.setMaxNumber(45);
//    dto.setAlgorithmRules(rules);
//
//    //    dto.setDescription("Предсозданные (50 наборов) чисел по 4 от 10 до 30");
//    //    FixedPoolRules rules = new FixedPoolRules();
//    //    rules.setNumberCount(4);
//    //    rules.setMinNumber(10);
//    //    rules.setMaxNumber(30);
//    //    rules.setPoolSize(50);
//    //    dto.setAlgorithmRules(rules);
//
//    //        RandomUniqueRules rules = new RandomUniqueRules();
//    //        dto.setDescription("5 из 36 автогенерация");
//    //            rules.setNumberCount(5);
//    //            rules.setMinNumber(1);
//    //            rules.setMaxNumber(36);
//    //            rules.setSorted(true);
//    //            dto.setAlgorithmRules(rules);
//
//    LotteryType entity = mapper.toEntity(dto);
//    entity = repository.save(entity); // Сохраняем в БД
//
//    return new ResponseEntity<>(mapper.toDto(entity), HttpStatus.CREATED);
//  }
}
