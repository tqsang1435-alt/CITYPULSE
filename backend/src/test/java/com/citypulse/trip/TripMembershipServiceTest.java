package com.citypulse.trip;

import com.citypulse.common.exception.AccessDeniedException;
import com.citypulse.common.exception.BusinessRuleException;
import com.citypulse.common.exception.ResourceNotFoundException;
import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripMembership;
import com.citypulse.trip.entity.TripRole;
import com.citypulse.trip.repository.TripInvitationRepository;
import com.citypulse.trip.repository.TripMembershipRepository;
import com.citypulse.trip.repository.TripRepository;
import com.citypulse.trip.service.*;
import com.citypulse.user.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripMembershipService Tests (UC-07, UC-08)")
class TripMembershipServiceTest {

    @Mock TripRepository           tripRepository;
    @Mock TripMembershipRepository membershipRepository;
    @Mock TripInvitationRepository invitationRepository;
    @Mock TripPermissionService    permissionService;

    @InjectMocks TripMembershipService service;

    private AppUser owner;
    private AppUser memberUser;
    private Trip    trip;
    private TripMembership membership;

    @BeforeEach
    void setUp() throws Exception {
        owner      = createUser(1L, "owner@citypulse.vn", "Owner");
        memberUser = createUser(2L, "member@citypulse.vn", "Member");
        trip       = createTrip(10L, "Chuyến đi Đà Nẵng", owner);
        membership = new TripMembership(trip, memberUser, TripRole.Viewer);
        setId(membership, 100L);
    }

    @Test
    @DisplayName("UC-07: Xem danh sách thành viên thành công")
    void getMembers_success() {
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        willDoNothing().given(permissionService).requireMember(trip, 1L);
        given(membershipRepository.findByTripIdOrderByJoinedAtAsc(10L)).willReturn(List.of(membership));
        given(invitationRepository.findByTripIdAndStatus(eq(10L), any())).willReturn(List.of());

        TripMembersListResponse response = service.getMembers(10L, 1L);

        assertThat(response.tripId()).isEqualTo(10L);
        assertThat(response.members()).hasSize(2); // Owner tự động thêm + member
        assertThat(response.members().get(0).role()).isEqualTo(TripRole.Owner);
        assertThat(response.members().get(1).userId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("UC-08: Owner đổi vai trò thành viên thành công")
    void updateMemberRole_success() {
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        willDoNothing().given(permissionService).requireOwner(trip, 1L);
        given(membershipRepository.findByTripIdAndUserId(10L, 2L)).willReturn(Optional.of(membership));
        given(membershipRepository.save(any(TripMembership.class))).willReturn(membership);

        MemberResponse response = service.updateMemberRole(10L, 2L, TripRole.Contributor, 1L);

        assertThat(response.role()).isEqualTo(TripRole.Contributor);
        assertThat(membership.getRole()).isEqualTo(TripRole.Contributor);
    }

    @Test
    @DisplayName("UC-08 Lỗi: Không thể đổi role của Owner chuyến đi")
    void updateMemberRole_cannotChangeOwnerRole() {
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        willDoNothing().given(permissionService).requireOwner(trip, 1L);

        assertThatThrownBy(() -> service.updateMemberRole(10L, 1L, TripRole.Contributor, 1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Không thể thay đổi vai trò của Owner");
    }

    @Test
    @DisplayName("UC-08: Owner xóa thành viên thành công")
    void removeMember_success() {
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(membershipRepository.existsByTripIdAndUserId(10L, 2L)).willReturn(true);

        service.removeMember(10L, 2L, 1L);

        then(membershipRepository).should().deleteByTripIdAndUserId(10L, 2L);
    }

    @Test
    @DisplayName("UC-08 Lỗi: Không được xóa Owner của chuyến đi")
    void removeMember_cannotRemoveOwner() {
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));

        assertThatThrownBy(() -> service.removeMember(10L, 1L, 1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Không thể xóa Owner");
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

