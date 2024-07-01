package pro.sky.animal_shelter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.animal_shelter.entity.Report;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.service.implementations.ReportServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
public class ReportControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportServiceImpl reportService;

    @Test
    public void testCreateReport() throws Exception {
        Users testUser = new Users();
        Report testReport = new Report();
        testReport.setUser(testUser);
        given(reportService.createReport(anyLong())).willReturn(testReport);

        MockMultipartFile file = new MockMultipartFile("file", "report.txt", "text/plain", "Report content".getBytes());

        mockMvc.perform(multipart("/reports/create")
                        .file(file)
                        .param("userId", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteReportById() throws Exception {
        mockMvc.perform(delete("/reports/1"))
                .andExpect(status().isOk());

        verify(reportService).deleteReport(1L);
    }

    @Test
    public void testGetAllReports() throws Exception {
        List<Report> reportsList = Arrays.asList(new Report(), new Report());
        given(reportService.getAllReports()).willReturn(reportsList);

        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetAllReportsFromCurrentUser() throws Exception {
        List<Report> reportsList = Arrays.asList(new Report(), new Report());
        Users newUser = new Users();
        newUser.setTelegramId("123");
        reportsList.get(0).setUser(newUser);
        reportsList.get(1).setUser(newUser);
        given(reportService.findReportsByTelegramId(123L)).willReturn(reportsList);

        mockMvc.perform(get("/reports/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}