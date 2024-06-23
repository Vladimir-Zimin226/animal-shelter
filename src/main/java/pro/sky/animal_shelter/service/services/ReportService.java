package pro.sky.animal_shelter.service.services;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.animal_shelter.entity.Report;
import pro.sky.animal_shelter.entity.Users;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public interface ReportService {

    Report createReport(Long userId);

    void deleteReport(Long reportId);

    Report findReportByUserId(Users user);

    List<Report> getAllReports();
}
