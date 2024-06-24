package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Report;
import pro.sky.animal_shelter.entity.Users;

import java.time.LocalDate;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findReportByUser(Users user);

    Report findReportByDate(LocalDate currentDate);
}

