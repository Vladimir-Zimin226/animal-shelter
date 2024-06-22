package pro.sky.animal_shelter.service.implementations;

import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.entity.Report;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.exception.ReportNotFoundException;
import pro.sky.animal_shelter.repository.ReportRepository;
import pro.sky.animal_shelter.repository.UsersRepository;
import pro.sky.animal_shelter.service.services.ReportService;

import java.util.List;


@Service
public class ReportServiceImpl implements ReportService {

    private final UsersRepository userRepository;
    private final ReportRepository reportRepository;

    public ReportServiceImpl(UsersRepository userRepository, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public Report createReport(Long userId) {
        Users existingUser = userRepository.findUsersById(userId);

        Report report = reportRepository.findReportByUser(existingUser);
        if (report == null) {
            report = new Report();
            report.setUser(existingUser);
            reportRepository.save(report);
        }
        return report;
    }

    @Override
    public void deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new ReportNotFoundException();
        }
        reportRepository.deleteById(reportId);
    }


    @Override
    public Report findReportByUserId(Users user) {

        return reportRepository.findReportByUser(user);
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

}
