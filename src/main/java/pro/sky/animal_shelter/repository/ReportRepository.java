package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Report findReportByUserId(Long id);

}
