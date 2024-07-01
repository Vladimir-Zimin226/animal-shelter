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
import org.springframework.web.multipart.MultipartFile;
import pro.sky.animal_shelter.entity.Report;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.service.services.ReportService;

import java.util.List;

@Tag(name = "Контроллер для работы отчётами",
        description = "Создание отчёта.  " +
                "Удаление отчётов из базы.  " +
                "Получение отчётов из базы.  " +
                "  ")
@RequestMapping("/reports")
@RestController
public class ReportController {

    public final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    @Operation(summary = "Создание нового отчёта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Users.class)
                            )
                    )
            })
    @PostMapping("/create")
    public ResponseEntity<Users> createReport(@RequestParam Long userId, @RequestParam MultipartFile file) {
        return ResponseEntity.ok(reportService.createReport(userId).getUser());
    }

    @Operation(summary = "Удаление отчёта из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Void.class)
                            )
                    )
            })
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReportById(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение всех отчётов из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            })
    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Report>> getAllReportsFromCurrentUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.findReportsByTelegramId(userId));
    }

}