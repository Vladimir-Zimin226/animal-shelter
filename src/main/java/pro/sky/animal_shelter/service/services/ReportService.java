package pro.sky.animal_shelter.service.services;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.animal_shelter.entity.Report;

import java.io.IOException;
import java.nio.file.Path;

@Service
public interface ReportService {

    Report createReport(Report report);

    void deleteReport(Long reportId);

    Report findReportByUserId(Long userId);

}
