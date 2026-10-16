package com.citypulse.trip;

import com.citypulse.common.exception.AccessDeniedException;
import com.citypulse.common.exception.BusinessRuleException;
import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripProposal;
import com.citypulse.trip.repository.TripProposalRepository;
import com.citypulse.trip.repository.TripRepository;
import com.citypulse.trip.service.*;
import com.citypulse.user.entity.AppUser;
import com.citypulse.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripProposalService Tests (UC-05, UC-06)")
class TripProposalServiceTest {

    @Mock TripRepository         tripRepository;
    @Mock AppUserRepository      userRepository;
    @Mock TripProposalRepository proposalRepository;
    @Mock TripPermissionService  permissionService;

    @InjectMocks TripProposalService service;

    private AppUser owner;
    private AppUser contributor;
    private Trip    trip;

    @BeforeEach
    void setUp() throws Exception {
        owner       = createUser(1L, "owner@citypulse.vn", "Owner");
        contributor = createUser(2L, "contrib@citypulse.vn", "Contributor");
        trip        = createTrip(10L, "Chuyến đi Đà Nẵng", owner);
    }

    @Test
    @DisplayName("UC-05: Contributor tạo đề xuất địa điểm thành công")
    void createProposal_success() {
        CreateProposalRequest request = new CreateProposalRequest(42L, "Bán đảo Sơn Trà", "Ngắm hoàng hôn");
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(2L)).willReturn(Optional.of(contributor));
        willDoNothing().given(permissionService).requireOwnerOrContributor(trip, 2L);

        TripProposal saved = new TripProposal(trip, contributor, 42L, "Bán đảo Sơn Trà", "Ngắm hoàng hôn");
        try { setId(saved, 100L); } catch (Exception ignored) {}
        given(proposalRepository.save(any(TripProposal.class))).willReturn(saved);

        ProposalResponse response = service.createProposal(10L, 2L, request);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.placeName()).isEqualTo("Bán đảo Sơn Trà");
        assertThat(response.status()).isEqualTo("pending");
        assertThat(response.proposedByUserId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("UC-05 Lỗi: Viewer không có quyền đề xuất địa điểm → 403 AccessDeniedException")
    void createProposal_viewerForbidden() {
        CreateProposalRequest request = new CreateProposalRequest(null, "Ngũ Hành Sơn", null);
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(3L)).willReturn(Optional.of(contributor));
        willThrow(new AccessDeniedException("Viewer không có quyền thực hiện thao tác này."))
                .given(permissionService).requireOwnerOrContributor(trip, 3L);

        assertThatThrownBy(() -> service.createProposal(10L, 3L, request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Viewer không có quyền");
    }

    @Test
    @DisplayName("UC-06: Owner duyệt đề xuất địa điểm thành công")
    void reviewProposal_approve_success() {
        TripProposal proposal = new TripProposal(trip, contributor, null, "Cầu Rồng", "Xem phun lửa");
        try { setId(proposal, 50L); } catch (Exception ignored) {}

        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(1L)).willReturn(Optional.of(owner));
        willDoNothing().given(permissionService).requireOwner(trip, 1L);
        given(proposalRepository.findByIdAndTripId(50L, 10L)).willReturn(Optional.of(proposal));
        given(proposalRepository.save(any(TripProposal.class))).willAnswer(inv -> inv.getArgument(0));

        ReviewProposalRequest request = new ReviewProposalRequest("approved", "Rất hợp lý");
        ProposalResponse response = service.reviewProposal(10L, 50L, 1L, request);

        assertThat(response.status()).isEqualTo("approved");
        assertThat(response.reviewedByUserId()).isEqualTo(1L);
        assertThat(response.reviewNote()).isEqualTo("Rất hợp lý");
        assertThat(proposal.getStatus()).isEqualTo(TripProposal.Status.approved);
    }

    @Test
    @DisplayName("UC-06: Owner từ chối đề xuất địa điểm thành công")
    void reviewProposal_reject_success() {
        TripProposal proposal = new TripProposal(trip, contributor, null, "Cầu Rồng", "Xem phun lửa");
        try { setId(proposal, 50L); } catch (Exception ignored) {}

        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(1L)).willReturn(Optional.of(owner));
        willDoNothing().given(permissionService).requireOwner(trip, 1L);
        given(proposalRepository.findByIdAndTripId(50L, 10L)).willReturn(Optional.of(proposal));
        given(proposalRepository.save(any(TripProposal.class))).willAnswer(inv -> inv.getArgument(0));

        ReviewProposalRequest request = new ReviewProposalRequest("rejected", "Lịch trình đã kín");
        ProposalResponse response = service.reviewProposal(10L, 50L, 1L, request);

        assertThat(response.status()).isEqualTo("rejected");
        assertThat(proposal.getStatus()).isEqualTo(TripProposal.Status.rejected);
    }

    @Test
    @DisplayName("UC-06 Lỗi: Contributor cố duyệt đề xuất → 403 AccessDeniedException")
    void reviewProposal_contributorCannotReview() {
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(2L)).willReturn(Optional.of(contributor));
        willThrow(new AccessDeniedException("Chỉ owner của chuyến đi mới có quyền thực hiện thao tác này."))
                .given(permissionService).requireOwner(trip, 2L);

        ReviewProposalRequest request = new ReviewProposalRequest("approved", null);

        assertThatThrownBy(() -> service.reviewProposal(10L, 50L, 2L, request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Chỉ owner");
    }

    @Test
    @DisplayName("UC-06 Lỗi: Đề xuất đã duyệt rồi thì không được duyệt lại")
    void reviewProposal_alreadyProcessed() {
        TripProposal proposal = new TripProposal(trip, contributor, null, "Cầu Rồng", "Xem phun lửa");
        proposal.approve(owner, "Đã duyệt");
        try { setId(proposal, 50L); } catch (Exception ignored) {}

        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(1L)).willReturn(Optional.of(owner));
        willDoNothing().given(permissionService).requireOwner(trip, 1L);
        given(proposalRepository.findByIdAndTripId(50L, 10L)).willReturn(Optional.of(proposal));

        ReviewProposalRequest request = new ReviewProposalRequest("rejected", null);

        assertThatThrownBy(() -> service.reviewProposal(10L, 50L, 1L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Đề xuất này đã được xử lý trước đó");
    }

    private AppUser createUser(Long id, String email, String name) throws Exception {
        AppUser u = new AppUser(email, name);
        setId(u, id);
        return u;
    }

    private Trip createTrip(Long id, String title, AppUser owner) throws Exception {
        Trip t = new Trip(title, owner);
        setId(t, id);
        return t;
    }

    private void setId(Object entity, Long id) throws Exception {
        var field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}

