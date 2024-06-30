package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.animal_shelter.entity.Report;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.exception.ReportNotFoundException;
import pro.sky.animal_shelter.repository.ReportRepository;
import pro.sky.animal_shelter.repository.UsersRepository;
import pro.sky.animal_shelter.service.implementations.ReportServiceImpl;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    Users vova = new Users(1L, "vovandius", "3123123123", "+79585190011", true);
    Report report = createReport(1L, new Users(1L, "vovandius", "3123123123", "+79585190011", true), LocalDate.now());

    private static Report createReport(Long id, Users user, LocalDate date) {
        Report report = new Report();
        report.setId(id);
        report.setUser(user);
        report.setDate(date);
        return report;
    }

    public static final List<Report> REPORTS_LIST = List.of(
            createReport(1L, new Users(1L, "vovandius", "3123123123", "+79585190011", true), LocalDate.now()),
            createReport(2L, new Users(2L, "sashapenya", "3123123121", "+79083659643", false), LocalDate.now().minusDays(1))
    );

    @Test
    void createReportTest() {
        when(usersRepository.findUsersById(1L)).thenReturn(vova);
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        Report createdReport = reportService.createReport(1L);
        assertEquals(report.getUser(), createdReport.getUser());
        assertEquals(report.getDate(), createdReport.getDate());

        verify(usersRepository, times(1)).findUsersById(1L);
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void deleteReportTest() {
        when(reportRepository.existsById(1L)).thenReturn(true);

        reportService.deleteReport(1L);

        verify(reportRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteReportNonExistentReportShouldThrowExceptionTest() {
        when(reportRepository.existsById(1L)).thenReturn(false);

        assertThrows(ReportNotFoundException.class, () -> {
            reportService.deleteReport(1L);
        });
    }

    @Test
    void findReportsByTelegramIdTest() {
        when(reportRepository.findAllReportsByTelegramId(1L)).thenReturn(REPORTS_LIST);

        List<Report> reports = reportService.findReportsByTelegramId(1L);
        assertEquals(REPORTS_LIST, reports);

        verify(reportRepository, times(1)).findAllReportsByTelegramId(1L);
    }

    @Test
    void getAllReportsTest() {
        when(reportRepository.findAll()).thenReturn(REPORTS_LIST);

        List<Report> reports = reportService.getAllReports();
        assertEquals(REPORTS_LIST, reports);

        verify(reportRepository, times(1)).findAll();
    }
}
