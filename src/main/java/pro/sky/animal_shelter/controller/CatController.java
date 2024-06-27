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
import pro.sky.animal_shelter.entity.Cats;
import pro.sky.animal_shelter.service.services.CatService;

import java.util.List;

@Tag(name = "Контроллер для работы с кошками",
        description = "Создание кошки. " +
                "Редактирование кошки. " +
                "Получение всех кошек из базы. " +
                "Удаление кошек из базы и прочие методы. ")
@RequestMapping("/cats")
@RestController
public class CatController {

    private final CatService catService;

    public CatController(CatService catService) {
        this.catService = catService;
    }

    @Operation(summary = "Создание кошки",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Создать запись о кошке",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Cats.class)
                    )
            )
    )
    @PostMapping("/create")
    public ResponseEntity<Cats> createCat(@RequestBody Cats cat) {
        return ResponseEntity.ok(catService.createCat(cat));
    }

    @Operation(summary = "Редактирование кошки",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактировать запись о кошке",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Cats.class)
                    )
            )
    )
    @PutMapping("/update/{catId}")
    public ResponseEntity<Cats> editCat(@PathVariable long catId, @RequestBody Cats cat) {
        Cats updatedCat = catService.updateCat(catId, cat);
        if (updatedCat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCat);
    }

    @Operation(summary = "Получаем всех кошек из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "найденные кошки",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cats.class))
                            )
                    )
            })
    @GetMapping("/all")
    public ResponseEntity<List<Cats>> getAllCats() {
        return ResponseEntity.ok(catService.getAllCats());
    }

    @Operation(summary = "Удаляем кошку из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Cats.class)
                            )
                    )
            })
    @DeleteMapping("/delete/{catId}")
    public ResponseEntity<Void> deleteCatById(@PathVariable Long catId) {
        catService.deleteCatById(catId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получаем всех кошек из базы данных, которые ищут хозяина",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "найденные кошки",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cats.class))
                            )
                    )
            })
    @GetMapping("/all_cats_who_need_owner")
    public ResponseEntity<List<Cats>> getAllCatsWhoNeededOwner() {
        return ResponseEntity.ok(catService.getAllWhoNeededOwner());
    }

    @Operation(summary = "Получаем всех кошек из базы данных, которые ищут спонсора",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "найденные кошки",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cats.class))
                            )
                    )
            })
    @GetMapping("/all_cats_who_need_curator")
    public ResponseEntity<List<Cats>> getAllCatsWhoNeededCurator() {
        return ResponseEntity.ok(catService.getAllWhoNeededCurator());
    }

    @Operation(summary = "Получаем всех кошек из базы данных, которые нашли дом",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "найденные кошки",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cats.class))
                            )
                    )
            })
    @GetMapping("/all_cats_who_found_home")
    public ResponseEntity<List<Cats>> getAllCatsWhoFoundHome() {
        return ResponseEntity.ok(catService.getAllWhoFoundHome());
    }
}
