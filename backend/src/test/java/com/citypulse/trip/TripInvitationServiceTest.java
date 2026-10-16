package com.citypulse.trip;

import com.citypulse.common.exception.AccessDeniedException;
import com.citypulse.common.exception.BusinessRuleException;
import com.citypulse.common.exception.ResourceNotFoundException;
import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripInvitation;
import com.citypulse.trip.entity.TripInvitation.Status;
import com.citypulse.trip.entity.TripMembership;
import com.citypulse.trip.entity.TripRole;
import com.citypulse.trip.repository.TripInvitationRepository;
import com.citypulse.trip.repository.TripMembershipRepository;
import com.citypulse.trip.repository.TripRepository;
import com.citypulse.trip.service.InvitationResponse;
import com.citypulse.trip.service.TripEmailException;
import com.citypulse.trip.service.TripEmailService;
import com.citypulse.trip.service.TripInvitationService;
import com.citypulse.user.entity.AppUser;
import com.citypulse.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests cho TripInvitationService (UC-01 -> UC-04).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TripInvitationService Tests (UC-01 -> UC-04)")
class TripInvitationServiceTest {

    @Mock TripRepository          tripRepository;
    @Mock AppUserRepository       userRepository;
    @Mock TripInvitationRepository invitationRepository;
    @Mock TripMembershipRepository membershipRepository;
    @Mock TripEmailService        emailService;

    @InjectMocks TripInvitationService service;

    private AppUser owner;
    private AppUser otherUser;
    private Trip    trip;

    @BeforeEach
    void setUp() throws Exception {
        owner     = createUser(1L, "owner@citypulse.vn", "Owner");
        otherUser = createUser(2L, "other@citypulse.vn", "Other");
        trip      = createTrip(10L, "Hành trình Đà Nẵng", owner);
    }

    // ── UC-01: Gửi lời mời ───────────────────────────────────────────────────

    @Test
    @DisplayName("UC-01 Thành công: tạo pending invitation và gửi email")
    void sendInvitation_success() {
        String inviteeEmail = "member@example.com";
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(1L)).willReturn(Optional.of(owner));
        given(invitationRepository.existsByTripIdAndInviteeEmailIgnoreCaseAndStatus(
                10L, inviteeEmail, Status.pending)).willReturn(false);

        TripInvitation savedInv = createInvitation(100L, trip, owner, inviteeEmail, Instant.now().plus(7, ChronoUnit.DAYS));
        given(invitationRepository.save(any(TripInvitation.class))).willReturn(savedInv);
        willDoNothing().given(emailService).sendTripInvitation(any(), any(), any());

