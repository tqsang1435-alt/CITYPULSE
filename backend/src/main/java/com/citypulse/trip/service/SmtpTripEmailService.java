package com.citypulse.trip.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Triển khai gửi email thực tế qua Spring Mail (SMTP).
 *
 * <p>Cấu hình qua biến môi trường (xem .env.example):
 * <ul>
 *   <li>MAIL_HOST – SMTP host (vd: smtp.gmail.com)</li>
 *   <li>MAIL_PORT – SMTP port (vd: 587)</li>
 *   <li>MAIL_USERNAME – địa chỉ email gửi</li>
 *   <li>MAIL_PASSWORD – mật khẩu hoặc App Password</li>
 *   <li>MAIL_FROM – địa chỉ "From" hiển thị</li>
 * </ul>
 */
@Service
public class SmtpTripEmailService implements TripEmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpTripEmailService.class);

    private final JavaMailSender mailSender;

    @Value("${citypulse.mail.from}")
    private String fromAddress;

    @Value("${citypulse.app.base-url:http://localhost:5173}")
    private String appBaseUrl;

    public SmtpTripEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendTripInvitation(String inviteeEmail, String tripTitle, String inviterName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(inviteeEmail);
        msg.setSubject("[CITYPULSE] " + inviterName + " mời bạn tham gia chuyến đi: " + tripTitle);
        msg.setText(buildEmailBody(inviteeEmail, tripTitle, inviterName));

        try {
            mailSender.send(msg);
            log.info("Đã gửi email mời tham gia trip '{}' đến {}", tripTitle, inviteeEmail);
        } catch (MailException ex) {
            log.error("Không thể gửi email mời trip '{}' đến {}: {}", tripTitle, inviteeEmail, ex.getMessage());
            throw new TripEmailException(
                    "Không thể gửi email đến " + inviteeEmail + ". Vui lòng thử lại sau.", ex);
        }
    }

    private String buildEmailBody(String inviteeEmail, String tripTitle, String inviterName) {
        return String.format("""
                Xin chào,

                %s đã mời bạn tham gia chuyến đi "%s" trên CITYPULSE.

                Truy cập %s/trips để xem và chấp nhận lời mời.

                Lời mời có hiệu lực trong 7 ngày.

                ——
                CITYPULSE · Khám phá Đà Nẵng theo cách của bạn
                """, inviterName, tripTitle, appBaseUrl);
    }
}
