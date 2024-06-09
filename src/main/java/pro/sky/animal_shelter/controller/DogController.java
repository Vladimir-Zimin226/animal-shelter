package pro.sky.animal_shelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.animal_shelter.entity.Dogs;
import pro.sky.animal_shelter.service.DogService;

import java.util.List;

@Tag(name = "Контроллер для работы с песиками",
        description = "Создание песика.  " +
                "Редактирование песика.  " +
                "Получение всех песиков из базы.  " +
                "Удаление песиков из базы и прочие методы. ")
@RequestMapping("/dogs")
@RestController
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @Operation(summary = "Создание песика",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Создать запись о песике",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Dogs.class)
                    )
            )
    )

    @PostMapping("/create")
    public ResponseEntity<Dogs> createDog(@RequestBody Dogs dog) {
        return ResponseEntity.ok(dogService.createDog(dog));
    }

    @Operation(summary = "Редактирование песика",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактировать запись о собачке",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Dogs.class)
                    )
            )
    )
    @PutMapping("/update/{dogId}")
    public ResponseEntity<Dogs> editDog(@PathVariable long dogId, @RequestBody Dogs dog) {
        Dogs updatedDog = dogService.updateDog(dogId, dog);
        if (updatedDog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedDog);
    }

    @Operation(summary = "Получаем всех песиков из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "найденные собачки",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dogs.class))
                            )
                    )
            })
    @GetMapping("/all")
    public ResponseEntity<List<Dogs>> getAllDogs() {
        return ResponseEntity.ok(dogService.getAllDogs());
    }

    @Operation(summary = "Удаляем песика из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Dogs .class)
                            )
                    )
            })

    @DeleteMapping("/{dogId}")
    public ResponseEntity<Void> deleteDogById(@PathVariable Long dogId) {
        dogService.deleteDogById(dogId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получаем всех песиков из базы данных, которые ищут хозяина",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "найденные песики",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dogs.class))
                            )
                    )
            })
    @GetMapping("/all_dogs_who_need_owner")
    public ResponseEntity<List<Dogs>> getAllDogsWhoNeededOwner() {
        return ResponseEntity.ok(dogService.getAllWhoNeededOwner());
    }

    @Operation(summary = "Получаем всех песиков из базы данных, которые ищут спонсора",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "найденные песики",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dogs.class))
                            )
                    )
            })
    @GetMapping("/all_dogs_who_need_curator")
    public ResponseEntity<List<Dogs>> getAllDogsWhoNeededCurator() {
        return ResponseEntity.ok(dogService.getAllWhoNeededCurator());
    }

    @Operation(summary = "Получаем всех песиков из базы данных, которые нащли дом",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "найденные песики",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dogs.class))
                            )
                    )
            })
    @GetMapping("/all_dogs_who_found_home")
    public ResponseEntity<List<Dogs>> getAllDogsWhoFoundHome() {
        return ResponseEntity.ok(dogService.getAllWhoFoundHome());
    }

}
