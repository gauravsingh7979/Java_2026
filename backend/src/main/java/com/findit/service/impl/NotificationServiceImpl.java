package com.findit.service.impl;

import com.findit.dto.common.PageResponse;
import com.findit.dto.notification.NotificationResponse;
import com.findit.entity.Notification;
import com.findit.entity.User;
import com.findit.exception.ResourceNotFoundException;
import com.findit.exception.UnauthorizedException;
import com.findit.repository.NotificationRepository;
import com.findit.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public NotificationServiceImpl(NotificationRepository notificationRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public Notification createNotification(User user, String title, String message, String type, String targetUrl) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .targetUrl(targetUrl)
                .read(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        sendEmailNotification(user.getEmail(), title, message);

        return saved;
    }

    @Async
    public void sendEmailNotification(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(toEmail);
            mail.setSubject("[FindIt] " + subject);
            mail.setText("Hello,\n\n" + body + "\n\nBest regards,\nThe FindIt Platform Team\nhttps://findit.local");
            mailSender.send(mail);
            log.info("Email notification sent successfully to {}", toEmail);
        } catch (Exception ex) {
            log.debug("Notice: Email notification could not be sent (SMTP not configured or offline): {}", ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page.map(this::mapToResponse));
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to modify this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .targetUrl(n.getTargetUrl())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