        InvitationResponse result = service.sendInvitation(10L, 1L, inviteeEmail);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.tripId()).isEqualTo(10L);
        assertThat(result.inviteeEmail()).isEqualTo(inviteeEmail);
        assertThat(result.status()).isEqualTo("pending");
        assertThat(result.expiresAt()).isAfter(Instant.now().plus(6, ChronoUnit.DAYS));

        then(emailService).should().sendTripInvitation(
                eq(inviteeEmail), eq("Hành trình Đà Nẵng"), eq("Owner"));
    }

    @Test
    @DisplayName("UC-01 Lỗi: trip không tồn tại → ResourceNotFoundException")
    void sendInvitation_tripNotFound() {
        given(tripRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.sendInvitation(99L, 1L, "x@x.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        then(invitationRepository).shouldHaveNoInteractions();
        then(emailService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("UC-01 Lỗi: không phải owner → AccessDeniedException")
    void sendInvitation_notOwner() {
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(2L)).willReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> service.sendInvitation(10L, 2L, "x@x.com"))
                .isInstanceOf(AccessDeniedException.class);

        then(invitationRepository).shouldHaveNoInteractions();
        then(emailService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("UC-01 Lỗi: đã có pending invitation → BusinessRuleException")
    void sendInvitation_duplicatePending() {
        String inviteeEmail = "member@example.com";
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(1L)).willReturn(Optional.of(owner));
        given(invitationRepository.existsByTripIdAndInviteeEmailIgnoreCaseAndStatus(
                10L, inviteeEmail, Status.pending)).willReturn(true);

        assertThatThrownBy(() -> service.sendInvitation(10L, 1L, inviteeEmail))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Lời mời đã được gửi trước đó");

        then(invitationRepository).should(never()).save(any());
        then(emailService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("UC-01 Lỗi: người được mời đã là member của trip → BusinessRuleException")
    void sendInvitation_alreadyMember() {
        String inviteeEmail = "other@citypulse.vn";
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(1L)).willReturn(Optional.of(owner));
        given(invitationRepository.existsByTripIdAndInviteeEmailIgnoreCaseAndStatus(
                10L, inviteeEmail, Status.pending)).willReturn(false);
        given(userRepository.findByEmailIgnoreCase(inviteeEmail)).willReturn(Optional.of(otherUser));
        given(membershipRepository.existsByTripIdAndUserId(10L, 2L)).willReturn(true);

        assertThatThrownBy(() -> service.sendInvitation(10L, 1L, inviteeEmail))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Người dùng đã là thành viên");

        then(invitationRepository).should(never()).save(any());
        then(emailService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("UC-01 Lỗi: email thất bại → TripEmailException (không lưu invitation)")
    void sendInvitation_emailFails() {
        String inviteeEmail = "member@example.com";
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(userRepository.findById(1L)).willReturn(Optional.of(owner));
        given(invitationRepository.existsByTripIdAndInviteeEmailIgnoreCaseAndStatus(
                10L, inviteeEmail, Status.pending)).willReturn(false);

        TripInvitation savedInv = createInvitation(100L, trip, owner, inviteeEmail, Instant.now().plus(7, ChronoUnit.DAYS));
        given(invitationRepository.save(any())).willReturn(savedInv);

        willThrow(new TripEmailException("SMTP lỗi", new RuntimeException()))
                .given(emailService).sendTripInvitation(any(), any(), any());

        assertThatThrownBy(() -> service.sendInvitation(10L, 1L, inviteeEmail))
                .isInstanceOf(TripEmailException.class);
    }

    // ── UC-02: Chấp nhận lời mời ─────────────────────────────────────────────

    @Test
    @DisplayName("UC-02 Thành công: chấp nhận lời mời tạo membership Viewer")
    void acceptInvitation_success() {
        TripInvitation invitation = createInvitation(100L, trip, owner, "other@citypulse.vn", Instant.now().plus(7, ChronoUnit.DAYS));
        given(invitationRepository.findByToken("valid-token")).willReturn(Optional.of(invitation));
        given(userRepository.findById(2L)).willReturn(Optional.of(otherUser));
        given(membershipRepository.existsByTripIdAndUserId(10L, 2L)).willReturn(false);

        InvitationResponse response = service.acceptInvitationByToken("valid-token", 2L);

        assertThat(response.status()).isEqualTo("accepted");
        assertThat(invitation.getStatus()).isEqualTo(Status.accepted);
        then(membershipRepository).should().save(argThat(m ->
                m.getTrip().getId().equals(10L) &&
                m.getUser().getId().equals(2L) &&
                m.getRole() == TripRole.Viewer));
    }

    @Test
    @DisplayName("UC-02 Lỗi: lời mời đã hết hạn → cập nhật expired và báo lỗi")
    void acceptInvitation_expired() {
        TripInvitation expiredInv = createInvitation(100L, trip, owner, "other@citypulse.vn", Instant.now().minus(1, ChronoUnit.DAYS));
        given(invitationRepository.findByToken("expired-token")).willReturn(Optional.of(expiredInv));
        given(userRepository.findById(2L)).willReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> service.acceptInvitationByToken("expired-token", 2L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Lời mời đã hết hạn");

        assertThat(expiredInv.getStatus()).isEqualTo(Status.expired);
        then(invitationRepository).should().save(expiredInv);
        then(membershipRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("UC-02 Lỗi: đã là thành viên → báo lỗi")
    void acceptInvitation_alreadyMember() {
        TripInvitation invitation = createInvitation(100L, trip, owner, "other@citypulse.vn", Instant.now().plus(7, ChronoUnit.DAYS));
        given(invitationRepository.findByToken("token")).willReturn(Optional.of(invitation));
        given(userRepository.findById(2L)).willReturn(Optional.of(otherUser));
        given(membershipRepository.existsByTripIdAndUserId(10L, 2L)).willReturn(true);

        assertThatThrownBy(() -> service.acceptInvitationByToken("token", 2L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Bạn đã tham gia rồi");

        assertThat(invitation.getStatus()).isEqualTo(Status.pending);
    }

    // ── UC-03: Từ chối lời mời ───────────────────────────────────────────────

    @Test
    @DisplayName("UC-03 Thành công: từ chối lời mời không tạo membership")
    void declineInvitation_success() {
        TripInvitation invitation = createInvitation(100L, trip, owner, "other@citypulse.vn", Instant.now().plus(7, ChronoUnit.DAYS));
        given(invitationRepository.findByToken("token")).willReturn(Optional.of(invitation));

        InvitationResponse response = service.declineInvitationByToken("token", 2L);

        assertThat(response.status()).isEqualTo("declined");
        assertThat(invitation.getStatus()).isEqualTo(Status.declined);
        then(membershipRepository).shouldHaveNoInteractions();
    }

    // ── UC-04: Hủy lời mời ───────────────────────────────────────────────────

    @Test
    @DisplayName("UC-04 Thành công: Owner hủy lời mời pending")
    void cancelInvitation_success() {
        TripInvitation invitation = createInvitation(100L, trip, owner, "other@citypulse.vn", Instant.now().plus(7, ChronoUnit.DAYS));
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));
        given(invitationRepository.findByIdAndTripId(100L, 10L)).willReturn(Optional.of(invitation));

        InvitationResponse response = service.cancelInvitation(10L, 100L, 1L);

        assertThat(response.status()).isEqualTo("cancelled");
        assertThat(invitation.getStatus()).isEqualTo(Status.cancelled);
    }

    @Test
    @DisplayName("UC-04 Lỗi: Không phải Owner không được hủy lời mời")
    void cancelInvitation_notOwner() {
        given(tripRepository.findById(10L)).willReturn(Optional.of(trip));

        assertThatThrownBy(() -> service.cancelInvitation(10L, 100L, 2L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Chỉ owner");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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

    private TripInvitation createInvitation(Long id, Trip trip, AppUser invitedBy,
                                             String inviteeEmail, Instant expiresAt) {
        TripInvitation inv = new TripInvitation(trip, invitedBy, inviteeEmail, expiresAt, "test-token");
        try { setId(inv, id); } catch (Exception ignored) {}
        return inv;
    }

    private void setId(Object entity, Long id) throws Exception {
        var field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
