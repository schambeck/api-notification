package com.schambeck.notification.service;

import com.schambeck.notification.domain.Notification;
import com.schambeck.notification.exception.NotFoundException;
import com.schambeck.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    @Override
    @Transactional
    public Notification create(Notification notification) {
        notification.setRead(false);
        return repository.save(notification);
    }

    @Override
    public long markAsReadAndCount(UUID id, String userId) {
        Notification notification = findById(id);
        updateRead(notification, true);
        return repository.countByUserIdAndReadIsFalse(userId);
    }

    @Override
    @Transactional
    public void updateRead(Notification notification, Boolean read) {
        repository.save(notification.toBuilder()
                .read(read)
                .build());
    }

    @Override
    public long countUnread(String userId) {
        return repository.countByUserIdAndReadIsFalse(userId);
    }

    @Override
    public List<Notification> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public Notification findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(format("Entity %s not found", id)));
    }

}
