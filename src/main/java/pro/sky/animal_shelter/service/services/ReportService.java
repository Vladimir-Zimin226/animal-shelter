package pro.sky.animal_shelter.service.services;


import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.entity.Report;
import java.util.List;

@Service
public interface ReportService {

    Report createReport(Long userId);

    void deleteReport(Long reportId);

    List<Report> findReportsByTelegramId(String telegramId);

    List<Report> getAllReports();
}
