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
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.service.services.UserService;

import java.util.List;


@Tag(name = "Контроллер для работы с пользователями",
        description = "Создание пользователя.  " +
                "Редактирование пользователя.  " +
                "Получение пользователей из базы.  " +
                "Удаление пользователей из базы.  ")
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Создание пользователя",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Создать запись о пользователе",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Users.class)
                    )
            )
    )

    @PostMapping("/create")
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @Operation(summary = "Редактирование пользователя",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактировать запись о пользователе",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Users.class)
                    )
            )
    )
    @PutMapping("/update/{userId}")
    public ResponseEntity<Users> editUser(@PathVariable long userId, @RequestBody Users user) {
        Users updatedUser = userService.updateUser(userId, user);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Получаем всех волонтеров из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "найденные волонтеры",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Users.class))
                            )
                    )
            })
    @GetMapping("/all_volunteer")
    public ResponseEntity<List<Users>> getAllVolunteers() {
        return ResponseEntity.ok(userService.getAllVolunteer());
    }

    @Operation(summary = "Получаем всех пользователей из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "найденные пользователи",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Users.class))
                            )
                    )
            })
    @GetMapping("/all")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @Operation(summary = "Удаляем пользователя из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Users.class)
                            )
                    )
            })

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok().build();
    }
}
