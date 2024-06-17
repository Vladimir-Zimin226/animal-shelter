package pro.sky.animal_shelter.service.implementations;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.entity.Report;
import pro.sky.animal_shelter.repository.ReportRepository;
import pro.sky.animal_shelter.service.services.ReportService;



@Service
@Transactional
public class ReportServiceImpl implements ReportService {


    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    @Override
    public Report createReport(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("User cannot be null");
        } else {
            return reportRepository.save(report);
        }

    }

    @Override
    public void deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new IllegalArgumentException("User not found with id: " + reportId);
        }
        reportRepository.deleteById(reportId);
    }

    @Override
    public Report findReportByUserId(Long userId) {
        return reportRepository.findReportByUserId(userId);
    }

}
