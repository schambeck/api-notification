package com.schambeck.notification.service;

import com.schambeck.notification.domain.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    Notification create(Notification invoice);

    long markAsReadAndCount(UUID id);

    void updateRead(Notification notification, Boolean read);

    long countByReadIsFalse();

    List<Notification> findAll();

    Notification findById(UUID id);

}
