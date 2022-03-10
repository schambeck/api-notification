package com.schambeck.notification.representation;

import com.schambeck.notification.domain.TypeNotification;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class NotificationDto implements Serializable {

    private final UUID id;
    private final TypeNotification type;
    private final String userId;
    private final String title;
    private final String message;
    private final Boolean read;
    private String link;

}
