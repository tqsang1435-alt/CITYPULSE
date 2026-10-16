package com.citypulse.admin;

import com.citypulse.admin.controller.AdminDashboardController;
import com.citypulse.admin.dto.AdminStatsOverviewResponse;
import com.citypulse.admin.service.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @Mock
    private AdminDashboardService adminDashboardService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AdminDashboardController controller = new AdminDashboardController(adminDashboardService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("ADMIN-01: Endpoint /stats trả về đầy đủ các chỉ số KPI khung quản trị")
    void shouldReturnDashboardStatsSuccessfully() throws Exception {
        AdminStatsOverviewResponse stats = new AdminStatsOverviewResponse(
            5, 2, 8, 1, 10, 25, "OPERATIONAL"
        );

        when(adminDashboardService.getStatsOverview()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/admin/dashboard/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openReportsCount").value(5))
            .andExpect(jsonPath("$.inReviewReportsCount").value(2))
            .andExpect(jsonPath("$.resolvedReportsCount").value(8))
            .andExpect(jsonPath("$.lockedUsersCount").value(1))
            .andExpect(jsonPath("$.totalModerationActionsCount").value(10))
            .andExpect(jsonPath("$.totalAuditLogsCount").value(25))
            .andExpect(jsonPath("$.systemStatus").value("OPERATIONAL"));
    }

    @Test
    @DisplayName("ADMIN-02: Endpoint /health trả về trạng thái HEALTHY cho phân hệ Admin")
    void shouldReturnAdminHealthSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subsystem").value("ADMIN_MODERATION_REPORT"))
            .andExpect(jsonPath("$.status").value("HEALTHY"))
            .andExpect(jsonPath("$.version").value("1.0-week2"));
    }
}
