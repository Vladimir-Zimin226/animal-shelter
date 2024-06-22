package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Report;
import pro.sky.animal_shelter.entity.Users;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findReportByUser(Users user);
}

