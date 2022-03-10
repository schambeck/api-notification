package com.schambeck.notification.repository;

import com.schambeck.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    long countByUserIdAndReadIsFalse(String userId);

    List<Notification> findByUserId(String userId);

}
