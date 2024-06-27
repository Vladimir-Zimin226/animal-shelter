package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.sky.animal_shelter.entity.Report;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "SELECT * FROM report_table WHERE user_id.telegramId = :telegramId", nativeQuery = true)
    List<Report> findAllReportsByTelegramId(@Param("telegramId") Long telegramId);

    Report findReportByDate(LocalDate currentDate);

    @Query(value = "DELETE * FROM report_table WHERE user_id.telegramId = :telegramId", nativeQuery = true)
    void deleteAllByUserId(@Param("telegramId") Long telegramId);

    @Query(value = "SELECT * FROM report_table WHERE create_date = :currentDate", nativeQuery = true)
    List<Report> findAllReportsByCurrentDate(@Param("currentDate") LocalDate currentDate);
}

