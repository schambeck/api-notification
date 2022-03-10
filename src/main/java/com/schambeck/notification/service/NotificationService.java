package com.schambeck.notification.service;

import com.schambeck.notification.domain.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    Notification create(Notification invoice, String userId);

    long markAsReadAndCount(UUID id, String userId);

    void updateRead(Notification notification, Boolean read);

    long countUnread(String userId);

    List<Notification> findByUserId(String userId);

    Notification findById(UUID id);

}
